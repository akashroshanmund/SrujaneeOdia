package com.srujanee.sahitya11;

import java.util.ArrayList;

public class DataWrapper {
    String categoryName;
    ArrayList<DataModel> dataset;
    int type;
    int container;
    DataWrapper(String categoryName, ArrayList<DataModel> list, int type, int container)
    {
        this.categoryName = categoryName;
        this.dataset = list;
        this.type = type;
        this.container = container;
    }
    public String getCategoryName()
    {
        return this.categoryName;
    }

    public int getContainer() {
        return container;
    }
    public int getType(){
        return type;
    }
}
