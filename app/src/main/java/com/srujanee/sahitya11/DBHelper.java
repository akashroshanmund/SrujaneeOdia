package com.srujanee.sahitya11;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;

public class DBHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "writer.db";
    Context context;

    static int postPositionForDynamicList = variable.DEFAULT;
    //----------------------------------------------VARIABLE DECLARATION----------------------------------------


    public static final int InputData_LIKED = 1;
    public static final int InputData_DISLIKED = 0;
    private static ArrayList<DataModel> InputDataList;


    //--------------------------------------------END OF VARIABLE DECLARATION------------------------------------------

    //create or open DataBase
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, variable.SQL_VERSION_NEW);
        this.context = context;
    }

    //create tables inside database
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //table that stores all the information about content from the user

        db.execSQL(
                "create table IF NOT EXISTS InputData " +
                        "(id text primary key, genre text, title text, author text, content text, reaction integer DEFAULT 0, date long , tag text, subCategory text, type Integer, editorChoice long)"
        );
        //table to store all timestamps
        db.execSQL(
                "create table IF NOT EXISTS Timestamp " +
                        "(lastUpdated long DEFAULT 0,lastsubcategorytimestamp long DEFAULT 0,lastcategorytimestamp long DEFAULT 0, lastShortPostUpdated long DEFAULT 0, profileTimeStamp long DEFAULT 0)"
        );

        //short post details
        db.execSQL(
                "create table if not exists shortPostData " +
                        "(userId text, postId text primary key, textPosition long, imageCode text, textColour text, textSize long, ShortDescription text )"
        );

        //Category Table
        db.execSQL(
                "create table IF NOT EXISTS categoryData " +
                        "(categoryId integer, category text, interest integer DEFAULT 0, imageCode TEXT, type long, subCategotry text, priority long, container long )"
        );
        //subCategory Table
        db.execSQL(
                "create table IF NOT EXISTS subCategory " +
                        "(subCategoryName text, interest integer DEFAULT 0, imageCode text, priority integer,  type long )"
        );
        //Posts liked by the user Table
        db.execSQL(
                "create table IF NOT EXISTS postLiked " +
                        "( postId text )"
        );

        //category liked by suer Table
        db.execSQL(
                "create table IF NOT EXISTS genreLiked " +
                        "( genreId integer )"
        );

        //followers table
        db.execSQL(
                "create table IF NOT EXISTS followers " +
                        "(followersId text )"
        );

        //following table
        db.execSQL(
                "create table IF NOT EXISTS following " +
                        "(followingId text DEFAULT \"@ତପସ୍ୱୀ\")"
        );

        //Draft table
        db.execSQL(
                "create table IF NOT EXISTS draft " +
                        "( id text primary key, title text, content text, type integer, date DATETIME DEFAULT CURRENT_TIMESTAMP )"
        );

        //posts by user
        db.execSQL(
                "create table IF NOT EXISTS myPost" +
                        "(myPostId text primary key)"
        );

        //book mark my user
        db.execSQL(
                "create table IF NOT EXISTS bookmark" +
                        "(bookmarkId text primary key)"
        );

        //trending topic table
        db.execSQL(
                "create table IF NOT EXISTS trending"+
                        "(postId text)"
        );

        //User Data
        db.execSQL(
                "create table IF NOT EXISTS userData"+
                        "(userId text primary key, userName text, profileImageUri text, description text, EmailId text, level long, rank text, subcription text)"
        );

        db.execSQL(
                "create table IF NOT EXISTS imageSubFolder" +
                        "(imageSubFolderId text primary key, lowerLimit long, upperLimit long, existing long)"
        );
    }

    //reinstalling database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.i("Trigger", "onUpgrade: new version"+ newVersion + "  old version"+oldVersion);
        db.execSQL("DROP TABLE IF EXISTS InputData");
        db.execSQL("DROP TABLE IF EXISTS Timestamp");
        db.execSQL("DROP TABLE IF EXISTS shortPostData");
        db.execSQL("DROP TABLE IF EXISTS categoryData");
        db.execSQL("DROP TABLE IF EXISTS subCategory");
        db.execSQL("DROP TABLE IF EXISTS postLiked");
        db.execSQL("DROP TABLE IF EXISTS genreLiked");
        db.execSQL("DROP TABLE IF EXISTS followers");
        db.execSQL("DROP TABLE IF EXISTS following");
        db.execSQL("DROP TABLE IF EXISTS draft");
        db.execSQL("DROP TABLE IF EXISTS myPost");
        db.execSQL("DROP TABLE IF EXISTS bookmark");
        db.execSQL("DROP TABLE IF EXISTS trending");
        db.execSQL("DROP TABLE IF EXISTS userData");

        onCreate(db);
    }

    //LOCAL DATABASE ACTIVITIES

    //----------------------------------Table- InputData SECTION---------------------------------------------------------------------------


    //Insert new InputData to the database
    public boolean insertContent(String id, String genre, String title, String author, String content, long date, String tag, String subCategory, long type, long editorChoice) {

        int reactionInit=0;
        //new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.InputData_COLUMN_ID, id);
        contentValues.put(variable.InputData_COLUMN_CATEGORY, genre);
        contentValues.put(variable.InputData_COLUMN_TITLE, title);
        contentValues.put(variable.InputData_COLUMN_USER_ID, author);
        contentValues.put(variable.InputData_COLUMN_CONTENT, content);
        contentValues.put(variable.InputData_COLUMN_DATE, date);
		contentValues.put(variable.InputData_COLUMN_TAG, tag);
        contentValues.put(variable.InputData_COLUMN_SUB_CATEGORY,subCategory);
        contentValues.put(variable.InputData_COLUMN_TYPE, type);
        contentValues.put(variable.InputData_COLUMN_EDITOR_CHOICE, editorChoice);
        db.insert(variable.InputData_TABLE_NAME, null, contentValues);
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.InputData_COLUMN_ID, id);
        contentBuff.put(variable.InputData_COLUMN_CATEGORY, genre);
        contentBuff.put(variable.InputData_COLUMN_TITLE, title);
        contentBuff.put(variable.InputData_COLUMN_CONTENT, content);
        contentBuff.put(variable.InputData_COLUMN_USER_ID, author);

          //  new SimpleDateFormat("yyyy-MM-dd HH:mm")
         //           .parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date))
            contentBuff.put(variable.InputData_FIREBASE_TIMSTAMP, date );
            contentBuff.put(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE, date );

        contentBuff.put(variable.InputData_COLUMN_TAG, tag);
        contentBuff.put(variable.InputData_COLUMN_SUB_CATEGORY, subCategory);
		contentBuff.put(variable.InputData_COLUMN_TYPE, type);
		contentBuff.put(variable.InputData_COLUMN_REACTION, reactionInit);
        contentBuff.put(variable.InputData_COLUMN_EDITOR_CHOICE, editorChoice);

        fdb.collection("InputData")
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

        Log.i("TimeTrack", "insertContent: "+date);

        return true;
    }
    /* insert on refresh */
    public void insertContentOnRefresh(String id, String genre, String title, String author, String content, long date, String tag, String subCategory, long type, long reactions, long editorChoice ) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor checkCursor = db.rawQuery("Select * from InputData where id = '"+id+"'", null);

        /* check if an instance is already present or not */
        if(checkCursor.moveToFirst() == false) {
            /* if content is default then it is deleted and should not be inserted */
            if(content.equals(variable.STRING_DEFAULT) == false) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(variable.InputData_COLUMN_ID, id);
                contentValues.put(variable.InputData_COLUMN_CATEGORY, genre);
                contentValues.put(variable.InputData_COLUMN_TITLE, title);
                contentValues.put(variable.InputData_COLUMN_USER_ID, author);
                contentValues.put(variable.InputData_COLUMN_CONTENT, content);
                contentValues.put(variable.InputData_COLUMN_DATE, date);
                contentValues.put(variable.InputData_COLUMN_TAG, tag);
                contentValues.put(variable.InputData_COLUMN_SUB_CATEGORY, subCategory);
                contentValues.put(variable.InputData_COLUMN_TYPE, type);
                contentValues.put(variable.InputData_COLUMN_REACTION, reactions);
                contentValues.put(variable.InputData_COLUMN_EDITOR_CHOICE, editorChoice);
                db.insert(variable.InputData_TABLE_NAME, null, contentValues);
                db.close();
            }
           }
        else{
            SQLiteDatabase db1 = this.getWritableDatabase();
            /* if content default then the post has been deleted  and should also be deleted from local */
            if(content.equals(variable.STRING_DEFAULT) == true) {

                db1.execSQL("delete from InputData where id = '"+id+"'");
                Log.i("postDelete", "insertContentOnRefresh: ");

            }
            /* the post has been edited and should be updated */
            else{
                ContentValues contentValues1 = new ContentValues();
                contentValues1.put(variable.InputData_COLUMN_TITLE, title);
                contentValues1.put(variable.InputData_COLUMN_CONTENT, content);
                contentValues1.put(variable.InputData_COLUMN_EDITOR_CHOICE,editorChoice);
                db1.update(variable.InputData_TABLE_NAME, contentValues1, "id=" + "'" + id + "'", null);
                //db1.execSQL("update InputData set title = '"+title+"' where id = '"+id+"'");
                //db1.execSQL("update InputData set content = '"+content+"' where id = '"+id+"'");

            }
            db1.close();
        }
            /*----------update timestamp--------------------*/
            SQLiteDatabase db2 = getWritableDatabase();
            Cursor cursor = db2.rawQuery("select * from Timestamp", null);
            if (cursor != null && cursor.getCount() > 0) {
                updateTimestamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED, date);
            } else {
                ContentValues contentValuesTimeStamp = new ContentValues();
                contentValuesTimeStamp.put(variable.timestamp_COLUMN_LAST_UPDATED, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
                db2.insert(variable.timestamp_TABLE_NAME, null, contentValuesTimeStamp);
            }
            db2.close();
    }


    //get a InputData of a specific author
    public DataModel getTrendingContent(String postId, int triggerFrom) {
      //  postId = "'"+postId+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        DataModel model = null;
        Cursor res;
        if(triggerFrom == variable.TRIGGERED_FROM_MAIN_ACTIVITY) {
            res = db.rawQuery("select * from InputData where id='" + postId + "'", null);
        }else if(triggerFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG){
            res = db.rawQuery("select * from InputData where id='" + postId + "' and type = "+variable.ADAPTER_CODE_LONG_WRITING+"", null);
            Log.i("tendingTrace", "fetch data: "+res.getCount());
        }else{
            res = db.rawQuery("select * from InputData where id='" + postId + "' and type = "+variable.ADAPTER_CODE_SHORT_WRITING+"", null);
        }
        res.moveToFirst();
        if(res.isAfterLast() == false)
        {
            //Log.i("inside", "getTrendingContent: ");
            if(triggerFrom == variable.TRIGGERED_FROM_MAIN_ACTIVITY){
            model = new DataModel(
                   "Trending",
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    "0",
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_TYPE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID))
            );
            }
            else if(triggerFrom == variable.TRIGGERED_FROM_NAV_TRENDING_LONG ||
                     triggerFrom == variable.TRIGGERED_FROM_NAV_TRENDING_SHORT)
            {
                Log.i("tendingTrace", "fill data: ");
             model = new DataModel(
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
                );
            }

        }
        Log.i("nukl", "getTrendingContent: "+model);
        res.close();
        db.close();
       return  model;
    }

    // get editor's choice
    public ArrayList<DataModel> getEditorChoice(int adapterCode){
        ArrayList<DataModel> retArray = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("select * from InputData where editorChoice = "+1+" and type = "+adapterCode+"", null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            retArray.add(new DataModel(
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
                    ));
            res.moveToNext();
        }

        return retArray;
    }

    //get category or subcategory of and postId
   public String[] getInputDataForDynamicLink(String postId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select * from InputData where id = '"+postId+"'",null);
        String[] returnData = {"0","0"};
        if(res.moveToFirst()){
            returnData[0] = res.getString(res.getColumnIndex(variable.InputData_COLUMN_CATEGORY));
            returnData[1] = res.getString(res.getColumnIndex(variable.InputData_COLUMN_SUB_CATEGORY));
        }
        if(returnData[0].equals("0")) {
            return null;
        }
        else {
            return returnData;
        }
   }


    //get total number of InputData available
    public int numberOfContent() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numContent = (int) DatabaseUtils.queryNumEntries(db, variable.InputData_TABLE_NAME);
        return numContent;
    }

    //get the current reaction number of content
    public int getReactionCount(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from InputData where id= '" + id + "'", null);
        res.moveToFirst();
        if (res.isAfterLast() == false) {
            return res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION));
        }
        return 0;
    }

    public boolean updateContentReactionOnRefresh(String id, long reactionCount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.InputData_COLUMN_REACTION, reactionCount);
        id = "'" + id + "'";
        db.update(variable.InputData_TABLE_NAME, contentValues, "id=" + id, null);
        return true;
    }

    //update the reaction to the content
    public boolean updateContentReaction(String id, final int direction) {
        final String finalId = id;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (direction == InputData_LIKED) {
            contentValues.put("reaction", (getReactionCount(id) + 1));
        } else {
            contentValues.put("reaction", (getReactionCount(id) - 1));
        }
        id = "'" + id + "'";
        db.update("InputData", contentValues, "id=" + id, null);
        /*----------------------------------------For Updating Data to Local DB----------------------------------------------------------*/

        /*----------------------------------------For Updating Data to Firebase  DB----------------------------------------------------------*/
         FirebaseFirestore fdb=FirebaseFirestore.getInstance();

        Log.i("id", "updateContentReaction: "+id);
        fdb.collection(variable.InputData_TABLE_NAME)
                 .whereEqualTo(variable.InputData_COLUMN_ID,finalId)
                 .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isComplete())
                    {
                        Log.i("test", "onComplete: 11" + task.getResult().getDocuments().get(0).getString(variable.InputData_COLUMN_ID));
                        FirebaseFirestore updatefdb=FirebaseFirestore.getInstance();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference dr = updatefdb.collection(variable.InputData_TABLE_NAME).document(document.getId());
                            Log.i("DocumentId", "onComplete: "+ document.getId());
                            if (direction == InputData_LIKED) {
                                dr.update(variable.InputData_COLUMN_REACTION, (getReactionCount(finalId))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }
                            else
                            {
                                dr.update(variable.InputData_COLUMN_REACTION, (getReactionCount(finalId)));
                            }
                            dr.update(variable.InputData_FIREBASE_TIMSTAMP_LAST_UPDATE,new Date().getTime());
                        }
                    }
             }
         });

        /*----------------------------------------For Updateng Data to Firebase DB----------------------------------------------------------*/


        return true;
    }


    //update the contents of a InputData
    public boolean updateContent(final int id, final String title, final String author, final String content, final String date, final String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
        final String currentDateandTime = sdf.format(new Date());
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", title);
        contentValues.put("author", author);
        contentValues.put("content", content);
        contentValues.put("date", date);
        contentValues.put("tag", tag);
        db.update("InputData", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        /*----------------------------------------For Updating Data to Firebase  DB----------------------------------------------------------*/
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        fdb.collection(variable.InputData_TABLE_NAME)
                .whereEqualTo(variable.InputData_COLUMN_ID,id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete())
                {
                    FirebaseFirestore updatefdb=FirebaseFirestore.getInstance();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference dr = updatefdb.collection(variable.InputData_TABLE_NAME).document(document.getId());
                            dr.update(variable.categoryData_COLUMN_CATEGORY_ID,id);
                            dr.update(variable.InputData_COLUMN_TITLE,title);
                            dr.update(variable.InputData_COLUMN_USER_ID,author);
                            dr.update(variable.InputData_COLUMN_CONTENT,content);
                            dr.update(variable.InputData_COLUMN_DATE,date);
                            dr.update(variable.InputData_COLUMN_TAG,tag);
                            dr.update("Timestamp",currentDateandTime);

                    }
                }
            }
        });

        /*----------------------------------------For Updateng Data to Firebase DB----------------------------------------------------------*/

        return true;
    }

    //delete a InputData from database by id
    public Integer deleteContent(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("InputData",
                "id ="+id,
                new String[]{Integer.toString(id)});
    }

    //get all InputDatas
    public ArrayList<DataModel> getContentsByAuthor(String authorName, int type) {

        InputDataList = new ArrayList<DataModel>();
        String outerTopic;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from InputData where author = '"+authorName+"' and type = "+type+" Order By date DESC", null);
        res.moveToFirst();

        if(type == variable.ADAPTER_CODE_SHORT_WRITING){
            outerTopic = "profileShort";
        }else{
            outerTopic = "profileLong";
        }
        //initialize the adapter to list all data
        while (res.isAfterLast() == false) {
            InputDataList.add(new DataModel(
                    outerTopic,
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    "0",
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_TYPE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID))
            ));
            res.moveToNext();
        }
        return InputDataList;
    }

    /* get data by author in long data model format */
    public ArrayList<DataModel> getContentsByAuthorLongDatModel(String authorName, int type) {

        InputDataList = new ArrayList<DataModel>();
        String outerTopic;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from InputData where author = '"+authorName+"' and type = "+type+" Order By date DESC", null);
        res.moveToFirst();

        if(type == variable.ADAPTER_CODE_SHORT_WRITING){
            outerTopic = "profileShort";
        }else{
            outerTopic = "profileLong";
        }
        //initialize the adapter to list all data
        while (res.isAfterLast() == false) {
            InputDataList.add(new DataModel(
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE)))
            );
            res.moveToNext();
        }
        return InputDataList;
    }
    public long getTimeStamp(String reqTimestamp)
    {
       Date DefaulDate = new Date();
     //  DefaulDate = new Date(DefaulDate.getTime() - 2);

        long returnTimeStamp = 0;

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor getTimestampdata = db.rawQuery("select * from Timestamp",null);

        if(getTimestampdata.moveToFirst())
        {
                if(reqTimestamp.equals(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED))
                {
                    returnTimeStamp = getTimestampdata.getLong(getTimestampdata.getColumnIndex(variable.timestamp_COLUMN_LAST_UPDATED));
                    Log.i("testeste", "getTimeStamp: ");
                }
               else if(reqTimestamp.equals(variable.timestamp_LABEL_lastSubCategorytimestamp))
                {
                    returnTimeStamp=getTimestampdata.getLong(getTimestampdata.getColumnIndex(variable.timestamp_COLUMN_lastsubcategorytimestamp));
                }
               else if(reqTimestamp.equals(variable.timestamp_LABEL_lastCategorytimestamp))
                {
                    returnTimeStamp=getTimestampdata.getLong(getTimestampdata.getColumnIndex(variable.timestamp_COLUMN_lastcategorytimestamp));
                }
               else if(reqTimestamp.equals(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED)){
                   returnTimeStamp = getTimestampdata.getLong(getTimestampdata.getColumnIndex(variable.timestamp_COLUMN_LAST_SHORTPOST_TIMESTAMP));
                }
               else if(reqTimestamp.equals(variable.timestamp_LABEL_LAST_PROFILE_TIMESTAMP)){
                   returnTimeStamp = getTimestampdata.getLong(getTimestampdata.getColumnIndex(variable.timestamp_COLUMN_LAST_PROFILE_TIMESTAMP));
                }

        }
        Log.i("TimeTrack", "getTimeStamp:  "+ returnTimeStamp);
        return returnTimeStamp;
    }
 public ArrayList<DataModel> getBookMarkPosts(int postType) {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res1 = db.rawQuery("select * from bookmark", null);
        res1.moveToFirst();
        while(!res1.isAfterLast()){
            String postId = res1.getString(res1.getColumnIndex(variable.bookmark_COLUMN_ID));
            Cursor res = db.rawQuery("select * from InputData where id = '"+postId+"' and type = "+postType+"", null);
            Log.i("coumt", "getAllContentsByAuthor: "+res1.getCount()+" "+postId);
            //initialize the adapter to list all data

            if(res.moveToFirst()) {
                InputDataList.add(new DataModel(
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE)))
                );
            }
                res1.moveToNext();
        }

        return InputDataList;
    }

    //todo
    public ArrayList<shortPostDetails> getBookMarkShortPosts(int postType) {


        ArrayList<shortPostDetails> InputDataShortList = new ArrayList<shortPostDetails>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res1 = db.rawQuery("select * from bookmark", null);
        res1.moveToFirst();
        while(!res1.isAfterLast()){
            String postId = res1.getString(res1.getColumnIndex(variable.bookmark_COLUMN_ID));
            Cursor res = db.rawQuery("select * from shortPostData where postId = '"+postId+"'", null);
            Log.i("coumt", "getAllContentsByAuthor: "+res.getCount());
            //initialize the adapter to list all data
            if(res.moveToFirst()) {
                InputDataShortList.add(new shortPostDetails(
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_IMAGE_CODE)),
                        res.getInt(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_SIZE)),
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_COLOUR)),
                        res.getInt(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_POSITION)),
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_SHORT_DESCRIPTION))
                ));
            }
            res1.moveToNext();
        }

        return InputDataShortList;
    }
    //get content category wise
    public ArrayList<DataModel> getGenreContent(String innerTopic, String outerTopic) {


        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from InputData where genre = '" + outerTopic + "' and subCategory = '"+innerTopic+"' Order By date DESC", null);

        if(res.getCount() == 0)
        {
            if(innerTopic.contains(variable.STRING_ALL)){
                String[] spilt = innerTopic.split("_");
                outerTopic = spilt[1];
                if(outerTopic.equals(variable.STRING_SHORT_POST)){
                    res = db.rawQuery("select * from InputData where type = "+variable.ADAPTER_CODE_SHORT_WRITING+" Order By date DESC", null);
                }else if(outerTopic.equals(variable.STRING_LONG_POST)){
                    res = db.rawQuery("select * from InputData where type = " + variable.ADAPTER_CODE_LONG_WRITING + " Order By date DESC", null);
                }
            }
            else {
                res = db.rawQuery("select * from InputData where genre = '" + innerTopic + "' Order By date DESC", null);
            }
        }
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            InputDataList.add(new DataModel(
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
            ));
            res.moveToNext();
        }
        return InputDataList;
    }  public ArrayList<DataModel> getGenreContentForDynamicScroll(String outerTopic, long type) {


        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from InputData where genre = '" + outerTopic + "' and type = "+type+" Order By date DESC", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            InputDataList.add(new DataModel(
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
            ));
            res.moveToNext();
        }
        return InputDataList;
    }

    /* get the buffer data as per seach valsues */
    public ArrayList<String> getSearchBufferData(int adapterCode){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select * From InputData where type = "+adapterCode+"", null);
        res.moveToFirst();
        while (!res.isAfterLast()){
            String titlLocal = res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE));
            String userIdLocal = res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID));
            if(!arrayList.contains(titlLocal)) {
                arrayList.add(titlLocal);
            }
            if(!arrayList.contains(userIdLocal)) {
                arrayList.add(userIdLocal);
            }
            res.moveToNext();
        }

        if(adapterCode == variable.ADAPTER_CODE_SHORT_WRITING) {
            String descriptionLocal;

            res = db.rawQuery("Select * From shortPostData", null);
            res.moveToFirst();
            while (!res.isAfterLast()){
                descriptionLocal = res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_SHORT_DESCRIPTION));
                if(!arrayList.contains(descriptionLocal)) {
                    arrayList.add(descriptionLocal);
                }
                res.moveToNext();
            }
        }
        return arrayList;
    }

    /* get searched data */
    public ArrayList<DataModel> getSearchedData(String searchedText, int adapterCode){
        searchedText = "%" + searchedText + "%";

        ArrayList<DataModel> searchedData = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select * From InputData where  title LIKE '"+searchedText+"' or author LIKE '"+searchedText+"'", null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            if(res.getInt(res.getColumnIndex(variable.InputData_COLUMN_TYPE ))== adapterCode) {
                searchedData.add(new DataModel(
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
                ));
            }
            Log.i("fetchedData", "getSearchedData: "+res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)));
                    res.moveToNext();
        }

        if(adapterCode == variable.ADAPTER_CODE_SHORT_WRITING){
            Cursor shortPostRes = db.rawQuery("Select * From shortPostData where ShortDescription LIKE '"+searchedText+"'", null);
            shortPostRes.moveToFirst();
            while(!shortPostRes.isAfterLast()){
                String postId = shortPostRes.getString(shortPostRes.getColumnIndex(variable.InputData_COLUMN_ID));
                Cursor res1 = db.rawQuery("Select * From InputData Where id = '"+postId+"'",null);
                searchedData.add(new DataModel(
                        res1.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res1.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res1.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                        res1.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        res1.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                        res1.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                        res1.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                        res1.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))
                ));
                Log.i("fetchedData", "getSearchedData: "+res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)));
                shortPostRes.moveToNext();

            }
        }
        return  searchedData;
    }

    //get content data for dynamic link
    public ArrayList<DataModel> getGenreContentForDynamicLink(String innerTopic, String outerTopic, String postId) {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from InputData where genre = '" + outerTopic + "' and subCategory = '"+innerTopic+"' Order By date DESC", null);

        if(res.getCount() == 0)
        {
            res = db.rawQuery("select * from InputData where genre = '" + innerTopic + "' Order By date DESC", null);
        }
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            if(postId.equals(res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID))))
            {
                postPositionForDynamicList = res.getPosition();
            }
                InputDataList.add(new DataModel(
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                        res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                        res.getInt(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))

                ));
            res.moveToNext();
        }
        return InputDataList;
    }


    public ArrayList<DataModel> getGenreContent(String postId) {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        res = db.rawQuery("select * from InputData where id = '" + postId + "' Order By date DESC", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            InputDataList.add(new DataModel(
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE)),
                    res.getString(res.getColumnIndex(variable.InputData_COLUMN_TAG)),
                    res.getInt(res.getColumnIndex(variable.InputData_COLUMN_REACTION)),
                    res.getLong(res.getColumnIndex(variable.InputData_COLUMN_EDITOR_CHOICE))

            ));
            res.moveToNext();
        }
        return InputDataList;
    }





    //---------------------------------------Table- category section-----------------------------------------------------------------------
