package com.jiangziandroid.ribbit;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ViewImageActivity extends ActionBarActivity {

    @InjectView(R.id.imageView) ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.inject(this);
        Uri imageUri = getIntent().getData();
        Picasso.with(this).load(imageUri).into(mImageView);
    }

}
