package com.wkl.photoselector.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wkl.photoselector.R;
import com.wkl.photoselector.entity.Photo;
import com.wkl.photoselector.entity.PhotoDirectory;
import com.wkl.photoselector.listener.OnItemCheckListener;
import com.wkl.photoselector.util.ViewHolder;

/**
 * 图片适配器
 * 
 * @author wkl
 */
public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<PhotoDirectory> directories;
    private FragmentActivity activity;
    private int directoryIndex = 0;
    private ArrayList<String> selectedPhotos; // 选中的图片路径
    private boolean showCamera = false;
    private OnItemCheckListener onItemCheckListener;

    public PhotoAdapter(FragmentActivity activity,
            List<PhotoDirectory> directories) {
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        this.directories = directories;
        selectedPhotos = new ArrayList<String>();
    }

    public void showCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    /**
     * 设置选中的路径
     * 
     * @param directoryIndex
     */
    public void setDirectoryIndex(int directoryIndex) {
        this.directoryIndex = directoryIndex;
    }

    public int getDirectoryIndex() {
        return directoryIndex;
    }

    /**
     * 当前目录的图片
     * 
     * @return
     */
    public List<Photo> getCurrentPhotos() {
        return directories.get(directoryIndex).getPhotos();
    }

    /**
     * 是否选中
     * 
     * @param photo
     * @return
     */
    public boolean isSelected(String path) {
        return selectedPhotos.contains(path);
    }

    /**
     * 选择图片
     * 
     * @param photo
     */
    public void selectPhoto(String path) {
        if (isSelected(path)) {
            selectedPhotos.remove(path);
        } else {
            selectedPhotos.add(path);
        }
    }

    /**
     * 已选择图片数量
     * 
     * @return
     */
    public int getSelectedCount() {
        return selectedPhotos.size();
    }

    public void clearSelection() {
        selectedPhotos.clear();
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public int getCount() {
        return getCurrentPhotos().size() + (showCamera ? 1 : 0);
    }

    @Override
    public Object getItem(int position) {
        return getCurrentPhotos().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getCurrentPhotos().get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_photo, parent, false);
        }

        ImageView ivPhoto = ViewHolder.get(convertView, R.id.iv_photo);
        ImageView ivSelect = ViewHolder.get(convertView, R.id.iv_selected);
        if (showCamera && position == 0) {
            ivPhoto.setImageResource(R.drawable.camera);
            ivSelect.setVisibility(View.GONE);
        } else {
            ivSelect.setVisibility(View.VISIBLE);
            List<Photo> photos = getCurrentPhotos();
            final Photo photo;
            if (showCamera) {
                photo = photos.get(position - 1);
            } else {
                photo = photos.get(position);
            }
            Glide.with(activity).load(photo.getPath()).centerCrop()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .error(R.drawable.ic_broken_image_black_48dp).into(ivPhoto);
            final boolean selected = isSelected(photo.getPath());
            ivPhoto.setSelected(selected);
            ivSelect.setSelected(selected);
            ivSelect.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    boolean check = true;
                    if (onItemCheckListener != null) {
                        check = onItemCheckListener.onItemCheck(position,
                                photo, selected, getSelectedCount());
                    }
                    if (check) {
                        selectPhoto(photo.getPath());
                        notifyDataSetChanged();
                    }
                }
            });
        }
        return convertView;
    }

    public ArrayList<String> getSelectedPaths() {
        return selectedPhotos;
    }

    /**
     * 当前目录的图片路径
     * 
     * @return
     */
    public List<String> getPhotoPaths() {
        List<String> currentPhotoPaths = new ArrayList<String>(
                getCurrentPhotos().size());
        for (Photo photo : getCurrentPhotos()) {
            currentPhotoPaths.add(photo.getPath());
        }
        return currentPhotoPaths;
    }

}
