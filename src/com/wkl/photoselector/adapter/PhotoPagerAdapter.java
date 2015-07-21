package com.wkl.photoselector.adapter;

import java.io.File;
import java.util.List;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wkl.photoselector.R;

/**
 * PagerAdapter
 * 
 * @author wkl
 */
public class PhotoPagerAdapter extends PagerAdapter {
    private FragmentActivity activity;
    private List<String> paths;
    private LayoutInflater inflater;

    public PhotoPagerAdapter(FragmentActivity activity, List<String> paths) {
        this.activity = activity;
        this.paths = paths;
        this.inflater = activity.getLayoutInflater();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = inflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);

        final String path = paths.get(position);
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(activity).load(uri).override(800, 800)
                .error(R.drawable.ic_broken_image_black_48dp).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        container.addView(itemView);

        return itemView;

    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
