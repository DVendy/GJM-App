package com.example.adi.gjm_app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Adi on 5/6/2015.
 */
public class ProductListActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;
    private ArrayList tempProductData;
    private ProductListAdapter listAdapter;
    private String query;
    final Context context = this;
    private ListView lv1;
    private TextView listInfo;

    //max product to be shown
    final int maxProduct = 20;

    //paging
    private int productCount = 0;
    private int productCurrentPageIdx = 1;
    private int productPageCount = 1;

    //
    private Button btnGoto;
    private Button btnNext;
    private Button btnPrev;

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_product_list);
        super.initializeActivity();
        super.setPageTitle("Product");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();
        dbHandler = new DatabaseHandler(getApplicationContext());

        btnGoto = (Button)findViewById(R.id.btnProductListGoto);
        btnGoto.setBackgroundResource(R.drawable.button);
        btnNext = (Button)findViewById(R.id.btnProductListNextPage);
        btnPrev = (Button)findViewById(R.id.btnProductListPrevPage);

        listLoad();
        buttonAction();
    }

    private void buttonAction()
    {
        //go to button action, showing dialog
        btnGoto.setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View V)
                {
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.popup_goto);
                    dialog.setTitle("halaman "+ productCurrentPageIdx +" / "+ productPageCount);

                    // set the custom dialog components - text, image and button
                    TextView text = (TextView) dialog.findViewById(R.id.tvPopupInfo);
                    text.setText("saya ingin ke halaman : ");

                    Button dialogButton = (Button) dialog.findViewById(R.id.btnPopupConfirm);
                    final EditText gotoIdx = (EditText) dialog.findViewById(R.id.tfInputGoTo);
                    gotoIdx.setText(Integer.toString(productCurrentPageIdx));

                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //get user input
                            int thePage = Integer.parseInt(gotoIdx.getText().toString());

                            if(thePage>0 && thePage<=productPageCount) {
                                //set current page index to input value
                                productCurrentPageIdx = thePage;

                                //update data
                                moveToPage(productCurrentPageIdx);
                                listAdapter.updateList(tempProductData);
                                listAdapter.notifyDataSetChanged();
                                listInfoRefresh();
                                lv1.setSelectionAfterHeaderView();
                            }
                            else
                            {
                                Toast.makeText(ProductListActivity.this, "Anda memasukan angka yang salah", Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        );

        //find button action, go to find page
        Button btnFind = (Button)findViewById(R.id.btnProductListFind);
        btnFind.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //go to details
                        intent = new Intent("com.example.adi.gjm_app.ProductSearchActivity");
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);
                    }
                }
        );
    }

    private void setPagingBtnAction()
    {
        if(productCurrentPageIdx>1) //if not in first page prev button should be enabled
        {
            setToActiveBtn(btnPrev);
            btnPrev.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //set current page idx to increase
                            changePageIdx(-1);
                            //update data
                            moveToPage(productCurrentPageIdx);
                            listAdapter.updateList(tempProductData);
                            listAdapter.notifyDataSetChanged();
                            listInfoRefresh();
                            lv1.setSelectionAfterHeaderView();
                        }
                    }
            );
        }
        else
        {
            setToDisabledBtn(btnPrev);
            btnPrev.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //do nothing
                        }
                    }
            );
        }

        //go to next page
        if(productCurrentPageIdx<productPageCount) //if not in last page next page should be enabled
        {
            setToActiveBtn(btnNext);
            btnNext.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //set current page idx to increase
                            changePageIdx(1);
                            //update data
                            moveToPage(productCurrentPageIdx);
                            listAdapter.updateList(tempProductData);
                            listAdapter.notifyDataSetChanged();
                            listInfoRefresh();
                            lv1.setSelectionAfterHeaderView();
                        }
                    }
            );
        }
        else
        {
            setToDisabledBtn(btnNext);
            btnNext.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            //do nothing
                        }
                    }
            );
        }

    }

    private void setToActiveBtn(Button btn)
    {
        //color: active~bg#e4ebf0 & txt#8092ab;
        btn.setBackgroundResource(R.drawable.button);
        btn.setTextColor(Color.parseColor("#8092ab"));
    }

    private void setToDisabledBtn(Button btn)
    {
        //color: disabled~bg#8092ab & txt#fff
        btn.setBackgroundColor(Color.parseColor("#8092ab"));
        btn.setTextColor(Color.parseColor("#ffffff"));
    }

    private void changePageIdx(int diff)
    {
        int temp = productCurrentPageIdx+diff;

        if(temp > 0 && temp <= productPageCount)
        {
            productCurrentPageIdx = temp;
        }
    }

    private void listLoad()
    {
        //build query
        query = "";
        if(currentSession.getSearchKey().size() > 0)
        {
            query += currentSession.buildSearchQuery();
        }
        if(currentSession.getFilterKey().size() > 0)
        {
            if(currentSession.getSearchKey().size() > 0) { //there's query and filter
                query += " AND ";
            }
            query += currentSession.buildFilterQuery();
        }

        //get data
        initializeListData();
        //clear after query using
        currentSession.resetSearchFilter();
        //set list info
        listInfo = (TextView) findViewById(R.id.tvProductListInfo);
        listInfoRefresh();

        //list adapter and action
        lv1 = (ListView) findViewById(R.id.lstProduct);
        listAdapter = new ProductListAdapter(this, tempProductData);
        lv1.setAdapter(listAdapter);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv1.getItemAtPosition(position);
                Product productData = (Product) o;

                //go to details
                intent = new Intent("com.example.adi.gjm_app.ProductDetailActivity");
                intent.putExtra("SESSION", currentSession);
                intent.putExtra("PRODUCT", productData);
                startActivity(intent);

                //Toast.makeText(ProductListActivity.this, "Selected :" + " " + productData.getId(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void listInfoRefresh() {
        DecimalFormat df = new DecimalFormat("#,###,###,##0");
        listInfo.setText(df.format(productCount) +" results in "+ productPageCount +" pages ("+ productCurrentPageIdx +"/"+productPageCount+")" );
    }

    private void initializeListData() {
        //get product count from database
        productCount = dbHandler.countProducts(query);
        productPageCount = (int) Math.ceil((double) productCount/(double)maxProduct);

        //get first page product
        tempProductData = dbHandler.getProducts(maxProduct, 0, query);

        //set paging button action
        setPagingBtnAction();
    }

    public void moveToPage(int pageIdx)
    {
        //get product on spesific page
        tempProductData = dbHandler.getProducts(maxProduct, (pageIdx-1)*maxProduct, query);

        //set paging button action
        setPagingBtnAction();
    }
}
