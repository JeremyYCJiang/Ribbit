package com.jiangziandroid.ribbit.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jiangziandroid.ribbit.utils.ParseConstants;
import com.jiangziandroid.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/4/20.
 */
public class FriendsFragment extends android.support.v4.app.ListFragment{

    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected List<ParseUser> mFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
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
                               getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                       setListAdapter(adapter);
                   } else {
                       AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
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
