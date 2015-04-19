package com.jiangziandroid.ribbit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JeremyYCJiang on 2015/4/20.
 */
public class FriendsFragment extends android.support.v4.app.ListFragment{
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

}
