package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Adi on 5/22/2015.
 */
public class ProductDetailActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_product_detail);
        super.initializeActivity();
        super.setPageTitle("Product");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();

        //get data
        currentProduct = (Product) intent.getExtras().getSerializable("PRODUCT");

        //set all
        TextView currency = (TextView) findViewById(R.id.tvProductDetailCurrency);
        TextView price = (TextView) findViewById(R.id.tvProductDetailPrice);
        TextView code = (TextView) findViewById(R.id.tvProductDetailCode);
        TextView date = (TextView) findViewById(R.id.tvProductDetailDate);
        TextView name = (TextView) findViewById(R.id.tvProductDetailName);
        TextView category = (TextView) findViewById(R.id.tvProductDetailCategory);
        TextView description = (TextView) findViewById(R.id.tvProductDetailDescription);

        currency.setText(currentProduct.getKurs());
        price.setText(currentProduct.getPrice());
        code.setText(currentProduct.getItemcode());
        date.setText(currentProduct.getFormatedDate(1));
        name.setText(currentProduct.getName());
        category.setText(currentProduct.getMerek());

        String desc = "";
        if(!currentProduct.getModel().equals(currentProduct.nullLabel))
            desc += currentProduct.getModel() + "\n";
        if(!currentProduct.getSpec().equals(currentProduct.nullLabel))
            desc += currentProduct.getSpec() + "\n";
        if(!currentProduct.getRegistrasi().equals(currentProduct.nullLabel))
            desc += currentProduct.getRegistrasi() + "\n";
        description.setText(desc);

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
            //go to details
            intent = new Intent("com.example.adi.gjm_app.SettingsActivity");
            intent.putExtra("SESSION",currentSession);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_product) {
            //go to details
            intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
            intent.putExtra("SESSION", currentSession);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_news) {
            //go to details
            intent = new Intent("com.example.adi.gjm_app.NewsListActivity");
            intent.putExtra("SESSION", currentSession);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.action_exit)
        {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
