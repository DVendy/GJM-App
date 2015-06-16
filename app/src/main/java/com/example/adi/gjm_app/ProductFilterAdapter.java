package com.example.adi.gjm_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adi on 5/12/2015.
 */
public class ProductFilterAdapter extends BaseAdapter {
    private ArrayList<String> listData;
    private LayoutInflater layoutInflater;

    public ProductFilterAdapter(Context aContext, ArrayList<String> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        filterView holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.single_list_filter, null);
            holder = new filterView();
            holder.itemName = (TextView) convertView.findViewById(R.id.tvProductFilterItem);

            convertView.setTag(holder);
        } else {
            holder = (filterView) convertView.getTag();
        }

        holder.itemName.setText(listData.get(position));

        return convertView;
    }

    public void add(String new_item)
    {
        this.listData.add(new_item);
    }

    public void delete(String key)
    {
        int idx = findIdx(key);
        if(idx>=0) {
            this.listData.remove(idx);
        }
    }

    public void delete(int idx)
    {
        if(idx>=0 && idx<=this.listData.size())
        {
            this.listData.remove(idx);
        }
    }

    public int findIdx(String key)
    {
        int i;
        int idx = -1; //not found
        for(i=0; i<this.listData.size(); i++)
        {
            if(this.listData.get(i) == key)
            {
                idx = i;
                break;
            }
        }

        return idx;
    }

    static class filterView {
        TextView itemName;
    }

}
