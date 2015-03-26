package com.thefuzzybrain.ishan0445.ribbit;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class RecipientsActivity extends ActionBarActivity {
    protected Toolbar toolbar;
    protected ListView lv;
    protected TextView tv;
    protected CircularProgressBar cpb;
    protected Uri mMediaUri;
    protected String mFileType;


    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected MenuItem mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        toolbar = (Toolbar) findViewById(R.id.appBarRecipients);
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.lvRecipients);


        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        tv = (TextView) findViewById(R.id.tvNoFriends);
        tv.setVisibility(View.VISIBLE);

        cpb = (CircularProgressBar) findViewById(R.id.cpbRecipients);
        cpb.setVisibility(View.VISIBLE);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FLE_TYPE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(lv.getCheckedItemCount() > 0){
                    mSend.setVisible(true);
                }else {
                    mSend.setVisible(false);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_PARSE_RELATION);

        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if(e == null){
                    mFriends = friends;

                    mFriends.add(ParseUser.getCurrentUser());

                    String[] users = new String[mFriends.size()];
                    int i=0;
                    for(ParseUser u : mFriends){
                        users[i] = u.getUsername();
                        i++;
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getApplicationContext(),android.R.layout.simple_list_item_checked,users);

                    tv.setVisibility(View.INVISIBLE);
                    cpb.setVisibility(View.INVISIBLE);
                    lv.setAdapter(adapter);

                }else {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    tv.setVisibility(View.INVISIBLE);
                    cpb.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipiants, menu);
        mSend = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sendNow) {
            ParseObject message = createMessages();
            if(message == null){
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("We're Sorry!")
                        .setContentText("Their was an error with the file selected. Please select another file")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }else {
                sendMessage(message);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(getApplicationContext(),"Message Sent!",Toast.LENGTH_LONG).show();
                }else{
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("We're Sorry!")
                            .setContentText("Their was an error sending the message!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            }

        } );
    }

    private ParseObject createMessages() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS,getRecipientIDs());
        message.put(ParseConstants.KEY_FLE_TYPE,mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);

        if(fileBytes == null){
            return null;
        }else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file = new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE,file);
            return message;
        }

    }

    private ArrayList<String> getRecipientIDs() {
        ArrayList<String> recipientIDs = new ArrayList<>();
        for(int i = 0 ; i < lv.getCount();i++){
            if(lv.isItemChecked(i)){
                recipientIDs.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIDs;
    }
}
