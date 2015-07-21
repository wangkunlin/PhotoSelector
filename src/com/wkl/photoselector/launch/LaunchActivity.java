package com.wkl.photoselector.launch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.wkl.photoselector.R;
import com.wkl.photoselector.util.PickerManager;

public class LaunchActivity extends Activity {

    TextView res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_activity);
        res = (TextView) findViewById(R.id.result);
        findViewById(R.id.multi).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PickerManager picker = new PickerManager();
                picker.setMaxCount(4);
                picker.setShowCamera(true);
                startActivityForResult(picker.obtainPickerIntent(), 100);
            }
        });
        findViewById(R.id.single).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PickerManager picker = new PickerManager();
                picker.setMaxCount(1);
                picker.setShowCamera(true);
                startActivityForResult(picker.obtainPickerIntent(), 101);
            }
        });
        findViewById(R.id.no_capture).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PickerManager picker = new PickerManager(7, false);
                startActivityForResult(picker.obtainPickerIntent(), 102);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> paths = data
                    .getStringArrayListExtra(PickerManager.SELECTED_PHOTOS);
            if (paths != null && paths.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("result:");
                for (String string : paths) {
                    sb.append("\n").append(string).append("\n");
                }
                res.setText(sb.toString());
            }
        }
    }
}
