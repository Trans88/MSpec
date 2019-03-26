package com.trans.mspec;

import java.util.ArrayList;
import java.util.List;

public class ImageInfo {
    private List<List<Integer>> mGrayList=new ArrayList<>();

    public ImageInfo() {
    }
    public void addImage(List<Integer> mGray){
        mGrayList.add(mGray);
    }

    public void setmGrayList(List<List<Integer>> mGrayList) {
        this.mGrayList = mGrayList;
    }

    public List<List<Integer>> getmGrayList() {
        return mGrayList;
    }
}
