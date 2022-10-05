package com.example.motion.object;

import android.os.Bundle;

import com.example.motion.helpers.MLVideoHelperActivity;
import com.example.motion.helpers.vision.FaceDetectorProcessor;

public class DriverDrowsinessDetectionActivity extends MLVideoHelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setProcessor() {
        cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));
    }
}
