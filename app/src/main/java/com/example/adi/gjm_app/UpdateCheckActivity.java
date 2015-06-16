package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Adi on 5/8/2015.
 */
public class UpdateCheckActivity extends Activity {

    private DatabaseHandler dbHandler;
    private ServerDatabaseHandler svdbHandler;

    private TextView updateInfo;
    private Session currentSession;

    private LinearLayout updateLayout;
    private Button btnUpdate;
    private Button btnUpdateSkip;
    private TextView tvUpdateSkip;
    private TextView tvUpdateInfo;
    private LinearLayout updateSkipLayout;
    private ImageView line;

    private Intent intent;

    final int productUpdateLimit = 1000; //change this
    int percentage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_update_check);

        intent = getIntent();
        currentSession = (Session) intent.getExtras().getSerializable("SESSION");
        dbHandler = new DatabaseHandler(getApplicationContext());
        svdbHandler = new ServerDatabaseHandler(currentSession.getBaseUrl());

        //initialize tv and btn
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdateSkip = (Button)findViewById(R.id.btnUpdateSkip);
        tvUpdateInfo = (TextView) findViewById(R.id.tvUpdatingInfo);
        tvUpdateInfo.setVisibility(View.GONE);
        updateSkipLayout = (LinearLayout) findViewById(R.id.updateSkip);
        updateLayout = (LinearLayout) findViewById(R.id.updateContainer);
        line = (ImageView) findViewById(R.id.updateLine);

        UpdateBtnListener();
    }

    /* Update btn and skip btn action listener */
    private void UpdateBtnListener()
    {
        //update btn is pressed
        btnUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //disable all activity around
                        disableActivity();

                        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                "MyWakelockTag");
                        wakeLock.acquire();

                        //updating data
                        updateAsyncTask updateTask = new updateAsyncTask();
                        updateTask.execute("abc", "10", "Hello world");

                        if(updateTask.isCancelled())
                        {
                            enableActivity();
                            Toast.makeText(UpdateCheckActivity.this, "Update gagal, silahkan ulangi lagi", Toast.LENGTH_LONG).show();
                        }

                        wakeLock.release();
                    }
                }
        );

        //skip btn is pressed, go to listview with indicator
        btnUpdateSkip.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //no need update go to list
                        Intent intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);
                    }
                }
        );
    }

    private void disableActivity() {
        tvUpdateInfo.setVisibility(View.VISIBLE);

        updateSkipLayout.setVisibility(View.GONE);

        updateLayout.setBackgroundColor(Color.parseColor("#feeaf1"));
        btnUpdate.setBackgroundColor(Color.parseColor("#ff4081"));
        line.setBackgroundColor(Color.parseColor("#ff4081"));
        btnUpdate.setText("0 %");
    }

    private void enableActivity()
    {
        tvUpdateInfo.setVisibility(View.GONE);

        updateSkipLayout.setVisibility(View.VISIBLE);

        updateLayout.setBackgroundColor(Color.parseColor("#f2f6f9"));
        btnUpdate.setBackgroundColor(Color.parseColor("#00a4e7"));
        line.setBackgroundColor(Color.parseColor("#00a4e7"));
        btnUpdate.setText("Perbaharui");
    }

    private class updateAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String...arg) {

            try {
                int count = svdbHandler.getProductCount(dbHandler.getVersion());
                int iterasi =  (int) Math.ceil((double)count/(double)productUpdateLimit);
                String version = dbHandler.getVersion();
                int i;

                for (i = 0; i<iterasi; i++){
                    List<Product> products = svdbHandler.getProducts(version, productUpdateLimit, i * productUpdateLimit);

                    if(products==null) {//fail on connection
                        runOnUiThread(new Thread() {
                            public void run() {
                                enableActivity();
                            }
                        });
                        break;
                    }
                    else {
                        dbHandler.deleteProducts(products);

                        for (Product item : products) {
                            dbHandler.createProduct(item);
                        }

                        percentage = (int) (((double) (i * productUpdateLimit) / (double) count) * 100);

                        runOnUiThread(new Thread() {
                            public void run() {
                                btnUpdate.setText(percentage + " %");
                            }
                        });
                    }

                    if (isCancelled())
                        break;
                }

                if(count>=0 && i==iterasi) { //not failing in counting products and
                    //update current version on local db
                    dbHandler.updateVersion(svdbHandler.getVersion());

                    currentSession.setUpdateStatus(2); //data on update
                }
                else
                {
                    currentSession.setUpdateStatus(1); //failing on connection
                }

                runOnUiThread(new Thread() {
                    public void run() {

                        if(currentSession.getUpdateStatus()==2) { //update success
                            updateLayout.setBackgroundColor(Color.parseColor("#f2f6f9")); //bug in background

                            //go to product list activity
                            Intent intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                            intent.putExtra("SESSION", currentSession);
                            startActivity(intent);
                        }else{
                            enableActivity();
                            Toast.makeText(UpdateCheckActivity.this, "Tidak dapat terhubung dengan server", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (JSONException e) {

                //fail on parsing
                runOnUiThread(new Thread() {
                    public void run() {
                        Toast.makeText(UpdateCheckActivity.this, "Terjadi kesalahan pada server, silahkan ulangi beberapa saat lagi", Toast.LENGTH_LONG).show();
                        enableActivity();
                    }
                });

                e.printStackTrace();
            }

            if (currentSession.getUpdateStatus() == 2) //update done go to product list
            {
                runOnUiThread(new Thread() {
                    public void run() {
                        //update data finished go to list
                        Intent intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
        }

    }
}
