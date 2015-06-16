package com.example.adi.gjm_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adi on 5/12/2015.
 */
public class NewsListAdapter extends ArrayAdapter<News> {
    private List<News> listData;
    private Context ctx;
    private int layoutId;
    private LayoutInflater layoutInflater;

    public NewsListAdapter(Context ctx, List<News> listData, int layoutId) {
        super(ctx, layoutId, listData);
        this.listData = listData;
        this.ctx = ctx;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return listData.size() ;
    }

    @Override
    public News getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listData.get(position).hashCode();
    }

    public void addList(List<News> newList)
    {
        for(News item : newList) {
            listData.add(item);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        newsView holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, parent, false);

            holder = new newsView();
            holder.titleView = (TextView) convertView.findViewById(R.id.tvNewsListTitle);
            holder.dayView = (TextView) convertView.findViewById(R.id.tvNewsListDay);
            holder.monthView = (TextView) convertView.findViewById(R.id.tvNewsListMonth);

            convertView.setTag(holder);
        } else {
            holder = (newsView) convertView.getTag();
        }

        String title;
        if(listData.get(position).getTitle().length()<30)
            title = listData.get(position).getTitle();
        else
            title = listData.get(position).getTitle().substring(0,30)+"...";

        holder.titleView.setText(title);
        holder.dayView.setText(listData.get(position).getFormattedDate("d"));
        holder.monthView.setText(listData.get(position).getFormattedDate("MMM"));

        return convertView;
    }

    static class newsView {
        TextView titleView;
        TextView dayView;
        TextView monthView;
    }

}