//Insert new category to the table
    public boolean insertCategoryData(int id, String category, long interest, String imageCode, long type, String subCategory, long priority, long container) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.categoryData_COLUMN_CATEGORY_ID, id);
        contentValues.put(variable.categoryData_COLUMN_CATEGORY, category);
        contentValues.put(variable.categoryData_COLUMN_INTEREST, interest);
        contentValues.put(variable.categoryData_COLUMN_IMAGE_CODE, imageCode);
        contentValues.put(variable.categoryData_COLUMN_TYPE, type);
        contentValues.put(variable.categoryData_COLUMN_SUBCATEGORY, subCategory);
        contentValues.put(variable.categoryData_COLUMN_PRIORITY, priority);
        contentValues.put(variable.categoryData_COLOUMN_CONTAINER, container);
        db.insert(variable.categoryData_TABLE_NAME, null, contentValues);
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/

        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
      //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
       // String currentDateandTime = sdf.format(new Date());
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.categoryData_COLUMN_CATEGORY_ID, id);
        contentBuff.put(variable.categoryData_COLUMN_CATEGORY, category);
        contentBuff.put(variable.categoryData_COLUMN_INTEREST, interest);
        contentBuff.put(variable.categoryData_COLUMN_IMAGE_CODE, imageCode);
        contentBuff.put(variable.categoryData_COLUMN_TYPE, type);
        contentBuff.put(variable.categoryData_COLUMN_SUBCATEGORY, subCategory);
        contentBuff.put(variable.categoryData_COLUMN_PRIORITY, priority);
        contentBuff.put(variable.categoryData_COLOUMN_CONTAINER, container);
        contentBuff.put(variable.categoryData_FIREBASE_TIMSTAMP, new Date().getTime());


        fdb.collection(variable.categoryData_TABLE_NAME)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/
        return true;
    }

    /* Insert Category on refresh */
    public void insertCategoryDataOnRefresh(long id, String category, long interest, String imageCodeIndex, long type, String subCategory, long priority, long container) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.categoryData_COLUMN_CATEGORY_ID, id);
        contentValues.put(variable.categoryData_COLUMN_CATEGORY, category);
        contentValues.put(variable.categoryData_COLUMN_INTEREST, interest);
        contentValues.put(variable.categoryData_COLUMN_IMAGE_CODE, imageCodeIndex);
        contentValues.put(variable.categoryData_COLUMN_TYPE, type);
        contentValues.put(variable.categoryData_COLUMN_SUBCATEGORY, subCategory);
        contentValues.put(variable.categoryData_COLUMN_PRIORITY, priority);
        contentValues.put(variable.categoryData_COLOUMN_CONTAINER, container);
        db.insert(variable.categoryData_TABLE_NAME, null, contentValues);
    }

    public ArrayList<DataModel> getSubCategoryForCategory(String categoryName)
    {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData where category = '"+categoryName+"'", null);
        res.moveToFirst();

        //initialize the data for adapter
        if (res.isAfterLast() == false) {
            String subcategogyList[] =  res.getString(res.getColumnIndex(variable.categoryData_COLUMN_SUBCATEGORY)).split(":");
            for(String subcategoryName : subcategogyList )
            {
                Log.i("splitted", "getSubCategoryForCategory: "+subcategoryName);;
                InputDataList.add(new DataModel(
                        categoryName,
                        subcategoryName,
                        getSubCategoryInterest(subcategoryName),
                        "getSubCategoryImage(name)",
                        getSubcategoryType(subcategoryName),
                        51
                ));
            }
                return InputDataList;
        }
        return null;
    }
    public ArrayList<String> getSubCategoryStringListForCategory(String categoryName)
    {
        categoryName = categoryName.toLowerCase();
        ArrayList<String> subCategoryList = new ArrayList<String>();
        /* default subcategory to display  in the topic list search scroll*/
        subCategoryList.add("mixed");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData where category = '"+categoryName+"'", null);
        res.moveToFirst();

        //initialize the data for adapter
        if (res.isAfterLast() == false) {
            String subcategogyList[] =  res.getString(res.getColumnIndex(variable.categoryData_COLUMN_SUBCATEGORY)).split(":");
            for(String subcategoryName : subcategogyList )
            {
                Log.i("splitted", "getSubCategoryForCategory: "+subcategoryName);;
                subCategoryList.add(subcategoryName);
            }
            return subCategoryList;
        }
        return null;
    }
    //this function only provides the data to initialize the nested recycler view
    public ArrayList<DataModel> getAllCategory(int postType) {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData where container = "+postType+" order by priority", null);
        res.moveToFirst();

        Log.i("countttt", "getAllCategory: "+res.getCount());
        //initialize the data for adapter
        while (res.isAfterLast() == false) {
           int imageIndex =  res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_IMAGE_CODE));
           String outerTopic ;
           if(postType == variable.ADAPTER_CODE_CONTAINER_LONG){
               outerTopic = variable.LONG_POST;
           }else if(postType == variable.ADAPTER_CODE_CONTAINER_SHORT){
               outerTopic = variable.SHORT_POST;
            }else{
               outerTopic = res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY));
           }
            InputDataList.add(new DataModel(
                    outerTopic,
                    res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)),
                    res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_INTEREST)),
                    res.getString(res.getColumnIndex(variable.categoryData_COLUMN_IMAGE_CODE)),
                    res.getLong(res.getColumnIndex(variable.categoryData_COLUMN_TYPE)),
                    res.getLong(res.getColumnIndex(variable.categoryData_COLOUMN_CONTAINER))
            ));
            res.moveToNext();
        }
        return InputDataList;
    }
    public ArrayList<DataModel> getAllCategoryForDynamicScroll(String category) {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
     //   Cursor res = db.rawQuery("select * from categoryData where container = "+postType+" order by priority", null);
       // res.moveToFirst();
        //initialize the data for adapter
      /*  while (res.isAfterLast() == false) {
           int imageIndex =  res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_IMAGE_CODE));
           String outerTopic ;

            outerTopic = res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY));*/
        Log.i("dynamicTest", "getAllCategoryForDynamicScroll: "+category);
            Cursor res1 = db.rawQuery("select * from InputData where genre = '"+category+"' order by date", null);

            res1.moveToFirst();
            while (!(res1.isAfterLast())) {
                InputDataList.add(new DataModel(
                        category,
                        res1.getString(res1.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                        res1.getString(res1.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                        "0",
                        res1.getString(res1.getColumnIndex(variable.InputData_COLUMN_ID)),
                        res1.getLong(res1.getColumnIndex(variable.InputData_COLUMN_TYPE)),
                        res1.getString(res1.getColumnIndex(variable.InputData_COLUMN_USER_ID))
                ));
                Log.i("dynamicTest", "title: "+res1.getString(res1.getColumnIndex(variable.InputData_COLUMN_TITLE)));
                res1.moveToNext();
            }
           /* res.moveToNext();
        }*/
        return InputDataList;
    }
    public ArrayList<String> getAllDynamicCategory(int postType){
        ArrayList<String> dynamicCategory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData where container = "+postType+" order by priority", null);
        res.moveToFirst();
        while (res.isAfterLast() == false){
            dynamicCategory.add(res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)));
            Log.i("dynamicTest", "getAllDynamicCategory: "+res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)));
            res.moveToNext();
        }
        return dynamicCategory;
    }

