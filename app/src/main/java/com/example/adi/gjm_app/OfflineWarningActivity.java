package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
public class OfflineWarningActivity extends Activity {

    private TextView updateInfo;
    private Session currentSession;
    private Intent intent;
    private ServerDatabaseHandler svdbHandler;
    private DatabaseHandler dbHandler;

    private LinearLayout contentContainer;
    private LinearLayout skipConnect;
    private Button btnTryAgain;
    private Button btnBack;
    private ImageView loaderConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_offline_warning);

        intent = getIntent();
        currentSession = (Session) intent.getExtras().getSerializable("SESSION");

        //initialize tv and btn
        contentContainer = (LinearLayout)findViewById(R.id.offlineWarningContainer);
        skipConnect = (LinearLayout)findViewById(R.id.connectSkip);
        btnTryAgain = (Button)findViewById(R.id.btnOfflineWarningTryAgain);
        btnBack = (Button) findViewById(R.id.btnOfflineWarningBack);
        loaderConnect = (ImageView) findViewById(R.id.loaderOfflineConnect);

        svdbHandler = new ServerDatabaseHandler(currentSession.getBaseUrl());
        dbHandler = new DatabaseHandler(getApplicationContext());

        UpdateBtnListener();
    }

    /* Connect btn and back btn action listener */
    private void UpdateBtnListener()
    {
        //connect btn is pressed
        btnTryAgain.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //disable all button
                        disableActivity();

                        //run connect
                        connectAsyncTask connectTask = new connectAsyncTask();
                        connectTask.execute("abc", "10", "Hello world");

                        //update data finished go to list
                        /*
                        intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                        currentSession.setUpdateStatus(2);
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);*/
                    }
                }
        );

        //back btn is pressed, go to listview with indicator
        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //no need update go back
                        onBackPressed();
                    }
                }
        );
    }

    private class connectAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String...arg) {
            try{
                currentSession.setUpdateStatus(svdbHandler.checkForUpdate(dbHandler.getVersion()));

                runOnUiThread(new Thread() {
                    public void run() {
                        if(currentSession.getUpdateStatus()==0)
                        {
                            //need update go to update screen
                            Intent intent = new Intent("com.example.adi.gjm_app.UpdateCheckActivity");
                            intent.putExtra("SESSION",currentSession);
                            startActivity(intent);
                        }
                        else
                        {
                            //no need update go to product list
                            Intent intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                            intent.putExtra("SESSION",currentSession);
                            startActivity(intent);
                        }
                    }
                });
            }
            catch (NullPointerException e) //cant connect to server
            {
                runOnUiThread(new Thread() {
                    public void run() {
                        enableActivity();
                    }
                });
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            //do nothing
        }
    }

    private void disableActivity() {
        contentContainer.setBackgroundColor(Color.parseColor("#feeaf1"));
        btnTryAgain.setBackgroundColor(Color.parseColor("#ff4081"));
        btnTryAgain.setText("Mencoba...");
        btnTryAgain.setClickable(false);

        loaderConnect.setBackgroundResource(R.drawable.loader_animation);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) loaderConnect.getBackground();
        // Start the animation (looped playback by default).
        frameAnimation.start();

        skipConnect.setVisibility(View.GONE);
    }

    private void enableActivity() {
        contentContainer.setBackgroundColor(Color.parseColor("#f2f6f9"));
        btnTryAgain.setBackgroundColor(Color.parseColor("#00a4e7"));
        btnTryAgain.setText("Coba Lagi");
        btnTryAgain.setClickable(true);
        loaderConnect.setBackgroundResource(R.drawable.offline);

        skipConnect.setVisibility(View.VISIBLE);
    }
}
