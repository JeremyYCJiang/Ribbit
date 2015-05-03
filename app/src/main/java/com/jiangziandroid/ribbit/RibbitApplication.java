package com.jiangziandroid.ribbit;

import android.app.Application;
import android.widget.Toast;

import com.jiangziandroid.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;


public class RibbitApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lzqxZmJ61vrWyoBTj9Kmo9BYxiMZ8X9nB4DAsmQt", "0Te44SxLbw0Y8XTBzdylgX2gUB6h6c4tGkesCDn8");
        //com.parse.PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_stat_ic_launcher);
        ParseInstallation.getCurrentInstallation().saveInBackground(new com.parse.SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = ParseInstallation.getCurrentInstallation().getInstallationId();
                    Toast.makeText(RibbitApplication.this, "User Parse installation ID: " +
                            installationId, Toast.LENGTH_LONG).show();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    Toast.makeText(RibbitApplication.this, "Failed to register to Parse!", Toast.LENGTH_LONG).show();
                }
            }
        });
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "jeremy");
//        testObject.saveInBackground();

//        AVOSCloud.initialize(this, "ka6e1hp9xyh933mkak5f3e51y3zx3hgccl7j9wcu28piybwr",
//                "xi8i9e1l2wmw02mkulpy8awdnpn73dskam2ez1no31rv3qb0");
//        AVObject testObject = new AVObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();
//        //AVInstallation.getCurrentInstallation().saveInBackground();
//        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
//            public void done(AVException e) {
//                if (e == null) {
//                    // 保存成功
//                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
//                    Toast.makeText(RibbitApplication.this, "User AVOSCloud installation ID: " +
//                            installationId, Toast.LENGTH_LONG).show();
//                    // 关联  installationId 到用户表等操作……
//                } else {
//                    // 保存失败，输出错误信息
//                    Toast.makeText(RibbitApplication.this, "Failed to register to AVOSCloud!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//        AVOSCloud.setLogLevel(AVOSCloud.LOG_LEVEL_VERBOSE);
//        AVPush push = new AVPush();
//        JSONObject object = new JSONObject();
//        try {
//            object.put("alert", "push message to android device directly");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        push.setPushToAndroid(true);
//        push.setData(object);
//        push.sendInBackground(new SendCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    // push successfully.
//                    Toast.makeText(RibbitApplication.this, "push successfully!", Toast.LENGTH_LONG).show();
//                } else {
//                    // something wrong.
//                    Toast.makeText(RibbitApplication.this, "something wrong!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

    }


        public static void updateParseInstallation(ParseUser user){
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
            installation.saveInBackground();
        }




}
