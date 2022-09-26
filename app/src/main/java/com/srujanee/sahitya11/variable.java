package com.srujanee.sahitya11;

public class variable {

    //Shared preference key names
    public static final String SP_IS_UPDATE_MANDATORY = "isUpdateMandatory";
    public static final int MANDATORY_UPDATE_CODE = 100 /*1st code with each mandatory update value will be increased by 100, and the notification
                                                         value will be  existing MANDATORY_UPDATE_CODE + 15*/;

    /* newer version should always be greater older version */
    public static final int SQL_VERSION_OLD = 6;
    public static final int SQL_VERSION_NEW = 7;


    public static final String OFFICIAL_USER_ID = "Srujanee";
    public static String USER_ID = "default";
    public static String USER_EMAIL_ID = "default";
    public static String USER_PROFILE_IMAGE_URI_AS_STRING ="default";
    public static String USER_PROFIE_SUBSCRIPTION = "default";
    public static final String USER_ID_POST_DETAIL = USER_ID +"_"+"Post";
    public static final String PROFILE_TIMESTAMP = "TIMESTAMP_PROFILE";
    public static final String PROFILE_POST_ID = "postId";
    public static final int RC_SIGN_IN = 123;
    public static final String USER_LIST = "userList";
    public static int USER_PROFILE_REFRESH_FLAG = 0;
    public static boolean isMainActivityOnResumeExecuted = false;


    public static int isFromTrending = 0;
    public static boolean flagReturnedFromViewContent = false;

    public static String TRY_CATCH_ERROR = "TryCatchError";


    public static final long INPUT_VALUE_ZERO = 0;
    public static final long INPUT_VALUE_ONE = 1;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    //-----------------------table details--------------------------------------------------

    //InputData Table details
    public static final String InputData_TABLE_NAME = "InputData";
    public static final String InputData_COLUMN_ID = "id";
    public static final String InputData_COLUMN_TITLE = "title";
    public static final String InputData_COLUMN_USER_ID = "author";
    public static final String InputData_COLUMN_CONTENT = "content";
    public static final String InputData_COLUMN_DATE = "date";
    public static final String InputData_COLUMN_REACTION = "reaction";
    public static final String InputData_COLUMN_TAG = "tag";
    public static final String InputData_COLUMN_SUB_CATEGORY = "subCategory";
    public static final String InputData_COLUMN_CATEGORY = "genre";
    public static final String InputData_COLUMN_TYPE = "type";
    public static final String InputData_COLUMN_EDITOR_CHOICE = "editorChoice";
    public static final String InputData_FIREBASE_TIMSTAMP = "inputDataTimeStamp";
    public static final String InputData_FIREBASE_TIMSTAMP_LAST_UPDATE = "inputDataTimeStampLastUpdate";

    //shortPostData table
    public static final String shortPostData_TABLE_NAME = "shortPostData";
    public static final String shortPostData_COLUMN_POST_ID = "postId";
    public static final String shortPostData_COLUMN_TEXT_POSITION = "textPosition";
    public static final String shortPostData_COLUMN_TEXT_COLOUR = "textColour";
    public static final String shortPostData_COLUMN_TEXT_SIZE = "textSize";
    public static final String shortPostData_COLUMN_IMAGE_CODE = "imageCode";
    public static final String shortPostData_FIREBASE_TIMSTAMP = "shortPostTimeStamp";
    public static final String shortPostData_COLUMN_SHORT_DESCRIPTION = "ShortDescription";
    public static final String shortPostData_COLUMN_USER_ID = "userId";

