/*
  Type: Activity
  Purpose: to create new post. (Writing window)
  Source Of Trigger: Via floating Button (Bottom Left Corner)
  Used Xml: Activity_create_new.xml(main)
  Dependency: N/A
  Function Names: N/A
*/

package com.srujanee.sahitya11;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class createNew extends AppCompatActivity {

    String newTitle;
    String newContent;
    Date newDate;
    int newType;
    boolean isAuthentic;


    private String newDraftId;
    String intentId;
    int triggeredFrom;

    Context context;
    Calendar calendar;
    SimpleDateFormat dateFormat;

    int adapterCode;

    EditText newGetTitle;
    EditText newGetContent;
    Switch createOptionSwitch;
    TextView textOption;
    RelativeLayout createParentLayout;
    View rootView;
    DBHelper myDb;
    Boolean isCreateOptionSwitchChecked;
    int typeFaceSelected = Typeface.NORMAL; //private global var with NORMAL as default value
    //String newResultedText = "";
     int noofchar=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        try{

        createParentLayout = (RelativeLayout) findViewById(R.id.createParentLayout);
        Intent intent = getIntent();
        context = this;
        isCreateOptionSwitchChecked = false;
        //adapterCode = intent.getIntExtra("writingType",variable.ADAPTER_CODE_LONG_WRITING);
        adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
        newDraftId = intent.getStringExtra("draftId");
        triggeredFrom = intent.getIntExtra(variable.TRIGGERED_FROM, variable.DEFAULT);
        newTitle = intent.getStringExtra("title");
        newContent = intent.getStringExtra("content");
        createOptionSwitch = (Switch) findViewById(R.id.createTypeSwitch);


         newGetContent = (EditText)findViewById(R.id.newContent);
         newGetTitle = (EditText)findViewById(R.id.newTitle);
         myDb = new DBHelper(context);
        /* download recent image folder, if available -todo*/
        myDb.insertImageSubFolder();

         if(triggeredFrom == variable.TRIGGERED_FROM_DRAFT){
           //  Toast.makeText(this,"hello"+myDb.getDraftNumber()+"ss",LENGTH_SHORT).show();
             Log.i("before", "onCreate: "+myDb.getDraftNumber());
             newGetContent.setText(newContent);
             newGetTitle.setText(newTitle);
             myDb.deleteDraft(newDraftId);
             Log.i("after", "onCreate: "+myDb.getDraftNumber());
            // Toast.makeText(this,"hello"+myDb.getDraftNumber()+"ss",LENGTH_SHORT).show();
         }

         newGetTitle.requestFocus();
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

         getDetails();
        createOptionSwitch.setText("Short Writing");
        createOptionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
               if(checked){
                   adapterCode = variable.ADAPTER_CODE_LONG_WRITING;
                   createOptionSwitch.setText("Long Writing");
               }else{
                   adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
                   createOptionSwitch.setText("Short Writing");
               }
                isCreateOptionSwitchChecked = checked;
            }
        });
        Log.i("switch", "onCreate: "+createOptionSwitch.getTextOff()+" "+createOptionSwitch.getTextOn());
        //input method to choose language
        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imeManager.showInputMethodPicker();

        newGetContent.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(final CharSequence s, int start, int before, int count) {
               // String sAux = (s.toString()).substring(start, start + count);

                char inputChar = ' ';
                if(s.length() != 0) {
                    inputChar = s.toString().charAt(s.length() - 1);
                }
                int se = inputChar;
                Log.i("tes22t", "onTextChanged: "+se);
                /* accept only odia character, english numeric and special characters */
             /*   if((inputChar >= 0 && inputChar <= 64) ||
                        (inputChar >= 91 && inputChar <= 96) ||
                        (inputChar >= 123 && inputChar <= 127) ||
                        (inputChar >= 2817 && inputChar <= 2928)){
                    //success nothing needs to be done
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please Write ODIA only...");
                    // builder.setMessage("Are you sure?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            String tempContent = s.toString().substring(0,s.length()-1);
                            newGetContent.setText(tempContent);
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    Log.i("test", "onTextChanged: "+"neihia");
                } */

                //newGetContent.setText(s);
                   /* switch (typeFaceSelected) {
                        case Typeface.NORMAL:
                            resultedText = sAux;
                            break;
                        case Typeface.ITALIC:
                            resultedText = "<i>" + sAux + "</i>";
                            break;
                        case Typeface.BOLD:
                            resultedText = "<b>" + sAux + "</b>";
                            break;
                }*/
             //   getContent.setText(Html.fromHtml(resultedText));

            }
        });



   /*  containerCreate.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//todo
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = rootView.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top);
               // Log.i("Keyboard Size", "Size: " + screenHeight+" "+400);

                //boolean visible = heightDiff > screenHeight / 3;
            }
        });*/
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onCreateNew", e.getMessage());
        }
    }

    private boolean getDetails()
    {

        if(triggeredFrom == variable.DEFAULT) {
            newDraftId = myDb.getDraftNumber() + 1 + "";
        }

        newTitle = newGetTitle.getText().toString();
        newContent = newGetContent.getText().toString();
        newType = adapterCode;
       // Toast.makeText(context,newDraftId,Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            newDate = calendar.getTime();
        }
        if(newContent.length() < 10)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    public void saveContent(View view) {
        String temp = newGetContent.getText().toString();
        newGetContent.setText(Html.fromHtml(temp));
        temp = newGetContent.getText().toString();
        newGetContent.setFocusable(true);
        newGetContent.setCursorVisible(true);
        newGetContent.setSelection(temp.length());
       /* getetails();
        id = (int)new Random().nextInt();//todo
        myDb = new DBHelper(this);
        myDb.insertContent("user"+id,newGenre,newTitle,newAuthor,newContent,newDate,newTag,newSubCategory);
        Toast.makeText(this,id+" saved",Toast.LENGTH_SHORT).show();*/
    }
     //private global var, this will give to your textview the result of the edittext in HTML

