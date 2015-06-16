package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    private DatabaseHandler dbHandler;
    private ServerDatabaseHandler svdbHandler;
    final String baseUrl = "http://192.168.10.79"; //server address
    private Session currentSession;

    private TextView txtUsername;
    private String uinUsername;
    private TextView txtPassword;
    private String uinPassword;
    private Button loginButton;

    private boolean isConnectedStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_login);

        currentSession = new Session();
        currentSession.setUpdateStatus(1);
        currentSession.setBaseUrl(baseUrl);
        dbHandler = new DatabaseHandler(getApplicationContext());
        svdbHandler = new ServerDatabaseHandler(baseUrl);

        dbHandler.createDataBase();

        txtUsername = (TextView)findViewById(R.id.tfUsername);
        txtPassword = (TextView)findViewById(R.id.tfPassword);
        loginButton = (Button) findViewById(R.id.btnLogin);

        LoginBtnListener();
    }

    private void LoginBtnListener()
    {
        Button btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //pre login actions
                        uinUsername = txtUsername.getText().toString();
                        uinPassword = txtPassword.getText().toString();
                        txtUsername.setEnabled(false);
                        txtPassword.setEnabled(false);
                        loginButton.setClickable(false);
                        loginButton.setText("Connecting...");

                        //login actions
                        loginAsyncTask loginTask = new loginAsyncTask();
                        loginTask.execute("abc", "10", "Hello world");
                    }
                }
        );
    }

    private class loginAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String...arg) {
            User tempUser = null;

            try{
                //get user in server
                tempUser = svdbHandler.getUser(uinUsername);

                if(tempUser!=null) {
                    //check for update
                    currentSession.setUpdateStatus(svdbHandler.checkForUpdate(dbHandler.getVersion()));
                    System.out.println(currentSession.getUpdateStatus());
                }
            }
            catch (JSONException e) {
                //string tidak benar
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
                currentSession.setUpdateStatus(1);
                isConnectedStatus = false;
                e.printStackTrace();
            }

            if(tempUser==null) //user not available in server, try to get in local
            {
                //get local user
                tempUser = dbHandler.getUser(uinUsername);

                if(tempUser==null) //user not found in both server and local
                {
                    if(isConnectedStatus)
                        showTaskStatus("Pengguna tidak ditemukan, silahkan hubungi admin");
                    else
                        showTaskStatus("Pengguna tidak ditemukan, tidak dapat terhubung ke server");
                }
                else //user found in local db
                {
                    if(tempUser.isValid(uinUsername,uinPassword)) //user is valid
                    {
                        //create session of user
                        currentSession.setUserId(tempUser.getId());
                        currentSession.setUsername(tempUser.getUsername());

                        //create login history
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        dbHandler.createLoginHistory(dateFormat.format(date), tempUser.getId());

                        firstActionChoice();
                    }
                    else
                    {
                        showTaskStatus("Password tidak sesuai");
                    }
                }
            }
            else //user found in server
            {
                if(tempUser.isValid(uinUsername,uinPassword)) //user is valid
                {
                    //update user
                    dbHandler.deleteUser(tempUser.getId());
                    dbHandler.createUser(tempUser);

                    //create session of user
                    currentSession.setUserId(tempUser.getId());
                    currentSession.setUsername(tempUser.getUsername());

                    //create login history
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    dbHandler.createLoginHistory(dateFormat.format(date), tempUser.getId());

                    //syncronize history
                    try{
                        ArrayList<LoginHistory> logins = dbHandler.getAllLoginHistory();
                        for (LoginHistory item : logins) {
                            svdbHandler.addHistory(item.getDate(), item.getUser_id());
                        }
                        dbHandler.deleteAllLoginHistory();
                    }
                    catch (JSONException e) {
                        //string tidak benar
                        e.printStackTrace();
                    }
                    catch (NullPointerException e)
                    {
                        currentSession.setUpdateStatus(1);
                        isConnectedStatus = false;
                        e.printStackTrace();
                    }

                    firstActionChoice();
                }
                else //user not valid, wrong password
                {
                    showTaskStatus("Password tidak sesuai");
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            //do nothing
        }

        private void showTaskStatus(final String message)
        {
            runOnUiThread(new Thread() {
                public void run() {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    txtUsername.setEnabled(true);
                    txtPassword.setEnabled(true);
                    loginButton.setClickable(true);
                    loginButton.setText("Login");
                }
            });
        }
    }

    private void firstActionChoice()
    {
        if(currentSession.getUpdateStatus()==0)
        {
            //need update go to update screen
            Intent intent = new Intent("com.example.adi.gjm_app.UpdateCheckActivity");
            intent.putExtra("SESSION",currentSession);
            startActivity(intent);
        }
        else
        {
            //no need update go to list
            Intent intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
            intent.putExtra("SESSION",currentSession);
            startActivity(intent);
        }
    }
}
