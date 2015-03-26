package com.thefuzzybrain.ishan0445.ribbit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;


public class LoginActivity extends ActionBarActivity {
    protected Toolbar toolbar;
    protected TextView tv , tvFP;
    protected EditText etUserName,etPassword;
    protected Button bt;
    CircularProgressBar cpb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        toolbar = (Toolbar) findViewById(R.id.appBarLogin);
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.tvSignUp);
        tvFP = (TextView) findViewById(R.id.tvForgotPassword);

        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bt = (Button) findViewById(R.id.btLogin);
        cpb = (CircularProgressBar) findViewById(R.id.cpbLogin);

        cpb.setVisibility(View.INVISIBLE);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpb.setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(etUserName.getText().toString().toLowerCase(),
                        etPassword.getText().toString(), new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    cpb.setVisibility(View.INVISIBLE);
                                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(in);
                                    finish();
                                } else {
                                    cpb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(in);
            }
        });

        tvFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(in);
            }
        });
    }
}