//You can edit the text you add



    public void makeBold(View view) {
        setCode("<b></b>",3);
        //setSelectedWordToTexture();
    }

    public void makeItalic(View view) {
      setCode("<i></i>",3);
    }

    public void highlight(View view) {
       setCode("<font color='red'></font>",18);
    }
    public void underLine(View view) {
        setCode("<u></u>",3);
    }
    public void setCode(String code, int buffer)
    {
        newContent = newGetContent.getText().toString();
        int cursorPosition = newGetContent.getSelectionEnd();
        int textLength = newContent.length();
        String temp1 = newContent.substring(0,cursorPosition);
        String cmd = code;
        String temp2 = newContent.substring((cursorPosition),textLength);
        newContent = temp1+cmd+temp2;
        newGetContent.setText( newContent);
        newGetContent.setFocusable(true);
        newGetContent.setCursorVisible(true);
        newGetContent.setSelection(cursorPosition+buffer);
    }

    public void setSelectedWordToTexture(String code, int buffer){
        String text = newGetContent.getText().toString();
        String words[] = text.split(" ");
        String lastWord = words[words.length-1];
    }

    public void proceed(View view) {
        isAuthentic = true;
        if (!myDb.isConnectedToInternet()) {
            Toast.makeText(context, "Please connect to internet...", Toast.LENGTH_LONG).show();
        } else {
            newTitle = newGetTitle.getText().toString();
            newContent = newGetContent.getText().toString();
            char[] titleCharArray = newTitle.toCharArray();
            char[] characterArray = newContent.toCharArray();
            for (int index = 0; index < characterArray.length; index++) {
                char inputChar = characterArray[index];

                /* check if other than permitted character typed */
                if ((inputChar >= 0 && inputChar <= 64) ||
                        (inputChar >= 91 && inputChar <= 96) ||
                        (inputChar >= 123 && inputChar <= 127) ||
                        (inputChar >= 2817 && inputChar <= 2928)||
                        (inputChar == 'ୱ') ||
                        (inputChar == '।') ||
                         (inputChar == 160)){
                    //success nothing to do

                } else {
                    int x = inputChar;
                    String message =   "Body: Deviation at " + index + "th character  " + "'" + inputChar +"("+x+")"+ "'";
                    showPopUp(message);
                    Log.i("getCharss", "proceed: "+"Title: Deviation at " + index + "th character  " + "'" + inputChar +"("+x+")"+ "'");
                   // snackBar(message);
                    break;
                }
                newContent = characterArray.toString();
            }

            for (int index = 0; index < titleCharArray.length; index++) {
                char inputChar = titleCharArray[index];

                /* check if other than permitted character typed */
                if ((inputChar >= 0 && inputChar <= 64) ||
                        (inputChar >= 91 && inputChar <= 96) ||
                        (inputChar >= 123 && inputChar <= 127) ||
                        (inputChar >= 2817 && inputChar <= 2928) ||
                        (inputChar == 'ୱ') ||
                        (inputChar == '।') ||
                        (inputChar == 160)
                ) {
                    //success nothing to do
                } else {
                    int x = inputChar;
                    String message =   "Title: Deviation at " + index + "th character  " + "'" + inputChar +"("+x+")"+ "'";
                    Log.i("getCharss", "proceed: "+"Title: Deviation at " + index + "th character  " + "'" + inputChar + "'");
                    showPopUp(message);
                    // snackBar(message);
                    break;
                }
                newContent = characterArray.toString();

            }


            if (isAuthentic){
                if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {
                    Log.e("length", "proceed: " + newTitle.length() + " " + newContent.length());
                    if (newTitle.length() < 2) {
                        snackBar("Too Short Title!");
                        //Toast.makeText(context, "Too Short To Post!", Toast.LENGTH_SHORT).show();

                    } else if (newContent.length() < 2) {
                        snackBar("Too Short For A Long Post!");
                        //Toast.makeText(context, "Too Short To Post! please select short post", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, postActivity.class);
                        intent.putExtra("title", newGetTitle.getText().toString());
                        intent.putExtra("content", newGetContent.getText().toString());
                        intent.putExtra("draftId", newDraftId);
                        intent.putExtra("adapterCode", variable.ADAPTER_CODE_LONG_WRITING);
                        startActivity(intent);
                        myDb.insertdraft(newDraftId, newTitle, newContent, newDate, newType);
                    }
                } else if (adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {
                    Log.e("length", "proceed: " + newTitle.length() + " " + newContent.length());
                    if (newContent.length() < 5) {
                        snackBar("Too Short To Post!");
                        //Toast.makeText(context, "Too Short To Post!", Toast.LENGTH_SHORT).show();
                    } else if (newContent.length() > 200) {
                        snackBar("Too Long For A Short Post!");
                        //Toast.makeText(context, "Too Long To Post! Please select long writing", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, TextEdit.class);
                        intent.putExtra("title", newGetTitle.getText().toString());
                        intent.putExtra("content", newGetContent.getText().toString());
                        intent.putExtra("draftId", newDraftId);
                        startActivity(intent);
                        myDb.insertdraft(newDraftId, newTitle, newContent, newDate, newType);

                    }
                } else {
                    /* nothing to do */
                }
        }
    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDb = new DBHelper(context);
        if(MainActivity.isPosted)
        {
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {

        Log.i("create new", "onBackPressed: ");
        boolean shouldStore = getDetails();
        if (shouldStore) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Do you want to save as draft?");
            // builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    myDb.insertdraft(newDraftId, newTitle, newContent, newDate, newType);
                    dialog.dismiss();
                    Toast.makeText(context, "Saved To Draft", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(context, "Discarded", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            super.onBackPressed();
    }
    }
    public void snackBar(String message){
        Snackbar.make(createParentLayout,message,Snackbar.LENGTH_LONG).show();
    }

    public void showPopUp(String message){
        isAuthentic = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("The content should only be odia characters...\n"+message);
        // builder.setMessage("Are you sure?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        Toast.makeText(context, "Please make correction and try again...", Toast.LENGTH_LONG).show();
    }

    public void BackButtonCreateNew(View view) {
        finish();
    }
}