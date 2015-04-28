package com.jiangziandroid.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import butterknife.ButterKnife;
import butterknife.InjectView;


public class RecipientsActivity extends ListActivity {

    //@InjectView(R.id.MessageSendButton) Button mMessageSendButton;
    @InjectView(R.id.action_send) MenuItem mSendMenu;
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        ButterKnife.inject(this);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser friend : mFriends) {
                        usernames[i] = friend.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                } else {
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(l.getCheckedItemCount() == 0){
            //mMessageSendButton.setVisibility(View.INVISIBLE);
            mSendMenu.setVisible(false);

        }else {
            //mMessageSendButton.setVisibility(View.VISIBLE);
            mSendMenu.setVisible(true);
        }
        mSendMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ParseObject message = createMessage();
                if(message == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle("We're Sorry!")
                            .setMessage("There was an error with the file selected, please try again!")
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    send(message);
                    finish();
                }
                return true;
            }
        });
        /**
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject message = createMessage();
                if(message == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setTitle("We're Sorry!")
                           .setMessage("There was an error with the file selected, please try again!")
                           .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    send(message);
                    finish();
                }
            }
        });
        **/
    }


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
            if(getListView().isItemChecked(i)){
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