    //category table details
    public static final String categoryData_TABLE_NAME = "categoryData";
    public static final String categoryData_COLUMN_CATEGORY = "category";
    public static final String categoryData_COLUMN_INTEREST = "interest";
    public static final String categoryData_COLUMN_IMAGE_CODE = "imageCode";
    public static final String categoryData_COLUMN_TYPE = "type";
    public static final String categoryData_COLUMN_PRIORITY = "priority";
    public static final String categoryData_COLOUMN_CONTAINER = "container";
    public static final String categoryData_COLUMN_SUBCATEGORY = "subCategotry";
    public static final String categoryData_COLUMN_CATEGORY_ID = "categoryId";
    public static final String categoryData_FIREBASE_TIMSTAMP = "categoryTimeStamp";

    //subCategory table
    public static final String subCategory_TABLE_NAME = "subCategory";
    public static final String subCategory_COLUMN_SUBCATEGORY_NAME = "subCategoryName";
    public static final String subCategory_COLUMN_INTEREST = "interest";
    public static final String subCategory_COLUMN_PRIORITY = "priority";
    public static final String subCategory_COLUMN_IMAGE_CODE = "imageCode";
    public static final String subCategory_COLUMN_TYPE = "type";
    public static final String subCategory_FIREBASE_TIMSTAMP = "subCategoryTimeStamp";

    //Trending table
    public static final String trending_TABLE_NAME = "trending";
    public static final String trending_COLUMN_POST_ID = "postId";
    //Timestamp table
    public static final String timestamp_TABLE_NAME = "Timestamp";
    public static final String timestamp_COLUMN_LAST_UPDATED = "lastUpdated";
    public static final String timestamp_COLUMN_lastsubcategorytimestamp = "lastsubcategorytimestamp";
    public static final String timestamp_COLUMN_lastcategorytimestamp = "lastcategorytimestamp";
    public static final String timestamp_COLUMN_LAST_SHORTPOST_TIMESTAMP = "lastShortPostUpdated";
    public static final String timestamp_COLUMN_LAST_PROFILE_TIMESTAMP = "profileTimeStamp";

    //Timestamp Labels
    public static final String timestamp_LABEL_INPUTDATA_LASTUPDATED = "LASTUPDATED_INPUTDATA";
    public static final String timestamp_LABEL_lastSubCategorytimestamp = "LAST_SUB_CATEGORYUPDATE";
    public static final String timestamp_LABEL_lastCategorytimestamp = "LAST_CATEGORYUPDATE";
    public static final String timestamp_LABEL_LAST_SHORTPOST_UPDATED = "LAST_SHORTPOST_UPDATED";
    public static final String timestamp_LABEL_LAST_PROFILE_TIMESTAMP= "profileTimeStamp";

    //draft table details
    public static final String draft_TABLE_NAME = "draft";
    public static final String draft_COLUMN_ID = "id";
    public static final String draft_COLUMN_TITLE = "title";
    public static final String draft_COLUMN_AUTHOR = "author";
    public static final String draft_COLUMN_CONTENT = "content";
    public static final String draft_COLUMN_DATE = "date";
    public static final String draft_COLUMN_TAG = "tag";

    //UserData table
    public static final String userData_TABLE_NAME = "userData";
    public static final String userData_COLUMN_USER_ID = "userId";
    public static final String userData_COLUMN_USER_EMAIL_ID = "EmailId";
    public static final String userData_COLUMN_LEVEL = "level";
    public static final String userData_COLUMN_RANK = "rank";
    public static final String userData_COLUMN_DESCRIONTION = "description";
    public static final String userData_COLUMN_USER_NAME = "userName";
    public static final String userData_COLUMN_PROFILE_IMAGE_URI = "profileImageUri";
    public static final String userData_COLUMN_PROFILE_SUBSCRIPTION = "subcription";

    //user specific table details
    public static final String bookmark_TABLE_NAME = "bookmark";
    public static final String bookmark_COLUMN_ID = "bookmarkId";

    public static final String POST_LIKED_TABLE_NAME = "postLiked";
    public static final String POST_LIKED_COLUMN_NAME = "postId";

    public static final String GENRE_LIKED_TABLE_NAME = "genreLiked";
    public static final String GENRE_LIKED_COLUMN_NAME = "genreId";

