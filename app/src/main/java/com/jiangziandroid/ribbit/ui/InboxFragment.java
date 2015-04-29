package com.jiangziandroid.ribbit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jiangziandroid.ribbit.R;
import com.jiangziandroid.ribbit.adapters.MessageAdapter;
import com.jiangziandroid.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/4/20.
 */
public class InboxFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    protected RecyclerView mRecyclerView;
    protected List<ParseObject> mReceivedMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        retrieveMessages();
    }

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if(e == null){
                    mReceivedMessages = messages;
                    MessageAdapter messageAdapter = new MessageAdapter(getActivity(), mReceivedMessages);
                    mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.inboxRecyclerView);
                    if(mRecyclerView.getAdapter() == null){
                        //initial the adapter
                        mRecyclerView.setAdapter(messageAdapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setHasFixedSize(true);
                    }
                    else {
                        //refill the adapter
                        ((MessageAdapter)mRecyclerView.getAdapter()).refill(mReceivedMessages);
                    }
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

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "We're refreshing the page, take a break!", Toast.LENGTH_SHORT).show();
        retrieveMessages();
    }
}
