package com.srujanee.sahitya11;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class customAdapterSearchListView extends BaseAdapter {
    ArrayList<String> searchListBuffer;
    ArrayList<String> searchListToDisplay;
    Context context;
    Activity activity;
    public customAdapterSearchListView(){

    }
    public customAdapterSearchListView(ArrayList<String> searchList, Context context){
        this.searchListBuffer = new ArrayList<>();
        this.searchListToDisplay = new ArrayList<>();
        this.context = context;
        this.activity = (Activity)context;

        this.searchListBuffer = searchList;
    }

    class ViewHolderList {
        TextView tvList;
    }
    @Override
    public int getCount() {
        return searchListToDisplay.size();
    }

    @Override
    public Object getItem(int position) {
        return searchListToDisplay.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolderList holder = new ViewHolderList();
        if(view == null) {
            view = inflater.inflate(R.layout.custom_list_layout, null);
            holder.tvList = (TextView) view.findViewById(R.id.listViewTextView);
            view.setTag(holder);
        }else{
            holder = (ViewHolderList)view.getTag();
        }
        holder.tvList.setText(searchListToDisplay.get(position));
        return view;
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        searchListToDisplay.clear();

        if (charText.length() == 0) {
            //searchListToDisplay.addAll(searchListBuffer);
        } else {

            for (String item : searchListBuffer) {
                if (item.toLowerCase(Locale.getDefault()).contains(charText)) {
                    searchListToDisplay.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
