package com.example.adi.gjm_app;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

/**
 * Created by DVendy - Kreasys on 6/9/2015.
 */
public class EndlessListView extends ListView implements OnScrollListener {

    private View footer;
    private boolean isLoading;
    private EndlessListener listener;
    private NewsListAdapter adapter;
    private int lastArrayCount;
    public boolean endOfList;

    public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnScrollListener(this);

        lastArrayCount = 0;
        endOfList = false;
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);

        lastArrayCount = 0;
        endOfList = false;
    }

    public EndlessListView(Context context) {
        super(context);
        this.setOnScrollListener(this);

        lastArrayCount = 0;
        endOfList = false;
    }

    public void setListener(EndlessListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null)
            return ;

        if (getAdapter().getCount() == 0)
            return ;

        int l = visibleItemCount + firstVisibleItem;

        if (l >= totalItemCount && !isLoading && !endOfList) {
            // It is time to add new data. We call the listener
            this.addFooterView(footer);
            isLoading = true;
            listener.loadData();

            System.out.println(lastArrayCount);
            System.out.println(totalItemCount);
            if(lastArrayCount==totalItemCount)
            {
                endOfList = true;
            }

            //set last array count
            lastArrayCount = totalItemCount;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    public void setLoadingView(int resId) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater.inflate(resId, null);
        this.addFooterView(footer);

    }

    public void setAdapter(NewsListAdapter adapter) {
        super.setAdapter(adapter);
        this.adapter = adapter;
        this.removeFooterView(footer);
    }

    public void addNewData(List<News> data) {

        this.removeFooterView(footer);
        adapter.addList(data);
        adapter.notifyDataSetChanged();
        isLoading = false;
    }


    public EndlessListener setListener() {
        return listener;
    }


    public static interface EndlessListener {
        public void loadData() ;
    }

}