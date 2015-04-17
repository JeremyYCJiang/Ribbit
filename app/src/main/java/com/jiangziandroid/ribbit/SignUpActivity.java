package com.jiangziandroid.ribbit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignUpActivity extends ActionBarActivity {

    @InjectView(R.id.SignUpBackButton) Button mSignUpBackButton;
    @InjectView(R.id.SignUpButton) Button mSignUpButton;
    @InjectView(R.id.SignUpNameEditText) EditText mSignUpNameEditText;
    @InjectView(R.id.SignUpEmailEditText) EditText mSignUpEmailEditText;
    @InjectView(R.id.SignUpPwdEditText) EditText mSignUpPwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mSignUpNameEditText.getText().toString();
                String email = mSignUpEmailEditText.getText().toString();
                String password = mSignUpPwdEditText.getText().toString();
                //remove only the spaces at the beginning or end of the String (not the ones in the middle).
                name = name.trim();
                email = email.trim();
                password = password.trim();
                //check if the user input is empty
                if(name.isEmpty()||email.isEmpty()||password.isEmpty()){
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setTitle(getString(R.string.sign_up_error_title))
                           .setMessage(getString(R.string.sign_up_error_message))
                           .setPositiveButton("OK", null);
                    // 3. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    //create the new user!
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(name);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                // Sign up didn't succeed. Look at the ParseException to figure out what went wrong
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setTitle(R.string.sign_up_error_title)
                                       .setMessage(e.getMessage())
                                       .setPositiveButton("OK", null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        mSignUpBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
