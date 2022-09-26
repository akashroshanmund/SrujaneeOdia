package com.srujanee.sahitya11;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CustomAdapterSearchRecycler extends RecyclerView.Adapter<CustomAdapterSearchRecycler.MyViewHolderNested>  {

    static Context context;
    private ArrayList<String> dataArray;

    public CustomAdapterSearchRecycler(ArrayList<String> topicList, Context context){
        dataArray = new ArrayList<String>();
        this.dataArray = topicList;
        this.context = context;
    }

    public static class MyViewHolderNested extends RecyclerView.ViewHolder {

        TextView searchItem;
        TextView searchTitleNameSelected;

        public MyViewHolderNested(View itemView) {
            super(itemView);
            searchItem = itemView.findViewById(R.id.searchTitleName);
            searchTitleNameSelected = itemView.findViewById(R.id.searchTitleNameSelected);
        }
    }
    @NonNull
    @Override
    public MyViewHolderNested onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_subcategory_recycler, parent, false);
        view.setOnClickListener(topicList.myOnClickListenerSearchRecycler);
        MyViewHolderNested myViewHolderNested = new MyViewHolderNested(view);

        return myViewHolderNested;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderNested holder, int position) {
        try {
           String subCategoryToDisplay = dataArray.get(position);
           subCategoryToDisplay = subCategoryToDisplay.substring(0,1).toUpperCase() + subCategoryToDisplay.substring(1);
           holder.searchItem.setText(subCategoryToDisplay);
            Log.i("SearchCheck", "onBindViewHolder: "+dataArray.get(position));
        }catch (Exception e){
            Log.e(variable.TRY_CATCH_ERROR + " onBindViewHolder", e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }
}
