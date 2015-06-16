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
public class ProductListAdapter extends BaseAdapter {
    private ArrayList<Product> listData;
    private LayoutInflater layoutInflater;

    public ProductListAdapter(Context aContext, ArrayList<Product> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    public void updateList(ArrayList<Product> newList)
    {
        this.listData = newList;
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

        String tempId = listData.get(position).getItemcode();

        productView holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.single_list_product, null);
            holder = new productView();
            holder.codeView = (TextView) convertView.findViewById(R.id.tvItemcode);
            holder.categoryView = (TextView) convertView.findViewById(R.id.tvItemdistributor);
            holder.nameView = (TextView) convertView.findViewById(R.id.tvItemname);
            holder.currencyView = (TextView) convertView.findViewById(R.id.tvItemcurrency);
            holder.priceView = (TextView) convertView.findViewById(R.id.tvItemprice);

            convertView.setTag(holder);
        } else {
            holder = (productView) convertView.getTag();
        }

        holder.codeView.setText(listData.get(position).getItemcode());
        holder.categoryView.setText(listData.get(position).getMerek());
        holder.nameView.setText(listData.get(position).getName());
        /*if(listData.get(position).getKurs().equals("NULL"))
            holder.currencyView.setText(listData.get(position).nullLabel);
        else*/
            holder.currencyView.setText(listData.get(position).getKurs());
        holder.priceView.setText(listData.get(position).getPrice());

        return convertView;
    }

    static class productView {
        TextView codeView;
        TextView categoryView;
        TextView nameView;
        TextView currencyView;
        TextView priceView;
    }

}
