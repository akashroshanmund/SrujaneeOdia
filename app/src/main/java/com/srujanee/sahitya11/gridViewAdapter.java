package com.srujanee.sahitya11;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class gridViewAdapter extends BaseAdapter {
    private Context mContext;
    boolean flag;
    int postGridCode;
    int editGridViewCode;
    static View selectedView = null;
    static int selectedPositionSubCategory ;
    static View selectedViewSubCategory = null;
    static int selectedPosition ;
    ArrayList<String> postList = new ArrayList<String>();
    ArrayList<String> postListSubCategory = new ArrayList<String>();

    // Constructor
    public gridViewAdapter(Context c, boolean flag, int code) {
        mContext = c;
        this.flag = flag;
        this.editGridViewCode = code;

    }

    public gridViewAdapter(Context context, ArrayList<String> postList, int gridCode, int selectedCategory, int selectedSubCategory) {
        this.mContext = context;
        this.postGridCode = gridCode;
        if (postGridCode == variable.ADAPTER_CODE_SUBCATEGORY_LIST) {
            this.postListSubCategory = postList;
        } else if (postGridCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
            this.postList = postList;
        }
        this.selectedPositionSubCategory = selectedSubCategory;
        this.selectedPosition = selectedCategory;
    }


    public int getCount() {
        if (postGridCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
            return postList.size();
        } else if (postGridCode == variable.ADAPTER_CODE_SUBCATEGORY_LIST) {
            return postListSubCategory.size();
        } else {
            if (editGridViewCode == variable.STYLE_TEXT_BACKGROUND) {
                return variable.drawableImageCode.length;
            } else if(editGridViewCode == variable.STYLE_TEXT_SIZE) {
               return 10;
            }else if(editGridViewCode == variable.STYLE_TEXT_COLOUR)
            {
                return variable.textColour.length;
            }else if(editGridViewCode == variable.STYLE_TEXT_POSITION){
                return variable.drawableImageCode.length;
            }
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

              String displayBuff = postList.get(position);
             displayBuff= displayBuff.substring(0,1).toUpperCase() + displayBuff.substring(1);
            textView.setText(displayBuff);


            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(selectedView!= null) {
                        selectedView.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                    }
                    selectedPosition = position;
                    selectedView = view;
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.orange, null));
                    DBHelper myDb = new DBHelper(mContext);
                    postActivity.subCategoryList = myDb.getSubCategoryNames(textView.getText().toString().toLowerCase());
                    postActivity.adapterSubCategory = new gridViewAdapter(mContext,postActivity.subCategoryList,variable.ADAPTER_CODE_SUBCATEGORY_LIST, selectedPosition, selectedPositionSubCategory);
                    postActivity.subCategoryGrid.setAdapter(postActivity.adapterSubCategory);

                }
            });
            return textView;
        } else if (postGridCode == variable.ADAPTER_CODE_SUBCATEGORY_LIST) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(mContext);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setPadding(2, 10, 2, 10);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                textView.setTextSize(15);

            } else {
                textView = (TextView) convertView;
            }

            String displayBuff = postListSubCategory.get(position);
            displayBuff= displayBuff.substring(0,1).toUpperCase() + displayBuff.substring(1);
            textView.setText(displayBuff);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(selectedViewSubCategory != null) {
                        selectedViewSubCategory.setBackgroundColor(mContext.getResources().getColor(R.color.white, null));
                    }
                    selectedPositionSubCategory = position;
                    selectedViewSubCategory = view;
                    view.setBackgroundColor(mContext.getResources().getColor(R.color.orange, null));
                }
            });
          return textView;
        } else {
            ImageView imageView;
            if (convertView == null) {

                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(300, 277));
                imageView.setPadding(23, 23, 23, 0);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                imageView = (ImageView) convertView;
            }
            if (editGridViewCode == variable.STYLE_TEXT_BACKGROUND) {
                imageView.setImageResource(variable.drawableImageCode[position]);
            } else if(editGridViewCode == variable.STYLE_TEXT_SIZE) {
                imageView.setImageResource(R.drawable.text_size);

            }else if(editGridViewCode == variable.STYLE_TEXT_COLOUR)
            {
                //imageView.setBackgroundColor(mContext.getResources().getColor(variable.textColourCode[position],null));
                imageView.setBackgroundColor(Color.parseColor("#"+variable.textColourCode[position]));
            }else if(editGridViewCode == variable.STYLE_TEXT_POSITION){
                imageView.setImageResource(variable.drawableImageCode[position]);
            }
            return imageView;
        }

    }
    public View getCategorySelected()
    {
        return selectedView;
    }
    public View getSubCategorySelected()
    {
        return selectedViewSubCategory;
    }


    // Keep all Images in array

}
