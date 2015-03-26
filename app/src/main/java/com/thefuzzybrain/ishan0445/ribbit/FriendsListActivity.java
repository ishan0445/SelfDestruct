package com.thefuzzybrain.ishan0445.ribbit;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class FriendsListActivity extends ActionBarActivity {


    protected ListView lv;
    protected Toolbar toolbar;

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    protected CircularProgressBar cpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        toolbar = (Toolbar) findViewById(R.id.appBarFriendsList);
        lv = (ListView) findViewById(R.id.lvFriendsList);
        cpb = (CircularProgressBar) findViewById(R.id.cpbFriendsList);

        cpb.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
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
                    mFriends = removeCurrentUser(mFriends);
                    String[] users = new String[mFriends.size()];
                    int i=0;
                    for(ParseUser u : mFriends){
                        users[i] = u.getUsername();
                        i++;
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getApplicationContext(),android.R.layout.simple_list_item_1,users);
                        cpb.setVisibility(View.INVISIBLE);
                        lv.setAdapter(adapter);
                    }
                }else {
                    cpb.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private List<ParseUser> removeCurrentUser(List<ParseUser> mFriends) {
        ParseUser remUser = null;
        for(ParseUser x : mFriends){
            if(x.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                remUser = x;
            }
        }

        if(remUser != null){
            mFriends.remove(remUser);
        }
        return mFriends;
    }

}
