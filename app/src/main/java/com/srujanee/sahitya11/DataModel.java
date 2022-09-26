package com.srujanee.sahitya11;

import java.io.Serializable;
import java.util.ArrayList;

public class DataModel {

    //Content data
    String InputDataId;
    String InputDataTitle;
    String InputDataAuthor;
    String InputDataContent;
    long InputDataDate;
    String InputDataTag;
    long InputDataReaction;
    long InputDataEditorChoice;

    //categoryData variable
    String outerTopic;
    int interest;
    String imageCode;
    String innerTopic;
    int type;
    int container;

    //trending specific
    String trendingContent;
    String trendingTitle;
    String trendingPostId;
    String trendingAuthorName;
    long postType = variable.DEFAULT;

    //Fun and TOD/general specific
    String uriOfImage;

    //Essay data
    //todo

    //InputData dataModel loader
    public DataModel(String id, String title, String author, String content, long date, String tag, long reaction, long editorChoice ) {
        this.InputDataId = id;
        this.InputDataTitle = title;
        this.InputDataAuthor = author;
        this.InputDataContent = content;
        this.InputDataDate = date;
        this.InputDataTag = tag;
        this.InputDataReaction = reaction;
        this.InputDataEditorChoice = editorChoice;
    }
    //Essay dataModel Loader
    //Todo

    //get Content data

    public String getContentId(){
        return this.InputDataId;
    }
    public String getContentTitle(){
        return this.InputDataTitle;
    }
    public String getContentAuthor(){
        return this.InputDataAuthor;
    }
    public String getContent() {
        return InputDataContent;
    }
    public long getContentDate(){
        return this.InputDataDate;
    }
    public String getContentTag(){
        return this.InputDataTag;
    }
    public long getReactionCount(){return this.InputDataReaction; }
    public long getInputDataEditorChoice(){
        return this.InputDataEditorChoice;
    }

    //categoryData dataModel loader
    public DataModel(String outerTopic, String  innerTopic, int interest, String imageCode, long type, long container)
    {
        this.outerTopic = outerTopic;
        this.innerTopic = innerTopic;
        this.interest = interest;
        this.imageCode = imageCode;
        this.type = (int) type;
        this.container = (int) container;

    }

    //specific to trending topics
    public DataModel(String outerTopic, String  title, String content, String imageCode, String postId, long type, String authorName)
    {
        this.outerTopic = outerTopic;
        this.trendingContent = content;
        this.trendingPostId = postId;
        this.imageCode = imageCode;
        this.trendingTitle = title;
        this.postType = type;
        this.trendingAuthorName = authorName;
    }

    //Specific to FUN and TOD/General
    public DataModel(String outerTopic, String uriOfImage)
    {
        this.outerTopic = outerTopic;
        this.uriOfImage = uriOfImage;

    }


    public String getTrendingPostId()
    {
        return this.trendingPostId;
    }
    public String getTrendingAuthorName(){
        return  this.trendingAuthorName;
    }
    public  long getPostType()
    {
        return this.postType;
    }
    //get category data
    public String getOuterTopic()
    {
        return this.outerTopic;
    }
    public String getInnerTopic(){
        return this.innerTopic;
    }
    public int getInterest(){
        return this.interest;
    }
    public String getImageCode(){
        return this.imageCode;
    }
    public  int getCategoryType(){
        return this.type;
    }
    public  int getCategoryContainer(){
        return container;
    }

    public String getTrendingContent(){
        return this.trendingContent;
    }
    public String getTrendingTitle(){
        return  this.trendingTitle;
    }

    public String getUriOfImage(){
        return this.uriOfImage;
    }

    String categoryName;
    ArrayList<DataModel> dataSet;
    public DataModel(String categoryName, ArrayList<DataModel> dataSet)
    {
        this.categoryName = categoryName;
        this.dataSet = dataSet;
    }


}
class draftModel{
    private String draftId;
   // private String draftAuthor;
    private String draftTitle;
    private String draftContent;
    private String draftDate;
    private String draftTag;
    draftModel(String id, String title, String content, String date)
    {
      this.draftTitle = title;
      this.draftId = id;
      this.draftContent = content;
      this.draftDate = date;
    //  this.draftTag = tag;
    }

    public String getDraftId()
    {
        return this.draftId;
    }
 //   public String getDraftAuthor()
    {
  //      return this.draftAuthor;
    }
    public String getDraftTitle(){
        return this.draftTitle;
    }
    public String getDraftContent()
    {
        return this.draftContent;
    }
    public String getDraftDate()
    {
        return this.draftDate;
    }
    public String getDraftTag()
    {
        return this.draftTag;
    }

}

class oneFieldModel{
    private String variable;
    public oneFieldModel(String variable)
    {
        this.variable = variable;
    }
    public String getOneField()
    {
        return this.variable;
    }
}

class shortPostDetails{
    private long textPositionIndex;
    private long textSizeIndex;
    private String textColourIndex;
    private String textImageIndex;
    private String shortDescription;

    shortPostDetails(String backgroundIndex, long sizeIndex, String colourIndex, long positionIndex, String shortDescription)
    {

        this.textImageIndex = backgroundIndex;
        this.textSizeIndex = sizeIndex;
        this.textColourIndex = colourIndex;
        this.textPositionIndex = positionIndex;
        this.shortDescription = shortDescription;
    }

    public long getTextPositionIndex(){
        return this.textPositionIndex;
    }
    public long getTextSizeIndex(){
        return this.textSizeIndex;
    }

    public String getTextColourIndex() {
        return textColourIndex;
    }

    public String getTextImageIndex() {
        return textImageIndex;
    }

    public String getShortDescription(){
        return this.shortDescription;
    }
}

class profileDataModel implements Serializable {
    private String userId;

    public profileDataModel(String userId){
        this.userId = userId;
    }

    public String getProfileUserId(){
        return this.userId;
    }
}

