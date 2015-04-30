package com.jiangziandroid.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
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
import com.jiangziandroid.ribbit.utils.FileHelper;
import com.jiangziandroid.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends Activity {

    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;
    protected Uri mMediaUri;
    protected String mFileType;
    protected GridView mGridView;
    protected TextView mEmptyTextView;
    protected MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        mGridView = (GridView) findViewById(R.id.usersGrid);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mEmptyTextView = (TextView) findViewById(android.R.id.empty);
        mGridView.setEmptyView(mEmptyTextView);
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getStringExtra(ParseConstants.KEY_FILE_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(50);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    if(mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    }
                    else {
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle(getString(R.string.error_title))
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        menuItem = menu.findItem(R.id.action_send);
        return super.onCreateOptionsMenu(menu);
    }


    public AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mGridView.getCheckedItemCount() == 0){
                //No Item checked
                menuItem.setVisible(false);
            }
            else {
                //One or more item checked
                menuItem.setVisible(true);
            }
            ImageView checkedImageView = (ImageView) view.findViewById(R.id.userPhotoSelectedImageView);
            if(mGridView.isItemChecked(position)){
                checkedImageView.setVisibility(View.VISIBLE);
            }
            else {
                checkedImageView.setVisibility(View.INVISIBLE);
            }
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ParseObject message = createMessage();
                    if (message == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                        builder.setTitle("We're Sorry!")
                                .setMessage("There was an error with the file selected, please try again!")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        send(message);
                        finish();
                    }
                return true;
                }
            });
        }
    };



    protected ParseObject createMessage() {
        ParseObject messages = new ParseObject(ParseConstants.CLASS_MESSAGES);
        messages.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
        messages.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser.getUsername());
        messages.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        messages.put(ParseConstants.KEY_FILE_TYPE, mFileType);
        // Getting started with ParseFile is easy.
        // First, you'll need to have the data in byte[] form and then create a ParseFile with it.
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if(fileBytes == null){
            return null;
        }
        else {
            //if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
            //    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            //}
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile parseFile = new ParseFile(fileName, fileBytes);
            messages.put(ParseConstants.KEY_FILE, parseFile);
            return messages;
        }
    }

    protected ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<>();
        for(int i =0; i<mFriends.size(); i++){
            if(mGridView.isItemChecked(i)){
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }


    protected void send(ParseObject message) {
        message.getParseFile(ParseConstants.KEY_FILE).saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Success
                    Toast.makeText(RecipientsActivity.this, "Successfully send file to Parse.com ^.^",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle("We're Sorry!")
                            .setMessage("There was an error with the file uploaded, please try again!")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer percentDone) {
                System.out.println("uploading: " + percentDone + "%");
            }
        });
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Success
                    Toast.makeText(RecipientsActivity.this, "Successfully send messages to Parse.com ^.^",
                            Toast.LENGTH_LONG).show();
                } else {
                    //Failed
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle("We're Sorry!")
                            .setMessage("There was an error with the messages saved , please try again!")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


}
