package com.srujanee.sahitya11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static RecyclerView mainRecyclerViewParent;              /* vertical recycler view */
    static RecyclerView mainRecyclerViewNested;              /* horizontal recycler view */
    ImageView imgUserImage;                                  /* user's image in navigator window*/
    TextView tvUserId;                                       /* Navigator window TextView */
    FloatingActionButton fab;                                /* writing option */
    MenuItem signOutOption;
    MenuItem signInOption;
    ProgressBar mainProgressBar;

    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView.Adapter adapter;
    static View.OnClickListener myOnClickListener;
    //drawer layout variable
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    androidx.appcompat.widget.Toolbar toolbar;

    /*dynamic scroll data */
    Map<String, ArrayList<DataModel>> catergoryToListMap;
    static int dynamicScrollClickType = variable.ADAPTER_CODE_SHORT_WRITING;
    static int ScrollClickPosition = 0;
    static int recyclerViewNestedTargetPosition = 0;         /* scrolling position, it is also accessed from customAdapterNested*/

    //Arraylists
    static ArrayList<DataModel> data;
    static ArrayList<DataModel> shortCategory;
    static ArrayList<DataModel> category;
    static ArrayList<DataModel> subCategoryList;
    static ArrayList<DataModel> trending;
    static ArrayList<DataModel> followedPost;
    static ArrayList<DataModel> funPost;
    static ArrayList<DataModel> generalPost;
    static ArrayList<DataWrapper> dataWrapper;


    //Variable declaration
    static String userEmail = variable.STRING_DEFAULT;
    static String enteredUserID;
    static String fireBaseUserID = variable.STRING_DEFAULT;
    static String userSubcription = variable.STRING_DEFAULT;
    String innerTopic;
    String outerTopic;
    String TAG = "permission";
    String postIdFromDynamicLink;
    public static boolean isPosted = false;
    static int onDownloadComplete = 0;
    DBHelper myDb;
    Context context;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(variable.tag_mainActivity, "onCreate: Entered");
        try{
        /*inflate card_layout_Horizontal layout*/
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.card_layout_horizontal, null);

        /* Find view by Ids */
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar);
        mainRecyclerViewParent = (RecyclerView) findViewById(R.id.parentRecycler);
        fab = (FloatingActionButton) findViewById(R.id.mainCreateOption);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.mainNavigator);
        mainRecyclerViewNested = (RecyclerView) view.findViewById(R.id.nestedRecycler);
        View navigationHeader = mNavigationView.getHeaderView(0);                  //get header view from navigation window
        imgUserImage = (ImageView) navigationHeader.findViewById(R.id.nav_header_imageView);
        tvUserId = (TextView) navigationHeader.findViewById(R.id.nav_header_userId);

        /* get view from navigator view */
        Menu menu = mNavigationView.getMenu();
        signOutOption = menu.findItem(R.id.nav_signOut);
        signInOption = menu.findItem(R.id.nav_signIn);

        /* setting layout parameters */
        layoutManager = new LinearLayoutManager(this);
        mainRecyclerViewParent.setLayoutManager(layoutManager);
        mainRecyclerViewParent.setHasFixedSize(true);
        context = this;
        myDb = new DBHelper(this);
        variable.isMainActivityOnResumeExecuted = false;

        /* set action bar */
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.drawer_white);
        actionbar.setDisplayHomeAsUpEnabled(true);

        /* initialize the user id */
        variable.USER_ID = myDb.getUserId();
        variable.USER_EMAIL_ID = myDb.getUserEmail();
        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();

        Log.i("SubscriptionCheck", "onCreate: " + myDb.getUserSubscription());
        Log.i("userId", "onCreate: " + myDb.getUserId() + " " + variable.USER_ID + " " + variable.STRING_DEFAULT);
        /* google sign */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        //get permissions
        isStoragePermissionGranted();

        /* if not logged in */
      /*  if(variable.USER_ID.equals(variable.STRING_DEFAULT)){
           signInOption.setVisible(true);
           signOutOption.setVisible(false);
            Log.i("check", "onCreate: "+"not signedin");
        }else{
            signOutOption.setVisible(true);
            signInOption.setVisible(false);
            Log.i("check", "onCreate: "+"signedIn");
        }*/


        /* set On click listeners */
        mNavigationView.setNavigationItemSelectedListener(this);
        myOnClickListener = new MainActivity.MyOnClickListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        SharedPreferences sp = getSharedPreferences("FILE_NAME", MODE_PRIVATE);
        /* This code gets executed only once after the installation of the application */
        if (!sp.contains("key")) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("key", 1);
            edit.apply();

            /* update the time stamp on installation of application */
            Calendar calendar = Calendar.getInstance();
            calendar.set(2020, 7, 1);        /* post after the date july-7th-2020 will be downloaded */
            long timeJuly2020 = calendar.getTimeInMillis();
            myDb.updateTimestamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED, timeJuly2020);

            /* if folder exists, delete and start fresh */
            File file = new File(context.getExternalFilesDir(null), "Srujanee");
            if (file.exists()) {
                file.delete();
                Log.i("mkdirs", "getAllImageDirectoryUri: ");
            }
            /* create user file */
            file.mkdirs();

            //todo remove
          //  myDb.insertImageSubFolderHelper("Basic", 0, 499, 0);

            File file1 = new File(context.getExternalFilesDir(null), "Srujanee/TOD");
            if (file1.exists()) {
                file1.delete();
            }
            file1.mkdirs();

            myDb.donloadImageFromFirebase(file1, "TOD", null);

            // myDb.insertImageSubFolderHelper("Bhakti", 500,999,0);
            // get data from share SharePreference

        /*   myDb.insertContent("user1", "poem", "The Last Ride Together", "Robert Browning", "This is a beautiful tribute to his life-long partner, Elizabeth Barrett Browning, at the end of their journey together.", new Date().getTime(), "fun","devotioal",variable.ADAPTER_CODE_SHORT_WRITING, 0);
            myDb.insertContent("user2", "quote", "A November Night", "Robert Browning", "A lovely discovery, Ms. Teasdale's passionate poetry resonates beautifully as a timeless expression of love.",  new Date().getTime(), "fun","love",variable.ADAPTER_CODE_SHORT_WRITING, 0);
            myDb.insertContent("user", "poem", "I Am Not Yours", "Robert Browning", "Oh, plunge me deep in love, put out my senses, leave me deaf and blind, swept by the tempest of your love, a taper in a rushing wind.", new Date().getTime(), "common","patriotic", variable.ADAPTER_CODE_SHORT_WRITING, 0);
            myDb.insertShortPostData("Robert Browning","user1","1",1,1,1,"bhal to- han");
            myDb.insertShortPostData("Robert Browning","user2","2",3,1,1,"bhal to- han");
            myDb.insertShortPostData("default","user","3",5,5,3, "bhal to- han");
            myDb.insertContent("user", "quote", "title4", "author4", "Hello how are you? What are you doing? what do you like. this is good",  new Date().getTime(), "fun","GoodWill", variable.ADAPTER_CODE_SHORT_WRITING, 0);
            myDb.insertShortPostData("default","user","1",1,3,1, "bhal to- han");
            myDb.insertContent("user5", "story", "ଜଣେ ତପସ୍ୱୀ ", "@ତପସ୍ୱୀ", "ଜଣେ ତପସ୍ୱୀ ତାଙ୍କ ଶିଷ୍ୟଗଣଙ୍କ ସହିତ ହମାଳୟ ପାଦଦେଶରେ କୁଟୀର ରଚନା କରି ରହୁଥାନ୍ତି । ପ୍ରତ୍ୟହ ଜପ ତପରେ ତାଙ୍କର ଜୀବନ ଭାରି ଶାନ୍ତିରେ କଟି ଯାଉଥାଏ ।\n" +
                    "\n" +
                    "ଏକଦା ତାଙ୍କର ଶିଷ୍ୟଗଣ ସହିତ ତପସ୍ୱୀ ତୀର୍ଥାଟନରେ ଯାଇ କାଶୀ ଧାମରେ ପହଁଚିଲେ । ହିମାଳୟରୁ ଆସିଥିବା ତପସ୍ୱୀଙ୍କର ଆଗମନର ଖବର କାଶୀ ନରେଶଙ୍କ ପାଖରେ ପହଁଚିଗଲା । ସୁତରାଂ ନରେଶ ତପସ୍ୱୀଙ୍କୁ ଭେଟି ତାଙ୍କର ସେବା କରିବା ନିମନ୍ତେ କିଛିଦିନ ରାଜପ୍ରାସାଦରେ ଅବସ୍ଥାନ କରିବା ପାଇଁ ନିମନ୍ତ୍ରଣ ଜଣାଇଲେ ।\n" +
                    "\n" +
                    "ରାଜାଙ୍କର ଅନୁରୋଧ ରକ୍ଷା କରି ତପସ୍ୱୀ ଏବେ ରାଜପ୍ରାସାଦ ଯାଇ ସେଠାରେ ରହିଲେ । କାଶୀ ନରେଶ ତାଙ୍କର ରହିବା ଖାଇବାର ସୁବନ୍ଦୋବସ୍ତ କରିଦେଲେ । ସାଧୁବାବା ଯେଭଳି ସାମାନ୍ୟତମ ଅସୁବିଧା ଭୋଗ ନ କରନ୍ତି, ସେଥିଲାଗି ରାଜକର୍ମଚାରୀ ମାନେ ସଜାଗ ରହିଥାନ୍ତି ।",  new Date().getTime(), "ଧର୍ମଶାସ୍ତ୍ର","devotional", variable.ADAPTER_CODE_LONG_WRITING, 1);
            myDb.insertContent("user6", "article", "title6", "author6", "Covid-19 – navigating the uncharted\n" +
                    "Fauci et al., 2020 – N. Engl. J. Med.\n" +
                    "\n" +
                    "An editorial published in late March outlines the results from previous research concerning SARS-CoV-2, which have since been used to inform the response to the outbreak, including travel restrictions. As current studies continue to shed light on the characteristics of this virus, this article highlights the need to understand the pathogenesis of COVID-19 in order to navigate our responses in this unchartered arena.\n" +
                    "\n" +
                    "Treatment of 5 critically ill patients with COVID-19 with convalescent plasma\n" +
                    "Shen et al., 2020 – JAMA\n" +
                    "\n" +
                    "A study conducted in Shenzhen (China) involved transfusing five critically ill COVID-19 patients with convalescent blood plasma in order to determine whether this was beneficial to their treatment. The researchers observed an improved clinical status in their patients as a result of the treatment but stated that these observations require further evaluation in clinical trials.\n" +
                    "\n" +
                    "Clinical course and risk factors for mortality of adult inpatients with COVID-19 in Wuhan, China: a retrospective cohort study\n" +
                    "Zhou et al., 2020 – Lancet\n" +
                    "\n" +
                    "You might also like:\n" +
                    "\n" +
                    "COVID-19 research round-up: May\n" +
                    "COVID-19 research round-up: April\n" +
                    "COVID-19: Updates on vaccines and therapeutics\n" +
                    "A retrospective multicenter study analyzed data from 191 COVID-19 patients hospitalized in Wuhan (China) in January 2020, in order to identify risk factors for mortality and severity of illness. The researchers discovered that, among other factors, older age increased the likelihood of in-hospital death. Further, they observed that the median duration of viral shedding was 20 days.\n" +
                    "\n" +
                    "Characteristics of and important lessons from the coronavirus disease 2019 (COVID-19) outbreak in China\n" +
                    "Wu and McGoogan, 2020 – JAMA\n" +
                    "\n" +
                    "Published in late February, this article summarizes key findings from a large case series of COVID-19, published by the Chinese Center for Disease Control and Prevention (Beijing, China). The summary includes the epidemiologic characteristics of COVID-19 and a comparison with SARS and MERS.\n" +
                    "\n" +
                    "High temperature and high humidity reduce the transmission of COVID-19\n" +
                    "Wang et al., 2020 – SSRN\n" +
                    "\n" +
                    "Researchers calculated the reproductive number, R, for each Chinese city with more than 40 cases of the novel coronavirus. Using the daily R values from 21–23 January 2020 and linear regression modelling, they demonstrated that high temperature and high humidity could reduce the transmission of COVID-19, respectively. Though their results may seem to suggest that the arrival of summer and rainy season could reduce transmission of the virus, they also acknowledged that 80% of R-value fluctuations cannot be explained by temperature and humidity, therefore other measures are important for reducing transmission.\n" +
                    "\n" +
                    "Severe outcomes among patients with coronavirus disease 2019 (COVID-19)\n" +
                    "Bialek et al., 2020 – MMWR Morb. Mortal. Wkly. Rep.\n" +
                    "\n" +
                    "This report, published on 27 March 2020, uses preliminary data to describe outcomes among patients with COVID-19 in the United States as of mid-March 2020. Though the landscape continues to change as the virus spreads, the report outlines the implications for the disease in terms of severity and the need for social distancing.\n" +
                    "\n" +
                    "Temporal dynamics in viral shedding and transmissibility of COVID-19\n" +
                    "He et al., 2020 – Nature\n" +
                    "\n" +
                    "This study, published in Nature Medicine, investigated temporal patterns of viral shedding in 94 patients with COVID-19, as well as infectiousness profiles from a separate sample of transmission pairs. Using their results, the researchers estimated that a substantial number of cases were infected during the source’s presymptomatic stage.\n" +
                    "\n" +
                    "The incubation period of coronavirus disease 2019 (COVID-19) from publicly reported confirmed cases: estimation and application\n" +
                    "Lauer et al., 2020 – Ann. Intern. Med.\n" +
                    "\n" +
                    "In order to determine an estimated incubation period for COVID-19, researchers analyzed a pool of confirmed cases reported between 4 January – 24 February 2020 in areas outside Wuhan. Using this data, they estimated a median incubation period of 5.1 days and recommended 14 days’ quarantine for anyone potentially exposed to SARS-CoV-2.\n" +
                    "\n" +
                    "Compassionate use of remdesivir for patients with severe COVID-19\n" +
                    "Grein et al., 2020 – N. Engl. J. Med\n" +
                    "\n" +
                    "During the period from 25 January – 7 March 2020, a cohort of COVID-19 patients, hospitalized with low oxygen saturation, received a 10-day course of remdesivir. Researchers observed clinical improvement in 68% of those who completed the course, while also stating that ongoing trials of remdesivir therapy will be necessary in order to measure its efficacy.\n" +
                    "\n" +
                    "If you would like to stay up-to-date on COVID-19, check out our COVID-19 Hub.", new Date().getTime(),"common","covid", variable.ADAPTER_CODE_LONG_WRITING, 0);
            myDb.insertContent("user7", "story", "title7", "Robert Browning", "Hello how are you? What are you doing? what do you like. this is good",  new Date().getTime(), "fun","sport",variable.ADAPTER_CODE_LONG_WRITING, 0);
            myDb.insertContent("user8", "poem", "title8", "Robert Browning", "testing the length limit of the text view. It should not show more than one line of text",  new Date().getTime(), "fun","covid",variable.ADAPTER_CODE_LONG_WRITING, 1);
            myDb.insertShortPostData("default","user9","5",2,3,1,"bhal to- han");
            myDb.insertContent("user9", "opinion", "title9", "Robert Browning", "If this test passes then it should not show more than one line of text in the text view",  new Date().getTime(), "common","love",variable.ADAPTER_CODE_SHORT_WRITING, 0);
            myDb.insertContent("user10", "opinion", "title9", "Robert Browning", "If this test passes then it should not show more than one line of text in the text view",  new Date().getTime(), "common","devotional",variable.ADAPTER_CODE_LONG_WRITING, 0);

        myDb.insertCategoryData(101,"poetry",0,"1",variable.ADAPTER_CODE_LONG_WRITING, "relation:inspirational:philosophical:devotional:social",2,variable.ADAPTER_CODE_CONTAINER_LONG);
        myDb.insertCategoryData(102,"non fiction",0,"2",variable.ADAPTER_CODE_LONG_WRITING,"relation:inspirational:philosophical:devotional:social",3,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertCategoryData(103,"ancient",0,"3",variable.ADAPTER_CODE_LONG_WRITING,"devotional:cultural:historical",4,variable.ADAPTER_CODE_CONTAINER_LONG);

        myDb.insertCategoryData(500,"all short",0,"4",variable.ADAPTER_CODE_SHORT_WRITING,"other",1,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(501,"quote",0,"5",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:devotional:fact:odiaAndOdisha:cultural",2,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(502,"factual",0,"6",variable.ADAPTER_CODE_SHORT_WRITING,"fact",3,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(503,"phrase",0,"7",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:devotional:fact:odiaAndOdisha:cultural",4,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(504,"poem",0,"8",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:philosophical:devotional:social",5,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(505,"prose",0,"9",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:philosophical:devotional:social",6,variable.ADAPTER_CODE_CONTAINER_SHORT);

        myDb.insertSubCategory("relation",1,"6",1,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("inspirational",0,"0",2,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("philosophical",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("devotional",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("social",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("cultural",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("historical",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("odiaAndOdisha",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("fact",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("other",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);*/

            //  myDb.insertTrending("user");
         /*   myDb.insertCategoryData(100,"misc",0,"0",variable.ADAPTER_CODE_LONG_WRITING,"love",1,variable.ADAPTER_CODE_CONTAINER_LONG);
            myDb.insertCategoryData(101,"poetry",0,"1",variable.ADAPTER_CODE_LONG_WRITING, "love",2,variable.ADAPTER_CODE_CONTAINER_LONG);
            myDb.insertCategoryData(102,"prose",0,"2",variable.ADAPTER_CODE_LONG_WRITING,"love",3,variable.ADAPTER_CODE_LONG_WRITING);
            myDb.insertCategoryData(103,"ancient",0,"3",variable.ADAPTER_CODE_LONG_WRITING,"love",4,variable.ADAPTER_CODE_CONTAINER_LONG);

            myDb.insertCategoryData(500,"misc",0,"4",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",1,variable.ADAPTER_CODE_CONTAINER_SHORT);
            myDb.insertCategoryData(501,"quote",0,"5",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",2,variable.ADAPTER_CODE_CONTAINER_SHORT);
            myDb.insertCategoryData(502,"factual",0,"6",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",3,variable.ADAPTER_CODE_CONTAINER_SHORT);
            myDb.insertCategoryData(503,"phrase",0,"7",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",4,variable.ADAPTER_CODE_CONTAINER_SHORT);
            myDb.insertCategoryData(503,"poetry",0,"8",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",5,variable.ADAPTER_CODE_CONTAINER_SHORT);
            myDb.insertCategoryData(503,"prose",0,"9",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",6,variable.ADAPTER_CODE_CONTAINER_SHORT);
            //myDb.insertCategoryData(502,"all_long",0,"5",variable.ADAPTER_CODE_LONG_WRITING,"patriotic",5,variable.ADAPTER_CODE_CONTAINER_LONG);

           // myDb.insertSubCategory("sport",0,"3",4,variable.ADAPTER_CODE_SHORT_WRITING);
           // myDb.insertSubCategory("GoodWill",0,"5",6,variable.ADAPTER_CODE_SHORT_WRITING);
            myDb.insertSubCategory("love",1,"6",1,variable.ADAPTER_CODE_LONG_WRITING);
            myDb.insertSubCategory("devotional",0,"0",2,variable.ADAPTER_CODE_SHORT_WRITING);
            myDb.insertSubCategory("patriotic",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
            //myDb.insertSubCategory("covid",0,"4",5,variable.ADAPTER_CODE_LONG_WRITING);
*/

        }

        //  myDb.insertContent("user7", "story", "title7", "Robert Browning", "Hello how are you? What are you doing? what do you like. this is good",  new Date().getTime(), "fun","sport",variable.ADAPTER_CODE_LONG_WRITING, 0);

        //myDb.insertCategoryData(500,"all_short",0,"4",variable.ADAPTER_CODE_SHORT_WRITING,"devotional:patriotic",1,variable.ADAPTER_CODE_CONTAINER_SHORT);
        //myDb.insertCategoryData(500,"all_long",0,"4",variable.ADAPTER_CODE_LONG_WRITING,"devotional:patriotic",1,variable.ADAPTER_CODE_CONTAINER_LONG);


        //myDb.getAllImageDirectoryUri("default");
        /* set userId and image in navigator view */
       /* tvUserId.setText(variable.USER_ID);
        Picasso.get().load(variable.USER_PROFILE_IMAGE_URI_AS_STRING)
                .centerCrop()
                .transform(new CircleTransform(150,0))
                .fit()
                .into(imgUserImage);*/