//todo work around function created later combine with getCategoryNames
    public ArrayList<String> getCategoryNamesTemporary(int containerType) {

        ArrayList<String> categoryList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT * from categoryData where container = "+containerType+" order by category ASC", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            categoryList.add(res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)));
            res.moveToNext();
        }
        return categoryList;
    }

    //get the list of all category names
    public ArrayList<String> getCategoryNames(int adapterCode) {

        ArrayList<String> categoryList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT * from categoryData where type = "+adapterCode+" order by category ASC", null);

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            /* 'all' section should not be inserted */
            String temp = res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY));
            int containerType = (int) res.getColumnIndex(variable.categoryData_COLOUMN_CONTAINER);
            //containerType != variable.ADAPTER_CODE_CONTAINER_OTHER &&
            if(temp.contains("all") == false) {
                categoryList.add(res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)));
            }
            res.moveToNext();
        }
        return categoryList;
    }
    //get list of subcategories filtered from the InputData table
    public ArrayList<String> getSubCategoryNames(String category) {
        ArrayList<String> subCategoryList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT * from categoryData where category = '"+ category +"' order by category ASC", null);
        res.moveToFirst();
        if (res.isAfterLast() == false) {
            String subCategoryArray[] = res.getString(res.getColumnIndex(variable.categoryData_COLUMN_SUBCATEGORY)).split(":");
            for(String name : subCategoryArray)
            {
                subCategoryList.add(name);
            }
        }
        return subCategoryList;

    }

    //this function return if the post is a long type of short type
    public int getCategoryType(String outerTopic, String innerTopic) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData WHERE category = '" + outerTopic + "'", null);
        if(res.getCount() == 0)
        {
             res = db.rawQuery("select * from categoryData WHERE category = '" + innerTopic + "'", null);
        }
        res.moveToFirst();
        if (res.isAfterLast() == false) {
            return res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_TYPE));
        }
        return variable.ADAPTER_CODE_LONG_WRITING;
    }

    public int getCategoryType(String postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from InputData WHERE id = '" + postId + "'", null);

        Log.i("count", "getCategoryType: "+res.getCount());
        res.moveToFirst();
        if (res.isAfterLast() == false) {

            return res.getInt(res.getColumnIndex(variable.InputData_COLUMN_TYPE));
        }

        return variable.ADAPTER_CODE_LONG_WRITING;
    }

    //this function returns favorite contents
    public ArrayList<DataModel> getFavoriteCategory() {

        InputDataList = new ArrayList<DataModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from categoryData WHERE interest = 1 ", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            int imageIndex = res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_IMAGE_CODE));
            InputDataList.add(new DataModel(
                    "Favorite",
                    res.getString(res.getColumnIndex(variable.categoryData_COLUMN_CATEGORY)),
                    res.getInt(res.getColumnIndex(variable.categoryData_COLUMN_INTEREST)),
                    "imageIndex",
                    50,
                    52
            ));
            res.moveToNext();
        }
        return InputDataList;
    }


    //----------------------------Table - subCategory----------------------------------------------------------
    //insert data to subCategory table
    public boolean insertSubCategory(String subCategoryName, long interested, String imageCodeIndex, long priority, long type)
    {
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.subCategory_COLUMN_SUBCATEGORY_NAME, subCategoryName);
        contentValues.put(variable.subCategory_COLUMN_INTEREST, interested);
        contentValues.put(variable.subCategory_COLUMN_IMAGE_CODE, imageCodeIndex);
        contentValues.put(variable.subCategory_COLUMN_PRIORITY, priority);
        contentValues.put(variable.subCategory_COLUMN_TYPE, type);

        db.insert(variable.subCategory_TABLE_NAME, null, contentValues);
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/

        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.subCategory_COLUMN_SUBCATEGORY_NAME, subCategoryName);
        contentBuff.put(variable.subCategory_COLUMN_INTEREST, interested);
        contentBuff.put(variable.subCategory_COLUMN_IMAGE_CODE, imageCodeIndex);
        contentBuff.put(variable.subCategory_COLUMN_PRIORITY, priority);
        contentBuff.put(variable.subCategory_COLUMN_TYPE, type);
        contentBuff.put(variable.subCategory_FIREBASE_TIMSTAMP, new Date().getTime());

        fdb.collection(variable.subCategory_TABLE_NAME)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/
        return true;
    }

    /* insert On refresh */
    public void insertSubCategoryOnRefresh(String subCategoryName, long interested, String imageCodeIndex, long priority, long type) {
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.subCategory_COLUMN_SUBCATEGORY_NAME, subCategoryName);
        contentValues.put(variable.subCategory_COLUMN_INTEREST, interested);
        contentValues.put(variable.subCategory_COLUMN_IMAGE_CODE, imageCodeIndex);
        contentValues.put(variable.subCategory_COLUMN_PRIORITY, priority);
        contentValues.put(variable.subCategory_COLUMN_TYPE,type);
        db.insert(variable.subCategory_TABLE_NAME, null, contentValues);
    }
    /*get subcategory type */
    public int getSubcategoryType(String subCategoryName){
        int type = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select * from subCategory where subCategoryName = '"+subCategoryName+"'",null);
        if(res.moveToFirst()){
            type = (int)res.getLong(res.getColumnIndex(variable.subCategory_COLUMN_TYPE));
        }

        return  type;
    }

    //delete subcategory
    public void deleteSubCategory(String subCategoryName)
    {
        subCategoryName =  "'" + subCategoryName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(variable.bookmark_TABLE_NAME,
                "subCategoryName = " + subCategoryName,
                null);
    }

    //update priority
    public void updatePriority(String subCategoryName, int priority)
    {
        subCategoryName =  "'" + subCategoryName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.subCategory_COLUMN_PRIORITY, priority);
        db.update(variable.subCategory_TABLE_NAME,contentValues,"subCategoryName = " + subCategoryName,null);
    }

    public int getSubCategoryImage(String subCategoryName)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int imageIndex;
        Cursor res = db.rawQuery("select * from subCategory WHERE subCategoryName = '"+subCategoryName+"'", null);
        res.moveToFirst();

        if (res.isAfterLast() == false) {
            imageIndex = res.getInt(res.getColumnIndex(variable.subCategory_COLUMN_IMAGE_CODE));
            return variable.drawableImageCode[imageIndex];
        }else
        {
            return variable.DEFAULT;
        }
    }
    public int getSubCategoryInterest(String subCategoryName)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from subCategory WHERE subCategoryName = '"+subCategoryName+"'", null);
        res.moveToFirst();

        if (res.isAfterLast() == false) {
            return  res.getInt(res.getColumnIndex(variable.subCategory_COLUMN_INTEREST));
        }else
        {
            return variable.DEFAULT;
        }
    }

    //--------------------------------------table - ShortPostData  --------------------------------------------------------------------------


    /* Insert shortPostData on refresh */
    public void insertShortPostDataOnRefresh(String userId, String postId, String imageIndex, long textSizeIndex, String textColourIndex, long textPositionIndex, String shortDescription)
    {
      /*  SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.shortPostData_COLUMN_USER_ID, userId);
        contentValues.put(variable.shortPostData_COLUMN_POST_ID, postId);
        contentValues.put(variable.shortPostData_COLUMN_IMAGE_CODE, imageIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_SIZE, textSizeIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_COLOUR, textColourIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_POSITION, textPositionIndex);
        contentValues.put(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, shortDescription);
        db.insert(variable.shortPostData_TABLE_NAME,null,contentValues);
        db.close();*/

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor checkCursor = db.rawQuery("Select * from shortPostData where postId = '"+postId+"'", null);

        if(checkCursor.moveToFirst() == false) {
            if(shortDescription.equals(variable.STRING_DEFAULT) == false) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(variable.shortPostData_COLUMN_USER_ID, userId);
                contentValues.put(variable.shortPostData_COLUMN_POST_ID, postId);
                contentValues.put(variable.shortPostData_COLUMN_IMAGE_CODE, imageIndex);
                contentValues.put(variable.shortPostData_COLUMN_TEXT_SIZE, textSizeIndex);
                contentValues.put(variable.shortPostData_COLUMN_TEXT_COLOUR, textColourIndex);
                contentValues.put(variable.shortPostData_COLUMN_TEXT_POSITION, textPositionIndex);
                contentValues.put(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, shortDescription);
                db.insert(variable.shortPostData_TABLE_NAME, null, contentValues);
                db.close();
            }
        }
        else{
            SQLiteDatabase db1 = this.getWritableDatabase();
            if(shortDescription.equals(variable.STRING_DEFAULT) == true) {

                db1.execSQL("delete from shortPostData where postId = '"+postId+"'");
                Log.i("postShortDelete", "insertContentOnRefresh: ");

            }else{
                /*
                db1.execSQL("update shortPostData set title = '"+title+"' where id = '"+id+"'");
                db1.execSQL("update shortPostData set content = '"+content+"' where id = '"+id+"'");*/

            }
            db1.close();
        }

    }
    //insert shortpostdata
    public void insertShortPostData(String userId, String postId, String imageIndex, long textSizeIndex, String textColourIndex, long textPositionIndex, String shortDescription)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.shortPostData_COLUMN_USER_ID, userId);
        contentValues.put(variable.shortPostData_COLUMN_POST_ID, postId);
        contentValues.put(variable.shortPostData_COLUMN_IMAGE_CODE, imageIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_SIZE, textSizeIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_COLOUR, textColourIndex);
        contentValues.put(variable.shortPostData_COLUMN_TEXT_POSITION, textPositionIndex);
        contentValues.put(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, shortDescription);
        db.insert(variable.shortPostData_TABLE_NAME,null,contentValues);
        db.close();
        /* Firebase code */
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.shortPostData_COLUMN_USER_ID, userId);
        contentBuff.put(variable.shortPostData_COLUMN_POST_ID, postId);
        contentBuff.put(variable.shortPostData_COLUMN_IMAGE_CODE, imageIndex);
        contentBuff.put(variable.shortPostData_COLUMN_TEXT_SIZE, textSizeIndex);
        contentBuff.put(variable.shortPostData_COLUMN_TEXT_COLOUR, textColourIndex);
        contentBuff.put(variable.shortPostData_COLUMN_TEXT_POSITION, textPositionIndex);
        contentBuff.put(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, shortDescription);
        contentBuff.put(variable.shortPostData_FIREBASE_TIMSTAMP, new Date().getTime());

        fdb.collection(variable.shortPostData_TABLE_NAME)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public HashMap<String, shortPostDetails> getShortPostDetails(ArrayList<DataModel> postIds) {

        HashMap<String, shortPostDetails> shortDetails = new HashMap<String,shortPostDetails>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        String id;
        for(DataModel model : postIds) {
            id = model.getContentId();
            res = db.rawQuery("select * from shortPostData where postId = '" + id + "' ", null);

            res.moveToFirst();

            if (res.isAfterLast() == false) {
                shortDetails.put(
                        id,
                        new shortPostDetails(
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_IMAGE_CODE)),
                        res.getInt(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_SIZE)),
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_COLOUR)),
                        res.getInt(res.getColumnIndex(variable.shortPostData_COLUMN_TEXT_POSITION)),
                        res.getString(res.getColumnIndex(variable.shortPostData_COLUMN_SHORT_DESCRIPTION))
                ));
            }
        }
        return shortDetails;
    }


    //--------------------------------------Table - userData related functions---------------------------------------------------------

    //insert the userData
    public void insertUserDataFireBase(String userId, String userName, String profileImageUri, String userEmail, String description, long level, String rank, String subcription){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.userData_COLUMN_USER_ID,userId);
        contentValues.put(variable.userData_COLUMN_USER_NAME,userName);
        contentValues.put(variable.userData_COLUMN_PROFILE_IMAGE_URI, profileImageUri);
        contentValues.put(variable.userData_COLUMN_USER_EMAIL_ID, userEmail);
        contentValues.put(variable.userData_COLUMN_DESCRIONTION, description);
        contentValues.put(variable.userData_COLUMN_LEVEL, level);
        contentValues.put(variable.userData_COLUMN_RANK, rank);
        contentValues.put(variable.userData_COLUMN_PROFILE_SUBSCRIPTION, subcription);
        db.insert(variable.userData_TABLE_NAME,null,contentValues);
        db.close();

        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.userData_COLUMN_USER_ID,userId);
        contentBuff.put(variable.userData_COLUMN_USER_NAME,userName);
        contentBuff.put(variable.userData_COLUMN_PROFILE_IMAGE_URI, profileImageUri);
        contentBuff.put(variable.userData_COLUMN_DESCRIONTION, description);
        contentBuff.put(variable.userData_COLUMN_USER_EMAIL_ID, userEmail);
        contentBuff.put(variable.userData_COLUMN_LEVEL, level);
        contentBuff.put(variable.userData_COLUMN_RANK, rank);
        contentBuff.put(variable.userData_COLUMN_PROFILE_SUBSCRIPTION, subcription);

        fdb.collection(variable.USER_LIST)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

    }

    public void updateUserDataDescription(final String description){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.userData_COLUMN_DESCRIONTION, description);
        db.update(variable.userData_TABLE_NAME, contentValues,  "userId = " + "'"+variable.USER_ID+"'", null);
       // db.execSQL("update userData set description = '"+description+"' where userId = '"+variable.USER_ID+"'");
        db.close();


        /*----------------------------------------For Updating Data to Firebase  DB----------------------------------------------------------*/
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();

        Log.i("id", "updateContentReaction: ");
        fdb.collection(variable.USER_LIST)
                .whereEqualTo(variable.userData_COLUMN_USER_ID, variable.USER_ID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete())
                {
                //    Log.i("test", "onComplete: 11" + task.getResult().getDocuments().get(0).getString(variable.InputData_COLUMN_ID));
                    FirebaseFirestore updatefdb=FirebaseFirestore.getInstance();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference dr = updatefdb.collection(variable.USER_LIST).document(document.getId());
                        Log.i("DocumentId", "onComplete: "+ document.getId());

                            dr.update(variable.userData_COLUMN_DESCRIONTION, description).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("DocumentId", "onComplete: "+ "update hela");

                                }
                            });

                    }
                }
            }
        });


    }


    /* delete userData */
    public void deleteUserData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from userData");
        db.close();
    }
    public void insertUserData(String userId, String userName, String profileImageUri, String description, long level, String rank, String subscription)
    {
        Log.i("idddd", "insertUserData: "+userId);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.userData_COLUMN_USER_ID,userId);
        contentValues.put(variable.userData_COLUMN_USER_NAME,userName);
        contentValues.put(variable.userData_COLUMN_PROFILE_IMAGE_URI, profileImageUri);
        contentValues.put(variable.userData_COLUMN_DESCRIONTION, description);
        contentValues.put(variable.userData_COLUMN_LEVEL, level);
        contentValues.put(variable.userData_COLUMN_RANK, rank);
        contentValues.put(variable.userData_COLUMN_PROFILE_SUBSCRIPTION, subscription);
        db.insert(variable.userData_TABLE_NAME,null,contentValues);
        db.close();

    }
    public void updateTimestamp(String reqTimeStamp, long valTimestamp)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor curso = db.rawQuery("Select * from Timestamp ", null);
        if(curso.moveToFirst()) {
            if (reqTimeStamp.equals(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED)) {
                db.execSQL("UPDATE Timestamp set lastUpdated = "+valTimestamp+"");
                Log.i("asuche", "updateTimestamp: ");
            }
            else if (reqTimeStamp.equals(variable.timestamp_LABEL_lastCategorytimestamp)) {
                db.execSQL("UPDATE Timestamp set lastcategorytimestamp = "+valTimestamp+"");
                Log.i("timeStamp11", "updateTimestamp: "+valTimestamp);
            }
            else if (reqTimeStamp.equals(variable.timestamp_LABEL_lastSubCategorytimestamp)) {
                db.execSQL("UPDATE Timestamp set lastsubcategorytimestamp = "+valTimestamp+"");
                Log.i("timeStamp11", "updateTimestampSubCategory: "+valTimestamp);
            }
            else if (reqTimeStamp.equals(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED)){
                db.execSQL("UPDATE Timestamp set lastShortPostUpdated = "+valTimestamp+"");

            }
            else if( reqTimeStamp.equals(variable.timestamp_LABEL_LAST_PROFILE_TIMESTAMP)){
                db.execSQL("UPDATE Timestamp set profileTimeStamp = "+valTimestamp+"");
            }
        }else{
            contentValues.put(variable.timestamp_COLUMN_LAST_UPDATED, valTimestamp);
            db.insert(variable.timestamp_TABLE_NAME,null, contentValues);
        }

        Log.i("TimeTrack", "updateTimestamp: \n"+ "inputTime  :"+ valTimestamp+"\n   "+"outputTime :"+getTimeStamp(variable.timestamp_LABEL_INPUTDATA_LASTUPDATED));
    }
    //set trending post
    public void insertTrending()
    {
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from trending");
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select id from InputData where reaction >= 0.66*(Select max(reaction) from InputData ) order by date Desc limit 200", null);
        ContentValues contentValues = new ContentValues();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            contentValues.put(variable.trending_COLUMN_POST_ID,cursor.getString(cursor.getColumnIndex(variable.InputData_COLUMN_ID)));
           // Log.i("Loop", "insertTrending: "+ cursor.getInt(cursor.getColumnIndex(variable.InputData_COLUMN_REACTION)));
            db.insert(variable.trending_TABLE_NAME,null,contentValues);
            Log.i("number", "TrendingCount: "+cursor.getCount());
            cursor.moveToNext();
        }
        db.close();
        /*----------------------------------------For Inserting Data to Local DB----------------------------------------------------------*/

        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/


        //To be used in master app
        /* FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.trending_COLUMN_POST_ID,postId);
        fdb.collection(variable.trending_TABLE_NAME)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });*/
        /*----------------------------------------For Inserting Data to Firebase DB----------------------------------------------------------*/
    }
    //Get all trending data
    public  ArrayList<DataModel> getTrendingTopicList(int triggerFrom){

        InputDataList = new ArrayList<DataModel>();

        SQLiteDatabase db = this.getReadableDatabase();
               Cursor res = db.rawQuery(" select * from trending ",null);
        res.moveToFirst();

        while(!res.isAfterLast())
        {
            Log.i("tendingTrace", "count trend: "+res.getCount());
            DataModel model;
            model = getTrendingContent(res.getString(res.getColumnIndex(variable.trending_COLUMN_POST_ID)), triggerFrom);
            if(model != null)
            InputDataList.add(model);
            res.moveToNext();
        }
        res.close();
        db.close();
        return InputDataList;
    }

    //updates the count of likes in a post
    public boolean updatePostLiked(String postId) {

        /*----------------------------------------For Updating Data to Firebase  DB----------------------------------------------------------*/
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        final String finalId = postId;
        fdb.collection(variable.POST_LIKED_TABLE_NAME)
                .whereEqualTo(variable.POST_LIKED_COLUMN_NAME,postId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isComplete())
                {
                    FirebaseFirestore updatefdb=FirebaseFirestore.getInstance();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference dr = updatefdb.collection(variable.POST_LIKED_TABLE_NAME).document(document.getId());

//                            dr.update(variable.POST_LIKED_COLUMN_NAME, );////TBD

                    }
                }
            }
        });

        /*----------------------------------------For Updateng Data to Firebase DB----------------------------------------------------------*/


        return updateHelper(postId, variable.POST_LIKED_TABLE_NAME, variable.POST_LIKED_COLUMN_NAME);
    }
    //if something new written insert to the local data base
    public boolean insertMyPost(String myPostId) {
        return updateHelper(myPostId, variable.MY_POST_TABLE_NAME, variable.My_POST_COLUMN_NAME);
    }
   /* returns the number of rows exists in a table */
    public int getDbRowCount(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from '"+tableName+"'", null);
        return res.getCount();
    }

    //update the category liked by the user
    public boolean updateGenreLiked(int genreId) {
        return updateHelper(genreId, variable.GENRE_LIKED_TABLE_NAME, variable.GENRE_LIKED_COLUMN_NAME);
    }

    //if someone follows this should be updated
    public boolean updateFollowers(String followersId) {
        return updateHelper(followersId, variable.FOLLOWERS_TABLE_NAME, variable.FOLLOWERS_COLUMN_NAME);
    }

    public boolean updateFollowingOnRefresh(String followingId){
        return updateHelper(followingId,variable.FOLLOWING_TABLE_NAME, variable.FOLLOWING_COLUMN_NAME);
    }
    //if the user started following someone then this should be updated
    public boolean updateFollowing(String followingId) {

        return updateHelperFollow(followingId, variable.FOLLOWING_TABLE_NAME, variable.FOLLOWING_COLUMN_NAME);
    }
    public void insertFollowing(String followingId)
    {
         SQLiteDatabase db=getWritableDatabase();
         ContentValues contentValues=new ContentValues();
         contentValues.put(variable.FOLLOWING_COLUMN_NAME,followingId);
         db.insert(variable.FOLLOWING_TABLE_NAME,null,contentValues);
         db.close();
    }
    public void insertFollower(String followerId)
    {
         SQLiteDatabase db=getWritableDatabase();
         ContentValues contentValues=new ContentValues();
         contentValues.put(variable.FOLLOWERS_COLUMN_NAME,followerId);
         db.insert(variable.FOLLOWERS_TABLE_NAME,null,contentValues);
         db.close();
    }
    public void fetchFollowDataFirebase(String userId){
        FirebaseFirestore fdb=FirebaseFirestore.getInstance();
        final long longDate = getTimeStamp(variable.timestamp_LABEL_LAST_PROFILE_TIMESTAMP);
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
                                    insertFollowing(followingId);
                                }else if(followerId != null){
                                    insertFollower(followerId);
                                }
                                //myDb.updateFollowers(document.getString(variable.FOLLOWERS_COLUMN_NAME));
                                //myDb.updateFollowingOnRefresh(document.getString(variable.FOLLOWING_COLUMN_NAME));
                            }
                            if(task.getResult().size() > 0) {
                                updateTimestamp(variable.timestamp_COLUMN_LAST_PROFILE_TIMESTAMP, new Date().getTime());
                            }
                        } else {
                            Log.d("Failure", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.i("idddd", "fetchFollowDataFirebase: "+userId);

    }

    public SQLiteDatabase getSQL_Write_Instance(){
        return getWritableDatabase();
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
                        SQLiteDatabase myDb = getWritableDatabase();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            contentValues.put(variable.My_POST_COLUMN_NAME, document.getString(variable.InputData_COLUMN_ID));
                            myDb.insert(variable.MY_POST_TABLE_NAME, null, contentValues);
                        }
                        myDb.close();
                    }
                });
    }

    /* get recent posts from followed authors */
    public ArrayList<DataModel> getFollowedPost()
    {
        ArrayList<DataModel> array = new ArrayList<DataModel>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor localRes = db.rawQuery("SELECT * FROM following", null);
        if(localRes.moveToFirst()){
            while(!localRes.isAfterLast()){
                String followingId = localRes.getString(localRes.getColumnIndex(variable.FOLLOWING_COLUMN_NAME));
                Cursor res = db.rawQuery("SELECT * FROM InputData Where author = '"+followingId+"' order by date  DESC LIMIT 10  ", null);
                res.moveToFirst();
                while(!res.isAfterLast()) {
                    long postDate = res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE));
                    long currentDate = new Date().getTime();
                    long fiveDayBefore = new Date(currentDate - (14*24 * 3600000)).getTime();
                    if(postDate > fiveDayBefore) {
                        array.add(new DataModel(
                                "Followed",
                                res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                                res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                                "0",
                                res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                                res.getInt(res.getColumnIndex(variable.InputData_COLUMN_TYPE)),
                                res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID))
                        ));
                    }
                    res.moveToNext();
                }
                Log.i("TAG1", "getFollowedPost: "+res.getCount());
                localRes.moveToNext();
            }

        }
        else{
            String followingId = variable.OFFICIAL_USER_ID;
            Cursor res = db.rawQuery("SELECT * FROM InputData Where author = '"+followingId+"' order by date  DESC LIMIT 10  ", null);
            res.moveToFirst();
            while(!res.isAfterLast()) {
                long postDate = res.getLong(res.getColumnIndex(variable.InputData_COLUMN_DATE));
                long currentDate = new Date().getTime();
                long fiveDayBefore = new Date(currentDate - (5*24 * 3600000)).getTime();
                if(postDate > fiveDayBefore) {
                    array.add(new DataModel(
                            "Followed",
                            res.getString(res.getColumnIndex(variable.InputData_COLUMN_TITLE)),
                            res.getString(res.getColumnIndex(variable.InputData_COLUMN_CONTENT)),
                            "0",
                            res.getString(res.getColumnIndex(variable.InputData_COLUMN_ID)),
                            res.getInt(res.getColumnIndex(variable.InputData_COLUMN_TYPE)),
                            res.getString(res.getColumnIndex(variable.InputData_COLUMN_USER_ID))
                    ));
                }
                res.moveToNext();
            }
            Log.i("TAG1", "getFollowedPost: "+res.getCount());
        }

        Log.i("TAG", "getFollowedPost: "+localRes.getCount());
        return array;
    }

  //Helper functions---------------------
    //this functions checks if following/followed/liked/bookmarked type relations based on the arguments
    //if id is string
    public boolean isPositiveRelation(String newId, String table, String columnName) {
        return rowIdExists(newId, table, columnName);
    }
    //if id is integer
    public boolean isPositiveRelation(int newId, String table, String columnName) {
        return rowIdExists(newId, table, columnName);
    }

    // this is one helper function that establishes or terminates the relationship (i.e. like/follow/bookmarks..)
    public boolean updateHelper(String newId, String table, String columnName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (!rowIdExists(newId, table, columnName)) {

            contentValues.put(columnName, newId);
            db.insert(table, null, contentValues);
        } else {
            newId = "'" + newId + "'";

            db.delete(table, columnName + "=" + newId, null);
        }
        return true;
    }

    /* function to handle follow and unfollow */
    public boolean updateHelperFollow(final String newId, final String table, final String columnName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        FirebaseFirestore fdbUpdate = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        long currentTime = new Date().getTime();
        /* code to follow */
        if (!rowIdExists(newId, table, columnName)) {

            /* add new following in my collection */
            contentBuff.put(variable.FOLLOWING_COLUMN_NAME,newId);
            contentBuff.put(variable.FOLLOWING_FIREBASE_TIMESTAMP, currentTime);
            fdbUpdate.collection(variable.USER_ID)
                    .add(contentBuff)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });

             /* add follower in followed user's collection */
            contentBuff = new HashMap<>();
            contentBuff.put(variable.FOLLOWERS_COLUMN_NAME,variable.USER_ID);
            contentBuff.put(variable.FOLLOWERS_FIREBASE_TIMESTAMP, currentTime);
            fdbUpdate.collection(newId)
                    .add(contentBuff)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });
            contentValues.put(columnName, newId);
            /* add following in local database */
            db.insert(table, null, contentValues);
        }
        /* code to unfollow */
        else {

            /* delete following from my collection */
            fdbUpdate.collection(variable.USER_ID)
                    .whereEqualTo(variable.FOLLOWING_COLUMN_NAME,newId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Success", document.getId() + " => " + document.getData());
                            /*-Code to insert data to ShortPost Data-*/
                            FirebaseFirestore fdbDelete = FirebaseFirestore.getInstance();
                            fdbDelete.collection(variable.USER_ID).document(document.getId()).delete();
                        }
                        if(task.getResult().size() > 0) {
                            //myDb.updateTimestamp(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED, new Date().getTime());
                        }
                    } else {
                        Log.d("Failure", "Error getting documents: ", task.getException());
                    }
                }
            });

            /* delete follower from unfollowed user's collection */
            fdbUpdate.collection(newId)
                    .whereEqualTo(variable.FOLLOWERS_COLUMN_NAME, variable.USER_ID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Success", document.getId() + " => " + document.getData());
                            /*-Code to insert data to ShortPost Data-*/
                            FirebaseFirestore fdbDelete = FirebaseFirestore.getInstance();
                            fdbDelete.collection(newId).document(document.getId()).delete();
                        }
                        if(task.getResult().size() > 0) {
                            //myDb.updateTimestamp(variable.timestamp_LABEL_LAST_SHORTPOST_UPDATED, new Date().getTime());
                        }
                    } else {
                        Log.d("Failure", "Error getting documents: ", task.getException());
                    }
                }
            });

            /* delete following in local data base */
            db.delete(table, columnName + "=" + "'"+newId+"'", null);
        }
        return true;
    }

    public boolean updateHelper(int newId, String table, String columnName) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (!rowIdExists(newId, table, columnName)) {

            contentValues.put(columnName, newId);
            Log.i("function", "updateHelper: " + table + "  " + columnName);
            db.insert(table, null, contentValues);
        } else {
            db.delete(table, columnName + "=" + newId, null);
        }
        return true;
    }

    //checks if a particular row exist or not
    //if id is string
    public boolean rowIdExists(String newId, String table, String columnName) {

        Log.i("clmn", "rowIdExists: " + columnName);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " WHERE " + columnName + " = '" + newId + "'", null);
        boolean exists = (res.getCount() > 0);

        return exists;
    }

    public String getUserId(){
        String userId = variable.STRING_DEFAULT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from userData", null);
        if(res.getCount() > 0){
            res.moveToFirst();
            userId = res.getString(res.getColumnIndex(variable.userData_COLUMN_USER_ID));
            Log.i("iddd", "getUserId: "+userId);
        }
        return  userId;
    }

    /* get user Email */
     public String getUserEmail(){
        String userEmail = variable.STRING_DEFAULT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from userData", null);
        if(res.getCount() > 0){
            res.moveToFirst();
            userEmail = res.getString(res.getColumnIndex(variable.userData_COLUMN_USER_EMAIL_ID));
        }
        return  userEmail;
    }
    /* get User Image Uri */
    public String getUserImageUri(){
        String userImageUri = variable.STRING_DEFAULT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from userData", null);
        if(res.getCount() > 0){
            res.moveToFirst();
            userImageUri = res.getString(res.getColumnIndex(variable.userData_COLUMN_PROFILE_IMAGE_URI));
        }
        return  userImageUri;
    }
 /* get User user Subscription */
    public String getUserSubscription(){
        String userSubscription = variable.STRING_DEFAULT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from userData", null);
        if(res.getCount() > 0){
            res.moveToFirst();
            userSubscription = res.getString(res.getColumnIndex(variable.userData_COLUMN_PROFILE_SUBSCRIPTION));
        }
        return  userSubscription;
    }

    /* get user Level */
    public int getUserLevel(String userId, long totalPost, long totalLike, long totalShare){
        int retLevel = 1;
        if (totalLike >= 20 && totalPost >= 5  && totalShare >= 1){
            retLevel = 2;
        }
        else if(totalLike >= 50 && totalPost >= 10 && totalShare >= 3)
        {
            retLevel = 3;
        }else if(totalLike >= 75 && totalPost >= 15 && totalShare >= 7)
        {
            retLevel = 4;
        }else if(totalLike >= 100 && totalPost >= 20 && totalShare >= 10)
        {
            retLevel = 5;
        }else if(totalLike >= 140 && totalPost >= 25 && totalShare >= 15)
        {
            retLevel = 6;
        }else if(totalLike >= 200 && totalPost >= 40  && totalShare >= 20)
        {
            retLevel = 7;
        }else if(totalLike >= 300 && totalPost >= 50 && totalShare >= 35)
        {
            retLevel = 8;
        }else if(totalLike >= 400 && totalPost >=  60 && totalShare >= 40)
        {
            retLevel = 9;
        }else if(totalLike >= 500 && totalPost >= 80 && totalShare >= 50)
        {
            retLevel = 10;
        }
        return retLevel;
    }
   //if id is integer
    public boolean rowIdExists(int newId, String table, String columnName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " WHERE " + columnName + " = " + newId + "", null);
        boolean exists = (res.getCount() > 0);

        return exists;
    }

    //-------------------------------------------

    //insert the new bookmark
    public boolean insertBookMark(String bookMarkId)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(variable.bookmark_COLUMN_ID,bookMarkId );
        db.insert("bookmark", null, contentValues);

        return true;
    }

    //delete the bookmarked relation
    public void deleteBookmark(String bookId) {
        bookId =  "'" + bookId + "'";

        Log.i("tag", "insertBookMark: deleted");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(variable.bookmark_TABLE_NAME,
                "bookmarkId = " + bookId,
                null);

    }

    //get the count of all the bookmarked
    public int getBookmarkCount()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from bookmark", null);
        return res.getCount();
    }



