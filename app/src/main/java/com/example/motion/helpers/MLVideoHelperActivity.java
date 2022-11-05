package com.example.motion.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motion.R;
import com.example.motion.helpers.vision.CameraSource;
import com.example.motion.helpers.vision.CameraSourcePreview;
import com.example.motion.helpers.vision.GraphicOverlay;

import java.io.IOException;

public abstract class MLVideoHelperActivity extends AppCompatActivity{
    // Attributes with predefined classes
    private GraphicOverlay graphicOverlay;
    protected CameraSource cameraSource;

    // Attributes with user-defined classes
    private static final int REQUEST_CAMERA = 1001;
    private static final String TAG = "MLVideoHelperActivity";
    private CameraSourcePreview cameraScreen;

    // Objects
    public MotionManager motionManager;
    public MotionProcessor motionProcessor;

    // Activity's Life Cycle Functions
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_helper);
        connectToXML();
        instantiateMotionProcessor();
        requestCameraPermission();
        motionManager = new MotionManager(this, motionProcessor);
    }

    private void connectToXML(){
        graphicOverlay = findViewById(R.id.graphic_overlay);
        cameraScreen = findViewById(R.id.camera_screen);
    }

    private void instantiateMotionProcessor() {
        motionProcessor = new MotionProcessor(new MotionListener() {
            @Override
            public void verifyUserPose(boolean isDoingSelectedPose) {
                if(isDoingSelectedPose && !motionManager.getIsCountingDown()){
                    motionManager.startCountdown();
                }
            }
        });
    }

    public void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            initSource();
            startCameraSource();
        }
    }


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


    // Pre-defined Camera Functions
    private void initSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        setProcessor();
    }

    protected abstract void setProcessor();

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (cameraScreen == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                cameraScreen.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}
