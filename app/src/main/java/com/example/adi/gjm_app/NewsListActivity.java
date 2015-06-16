package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adi on 5/6/2015.
 */
public class NewsListActivity extends BaseActivity implements EndlessListView.EndlessListener{

    private Intent intent;
    private Session currentSession;
    private DatabaseHandler dbHandler;
    private ServerDatabaseHandler svdbHandler;
    final Context context = this;

    private List allNewsData;

    private EndlessListView lv;
    private final int maxItem = 10; //change this

    private int curOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_news_list);
        super.initializeActivity();
        super.setPageTitle("News");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();
        dbHandler = new DatabaseHandler(getApplicationContext());
        svdbHandler = new ServerDatabaseHandler(currentSession.getBaseUrl());

        lv = (EndlessListView) findViewById(R.id.lstNews);

        listLoad();
    }

    private void listLoad()
    {
        //update from server
        newsupdateAsyncTask updateTask = new newsupdateAsyncTask();
        updateTask.execute("abc", "10", "Hello world");

        //load from database
        allNewsData = getListData(maxItem,curOffset++);

        NewsListAdapter adp = new NewsListAdapter(this.context, allNewsData, R.layout.single_list_news);
        lv.setLoadingView(R.layout.loading_layout);
        lv.setAdapter(adp);
        lv.setListener(this);
    }

    private List<News> getListData(int limit, int offset)
    {
        //get from database
        List<News> temp = dbHandler.getNews(limit,offset);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                News newsData = (News) o;
                //go to details
                intent = new Intent("com.example.adi.gjm_app.NewsDetailActivity");
                intent.putExtra("SESSION",currentSession);
                intent.putExtra("NEWS",newsData);
                startActivity(intent);
            }
        });

        return temp;
    }

    @Override
    public void loadData() {
        // We load more data here
        FakeNetLoader fl = new FakeNetLoader();
        fl.execute(new String[]{});

    }

    private class newsupdateAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String...arg) {
            try{
                //get news
                List<News> newsList = svdbHandler.getNews(dbHandler.getLatestNewsId());
                System.out.println(newsList.size());

                //insert news to db
                for (News item : newsList) {
                    dbHandler.createNews(item);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void a) {
            //do nothing
        }

    }

    private class FakeNetLoader extends AsyncTask<String, Void, List<News>> {

        @Override
        protected List<News> doInBackground(String... params) {
            try {
                Thread.sleep(1000);
                if(lv.endOfList)
                {
                    runOnUiThread(new Thread() {
                        public void run() {
                        Toast.makeText(NewsListActivity.this, "Tidak ada berita lainnya", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getListData(maxItem, curOffset);
        }

        @Override
        protected void onPostExecute(List<News> result) {
            super.onPostExecute(result);
            lv.addNewData(result);
            curOffset++;
        }

    }
}
