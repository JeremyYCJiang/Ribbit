package com.jiangziandroid.ribbit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;
    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;//10MB
    protected Uri mMediaUri;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Check if user is logged in
        /** Whenever you use any signup or login methods, the user is cached on disk.
         You can treat this cache as a session, and automatically assume the user is logged in
         It would be bothersome if the user had to log in every time they open your app.
         You can avoid this by using the cached currentUser object.
         **/
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            Toast.makeText(MainActivity.this, "Welcome, " + currentUser.getUsername(), Toast.LENGTH_LONG).show();
        }


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }



    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //ParseUser currentUser = ParseUser.getCurrentUser();
        //menu.add(currentUser.getUsername());
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Take a Choice")
                        .setItems(R.array.camera_choices, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item
                                switch (which) {
                                    case 0: //Take picture
                                        // create Intent to take a picture and return control to the calling application
                                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        // create a file to save the image
                                        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                        // set the image file name
                                        if (mMediaUri == null) {
                                            //Display an error
                                            Toast.makeText(MainActivity.this, R.string.external_storage_error,
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            // start the image capture Intent
                                            startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                                        }
                                        break;
                                    case 1: //Take video
                                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                                        if (mMediaUri == null) {
                                            Toast.makeText(MainActivity.this, R.string.external_storage_error,
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
                                            startActivityForResult(takeVideoIntent, TAKE_VIDEO_REQUEST);
                                        }
                                        break;
                                    case 2: //Choose picture
                                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        // You will usually specify a broad MIME type (such as image/* or */*),
                                        // resulting in a broad range of content types the user can select from.
                                        choosePhotoIntent.setType("image/*");
                                        startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                                        break;
                                    case 3: //Choose video
                                        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                        chooseVideoIntent.setType("video/*");
                                        Toast.makeText(MainActivity.this, "Please note that the video file should less than 10MB!",
                                                Toast.LENGTH_LONG).show();
                                        startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                                        break;
                                }
                            }

                            private Uri getOutputMediaFileUri(int mediaType) {
                                // To be safe, you should check that the SDCard is mounted
                                // using Environment.getExternalStorageState() before doing this.
                                if (isExternalStorageAvailable()) {
                                    //Get the Uri
                                    //1.Get the external storage directory
                                    String appName = MainActivity.this.getString(R.string.app_name);
                                    File mediaStorageDir = new File(Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                            appName);
                                    // This location works best if you want the created images to be shared
                                    // between applications and persist after your app has been uninstalled.
                                    //2.Create our subDirectory
                                    // Create the storage directory if it does not exist
                                    if (!mediaStorageDir.exists()) {
                                        if (!mediaStorageDir.mkdirs()) {
                                            Toast.makeText(MainActivity.this, "failed to create directory",
                                                    Toast.LENGTH_LONG).show();
                                            return null;
                                        }
                                    }
                                    //3.Create a file name
                                    //4.Create the file
                                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA)
                                            .format(new Date());
                                    File mediaFile;
                                    if (mediaType == MEDIA_TYPE_IMAGE) {
                                        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                                                "IMG_" + timeStamp + ".jpg");
                                    } else if (mediaType == MEDIA_TYPE_VIDEO) {
                                        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                                                "VID_" + timeStamp + ".mp4");
                                    } else {
                                        return null;
                                    }
                                    //5.Return the file's Uri
                                    return Uri.fromFile(mediaFile);
                                } else {
                                    return null;
                                }
                            }

                            private boolean isExternalStorageAvailable() {
                                String state = Environment.getExternalStorageState();
                                return state.equals(Environment.MEDIA_MOUNTED);
                            }

                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fileType = null;
        if(requestCode == TAKE_PHOTO_REQUEST || requestCode == PICK_PHOTO_REQUEST){
            fileType = ParseConstants.TYPE_IMAGE;
        }
        else if(requestCode == TAKE_VIDEO_REQUEST || requestCode == PICK_VIDEO_REQUEST){
            fileType = ParseConstants.TYPE_VIDEO;
        }
        Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    // Image captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    // invoke the system's media scanner to add your photo to the Media Provider's database,
                    // making it available in the Android Gallery application and to other apps.
                    Intent photoScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    photoScanIntent.setData(data.getData());
                    sendBroadcast(photoScanIntent);
                    mMediaUri = data.getData();
                    Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                    recipientsIntent.setData(mMediaUri);
                    recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
                    startActivity(recipientsIntent);
                    break;

                case TAKE_VIDEO_REQUEST:
                    // Video captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    Intent videoScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    videoScanIntent.setData(data.getData());
                    sendBroadcast(videoScanIntent);
                    mMediaUri = data.getData();
                    Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                    recipientsIntent.setData(mMediaUri);
                    recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
                    startActivity(recipientsIntent);
                    break;

                case PICK_PHOTO_REQUEST:
                    if (data == null) {
                        Toast.makeText(this, "There was an error, please try again!", Toast.LENGTH_LONG).show();
                    } else {
                        mMediaUri = data.getData();
                        Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                        recipientsIntent.setData(mMediaUri);
                        recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
                        startActivity(recipientsIntent);
                    }
                    break;

                case PICK_VIDEO_REQUEST:
                    if (data == null) {
                        Toast.makeText(this, "There was an error, please try again!", Toast.LENGTH_LONG).show();
                    } else {
                        //make sure the file is less than 10MB
                        int fileSize = 0;
                        InputStream inputStream = null;
                        try {
                            inputStream = getContentResolver().openInputStream(data.getData());
                            fileSize = inputStream.available();
                        } catch (FileNotFoundException e) {
                            Toast.makeText(this, "FileNotFoundException: " + e, Toast.LENGTH_LONG).show();
                            return;
                        } catch (IOException e) {
                            Toast.makeText(this, "IOException: " + e, Toast.LENGTH_LONG).show();
                            return;
                        } finally {
                            try {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            } catch (IOException e) {/**Intentionally blank */}
                        }
                        if (fileSize >= FILE_SIZE_LIMIT) {
                            Toast.makeText(this, "The selected video is too large, please try a litter one ^.^",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            mMediaUri = data.getData();
                            Toast.makeText(this, "Media URI: " + mMediaUri, Toast.LENGTH_LONG).show();
                            recipientsIntent.setData(mMediaUri);
                            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
                            startActivity(recipientsIntent);
                        }
                    }
                    break;
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            // User cancelled the media capture
            Toast.makeText(this, "User canceled the media capture or pick!", Toast.LENGTH_LONG).show();
        }
        else {
            // Media capture failed, advise user
            Toast.makeText(this, "Media capture or pick failed, pleas try again!", Toast.LENGTH_LONG).show();
        }
    }

        @Override
        public void onTabSelected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction){
            // When the given tab is selected, switch to the corresponding page in
            // the ViewPager.
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction){
        }

        @Override
        public void onTabReselected (ActionBar.Tab tab, FragmentTransaction fragmentTransaction){
        }


 }



