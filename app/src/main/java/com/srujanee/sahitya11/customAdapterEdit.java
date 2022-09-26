package com.srujanee.sahitya11;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class customAdapterEdit extends RecyclerView.Adapter<customAdapterEdit.myViewHolderEdit>{
    Context context;
    Activity activity;
    int editRecyclerViewCode = variable.DEFAULT;

    ArrayList<String> imageUriOrSubFolderName; // if adaptercode == ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS it shold store the sub folder names
                                               // other wise it will store image uri for images

    public customAdapterEdit(Context context, int code, ArrayList<String> imageSubFolderName){
        this.imageUriOrSubFolderName = new ArrayList<String>();
        this.context = context;
        this.editRecyclerViewCode = code;
        this.imageUriOrSubFolderName = imageSubFolderName;
        Log.i("codee", "customAdapterEdit: "+editRecyclerViewCode);
    }

    /* constructor other than image background codes */
    public customAdapterEdit(Context context, int code){
        this.context = context;
        this.editRecyclerViewCode = code;
        Log.i("codee", "customAdapterEdit: "+editRecyclerViewCode);
    }
    public static class myViewHolderEdit extends RecyclerView.ViewHolder{
        ImageView editImageRecycler;
        TextView editTvSubFolderName;
        CardView editCardRecycler;
        public myViewHolderEdit(@NonNull View itemView) {
            super(itemView);
            editImageRecycler = itemView.findViewById(R.id.editRecyclerImage);
            editCardRecycler = itemView.findViewById(R.id.editRecyclerCard);
            editTvSubFolderName = itemView.findViewById(R.id.imageSubFolerName);
        }
    }
    @NonNull
    @Override
    public myViewHolderEdit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

            if(editRecyclerViewCode == variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.textview_image_subfolder, parent, false);
                view.setOnClickListener(TextEdit.myOnClickListenerImageSubCategory);
                Log.i("sssss", "onCreateViewHolder: ");
            }
            else {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_recycler_card, parent, false);
                view.setOnClickListener(TextEdit.myOnClickListener); Log.i("uuuuuu", "onCreateViewHolder: ");
            }
        myViewHolderEdit viewHolderEdit = new myViewHolderEdit(view);
        return viewHolderEdit;
    }

    @Override
    public void onBindViewHolder(@NonNull customAdapterEdit.myViewHolderEdit holder, int position) {
        try{
        ImageView editRecyclerImage = holder.editImageRecycler;


        if (editRecyclerViewCode == variable.STYLE_TEXT_BACKGROUND) {
          /* Picasso.get().load(imageUriOrSubFolderName.get(position))
                    .fit()
                    .into(editRecyclerImage);*/
            //editRecyclerImage.setImageResource(variable.drawableImageCode[position]);
            editRecyclerImage.setImageURI(Uri.parse(imageUriOrSubFolderName.get(position)));
            Log.i("imageOption111", "onBindViewHolder: ");
        } else if (editRecyclerViewCode == variable.STYLE_TEXT_SIZE) {
            editRecyclerImage.setImageResource(variable.textSizeImage[position]);

        } else if (editRecyclerViewCode == variable.STYLE_TEXT_COLOUR) {
            //editRecyclerImage.setBackgroundColor(context.getResources().getColor(variable.textColour[position], null));
            editRecyclerImage.setImageResource(variable.textColour[position]);
        } else if (editRecyclerViewCode == variable.STYLE_TEXT_POSITION) {
            editRecyclerImage.setImageResource(variable.textAlignment[position]);
        } else if (editRecyclerViewCode == variable.STYLE_TEXT_BACKGROUND_DEFAULT) {

        } else if (editRecyclerViewCode == variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS) {
            TextView tvImageSubFolderName = holder.editTvSubFolderName;
            tvImageSubFolderName.setText(imageUriOrSubFolderName.get(position).substring(0,1).toUpperCase()+imageUriOrSubFolderName.get(position).substring(1));
            Log.i("imageOption", "onBindViewHolder: ");
        } else if (editRecyclerViewCode == variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER) {
            Log.i("ssssssss", "onBindViewHolder: " + imageUriOrSubFolderName.get(position));
            editRecyclerImage.setImageURI(Uri.parse(imageUriOrSubFolderName.get(position)));
           /*Picasso.get().load(Uri.parse(imageUriOrSubFolderName.get(position)))
                    .centerCrop()
                    .transform(new CircleTransform(150,0))
                    .fit()
                    .into(editRecyclerImage);*/
        }
    }catch(Exception e)
      {
          Log.e(variable.TRY_CATCH_ERROR+" onBindViewHolder", e.getMessage() );
      }

    }

    @Override
    public int getItemCount() {
        if (editRecyclerViewCode == variable.STYLE_TEXT_BACKGROUND) {

            //return variable.drawableImageCode.length;
           //return  imageUriOrSubFolderName.size();
            return imageUriOrSubFolderName.size();
        } else if(editRecyclerViewCode == variable.STYLE_TEXT_SIZE) {
            return variable.textSizeImage.length;
        }else if(editRecyclerViewCode == variable.STYLE_TEXT_COLOUR)
        {
            return variable.textColour.length;
        }else if(editRecyclerViewCode == variable.STYLE_TEXT_POSITION){
            return variable.textAlignment.length;
        }
        else if(editRecyclerViewCode == variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS){
            return imageUriOrSubFolderName.size();
        }else if(editRecyclerViewCode == variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER){
            return imageUriOrSubFolderName.size();
        }
        return 0;
    }
}
