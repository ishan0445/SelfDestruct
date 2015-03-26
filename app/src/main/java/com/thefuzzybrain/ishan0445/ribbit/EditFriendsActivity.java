package com.thefuzzybrain.ishan0445.ribbit;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class EditFriendsActivity extends ActionBarActivity {
    Toolbar toolbar;
    EditText et;
    ListView lv;
    ImageView iv;

    protected CircularProgressBar cpb;


    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        toolbar = (Toolbar) findViewById(R.id.appBarEditFriends);
        setSupportActionBar(toolbar);

        et = (EditText) findViewById(R.id.etSearch);
        lv = (ListView) findViewById(R.id.lvEditFriends);
        iv = (ImageView) findViewById(R.id.ivSearch);
        cpb = (CircularProgressBar) findViewById(R.id.cpbEditFriends);
        cpb.setVisibility(View.VISIBLE);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(lv.isItemChecked(position)){
                    mFriendsRelation.add(mUsers.get(position));

                }else{
                    mFriendsRelation.remove(mUsers.get(position));
                }
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null)
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.orderByAscending(ParseConstants.KEY_USERNAME);
                query.whereContains(ParseConstants.KEY_USERNAME,et.getText().toString().trim());
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> parseUsers, ParseException e) {
                        if(e == null){
                            mUsers = parseUsers;
                            mUsers = removeCurrentUser(mUsers);
                            String[] users = new String[mUsers.size()];
                            int i=0;
                            for(ParseUser u : mUsers){
                                users[i] = u.getUsername();
                                i++;
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        getApplicationContext(),android.R.layout.simple_list_item_checked,users);
                                lv.setAdapter(adapter);
                                addFriendCheckMark();
                                cpb.setVisibility(View.INVISIBLE);
                            }
                        }else {
                            cpb.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private List<ParseUser> removeCurrentUser(List<ParseUser> mUsers) {
        ParseUser remUser = null;
        for(ParseUser x : mUsers){
            if(x.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                remUser = x;
            }
        }

        if(remUser != null){
            mUsers.remove(remUser);
        }
        return mUsers;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_PARSE_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null){
                    mUsers = users;
                    mUsers = removeCurrentUser(mUsers);
                    String[] usernames = new String[mUsers.size()];
                    int i=0;
                    for(ParseUser user : mUsers) {
                        usernames[i] = user.getUsername();
                        i++;

                    }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getApplicationContext(),android.R.layout.simple_list_item_checked,usernames);
                        cpb.setVisibility(View.INVISIBLE);
                        lv.setAdapter(adapter);
                        addFriendCheckMark();

                }else {
                    cpb.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addFriendCheckMark() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if(e==null){
                    for(int i=0;i< mUsers.size();i++){
                        ParseUser user = mUsers.get(i);

                        for(ParseUser friend : friends){
                            if(friend.getObjectId().equals(user.getObjectId()))
                                lv.setItemChecked(i,true);
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