//TODO
//----------------------------------------Table- Draft ----------------------------------------------------------------------------

    //insert new draft data
    public boolean insertdraft(String draftId, String draftTitle, String draftContent, Date draftDate, int type) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", draftId);
        contentValues.put("title", draftTitle);
        contentValues.put("content", draftContent);
        contentValues.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(draftDate));
        contentValues.put("type", type);
        db.insert("draft", null, contentValues);
        return true;
    }

    //returns the number of drafts available
    public int getDraftNumber()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from draft", null);
        return res.getCount();
    }

    //return the list of all the drafts
    public ArrayList<draftModel> getAllDraft() {
        ArrayList<draftModel> model = new ArrayList<draftModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM draft", null);
        res.moveToFirst();
        //set the datamodel for adapter
        while (!res.isAfterLast()) {
            model.add(new draftModel(res.getString(res.getColumnIndex(variable.draft_COLUMN_ID)),
                    res.getString(res.getColumnIndex(variable.draft_COLUMN_TITLE)),
                    res.getString(res.getColumnIndex(variable.draft_COLUMN_CONTENT)),
                    res.getString(res.getColumnIndex(variable.draft_COLUMN_DATE))));
            res.moveToNext();
        }
        return model;
    }

    //deletes a specific draft
    public void deleteDraft(String draftId) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(variable.draft_TABLE_NAME,
                "id = ?",
                new String[]{draftId});
        Log.i("delete", "deleteDraft: ");

    }

    //get all draft ID
    public ArrayList<oneFieldModel> getAllDraftIdt()
    {
        ArrayList<oneFieldModel> model = new ArrayList<oneFieldModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM draft", null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            model.add(new oneFieldModel(res.getString(res.getColumnIndex(variable.draft_COLUMN_ID))));
        }
        return model;
    }
