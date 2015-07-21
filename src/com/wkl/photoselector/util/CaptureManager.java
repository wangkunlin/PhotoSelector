package com.wkl.photoselector.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * 管理
 * 
 * @author wkl
 */
public class CaptureManager {

    private final static String CAPTURED_PHOTO_PATH_KEY = "currentPhotoPath";

    private String photoPath;
    private Context mContext;

    public CaptureManager(Context mContext) {
        this.mContext = mContext;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "-.jpg";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String dcimPath = storageDir.getAbsolutePath() + "/Camera";
        storageDir = new File(dcimPath);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                throw new IOException("make dirs failed!");
            }
        }
        photoPath = dcimPath + "/" + imageFileName;
        File image = new File(photoPath);
        return image;
    }

    /**
     * 生成拍照intent
     * 
     * @return
     * @throws IOException
     *             失败
     */
    public Intent obtainCaptureIntent() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                throw new IOException("create file failed!");
            }
        }
        return intent;
    }

    /**
     * 发送广播，添加图片到数据库
     */
    public void sendBroadcast() {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    public String getCurrentPhotoPath() {
        return photoPath;
    }

    /**
     * 用于横竖屏幕发生变化时的状态保存，对应于activity或者fragment的onSaveInstanceState方法<br>
     * 如屏幕不会横竖切换，则不需调用
     * 
     * @hide
     * @param savedInstanceState
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null && photoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, photoPath);
        }
    }

    /**
     * 屏幕变化后恢复状态，对应于activity或者fragment的onRestoreInstanceState方法<br>
     * 如屏幕不会横竖切换，则不需调用
     * 
     * @hide
     * @param savedInstanceState
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            photoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
    }

}
