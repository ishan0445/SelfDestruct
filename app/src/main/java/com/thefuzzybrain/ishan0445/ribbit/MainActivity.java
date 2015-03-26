package com.thefuzzybrain.ishan0445.ribbit;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class MainActivity extends ActionBarActivity {

    protected Toolbar toolbar;
    protected FloatingActionButton fab;
    protected List<ParseObject> mMessages;

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;


    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; //10MB

    protected Uri mMediaUri;

    protected CircularProgressBar cpb;
    protected ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        ParseUser pu = ParseUser.getCurrentUser();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        lv = (ListView) findViewById(R.id.lvMainActivity);
        cpb = (CircularProgressBar) findViewById(R.id.cpbMainActivity);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject message = mMessages.get(position);
                String url = message.getParseFile(ParseConstants.KEY_FILE).getUrl();



                if(message.getString(ParseConstants.KEY_FLE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
                    Intent in = new Intent(MainActivity.this,ViewImageActivity.class);
                    in.putExtra("URL",url);
                    startActivity(in);
                }else{
                    Intent in = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    in.setDataAndType(Uri.parse(url),"video/*");
                    startActivity(in);
                }

                List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

                if(ids.size() == 1){
                   message.deleteInBackground();
                }else{
                    message.remove(ParseUser.getCurrentUser().getObjectId());

                    ArrayList<String> idsToRemove = new ArrayList<String>();
                    idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

                    message.removeAll(ParseConstants.KEY_RECIPIENT_IDS,idsToRemove);
                    message.saveInBackground();
                }
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title("Choose One")
                        .items(R.array.items)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        mMediaUri = getOutoutMediaFileUri(MEDIA_TYPE_IMAGE);
                                        if (mMediaUri == null) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Their was a problem accessing your device's external storage",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST);
                                            }
                                        }

                                        break;
                                    case 1:
                                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                        mMediaUri = getOutoutMediaFileUri(MEDIA_TYPE_VIDEO);
                                        if (mMediaUri == null) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Their was a problem accessing your device's external storage",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                            startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                                        }
                                        break;
                                    case 2:
                                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        choosePhotoIntent.setType("image/*");
                                        startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                                        break;
                                    case 3:
                                        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        chooseVideoIntent.setType("video/*");
                                        Toast.makeText(getApplicationContext(),
                                                "Selected video must be less then 10 MB",
                                                Toast.LENGTH_LONG).show();
                                        startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                                        break;
                                }
                            }
                        }).show();
            }
        });

        if (pu == null) {
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
        }else{
            showCase();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
                if(data == null){
                    Toast.makeText(getApplicationContext(),
                            "Sorry their was an error",
                            Toast.LENGTH_LONG).show();
                }else{
                    mMediaUri = data.getData();
                }

                if(requestCode == PICK_VIDEO_REQUEST){
                    int fileSize = 0;
                    InputStream inputStream = null ;
                    try{
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),
                                "Their was a problem with the selected file",
                                Toast.LENGTH_LONG).show();
                        return;
                    }finally {
                        try {
                            inputStream.close();
                        } catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    if(fileSize >= FILE_SIZE_LIMIT){
                        Toast.makeText(getApplicationContext(),
                                "Sorry! File size too large. Select a new file",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }else{
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(MainActivity.this,RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if(requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST){
                fileType = ParseConstants.TYPE_IMAGE;
            }else{
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FLE_TYPE,fileType);
            startActivity(recipientsIntent);

        }else if(resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),
                    "Sorry their was an error",
                    Toast.LENGTH_LONG).show();
        }
    }

    private Uri getOutoutMediaFileUri(int mediaType) {
        if(isExternalStorageAvailable()){
            String appName = MainActivity.this.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    appName);
            if(! mediaStorageDir.exists()){
                if(mediaStorageDir.mkdirs()){
                    Log.e("Main Activity : ","Failed to create directory");
                }
            }

            File mediaFile;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(now);
            String path = mediaStorageDir.getPath() + File.separator;

            if(mediaType == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_"+ timestamp+".jpg");
            }else if(mediaType == MEDIA_TYPE_VIDEO){
                mediaFile = new File(path + "VID_"+ timestamp+".mp4");

            }else{
                return null;
            }

            return Uri.fromFile(mediaFile);
        }else {
            return null;
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        return state.equals(Environment.MEDIA_MOUNTED);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            ParseUser.getCurrentUser().logOut();
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(in);
            finish();
            return true;
        } else if (id == R.id.editFriends) {
            Intent in = new Intent(MainActivity.this, EditFriendsActivity.class);
            startActivity(in);
        } else if (id == R.id.friends) {
            Intent in = new Intent(MainActivity.this, FriendsListActivity.class);
            startActivity(in);
        }else if(id == R.id.refresh){
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    protected void refresh() {

        cpb.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e == null){
                    mMessages = messages;
                    MessageAdapter adapter = new MessageAdapter(getApplicationContext(),mMessages);
                    cpb.setVisibility(View.INVISIBLE);
                    lv.setAdapter(adapter);

                }else {
                    cpb.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Sorry their was an Error!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void showCase(){
        ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setContentTitle("Welcome to\nSelf Destruct\nTap to continue")
                .setContentText("")
                //.singleShot(0)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        showCaseOne();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .hideOnTouchOutside()

                .build();

        sv.hideButton();
        sv.setStyle(R.style.CustomShowcaseTheme);
    }

    protected void showCaseOne(){

        ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setContentTitle("Tap here to take a picture or a video")
                .setContentText("")
                //.singleShot(1)
                .setTarget(new ViewTarget(R.id.fab, MainActivity.this))
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        showCaseTwo();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .hideOnTouchOutside()

                .build();

        sv.hideButton();
        sv.setStyle(R.style.CustomShowcaseTheme);


    }

    protected void showCaseTwo(){
        ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setContentTitle("Tap here to see your friend list")
                .setContentText("")
                //.singleShot(2)
                .setTarget(new ViewTarget(R.id.friends, MainActivity.this))
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        showCaseThree();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .hideOnTouchOutside()
                .build();

        sv.hideButton();
        sv.setStyle(R.style.CustomShowcaseTheme);
    }

    protected void showCaseThree(){
        ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setContentTitle("Tap here to refresh your inbox")
                .setContentText("")
                //.singleShot(3)
                .setTarget(new ViewTarget(R.id.refresh, MainActivity.this))
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                        showCaseFour();
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .hideOnTouchOutside()
                .build();

        sv.hideButton();
        sv.setStyle(R.style.CustomShowcaseTheme);

    }

    protected void showCaseFour(){
        ShowcaseView sv = new ShowcaseView.Builder(MainActivity.this)
                .setContentTitle("More options can\nbe found in the menu,\nSuch as :\nEdit friends or logout")
                .setContentText("")
                //.singleShot(4)
                .setShowcaseEventListener(new OnShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewShow(final ShowcaseView scv) {
                    }

                    @Override
                    public void onShowcaseViewHide(final ShowcaseView scv) {
                        scv.setVisibility(View.GONE);
                    }

                    @Override
                    public void onShowcaseViewDidHide(final ShowcaseView scv) {
                    }

                })
                .hideOnTouchOutside()
                .build();
        sv.hideButton();
        sv.setStyle(R.style.CustomShowcaseTheme);

    }


}
