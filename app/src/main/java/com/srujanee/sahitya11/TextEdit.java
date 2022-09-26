package com.srujanee.sahitya11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

public class TextEdit extends AppCompatActivity {

    String receivedText;
    TextView writtenText;
    ProgressBar editProgressBar;
    //GridView gridView;
    RecyclerView edtiRecycler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.LayoutManager layoutManagerImageOption;
    RecyclerView editRecyclerImageOption;
    private static RecyclerView.Adapter adapter;
   static View.OnClickListener myOnClickListener;
   static View.OnClickListener myOnClickListenerImageSubCategory;
   // GridView gridViewText;
  //  gridViewAdapter array;
    Context context;
    int ScreenHeight;
    int setheight;
    int relativeHeight;
    int relativeWidth;
    ImageView editBackGround;
    String receivedTitle;
    ArrayList<String> imageSubFolderNames;
    ArrayList<String> imageUriList;
    ArrayList<String> imageUriListDefault;

    String DraftId;

    DBHelper myDb;

    int code;
    int codeImageSubCategory;

    //user Input to be stored
    int textSizeSelected = 2;
    String textColourSelected = "000000";
    String textBackGroundSelected;
    int textPositionSlected = 0;
    String receivedTextLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        try{
        context = this;
        myDb = new DBHelper(context);

        editProgressBar = (ProgressBar) findViewById(R.id.editProgressBar);
        writtenText = (TextView) findViewById(R.id.writtenText);
        edtiRecycler = (RecyclerView) findViewById(R.id.editRecycler);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        edtiRecycler.setLayoutManager(layoutManager);
        edtiRecycler.setHasFixedSize(true);
        edtiRecycler.setNestedScrollingEnabled(false);

        editRecyclerImageOption = (RecyclerView) findViewById(R.id.editRecyclerImageOption);
        layoutManagerImageOption = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        editRecyclerImageOption.setLayoutManager(layoutManagerImageOption);
        editRecyclerImageOption.setHasFixedSize(true);
        editRecyclerImageOption.setNestedScrollingEnabled(false);

        textBackGroundSelected = "Basic_2.jpg";//todo give default image if not selected
        textColourSelected = variable.textColourCode[variable.textColourCode.length-1];
            Log.i("clCode", "onCreate: "+textColourSelected);
        textPositionSlected = 0;

        myOnClickListener = new TextEdit.MyOnClickListener(this);
        myOnClickListenerImageSubCategory = new TextEdit.MyOnClickListenerImage(this);
        //  gridView = (GridView) findViewById(R.id.gridView);
        //   gridViewText = (GridView) findViewById(R.id.gridViewText);
        editBackGround = (ImageView) findViewById(R.id.edtibackGround);
        DisplayMetrics metrics = new DisplayMetrics();
        ScreenHeight = metrics.heightPixels;
        setheight = ScreenHeight / 4;
        imageSubFolderNames = new ArrayList<String>();
        imageUriList = new ArrayList<String>();
        imageUriListDefault = new ArrayList<String>();
        Intent intent = getIntent();
        receivedText = intent.getStringExtra("content");
        receivedTitle = intent.getStringExtra("title");
        DraftId = intent.getStringExtra("draftId");
        //array = new gridViewAdapter(this,false);
        receivedTextLocal = receivedText.replace("\n", "<br>");
        receivedTextLocal = "<big>" + receivedTitle + "</big>" + "&nbsp;&nbsp;" + "<br><br>" + receivedTextLocal + "<small><br>@" + variable.USER_ID + "</small>";


        writtenText.setText(Html.fromHtml(receivedTextLocal));
        writtenText.setTextColor(Color.parseColor("#"+textColourSelected));
        code = variable.STYLE_TEXT_BACKGROUND;
        //gridView.setAdapter(new gridViewAdapter(this,true,code));
        /* todo set default folder details */
        imageUriListDefault = myDb.getAllImageDirectoryUri("Basic", editProgressBar, variable.STRING_NOT_NULL);
        imageUriList = imageUriListDefault;
        //  Log.i("sfdasdf", "onClick: "+imageUriList.size());
        edtiRecycler.setAdapter(new customAdapterEdit(context, code, imageUriListDefault));
        //adapter = new customAdapterEdit(context, code);
        // edtiRecycler.setAdapter(adapter);
        codeImageSubCategory = variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS;

        imageSubFolderNames = myDb.getImageSubFolderNames();
        adapter = new customAdapterEdit(context, codeImageSubCategory, imageSubFolderNames);
        editRecyclerImageOption.setAdapter(adapter);

        //gridViewText.setAdapter(new gridViewAdapter(this,false));
        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.parentLayout);
        relativeWidth = rlayout.getWidth();
        relativeHeight = rlayout.getHeight();
        Log.i("W-H", relativeWidth + "-" + relativeHeight);
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> aparent, View view, int position, long l) {
            }
        });*/

      /*  gridViewText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> aparent, View view, int position, long l) {
                //writtenText.setBackground(context.getResources().getDrawable(array.mThumbIdsText[i],null));

            }
        });*/

        /*todo

        writtenText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    // Offsets are for centering the TextView on the touch location
                    v.setX(event.getRawX() - v.getWidth());
                    v.setY(event.getRawY() - v.getHeight());
                }

                return true;
            }

        });*/
    }
        catch(Exception e){
            Log.e(variable.TRY_CATCH_ERROR + " onCreateTextEdit", e.getMessage() );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.isPosted ){
            finish();
        }
    }

    public void backgroundOption(View view) {
        code = variable.STYLE_TEXT_BACKGROUND;
        edtiRecycler.setAdapter(new customAdapterEdit(context,code, imageUriListDefault));
        //edtiRecycler.setAdapter(new customAdapterEdit(this,code));
        editRecyclerImageOption.setVisibility(View.VISIBLE);
    }

    public void textPosition(View view) {
        code = variable.STYLE_TEXT_POSITION;
        edtiRecycler.setAdapter(new customAdapterEdit(this,code));
        editRecyclerImageOption.setVisibility(View.GONE);
    }

    public void textSize(View view) {
        code = variable.STYLE_TEXT_SIZE;
        edtiRecycler.setAdapter(new customAdapterEdit(this,code));
        editRecyclerImageOption.setVisibility(View.GONE);
    }

    public void textColour(View view) {
        code = variable.STYLE_TEXT_COLOUR;
        edtiRecycler.setAdapter(new customAdapterEdit(this,code));
        editRecyclerImageOption.setVisibility(View.GONE);
    }
    public void textFont(View view) {
   //     code = variable.STYL;
       // edtiRecycler.setAdapter(new customAdapterEdit(this,code));
        editRecyclerImageOption.setVisibility(View.GONE);
    }

    public void ProceedToPost(View view) {
        Intent intent = new Intent(this,postActivity.class);
        intent.putExtra("code", variable.ACTIVITY_TEXT_EDIT);
        intent.putExtra("backgroundIndex", textBackGroundSelected);
        intent.putExtra("textSize", textSizeSelected);
        intent.putExtra("textColour", textColourSelected);
        intent.putExtra("textPosition", textPositionSlected);
        intent.putExtra("adapterCode", variable.ADAPTER_CODE_SHORT_WRITING);
        intent.putExtra("title", receivedTitle);
        intent.putExtra("content", receivedText);
        intent.putExtra("draftId",DraftId);
        startActivity(intent);
    }

    public void BackButtonEditActivity(View view) {
        finish();
    }


    private class MyOnClickListener implements View.OnClickListener {

        int position;
        int imageSubCategoryPosition;
        Context context;
        public  MyOnClickListener(Context context)
        {
         this.context = context;
        }
        @Override
        public void onClick(View view){
            position = edtiRecycler.getChildAdapterPosition(view);
            imageSubCategoryPosition = editRecyclerImageOption.getChildAdapterPosition(view);
            Log.i("chekk", "onClick: "+position+"  "+imageSubCategoryPosition);
            if(code == variable.STYLE_TEXT_BACKGROUND) {
               /* if(codeImageSubCategory == variable.STYLE_TEXT_BACKGROUND_DEFAULT){

                }else if(codeImageSubCategory == 1){
                    editBackGround.setImageResource(variable.drawableImageCode[position]);
                }*/
               String[] splitCode = imageUriList.get(position).split("/");
                editBackGround.setImageURI(Uri.parse(imageUriList.get(position)));
                textBackGroundSelected = splitCode[splitCode.length-1];
                Log.i("uriName", "onClick: "+imageUriList.get(position));
            }
            else if(code == variable.STYLE_TEXT_SIZE){
                if(position < variable.textSize.length)
                    writtenText.setTextSize(variable.textSize[position]);
                textSizeSelected = position;
            }
            else if(code == variable.STYLE_TEXT_COLOUR)
            {
                writtenText.setTextColor(Color.parseColor("#"+variable.textColourCode[position]));
                textColourSelected = variable.textColourCode[position];

            }
            else if(code == variable.STYLE_TEXT_POSITION){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(position == variable.TEXT_CENTRE)
                {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_CENTER));
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    writtenText.setGravity(Gravity.START);
                }else if (position == variable.TEXT_LEFT_TOP)
                {
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    writtenText.setGravity(Gravity.CENTER);
                   /* params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_TEXT_START));*/
                    writtenText.setGravity(Gravity.START);
                }else if (position == variable.TEXT_CENTRE_TOP)
                {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    writtenText.setGravity(Gravity.CENTER);
                }else if (position == variable.TEXT_RIGHT_TOP)
                {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    writtenText.setGravity(Gravity.END);
                }else if (position == variable.TEXT_LEFT_CENTRE)
                {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    writtenText.setGravity(Gravity.START);
                }else if (position == variable.TEXT_RIGHT_CENTRE)
                {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    writtenText.setGravity(Gravity.END);
                }else if (position == variable.TEXT_LEFT_BOTTOM)
                {
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    writtenText.setGravity(Gravity.START);
                }else if (position == variable.TEXT_CENTRE_BOTTOM)
                {
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    writtenText.setGravity(Gravity.CENTER);
                }else if (position == variable.TEXT_RIGHT_BOTTOM)
                {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    writtenText.setGravity(Gravity.END);
                }
                else{
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    params.addRule((TextView.TEXT_ALIGNMENT_CENTER));
                    writtenText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    writtenText.setGravity(Gravity.START);
                }
                writtenText.setLayoutParams(params);
                textPositionSlected = position;
            }

        }
    }

    private class MyOnClickListenerImage implements View.OnClickListener {
        int imageSubFolderPosition;
        Context context;
        TextView holdPreviousSelectedView = null;

        public MyOnClickListenerImage(Context context){
            this.context = context;
        }
        @Override
        public void onClick(View view) {
            imageSubFolderPosition = editRecyclerImageOption.getChildAdapterPosition(view);
            TextView tvSubFolderName = view.findViewById(R.id.imageSubFolerName);
            tvSubFolderName.setTextColor(context.getResources().getColor(R.color.orange, null));
            if(holdPreviousSelectedView != null) {
                holdPreviousSelectedView.setTextColor(context.getResources().getColor(R.color.black, null));
            }
            holdPreviousSelectedView = tvSubFolderName;
           // Log.i("chaluna", "onClick: "+imageSubFolderPosition);
            imageUriList = new ArrayList<String>();

            codeImageSubCategory = variable.ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER;
            imageUriList = myDb.getAllImageDirectoryUri(imageSubFolderNames.get(imageSubFolderPosition), editProgressBar, variable.STRING_NOT_NULL);
            Log.i("sfdasdf", "onClick: "+imageUriList.size());
            edtiRecycler.setAdapter(new customAdapterEdit(context,codeImageSubCategory, imageUriList));

        }
    }
}
