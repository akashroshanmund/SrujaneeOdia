package com.srujanee.sahitya11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class customAdapterProfile extends  RecyclerView.Adapter<customAdapterProfile.MyViewHolder> {


    ArrayList<profileDataModel> profileList;
    Context context;
    Activity activity;
    static int triggerFrom;

    ArrayList<String> imageUriList;
    public customAdapterProfile( ArrayList<profileDataModel> profileList, Context context,  int triggerFrom){
        this.profileList = profileList;
        this.context = context;
        this.activity = (Activity)context;
        this.triggerFrom = triggerFrom;
    }

    /* to trigger for Thougt of the day */
    public  customAdapterProfile(ArrayList<String> imageUriList, int triggerFrom, Context context){
        this.imageUriList = new ArrayList<String>();
        this.triggerFrom = triggerFrom;
        this.imageUriList = imageUriList;
        this.context = context;
        this.activity = (Activity) context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        /* if trigger from profile */
        TextView tvUserId;

        /* if trigger from TOD */
        ImageView imageViewTOD;
        ImageButton imageButtonShare;
        View todImageContainer;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            if(triggerFrom == variable.TRIGGERED_FROM_USER_PROFILE_DATA) {
                tvUserId = itemView.findViewById(R.id.recyclerTvUserData);
            }
            else if(triggerFrom == variable.TRIGGERED_FROM_TOD){
                imageViewTOD = (ImageView)itemView.findViewById(R.id.todImageView);
                imageButtonShare = (ImageButton)itemView.findViewById(R.id.todShare);
                /* duplicated instance of todImageView to give VIEW input to save image */
                todImageContainer = (View) itemView.findViewById(R.id.todImageView);
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(triggerFrom == variable.TRIGGERED_FROM_USER_PROFILE_DATA) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_data_recycler, parent, false);
            view.setOnClickListener(userProfileData.myOnClickListener);

        }else if( triggerFrom == variable.TRIGGERED_FROM_TOD){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tod_recycler_layout, parent, false);
           // view.setOnClickListener(tod.myOnClickListener);
        }
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if(triggerFrom == variable.TRIGGERED_FROM_USER_PROFILE_DATA) {
            TextView tvProfileUserId = holder.tvUserId;
            tvProfileUserId.setText(profileList.get(position).getProfileUserId());
        }else if( triggerFrom == variable.TRIGGERED_FROM_TOD){
            holder.imageViewTOD.setImageURI(Uri.parse(imageUriList.get(position)));
            holder.imageButtonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uriOfSavedImage = setViewToBitmapImage(holder.todImageContainer);
                    getShortLinkUriForShort(position,uriOfSavedImage);
                }
            });

        }
        }

    @Override
    public int getItemCount() {
       if(triggerFrom == variable.TRIGGERED_FROM_USER_PROFILE_DATA) {
           return profileList.size();
       }else if(triggerFrom == variable.TRIGGERED_FROM_TOD){
           return imageUriList.size();
       }
       return  0;
    }


    /*-----------------------Dynamic link and download imgae-----------------------------------------*/
    public Uri setViewToBitmapImage(View view) {
//Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        Uri uri = SaveImage(returnedBitmap);
        return uri;
    }

    public Uri SaveImage(Bitmap finalBitmap) {
        //deprecated - String root = Environment.getExternalStorageDirectory().toString();
        //File myDir = new File(root+"/Srujanee");
        String root = context.getExternalFilesDir(null).toString();
        File myDir = new File(root, "Srujanee");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Srujanee-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.parse(root+"/Srujanee/"+fname);
    }

    public void getShortLinkUriForShort(int listPosition, final Uri imageUri) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.srujani1.com/" + "TOD" + "/" + variable.TRIGGERED_FROM_TOD + "/"))
                .setDomainUriPrefix("https://srujani1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Thought Of The Day")
                                .setDescription("Join Srujanee to get daily updates")
                                .setImageUrl(imageUri)
                                .build())
                .buildShortDynamicLink()
                //      ---------------
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.i("link", "onClick: " + shortLink + " " + flowchartLink);
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "To stay updated download Srujanee: Odia Literature Hub"+ "\n"+shortLink);
                            sendIntent.setType("text/plain");
                            sendIntent.putExtra(Intent.EXTRA_STREAM,imageUri );
                            sendIntent.setType("image/jpg");
                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }
    /*--------------------------------------------------------------------------------------------------*/


}
