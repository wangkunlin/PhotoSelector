package com.wkl.photoselector.adapter;

import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wkl.photoselector.R;
import com.wkl.photoselector.entity.PhotoDirectory;
import com.wkl.photoselector.util.ViewHolder;

/**
 * 文件夹
 * 
 * @author wkl
 */
public class DirectoryAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PhotoDirectory> directories;
    private FragmentActivity activity;

    public DirectoryAdapter(FragmentActivity activity,
            List<PhotoDirectory> directories) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.directories = directories;
    }

    @Override
    public int getCount() {
        return directories.size();
    }

    @Override
    public Object getItem(int position) {
        return directories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return directories.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_directory, parent,
                    false);
        }
        ImageView ivCover = ViewHolder.get(convertView, R.id.iv_dir_cover);
        TextView tvName = ViewHolder.get(convertView, R.id.tv_dir_name);
        PhotoDirectory directory = directories.get(position);
        Glide.with(activity).load(directory.getCoverPath()).thumbnail(0.1f)
                .into(ivCover);
        tvName.setText(directory.getName());
        return convertView;
    }

}
