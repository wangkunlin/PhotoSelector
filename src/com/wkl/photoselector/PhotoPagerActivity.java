package com.wkl.photoselector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.wkl.photoselector.adapter.PhotoPagerAdapter;

public class PhotoPagerActivity extends FragmentActivity {

    public final static long ANIM_DURATION = 200L;
    public final static String ARG_PATH = "PATHS";
    public final static String ARG_CURRENT_ITEM = "ARG_CURRENT_ITEM";

    private ArrayList<String> paths;
    private ViewPager viewPager;
    private PhotoPagerAdapter pagerAdapter;
    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_pager_activity);

        paths = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String[] pathArr = bundle.getStringArray(ARG_PATH);
            paths.clear();
            if (pathArr != null) {

                paths = new ArrayList<String>(Arrays.asList(pathArr));
            }
            currentItem = bundle.getInt(ARG_CURRENT_ITEM);

        }

        pagerAdapter = new PhotoPagerAdapter(this, paths);
        viewPager = (ViewPager) findViewById(R.id.vp_photos);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("postion", viewPager.getCurrentItem());
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * 创建参数
     * 
     * @param paths
     * @param currentItem
     * @return
     */
    public static Bundle createArgs(List<String> paths, int currentItem) {
        Bundle args = new Bundle();
        args.putStringArray(ARG_PATH, paths.toArray(new String[paths.size()]));
        args.putInt(ARG_CURRENT_ITEM, currentItem);
        return args;
    }
}
