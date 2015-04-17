package com.jiangziandroid.ribbit;

import android.app.Application;

import com.parse.Parse;


/**
 * Created by JeremyYCJiang on 2015/4/16.
 */
public class RibbitApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lzqxZmJ61vrWyoBTj9Kmo9BYxiMZ8X9nB4DAsmQt", "0Te44SxLbw0Y8XTBzdylgX2gUB6h6c4tGkesCDn8");
        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("foo", "jeremy");
        //testObject.saveInBackground();
    }
}
