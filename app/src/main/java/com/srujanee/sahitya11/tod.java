package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class tod extends AppCompatActivity {

    Context context;
    Activity activity;
    DBHelper myDb;
    int triggerPosition;
    TextView TodFunTitle;
    RecyclerView todRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    int triggerFrom;
    String imageType;
    Intent myIntent;
    ImageButton shareButton;
    ArrayList<String> imageUriStringList;
    ProgressBar progressDownloadImage;
    LinearLayout todMainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tod);
        try{
        context = this;
        activity = (Activity) context;
        this.myIntent = getIntent();
        this.imageType = getIntent().getStringExtra(variable.OUR_IMAGE_POST);
        this.triggerPosition = getIntent().getIntExtra("SelectedPosition",0);
        this.myDb = new DBHelper(context);
        triggerFrom = variable.TRIGGERED_FROM_TOD;
        this.imageUriStringList = new ArrayList<String >();
        this.progressDownloadImage = (ProgressBar) findViewById(R.id.todProgressBar);
        this.todMainContainer = ( LinearLayout) findViewById(R.id.todMainContainer);
        this.TodFunTitle = (TextView) findViewById(R.id.TOD_FUN_Title);

        this.shareButton = (ImageButton) findViewById(R.id.todShare);
        todRecyclerView = (RecyclerView) findViewById(R.id.todRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        todRecyclerView.setLayoutManager(layoutManager);
        todRecyclerView.setHasFixedSize(true);

        if(imageType.equals("TOD")) {
            TodFunTitle.setText("Thought Of The Day");
        }else if(imageType.equals("FUN")){
            TodFunTitle.setText("MEME");
        }
        adapter = new customAdapterProfile(imageUriStringList,triggerFrom,context);
        //File file = new File (context.getExternalFilesDir(null), "Srujanee/TOD");
        // Deprecated - File file = new File (Environment.getExternalStorageDirectory(), );
        File file = new File(context.getExternalFilesDir(null),"Srujanee/"+this.imageType);
            if(!file.exists()){
                file.mkdirs();
            }
            imageUriStringList = myDb.getAllImageDirectoryUri(imageType,null, null);
            adapter = new customAdapterProfile(imageUriStringList,triggerFrom,context);
            todRecyclerView.setAdapter(adapter);
            todRecyclerView.smoothScrollToPosition(triggerPosition);
        //downloadImageFromFirebase(file,this.imageType, progressDownloadImage);
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onCreateTod", e.getMessage());
        }

    }
    public void downloadImageFromFirebase(final File localFile, final String subFolderId, final ProgressBar progressBar){
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(!myDb.isConnectedToInternet()){
            snackBar("Please connect to internet...");
            if(progressBar != null){
                progressBar.setVisibility(View.GONE);
            }
        }
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final ArrayList<String> existingImages = new ArrayList<String>();
        String[] imageUriLocal;
        for(File file : localFile.listFiles())
        {
            imageUriLocal = file.getAbsolutePath().split("/");
            existingImages.add(imageUriLocal[imageUriLocal.length-1]);
        }
        StorageReference srujaneeStoage = mStorageRef.child("srujanee/"+subFolderId);
        srujaneeStoage.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.i("stepToImage", "step0: ");
                        for(StorageReference imageRef : listResult.getItems()){
                            String[] imageUriFirebase;
                            imageUriFirebase = String.valueOf(imageRef).split("/");
                            Log.i("stepToImage", "step-11: ");
                            if(existingImages.contains(imageUriFirebase[imageUriFirebase.length-1]) == false) {

                                File file = new File(localFile.getAbsolutePath()+"/"+imageUriFirebase[imageUriFirebase.length-1]);
                                imageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.i("stepToImage", "step1: ");
                                    }
                                });
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imageUriStringList = myDb.getAllImageDirectoryUri(subFolderId,null, null);
                                adapter = new customAdapterProfile(imageUriStringList,triggerFrom,context);
                                todRecyclerView.setAdapter(adapter);
                                if(progressBar != null){
                                    progressBar.setVisibility(View.GONE);
                                }
                                Log.i("stepToImage", "step3: ");
                            }
                        }, 2500);
                        //todo
                        Log.i("downloadImage", "onSuccess: ");
                        //StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("downloadImage", "onFailure: "+ e.getMessage() );
            }
        });
    }

    public void snackBar(String message){
        Snackbar.make(todMainContainer,message,Snackbar.LENGTH_LONG).show();
    }

    public void BackButtonTOD(View view) {
        finish();
    }
}