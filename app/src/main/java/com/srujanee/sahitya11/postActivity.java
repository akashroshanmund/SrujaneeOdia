package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class postActivity extends AppCompatActivity {

    //View declaration
    static String fireBaseUserID = variable.STRING_DEFAULT;
    Context context;
    static gridViewAdapter adapterCategory;
    static gridViewAdapter adapterSubCategory;
    GridView categoryGrid;
    static GridView subCategoryGrid;
    EditText postShortDescription;
    LinearLayout postParentLayout;
    CheckBox postCheck;
    ProgressBar progressBar;
    Button postButton;
    Intent getIntent;
    int adapterCode;
    //initialization data
    ArrayList<String> categoryList;
    static ArrayList<String> subCategoryList;
    private DBHelper myDb;
    String draftId;

    //variable to store in the database
    String storeId;
    String storeGenre = variable.STRING_DEFAULT;
    String storeTitle;
    String storeContent;
    String storeAuthor;
    long storeDate;
    String storeTag;
    String storeSubCategory = variable.STRING_DEFAULT;
    String storeShortDescription;

    //if opened from edit text intent
    String activityCode;
    String textBackgroundIndex;
    int textSizeIndex;
    String textColourIndex;
    int textPositionIndex;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    //log in
    static String userEmail = variable.STRING_DEFAULT;
    static String userSubscription = variable.STRING_DEFAULT;
    static String enteredUserID ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        try{
        this.getIntent = getIntent();
        this.context = this;

        /*prevent keyboard to push the view up */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        this.postParentLayout = (LinearLayout) findViewById(R.id.postParentLayout);
        this.categoryGrid = (GridView) findViewById(R.id.postCategoryGrid);
        this.subCategoryGrid = (GridView) findViewById(R.id.postSubCategoryGrid);
        this.postCheck = (CheckBox) findViewById(R.id.postAuthenticationCheck);
        this.postShortDescription = (EditText) findViewById(R.id.postShortDescription);
        this.progressBar = (ProgressBar) findViewById(R.id.postProgressBar);
        this.postButton = (Button) findViewById(R.id.postButton);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
// ...
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        myDb = new DBHelper(context);
        variable.USER_ID = myDb.getUserId();
        variable.USER_EMAIL_ID = myDb.getUserEmail();
        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();
        getDetails();
        categoryList = myDb.getCategoryNames(adapterCode);
        adapterCategory = new gridViewAdapter(context, categoryList, variable.ADAPTER_CODE_CATEGORY_LIST, variable.DEFAULT, variable.DEFAULT);
        categoryGrid.setAdapter(adapterCategory);

        subCategoryList = myDb.getSubCategoryNames("poem");
        adapterSubCategory = new gridViewAdapter(context, subCategoryList, variable.ADAPTER_CODE_SUBCATEGORY_LIST, variable.DEFAULT, variable.DEFAULT);
        subCategoryGrid.setAdapter(adapterSubCategory);


        if (activityCode != null && activityCode.equals(variable.ACTIVITY_TEXT_EDIT)) {
            postShortDescription.setVisibility(View.VISIBLE);
        }
    }catch(Exception e)
    {
        Log.e(variable.TRY_CATCH_ERROR + " onCreatePostActivity", e.getMessage() );
    }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void getDetails()
    {
        this.storeAuthor = variable.USER_ID;
        int temp = myDb.getDbRowCount(variable.MY_POST_TABLE_NAME) + 1;
        this.storeId = this.storeAuthor + temp;
        this.storeContent = getIntent.getStringExtra("content");
        this.storeTitle = getIntent.getStringExtra("title");
        this.adapterCode = getIntent.getIntExtra("adapterCode", variable.DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            long date = new Date().getTime();
            this.storeDate = date;
        }
        this.draftId = getIntent.getStringExtra("draftId");
        //todo Version 2
        storeTag = "default";

        this.activityCode = this.getIntent.getStringExtra("code");
        if(this.activityCode != null && this.activityCode.equals(variable.ACTIVITY_TEXT_EDIT)){
            this.textBackgroundIndex = getIntent.getStringExtra("backgroundIndex");
            this.textSizeIndex = getIntent.getIntExtra("textSize", variable.DEFAULT);
            this.textColourIndex = getIntent.getStringExtra("textColour");
            this.textPositionIndex = getIntent.getIntExtra("textPosition", variable.DEFAULT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void post(View view) {
        /* if not signed in ask to sign up  else post directly */
        if(variable.USER_ID.equals(variable.STRING_DEFAULT)) {
            signUpPopUp();
        }else{
            submitPost();
        }

    }

    public void postInformation(View view) {

    }

    public void submitPost() {

        if (adapterSubCategory.selectedPositionSubCategory == variable.DEFAULT || adapterCategory.selectedPosition == variable.DEFAULT) {
            Toast.makeText(context, "Please choose category and subCategory ", Toast.LENGTH_SHORT).show();
        } else if (!postCheck.isChecked()) {
            Toast.makeText(context, "Please select the check box", Toast.LENGTH_SHORT).show();
        } else {

            progressBar.setVisibility(View.VISIBLE);
            postButton.setText("");
            postButton.setClickable(false);
            this.storeAuthor = variable.USER_ID;
            int temp = myDb.getDbRowCount(variable.MY_POST_TABLE_NAME) + 1;
            this.storeId = this.storeAuthor + temp;
            this.storeGenre = this.categoryList.get(adapterCategory.selectedPosition).toLowerCase();
            this.storeSubCategory = this.subCategoryList.get(adapterSubCategory.selectedPositionSubCategory).toLowerCase();
            myDb.insertContent(storeId, storeGenre, storeTitle, storeAuthor, storeContent, storeDate, storeTag, storeSubCategory, adapterCode, variable.INPUT_VALUE_ZERO);
            myDb.insertMyPost(storeId);
            Toast.makeText(context, "posted successfully", Toast.LENGTH_SHORT).show();
            MainActivity.isPosted = true;
            myDb.deleteDraft(draftId);

            if (activityCode != null && activityCode.equals(variable.ACTIVITY_TEXT_EDIT)) {
                this.storeShortDescription = postShortDescription.getText().toString();
                myDb.insertShortPostData(variable.USER_ID, storeId, textBackgroundIndex, textSizeIndex, textColourIndex, textPositionIndex, storeShortDescription);
            }

            Log.i("postIdd", "submitPost: "+storeId);
            progressBar.setVisibility(View.GONE);
            postButton.setText("POST");
            postButton.setClickable(true);
            finish();
        }
    }

    public void signUpPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pen Name");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        // input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredUserID = input.getText().toString();
                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                fb.collection(variable.USER_LIST)
                        .whereEqualTo(variable.userData_COLUMN_USER_ID, enteredUserID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    Log.i("success", "onComplete: "+task.getResult().size());
                                    if(task.getResult().size() >0 ){
                                        //todo

                                        userEmail = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_USER_EMAIL_ID);
                                        userSubscription = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_PROFILE_SUBSCRIPTION);
                                        Log.i("userId", "onComplete: "+userEmail);
                                    }
                                    signIn();
                                }
                            }
                        });
                Log.i("signUp", "onClick: "+userEmail);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, variable.RC_SIGN_IN);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == variable.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Auth:", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Auth", "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        postButton.setText("");
        postButton.setClickable(false);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("UIDLogin", "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final DBHelper dbLocal = new DBHelper(context);

                            /* if new user  create instance in firebase */
                            if(userEmail.equals(variable.STRING_DEFAULT)){
                               /* dbLocal.insertUserDataFireBase(userID,user.getDisplayName(),String.valueOf(user.getPhotoUrl()), user.getEmail(),"",0,"default");
                                variable.USER_ID = myDb.getUserId();
                                variable.USER_EMAIL_ID = myDb.getUserEmail();
                                variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
                                Log.i("signUp", "newUser: "+userID);
                                submitPost();*/

                                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                                fb.collection(variable.USER_LIST)
                                        .whereEqualTo(variable.userData_COLUMN_USER_EMAIL_ID, user.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    Log.i("success", "onComplete: "+task.getResult().size());
                                                    if(task.getResult().size() >0 ){
                                                        fireBaseUserID = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_USER_ID);
                                                        userSubscription = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_PROFILE_SUBSCRIPTION);
                                                        Log.i("userId", "onComplete: "+userEmail);
                                                    }
                                                    Log.i("signInCondition", "userId: "+fireBaseUserID);
                                                    if(fireBaseUserID.equals(variable.STRING_DEFAULT)){
                                                        dbLocal.insertUserDataFireBase(enteredUserID,user.getDisplayName(),String.valueOf(user.getPhotoUrl()), user.getEmail(),"",0,variable.STRING_DEFAULT, userSubscription);
                                                        variable.USER_ID = myDb.getUserId();
                                                        variable.USER_EMAIL_ID = myDb.getUserEmail();
                                                        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
                                                        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();

                                                        submitPost();
                                                        Log.i("signInCondition", "onComplete: "+"new user");
                                                        snackBar("Successfully Signed In");

                                                    }
                                                    else{
                                                        signOut();
                                                        Log.i("signInCondition", "onComplete: "+"wrong pen name");
                                                        snackBar("Failed: Wrong Pen Name");
                                                    }

                                                }
                                            }
                                        });


                            }
                            /* if unauthentic userId  sign out*/
                            else if(!(user.getEmail()).equals(userEmail))
                            {
                                Log.i("signUp", "gaddar: "+enteredUserID);
                                snackBar("Failed: Wrong Pen Name");
                                signOut();
                            }
                            /* if existing user insert to local database */
                            else {
                               /* Log.i("signup", "existing: "+enteredUserID);
                                dbLocal.insertUserData(enteredUserID,user.getDisplayName(),String.valueOf(user.getPhotoUrl()), "",0,"default");
                               // new variable(myDb.getUserId(), myDb.getUserEmail(), myDb.getUserImageUri());
                                variable.USER_ID = enteredUserID;
                                insertPostIdFirebase(variable.USER_ID);
                                myDb.fetchFollowDataFirebase(variable.USER_ID);*/
                                FirebaseFirestore fb = FirebaseFirestore.getInstance();
                                fb.collection(variable.USER_LIST)
                                        .whereEqualTo(variable.userData_COLUMN_USER_EMAIL_ID, user.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("success", "onComplete: " + task.getResult().size());
                                                    if (task.getResult().size() > 0) {
                                                        fireBaseUserID = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_USER_ID);
                                                        userSubscription = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_PROFILE_SUBSCRIPTION);
                                                        Log.i("userId", "onComplete: " + userEmail);
                                                    }
                                                    if (enteredUserID.equals(fireBaseUserID)) {
                                                        Log.i("signUp", "onClick: " + userEmail);
                                                        Log.i("signInCondition", "onComplete: " + "existing user");
                                                        Log.i("signUp", "existing: " + enteredUserID);
                                                        dbLocal.insertUserData(enteredUserID, user.getDisplayName(), String.valueOf(user.getPhotoUrl()), "", 0, variable.STRING_DEFAULT,variable.STRING_DEFAULT);
                                                        variable.USER_ID = myDb.getUserId();
                                                        variable.USER_EMAIL_ID = myDb.getUserEmail();
                                                        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
                                                        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();
                                                        insertPostIdFirebase(variable.USER_ID);

                                                        myDb.fetchFollowDataFirebase(variable.USER_ID);

                                                        snackBar("Successfully Signed In");
                                                    } else {
                                                        signOut();
                                                        snackBar("Failed: Wrong E-mail Id");
                                                    }

                                                }
                                            }

                                        });

                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("UIDLogin", "signInWithCredential:failure", task.getException());
                            snackBar("Sign In Failed");
                        }
                    // ...
                    }
                });
    }

    /* google sign out code */
    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.i("signout", "onComplete: signOut"+"kehbata");
                    }
                });
        myDb.deleteSqlTable(variable.userData_TABLE_NAME);
        myDb.deleteSqlTable(variable.FOLLOWERS_TABLE_NAME);
        myDb.deleteSqlTable(variable.FOLLOWING_TABLE_NAME);
        myDb.deleteSqlTable(variable.MY_POST_TABLE_NAME);
        myDb.deleteSqlTable(variable.POST_LIKED_TABLE_NAME);
        myDb.deleteSqlTable(variable.draft_TABLE_NAME);
        myDb.deleteSqlTable(variable.bookmark_TABLE_NAME);
        myDb.deleteSqlTable(variable.GENRE_LIKED_TABLE_NAME);
        variable.USER_ID = variable.STRING_DEFAULT;
        variable.USER_EMAIL_ID = variable.STRING_DEFAULT;
        variable.USER_PROFILE_IMAGE_URI_AS_STRING = variable.STRING_DEFAULT;
        variable.USER_PROFIE_SUBSCRIPTION = variable.STRING_DEFAULT;
        // [END auth_fui_signout]
        progressBar.setVisibility(View.GONE);
        postButton.setText("POST");
        postButton.setClickable(true);
    }

    public void insertPostIdFirebase(String userId) {
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        // Map<String,Object> contentBuff=new HashMap<String,Object>();
        //contentBuff.put(variable.MY_POST_FIREBASE_POSTID,postid);

        fb.collection(variable.InputData_TABLE_NAME)
                .whereEqualTo(variable.InputData_COLUMN_USER_ID, userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ContentValues contentValues = new ContentValues();
                        DBHelper dbHelper = new DBHelper(context);
                        SQLiteDatabase sqlDb = dbHelper.getSQL_Write_Instance();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            contentValues.put(variable.My_POST_COLUMN_NAME, document.getString(variable.InputData_COLUMN_ID));
                            sqlDb.insert(variable.MY_POST_TABLE_NAME, null, contentValues);
                        }
                        sqlDb.close();
                        submitPost();
                    }
                });
    }

    public void snackBar(String message){
        Snackbar.make(postParentLayout,message,Snackbar.LENGTH_LONG).show();
    }


    public void BackButtonPostActivity(View view) {
        finish();
    }
}
