package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class View_Content extends AppCompatActivity {

    ArrayList<DataModel> data;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    TextView viewBookmark;
    DBHelper viewMyDb;
    Context context;
    TextView cardFollow;
    TextView cardReaction;
    TextView cardReactionCount;
    Activity activity;
    boolean isPostDeleted = false;


    RecyclerView.ViewHolder viewHolder;
    //variable declaration
    String id;
    String title;
    String cont;
    static String author;
    long date;
    String tag;
    long reactionCount;
    int triggeredFrom;
    boolean isEditing = false;

    TextView viewContentTitle;
    TextView viewContentAuthor;
    TextView viewContent;
    TextView viewReaction;
    TextView viewReactionCount;
    TextView viewFollow;
    TextView viewDeletePost;
    TextView viewEditPost;
    EditText etViewContentTitleEdit;
    EditText etViewContentEdit;
    ProgressBar viewProgressBar;
    RelativeLayout viewContentParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__content);

        try{
        context = this;
        activity = (Activity)context;
        viewMyDb = new DBHelper(context);

        //get clicked position from topic list to open that long post
        int IndexNumber = getIntent().getIntExtra("IndexNumber",0);
        triggeredFrom = getIntent().getIntExtra(variable.TRIGGERED_FROM, variable.DEFAULT);

        //views
        viewContentParentLayout = (RelativeLayout) findViewById(R.id.viewContentParentLayout);
       viewBookmark = (TextView)findViewById(R.id.viewBookmark);
       viewContentTitle = (TextView) findViewById(R.id.viewTextViewTitle);
       viewContentAuthor = (TextView) findViewById(R.id.viewTextViewAuthor);
       viewContent = (TextView) findViewById(R.id.viewTextViewContent);
       viewReaction = (TextView) findViewById(R.id.viewReaction);
       viewReactionCount = (TextView) findViewById(R.id.viewReactionCount);
       viewFollow = (TextView) findViewById(R.id.viewFollow);
       viewDeletePost = (TextView) findViewById(R.id.viewDeletePost);
       viewEditPost = (TextView) findViewById(R.id.viewEditPost);
       etViewContentEdit = (EditText) findViewById(R.id.etViewTextViewContentEdit);
       etViewContentTitleEdit = (EditText) findViewById(R.id.etViewContentTitleEdit);
       viewProgressBar = (ProgressBar) findViewById(R.id.viewProgressBar);

        //get details of long post to be displayed
        id = topicList.data.get(IndexNumber).getContentId();
        title = topicList.data.get(IndexNumber).getContentTitle();
        cont =  topicList.data.get(IndexNumber).getContent();
        author = topicList.data.get(IndexNumber).getContentAuthor() ;
        date = topicList.data.get(IndexNumber).getContentDate();
        tag = topicList.data.get(IndexNumber).getContentTag();
        reactionCount = topicList.data.get(IndexNumber).getReactionCount();
        Date postDate = new Date(date);
        String postDetails = author +"    "+new SimpleDateFormat("yyyy-MM-dd HH:mm").format(postDate);;

        /* set the view with values */
        viewContentTitle.setText(title);
        viewContentAuthor.setText(postDetails);
        viewContent.setText(cont);
        viewReactionCount.setText(reactionCount+"");

        /* if posted by the user currently logged in, enable edit and delete option */
        if(author.equals(variable.USER_ID) && triggeredFrom != variable.TRIGGERED_FROM_TREND){
            viewEditPost.setVisibility(View.VISIBLE);
            viewDeletePost.setVisibility(View.VISIBLE);
        }

        /* check if already bookmarked or not. Show the view accordingly */
        if(viewMyDb.isPositiveRelation(id,variable.bookmark_TABLE_NAME,variable.bookmark_COLUMN_ID)) {
            viewBookmark.setBackground(context.getResources().getDrawable(R.drawable.bookmark_bluefill,null));
        }else{
            viewBookmark.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_border_24,null));
        }

        /* check if already liked or not. Show the view accordingly */
        if(viewMyDb.isPositiveRelation(id,variable.POST_LIKED_TABLE_NAME,variable.POST_LIKED_COLUMN_NAME)) {
            viewReaction.setBackground(context.getResources().getDrawable(R.drawable.reaction_pink,null));
        }else{
            viewReaction.setBackground(context.getResources().getDrawable(R.drawable.reaction,null));
        }


       /* if(viewMyDb.isPositiveRelation(author,variable.FOLLOWING_TABLE_NAME,variable.FOLLOWING_COLUMN_NAME)) {
            viewFollow.setBackground(context.getResources().getDrawable(R.drawable.follow_orange,null));
        }else{
            viewFollow.setBackground(context.getResources().getDrawable(R.drawable.follow_black,null));
        }*/
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onCreateViewContent", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(triggeredFrom == variable.TRIGGERED_FROM_TREND){
            topicList.isFromTrending = true;
        }
        if(isPostDeleted == false) {
            variable.flagReturnedFromViewContent = true;
        }
    }

    /* Button to bookmark */
    public void bookmark(View view) {
       if(viewMyDb.isPositiveRelation(id,variable.bookmark_TABLE_NAME,variable.bookmark_COLUMN_ID)) {
           viewMyDb.deleteBookmark(id);
           viewBookmark.setBackground(context.getResources().getDrawable(R.drawable.ic_baseline_bookmark_border_24,null));
           snackBar("Bookmark Removed");
       }else{
           viewMyDb.insertBookMark(id);
           viewBookmark.setBackground(context.getResources().getDrawable(R.drawable.bookmark_bluefill,null));
           snackBar("Bookmarked");
       }
    }

    /* button to like or dislike */
    public void reactionRelation(View view) {
        if(variable.USER_ID.equals(variable.STRING_DEFAULT)){
            snackBar("Please Sign In to like the post");
        }else {
            if (viewMyDb.isPositiveRelation(id, variable.POST_LIKED_TABLE_NAME, variable.POST_LIKED_COLUMN_NAME)) {
                viewReaction.setBackgroundResource(R.drawable.reaction);
                viewMyDb.updateContentReaction(id, 0);
                viewReactionCount.setText(viewMyDb.getReactionCount(id) + "");
                viewReaction.setFreezesText(true);
            } else {

                viewReaction.setBackgroundResource(R.drawable.reaction_pink);
                viewMyDb.updateContentReaction(id, 1);
                viewReactionCount.setText(viewMyDb.getReactionCount(id) + "");
                viewReaction.setFreezesText(false);
            }
            viewMyDb.updatePostLiked(id);
        }
    }

    /* this will redirect to the author's profile
    *  forwarded value: AuthorId
    */
    public void viewOpenProfile(View view) {
        if(!viewMyDb.isConnectedToInternet()){
            snackBar("Please connect to internet...");
        }else {
            Intent intent = new Intent(context, userProfile.class);
            intent.putExtra("userId", author);
            context.startActivity(intent);
        }
    }

    /* for own post, this button deletes the post from server */
    public void deletePost(View view) {
        if(!viewMyDb.isConnectedToInternet()){
            snackBar("Please connect to internet...");
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Delete Post...");
            // builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    deletePost();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void deletePost(){
        if(this.author.equals(variable.USER_ID) && triggeredFrom != variable.TRIGGERED_FROM_TREND) {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            fb.collection(variable.InputData_TABLE_NAME)
                    .whereEqualTo(variable.InputData_COLUMN_ID, id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isComplete()) {
                                FirebaseFirestore fdbUpdate = FirebaseFirestore.getInstance();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    DocumentReference dr = fdbUpdate.collection(variable.InputData_TABLE_NAME).document(documentId);
                                    dr.update(variable.InputData_COLUMN_CONTENT, variable.STRING_DEFAULT);
                                    dr.update(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE, new Date().getTime());
                                    Log.i("deletePost", "onComplete: " + task.getResult().size());
                                }
                            }
                        }
                    });
            Toast.makeText(context,"Deleted",Toast.LENGTH_LONG).show();
        }
        SQLiteDatabase db = viewMyDb.getSQL_Write_Instance();
        db.delete(variable.InputData_TABLE_NAME, "id ="+"'"+id+"'", null);
        isPostDeleted = true;
        finish();
    }

    /* for own post this button enables editing */
    public void editPost(View view) {
        if(!viewMyDb.isConnectedToInternet()){
            snackBar("Please connect to internet...");
        }else {
            if (author.equals(variable.USER_ID) && triggeredFrom != variable.TRIGGERED_FROM_TREND) {

                if (isEditing) {
                    /* save */
                    viewProgressBar.setVisibility(View.VISIBLE);
                    String getTitle = etViewContentTitleEdit.getText().toString();
                    String getContent = etViewContentEdit.getText().toString();

                    viewContentTitle.setVisibility(View.VISIBLE);
                    viewContent.setVisibility(View.VISIBLE);
                    etViewContentEdit.setVisibility(View.GONE);
                    etViewContentTitleEdit.setVisibility(View.GONE);
                    viewDeletePost.setVisibility(View.VISIBLE);
                    viewBookmark.setVisibility(View.VISIBLE);
                    viewEditPost.setBackground(context.getResources().getDrawable(R.drawable.edit_white, null));

                    viewContentTitle.setText(getTitle);
                    viewContent.setText(getContent);

                    updatePost(getTitle, getContent);
                    isEditing = false;
                } else {
                    /* edit */
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    etViewContentTitleEdit.setVisibility(View.VISIBLE);
                    etViewContentEdit.setVisibility(View.VISIBLE);
                    etViewContentEdit.setFocusable(true);
                    viewContent.setVisibility(View.GONE);
                    viewContentTitle.setVisibility(View.GONE);
                    viewDeletePost.setVisibility(View.GONE);
                    viewEditPost.setBackground(context.getResources().getDrawable(R.drawable.save_white, null));
                    viewBookmark.setVisibility(View.GONE);

                    etViewContentTitleEdit.setText(title);
                    etViewContentEdit.setText(cont);

                    isEditing = true;
                }
            }
        }
    }

    public void updatePost(final String title, final String content){
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection(variable.InputData_TABLE_NAME)
                .whereEqualTo(variable.InputData_COLUMN_ID, id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            FirebaseFirestore fdbUpdate = FirebaseFirestore.getInstance();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                DocumentReference dr = fdbUpdate.collection(variable.InputData_TABLE_NAME).document(documentId);
                                dr.update(variable.InputData_COLUMN_CONTENT, content);
                                dr.update(variable.InputData_COLUMN_TITLE, title);
                                dr.update(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE, new Date().getTime());
                                Log.i("deletePost", "onComplete: " + task.getResult().size());
                            }
                        }
                        viewProgressBar.setVisibility(View.GONE);
                    }
                });
     }
    public void snackBar(String message){
        Snackbar.make(viewContentParentLayout,message,Snackbar.LENGTH_LONG).show();
    }

    public void sharePost(View view) {

            getShortLinkUriForLong();

    }

    public void getShortLinkUriForLong() {
        Log.i("teste", "getShortLinkUriForLong: click test");
        String descriptionToSend =cont;
        if(descriptionToSend.length() > 200 ){
            descriptionToSend = cont.substring(0,200);
        }
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.srujani1.com/" + id + "/" + variable.TRIGGERED_FROM_DYNAMIC_LINK + "/"))
                .setDomainUriPrefix("https://srujani1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(title)
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

    /* close intent*/
    public void BackButtonViewContent(View view) {
        finish();
    }
}
