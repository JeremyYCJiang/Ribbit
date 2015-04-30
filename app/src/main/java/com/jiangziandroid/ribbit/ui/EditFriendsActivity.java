package com.jiangziandroid.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiangziandroid.ribbit.R;
import com.jiangziandroid.ribbit.adapters.UserAdapter;
import com.jiangziandroid.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends Activity {

    protected List<ParseUser> mParseUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected GridView mGridView;
    protected TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        mGridView = (GridView) findViewById(R.id.usersGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        //Set Item Click Listener
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);
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
        query.setLimit(50);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    //Success
                    mParseUsers = parseUsers;
                    //String[] usernames = new String[mParseUsers.size()];
                    //int i = 0;
                    //for(ParseUser parseUser : parseUsers){
                    //    usernames[i] = parseUser.getUsername();
                    //    i++;
                    //}
                    if(mGridView.getAdapter() == null){
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mParseUsers);
                        mGridView.setAdapter(adapter);
                    }
                    else{
                        ((UserAdapter)mGridView.getAdapter()).refill(mParseUsers);
                    }
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
                                mGridView.setItemChecked(i,true);
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


    // For GridView
    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkedImageView = (ImageView) view.findViewById(R.id.userPhotoSelectedImageView);
            if (mGridView.isItemChecked(position)) {
                //Add friends if item is checked after item been touched
                //locally
                mFriendsRelation.add(mParseUsers.get(position));
                checkedImageView.setVisibility(View.VISIBLE);
            } else {
                //Remove friends if item is unchecked after item been touched
                //locally
                mFriendsRelation.remove(mParseUsers.get(position));
                checkedImageView.setVisibility(View.INVISIBLE);
            }
            //back-end
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(EditFriendsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };


}
