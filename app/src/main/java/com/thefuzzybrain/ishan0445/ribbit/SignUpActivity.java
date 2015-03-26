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
import com.parse.SignUpCallback;


import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class SignUpActivity extends ActionBarActivity {
    private EditText etUserName, etPassword1, etPassword2, etEmail;
    CircularProgressBar cpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup_avtivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.appBarSignUp);
        etUserName = (EditText) findViewById(R.id.etUserNameSignUp);
        etPassword1 = (EditText) findViewById(R.id.etPasswordSignUp1);
        etPassword2 = (EditText) findViewById(R.id.etPasswordSignUp2);
        etEmail = (EditText) findViewById(R.id.etEmail);
        Button bt = (Button) findViewById(R.id.btSignUp);
        cpb = (CircularProgressBar) findViewById(R.id.cpbSignUp);


        cpb.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpb.setVisibility(View.VISIBLE);
                String un, pwd, eml;
                un = etUserName.getText().toString().toLowerCase().trim();
                pwd = etPassword1.getText().toString();
                eml = etEmail.getText().toString().toLowerCase().trim();

                if (!eml.equals("")) {
                    if (pwd.equals(etPassword2.getText().toString().trim()) && !pwd.equals("")) {
                        ParseUser pu = new ParseUser();
                        pu.setUsername(un);
                        pu.setPassword(pwd);
                        pu.setEmail(eml);
                        pu.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    cpb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                    Intent in = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(in);
                                } else {
                                    cpb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else if (pwd.equals("")) {
                        cpb.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Password Cannot Be Empty", Toast.LENGTH_LONG).show();
                    } else {
                        cpb.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Password Mismatch", Toast.LENGTH_LONG).show();
                    }
                } else {
                    cpb.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Email field required", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
