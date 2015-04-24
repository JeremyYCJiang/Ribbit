package com.jiangziandroid.ribbit;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ViewImageActivity extends Activity {

    @InjectView(R.id.imageView)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.inject(this);
        Uri imageUri = getIntent().getData();
        Picasso.with(this).load(imageUri).into(mImageView);
        //Delete image after 10s
        //Timer timer = new Timer();
        //timer.schedule(new TimerTask() {
        //    @Override
        //    public void run() {
        //        finish();
        //    }
        //}, 10*1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Message Deleted!", Toast.LENGTH_SHORT).show();
    }
}
