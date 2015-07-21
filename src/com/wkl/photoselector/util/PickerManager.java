package com.wkl.photoselector.util;

import android.content.Intent;

/**
 * 
 * @author wkl
 */
public class PickerManager {

    private final static String PHOTO_PICKER_ACTIVITY = "com.wkl.photoselector.PHOTO_PICKER_ACTIVITY";
    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String SELECTED_PHOTOS = "SELECTED_PHOTOS";

    private int maxCount = 1;
    private boolean showCamera = true;

    public PickerManager() {
    }

    public PickerManager(int maxCount, boolean showCamera) {
        if (maxCount < 1) {
            maxCount = 1;
        }
        this.maxCount = maxCount;
        this.showCamera = showCamera;
    }

    /**
     * 默认为1
     * 
     * @param maxCount
     */
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    /**
     * 默认显示
     * 
     * @param showCamera
     */
    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    /**
     * 生成选择图片的intent
     * 
     * @return
     */
    public Intent obtainPickerIntent() {
        Intent i = new Intent(PHOTO_PICKER_ACTIVITY);
        i.putExtra(EXTRA_MAX_COUNT, maxCount);
        i.putExtra(EXTRA_SHOW_CAMERA, showCamera);
        return i;
    }
}