    public static final String FOLLOWERS_TABLE_NAME = "followers";
    public static final String FOLLOWERS_COLUMN_NAME = "followersId";
    public static final String FOLLOWERS_FIREBASE_TIMESTAMP = "timestampFollower";

    public static final String FOLLOWING_TABLE_NAME = "following";
    public static final String FOLLOWING_COLUMN_NAME = "followingId";
    public static final String FOLLOWING_FIREBASE_TIMESTAMP = "timestampFollowing";

    public static final String MY_POST_TABLE_NAME = "myPost";
    public static final String My_POST_COLUMN_NAME = "myPostId";
    public static final String MY_POST_FIREBASE_POSTID = "postId";
    public static final String MY_POST_FIREBASE_TIMESTAMP = "timestampPostId";

    //image Subfolder table
    public static final String IMAGE_SUBFOLDER_TABLE_NAME = "imageSubFolder";
    public static final String IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID = "imageSubFolderId";
    public static final String IMAGE_SUBFOLDER_COLUMN_LOWER_LIMIT = "lowerLimit";
    public static final String IMAGE_SUBFOLDER_COLUMN_UPPER_LIMIT = "upperLimit";
    public static final String IMAGE_SUBFOLDER_COLUMN_EXISTING = "existing";

//........................................VARIABLE FOR CODING------------------------------------------
    //adapter code for main menu (main activity)
    final static String ADAPTER_CODE_TRIGGER = "adapterCode";
    final static String ADAPTER_CODE_TOTAL_FOLLOWING = "totalfollowing";
    final static String ADAPTER_CODE_TOTAL_FOLLOWER = "totalfollower";
    final static int ADAPTER_CODE_FAVORITE = 1;
    final static int ADAPTER_CODE_TREND = 2;
    final static int ADAPTER_CODE_ALL = 3;
    final static int ADAPTER_CODE_PERSONAL = 4;
    final static int ADAPTER_CODE_CATEGORY_LIST = 5;
    final static int ADAPTER_CODE_SUBCATEGORY_LIST = 6;
    final static int ADAPTER_CODE_VIEW_CONTENT =151;
    final static int ADAPTER_CODE_USRE_PROFILE = 152;
    final static int ADAPTER_CODE_USRE_PROFILE_LONG_POST = 153;
    final static int ADAPTER_CODE_USRE_PROFILE_SHORT_POST = 154;
    final static int ADAPTER_CODE_USER_PROFILE_FOLLOWING = 155;
    final static int ADAPTER_CODE_USER_PROFILE_FOLLOWER = 156;
    final static int ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER_OPTIONS = 157;
    final static int ADAPTER_CODE_TEXT_EDIT_IMAGE_SUBFOLDER = 158;
    final static int ADAPTER_CODE_TEXT_EDIT_STYLE_OPTION = 159;

    //adapter code to see content in  (topic list)
    final static int ADAPTER_CODE_OPEN = 10;
    final static int ADAPTER_CODE_OPEN_POEM = 11;
    final static int ADAPTER_CODE_OPEN_QUOTE = 11;
    final static int ADAPTER_CODE_OPEN_ARTICLE = 13;
    final static int ADAPTER_CODE_OPEN_STORY = 13;
    final static int ADAPTER_CODE_SHORT_WRITING = 50;
    final static int ADAPTER_CODE_LONG_WRITING = 51;
    final static int ADAPTER_CODE_CONTAINER_SHORT = 100;
    final static int ADAPTER_CODE_CONTAINER_LONG = 101;
    final static int ADAPTER_CODE_CONTAINER_OTHER = 102;


    //grid view code
    final static int GRIDVIEW_POST_GENRE = 500;
    final static int GRIDVIEW_POST_TAG = 501;

