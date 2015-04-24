package com.jiangziandroid.ribbit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/4/20.
 */
public class InboxFragment extends Fragment{

    protected RecyclerView mRecyclerView;
    protected List<ParseObject> mReceivedMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e == null){
                    mReceivedMessages = messages;
                    MessageAdapter messageAdapter = new MessageAdapter(getActivity().getApplicationContext(), mReceivedMessages);
                    mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.inboxRecyclerView);
                    mRecyclerView.setAdapter(messageAdapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setHasFixedSize(true);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                    builder.setTitle(getString(R.string.error_title))
                            .setMessage(e.getMessage())
                            .setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });



    }



}
