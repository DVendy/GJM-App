package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Adi on 5/22/2015.
 */
public class ProductSearchActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_search);
        super.initializeActivity();
        super.setPageTitle("Search");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();

        buttonAction();

        if(currentSession.getFilterKey().size() > 0)
        {
            TextView productFilter = (TextView) findViewById(R.id.tvProductSearchFilter);
            productFilter.setText(TextUtils.join(", ", currentSession.getFilterKey()));
        }
    }

    private void buttonAction() {
        //find button action, go to product list page with current parameter
        Button btnFind = (Button)findViewById(R.id.btnProductSearchConfirm);
        btnFind.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View V)
                    {
                        //parsing search key
                        EditText searchKeyField = (EditText) findViewById(R.id.tfProductSearchKey);
                        String inputKey = searchKeyField.getText().toString();
                        if(inputKey != "") {
                            String[] arrKey = inputKey.split("\\s+");
                            ArrayList searchKeyList = new ArrayList<String>(Arrays.asList(arrKey));
                            currentSession.setSearchKey(searchKeyList);
                        }

                        if(safeKey(inputKey)) {
                            //go to product list
                            intent = new Intent("com.example.adi.gjm_app.ProductListActivity");
                            intent.putExtra("SESSION", currentSession);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(ProductSearchActivity.this, "Kata kunci hanya tidak diizinkan, silahkan gunakan huruf, angka, titik, atau tanda kurang", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        //filter button action, go to filter page
        Button btnFilter = (Button)findViewById(R.id.btnProductSearchFilter);
        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //go to filter page
                        intent = new Intent("com.example.adi.gjm_app.ProductFilterActivity");
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);
                    }
                }
        );
    }

    private boolean safeKey(String inputKey) {
        if(inputKey.matches("^[a-zA-Z0-9_\\.]*$"))
            return true;
        else
            return false;
    }
}