//-------------------------------
    public void insertUserDataOnSignUp(String userId, String userName,  String userImageUrl, String description,String subscription){

        /* update the userData table on firebase */
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        Map<String, Object> contentBuff = new HashMap<>();
        contentBuff.put(variable.userData_COLUMN_USER_ID, userId);
        contentBuff.put(variable.userData_COLUMN_USER_NAME, userName );
        contentBuff.put(variable.userData_COLUMN_PROFILE_IMAGE_URI, userImageUrl);
        contentBuff.put(variable.userData_COLUMN_DESCRIONTION, description);
        contentBuff.put(variable.userData_COLUMN_PROFILE_SUBSCRIPTION, subscription);
        //  new SimpleDateFormat("yyyy-MM-dd HH:mm")
        //           .parse(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date))

        fdb.collection(variable.userData_TABLE_NAME)
                .add(contentBuff)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
        insertUserData(userId, userName, userImageUrl, description, 0, "Bronze",variable.STRING_DEFAULT );

    }

    /* delete short post : called from CustomAdapter */
    public void deleteShortPost(String postId, String author){

        if(author.equals(variable.USER_ID)) {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            fb.collection(variable.InputData_TABLE_NAME)
                    .whereEqualTo(variable.InputData_COLUMN_ID, postId)
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
        }

        if(author.equals(variable.USER_ID)) {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            fb.collection(variable.shortPostData_TABLE_NAME)
                    .whereEqualTo(variable.shortPostData_COLUMN_POST_ID, postId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isComplete()) {
                                FirebaseFirestore fdbUpdate = FirebaseFirestore.getInstance();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String documentId = document.getId();
                                    DocumentReference dr = fdbUpdate.collection(variable.shortPostData_TABLE_NAME).document(documentId);
                                    dr.update(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, variable.STRING_DEFAULT);
                                    dr.update(variable.shortPostData_FIREBASE_TIMSTAMP, new Date().getTime());
                                    Log.i("deletePost", "onComplete: " + task.getResult().size());
                                }
                            }
                        }
                    });
        }
        SQLiteDatabase db = getReadableDatabase();
       // ContentValues contentValues = new ContentValues();
       // contentValues.put(variable.InputData_COLUMN_CONTENT, variable.STRING_DEFAULT);
        db.delete(variable.InputData_TABLE_NAME, "id ="+"'"+postId+"'", null);
        //contentValues = new ContentValues();
        //contentValues.put(variable.shortPostData_COLUMN_SHORT_DESCRIPTION, variable.STRING_DEFAULT);
        db.delete(variable.shortPostData_TABLE_NAME, "postId ="+"'"+postId+"'", null);
        db.close();
    }

    public String getParticularImageUriByCode(String imageCode, ImageView image, ProgressBar progressBar){
        //String imageIndexCode = String.valueOf(index);
        String imageUri = variable.STRING_DEFAULT;
        String subFolderName;
        String splitCode[];
        splitCode = imageCode.split("_");
        subFolderName = splitCode[0];
        File file = new File (context.getExternalFilesDir(null), "Srujanee"+"/"+subFolderName);
        //deprecated- File file = new File (Environment.getExternalStorageDirectory(), "Srujanee"+"/"+subFolderName);
        Log.i("usrr", "getParticularImageUriByCode: "+imageCode+" " +subFolderName);
       // File file = new File (Environment.getExternalStorageDirectory(), "Srujanee/"+index+".jpg");
        if(!file.exists()) {
            Log.i("mkdirs", "getAllImageDirectoryUri: "+ file.getAbsolutePath());
            file.mkdirs();
          //  donloadImageFromFirebase();
        }
       File file11 = new File (file.getAbsolutePath()+"/"+imageCode);
        if(!file11.exists()){
            progressBar.setVisibility(View.VISIBLE);
            downloadImageFromFirebaseByImageId(file11 ,subFolderName, imageCode, image, progressBar);
        }

       // imageUri = file.getAbsolutePath()+"/"+imageIndexCode+".png";
        //file = new F

        imageUri = file11.getAbsolutePath();
        return  imageUri;
    }
    public void donloadImageFromFirebase(final File localFile, String subFolderId, final ProgressBar progressBar){
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
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
                         for(StorageReference imageRef : listResult.getItems()){
                             String[] imageUriFirebase;
                             imageUriFirebase = String.valueOf(imageRef).split("/");
                            if(existingImages.contains(imageUriFirebase[imageUriFirebase.length-1]) == false) {
                                    File file = new File(localFile.getAbsolutePath()+"/"+imageUriFirebase[imageUriFirebase.length-1]);
                                    imageRef.getFile(file);
                                    Log.i("stepToImage", "step1: ");
                            }

                         }
                         if(progressBar != null){
                             progressBar.setVisibility(View.GONE);
                         }

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

    public void downloadImageFromFirebaseByImageId(final File localFile, String subFolderId, String imageCode, final ImageView imageFromCard, final ProgressBar progressBar){
        //File localFile = File.createTempFile("images", "jpg");

        /* todo -give check if image not availale in fire base storage but defined by a short post */
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference srujaneeStoage = mStorageRef.child("srujanee/"+subFolderId+"/"+imageCode);
        srujaneeStoage
                .getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        imageFromCard.setImageURI(Uri.parse(localFile.getAbsolutePath()));
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                /* todo set error message in card view / short post */
                progressBar.setVisibility(View.GONE);
            }
        });


       /* StorageReference mStorageRef;
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
                         for(StorageReference imageRef : listResult.getItems()){
                             String[] imageUriFirebase;
                             imageUriFirebase = String.valueOf(imageRef).split("/");
                            if(existingImages.contains(imageUriFirebase[imageUriFirebase.length-1]) == false) {
                             File file = new File(localFile.getAbsolutePath()+"/"+imageUriFirebase[imageUriFirebase.length-1]);
                                imageRef.getFile(file);
                                Log.i("jsjosdfjokse", "onSuccess: ");
                            }

                         }
                         Log.i("downloadImage", "onSuccess: ");
                         //StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");
                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.i("downloadImage", "onFailure: "+ e.getMessage() );
             }
         });*/
      /*  riversRef.getFile(localFile)
                .
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        Log.i("ImageDownload", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
                Log.i("ImageDownload", "onFail: ");
            }
        });*/
    }
