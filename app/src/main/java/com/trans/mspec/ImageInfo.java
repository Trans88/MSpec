package com.trans.mspec;

import java.util.List;

public class ImageInfo {
    private List<Gray> mGrayList;

    public ImageInfo(List<Gray> mGrayList) {
        this.mGrayList = mGrayList;
    }

    public List<Gray> getmGrayList() {
        return mGrayList;
    }

    public void setmGrayList(List<Gray> mGrayList) {
        this.mGrayList = mGrayList;
    }
}
