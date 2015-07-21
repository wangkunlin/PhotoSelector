package com.wkl.photoselector;

import static com.wkl.photoselector.util.PickerManager.EXTRA_MAX_COUNT;
import static com.wkl.photoselector.util.PickerManager.EXTRA_SHOW_CAMERA;
import static com.wkl.photoselector.util.PickerManager.SELECTED_PHOTOS;
import static com.wkl.photoselector.util.MediaStoreHelper.INDEX_ALL_PHOTOS;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wkl.photoselector.adapter.DirectoryAdapter;
import com.wkl.photoselector.adapter.PhotoAdapter;
import com.wkl.photoselector.entity.Photo;
import com.wkl.photoselector.entity.PhotoDirectory;
import com.wkl.photoselector.listener.OnItemCheckListener;
import com.wkl.photoselector.util.CaptureManager;
import com.wkl.photoselector.util.MediaStoreHelper;
import com.wkl.photoselector.util.MediaStoreHelper.PhotosResultCallback;

/**
 * 选择图片
 * 
 * @author wkl
 */
public class PhotoPickerActivity extends FragmentActivity implements
        OnItemCheckListener, OnClickListener {

    private CaptureManager captureManager;
    private int maxCount = 1; // 可选图片的数量
    private boolean showCamera = true;
    private List<PhotoDirectory> directories;
    private DrawerLayout drawerLayout;
    private GridView gridView;
    private PhotoAdapter photoAdapter;
    private ListView listView;
    private TextView title;
    private TextView numLimit;
    private DirectoryAdapter directoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_picker_activity);

        captureManager = new CaptureManager(this);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = getWindowManager();
        wm.getDefaultDisplay().getMetrics(dm);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        gridView = (GridView) findViewById(R.id.grid);
        listView = (ListView) findViewById(R.id.list);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, maxCount);
        if (maxCount < 0) {
            maxCount = 1;
        }

        title = (TextView) findViewById(R.id.title);
        title.setOnClickListener(this);
        numLimit = (TextView) findViewById(R.id.num);
        numLimit.setOnClickListener(this);
        numLimit.setText(getString(R.string.done_with_count, 0, maxCount));
        numLimit.setEnabled(false);

        DrawerLayout.LayoutParams lp = (LayoutParams) listView
                .getLayoutParams();
        lp.width = dm.widthPixels * 2 / 3;
        directories = new ArrayList<PhotoDirectory>();
        photoAdapter = new PhotoAdapter(this, directories);
        photoAdapter.showCamera(showCamera);
        photoAdapter.setOnItemCheckListener(this);
        directoryAdapter = new DirectoryAdapter(this, directories);
        MediaStoreHelper.getPhotoDirs(this, new PhotosResultCallback() {

            @Override
            public void onResultCallback(List<PhotoDirectory> dirs) {
                if (directories.size() > 0) {
                    directories.clear();
                }
                directories.addAll(dirs);
                gridView.setAdapter(photoAdapter);
                listView.setAdapter(directoryAdapter);
                listView.setItemChecked(0, true);
                title.setText(directories.get(0).getName());
            }
        });
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (showCamera && position == 0) { // 相机
                    try {
                        Intent intent = captureManager.obtainCaptureIntent();
                        startActivityForResult(intent, 101);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                R.string.capture_failed, Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                    return;
                }
                final int index = showCamera ? position - 1 : position;
                List<String> paths = photoAdapter.getPhotoPaths();
                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);
                Intent i = new Intent(
                        "com.wkl.photoselector.PHOTO_PAGER_ACTIVITY");
                Bundle args = PhotoPagerActivity.createArgs(paths, index);
                i.putExtras(args);
                startActivityForResult(i, 100);
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int index = photoAdapter.getDirectoryIndex();
                if (index != position) {
                    listView.setItemChecked(position, true);
                    photoAdapter.setDirectoryIndex(position);
                    photoAdapter.notifyDataSetChanged();
                    gridView.smoothScrollToPosition(0);
                    title.setText(directories.get(position).getName());
                }
                drawerLayout.closeDrawer(listView);
            }
        });
    }

    @Override
    public boolean onItemCheck(int position, Photo path, boolean isCheck,
            int selectedCount) {
        int total = selectedCount + (isCheck ? -1 : 1);

        if (maxCount == 1) { // 单选模式
            photoAdapter.clearSelection();
            photoAdapter.notifyDataSetChanged();
            if (total > 1) {
                total = 1;
            }
            numLimit.setText(getString(R.string.done_with_count, total,
                    maxCount));
            if (total == 0) {
                numLimit.setEnabled(false);
                return false;
            }
            numLimit.setEnabled(true);
            return true;
        }

        if (total > maxCount) { // 多选模式
            Toast.makeText(this,
                    getString(R.string.over_max_count_tips, maxCount),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (total > 0) {
            numLimit.setEnabled(true);
        } else {
            numLimit.setEnabled(false);
        }
        numLimit.setText(getString(R.string.done_with_count, total, maxCount));
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) { // 预览单张图片
                int position = data.getIntExtra("postion", -1);
                if (position > -1) {
                    position = showCamera ? position + 1 : position;
                    gridView.smoothScrollToPosition(position);
                }
            } else if (requestCode == 101) { // 拍照
                captureManager.sendBroadcast();
                if (directories.size() > 0) {
                    String path = captureManager.getCurrentPhotoPath();
                    PhotoDirectory directory = directories
                            .get(INDEX_ALL_PHOTOS);
                    directory.getPhotos().add(INDEX_ALL_PHOTOS,
                            new Photo(path.hashCode(), path));
                    directory.setCoverPath(path);
                    photoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.num) { // 完成按钮
            Intent intent = new Intent();
            intent.putExtra(EXTRA_MAX_COUNT, maxCount);
            intent.putStringArrayListExtra(SELECTED_PHOTOS,
                    photoAdapter.getSelectedPaths());
            setResult(RESULT_OK, intent);
            finish();
        } else if (id == R.id.title) { // 相册按钮
            if (drawerLayout.isDrawerOpen(listView)) {
                drawerLayout.closeDrawer(listView);
            } else {
                drawerLayout.openDrawer(listView);
            }
        }
    }

}