//todo  sfdsfasdf
    /* if check is null download should not happen (when called from TOD)*/
    public ArrayList<String> getAllImageDirectoryUri(String subFolderId, ProgressBar progressBar, String check){
        ArrayList<String> imageUriList = new ArrayList<String>();

        //File file = new File (context.getCacheDir(), "Srujanee");
        //File file = new File (context.getExternalFilesDir(null), "Srujanee");
        //

        //deprecated - File file = new File (Environment.getExternalStorageDirectory(), "Srujanee/"+subFolderId);
        File file = new File (context.getExternalFilesDir(null), "Srujanee/"+subFolderId);


        if(!file.exists()) {
            file.mkdirs();
            Log.i("mkdirs", "getAllImageDirectoryUri: ");
           file = new File (context.getExternalFilesDir(null), "Srujanee/"+subFolderId);
           //deprecated -   file = new File (Environment.getExternalStorageDirectory(), "Srujanee/"+subFolderId);
        }
        String imageUriSplit[];
        long imageIndex = -1;
        String imageUri;

        for(File image : file.listFiles()) {
            Log.i("imageuri", "getAllImageDirectoryUri: " + image.getAbsolutePath());
            Log.i("stepToImage", "step2: ");
            imageUri = image.getAbsolutePath();

          /* imageUriSplit = imageUri.split("/");
            imageUriSplit = imageUriSplit[imageUriSplit.length - 1].split("[.]");
            if (imageUriSplit.length > 0) {
                imageIndex = Long.valueOf(imageUriSplit[0]);
            }
            long upperLimit = getImageSubFolderUpperLimit(subFolderId);
            long lowerLimit = getImageSubFolderLowerLimit(subFolderId);
            if (imageIndex > 0 && imageIndex < 10) {

            }
            Log.i("imageuri", "getAllImageDirectoryUri: "+ imageUriSplit.length);*/
            imageUriList.add(imageUri);
        }
        if(check != null) {
            donloadImageFromFirebase(file, subFolderId, progressBar);
        }
        //todo for()

        ArrayList<String> imageUriListReverse = new ArrayList<String>();
        for(int index = imageUriList.size() - 1 ; index >= 0 ; index--){
            imageUriListReverse.add(imageUriList.get(index));
        }
       // Log.i("imageUrii", "getLocalImageUri: " + imageUriList.get(0));
        return imageUriListReverse;
    }

    public ArrayList<DataModel> getAllImageDirectoryUriMainActivity(String subFolderId, ProgressBar progressBar, String check){
        ArrayList<String> imageUriList = new ArrayList<String>();

        //File file = new File (context.getCacheDir(), "Srujanee");
        //File file = new File (context.getExternalFilesDir(null), "Srujanee");
        //

        //deprecated - File file = new File (Environment.getExternalStorageDirectory(), "Srujanee/"+subFolderId);
        File file = new File (context.getExternalFilesDir(null), "Srujanee/"+subFolderId);


        if(!file.exists()) {
            file.mkdirs();
            Log.i("mkdirs", "getAllImageDirectoryUri: ");
           file = new File (context.getExternalFilesDir(null), "Srujanee/"+subFolderId);
           //deprecated -   file = new File (Environment.getExternalStorageDirectory(), "Srujanee/"+subFolderId);
        }
        String imageUriSplit[];
        long imageIndex = -1;
        String imageUri;

        for(File image : file.listFiles()) {
            Log.i("imageuri", "getAllImageDirectoryUri: " + image.getAbsolutePath());
            Log.i("stepToImage", "step2: ");
            imageUri = image.getAbsolutePath();

          /* imageUriSplit = imageUri.split("/");
            imageUriSplit = imageUriSplit[imageUriSplit.length - 1].split("[.]");
            if (imageUriSplit.length > 0) {
                imageIndex = Long.valueOf(imageUriSplit[0]);
            }
            long upperLimit = getImageSubFolderUpperLimit(subFolderId);
            long lowerLimit = getImageSubFolderLowerLimit(subFolderId);
            if (imageIndex > 0 && imageIndex < 10) {

            }
            Log.i("imageuri", "getAllImageDirectoryUri: "+ imageUriSplit.length);*/
            imageUriList.add(imageUri);
        }
        if(check != null) {
            donloadImageFromFirebase(file, subFolderId, progressBar);
        }
        //todo for()

        ArrayList<DataModel> imageUriListReverse = new ArrayList<DataModel>();
        for(int index = imageUriList.size() - 1 ; index >= 0 ; index--){
            imageUriListReverse.add(new DataModel(subFolderId,imageUriList.get(index)));
        }
       // Log.i("imageUrii", "getLocalImageUri: " + imageUriList.get(0));
        return imageUriListReverse;
    }



    /* during app installation download all images and store to local directory */
    public void downloadAllImageFromFirebase(){
        String imageUri = variable.STRING_DEFAULT;
        //File file = new File (context.getExternalFilesDir(null), "Srujanee");
        File file = new File (Environment.getExternalStorageState(), "Srujanee");
        if(!file.exists()) {
            file.mkdir();
            Log.i("mkdirs", "getAllImageDirectoryUri: ");

        }

    }

    //-------------------------------image subfolder code --------------------------------------------------------------------------------
    public void insertImageSubFolder()
    {
        /* clear the table and refresh with server data */
        SQLiteDatabase db = getWritableDatabase();
        //db.delete(variable.IMAGE_SUBFOLDER_TABLE_NAME, null, null);
        db.close();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection(variable.IMAGE_SUBFOLDER_TABLE_NAME)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot document : task.getResult()){
                    if(task.isComplete()){
                        insertImageSubFolderHelper(
                                document.getString(variable.IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID),
                                document.getLong(variable.IMAGE_SUBFOLDER_COLUMN_LOWER_LIMIT),
                                document.getLong(variable.IMAGE_SUBFOLDER_COLUMN_UPPER_LIMIT),
                                document.getLong(variable.IMAGE_SUBFOLDER_COLUMN_EXISTING)
                        );
                        Log.i("checkimage", "onComplete: "+ document.getString(variable.IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID));
                        if(document.getString(variable.IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID).equals(variable.IMAGE_SUBFOLDER_DEFAULT) ){
                            // getAllImageDirectoryUri()
                        }
                    }
                }
            }
        });
    }
    public void insertImageSubFolderHelper(String subFolderId, long lowerLimit, long upperLimit, long existing){
       SQLiteDatabase db = getWritableDatabase();
       ContentValues contentValues = new ContentValues();
       contentValues.put(variable.IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID, subFolderId);
       contentValues.put(variable.IMAGE_SUBFOLDER_COLUMN_LOWER_LIMIT, lowerLimit);
       contentValues.put(variable.IMAGE_SUBFOLDER_COLUMN_UPPER_LIMIT, upperLimit);
       contentValues.put(variable.IMAGE_SUBFOLDER_COLUMN_EXISTING, existing);
       db.insert(variable.IMAGE_SUBFOLDER_TABLE_NAME, null , contentValues);
       db.close();
    }

    public  ArrayList<String> getImageSubFolderNames(){
        ArrayList<String> imageSubfolderNames = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select * from imageSubFolder", null);
        res.moveToFirst();
        while (!res.isAfterLast()){
            imageSubfolderNames.add(res.getString(res.getColumnIndex(variable.IMAGE_SUBFOLDER_COLUMN_SUBFOLDER_ID)));
            res.moveToNext();
        }
        return imageSubfolderNames;
    }

    public long getImageSubFolderUpperLimit(String subFolderId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select upperLimit from imageSubFolder where imageSubFolderId = '"+subFolderId+"'", null);
        if(res.moveToFirst()){
            return  res.getLong(res.getColumnIndex(variable.IMAGE_SUBFOLDER_COLUMN_UPPER_LIMIT));
        }
        return variable.DEFAULT_LONG;
    }
    public long getImageSubFolderLowerLimit(String subFolderId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery("Select lowerLimit from imageSubFolder where imageSubFolderId = '"+subFolderId+"'", null);
        if(res.moveToFirst()){
            return  res.getLong(res.getColumnIndex(variable.IMAGE_SUBFOLDER_COLUMN_LOWER_LIMIT));
        }
        return variable.DEFAULT_LONG;
    }
    public void fetchPositiveRelationFromFirebase()
    {
        FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    }

    public void deleteSqlTable(String tableName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("Delete from '"+tableName+"'");
        db.close();
        Log.i("sqlDelete", "deleteSqlTable: ");
    }


    public boolean isConnectedToInternet() {
        /* todo- wifi connectivity check */
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetwork() == null)
            return  false;
        else
            return  true;
    }

//----------------------------------------END OF ESSAY SECTION-----------------------------------------------------------------------
}