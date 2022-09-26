package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class userProfile extends AppCompatActivity {
    //View variables
    TextView profileTotalPosts;
    TextView profileTotalLikes;
    TextView profileTotalFollowing;
    TextView profileTotalFollower;
    TextView tvProfileUserId;
    TextView tvProfileUserName;
    EditText etProfileUserDescription;
    ImageView imgProfileUserImage;
    TextView tvProfileUserLevel;
    LinearLayout profileParentLayout;
    //TextView tvProfileUserRank;
    ProgressBar profileProgressBar;

    static long cntTotalReaction=0;
    static long cntTotalShare = 0;
    static long cntTotalPost = 0;
    TextView tvFollowOrEdit;

    boolean isEditing = false;
    RecyclerView profileRecyclerView;
    RecyclerView profileRecyclerViewNested;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    static View.OnClickListener myOnClickListener;
    static View.OnLongClickListener myOnLongClickListener;

    static int selectedItemPositionNested = 0;
    static ArrayList<DataModel> dataLongPostsToForward = null;
    static ArrayList<DataModel> dataShortPostsToForward = null;
    static HashMap<String,shortPostDetails> dataShortPostsDetailsForward = null;
    ArrayList<DataModel> dataLongPost = null;
    ArrayList<DataModel> dataShortPost = null;
    ArrayList<DataWrapper> dataWrapperArrayList = null;

private ArrayList<profileDataModel> followers;
private    ArrayList<profileDataModel > followings;

     String profileUserId = variable.STRING_DEFAULT;
     String profileUserName;
     String profileUserEmail;
     String profileUserImage;
     String profileUserDescription;
    //local variables
    String triggerFrom;
    DBHelper myDb;
    Context context;
    String userIdToDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        try{
        // initialization
        Intent receiveIntent = getIntent();
        userIdToDisplay = receiveIntent.getStringExtra("userId");
        if(userIdToDisplay == null){
            Log.i("nullValue", "onCreate: "+userIdToDisplay);
            userIdToDisplay = variable.USER_ID;
        }

        Log.i("profile", "oncreate: ");
        context = this;
        myDb = new DBHelper(context);
        followers = new ArrayList<profileDataModel>();
        followings = new ArrayList<profileDataModel>();
        fetchProfileDataFirebase(userIdToDisplay); //TODO provide proper userId
        Intent intent = getIntent();
        triggerFrom = intent.getStringExtra(variable.TRIGGERED_FROM);

        // view initialization
        this.profileParentLayout = (LinearLayout) findViewById(R.id.profileParentLayout);
        this.profileTotalPosts = (TextView) findViewById(R.id.profileTotalPostCount);
        this.profileTotalLikes = (TextView) findViewById(R.id.profileTotalLikeCount);
        this.profileTotalFollowing = (TextView) findViewById(R.id.profileTotalFollowingCount);
        this.profileTotalFollower = (TextView) findViewById(R.id.profileTotalFollowerCount);
        this.profileRecyclerView = (RecyclerView) findViewById(R.id.profileRecyclerView);
        this.tvProfileUserId = (TextView) findViewById(R.id.profileUserId);
        this.tvProfileUserName = (TextView) findViewById(R.id.profileUserName);
        this.imgProfileUserImage = (ImageView) findViewById(R.id.profileAvatar);
        this.etProfileUserDescription = (EditText) findViewById(R.id.profileUserDescription);
        this.tvFollowOrEdit = (TextView) findViewById(R.id.profileFollowOrEdit);
        this.tvProfileUserLevel = (TextView) findViewById(R.id.profileLevel);
       // this.tvProfileUserRank = (TextView) findViewById(R.id.profileRank);
        this.profileProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);

        layoutManager = new LinearLayoutManager(this);
        profileRecyclerView.setLayoutManager(layoutManager);
        profileRecyclerView.setHasFixedSize(true);
        profileRecyclerView.setNestedScrollingEnabled(false);
        profileRecyclerView.setNestedScrollingEnabled(false);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.card_layout_horizontal, null);
        profileRecyclerViewNested = view.findViewById(R.id.nestedRecycler);

        myOnClickListener = new userProfile.MyOnClickListener(this);

        if(triggerFrom != null && triggerFrom.equals("forSignUp")){

        }else{

        /* set view data */
            //myDb.getDbRowCount(variable.MY_POST_TABLE_NAME)
        profileTotalPosts.setText(String.valueOf(0));
        profileTotalFollowing.setText(String.valueOf(0));
        profileTotalFollower.setText(String.valueOf(0));
        fetchPostIdFirebase(userIdToDisplay);
        }

        /* show if following of not */
        if(userIdToDisplay.equals(variable.USER_ID)){
               tvFollowOrEdit.setText("Edit");
        }else{
            if(myDb.isPositiveRelation(userIdToDisplay, variable.FOLLOWING_TABLE_NAME,variable.FOLLOWING_COLUMN_NAME)) {
                tvFollowOrEdit.setText("Following");
            }else{
                tvFollowOrEdit.setText("Follow");
            }
        }
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onCreateUserProfile", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            /* after returning from an activity it should reload the recycler view */
            Log.i("profilesssss", "onResume: ");
            /* if not signed in user should not be able to follow or edit */
            if (variable.USER_ID.equals(variable.STRING_DEFAULT)) {
                tvFollowOrEdit.setVisibility(View.GONE);
                snackBar("Please Sign In to follow");
            }
            if (dataWrapperArrayList != null) {
                if (profileUserId.equals(variable.USER_ID)) {
                    dataLongPost = myDb.getContentsByAuthor(variable.USER_ID, variable.ADAPTER_CODE_LONG_WRITING);
                    dataShortPost = myDb.getContentsByAuthor(variable.USER_ID, variable.ADAPTER_CODE_SHORT_WRITING);
                    dataWrapperArrayList = new ArrayList<DataWrapper>();
                    if(dataLongPost != null && dataLongPost.size() != 0) {

                        DataWrapper dataWrapper = new DataWrapper("my Long Post", dataLongPost, variable.DEFAULT, variable.DEFAULT);
                        dataWrapperArrayList.add(dataWrapper);
                    }
                    if(dataLongPost != null && dataShortPost.size() != 0) {
                        DataWrapper dataWrapper1 = new DataWrapper("my Short Post", dataShortPost, variable.DEFAULT, variable.DEFAULT);
                        dataWrapperArrayList.add(dataWrapper1);
                        Log.i("checkcc", "onResume: "+dataShortPost.size());
                    }
                }
                Log.e("checkcc", "onResume: "+dataLongPost.size());
                adapter = new CustomAdapterNested(dataWrapperArrayList, context, variable.ADAPTER_CODE_USRE_PROFILE);
                profileRecyclerView.setAdapter(adapter);
            }
            if (variable.USER_PROFILE_REFRESH_FLAG == 1) {

                fetchProfileDataFirebase(userIdToDisplay);
                Log.i("ssss", "onResume: ");
                variable.USER_PROFILE_REFRESH_FLAG = 0;
        }
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onResumeUserProfile", e.getMessage());
        }
    }

    public void postTotalPost(View view) {
    }

    public void postTotalLike(View view) {

    }

    public void postTotalFollowing(View view) {
        Intent intent = new Intent(context, userProfileData.class);
        intent.putExtra(variable.ADAPTER_CODE_TRIGGER, variable.ADAPTER_CODE_USER_PROFILE_FOLLOWING);
        intent.putExtra(variable.ADAPTER_CODE_TOTAL_FOLLOWING, followings);

        startActivity(intent);
    }

    public void postTotalFollowers(View view) {
        Intent intent = new Intent(context, userProfileData.class);
        intent.putExtra(variable.ADAPTER_CODE_TRIGGER, variable.ADAPTER_CODE_USER_PROFILE_FOLLOWER);
        intent.putExtra(variable.ADAPTER_CODE_TOTAL_FOLLOWER, followers);
        Bundle followerBundle=new Bundle();
        //followerBundle.putParcelableArrayList();
        startActivity(intent);
    }

    public void fetchProfileDataFirebase(String userId){
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        final long longDate = myDb.getTimeStamp(variable.timestamp_LABEL_LAST_PROFILE_TIMESTAMP);
        followings = new ArrayList<>();
        followers = new ArrayList<>();
        fdb.collection(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Success", document.getId() + " => " + document.getData());
                                /*-Code to insert data to category Data-*/
                                Log.e("Error: ", document.getString(variable.FOLLOWING_COLUMN_NAME)+"");
                                String followingId = document.getString(variable.FOLLOWING_COLUMN_NAME);
                                String followerId = document.getString(variable.FOLLOWERS_COLUMN_NAME);
                                if(followingId != null){
                                    followings.add(new profileDataModel(followingId));
                                }else if(followerId != null){
                                    followers.add(new profileDataModel(followerId));
                                }
                                //myDb.updateFollowers(document.getString(variable.FOLLOWERS_COLUMN_NAME));
                                //myDb.updateFollowingOnRefresh(document.getString(variable.FOLLOWING_COLUMN_NAME));
                            }
                            if(task.getResult().size() > 0) {
                                myDb.updateTimestamp(variable.timestamp_COLUMN_LAST_PROFILE_TIMESTAMP, new Date().getTime());
                            }
                            profileTotalFollowing.setText(String.valueOf(followings.size()));
                            profileTotalFollower.setText(String.valueOf(followers.size()));
                        } else {
                            Log.d("Failure", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public void fetchPostIdFirebase(String authorName)
    {
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        Log.d("PostId_Author",authorName);
        fdb.collection(variable.InputData_TABLE_NAME)
                .whereEqualTo(variable.InputData_COLUMN_USER_ID,authorName)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 dataLongPostsToForward = new ArrayList<DataModel>();
                 dataShortPostsToForward = new ArrayList<DataModel>();
                 dataLongPost = new ArrayList<DataModel>();
                 dataShortPost = new ArrayList<DataModel>();
                 dataWrapperArrayList = new ArrayList<DataWrapper>();

                if (task.isSuccessful()) {
                    cntTotalReaction=0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Success", document.getId() + " => " + document.getData());
                        if (!(document.getString(variable.InputData_COLUMN_CONTENT).equals(variable.STRING_DEFAULT))) {
                            cntTotalReaction = cntTotalReaction + document.getLong(variable.InputData_COLUMN_REACTION);
                            /*-Code to insert data to Input Data-*/
                            if (document.getLong(variable.InputData_COLUMN_TYPE) == variable.ADAPTER_CODE_LONG_WRITING) {

                                dataLongPostsToForward.add(new DataModel(
                                                document.getString(variable.InputData_COLUMN_ID),
                                                document.getString(variable.InputData_COLUMN_TITLE),
                                                document.getString(variable.InputData_COLUMN_USER_ID),
                                                document.getString(variable.InputData_COLUMN_CONTENT),
                                                document.getLong(variable.InputData_FIREBASE_TIMSTAMP),
                                                document.getString(variable.InputData_COLUMN_TAG),
                                                document.getLong(variable.InputData_COLUMN_REACTION),
                                                document.getLong(variable.InputData_COLUMN_EDITOR_CHOICE)
                                        )
                                );

                                dataLongPost.add(new DataModel(
                                        "profileLong",
                                        document.getString(variable.InputData_COLUMN_TITLE),
                                        document.getString(variable.InputData_COLUMN_CONTENT),
                                        "0",
                                        document.getString(variable.InputData_COLUMN_ID),
                                        document.getLong(variable.InputData_COLUMN_TYPE),
                                        document.getString(variable.InputData_COLUMN_USER_ID)

                                ));
                                Log.i("dataFrom", "onComplete: " + document.getString(variable.InputData_COLUMN_TITLE));
                            } else {

                                dataShortPostsToForward.add(new DataModel(document.getString(variable.InputData_COLUMN_ID),
                                        document.getString(variable.InputData_COLUMN_TITLE),
                                        document.getString(variable.InputData_COLUMN_USER_ID),
                                        document.getString(variable.InputData_COLUMN_CONTENT),
                                        document.getLong(variable.InputData_FIREBASE_TIMSTAMP),
                                        document.getString(variable.InputData_COLUMN_TAG),
                                        document.getLong(variable.InputData_COLUMN_REACTION),
                                        document.getLong(variable.InputData_COLUMN_EDITOR_CHOICE))
                                );

                                dataShortPost.add(new DataModel(
                                        "profileShort",
                                        document.getString(variable.InputData_COLUMN_TITLE),
                                        document.getString(variable.InputData_COLUMN_CONTENT),
                                        "0",
                                        document.getString(variable.InputData_COLUMN_ID),
                                        document.getLong(variable.InputData_COLUMN_TYPE),
                                        document.getString(variable.InputData_COLUMN_USER_ID)));
                            }
                          }
                        }
                        profileTotalLikes.setText(String.valueOf(task.getResult().size()));
                       if(dataLongPost != null && dataLongPost.size() != 0) {
                           DataWrapper dataWrapper = new DataWrapper("my Long Post", dataLongPost, variable.DEFAULT, variable.DEFAULT);
                           if (dataWrapper != null) {
                               dataWrapperArrayList.add(dataWrapper);
                           }
                       }
                       if(dataShortPost != null && dataShortPost.size() != 0) {
                           DataWrapper dataWrapper1 = new DataWrapper("my Short Post", dataShortPost, variable.DEFAULT, variable.DEFAULT);
                           if (dataWrapper1 != null) {
                               dataWrapperArrayList.add(dataWrapper1);
                           }
                       }
                        profileTotalLikes.setText(String.valueOf(cntTotalReaction));
                        cntTotalPost = dataLongPost.size() + dataShortPost.size();
                        profileTotalPosts.setText(String.valueOf(cntTotalPost));
                        /* get user level and rank */
                        //long userLevel = myDb.getUserLevel(userIdToDisplay,cntTotalPost, cntTotalReaction, cntTotalShare);
                        profileProgressBar.setVisibility(View.GONE);
                        adapter = new CustomAdapterNested(dataWrapperArrayList, context, variable.ADAPTER_CODE_USRE_PROFILE);
                        profileRecyclerView.setAdapter(adapter);

                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });

        Log.i("Author", "fetchPostIdFirebase: "+authorName);
        fdb.collection(variable.shortPostData_TABLE_NAME)
                .whereEqualTo(variable.shortPostData_COLUMN_USER_ID,authorName)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dataShortPostsDetailsForward = new HashMap<String, shortPostDetails>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Success", document.getId() + " => " + document.getData());
                        if (!document.getString(variable.shortPostData_COLUMN_SHORT_DESCRIPTION).equals(variable.STRING_DEFAULT)) {
                            /*-Code to insert data to ShortPost Data-*/
                            dataShortPostsDetailsForward.put(
                                    document.getString(variable.shortPostData_COLUMN_POST_ID),
                                    new shortPostDetails((document.getString(variable.shortPostData_COLUMN_IMAGE_CODE)),
                                    (long) (document.get(variable.shortPostData_COLUMN_TEXT_SIZE)),
                                    (String) (document.get(variable.shortPostData_COLUMN_TEXT_COLOUR)),
                                    (long) (document.get(variable.shortPostData_COLUMN_TEXT_POSITION)),
                                    document.getString(variable.shortPostData_COLUMN_SHORT_DESCRIPTION)));
                        }

                        Log.i("shortPost", "onComplete: " + dataShortPostsDetailsForward.size());
                        if (task.getResult().size() > 0) {
                            myDb.updateTimestamp(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED, new Date().getTime());
                        }
                    }
                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });

        fdb.collection(variable.USER_LIST)
                .whereEqualTo(variable.userData_COLUMN_USER_ID,authorName)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("SuccessProfile", document.getId() + " => " + document.getData());
                        profileUserEmail = document.getString(variable.userData_COLUMN_USER_EMAIL_ID);
                        profileUserId = document.getString(variable.userData_COLUMN_USER_ID);
                        profileUserImage =document.getString(variable.userData_COLUMN_PROFILE_IMAGE_URI);
                        profileUserName = document.getString(variable.userData_COLUMN_USER_NAME);
                        profileUserDescription = document.getString(variable.userData_COLUMN_DESCRIONTION);
                        /* todo - display subscription type */
                        tvProfileUserId.setText(profileUserId);
                        tvProfileUserName.setText(profileUserName);
                        etProfileUserDescription.setText(profileUserDescription);
                        etProfileUserDescription.setEnabled(false);
                        Picasso.get().load(profileUserImage)
                                .centerCrop()
                                .transform(new CircleTransform(150,0))
                                .fit()
                                .into(imgProfileUserImage);

                        //imgProfileUserImage.setImageURI(profileUserImage);

                    }

                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void followOrEdit(View view) {
        /* if the self profile is opened give edit option else give follow option */
        if(variable.USER_ID.equals(variable.STRING_DEFAULT)){
            tvFollowOrEdit.setVisibility(View.GONE);
        }else {
            if (!userIdToDisplay.equals(variable.USER_ID)) {
                /* if already following unfollow else follow*/
                if (myDb.isPositiveRelation(userIdToDisplay, variable.FOLLOWING_TABLE_NAME, variable.FOLLOWING_COLUMN_NAME)) {
                    tvFollowOrEdit.setText("Follow");
                    Log.i("follow", "followOrEdit: " + "follow");
                } else {
                    tvFollowOrEdit.setText("Following");
                    Log.i("follow", "followOrEdit: " + "following");
                }

                /* if follow option clicked, set refresh flag */
                myDb.updateFollowing(userIdToDisplay);
                variable.USER_PROFILE_REFRESH_FLAG = 1;
            /*SharedPreferences SpRefresh = getSharedPreferences("UserProfileSp",MODE_PRIVATE);
            SharedPreferences.Editor editSp = SpRefresh.edit();
            editSp.putInt(variable.USER_PROFILE_REFRESH_FLAG, 1);
            editSp.apply();*/


            } else if (isEditing) {
                //save
                tvFollowOrEdit.setText("Edit");
                isEditing = false;
                etProfileUserDescription.setEnabled(false);
                String getDescription = etProfileUserDescription.getText().toString();
                myDb.updateUserDataDescription(getDescription);
                snackBar("Updated...");
            } else {
                //edit
                tvFollowOrEdit.setText("Save");
                isEditing = true;
                etProfileUserDescription.setEnabled(true);
            }
        }
    }

    public void openLongPost(View view) {
        Intent intent = new Intent(context, topicList.class);
        intent.putExtra(variable.TRIGGERED_FROM, variable.TRIGGERED_FROM_USER_PROFILE);
        startActivity(intent);
    }

    public void BackButtonProfile(View view) {
        finish();
    }

    private class MyOnClickListener implements View.OnClickListener {
        Context context;
        RecyclerView.ViewHolder viewHolderNested;
        public MyOnClickListener(Context parentContext){
            context = parentContext;
        }

        @Override
        public void onClick(View view) {

            selectedItemPositionNested = profileRecyclerViewNested.getChildAdapterPosition(view);
            viewHolderNested = profileRecyclerViewNested.findViewHolderForAdapterPosition(selectedItemPositionNested);

            TextView outerTextview = (TextView)view.findViewById(R.id.innerTopicOuter);
            TextView innerTextview = (TextView)view.findViewById(R.id.innerHeader);
            TextView header = (TextView)view.findViewById(R.id.header);
            String outerTopic = outerTextview.getText().toString().toLowerCase();
            Log.i("profile", "onClick: "+outerTopic);
            Intent intent = new Intent(context, topicList.class);
            if(outerTopic.equals("profilelong")){
                intent.putExtra(variable.TRIGGERED_FROM, variable.ADAPTER_CODE_USRE_PROFILE_LONG_POST);
            }else{
                intent.putExtra(variable.TRIGGERED_FROM, variable.ADAPTER_CODE_USRE_PROFILE_SHORT_POST);
            }
            startActivity(intent);
        }

}
    public void snackBar(String message){
        Snackbar.make(profileParentLayout,message,Snackbar.LENGTH_LONG).show();
    }
}