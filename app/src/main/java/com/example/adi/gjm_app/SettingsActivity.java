package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Adi on 5/8/2015.
 */
public class SettingsActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;
    private User newUser;

    private EditText name;
    private EditText newPass;
    private EditText newPassConfirm;
    private Button updateBtn;
    private LinearLayout container;

    private DatabaseHandler dbHandler;
    private ServerDatabaseHandler svdbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_settings);
        super.initializeActivity();
        super.setPageTitle("My Profile");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();
        dbHandler = new DatabaseHandler(getApplicationContext());
        svdbHandler = new ServerDatabaseHandler(currentSession.getBaseUrl());

        name = (EditText) findViewById(R.id.tfSettingsUsername);
        newUser = dbHandler.getUser(currentSession.getUsername());
        name.setText(newUser.getName());
        newPass = (EditText) findViewById(R.id.tfSettingsNewPass);
        newPassConfirm = (EditText) findViewById(R.id.tfSettingsNewPassConfirm);
        updateBtn = (Button) findViewById(R.id.btnSettingsUpdate);
        container = (LinearLayout) findViewById(R.id.settingsContainer);
        actionButtonListener();
    }

    private void actionButtonListener() {
        updateBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //pre login actions
                        disableActivity();

                        //update data to server actions
                        updateProfileAsyncTask updateProfileTask = new updateProfileAsyncTask();
                        updateProfileTask.execute("abc", "10", "Hello world");
                    }
                }
        );
    }

    private class updateProfileAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String...arg) {

            if(newUser!=null) {
                if (!name.getText().toString().isEmpty() && !newPass.getText().toString().isEmpty()) { //username and new password not empty
                    if (newPass.getText().toString().equals(newPassConfirm.getText().toString())) //new password and confirmation password is equal
                    {
                        //update user data
                        newUser.setName(name.getText().toString());
                        newUser.setPassword(newPass.getText().toString());

                        //update to server
                        int updateActionStatus = 0;
                        try {
                            updateActionStatus = svdbHandler.updateUser(newUser);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                        if (updateActionStatus==1) //success
                        {
                            //update to phone db
                            dbHandler.updateUser(newUser);

                            runOnUiThread(new Thread() {
                                public void run() {
                                    enableActivity();
                                    Toast.makeText(SettingsActivity.this, "Data berhasil diubah", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else // failing
                        {
                            runOnUiThread(new Thread() {
                                public void run() {
                                    enableActivity();
                                    Toast.makeText(SettingsActivity.this, "Tidak dapat terhubung dengan server, perubahan dibatalkan", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Thread() {
                            public void run() {
                                enableActivity();
                                Toast.makeText(SettingsActivity.this, "Password baru tidak sesuai dengan konfirmasinya", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Thread() {
                        public void run() {
                            enableActivity();
                            Toast.makeText(SettingsActivity.this, "Nama dan password baru tidak boleh kosong", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                runOnUiThread(new Thread() {
                    public void run() {
                        enableActivity();
                        Toast.makeText(SettingsActivity.this, "Terjadi kesalahan pada sistem", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            //do nothing
        }
    }

    private void disableActivity()
    {
        name.setEnabled(false);
        newPass.setEnabled(false);
        newPassConfirm.setEnabled(false);

        container.setBackgroundColor(Color.parseColor("#feeaf1"));
        updateBtn.setBackgroundColor(Color.parseColor("#ff4081"));
    }

    private void enableActivity()
    {
        name.setEnabled(true);
        newPass.setEnabled(true);
        newPassConfirm.setEnabled(true);

        container.setBackgroundColor(Color.parseColor("#f2f6f9"));
        updateBtn.setBackgroundColor(Color.parseColor("#00a4e7"));
    }
}