    //Data for each
    public static final int OPEN_ACTIVITY_QUOTE = 100;
    public static final int OPEN_ACTIVITY_POEM = 101;
    public static final int OPEN_ACTIVITY_ARTICLE = 102;
    public static final int OPEN_ACTIVITY_OPINION = 103;
    public static final int OPEN_ACTIVITY_STORY = 104;

    public static final int DEFAULT = -1;
    public static final long DEFAULT_LONG = -1;
    public static final String STRING_DEFAULT = "default";
    public static final String STRING_NOT_NULL = "notNull";
    public static final String STRING_ALL = "all";
    public static final String STRING_SHORT_POST = "short";
    public static final String STRING_LONG_POST = "long";
    public static final int POSITIVE_RESPONSE = 1000;

    //short name of intent
    public static final String ACTIVITY_CREATE_NEW = "create";
    public static final String ACTIVITY_MAIN = "main";
    public static final String ACTIVITY_TEXT_EDIT ="edit";
    public static final String ACTIVITY_TOPIC_LIST = "topic";
    public static final String ACTIVITY_VIEW_CONTENT = "LongView";
    public static final String ACTIVITY_USER_PROFILE = "profile";
    public static final String ACTIVITY_POST = "post";
    public static final String CARD_LAYOUT_HORIZONTAL = "Hcard";
    public static final String CARDS_LAYOUT = "cards";
    public static final String INNER_CARD_VIEW = "innerCard";
    public static final String SIGN_UP_ACTIVITY = "signUp";

    //TEXT ALIGNMENT CODE
    public static final int TEXT_CENTRE = 0;
    public static final int TEXT_LEFT_TOP = 1;
    public static final int TEXT_CENTRE_TOP = 2;
    public static final int TEXT_RIGHT_TOP = 3;
    public static final int TEXT_LEFT_CENTRE = 4;
    public static final int TEXT_RIGHT_CENTRE =5;
    public static final int TEXT_LEFT_BOTTOM = 6;
    public static final int TEXT_CENTRE_BOTTOM = 7;
    public static final int TEXT_RIGHT_BOTTOM = 8;

    //EditTex activity - gridView Code
    public static final int STYLE_TEXT_SIZE = 600;
    public static final int STYLE_TEXT_COLOUR = 601;
    public static final int STYLE_TEXT_BACKGROUND = 602;
    public static final int STYLE_TEXT_POSITION = 603;
    public static final int STYLE_TEXT_BACKGROUND_DEFAULT = 604;

    //filter to show data
    public static final int SHOW_TRENDING = 5000;
    public static final int SHOW_CATEGORY = 5001;
    public static final int SHOW_CATEGORY_SUBCATEGORY = 5002;
    //code for draft activity
    public static final int DRAFT_CODE = 5000;

    //triggeredFrom code for topic list
    public static final String TRIGGERED_FROM = "triggeredFrom";
    public static final int TRIGGERED_FROM_DRAFT = 10000;
    public static final int TRIGGERED_FROM_AUTHOR_SHORT_POST = 10001;
    public static final int TRIGGERED_FROM_AUTHOR_LONG_POST = 10003;
    public static final int TRIGGERED_FROM_MAIN_ACTIVITY = 10004;
    public static final int TRIGGERED_FROM_BOOKMARKS_SHORT =10005;
    public static final int TRIGGERED_FROM_BOOKMARKS_LONG =10006;
    public static final int TRIGGERED_FROM_TREND = 1007;
    public static final int TRIGGERED_FROM_DYNAMIC_LINK = 1008;
    public static final int TRIGGERED_FROM_USER_PROFILE = 1009;
    public static final int TRIGGERED_FROM_NAV_EDITOR_CHOICE = 1011;
    public static final int TRIGGERED_FROM_NAVIGATOR_WINDOW = 1012;
    public static final int TRIGGERED_FROM_NAV_TRENDING_LONG = 1013;
    public static final int TRIGGERED_FROM_NAV_TRENDING_SHORT = 1014;
    public static final int TRIGGERED_FROM_NAV_EDITOR_CHOICE_SHORT = 1015;
    public static final int TRIGGERED_FROM_NAV_EDITOR_CHOICE_LONG = 1016;
    public static final int TRIGGERED_FROM_DBHELPER = 1017;
    public static final int TRIGGERED_FROM_DEFAULT = 1018;
    public static final int TRIGGERED_FROM_TOD = 1019;
    public static final int TRIGGERED_FROM_USER_PROFILE_DATA = 1020;
    public static final int TRIGGERED_FROM_TOPICLIST_SEARCH= 1021;
    public static final int TRIGGERED_FROM_TOPICLIST_SUBCATEGORY_SEARCH= 1022;
    public static final int TRIGGERED_FROM_TOPICLIST_CATEGORY_SEARCH= 1023;

