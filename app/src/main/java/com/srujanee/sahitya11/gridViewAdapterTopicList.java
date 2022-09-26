package com.srujanee.sahitya11;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class gridViewAdapterTopicList extends BaseAdapter {
    private Context mContext;
    boolean flag;
    int postGridCode;
    int editGridViewCode;

    ArrayList<String> postList = new ArrayList<String>();
    ArrayList<String> postListSubCategory = new ArrayList<String>();

    // Constructor
    public gridViewAdapterTopicList(Context c, boolean flag, int code) {
        mContext = c;
        this.flag = flag;
        this.editGridViewCode = code;

    }

    public gridViewAdapterTopicList(Context context, ArrayList<String> postList, int gridCode) {
        this.mContext = context;
        this.postGridCode = gridCode;
        if (postGridCode == variable.ADAPTER_CODE_SUBCATEGORY_LIST) {
            this.postListSubCategory = postList;
        } else if (postGridCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
            this.postList = postList;
        }
    }


    public int getCount() {
        if (postGridCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
            return postList.size();
        } else if (postGridCode == variable.ADAPTER_CODE_SUBCATEGORY_LIST) {
            return postListSubCategory.size();
        }
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (postGridCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
            final TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(2, 10, 2, 10);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                textView.setTextSize(15);

            } else {
                textView = (TextView) convertView;
            }


                textView.setText(postList.get(position));


           /* textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(selectedView!= null) {
                        selectedView.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                    }
                    selectedPosition = position;
                    selectedView = view;
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.orange, null));
                    DBHelper myDb = new DBHelper(mContext);
                  //  postActivity.subCategoryList = myDb.getSubCategoryNames(textView.getText().toString());
                  //  postActivity.adapterSubCategory = new gridViewAdapterTopicList(mContext,postActivity.subCategoryList,variable.ADAPTER_CODE_SUBCATEGORY_LIST, selectedPosition, selectedPositionSubCategory);
                 //   postActivity.subCategoryGrid.setAdapter(postActivity.adapterSubCategory);

                }
            });*/
            return textView;
            /* if subcategory list*/
        } else  {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(2, 20, 2, 20);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.orange, null));
                textView.setTextSize(15);
                textView.setHeight(100);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                params.setMargins(22,20,20,20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setLayoutParams(params);

            } else {
                textView = (TextView) convertView;
            }
            textView.setText(postListSubCategory.get(position));
            Log.i("searchGrid", "getView: "+postListSubCategory.get(position));
            /*textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(selectedViewSubCategory != null) {
                        selectedViewSubCategory.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                    }
                    selectedPositionSubCategory = position;
                    selectedViewSubCategory = view;
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.orange, null));
                }
            });*/
            return textView;
        }

    }



    // Keep all Images in array

}
