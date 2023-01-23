package com.example.motion.object;

import static com.google.mlkit.vision.pose.PoseDetectorOptionsBase.CPU;
import static com.google.mlkit.vision.pose.PoseDetectorOptionsBase.CPU_GPU;

import android.os.Bundle;
import android.view.View;

import com.example.motion.helpers.MLVideoHelperActivity;
import com.example.motion.helpers.vision.posedetector.PoseDetectorProcessor;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;

public class PoseDetectionActivity extends MLVideoHelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setProcessor() {

        AccuratePoseDetectorOptions.Builder builder =
                new AccuratePoseDetectorOptions.Builder()
                        .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE);

        builder.setPreferredHardwareConfigs(CPU);

        AccuratePoseDetectorOptions options = builder.build();

        PoseDetectorProcessor poseDetectorProcessor = new PoseDetectorProcessor(this, options, true, true, super.motionProcessor);
        cameraSource.setMachineLearningFrameProcessor(poseDetectorProcessor);
    }

}