    public static final String OUR_IMAGE_POST = "postType";





    /* Image subfolder nam ( should be kept fixed ) */
    public static final String IMAGE_SUBFOLDER_DEFAULT = "default";



    public static final String LONG_POST = "Long Post";
    public static final String SHORT_POST = "Short Post";
    public static final String FUN_POST = "FUN";
    public static final String GENERAL_POST = "TOD";

    //This array list contains all the drawable images code
    // "THE ODER OF THIS ARRAY SHOULD NOT BE CHANGED AT ANY COST"//
    static final int[] drawableImageCode = {

           R.drawable.all_long,
           R.drawable.poetry_long,
           R.drawable.prose_long,
           R.drawable.ancient_long,

           R.drawable.all_short,
           R.drawable.quote_short,
           R.drawable.factual_short,
           R.drawable.phrase_short,
           R.drawable.poetry_short,
           R.drawable.prose_short,
    };

    static final int[] textSize = {
            15,
            20,
            25,
            30,
            35,
            40,
            45
    };

    static final int[] textSizeImage = {
            R.drawable.fontsize_15,
            R.drawable.fontsize_20,
            R.drawable.fontsize_25,
            R.drawable.fontsize_30,
            R.drawable.fontsize_35,
            R.drawable.fontsize_40,
            R.drawable.fontsize_45,
    };
    static final int[] textAlignment = {
            R.drawable.alignment_center,
            R.drawable.alignment_left_top_corner,
            R.drawable.alignment_top_center,
            R.drawable.alignment_right_top_corner,
            R.drawable.alignment_left_center,
            R.drawable.alignment_right_center,
            R.drawable.alignment_left_bottom_corner,
            R.drawable.alignment_bottom_center,
            R.drawable.alignment_right_bottom_corner
    };

    static final int[] textColour = {

            R.drawable.clrffffff,
            R.drawable.clr00a8f3,
            R.drawable.clr0ed145,
            R.drawable.clr3f48cc,
            R.drawable.clr8cfffb,
            R.drawable.clr88001b,
            R.drawable.clr585858,
            R.drawable.clrb83dba,
            R.drawable.clrb97a56,
            R.drawable.clrc3c3c3,
            R.drawable.clrc4ff0e,
            R.drawable.clrec1c24,
            R.drawable.clrfdeca6,
            R.drawable.clrff7f27,
            R.drawable.clrffaec8,
            R.drawable.clrffca18,
            R.drawable.clrfff200,
            R.drawable.clr000000
    };
    static final String[] textColourCode = {
            "ffffff",
            "00a8f3",
            "0ed145",
            "3f48cc",
            "8cfffb",
            "88001b",
            "585858",
            "b83dba",
            "b97a56",
            "c3c3c3",
            "c4ff0e",
            "ec1c24",
            "fdeca6",
            "ffaec8",
            "ff7f27",
            "ffca18",
            "fff200",
            "000000"
    };

    /* log tag variables */
    public static final String tag_mainActivity = "Main_Activity_Running";
    }




