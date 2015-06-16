package com.example.adi.gjm_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Adi on 5/22/2015.
 */
public class ProductFilterActivity extends BaseActivity {

    private Intent intent;
    private Session currentSession;
    private ProductFilterAdapter listAdapter;
    private ArrayList<String> merkData;
    private ArrayList<String> merkDataChoosen;
    private ListView lvMerkList;
    private AutoCompleteTextView acProductFilter;

    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medium_filter);
        super.initializeActivity();
        super.setPageTitle("Filter");

        //set intent & session
        this.currentSession = super.getBaseSession();
        this.intent = super.getBaseIntent();
        dbHandler = new DatabaseHandler(getApplicationContext());

        getData();
        initializeActivity();
        buttonAction();
    }

    private void buttonAction() {
        //filter button action, go to search page with current parameter
        Button btnFilter = (Button)findViewById(R.id.btnProductFilterConfirm);
        btnFilter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View V) {
                        //set session filter
                        if(merkDataChoosen.size()>0)
                        {
                            currentSession.setFilterKey(merkDataChoosen);
                        }

                        //go to details
                        intent = new Intent("com.example.adi.gjm_app.ProductSearchActivity");
                        intent.putExtra("SESSION", currentSession);
                        startActivity(intent);
                    }
                }
        );

        //on select autocomplete
        acProductFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3) {

                if (listAdapter.findIdx(merkData.get(index)) == -1) { //prevent duplication
                    //add to list
                    listAdapter.add(merkData.get(index));
                    listAdapter.notifyDataSetChanged();

                    //hide infos
                    TextView infos = (TextView) findViewById(R.id.tvProductFilterInfo);
                    if (merkDataChoosen.size() > 0) {
                        infos.setVisibility(View.GONE);
                    }
                }

                //clear field
                acProductFilter.setText("");
            }
        });

        lvMerkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lvMerkList.getItemAtPosition(position);
                String filterData = (String) o;

                int findIdx = listAdapter.findIdx(filterData);
                if(findIdx>=0) { //prevent duplication
                    //add to list
                    listAdapter.delete(findIdx);
                    listAdapter.notifyDataSetChanged();

                    //hide infos
                    TextView infos = (TextView) findViewById(R.id.tvProductFilterInfo);
                    if(merkDataChoosen.size()==0)
                    {
                        infos.setVisibility(View.VISIBLE);
                    }
                }

                //Toast.makeText(ProductFilterActivity.this, "Masuk brow - "+findIdx+" - "+filterData+"-", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getData()
    {
        // Defined Array values to show in ListView
        merkData = dbHandler.getAllMerk();
    }

    public void initializeActivity()
    {
        //set autocomplete
        acProductFilter = (AutoCompleteTextView) findViewById(R.id.acProductFilterField);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, merkData);
        acProductFilter.setAdapter(adapter);
        acProductFilter.setThreshold(3);

        //set listview
        lvMerkList = (ListView) findViewById(R.id.lstMerk);
        merkDataChoosen = new ArrayList<String>();
        if(currentSession.getFilterKey().size() > 0)
        {
            merkDataChoosen = currentSession.getFilterKey();
            TextView infos = (TextView) findViewById(R.id.tvProductFilterInfo);
            infos.setVisibility(View.GONE);
        }
        listAdapter = new ProductFilterAdapter(this, merkDataChoosen);
        lvMerkList.setAdapter(listAdapter);
    }
}
