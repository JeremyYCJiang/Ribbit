package com.jiangziandroid.ribbit.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangziandroid.ribbit.R;
import com.jiangziandroid.ribbit.utils.MD5Util;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JeremyYCJiang on 2015/4/23.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mParseUsers;

    public UserAdapter(Context context, List<ParseUser> receivedUsers) {
        super(context, R.layout.user_item, receivedUsers);
        mContext = context;
        mParseUsers = receivedUsers;
    }

    //data mapping code
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder is a best practice.
        ViewHolder viewHolder;
        if(convertView == null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mUserPhotoImageView = (ImageView) convertView.findViewById(R.id.userPhotoImageView);
            viewHolder.mCheckedImageView = (ImageView) convertView.findViewById(R.id.userPhotoSelectedImageView);
            viewHolder.mNameTextView = (TextView) convertView.findViewById(R.id.usernameLabelTextView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ParseUser mUser = mParseUsers.get(position);
        String email = mUser.getEmail().toLowerCase();
        //Check email is valid
        if(email.equals("")){
            viewHolder.mUserPhotoImageView.setImageResource(R.drawable.avatar_empty);
        }
        else {
            String hash = MD5Util.md5Hex(email);
            //Size
            // By default, images are presented at 80px by 80px if no size parameter is supplied.
            // You may request a specific image size, which will be dynamically delivered from
            // Gravatar by using the s= or size= parameter and passing a single pixel dimension
            // (since the images are square)
            //Response
            // To use these options, just pass one of the following keywords as the d= parameter to an image request:
            // 404: do not load any image if none is associated with the email hash, instead return an
            // HTTP 404 (File Not Found) response
            String gravatarUrl = "http://www.gravatar.com/avatar/"+ hash + "?s=204&d=404";
            Log.d("URL Check :", gravatarUrl);
            Picasso.with(getContext())
                   .load(gravatarUrl)
                   .placeholder(R.drawable.avatar_empty)
                   .into(viewHolder.mUserPhotoImageView);
        }
        viewHolder.mNameTextView.setText(mUser.getUsername());
        GridView gridView = (GridView) parent;
        if(gridView.isItemChecked(position)){
            viewHolder.mCheckedImageView.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.mCheckedImageView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    //just views
    private static class ViewHolder{
        ImageView mUserPhotoImageView;
        ImageView mCheckedImageView;
        TextView mNameTextView;
    }

    public void refill(List<ParseUser> users){
        mParseUsers.clear();
        mParseUsers.addAll(users);
        notifyDataSetChanged();
    }



}
