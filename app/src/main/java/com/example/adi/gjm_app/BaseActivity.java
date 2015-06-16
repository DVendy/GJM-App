package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Adi on 6/4/2015.
 */
public class BaseActivity extends Activity {

    private Intent baseIntent;
    private Session baseSession;
    private TextView mainTitle;
    private ImageButton btnMenu;
    private ImageView indicator;

    protected void initializeActivity() {
        baseIntent = getIntent();
        baseSession = (Session) baseIntent.getExtras().getSerializable("SESSION");
        mainTitle = (TextView)findViewById(R.id.tvMainTitle);

        //top menu button action
        btnMenu = (ImageButton)findViewById(R.id.btnMainMenu);
        btnMenu.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        openOptionsMenu();
                    }
                }
        );

        //indicator actions
        indicator = (ImageView) findViewById(R.id.statusIndicator);
        if(baseSession.getUpdateStatus()==0)
        {
            //set indicator to outdated
            indicator.setBackgroundResource(R.drawable.outdated);
            indicator.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //reset search, filter key, and paging
                            baseSession.resetSearchFilter();

                            baseIntent = new Intent("com.example.adi.gjm_app.UpdateCheckActivity");
                            baseIntent.putExtra("SESSION",baseSession);
                            startActivity(baseIntent);
                        }
                    }
            );
        }
        else if(baseSession.getUpdateStatus()==1)
        {
            //set indicator to iddle
            indicator.setBackgroundResource(R.drawable.iddle);
            indicator.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //reset search, filter key, and paging
                            baseSession.resetSearchFilter();

                            baseIntent = new Intent("com.example.adi.gjm_app.OfflineWarningActivity");
                            baseIntent.putExtra("SESSION",baseSession);
                            startActivity(baseIntent);
                        }
                    }
            );
        }
    }

    public void setPageTitle(String title)
    {
        mainTitle.setText(title); //set page name
    }

    public Session getBaseSession()
    {
        return this.baseSession;
    }

    public Intent getBaseIntent() {
        return baseIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            //go to settings
            baseIntent = new Intent("com.example.adi.gjm_app.SettingsActivity");
            baseIntent.putExtra("SESSION",baseSession);
            startActivity(baseIntent);
            return true;
        }
        else if (id == R.id.action_product) {
            //reset search, filter key, and paging
            baseSession.resetSearchFilter();

            //go to product list page
            baseIntent = new Intent("com.example.adi.gjm_app.ProductListActivity");
            baseIntent.putExtra("SESSION", baseSession);
            startActivity(baseIntent);
            return true;
        }
        else if (id == R.id.action_news) {
            //reset search, filter key, and paging
            baseSession.resetSearchFilter();

            //go to news list page
            baseIntent = new Intent("com.example.adi.gjm_app.NewsListActivity");
            baseIntent.putExtra("SESSION", baseSession);
            startActivity(baseIntent);
            return true;
        }
        else if(id == R.id.action_exit)
        {
            baseIntent = new Intent(Intent.ACTION_MAIN);
            baseIntent.addCategory(Intent.CATEGORY_HOME);
            baseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(baseIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