/*
       myDb.insertCategoryData(500,"all_long",0,"0",variable.ADAPTER_CODE_LONG_WRITING,"misc",1,variable.ADAPTER_CODE_CONTAINER_LONG);
        myDb.insertCategoryData(101,"poetry",0,"1",variable.ADAPTER_CODE_LONG_WRITING, "relations:inspirationals:philosophy:devotion:communal",2,variable.ADAPTER_CODE_CONTAINER_LONG);
        myDb.insertCategoryData(102,"non fiction",0,"2",variable.ADAPTER_CODE_LONG_WRITING,"relations:inspirationals:philosophy:devotion:communal",3,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertCategoryData(103,"ancient",0,"3",variable.ADAPTER_CODE_LONG_WRITING,"devotion:culture:history",4,variable.ADAPTER_CODE_CONTAINER_LONG);

        myDb.insertSubCategory("relations",1,"6",1,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("inspirationals",0,"0",2,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("philosophy",1,"4",3,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("devotion",1,"4",4,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("communal",1,"4",5,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("culture",1,"4",6,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("history",1,"4",7,variable.ADAPTER_CODE_LONG_WRITING);
        myDb.insertSubCategory("misc",1,"4",8,variable.ADAPTER_CODE_LONG_WRITING);


        myDb.insertCategoryData(500,"all_short",0,"4",variable.ADAPTER_CODE_SHORT_WRITING,"other",1,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(501,"quote",0,"5",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:devotional:fact:odiaAndOdisha:cultural",2,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(502,"factual",0,"6",variable.ADAPTER_CODE_SHORT_WRITING,"fact",3,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(503,"phrase",0,"7",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:devotional:fact:odiaAndOdisha:cultural",4,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(504,"poem",0,"8",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:philosophical:devotional:social",5,variable.ADAPTER_CODE_CONTAINER_SHORT);
        myDb.insertCategoryData(505,"prose",0,"9",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:philosophical:devotional:social",6,variable.ADAPTER_CODE_CONTAINER_SHORT);

        myDb.insertSubCategory("relation",1,"6",1,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("inspirational",0,"0",2,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("philosophical",1,"4",3,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("devotional",1,"4",4,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("social",1,"4",5,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("cultural",1,"4",6,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("historical",1,"4",7,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("odiaAndOdisha",1,"4",8,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("fact",1,"4",9,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertSubCategory("other",1,"4",10,variable.ADAPTER_CODE_SHORT_WRITING);
        myDb.insertContent("srujanee1", "quote", "ସ୍ବାଗତ", "Srujanee", "Srujanee କୁ ସ୍ୱାଗତ|",  new Date().getTime(), "common","odiaAndOdisha",variable.ADAPTER_CODE_LONG_WRITING, 1);

*/
            //myDb.insertCategoryData(505,"TestScroll1",0,"4",variable.ADAPTER_CODE_SHORT_WRITING,"",1,variable.ADAPTER_CODE_CONTAINER_OTHER);
            ///  myDb.insertContent("ssss33s", "TestScroll1", "The Last Ride Together", "Robert Browning", "This is a beautiful tribute to his life-long partner, Elizabeth Barrett Browning, at the end of their journey together.", new Date().getTime(), "fun","",variable.ADAPTER_CODE_LONG_WRITING, 0);
            // myDb.insertContent("ssss", "TestScroll1", "A November Night", "Robert Browning", "A lovely discovery, Ms. Teasdale's passionate poetry resonates beautifully as a timeless expression of love.",  new Date().getTime(), "fun","",variable.ADAPTER_CODE_SHORT_WRITING, 0);
            //   myDb.insertShortPostData("Robert Browning","ssss","1",1,1,1,"bhal to- han");

          // myDb.insertCategoryData(505,"sri jagannath",0,"9",variable.ADAPTER_CODE_SHORT_WRITING,"relation:inspirational:philosophical:devotional:social",6,variable.ADAPTER_CODE_CONTAINER_SHORT);

          // myDb.insertSubCategory("relation",1,"6",1,variable.ADAPTER_CODE_SHORT_WRITING);
        fetchAllDataFirebase();       /*For Syncing Local database with Firebase data*/
        createOptionOnclick();       /* floating option trigger to open writing window */

    }catch(Exception e){
            Log.e(variable.TRY_CATCH_ERROR +" onCreateMainActivity", e.getMessage() );
        }
    }

    /* fetching data and initialisation of recycler view should be defined inside onResume */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(variable.tag_mainActivity, "onResume: entered and waiting for resume logic to complete ");
            try {
                /* clear trending check variable */
                variable.isFromTrending = 0;
                /* reset the adapter code, as the static variable might have been over written in other activities */
                CustomAdapter.adapterCode = variable.ADAPTER_CODE_CATEGORY_LIST;
                /* if not logged it */
                if (variable.USER_ID.equals(variable.STRING_DEFAULT)) {
                    signInOption.setVisible(true);
                    signOutOption.setVisible(false);
                    tvUserId.setText("Guest");
                    variable.USER_PROFILE_IMAGE_URI_AS_STRING = "https://lh3.googleusercontent.com/a-/AOh14GiDS1bXTB-uSrqwhDU5I_2pXNP1LoWd4pVpklij=s96-c";
                    Log.i("check", "onCreate: " + "not signedin");
                } else {
                    signOutOption.setVisible(true);
                    signInOption.setVisible(false);

                    tvUserId.setText(variable.USER_ID);
                    Log.i("check", "onCreate: " + "signedIn");
                }
                Picasso.get().load(variable.USER_PROFILE_IMAGE_URI_AS_STRING)
                        .centerCrop()
                        .transform(new CircleTransform(150, 0))
                        .fit()
                        .into(imgUserImage);

                if (isPosted) {
                    isPosted = false;
                }
                /* if downloaded successfully onResume will be triggered from Firebase threads. 4 = the number of firebase thread running in background */
                if (variable.isMainActivityOnResumeExecuted == false) {
                    if (onDownloadComplete == 4) {
                        Log.i(variable.tag_mainActivity, "onResume: logic completed. Main onResume code entered");
                        receiveDynamicLink(); // receive the link from shared link
                        variable.isMainActivityOnResumeExecuted = true;
                        mainProgressBar.setVisibility(View.GONE);
                        //java class and variable initialization
                        myDb.insertTrending();   // initialize trending IDs
                        data = new ArrayList<DataModel>();
                        subCategoryList = new ArrayList<DataModel>();
                        shortCategory = new ArrayList<DataModel>();
                        category = new ArrayList<DataModel>();
                        trending = new ArrayList<DataModel>();
                        dataWrapper = new ArrayList<DataWrapper>();

                        /* get data for trending category */
                        trending = myDb.getTrendingTopicList(variable.TRIGGERED_FROM_MAIN_ACTIVITY);
                        if (trending != null && trending.size() != 0) {
                            dataWrapper.add(new DataWrapper("Trending", trending, variable.DEFAULT, variable.DEFAULT));
                        }

                        /* get data for short categories */
                        shortCategory = myDb.getAllCategory(variable.ADAPTER_CODE_CONTAINER_SHORT);
                        if (shortCategory != null) {
                            dataWrapper.add(new DataWrapper(variable.SHORT_POST, shortCategory, variable.DEFAULT, variable.DEFAULT));
                        }

                        /* get data for followed post */
                        followedPost = myDb.getFollowedPost();
                        // Log.i("followedPost", "onResume: "+followedPost.get(0));
                        if (followedPost != null && followedPost.size() != 0) {
                            dataWrapper.add(new DataWrapper("Followed", followedPost, variable.DEFAULT, variable.DEFAULT));
                        }
                        /* get data for Long categories */
                        category = myDb.getAllCategory(variable.ADAPTER_CODE_CONTAINER_LONG);
                        if (category != null) {
                            dataWrapper.add(new DataWrapper(variable.LONG_POST, category, variable.DEFAULT, variable.DEFAULT));
                        }

                        /* get Data for FUN section */
                        funPost = myDb.getAllImageDirectoryUriMainActivity("FUN",null, null);
                        if (category != null) {
                            dataWrapper.add(new DataWrapper(variable.FUN_POST, funPost, variable.DEFAULT, variable.DEFAULT));
                        }
                        /* get Data for TOD section */
                        generalPost = myDb.getAllImageDirectoryUriMainActivity("TOD",null,null);
                        if(generalPost != null)
                        {
                            dataWrapper.add(new DataWrapper(variable.GENERAL_POST, generalPost, variable.DEFAULT, variable.DEFAULT));
                        }



                        /* get sub-category of every category */
/* DYNAMIC SCROLL CODE - TODO
            ArrayList<String> dynamicCategory = myDb.getAllDynamicCategory(variable.ADAPTER_CODE_CONTAINER_OTHER);
            // category = myDb.getAllCategory(variable.ADAPTER_CODE_CONTAINER_OTHER);
            catergoryToListMap = new HashMap<String, ArrayList<DataModel>>();
            // category = myDb.getContentsByAuthor(variable.USER_ID, variable.ADAPTER_CODE_SHORT_WRITING);
            for (String scrollName : dynamicCategory) {
                //subCategoryList = myDb.getSubCategoryForCategory(model.getInnerTopic());
                category = myDb.getAllCategoryForDynamicScroll(scrollName);
                dataWrapper.add(new DataWrapper(scrollName, category, variable.ADAPTER_CODE_LONG_WRITING, variable.ADAPTER_CODE_CONTAINER_OTHER));
                catergoryToListMap.put(scrollName.toLowerCase(), category);
            }*/



                        /* load the recycler view */
                        adapter = new CustomAdapterNested(dataWrapper, this, variable.ADAPTER_CODE_CATEGORY_LIST);
                        mainRecyclerViewParent.setAdapter(adapter);

                        /* set starting position for recycler view */
      /*  RecyclerView.SmoothScroller smoothScroller = new
                LinearSmoothScroller(context) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };
        smoothScroller.setTargetPosition(recyclerViewNestedTargetPosition);
        layoutManager.startSmoothScroll(smoothScroller);*/
                    }
                }
            } catch (Exception e) {
                Log.e(variable.TRY_CATCH_ERROR + " onResumeMainActivity", e.getMessage());
            }

}

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /* Click listener for navigation window item */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int menuId = menuItem.getItemId();
        if(menuId == R.id.nav_userProfile){
            if(!myDb.isConnectedToInternet()){
                snackBar("Please connect to internet...");
            }
            else if(variable.USER_ID.equals(variable.STRING_DEFAULT)){
                snackBar("Please Sign In...");
            }
            else {
                Intent intent = new Intent(this, userProfile.class);
                startActivity(intent);
            }
        }
        else if(menuId == R.id.nav_draft){
            Intent intent = new Intent(this,topicList.class);
            intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_DRAFT);
            startActivity(intent);
        }
        else if(menuId == R.id.nav_myBookMarks){
            Intent intent = new Intent(this,topicList.class);
            intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_BOOKMARKS_SHORT);
            startActivity(intent);
            Log.i("traceBookmark", "onNavigationItemSelected: ");
        }
        else if(menuId == R.id.nav_trending_short)
        {
            Intent intent = new Intent(this,topicList.class);
            intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_NAV_TRENDING_SHORT);
            Log.i("tendingTrace", "onNavigationItemSelected: ");
            startActivity(intent);
        }else if(menuId == R.id.nav_about)
        {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }
        else if(menuId == R.id.nav_Editor_Choice_Short){
            Intent intent = new Intent(this,topicList.class);
            intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT);
            Log.i("tendingTrace", "onNavigationItemSelected: ");
            startActivity(intent);
        }
        else if(menuId == R.id.nav_signOut){
            if(!myDb.isConnectedToInternet()){
                snackBar("Please connect to internet...");
            }
            else {
                signOut();
                variable.USER_ID = variable.STRING_DEFAULT;
                variable.USER_EMAIL_ID = variable.STRING_DEFAULT;
                variable.USER_PROFILE_IMAGE_URI_AS_STRING = variable.STRING_DEFAULT;
                variable.USER_PROFIE_SUBSCRIPTION = variable.STRING_DEFAULT;
                Toast.makeText(this, "Successfully Sign out", LENGTH_SHORT).show();
                signOutOption.setVisible(false);
                signInOption.setVisible(true);
            }
        }
        else if(menuId == R.id.nav_signIn){
            if(!myDb.isConnectedToInternet()){
                snackBar("Please connect to internet...");
            }else {
                signUpPopUp();
            }
        }else if(menuId == R.id.nav_share){
            generatePlayStoreLinkToShare();
        }

        return false;
    }

    public void generatePlayStoreLinkToShare() {
        String id = "Srujanee1";
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.srujani1.com/" + id + "/" + variable.TRIGGERED_FROM_DYNAMIC_LINK + "/"))
                .setDomainUriPrefix("https://srujani1.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Srujanee Odia")
                                .setDescription("ଓଡ଼ିଆ ଲୋକଙ୍କ ପାଇଁ ଏକ ଲେଖିବା ଏବଂ ପଢିବା ସମ୍ବନ୍ଧୀୟ ପ୍ଲାଟଫର୍ମ ଯାହା କିଛି ଉନ୍ନତମାନର ବୈଶିଷ୍ଟ୍ୟ ପ୍ରଦାନ କରୁଅଛି. ଓଡିଶାରେ ପ୍ରଥମ ଥର ପାଇଁ ଏହା ଏକ ଅଭିନବ ପ୍ରୟାସ ଯାହା ଆପଣଙ୍କୁ ଏକ ଡିଜିଟାଲ୍ ମଂଚରେ ନିଜର ଭାବନା ପରିପ୍ରକାଶ କରିବାରେ ସାହାଯ୍ୟ କରିବ |")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener((Activity)context, new OnCompleteListener<ShortDynamicLink>() {
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
                        }
                    }
                });
    }
    public void ThoughtOfTheDay(View view) {
       Intent intent = new Intent(context, tod.class);
       intent.putExtra(variable.OUR_IMAGE_POST, "TOD");
       startActivity(intent);
    }

    public void funPart(View view) {
        Intent intent = new Intent(context, tod.class);
        intent.putExtra(variable.OUR_IMAGE_POST, "FUN");
        startActivity(intent);
    }

    /* click listener for recycler views
       onSingleClick
       onDoubleClick  */
    private class MyOnClickListener implements View.OnClickListener {
    private final int doubleClickTimeout;
    private final Context context;
    private Handler handler;
    int selectedItemPositionParent = -1;
    int selectedItemPositionNested = -1;

    //view holders
    RecyclerView.ViewHolder viewHolderParent;
    RecyclerView.ViewHolder viewHolderNested;


    private long firstClickTime;

    public MyOnClickListener(Context context) {
        this.context = context;
        this.doubleClickTimeout = ViewConfiguration.getDoubleTapTimeout();
        this.firstClickTime = 0L;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /* Primary click inbuilt listener */
    @Override
    public void onClick(final View view) {
        selectedItemPositionNested = mainRecyclerViewNested.getChildAdapterPosition(view);
        viewHolderNested = mainRecyclerViewNested.findViewHolderForAdapterPosition(selectedItemPositionNested);

        selectedItemPositionParent = mainRecyclerViewParent.getChildAdapterPosition(view);
        viewHolderParent = mainRecyclerViewParent.findViewHolderForAdapterPosition(selectedItemPositionParent);

        Log.i("selectedposition", "onClick: parent"+selectedItemPositionParent+"  nested"+selectedItemPositionNested);
        long now = System.currentTimeMillis();
            /* condition to check single click or double click */
        if (now - firstClickTime < doubleClickTimeout) {
            handler.removeCallbacksAndMessages(null);
            firstClickTime = 0L;
            onDoubleClick(view);
        }
        else {
            firstClickTime = now;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onSingleClick(view, selectedItemPositionParent);
                    firstClickTime = 0L;
                }
            }, doubleClickTimeout);
        }
        selectedItemPositionNested = -1;
        }

        /* handles activity for recycler view single clicked */
        public void onSingleClick(View view, int clickedPosition){

            TextView outerTextview = (TextView)view.findViewById(R.id.innerTopicOuter);
            TextView innerTextview = (TextView)view.findViewById(R.id.innerHeader);
            TextView header = (TextView)view.findViewById(R.id.header);

            recyclerViewNestedTargetPosition = clickedPosition;
            Log.i("recycler", "onSingleClick: "+recyclerViewNestedTargetPosition);
            if(header != null) {
                String list = header.getText().toString().toLowerCase();

            }else if(innerTextview != null)
            {
                innerTopic = innerTextview.getText().toString().toLowerCase();
                outerTopic = outerTextview.getText().toString().toLowerCase();
                Log.i("check", "onSingleClick: "+innerTopic+" "+outerTopic);
                OpenActivity(innerTopic,outerTopic, clickedPosition);
            }
        }
        /* handles activity for recycler view double clicked */
        public void onDoubleClick(View view) {
            //todo not planned yet
        }
    }
    /*---For Fetching Data From Firebase DB---*/
 /*-----------------------------------User Defined Helper Functions ------------------------------------------------------------*/

    /* helper function that opens the activity on click */
    public void OpenActivity(String inner , String outer, int clickedPosition)
    {
        /*set click position to static variable - accessed by topic list */
        /*todo - implement scrolling position during dynamic scroll */
       // ScrollClickPosition = clickedPosition;
        /* if trending content clicked innerTopic should store the postId */
        if(outerTopic.toLowerCase().equals(variable.FUN_POST.toLowerCase()) ||
            outerTopic.toLowerCase().equals(variable.GENERAL_POST.toLowerCase())){ /* specific activity */
            Intent intent = new Intent(context, tod.class);
            intent.putExtra(variable.OUR_IMAGE_POST, outerTopic);
            intent.putExtra("SelectedPosition", clickedPosition);
            startActivity(intent);
            //Log.i("testttttttt", "OpenActivity: "+outerTopic);

        }else{ /* Activity related to topiclist.class */
        if(outer != null && outer.equals("trending")){
            inner = trending.get(clickedPosition).getTrendingPostId();
            variable.isFromTrending = 1;
        }else if(outer !=null && outer.equals("followed"))
        {
            inner = followedPost.get(clickedPosition).getTrendingPostId();
        }else if(outerTopic.equals(variable.SHORT_POST.toLowerCase()) || outerTopic.equals(variable.LONG_POST.toLowerCase())) {
            /* nothing special */
        }
        else{ /* dynamic scroll interaction */
            Log.i("testete", "OpenActivity: "+outerTopic);

            dynamicScrollClickType = (int)catergoryToListMap.get(outerTopic).get(clickedPosition).getPostType();
        }
        Intent intent = new Intent(this, topicList.class);
        intent.putExtra("innerTopic",inner);
        intent.putExtra("outerTopic",outer);
        intent.putExtra(variable.TRIGGERED_FROM,variable.TRIGGERED_FROM_MAIN_ACTIVITY);
        startActivity(intent);
     }
    }

    /*---For Fetching Data From Firebase DB---*/
      void fetchAllDataFirebase()
     {
         Log.i(variable.tag_mainActivity, "fetchAllDataFirebase:  Triggered");
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();

        //String lastLoginTimestamp= myDb.getTimeStamp("LASTLOGIN");
        //String lastUpdatedsubCategoryTimestamp= myDb.getTimeStamp("LASTSUBCATEGORYUPDATE");
       // String lastUpdatedCategoryTimestamp= myDb.getTimeStamp("LASTCATEGORYUPDATE");
        //long longDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(myDb.getTimeStamp(variable.timestamp_COLUMN_LAST_UPDATED))).getTime();

        final long longDate = myDb.getTimeStamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED);
        Log.i(variable.tag_mainActivity, "fetchAllDataFirebase:  trigger time "+longDate);

        /* update InputData table */
        fdb.collection(variable.InputData_TABLE_NAME)
                .orderBy(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE )
                .whereGreaterThanOrEqualTo(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(variable.tag_mainActivity, "Fetch InputData: " +document.getId() + " => " + document.getData());
                        /*-Code to insert data to Input Data table-*/
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
                    }
                    Log.i(variable.tag_mainActivity, "Fetch InputData Success: Download size "+ task.getResult().size());
                    /* update time stamp */
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED, new Date().getTime());
                    }
                } else {
                    Log.e(variable.tag_mainActivity, "Fetch InputData Failed:  ", task.getException());
                }
                //todo info: call sequence change   receiveDynamicLink();

                /* logic to trigger onResume */
                onDownloadComplete += 1;
                if(onDownloadComplete == 4) {
                    onResume();
                }
                Log.i(variable.tag_mainActivity+"t", "Fetch InputData Complete: onResume logic count"+ onDownloadComplete);
            }
        });

        /* update short post data table */
        fdb.collection(variable.shortPostData_TABLE_NAME)
                .orderBy(variable.shortPostData_FIREBASE_TIMSTAMP)
                .whereGreaterThanOrEqualTo(variable.shortPostData_FIREBASE_TIMSTAMP,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(variable.tag_mainActivity, "Fetch shortPostData" +document.getId() + " => " + document.getData());
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
                    /*update time stamp */
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED, new Date().getTime());
                    }
                    Log.i(variable.tag_mainActivity, "Fetch Short Post Data Success: Download size "+task.getResult().size());
                } else {
                    Log.e(variable.tag_mainActivity, "Fetch Short Post Data Failed: ", task.getException());
                }
                /* logic to trigger orResume */
                onDownloadComplete += 1;
                if(onDownloadComplete == 4) {
                    onResume();
                }
                Log.i(variable.tag_mainActivity+"t", "Fetch ShortPostData Complete: onResume logic count"+ onDownloadComplete);
            }
        });

        /* update subCategory name table */
        fdb.collection(variable.subCategory_TABLE_NAME)
                .orderBy(variable.subCategory_FIREBASE_TIMSTAMP)
                .whereGreaterThanOrEqualTo(variable.subCategory_FIREBASE_TIMSTAMP,longDate)
                .limit(500)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(variable.tag_mainActivity, "Fetch subCategory" +document.getId() + " => " + document.getData());
                        /*-Code to insert data to subCategory Data-*/
                        myDb.insertSubCategoryOnRefresh(document.getString(variable.subCategory_COLUMN_SUBCATEGORY_NAME),
                                document.getLong(variable.subCategory_COLUMN_INTEREST),
                                document.getString(variable.subCategory_COLUMN_IMAGE_CODE),
                                document.getLong(variable.subCategory_COLUMN_PRIORITY),
                                document.getLong(variable.subCategory_COLUMN_TYPE));
                    }
                    /* update time stamp */
                    if(task.getResult().size() > 0) {
                        myDb.updateTimestamp(variable.timestamp_LABEL_lastSubCategorytimestamp, new Date().getTime());
                    }
                    Log.i(variable.tag_mainActivity, "Fetch subCategory Success: Download size "+task.getResult().size());
                } else {
                    Log.e(variable.tag_mainActivity, "Fetch subCategory Failed: ", task.getException());
                }
                /* logic to trigger onResume */
                onDownloadComplete += 1;
                if(onDownloadComplete == 4) {
                    onResume();
                }
                Log.i(variable.tag_mainActivity+"t", "Fetch subCategory Complete: onResume logic count"+ onDownloadComplete);
            }
        });

        /* update category table */
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
                                Log.d(variable.tag_mainActivity, "Fetch categoty" +document.getId() + " => " + document.getData());
                                /*-Code to insert data to category Data-*/
                                myDb.insertCategoryDataOnRefresh(
                                        document.getLong(variable.categoryData_COLUMN_CATEGORY_ID),
                                        document.getString(variable.categoryData_COLUMN_CATEGORY),
                                        document.getLong(variable.categoryData_COLUMN_INTEREST),
                                        document.getString(variable.categoryData_COLUMN_IMAGE_CODE),
                                        document.getLong(variable.categoryData_COLUMN_TYPE),
                                        document.getString(variable.categoryData_COLUMN_SUBCATEGORY),
                                        document.getLong(variable.categoryData_COLUMN_PRIORITY),
                                        document.getLong(variable.categoryData_COLOUMN_CONTAINER)
                                );
                            }
                            if(task.getResult().size() > 0) {
                                Log.i("timestamp11", "onComplete: "+"");
                                myDb.updateTimestamp(variable.timestamp_COLUMN_lastcategorytimestamp, new Date().getTime());
                            }
                            Log.i(variable.tag_mainActivity, "Fetch category Success: Download size "+task.getResult().size());
                        } else {
                            Log.e(variable.tag_mainActivity, "Fetch category Failed: ", task.getException());
                        }

                        /*logic to trigger onResume */

                        onDownloadComplete += 1;
                        if(onDownloadComplete == 4) {
                            onResume();
                        }
                        Log.i(variable.tag_mainActivity+"t", "Fetch category Complete: onResume logic count"+ onDownloadComplete);
                    }
                });

         downloadImageFromFirebase("TOD");
         downloadImageFromFirebase("FUN");
        /* Download ImageCode */


    } /*---For Fetching Data From Firebase DB---*/
    public void downloadImageFromFirebase( final String subFolderId){

        final File localFile = new File(context.getExternalFilesDir(null),"Srujanee/"+subFolderId);
        if(!localFile.exists()){
            localFile.mkdirs();
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

                        Log.i(variable.tag_mainActivity+"t", "onSuccess:onDownloadComplete "+onDownloadComplete);
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


    /* if opening app through shared link, this function will catch the required data */
    public void receiveDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            String[] expectedData = deepLink.toString().split("/");

                            postIdFromDynamicLink = expectedData[3];
                            int triggerFromLocal = Integer.valueOf(expectedData[4]);
                            for (String s : expectedData) {
                                Log.i("string", "onSuccess: " + s);
                            }

                          /* if triggered from TOD directly open TOD intent */
                            if (triggerFromLocal == variable.TRIGGERED_FROM_TOD) {
                                Intent intentTod = new Intent(context,tod.class);
                                startActivity(intentTod);

                            } else {     /* open short post or long post from topic list */
                                String data[] = myDb.getInputDataForDynamicLink(postIdFromDynamicLink);
                                if (data != null) {
                                    outerTopic = data[0];
                                    innerTopic = data[1];
                                }
                                Intent intent = new Intent(getApplicationContext(), topicList.class);
                                intent.putExtra("innerTopic", innerTopic);
                                intent.putExtra("outerTopic", outerTopic);
                                intent.putExtra("postId", postIdFromDynamicLink);
                                intent.putExtra(variable.TRIGGERED_FROM, variable.TRIGGERED_FROM_DYNAMIC_LINK);
                                startActivity(intent);
                            }
                        }
                        Log.i("deepLink", "onSuccess: "+deepLink+" "+postIdFromDynamicLink);
                        Log.i("DynamicLink", "onComplete: "+outerTopic+" "+innerTopic+" "+postIdFromDynamicLink);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dynamic link", "getDynamicLink:onFailure", e);
                    }
                });
    }
    /* dialog box to give pen name */
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
    /* get runtime permission */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                //return true;
            }
            else
            {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //return false;
            }

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                //return true;
            }
            else
            {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                //return false;
            }
            return false;
        }
        else { //permission is automatically granted on sdk<23 upon installation

            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            }
    }
    /* google sign out code */
    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Log.i(TAG, "onComplete: signOut"+"kehbata");
                        snackBar("Successfully Signed Out");
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
        /* refresh ui name and profile picture */
        Picasso.get().load(variable.USER_PROFILE_IMAGE_URI_AS_STRING)
                .centerCrop()
                .transform(new CircleTransform(150,0))
                .fit()
                .into(imgUserImage);
        tvUserId.setText(variable.USER_ID);
        // [END auth_fui_signout]
    }

    /* sign in code */
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
                            Log.i("userEmail", "onComplete: "+userEmail+" "+user.getEmail());
                            if(userEmail.equals(variable.STRING_DEFAULT)){
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
                                                        userSubcription = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_PROFILE_SUBSCRIPTION);
                                                        Log.i("userId", "onComplete: "+userEmail);
                                                    }
                                                    Log.i("signInCondition", "userId: "+fireBaseUserID);
                                                    if(fireBaseUserID.equals(variable.STRING_DEFAULT)){
                                                        dbLocal.insertUserDataFireBase(enteredUserID,user.getDisplayName(),String.valueOf(user.getPhotoUrl()), user.getEmail(),"",0,variable.STRING_DEFAULT,userSubcription);
                                                        variable.USER_ID = myDb.getUserId();
                                                        variable.USER_EMAIL_ID = myDb.getUserEmail();
                                                        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
                                                        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();
                                                        Picasso.get().load(variable.USER_PROFILE_IMAGE_URI_AS_STRING)
                                                                .centerCrop()
                                                                .transform(new CircleTransform(150,0))
                                                                .fit()
                                                                .into(imgUserImage);
                                                        tvUserId.setText(variable.USER_ID);
                                                        signOutOption.setVisible(true);
                                                        signInOption.setVisible(false);
                                                        Log.i("signInCondition", "onComplete: "+"new user");
                                                        snackBar("Successfully Signed In");
                                                    }
                                                    else{
                                                        snackBar("Failed: Wrong Pen Name");
                                                        signOut();
                                                        Log.i("signInCondition", "onComplete: "+"wrong pen name");
                                                    }
                                                }
                                            }
                                        });
                                Log.i("signUp", "newUser: "+enteredUserID);
                            }
                            /* if unauthentic userId  sign out*/
                            else if(!(user.getEmail()).equals(userEmail))
                            {
                                Log.i("signInCondition", "onComplete: "+"unauthentic pen name");
                                snackBar("Failed: Wrong Pen Name");
                                signOut();
                            }
                            /* if existing user insert to local database */
                            else {
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
                                                        userSubcription = task.getResult().getDocuments().get(0).getString(variable.userData_COLUMN_PROFILE_SUBSCRIPTION);

                                                        Log.i("userId", "onComplete: " + userEmail);
                                                    }
                                                    if (enteredUserID.equals(fireBaseUserID)) {
                                                        Log.i("signUp", "onClick: " + userEmail);
                                                        Log.i("signInCondition", "onComplete: " + "existing user");
                                                        Log.i("signUp", "existing: " + enteredUserID);
                                                        dbLocal.insertUserData(enteredUserID, user.getDisplayName(), String.valueOf(user.getPhotoUrl()), "", 0, "default", userSubcription);
                                                        variable.USER_ID = myDb.getUserId();
                                                        variable.USER_EMAIL_ID = myDb.getUserEmail();
                                                        variable.USER_PROFILE_IMAGE_URI_AS_STRING = myDb.getUserImageUri();
                                                        variable.USER_PROFIE_SUBSCRIPTION = myDb.getUserSubscription();
                                                        Picasso.get().load(variable.USER_PROFILE_IMAGE_URI_AS_STRING)
                                                                .centerCrop()
                                                                .transform(new CircleTransform(150, 0))
                                                                .fit()
                                                                .into(imgUserImage);
                                                        tvUserId.setText(variable.USER_ID);

                                                        myDb.fetchFollowDataFirebase(variable.USER_ID);
                                                        myDb.insertPostIdFirebase(variable.USER_ID);
                                                        signOutOption.setVisible(true);
                                                        signInOption.setVisible(false);
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
                    }
                });
    }

    /*open createNew activity */
    public void createOptionOnclick(){
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shortIntent = new Intent(context, createNew.class);
                shortIntent.putExtra("writingType",variable.ADAPTER_CODE_SHORT_WRITING);
                startActivity(shortIntent);
            }
        });
        Log.i(variable.tag_mainActivity, "createOptionOnclick: executed");
    }

    public void snackBar(String message){
        Snackbar.make(drawerLayout,message,Snackbar.LENGTH_LONG).show();
    }

    /* Todo- to do database repair use this code  ( the code will be dead, use if needed )*/
//    public boolean updateFirebaseDatabase() {
//
//        /*----------------------------------------For Updating Data to Local DB----------------------------------------------------------*/
//
//        /*----------------------------------------For Updating Data to Firebase  DB----------------------------------------------------------*/
//        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
//
//
//        fdb.collection(variable.InputData_TABLE_NAME)
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isComplete())
//                {
//                    Log.i("test", "onComplete: 11" + task.getResult().getDocuments().get(0).getString(variable.InputData_COLUMN_ID));
//                    FirebaseFirestore updatefdb=FirebaseFirestore.getInstance();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        DocumentReference dr = updatefdb.collection(variable.InputData_TABLE_NAME).document(document.getId());
//                        Log.i("DocumentId", "onComplete: "+ document.getId());
//
//                            dr.update(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE, document.getLong(variable.InputData_FIREBASE_TIMSTAMP)).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                }
//                            });
//
//
//                    }
//                }
//            }
//        });
//
//        /*----------------------------------------For Updateng Data to Firebase DB----------------------------------------------------------*/
//
//
//        return true;
//    }


}
