package com.thefuzzybrain.ishan0445.ribbit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import bolts.Task;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class ForgotPasswordActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private EditText et;
    private Button bt;
    CircularProgressBar cpb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.appBarForgot);
        setSupportActionBar(toolbar);

        et = (EditText) findViewById(R.id.etResetEmail);
        bt = (Button) findViewById(R.id.btResetPassword);
        cpb = (CircularProgressBar) findViewById(R.id.cpbForgotPassword);
        cpb.setVisibility(View.INVISIBLE);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpb.setVisibility(View.VISIBLE);
                String eml = et.getText().toString().trim();
                ParseUser.requestPasswordResetInBackground(eml, new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            cpb.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Password reset email sent successfully", Toast.LENGTH_LONG).show();
                            Intent in = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
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

    }


}
