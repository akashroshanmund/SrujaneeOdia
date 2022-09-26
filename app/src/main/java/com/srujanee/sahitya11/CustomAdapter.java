package com.srujanee.sahitya11;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {


    static String CategoryName;
    private ArrayList<DataModel> dataSet;
    private ArrayList<draftModel> dataSetDraft;
    private HashMap<String, shortPostDetails> shortDetails;
    static Context context;
    static int adapterCode;
    //RecyclerView recyclerView;
    static Activity activity;
    DBHelper myDb;
    final String Srujanee_Logo_Link = "https://firebasestorage.googleapis.com/v0/b/srujanee-b0e3f.appspot.com/o/metadata%2Fsrujanee_app_logo-playstore-min.png?alt=media&token=dbe43705-d87c-41a9-a376-60af12afdcc4";

    //generic constructor
    public CustomAdapter(ArrayList<DataModel> data, Context context, int adapterCode) {
        this.dataSet = data;
        this.context = context;
        this.activity = (Activity) context;
        this.adapterCode = adapterCode;
        this.myDb = new DBHelper(context);
    }

    public CustomAdapter(ArrayList<DataModel> data, HashMap<String, shortPostDetails> shortDetails, Context context, int adapterCode) {
        this.dataSet = data;
        this.context = context;
        this.activity = (Activity) context;
        this.adapterCode = adapterCode;
        this.myDb = new DBHelper(context);
        this.shortDetails = shortDetails;
    }

    public CustomAdapter(ArrayList<draftModel> draftData, int adapterCode, Context context) {
        this.dataSetDraft = draftData;
        this.context = context;
        this.adapterCode = adapterCode;
        this.activity = (Activity) context;
        this.myDb = new DBHelper(context);
    }

    void shortLink() {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/"))
                .setDomainUriPrefix("https://example.page.link")
                // Set parameters
                // ...
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

    public void getShortLinkUriForShort(int listPosition, final Uri imageUri) {

        String descriptionToSend = dataSet.get(listPosition).getContent();
        if(descriptionToSend.length() > 200 ){
            descriptionToSend = dataSet.get(listPosition).getContent().substring(0,200);
        }

        Uri myUrlShort = Uri.parse(Srujanee_Logo_Link);

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.srujani1.com/" + dataSet.get(listPosition).getContentId() + "/" + variable.TRIGGERED_FROM_DYNAMIC_LINK + "/"))
                .setDomainUriPrefix("https://srujani1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(dataSet.get(listPosition).getContentTitle())
                                .setDescription(descriptionToSend)
                                .setImageUrl(myUrlShort)
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
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "To stay updated download Srujanee: Odia Literature Hub" + "\n" + shortLink);
                            sendIntent.setType("text/plain");
                            sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
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

    public void getShortLinkUriForLong(int listPosition) {
        String descriptionToSend = dataSet.get(listPosition).getContent();
        if(descriptionToSend.length() > 200 ){
            descriptionToSend = dataSet.get(listPosition).getContent().substring(0,200);
        }
        Uri myUrlLong = Uri.parse(Srujanee_Logo_Link);
        Log.i("teste", "getShortLinkUriForLong: click test");
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.srujani1.com/" + dataSet.get(listPosition).getContentId() + "/" + variable.TRIGGERED_FROM_DYNAMIC_LINK + "/"))
                .setDomainUriPrefix("https://srujani1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(dataSet.get(listPosition).getContentTitle())
                                .setImageUrl(myUrlLong)
                                .setDescription(descriptionToSend)
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
                            sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink + "");
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            context.startActivity(shareIntent);
                        } else {
                            // Error
                            // ...
                        }
                    }
                });
    }

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
         //deprecated -  String root = Environment.getExternalStorageDirectory().toString();
        //File myDir = new File(root + "/Srujanee");
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
        return Uri.parse(root + "/Srujanee/" + fname);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //general view for content display
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewDescriptionShortPost;
        TextView textViewAuthor;
        CardView cardView;
        TextView reactionCount;
        TextView reaction;
        TextView editorChoice;
        static ImageView imgCardThreeDot;

        LinearLayout reactionContainer;

        //for short writing specifit
        ImageView shortWritingImage;
        View shortPostImageContainer;
        RelativeLayout relativeContainerShortPost;
        ProgressBar cardProgressbar;

        //interactive views
        TextView cardBookmark;
        TextView cardShare;
        TextView cardShareCount;
        TextView cardFollow;

        //for category list
        ImageView innerImageView;
        TextView innerImageViewUnderLine;
        TextView innerHeader;
        TextView outerTopic;
        TextView trendingContent;
        LinearLayout innerCardViewParent;

        //for view content activity
     /*   TextView viewTitle;
        TextView viewContent;
        TextView viewAuthor;
        TextView viewReactionCount;
        TextView viewReaction;
        TextView viewDate;
        TextView viewSubCategory;
        TextView viewFollow;*/

        RecyclerView recyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            try{

            if (adapterCode == variable.ADAPTER_CODE_CATEGORY_LIST || adapterCode == variable.ADAPTER_CODE_USRE_PROFILE) {
                this.innerImageView = (ImageView) itemView.findViewById(R.id.innerImageView);
                this.innerImageViewUnderLine = (TextView) itemView.findViewById(R.id.innerImageViewUnderLine);
                this.innerHeader = (TextView) itemView.findViewById(R.id.innerHeader);
                this.outerTopic = (TextView) itemView.findViewById(R.id.innerTopicOuter);
                this.trendingContent = (TextView) itemView.findViewById(R.id.innerTrendingContent);
                this.innerCardViewParent = (LinearLayout) itemView.findViewById(R.id.innerCardViewLinearLayout);

            }
             /*else if(adapterCode == variable.ADAPTER_CODE_VIEW_CONTENT)
            {
               this.viewTitle = (TextView)itemView.findViewById(R.id.viewTitle);
                this.viewAuthor = (TextView)itemView.findViewById(R.id.viewAuthor);
                this.viewSubCategory = (TextView)itemView.findViewById(R.id.viewSubCategory);
                this.viewDate = (TextView)itemView.findViewById(R.id.viewDate);
                this.viewContent = (TextView)itemView.findViewById(R.id.viewContent);
                this.viewReaction = (TextView)itemView.findViewById(R.id.viewReaction);
                this.viewFollow = (TextView)itemView.findViewById(R.id.viewFollowAuthor);
                this.viewReactionCount = (TextView)itemView.findViewById(R.id.viewReactionCount);

            }*/
            else {
                this.editorChoice = itemView.findViewById(R.id.cardEditorChoice);
                this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                this.textViewDescription = (TextView) itemView.findViewById(R.id.textViewContent);
                this.textViewDescriptionShortPost = (TextView) itemView.findViewById(R.id.textViewDescriptionShortPost);
                this.textViewAuthor = (TextView) itemView.findViewById(R.id.textViewAuthor);
                this.cardView = (CardView) itemView.findViewById(R.id.card_view);
                this.reactionCount = (TextView) itemView.findViewById(R.id.reactionCount);
                this.shortWritingImage = (ImageView) itemView.findViewById(R.id.shortWritingImage);
                recyclerView = (RecyclerView) itemView.findViewById(R.id.my_recycler_view);
                this.cardView = (CardView) itemView.findViewById(R.id.card_view);
                this.reaction = (TextView) itemView.findViewById(R.id.reaction);
                this.reactionContainer = (LinearLayout) itemView.findViewById(R.id.cardLinearReactionContainer);
                this.imgCardThreeDot = (ImageView) itemView.findViewById(R.id.imgCardThreeDotOption);

                //interactive view
                this.cardBookmark = (TextView) itemView.findViewById(R.id.cardBookMark);
                this.cardShare = (TextView) itemView.findViewById(R.id.cardShare);
                this.cardShareCount = (TextView) itemView.findViewById(R.id.cardShareCount);
                this.cardFollow = (TextView) itemView.findViewById(R.id.cardFollow);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if ((adapterCode == variable.ADAPTER_CODE_SHORT_WRITING)) {
                    // textViewDescription.setHeight(1000);
                    // textViewDescription.setMinHeight(100);
                    // textViewDescription.setMaxHeight(350);
                    // textViewDescription.setMaxLines(10);
                    // textViewDescription.getLayoutParams().height = 350;
                    textViewDescription.setGravity(Gravity.CENTER_VERTICAL);
                    shortWritingImage.setVisibility(View.VISIBLE);
                    textViewDescriptionShortPost.setVisibility(View.VISIBLE);
                    params.setMargins(14, 20, 14, 5);
                    cardView.setLayoutParams(params);
                    cardView.setRadius(10);
                    cardView.setElevation(50);


                    shortPostImageContainer = (View) itemView.findViewById(R.id.shortPostImageContainer);
                    cardProgressbar = (ProgressBar) itemView.findViewById(R.id.cardProgressBar);
                    relativeContainerShortPost = itemView.findViewById(R.id.shortPostImageContainer);


                } else if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {
                    textViewDescription.setMaxLines(1);
                    params.setMargins(2, 5, 2, 5);
                    textViewDescription.getLayoutParams().height = 130;
                    textViewDescription.setMaxLines(1);
                    reaction.getLayoutParams().height = 60;
                    cardBookmark.getLayoutParams().height = 60;
                    cardShare.getLayoutParams().height = 60;
                    textViewTitle.setMaxLines(1);
                    cardView.setLayoutParams(params);
                    cardView.setRadius(15);
                    cardView.setElevation(30);
                    reactionContainer.setOrientation(LinearLayout.HORIZONTAL);
                    this.cardFollow.setVisibility(View.GONE);
                    LinearLayout.LayoutParams relativeTextParam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 100);
                    relativeContainerShortPost = itemView.findViewById(R.id.shortPostImageContainer);
                    relativeContainerShortPost.setLayoutParams(relativeTextParam);
                } else if (adapterCode == variable.TRIGGERED_FROM_DRAFT) {
                    textViewDescription.setMaxLines(1);
                    params.setMargins(2, 5, 2, 5);
                    textViewDescription.setHeight(40);
                    textViewTitle.setMaxLines(1);
                    cardView.setLayoutParams(params);
                    cardView.setRadius(15);
                    cardView.setElevation(25);
                    cardView.getLayoutParams().height = 350;
                    reactionContainer.setOrientation(LinearLayout.HORIZONTAL);
                    this.cardFollow.setVisibility(View.GONE);
                    reactionContainer.setVisibility(View.GONE);
                }
                //this code can be used in future
            /*    else if(adapterCode == variable.ADAPTER_CODE_VIEW_CONTENT){
                    RelativeLayout.LayoutParams paramTextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                     textViewTitle.setTextAppearance(R.style.TextAppearance_AppCompat_Caption);
                     textViewTitle.setTextSize(30);
                     textViewAuthor.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                     textViewAuthor.setTextSize(15);
                     textViewDescription.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);

                     textViewDescription.setTextSize(20);
                     textViewDescription.setPadding(0,13,0,18);
                     paramTextView.setMargins(0,15,0,400);
                     textViewDescription.setLayoutParams(paramTextView);

                }*/
            }
        }catch(Exception e)
         {
             Log.e(variable.TRY_CATCH_ERROR+" MyViewHolder", e.getMessage());
         }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = null;
            int x;
            if (adapterCode == variable.ADAPTER_CODE_CATEGORY_LIST) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.inner_card_view, parent, false);
                view.setOnClickListener(MainActivity.myOnClickListener);
            } else if (adapterCode == variable.ADAPTER_CODE_USRE_PROFILE) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.inner_card_view, parent, false);
                view.setOnClickListener(userProfile.myOnClickListener);
                view.setOnLongClickListener(userProfile.myOnLongClickListener);
            } else if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING || adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cards_layout, parent, false);
                view.setOnClickListener(topicList.myOnClickListener);
            } else if (adapterCode == variable.ADAPTER_CODE_VIEW_CONTENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cards_layout, parent, false);
                // view.setOnClickListener(topicList.myOnClickListener);
            } else if (adapterCode == variable.TRIGGERED_FROM_DRAFT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cards_layout, parent, false);
                view.setOnClickListener(topicList.myOnClickListener);
                view.setOnLongClickListener(topicList.myOnLongClickListener);
            }

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
      try{

      /*  if(dataSet.get(listPosition).getPostType() == variable.ADAPTER_CODE_TREND){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView textViewDescription = (TextView) holder.textViewDescription;
            CardView cardView = (CardView) holder.cardView;
            ImageView shortWritingImage = (ImageView) holder.shortWritingImage;
            textViewDescription.setMinHeight(100);
            textViewDescription.setMaxHeight(1000);
            textViewDescription.setGravity(Gravity.CENTER_VERTICAL);
            textViewDescription.setMaxLines(10);
            shortWritingImage.setVisibility(View.VISIBLE);
            params.setMargins(14,20,14,5);
            cardView.setLayoutParams(params);
            cardView.setRadius(20);
            cardView.setElevation(30);
        }*/


        if (adapterCode == variable.ADAPTER_CODE_CATEGORY_LIST || adapterCode == variable.ADAPTER_CODE_USRE_PROFILE) {
            Log.i("traceBookmark", "short categoty list " + dataSet.size());
            ImageView imageView = (ImageView) holder.innerImageView;
            TextView imageViewUnderLine = (TextView) holder. innerImageViewUnderLine;
            TextView header = (TextView) holder.innerHeader;
            TextView tempOuterTopic = (TextView) holder.outerTopic;
            TextView trendingContent = (TextView) holder.trendingContent;
            LinearLayout innerCardViewParent = (LinearLayout) holder.innerCardViewParent;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(20,20,20,20);

            trendingContent.setMaxLines(6);
            trendingContent.setPadding(25, 5, 5, 5);

            if (dataSet.size() > 0 && dataSet.get(listPosition).getOuterTopic().equals("Trending")) {
                header.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imageViewUnderLine.setVisibility(View.GONE);
                trendingContent.setVisibility(View.VISIBLE);
                innerCardViewParent.setBackground(context.getDrawable(R.drawable.rectangle_shape_for_trending));
                innerCardViewParent.setLayoutParams(params);
                header.setTypeface(Typeface.DEFAULT_BOLD);
                trendingContent.setText(dataSet.get(listPosition).getTrendingContent());
                String postTitle = (dataSet.get(listPosition).getTrendingTitle());
                String postAuthor = dataSet.get(listPosition).getTrendingAuthorName();
                //String titleData = "<p style = \"font-size:100px\">"+postTitle+"</p>"+" -"+"<I>"+postAuthor+"</I>";
                String titleData = "<big>" + postTitle + "</big>" + "&nbsp;&nbsp; -" + "<I>" + postAuthor + "</I>";
                //<big><b>Xyzd</b></big><small>.com</small>"
                header.setText(Html.fromHtml(titleData));

            } else if (dataSet.size() > 0 && dataSet.get(listPosition).getOuterTopic().equals("Followed")) {
                imageView.setVisibility(View.GONE);
                header.setVisibility(View.VISIBLE);
                imageViewUnderLine.setVisibility(View.GONE);
                trendingContent.setVisibility(View.VISIBLE);
                innerCardViewParent.setBackground(context.getDrawable(R.drawable.rectangle_shape_for_trending));
                innerCardViewParent.setLayoutParams(params);
                header.setTypeface(Typeface.DEFAULT_BOLD);
                trendingContent.setText(dataSet.get(listPosition).getTrendingContent());
                String postTitle = (dataSet.get(listPosition).getTrendingTitle());
                /* truncate the string */
                if (postTitle.length() > 18) {
                    postTitle = postTitle.substring(0, 15) + "...";
                }
                String postAuthor = dataSet.get(listPosition).getTrendingAuthorName();
                //String titleData = "<p style = \"font-size:100px\">"+postTitle+"</p>"+" -"+"<I>"+postAuthor+"</I>";
                String titleData = "<big>" + postAuthor + "</big>" + "&nbsp;&nbsp;" + "<I><br>" + postTitle + "</I>";
                //<big><b>Xyzd</b></big><small>.com</small>"
                LinearLayout.LayoutParams paramsLocal = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsLocal.setMargins(0, 0, 0, 0);
                paramsLocal.gravity = Gravity.CENTER;
                header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                header.setLayoutParams(paramsLocal);

                header.setText(Html.fromHtml(titleData));
            } else if (dataSet.get(listPosition).getOuterTopic().equals("profileLong") ||
                    dataSet.get(listPosition).getOuterTopic().equals("profileShort")) {
                header.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imageViewUnderLine.setVisibility(View.GONE);
                innerCardViewParent.setBackground(context.getDrawable(R.drawable.rectangle_shape_for_trending));
                innerCardViewParent.setLayoutParams(params);
                trendingContent.setVisibility(View.VISIBLE);
                header.setTypeface(Typeface.DEFAULT_BOLD);
                trendingContent.setText(dataSet.get(listPosition).getTrendingContent());
                header.setText((dataSet.get(listPosition).getTrendingTitle()));
            } else if ((dataSet.get(listPosition).getOuterTopic().toLowerCase()).equals(variable.LONG_POST.toLowerCase()) ||
                    (dataSet.get(listPosition).getOuterTopic().toLowerCase()).equals(variable.SHORT_POST.toLowerCase())) {
                int imageIndex = Integer.valueOf(dataSet.get(listPosition).imageCode);
                // Picasso.get().load(context.getResources().getDrawable(variable.drawableImageCode[imageIndex],null)).into();
                imageView.setImageDrawable(context.getResources().getDrawable(variable.drawableImageCode[imageIndex], null));
                header.setText((dataSet.get(listPosition).getInnerTopic()).toUpperCase());
            } else if((dataSet.get(listPosition).getOuterTopic().toLowerCase()).equals(variable.FUN_POST.toLowerCase()) ||
                    (dataSet.get(listPosition).getOuterTopic().toLowerCase()).equals(variable.GENERAL_POST.toLowerCase())){
                Uri imgUri=Uri.parse(dataSet.get(listPosition).getUriOfImage());
                imageView.setImageURI(imgUri);

            }
            else{/* dynamic scroll view set */
                imageView.setVisibility(View.GONE);
                imageViewUnderLine.setVisibility(View.GONE);
                trendingContent.setVisibility(View.VISIBLE);
                header.setTypeface(Typeface.DEFAULT_BOLD);
                trendingContent.setText(dataSet.get(listPosition).getTrendingContent());
                String postTitle = (dataSet.get(listPosition).getTrendingTitle());
                String postAuthor = dataSet.get(listPosition).getTrendingAuthorName();
                //String titleData = "<p style = \"font-size:100px\">"+postTitle+"</p>"+" -"+"<I>"+postAuthor+"</I>";
                String titleData = "<big>" + postTitle + "</big>" + "&nbsp;&nbsp; -" + "<I>" + postAuthor + "</I>";
                //<big><b>Xyzd</b></big><small>.com</small>"
                header.setText(Html.fromHtml(titleData));
            }
            tempOuterTopic.setText(dataSet.get(listPosition).getOuterTopic());
             Log.i("checkOuterTopic", "onBindViewHolder: "+dataSet.get(listPosition).outerTopic.toUpperCase());
        }
         /* else if(adapterCode == variable.ADAPTER_CODE_VIEW_CONTENT){
            TextView viewTitle = holder.viewTitle;
             TextView viewContent = holder.viewContent;
             TextView viewAuthor = holder.viewAuthor;
             TextView viewReactionCount = holder.viewReactionCount;
             TextView viewReaction = holder.viewReaction;
             TextView viewDate = holder.viewDate;
             TextView viewSubCategory = holder.viewSubCategory;
             TextView viewFollow = holder.viewFollow;
         }*/
        else if (adapterCode == variable.TRIGGERED_FROM_DRAFT) {
            TextView textViewTitle = holder.textViewTitle;
            TextView textViewDescription = holder.textViewDescription;
            textViewTitle.setText(dataSetDraft.get(listPosition).getDraftTitle());
            textViewDescription.setText(dataSetDraft.get(listPosition).getDraftContent());

        } else if (adapterCode == variable.ADAPTER_CODE_SHORT_WRITING || adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {

            //  Log.i("postCount", "onBindViewHolder: "+dataSet.size()+" "+shortDetails.size());

            TextView textViewTitle = holder.textViewTitle;
            TextView textViewDescription = holder.textViewDescription;
            TextView textViewAuthor = holder.textViewAuthor;
            TextView reactionCount = holder.reactionCount;
            TextView reaction = holder.reaction;
            TextView editorChoice = holder.editorChoice;
            final ImageView imgCardThreeDot = holder.imgCardThreeDot;
            View shortPostImageContainer = holder.shortPostImageContainer;
            final ImageView shortImage = holder.shortWritingImage;
            TextView textViewDescriptionShortPost = holder.textViewDescriptionShortPost;

            //Interactive views
            final TextView cardBookmark = holder.cardBookmark;
            TextView cardShare = holder.cardShare;
            TextView cardShareCount = holder.cardShareCount;
            final TextView cardFollow = holder.cardFollow;

            if (myDb.isPositiveRelation(dataSet.get(listPosition).getContentId(), variable.POST_LIKED_TABLE_NAME, variable.POST_LIKED_COLUMN_NAME)) {
                reaction.setBackground(context.getResources().getDrawable(R.drawable.reaction_pink, null));
            }
            if (dataSet.get(listPosition).getInputDataEditorChoice() == variable.INPUT_VALUE_ONE) {
                editorChoice.setVisibility(View.VISIBLE);
                Log.i("editorChoice", "onBindViewHolder: ");
            }
            Date postDate = new Date(dataSet.get(listPosition).InputDataDate);
            String details = "Author: " + dataSet.get(listPosition).getContentAuthor() + "  Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(postDate);
            textViewAuthor.setText(details);
            textViewTitle.setText(dataSet.get(listPosition).getContentTitle());
            textViewDescription.setText(dataSet.get(listPosition).getContent());
            reactionCount.setText(dataSet.get(listPosition).getReactionCount() + "");

            /*Common onClick Listeners*/
            cardBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("bookMark", "onClick: ");
                    String id = dataSet.get(listPosition).getContentId();
                    if (myDb.isPositiveRelation(id, variable.bookmark_TABLE_NAME, variable.bookmark_COLUMN_ID)) {
                        myDb.deleteBookmark(id);
                        view.setBackground(context.getResources().getDrawable(R.drawable.bookmark__black_border_24, null));
                    } else {
                        myDb.insertBookMark(id);
                        view.setBackground(context.getResources().getDrawable(R.drawable.bookmark_bluefill, null));
                    }
                }
            });
            /* get extra final variable to be used inside the click listener function */
            final TextView reactionDynamic = reaction;
            final TextView reactionCountDynamic = reactionCount;
            reaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = dataSet.get(listPosition).InputDataId;
                    if(!myDb.isConnectedToInternet()){
                        Toast.makeText(context,"Please connect to internet...",Toast.LENGTH_LONG);
                    }
                    else if (variable.USER_ID.equals(variable.STRING_DEFAULT)) {
                        Toast.makeText(context,"Please Sign In to like the post",Toast.LENGTH_LONG);
                    } else {
                        if (myDb.isPositiveRelation(id, variable.POST_LIKED_TABLE_NAME, variable.POST_LIKED_COLUMN_NAME)) {
                            reactionDynamic.setBackgroundResource(R.drawable.reaction);
                            myDb.updateContentReaction(id, 0);
                            reactionCountDynamic.setText(myDb.getReactionCount(id) + "");
                            reactionDynamic.setFreezesText(true);
                        }
                        //disliked
                        else {
                            reactionDynamic.setBackgroundResource(R.drawable.reaction_pink);
                            myDb.updateContentReaction(id, 1);
                            reactionCountDynamic.setText(myDb.getReactionCount(id) + "");
                            reactionDynamic.setFreezesText(false);
                        }
                        myDb.updatePostLiked(id);
                    }
                }
            });


            cardShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /* If short post then download the image */
                    if (adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {
                        Uri uriOfSavedImage = setViewToBitmapImage(holder.shortPostImageContainer);
                        getShortLinkUriForShort(listPosition, uriOfSavedImage);
                    } else {
                        getShortLinkUriForLong(listPosition);
                    }


                    // DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()

                    // .buildDynamicLink();
                    //  Uri dynamicLinkUri = dynamicLink.getUri();

               /*      Log.i("link", "onClick: "+dynamicLinkUri);
                     Intent sendIntent = new Intent();
                     sendIntent.setAction(Intent.ACTION_SEND);
                     sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri+"");
                     sendIntent.setType("text/plain");

                     Intent shareIntent = Intent.createChooser(sendIntent, null);
                     context.startActivity(shareIntent);*/

                }
            });

            cardFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /* if already following unfollow else follow*/
                    if (myDb.isPositiveRelation(variable.USER_ID, variable.FOLLOWING_TABLE_NAME, variable.FOLLOWING_COLUMN_NAME)) {
                        view.setBackgroundResource(R.drawable.follow_black);
                        // viewReactionCount.setText(viewMyDb.getReactionCount(id)+"");
                        // viewFollow.setFreezesText(true);
                    } else {
                        view.setBackgroundResource(R.drawable.follow_orange);
                        //viewReactionCount.setText(viewMyDb.getReactionCount(id)+"");
                        //viewFollow.setFreezesText(false);
                    }
                    myDb.updateFollowing(dataSet.get(listPosition).getContentAuthor());
                }
            });
            /* ---------------------------------------------------------------------------------------------------------------------*/
            if (adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {

                String contentLocalFormatted = (dataSet.get(listPosition).getContent()).replace("\n", "<br>");
                String contentLocalShort = "<big>" + dataSet.get(listPosition).getContentTitle() + "</big>" + "&nbsp;&nbsp;" + "<br><br>" + contentLocalFormatted + "<small><br>@" + dataSet.get(listPosition).InputDataAuthor + "</small>";
                textViewDescription.setText(Html.fromHtml(contentLocalShort));
                textViewDescription.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
                ProgressBar progressBarLocal = holder.cardProgressbar;
                textViewTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!myDb.isConnectedToInternet()) {
                            Toast.makeText(context, "Please connect to internet...", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(context, userProfile.class);
                            intent.putExtra("userId", dataSet.get(listPosition).getContentAuthor());
                            context.startActivity(intent);
                        }
                    }
                });

                shortPostDetails shortPostIndexLocal = shortDetails.get(dataSet.get(listPosition).getContentId());
                Log.i("ListSize", shortDetails.size() + "");
                textViewDescriptionShortPost.setText(shortPostIndexLocal.getShortDescription());
                if(shortPostIndexLocal.getShortDescription().equals("")) {
                   textViewDescriptionShortPost.setVisibility(View.GONE);
                }
                Log.i("resource id", shortPostIndexLocal.getTextColourIndex()+"");
             //   textViewDescription.setTextColor(context.getResources().getColor( (int)shortPostIndexLocal.getTextColourIndex(), null));
                try {
                    textViewDescription.setTextColor(Color.parseColor("#" + shortPostIndexLocal.getTextColourIndex()));
                }catch (Exception e){
                    textViewDescription.setTextColor(Color.parseColor("#" + "000000"));
                    Log.e("TextColorDisPlayError", "CustomAdaper Line 724");

                }

                textViewDescription.setTextSize(variable.textSize[(int) shortPostIndexLocal.getTextSizeIndex()]);

                String detailsForShortPost = "Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(postDate);

                textViewAuthor.setText(detailsForShortPost);
                /* if short post show author's name in place of content title */
                textViewTitle.setText(dataSet.get(listPosition).getContentAuthor());
                // textViewDescription.setBackground(context.getResources().getDrawable(variable.drawableImageCode[temp.getTextImageIndex()],null)
                Log.i("imageUriTest", "onBindViewHolder: " + "imgaeUriToDisplay");

                String imgaeUriToDisplay = myDb.getParticularImageUriByCode(shortPostIndexLocal.getTextImageIndex(), shortImage, progressBarLocal);
                Log.i("imageUri", "onBindViewHolder: " + imgaeUriToDisplay);
                /*Picasso.get().load(imgaeUriToDisplay)
                        .fit()
                        .into(shortImage);*/

                shortImage.setImageURI(Uri.parse(imgaeUriToDisplay));
                //shortImage.setImageResource(variable.drawableImageCode[(int)shortPostIndexLocal.getTextImageIndex()]);
                final int position = (int) shortPostIndexLocal.getTextPositionIndex();
                if (dataSet.get(listPosition).getContentAuthor().equals(variable.USER_ID)) {
                    if (variable.isFromTrending != 1) {
                        imgCardThreeDot.setVisibility(View.VISIBLE);
                    }
                    imgCardThreeDot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("popup", "onClick: ");
                            PopupMenu popupMenu = new PopupMenu(context, imgCardThreeDot);
                            popupMenu.inflate(R.menu.three_dot_main_card);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    int optionId = menuItem.getItemId();
                                /* if (optionId == R.id.cardEditPost) {
                                     Log.i("popup", "onMenuItemClick: " + "edit");
                                 }*/
                                    if (optionId == R.id.cardDeletePost) {
                                        if (!myDb.isConnectedToInternet()) {
                                            Toast.makeText(context, "Please connect to internet...", Toast.LENGTH_LONG).show();
                                        } else {
                                            myDb.deleteShortPost(dataSet.get(listPosition).getContentId(), dataSet.get(listPosition).getContentAuthor());
                                            Log.i("popup", "onMenuItemClick: " + "delete");
                                            Intent intent = new Intent("topicListIntentFilter");
                                            context.sendBroadcast(intent);
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    return false;
                                }
                            });
                            popupMenu.show();
                        }

                    });
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (position == variable.TEXT_CENTRE) {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_CENTER));
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewDescription.setGravity(Gravity.START);
                } else if (position == variable.TEXT_LEFT_TOP) {
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    textViewDescription.setGravity(Gravity.CENTER);
                   /* params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_TEXT_START));*/
                    textViewDescription.setGravity(Gravity.START);
                } else if (position == variable.TEXT_CENTRE_TOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewDescription.setGravity(Gravity.CENTER);
                } else if (position == variable.TEXT_RIGHT_TOP) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    textViewDescription.setGravity(Gravity.END);
                } else if (position == variable.TEXT_LEFT_CENTRE) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    textViewDescription.setGravity(Gravity.START);
                } else if (position == variable.TEXT_RIGHT_CENTRE) {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    textViewDescription.setGravity(Gravity.END);
                } else if (position == variable.TEXT_LEFT_BOTTOM) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    textViewDescription.setGravity(Gravity.START);
                } else if (position == variable.TEXT_CENTRE_BOTTOM) {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewDescription.setGravity(Gravity.CENTER);
                } else if (position == variable.TEXT_RIGHT_BOTTOM) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    textViewDescription.setGravity(Gravity.END);
                } else {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_CENTER));
                    textViewDescription.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textViewDescription.setGravity(Gravity.START);
                }
                textViewDescription.setLayoutParams(params);
            }

        }
    }catch(Exception e)
      {
          Log.e("TryCatchError", "Custom Adapter "+e.getMessage());
      }
    }
    @Override
    public int getItemCount() {
        if(adapterCode == variable.TRIGGERED_FROM_DRAFT)
        {
            return dataSetDraft.size();
        }
        else {
            return dataSet.size();
        }
    }
}
