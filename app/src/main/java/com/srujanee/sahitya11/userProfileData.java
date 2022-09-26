package com.srujanee.sahitya11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class userProfileData extends AppCompatActivity {

    RecyclerView recyclerViewUserData;
    RecyclerView.Adapter adapter;
    ArrayList<profileDataModel> profileList;
    ArrayList<profileDataModel> followers;
    ArrayList<profileDataModel> following;

    private int adapterCode;
    RecyclerView.LayoutManager layoutManager;
    static View.OnClickListener myOnClickListener;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_data);
        try{

        context = this;
        myOnClickListener = new userProfileData.MyOnClickListener(context);
        recyclerViewUserData = (RecyclerView) findViewById(R.id.userDataRecycler);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerViewUserData.setLayoutManager(layoutManager);
        recyclerViewUserData.setHasFixedSize(true);
        recyclerViewUserData.setNestedScrollingEnabled(false);
        recyclerViewUserData.setNestedScrollingEnabled(false);

        profileList = new ArrayList<profileDataModel>();

        Intent intent = getIntent();
        adapterCode = intent.getIntExtra(variable.ADAPTER_CODE_TRIGGER, variable.DEFAULT);

        //profileList = userProfile.followings;
        Log.e("SIZEPROFFOLLOW",profileList.size()+"");
        if(adapterCode == variable.ADAPTER_CODE_USER_PROFILE_FOLLOWER){
            profileList =(ArrayList<profileDataModel>)getIntent().getSerializableExtra(variable.ADAPTER_CODE_TOTAL_FOLLOWER);
        }else if(adapterCode == variable.ADAPTER_CODE_USER_PROFILE_FOLLOWING){
            profileList =(ArrayList<profileDataModel>)getIntent().getSerializableExtra(variable.ADAPTER_CODE_TOTAL_FOLLOWING);
        }
        else
        {
            profileList =(ArrayList<profileDataModel>)getIntent().getSerializableExtra(variable.ADAPTER_CODE_TOTAL_FOLLOWING);
        }
        adapter = new customAdapterProfile(profileList, context,variable.TRIGGERED_FROM_USER_PROFILE_DATA);
        recyclerViewUserData.setAdapter(adapter);
        }catch(Exception e)
        {
            Log.e(variable.TRY_CATCH_ERROR + " onCreateProfileData", e.getMessage());
        }
    }

    public void BackButtonProfileData(View view) {
        finish();
    }

    private class MyOnClickListener implements View.OnClickListener {

        private int recyclerViewClickPosition;
        RecyclerView.ViewHolder viewHolder;
        String clickedUserId;
        public MyOnClickListener(Context context) {

        }

        @Override
        public void onClick(View view) {
            Log.i("clicked", "onClick: ");
           recyclerViewClickPosition =  recyclerViewUserData.getChildAdapterPosition(view);
           clickedUserId = profileList.get(recyclerViewClickPosition).getProfileUserId();
           Intent intent = new Intent(context, userProfile.class);
           intent.putExtra("userId", clickedUserId );
           startActivity(intent);
        }
    }
}