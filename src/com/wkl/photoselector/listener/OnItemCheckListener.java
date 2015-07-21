package com.wkl.photoselector.listener;

import com.wkl.photoselector.entity.Photo;

/**
 * item选中监听
 * 
 * @author wkl
 * 
 */
public interface OnItemCheckListener {
    /***
     * 
     * @param position
     *            所选图片的位置
     * @param path
     *            所选的图片
     * @param isCheck
     *            当前状态
     * @param selectedCount
     *            已选数量
     * @return enable check
     */
    public boolean onItemCheck(int position, Photo path, boolean isCheck,
            int selectedCount);
}
