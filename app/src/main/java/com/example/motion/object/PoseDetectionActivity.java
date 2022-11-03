package com.example.motion.object;

import android.os.Bundle;

import com.example.motion.helpers.MLVideoHelperActivity;
import com.example.motion.helpers.PoseClassificationChecker;
import com.example.motion.helpers.vision.posedetector.PoseDetectorProcessor;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

public class PoseDetectionActivity extends MLVideoHelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setProcessor() {

        AccuratePoseDetectorOptions options = new AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build();
        PoseDetectorProcessor poseDetectorProcessor = new PoseDetectorProcessor(this, options, true, true, super.poseClassificationChecker);
        cameraSource.setMachineLearningFrameProcessor(poseDetectorProcessor);
    }
}
