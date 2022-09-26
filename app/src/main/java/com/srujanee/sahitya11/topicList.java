package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class topicList extends AppCompatActivity {

    /* view declaration */
    static int threadCompletionCount = 0;
    private static RecyclerView recyclerView;
    RecyclerView subCategoryListRecycler;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.Adapter adapterSubCategoryRecycler;
    private int recyclerViewTargetPosition = 0;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManagerSubCategoryRecycler;
   // GridView topicListGridView;
    Switch tvLongShortSwitch;
    androidx.appcompat.widget.SearchView searchView;
    FloatingActionButton fab;
    TextView deleteBin;
    TextView tvCategorySelection;
    RelativeLayout topicListParentLayout;
    static Context context;
    SwipeRefreshLayout pullTorefresh;
    /* Arraylists declaration */
    static ArrayList<DataModel> data;
    ArrayList<draftModel> dataSetDraft;
    static HashMap<String, shortPostDetails> shortDetails;
    ArrayList<String> searchListBuffer;
    ListView searchListView;
    ArrayList<String> subCategoryList;
    ArrayList<String> CategoryList;
    Spinner filterSpinner;
    ArrayList<String> spinnerList;
    TextView searchIcon;


    /* global variable declaration */
    static private int SPLASH_TIME_OUT = 2000;
    static int adapterCode;
    String innerTopic;

    String outerTopic;
    Intent myIntent;
    String postIdFromDynamicLink;
    int triggeredFrom;
    static public String idOnLongPress;     /* stores the id after long press only for draft and post */
    DBHelper myDb;
    BroadcastReceiver broadcastReceiver;
    customAdapterSearchListView listAdapter;
    String searchedText = variable.STRING_DEFAULT;
    String subCategorySearched = variable.STRING_DEFAULT;

    /* activity flags */
            /* Used to finish the activity if posted successfully in post Activity */
    public static boolean isFromTrending = false;
    private boolean isRunning;

    /* onclick listeners */
    static View.OnClickListener myOnClickListener;
    static View.OnClickListener myOnClickListenerSearchRecycler;
    static View.OnLongClickListener myOnLongClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);
        try{

        /* Find view by Id */
        this.deleteBin = (TextView) findViewById(R.id.topicDeleteBin);
        fab = findViewById(R.id.fab);
        this.topicListParentLayout = (RelativeLayout) findViewById(R.id.topicListParentLayout);
        this.recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        this.subCategoryListRecycler = (RecyclerView) findViewById(R.id.topicListRecyclerSubcategory);
        this.searchView = (androidx.appcompat.widget.SearchView) findViewById(R.id.topicListSearchView);
        this.searchListView = (ListView) findViewById(R.id.searchListView);
        this.searchListView.setElevation(35);
        this.searchListView.bringToFront();
        this.searchIcon = (TextView)findViewById(R.id.searchIcon);

        /* set layout manager for recycler view */
        layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setHasFixedSize(true);

        layoutManagerSubCategoryRecycler = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        this.subCategoryListRecycler.setLayoutManager(layoutManagerSubCategoryRecycler);
        this.subCategoryListRecycler.setItemAnimator(new DefaultItemAnimator());
        this.subCategoryListRecycler.setHasFixedSize(true);

        this.tvLongShortSwitch = (Switch) findViewById(R.id.topicLongShortSwitch);
       // this.topicListGridView = (GridView)findViewById(R.id.topicListGridView);
        filterSpinner = (Spinner) findViewById(R.id.topicListSpinner);
        tvCategorySelection = (TextView) findViewById(R.id.categorySelection);

        /* variable initialisation */
        broadcastReceiver = new broadCastReceiverTopicList();
        IntentFilter intentFilter = new IntentFilter("topicListIntentFilter");
        registerReceiver(broadcastReceiver, intentFilter);
        this.isRunning = true;
        this.context = this;
        this.myDb = new DBHelper(this);
        data = new ArrayList<DataModel>();
        pullTorefresh = findViewById(R.id.swipeTorefresh);
        /* OnClick listener initialisation */
        myOnClickListener = new topicList.MyOnClickListener(this);
        myOnClickListenerSearchRecycler = new topicList.myOnClickListenerSearchRecycler(this);
        myOnLongClickListener = new topicList.MyOnClickListener(this);


        /* Get intent data */
        myIntent = getIntent();
        triggeredFrom = myIntent.getIntExtra(variable.TRIGGERED_FROM, variable.DEFAULT);

        innerTopic = myIntent.getStringExtra("innerTopic");//word case dependency ..need to calibrate to a generalized format//todo
        outerTopic = myIntent.getStringExtra("outerTopic");
        postIdFromDynamicLink = myIntent.getStringExtra("postId");

        searchListBuffer = new ArrayList<>();
        searchListBuffer.add("hello");
        searchListBuffer.add("bye");
        searchListBuffer.add("sarigala");


        Log.i("spinnerData", "onCreate: "+outerTopic);
        if(triggeredFrom == variable.TRIGGERED_FROM_MAIN_ACTIVITY) {

               // filterSpinner.setVisibility(View.VISIBLE);
                spinnerList = new ArrayList<>();
                String splitted[] = innerTopic.split("_");
                if (outerTopic.equals(variable.LONG_POST.toLowerCase())) {
                    spinnerList = myDb.getCategoryNamesTemporary(variable.ADAPTER_CODE_CONTAINER_LONG);
                } else if (outerTopic.equals(variable.SHORT_POST.toLowerCase())) {
                    spinnerList = myDb.getCategoryNamesTemporary(variable.ADAPTER_CODE_CONTAINER_SHORT);
                }
                ArrayAdapter<String> dataAdapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filterSpinner.setAdapter(dataAdapter);
                try {
                    filterSpinner.setSelection(spinnerList.indexOf(innerTopic));
                }catch (Exception e){
                    Log.i(variable.TRY_CATCH_ERROR, "spinner  " +e.getMessage());
                }
        }

        /* activate the float button */
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose A Type");
                builder.setItems(new CharSequence[]
                                {"Short Writing(Eg: Quote,Thought,Short-Poem...)\n", "Long Writing(Eg:Article, Story, Opinion...)\n"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent shortIntent = new Intent(topicList.this, createNew.class);
                                        shortIntent.putExtra("writingType",variable.ADAPTER_CODE_SHORT_WRITING);
                                        startActivity(shortIntent);
                                        Toast.makeText(context, "Word Limit 100. Be YourSelf! Good Luck!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Intent longIntent = new Intent(topicList.this, createNew.class);
                                        longIntent.putExtra("writingType",variable.ADAPTER_CODE_LONG_WRITING);
                                        startActivity(longIntent);
                                        Toast.makeText(context, "No Word Limit. Be YourSelf! Good Luck!", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                builder.create().show();*/
                Intent shortIntent = new Intent(topicList.this, createNew.class);
                shortIntent.putExtra("writingType", variable.ADAPTER_CODE_SHORT_WRITING);
                startActivity(shortIntent);

            }
        });
        if (triggeredFrom == variable.TRIGGERED_FROM_MAIN_ACTIVITY) {
            TypedValue tv = new TypedValue();

            int actionBarHeight = 0;

            if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            pullTorefresh.setProgressViewOffset(false, 0, actionBarHeight);
            pullTorefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pullTorefresh.setEnabled(true);
                    if (!myDb.isConnectedToInternet()) {
                        snackBar("Please connect to internet...");
                        pullTorefresh.setRefreshing(false);
                    } else
                    {
                        fetchAllDataFirebase();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Refreshed", triggeredFrom + "");
                            {

                                adapterCode = getAdapterCodeForMainActivity(outerTopic, innerTopic);
                                data = myDb.getGenreContent(innerTopic.toLowerCase(), outerTopic);
                                callSetAdapter(adapterCode);
                                pullTorefresh.setRefreshing(false);
                                snackBar("Refreshed");
                                Log.i("Refreshed", "run:Complete ");
                            }
                        }
                    }, SPLASH_TIME_OUT);
                }

                }

            });
        }
        else{
            pullTorefresh.setEnabled(false);
        }
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchListView.setVisibility(View.VISIBLE);
                searchListBuffer = myDb.getSearchBufferData(adapterCode);
                listAdapter = new customAdapterSearchListView(searchListBuffer,context);
                searchListView.setAdapter(listAdapter);
                searchView.getLayoutParams().width = variable.screenWidth;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String txt=newText;
                listAdapter.filter(txt);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                searchView.setLayoutParams(params);

                searchView.setVisibility(View.GONE);
                searchIcon.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) view.findViewById(R.id.listViewTextView);
                 searchedText = String.valueOf(textView.getText());
                 triggeredFrom = variable.TRIGGERED_FROM_TOPICLIST_SEARCH;
                 onResume();
                 searchListView.setVisibility(View.GONE);
            }
        });


        /*topicListGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                 subCategorySearched = subCategoryList.get(position);
                triggeredFrom = variable.TRIGGERED_FROM_TOPICLIST_SUBCATEGORY_SEARCH;

                topicListGridView.setVisibility(View.GONE);
                Log.i("failee", "onItemClick: "+"csss");
                onResume();
            }
        });*/

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


               if(spinnerList.get(position).contains("all") == false) {
                   triggeredFrom = variable.TRIGGERED_FROM_TOPICLIST_CATEGORY_SEARCH;

               }else{
                   triggeredFrom = variable.TRIGGERED_FROM_MAIN_ACTIVITY;
               }
                innerTopic = spinnerList.get(position);
                setSubCategoryList(innerTopic);
                onResume();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvLongShortSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                /* checked = long writing, unchecked = short post */

                    if(triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_SHORT){
                        triggeredFrom = variable.TRIGGERED_FROM_NAV_TRENDING_LONG;
                    }
                    else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG){
                        triggeredFrom = variable.TRIGGERED_FROM_NAV_TRENDING_SHORT;
                    }

                    else if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_SHORT){
                        triggeredFrom = variable.TRIGGERED_FROM_BOOKMARKS_LONG;
                    }
                    else if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_LONG){
                        triggeredFrom = variable.TRIGGERED_FROM_BOOKMARKS_SHORT;
                    }

                    else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT){
                        triggeredFrom = variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG;
                    }
                    else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG){
                        triggeredFrom = variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT;
                    }

                if(checked){
                    tvLongShortSwitch.setText("Long Writing");
                }else{
                    tvLongShortSwitch.setText("Short Writing");
                }
                    Log.i("Refresh1111", "longShortSwitch: ");
                    onResume();


            }
        });
        /* set search bar list - subCategories in recycler view */
        setSubCategoryList(innerTopic);



    }catch(Exception e){
        Log.e(variable.TRY_CATCH_ERROR +" onCreateTopicList", e.getMessage() );
    }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
        /* if the flags are true the intent should be finished on resume from other activity*/
        if (MainActivity.isPosted || isFromTrending) {
            /* if a post successfully uploaded then it should go to main activity from postActivity directly */
            isFromTrending = false;        /* to show the long post from trending, do not need to display the topicList activity*/
            finish();
        } else if (triggeredFrom == variable.TRIGGERED_FROM_TOPICLIST_CATEGORY_SEARCH) {
            adapterCode = getAdapterCodeForMainActivity(innerTopic, subCategorySearched);
            data = myDb.getGenreContent(innerTopic, outerTopic);
            callSetAdapter(adapterCode);
        } else if (triggeredFrom == variable.TRIGGERED_FROM_TOPICLIST_SUBCATEGORY_SEARCH) {

            /* inner topic holds the category name */
            if (outerTopic.equals(variable.SHORT_POST.toLowerCase()) || outerTopic.equals(variable.LONG_POST.toLowerCase())) {
                    adapterCode = getAdapterCodeForMainActivity(innerTopic, subCategorySearched);
                    data = myDb.getGenreContent(subCategorySearched, innerTopic);
                    callSetAdapter(adapterCode);
            }
            Log.i("failee", "onResume: " + outerTopic + " " + innerTopic);

        } else if (triggeredFrom == variable.TRIGGERED_FROM_TOPICLIST_SEARCH) {
            data = myDb.getSearchedData(searchedText, adapterCode);
            callSetAdapter(adapterCode);
        } else if (triggeredFrom == variable.TRIGGERED_FROM_DYNAMIC_LINK) {
            data = new ArrayList<DataModel>();
            isRunning = true;
            adapterCode = getAdapterCodeForMainActivity(outerTopic, innerTopic);                 /* adapterCode could be short or long */
            data = myDb.getGenreContentForDynamicLink(innerTopic.toLowerCase(), outerTopic, postIdFromDynamicLink);
            callSetAdapter(adapterCode);
            Log.i("DynamicLink", "onResume: " + DBHelper.postPositionForDynamicList + " " + outerTopic + " " + innerTopic);
        } else if (triggeredFrom == variable.TRIGGERED_FROM_MAIN_ACTIVITY) {

            data = new ArrayList<DataModel>();
            isRunning = true;

            //todo
            /* if clicked on trending row display only one post, else display the usual filtered list */
            if (outerTopic != null && outerTopic.equals("trending") || outerTopic.equals("followed")) {
                String postId = innerTopic;
                adapterCode = myDb.getCategoryType(postId);                                            /* type could be short or long */
                data = myDb.getGenreContent(postId);                                                   /* get post details related to the particular Id */

                /* if clicked item is of long type, directly open the viewContent activity else display shortPost on recycler view of this activity*/
                if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {
                    Intent intent = new Intent(context, View_Content.class);
                    intent.putExtra(variable.TRIGGERED_FROM, variable.TRIGGERED_FROM_TREND);
                    intent.putExtra("IndexNumber", 0);                                   /* index must be '0' as there should be only one item in data list */
                    startActivity(intent);
                } else {

                    callSetAdapter(adapterCode);
                }
            } else if (outerTopic != null && outerTopic.toLowerCase().equals(variable.LONG_POST.toLowerCase()) || outerTopic.toLowerCase().equals(variable.SHORT_POST.toLowerCase())) {
                Log.i("adapterChodeCheck", "onResume: " + outerTopic + " " + innerTopic);
                adapterCode = getAdapterCodeForMainActivity(outerTopic, innerTopic);                 /* adapterCode could be short or long */
                data = myDb.getGenreContent(innerTopic.toLowerCase(), outerTopic);
                if(variable.flagReturnedFromViewContent == true) { /* if returned from view content, the activity should not refresh as the scroll position matters */
                    variable.flagReturnedFromViewContent = false;
                }else {
                    callSetAdapter(adapterCode);
                }

                /* search option is enabled if triggred from main activity and not trending of followed*/
           //     searchView.setVisibility(View.VISIBLE);
                searchIcon.setVisibility(View.VISIBLE);
            } else { /* dynamic scroll handling*/
                adapterCode = MainActivity.dynamicScrollClickType;
                data = myDb.getGenreContentForDynamicScroll("TestScroll", adapterCode);
                callSetAdapter(adapterCode);
            }
        } else if (triggeredFrom == variable.ADAPTER_CODE_USRE_PROFILE_LONG_POST) {
            isRunning = true;
            adapterCode = variable.ADAPTER_CODE_LONG_WRITING;
            String author = userProfile.dataLongPostsToForward.get(0).getContentAuthor();
            if (author.equals(variable.USER_ID)) {
                data = myDb.getContentsByAuthorLongDatModel(author, adapterCode);
            } else {
                data = userProfile.dataLongPostsToForward;
            }
            callSetAdapter(adapterCode);
        } else if (triggeredFrom == variable.ADAPTER_CODE_USRE_PROFILE_SHORT_POST) {
            adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
            String author = userProfile.dataShortPostsToForward.get(0).getContentAuthor();
            if (author.equals(variable.USER_ID)) {
                data = myDb.getContentsByAuthorLongDatModel(author, adapterCode);
            } else {
                data = userProfile.dataShortPostsToForward;
            }
            isRunning = true;
            callSetAdapter(adapterCode);
        } else if (triggeredFrom == variable.TRIGGERED_FROM_DRAFT) {
            isRunning = true;
            setDraftList();
        } else if (triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_SHORT ||
                triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_LONG ||
                triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG ||
                triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_SHORT ||
                triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG ||
                triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT) {

            tvLongShortSwitch.setVisibility(View.VISIBLE);

            Log.i("traceBookmark", "topic list triggered from ");
            isRunning = true;
            setBookmarkList(triggeredFrom);
        }
      }
        catch(Exception e){
            Log.e(variable.TRY_CATCH_ERROR+" onResumeTopicList", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

   /* public void longShortSwitch(View view) {
        if(triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_SHORT){
            triggeredFrom = variable.TRIGGERED_FROM_NAV_TRENDING_LONG;
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG){
            triggeredFrom = variable.TRIGGERED_FROM_NAV_TRENDING_SHORT;
        }

        else if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_SHORT){
            triggeredFrom = variable.TRIGGERED_FROM_BOOKMARKS_LONG;
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_LONG){
            triggeredFrom = variable.TRIGGERED_FROM_BOOKMARKS_SHORT;
        }

        else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT){
            triggeredFrom = variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG;
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG){
            triggeredFrom = variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT;
        }
        Log.i("Refresh1111", "longShortSwitch: ");
        onResume();
    }*/



    public void getCategoryname(View view) {

       /* if(outerTopic.equals(variable.SHORT_POST.toLowerCase()) || outerTopic.equals(variable.LONG_POST.toLowerCase())){
            subCategoryList = myDb.getSubCategoryStringListForCategory(innerTopic);
            if(subCategoryList != null) {
                topicListGridView.setVisibility(View.VISIBLE);
                gridViewAdapterTopicList gridAdapter = new gridViewAdapterTopicList(context, subCategoryList, variable.ADAPTER_CODE_SUBCATEGORY_LIST);
                topicListGridView.setAdapter(gridAdapter);
            }

        }*/
        Log.i("subcategorysearch", "getCategoryname: "+outerTopic+" "+innerTopic);



    }

    public void topicListSpinner(View view) {
        CategoryList = new ArrayList<String>();
        //CategoryList = myDb.getCategoryNames(var)
    }

    public void BackButtonTopicList(View view) {
        finish();
    }

    public void activateSearchOption(View view) {
        searchView.setIconified(false);
        searchView.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);
    }

    private class myOnClickListenerSearchRecycler implements View.OnClickListener{
        Context context;
        int selectedTopicPosition;
        TextView lastSelectedTopicView = null;
        myOnClickListenerSearchRecycler(Context context){
            this.context = context;
        }

        @Override
        public void onClick(View view) {
            selectedTopicPosition = subCategoryListRecycler.getChildAdapterPosition(view);

            /* subcategry searched is an global data which is used in onResume after being set here */
            subCategorySearched = subCategoryList.get(selectedTopicPosition);
            if(selectedTopicPosition == 0){
                triggeredFrom = variable.TRIGGERED_FROM_MAIN_ACTIVITY;
            }else {
                triggeredFrom = variable.TRIGGERED_FROM_TOPICLIST_SUBCATEGORY_SEARCH;
            }
            view.findViewById(R.id.searchTitleNameSelected).setVisibility(View.VISIBLE);
            if(lastSelectedTopicView == null){
               /* do nothing */
            }else{
                lastSelectedTopicView.setVisibility(View.GONE);
            }
            lastSelectedTopicView = (TextView)view.findViewById(R.id.searchTitleNameSelected);

            onResume();
            Log.i("onclicksear", "onClick: "+subCategorySearched);
        }
    }

    private class MyOnClickListener implements View.OnLongClickListener,View.OnClickListener{

        int selectedItemPosition;
        RecyclerView.ViewHolder viewHolder;
        private final int doubleClickTimeout;
        private final Context context;
        private Handler handler;
        private long firstClickTime;

        public MyOnClickListener(Context context) {
            this.context = context;
            doubleClickTimeout = ViewConfiguration.getDoubleTapTimeout();
            firstClickTime = 0L;
            handler = new Handler(Looper.getMainLooper());
        }

        /* onLongClick is only functional for
        *  draft
        *  my short post
        *  my long post
        *  bookmark */
        @Override
        public boolean onLongClick(View view) {
            selectedItemPosition = recyclerView.getChildAdapterPosition(view);
            viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
            CardView card = (CardView) viewHolder.itemView.findViewById(R.id.card_view);
            card.setBackgroundColor(getColor(R.color.orange));
            idOnLongPress = dataSetDraft.get(selectedItemPosition).getDraftId();
            deleteBin.setVisibility(View.VISIBLE);
            return true;
        }

        /* onClicklistener inbuilt function */
        @Override
        public void onClick(final View view) {

            Log.i("ssssss", "onSingleClick: ");
            selectedItemPosition = recyclerView.getChildAdapterPosition(view);
            viewHolder = recyclerView.findViewHolderForAdapterPosition(selectedItemPosition);
            long now = System.currentTimeMillis();

            /* condition to check SingleClick or doubleClick */
            if (now - firstClickTime < doubleClickTimeout) {
                handler.removeCallbacksAndMessages(null);
                firstClickTime = 0L;
                onDoubleClick(view, selectedItemPosition);
            } else {
                firstClickTime = now;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSingleClick(view, selectedItemPosition);
                        firstClickTime = 0L;
                    }
                }, doubleClickTimeout);
            }
        }

        /* handles operations on single click
         *  argument:
         *  view: holds the view of clicked layout
         *  selectedItemPositionLocal: click position */
        public void onSingleClick(View view, int selectedItemPositionLocal ){
            recyclerViewTargetPosition = selectedItemPosition;
            if(isRunning) {
                if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {
                    Intent intent = new Intent(context, View_Content.class);
                    intent.putExtra("IndexNumber", selectedItemPositionLocal);
                    startActivity(intent);
                    Log.i("longbookmark", "onSingleClick: ");
                }
                if (triggeredFrom == variable.TRIGGERED_FROM_DRAFT){
                    Intent intent = new Intent(context , createNew.class);
                    intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_DRAFT);
                    intent.putExtra("draftId", dataSetDraft.get(selectedItemPositionLocal).getDraftId());
                    intent.putExtra("title", dataSetDraft.get(selectedItemPositionLocal).getDraftTitle());
                    intent.putExtra("content", dataSetDraft.get(selectedItemPositionLocal).getDraftContent());
                    startActivity(intent);
                }
            }
        }

        /* handles operations on double click
        *  argument:
        *  view: holds the view of clicked layout
        *  selectedItemPositionLocal: click position */
        public void onDoubleClick(View view, int selectedItemPositionLocal) {
            TextView reaction
                    = (TextView) view.findViewById(R.id.reaction);
            TextView reactionCount
                    = (TextView) view.findViewById(R.id.reactionCount);
            selectedItemPositionLocal = recyclerView.getChildAdapterPosition(view);
            String id = data.get(selectedItemPositionLocal).getContentId();
            Log.i("hell", "onDoubleClick: " + id);
            //liked

            if(!myDb.isConnectedToInternet()){
                snackBar("Please connect to internet...");
            }
            else if (variable.USER_ID.equals(variable.STRING_DEFAULT)) {
                snackBar("Please Sign In to like the post");
            } else {
                if (myDb.isPositiveRelation(id, variable.POST_LIKED_TABLE_NAME, variable.POST_LIKED_COLUMN_NAME)) {
                    reaction.setBackgroundResource(R.drawable.reaction);
                    myDb.updateContentReaction(id, 0);
                    reactionCount.setText(myDb.getReactionCount(id) + "");
                    reaction.setFreezesText(true);
                }
                //disliked
                else {
                    reaction.setBackgroundResource(R.drawable.reaction_pink);
                    myDb.updateContentReaction(id, 1);
                    reactionCount.setText(myDb.getReactionCount(id) + "");
                    reaction.setFreezesText(false);
                }
                myDb.updatePostLiked(id);
            }
        }
    }
	
	 public void deletePostOrDraft(View view) {
        myDb.deleteDraft(idOnLongPress);                 /* deletes selected item */
        setDraftList();                                  /* refresh the list after deletion */
        deleteBin.setVisibility(View.GONE);
    }

    /* -------------------------------------user declared helper functions-------------------------------------------*/

    /* retrieves and sets the author's specific data
    *  Argument:
    *  author: name of author
    *  triggeredFrom: stores the triggering activity details */
    public void setBookmarkList(int triggeredFrom){

        if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_SHORT) {
            adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
            data = myDb.getBookMarkPosts( adapterCode);
           // snackBar("No Bookmarks");//todo - check if snack bar in callSetAdapter is sufficient or not
            Log.i("traceBookmark", "short filter  ");
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_BOOKMARKS_LONG) {
            adapterCode = variable.ADAPTER_CODE_LONG_WRITING;
            data = myDb.getBookMarkPosts( adapterCode);
           // snackBar("No Bookmarks");
            Log.i("traceBookmark", "long filter  ");
        }else if (triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG)
        {
            adapterCode = variable.ADAPTER_CODE_LONG_WRITING;
            data = myDb.getTrendingTopicList(variable.TRIGGERED_FROM_NAV_TRENDING_LONG);
            Log.i("tendingTrace", "topic list: ");
        }
        else if (triggeredFrom == variable.TRIGGERED_FROM_NAV_TRENDING_SHORT)
        {
            adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
            data = myDb.getTrendingTopicList(variable.TRIGGERED_FROM_NAV_TRENDING_SHORT);
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT){
            adapterCode = variable.ADAPTER_CODE_SHORT_WRITING;
            data = myDb.getEditorChoice(adapterCode);
        }
        else if(triggeredFrom == variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG){
            adapterCode = variable.ADAPTER_CODE_LONG_WRITING;
            data = myDb.getEditorChoice(adapterCode);
        }
        Log.i("traceBookmark", "filter  "+data.size()+" "+adapterCode);
        callSetAdapter(adapterCode);
    }

    /* helper function to get and display the draft data */
    public void setDraftList()
    {
        isRunning = true;
        dataSetDraft = new ArrayList<draftModel>();
        adapterCode = variable.TRIGGERED_FROM_DRAFT;
        dataSetDraft = myDb.getAllDraft();
        if(dataSetDraft == null || dataSetDraft.size() == 0){
            Snackbar.make(topicListParentLayout,"No Drafts",Snackbar.LENGTH_INDEFINITE).show();
        }else{
        callSetAdapter(adapterCode);
        }
    }

    /* sets the recycler view with proper adapter code
    * argument:
    *  adapterCode: this code helps in deciding the view of recycler view. eg. long, short, draft etc */
    public void callSetAdapter(int adapterCode) {
        if (adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {
            if( triggeredFrom == variable.ADAPTER_CODE_USRE_PROFILE_SHORT_POST && userProfile.dataShortPostsToForward.get(0).getContentAuthor().equals(variable.USER_ID) == false) {
                shortDetails = userProfile.dataShortPostsDetailsForward;         /* if from user profile then directly replace the arraylist */
            }
            else {
                shortDetails = myDb.getShortPostDetails(data);                   /* for shortPosts extra data related to text design needs to be loaded */
                Log.i("5", "short post fillers  "+shortDetails.size());
            }
            adapter = new CustomAdapter(data, shortDetails, this, variable.ADAPTER_CODE_SHORT_WRITING);
        }
        else if (adapterCode == variable.ADAPTER_CODE_LONG_WRITING) {
            adapter = new CustomAdapter(data, this, variable.ADAPTER_CODE_LONG_WRITING);
            Log.i("traceBookmark", "callSetAdapter: "+"long book mark");
        }
        else if (triggeredFrom == variable.TRIGGERED_FROM_DRAFT) {
            adapter = new CustomAdapter(dataSetDraft, variable.TRIGGERED_FROM_DRAFT, this);
        }
        recyclerView.setAdapter(adapter);
        if(data.size() == 0) {
            snackBar("No data available for this section...");
            //  topicListParentLayout.setBackground(context.getResources().getDrawable(R.drawable.default_inkspill,null));
        }

//        }else{
//            topicListParentLayout.setBackgroundColor(context.getResources().getColor(R.color.backGround,null));
//        }
        /* scroll list to target position */
        if (triggeredFrom == variable.TRIGGERED_FROM_DYNAMIC_LINK) {
          /*  RecyclerView.SmoothScroller smoothScroller = new
                    LinearSmoothScroller(context) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return LinearSmoothScroller.SNAP_TO_START;
                        }
                    };
            smoothScroller.setTargetPosition(DBHelper.postPositionForDynamicList);
            layoutManager.startSmoothScroll(smoothScroller);*/
            recyclerViewTargetPosition = DBHelper.postPositionForDynamicList;
        }
        if( triggeredFrom == variable.ADAPTER_CODE_USRE_PROFILE_SHORT_POST || triggeredFrom == variable.ADAPTER_CODE_USRE_PROFILE_LONG_POST){
            recyclerViewTargetPosition = userProfile.selectedItemPositionNested;
        }else{
            recyclerViewTargetPosition = MainActivity.ScrollClickPosition;
        }

        RecyclerView.SmoothScroller smoothScroller = new
                LinearSmoothScroller(context) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
        smoothScroller.setTargetPosition(recyclerViewTargetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    /* returns the post type that is short or long */
    public int getAdapterCodeForMainActivity(String outerTopic,String innerTopic)
    {
        int adaptercode = myDb.getCategoryType(outerTopic, innerTopic);
        return adaptercode;
    }


    /*---For Fetching Data From Firebase DB---*/
    void fetchAllDataFirebase()
    {
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        //Date currentTimeStamp=new Date();
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final  long strCurrentTimeStamp = new Date().getTime();
        //String lastLoginTimestamp= myDb.getTimeStamp("LASTLOGIN");
        //String lastUpdatedsubCategoryTimestamp= myDb.getTimeStamp("LASTSUBCATEGORYUPDATE");
        //String lastUpdatedCategoryTimestamp= myDb.getTimeStamp("LASTCATEGORYUPDATE");

        //long longDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(myDb.getTimeStamp(variable.timestamp_COLUMN_LAST_UPDATED))).getTime();
        final long longDate = myDb.getTimeStamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED);
        fdb.collection(variable.InputData_TABLE_NAME)
                .orderBy(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE )
                .whereGreaterThanOrEqualTo(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Success", document.getId() + " => " + document.getData());
                        /*-Code to insert data to Input Data-*/
                        SimpleDateFormat stringTodateformatter=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        myDb.insertContentOnRefresh(document.getString(variable.InputData_COLUMN_ID),
                                document.getString(variable.InputData_COLUMN_CATEGORY),
                                document.getString(variable.InputData_COLUMN_TITLE),
                                document.getString(variable.InputData_COLUMN_USER_ID),
                                document.getString(variable.InputData_COLUMN_CONTENT),
                                document.getLong(variable.InputData_FIREBASE_TIMSTAMP),
                                document.getString(variable.InputData_COLUMN_TAG),
                                document.getString(variable.InputData_COLUMN_SUB_CATEGORY),
                                document.getLong(variable.InputData_COLUMN_TYPE),
                                document.getLong(variable.InputData_COLUMN_REACTION),
                                document.getLong(variable.InputData_COLUMN_EDITOR_CHOICE)
                        );

                        boolean isRowExists = myDb.rowIdExists(document.getString(variable.InputData_COLUMN_ID), variable.InputData_TABLE_NAME, variable.InputData_COLUMN_ID);
                        if(isRowExists){
                            myDb.updateContentReactionOnRefresh(document.getString(variable.InputData_COLUMN_ID), document.getLong(variable.InputData_COLUMN_REACTION));
                        }else {

                        }

                        Log.i("Timetrack", "onComplete: from FB"+ document.getLong(variable.InputData_COLUMN_DATE)+
                                "\n  localtime"+ longDate);

                    }
                    Log.i("DownloadCount", "onComplete: "+ task.getResult().size());
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED, new Date().getTime());
                    }
                    threadCompletionCount++;
                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });

        fdb.collection(variable.shortPostData_TABLE_NAME)
                .orderBy(variable.shortPostData_FIREBASE_TIMSTAMP)
                .whereGreaterThanOrEqualTo(variable.shortPostData_FIREBASE_TIMSTAMP,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Success", document.getId() + " => " + document.getData());
                        /*-Code to insert data to ShortPost Data-*/
                        myDb.insertShortPostDataOnRefresh(
                                document.getString(variable.shortPostData_COLUMN_USER_ID),
                                document.getString(variable.shortPostData_COLUMN_POST_ID),
                                document.getString(variable.shortPostData_COLUMN_IMAGE_CODE),
                                (long)(document.get(variable.shortPostData_COLUMN_TEXT_SIZE)),
                                (String)(document.get(variable.shortPostData_COLUMN_TEXT_COLOUR)),
                                (long)(document.get(variable.shortPostData_COLUMN_TEXT_POSITION)),
                                document.getString(variable.shortPostData_COLUMN_SHORT_DESCRIPTION)
                        );
                    }
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED, new Date().getTime());
                    }
                    threadCompletionCount++;
                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });
        fdb.collection(variable.subCategory_TABLE_NAME)
                .orderBy(variable.subCategory_FIREBASE_TIMSTAMP)
                .whereGreaterThanOrEqualTo(variable.subCategory_FIREBASE_TIMSTAMP,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Success", document.getId() + " => " + document.getData());
                        /*-Code to insert data to subCategory Data-*/
                        myDb.insertSubCategoryOnRefresh(document.getString(variable.subCategory_COLUMN_SUBCATEGORY_NAME),
                                document.getLong(variable.subCategory_COLUMN_INTEREST),
                                document.getString(variable.subCategory_COLUMN_IMAGE_CODE),
                                document.getLong(variable.subCategory_COLUMN_PRIORITY),
                                document.getLong(variable.subCategory_COLUMN_TYPE));
                    }
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_lastSubCategorytimestamp, new Date().getTime());
                    }
                    threadCompletionCount++;
                } else {
                    Log.d("Failure", "Error getting documents: ", task.getException());
                }
            }
        });
        fdb.collection(variable.categoryData_TABLE_NAME)
                .orderBy(variable.categoryData_FIREBASE_TIMSTAMP)
                .whereGreaterThanOrEqualTo(variable.categoryData_FIREBASE_TIMSTAMP,longDate)
                .limit(500)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Success", document.getId() + " => " + document.getData());
                                /*-Code to insert data to category Data-*/
                                myDb.insertCategoryDataOnRefresh(
                                        document.getLong(variable.categoryData_COLUMN_CATEGORY_ID),
                                        document.getString(variable.categoryData_COLUMN_CATEGORY),
                                        document.getLong(variable.categoryData_COLUMN_INTEREST),
                                        document.getString(variable.categoryData_COLUMN_IMAGE_CODE),
                                        document.getLong(variable.categoryData_COLUMN_TYPE),
                                        document.getString(variable.categoryData_COLUMN_SUBCATEGORY),
                                        document.getLong(variable.categoryData_COLUMN_PRIORITY),
                                        document.getLong(variable.categoryData_COLOUMN_CONTAINER));
                            }
                            if(task.getResult().size() > 0) {
                                myDb.updateTimestamp(variable.timestamp_COLUMN_lastcategorytimestamp, new Date().getTime());
                            }
                            threadCompletionCount++;

                        } else {
                            Log.d("Failure", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void setSubCategoryList(String innerTopicTemp){
        innerTopicTemp = innerTopic;
        if(outerTopic.equals(variable.SHORT_POST.toLowerCase()) || outerTopic.equals(variable.LONG_POST.toLowerCase())){
            subCategoryList = myDb.getSubCategoryStringListForCategory(innerTopicTemp);
            if(subCategoryList != null && !innerTopic.contains("all")) {
                adapterSubCategoryRecycler = new CustomAdapterSearchRecycler(subCategoryList,context);
                subCategoryListRecycler.setAdapter(adapterSubCategoryRecycler);
                subCategoryListRecycler.setVisibility(View.VISIBLE);
            }else{
                subCategoryListRecycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    class broadCastReceiverTopicList extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }

    }
    public void snackBar(String message){
        Snackbar.make(topicListParentLayout,message,Snackbar.LENGTH_LONG).show();
    }


}
