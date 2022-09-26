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


public class CustomAdapterNested extends RecyclerView.Adapter<CustomAdapterNested.MyViewHolderNested>  {

    static Context context;
    private ArrayList<DataWrapper> dataArray;
    int adapterCode;




    public static class MyViewHolderNested extends RecyclerView.ViewHolder {

        RecyclerView recyclerViewNested;
        TextView header;
        CardView cardView;
        private RecyclerView.LayoutManager layoutManager;

        public MyViewHolderNested(View itemView) {
            super(itemView);
            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerViewNested = (RecyclerView)itemView.findViewById(R.id.nestedRecycler);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view_horizontal) ;
            cardView.setElevation(0);

            recyclerViewNested.setLayoutManager(layoutManager);
            recyclerViewNested.setHasFixedSize(true);
            recyclerViewNested.setItemAnimator(new DefaultItemAnimator());
            header = (TextView)itemView.findViewById(R.id.header);

            /* set starting position for recycler view */
            RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
            smoothScroller.setTargetPosition(MainActivity.recyclerViewNestedTargetPosition);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }

    public CustomAdapterNested(ArrayList<DataWrapper> data, Context context, int adapterCode) {
        this.dataArray = data;
        this.context = context;
        this.adapterCode = adapterCode;
    }

    @NonNull
    @Override
    public MyViewHolderNested onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout_horizontal, parent, false);
         MyViewHolderNested myViewHolderNested = new MyViewHolderNested(view);

        return myViewHolderNested;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderNested holder, int position) {
        try {
            CustomAdapter adapter = new CustomAdapter(dataArray.get(position).dataset, context, adapterCode);
            holder.header.setText(dataArray.get(position).getCategoryName());
            holder.recyclerViewNested.setAdapter(adapter);
        }catch (Exception e){
            Log.e(variable.TRY_CATCH_ERROR + " onBindViewHolder", e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

}
