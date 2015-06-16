package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Adi on 5/22/2015.
 */
public class NewsDetailActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;
    private News currentNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_news_detail);
        super.initializeActivity();
        super.setPageTitle("News");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();

        //get data
        currentNews = (News) intent.getExtras().getSerializable("NEWS");

        //set all
        TextView date = (TextView) findViewById(R.id.tvNewsDetailDate);
        TextView title = (TextView) findViewById(R.id.tvNewsDetailTitle);
        WebView desc = (WebView) findViewById(R.id.tvNewsDetailDescription);

        date.setText(currentNews.getFormattedDate("d MMM y"));
        title.setText(currentNews.getTitle());
        desc.loadDataWithBaseURL(null, "<html><body style='color:#8092aa'>"+ currentNews.getContent() +"</body></html>", "text/html", "utf-8", null);
    }

}
