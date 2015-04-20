package com.jiangziandroid.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    protected List<ParseUser> mParseUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        //ParseQuery query = ParseQuery.getQuery("User");
        //return a list of ParseUser Objects
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        //sort the data
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        //limit our results to 10 users
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    //Success
                    mParseUsers = parseUsers;
                    String[] usernames = new String[mParseUsers.size()];
                    int i = 0;
                    for(ParseUser parseUser : parseUsers){
                        usernames[i] = parseUser.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                    addFriendCheckmarks();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setTitle(getString(R.string.error_title))
                           .setMessage(e.getMessage())
                           .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        super.onResume();
    }

    private void addFriendCheckmarks() {
        // By default, the list of objects in this relation are not downloaded.
        // You can get the list of Friends by calling findInBackground on the ParseQuery returned by getQuery.
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e != null) {
                    // There was an error
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
                    builder.setTitle(getString(R.string.error_title))
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // friends have all the Users the current user added.
                    for(int i=0; i<mParseUsers.size(); i++){
                        ParseUser user = mParseUsers.get(i);
                        for(ParseUser friend: friends){
                            if(friend.getObjectId().equals(user.getObjectId())){
                                getListView().setItemChecked(i, true);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(getListView().isItemChecked(position)){
            //Add friends if item is checked after item been touched
            //locally
            mFriendsRelation.add(mParseUsers.get(position));
            //back-end
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Toast.makeText(EditFriendsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            //Remove friends if item is unchecked after item been touched
            mFriendsRelation.remove(mParseUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Toast.makeText(EditFriendsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }
}
