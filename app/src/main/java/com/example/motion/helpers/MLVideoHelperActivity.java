package com.example.motion.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motion.R;
import com.example.motion.helpers.vision.CameraSource;
import com.example.motion.helpers.vision.CameraSourcePreview;
import com.example.motion.helpers.vision.GraphicOverlay;
import com.example.motion.helpers.vision.posedetector.PoseDetectorProcessor;

import java.io.IOException;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public abstract class MLVideoHelperActivity extends AppCompatActivity {
    // Attributes with predefined classes
    private GraphicOverlay graphicOverlay;
    protected CameraSource cameraSource;

    // Attributes with user-defined classes
    private static final int REQUEST_CAMERA = 1001;
    private static final String TAG = "MLVideoHelperActivity";
    private CameraSourcePreview preview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_helper);

        preview = findViewById(R.id.preview_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        ImageButton warriorBtn = findViewById(R.id.btn_warrior2);
        ImageButton goddessBtn = findViewById(R.id.btn_goddess);
        ImageButton treeBtn = findViewById(R.id.btn_tree);

            // Requests permission to access the device's camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            initSource();
            startCameraSource();
            clickPoseBtn(warriorBtn, goddessBtn, treeBtn);

        }
    }

    //Actions to do for the buttons
    private void clickPoseBtn(ImageButton warriorBtn, ImageButton goddessBtn, ImageButton treeBtn) {
        MLVideoHelperActivity context = MLVideoHelperActivity.this;
        warriorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(context, "Please perform Warrior Pose", Toast.LENGTH_SHORT, R.style.mytoast).show();
                warriorBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_pressed));
                goddessBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
                treeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
            }
        });

        goddessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(context, "Please perform Goddess Pose", Toast.LENGTH_SHORT, R.style.mytoast).show();
                goddessBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_pressed));
                warriorBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
                treeBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
            }
        });

        treeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(context, "Please perform Tree Pose", Toast.LENGTH_SHORT, R.style.mytoast).show();
                treeBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_pressed));
                warriorBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
                goddessBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
            }
        });
    }

    // Pre-defined Methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSource();
            startCameraSource();
        }
    }

    private void initSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        setProcessor();
    }

    protected abstract void setProcessor();

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    public void clickSelectedPose(View view) {
        StyleableToast.makeText(this, "Please perform selected pose!", Toast.LENGTH_SHORT, R.style.mytoast).show();
    }
}
