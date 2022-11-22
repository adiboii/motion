/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.motion.helpers.vision.posedetector;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.example.motion.helpers.MotionProcessor;
import com.example.motion.helpers.vision.GraphicOverlay;
import com.example.motion.helpers.vision.VisionProcessorBase;
import com.example.motion.helpers.vision.posedetector.classification.PoseClassifierProcessor;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
    extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
  // Attributes
  private static final String TAG = "PoseDetectorProcessor";
  private final PoseDetector detector;
  private final boolean runClassification;
  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;
  private PoseClassifierProcessor poseClassifierProcessor;
  private final MotionProcessor motionProcessor;

  // Constructors
  public PoseDetectorProcessor(
      Context context,
      PoseDetectorOptionsBase options,
      boolean runClassification,
      boolean isStreamMode,
      MotionProcessor motionProcessor) {
    super(context);
    detector = PoseDetection.getClient(options);
    this.runClassification = runClassification;
    this.isStreamMode = isStreamMode;
    this.context = context;
    this.motionProcessor = motionProcessor;
    classificationExecutor = Executors.newSingleThreadExecutor();
  }

  // Pre-defined Methods
  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }

  @Override
  protected Task<PoseWithClassification> detectInImage(InputImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }


  @Override
  protected Task<PoseWithClassification> detectInImage(MlImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }

  @Override
  // TODO: make a class to take in classification result and place it here
  protected void onSuccess(
      @NonNull PoseWithClassification poseWithClassification,
      @NonNull GraphicOverlay graphicOverlay) {

    /**
     * If pose is detected, call on listener to check
     * if user is doing the selected pose.
     * */
    motionProcessor.listener.verifyUserPose(motionProcessor.isDoingSelectedPose(poseWithClassification.getResult(), poseClassifierProcessor.confidenceLevel));

    graphicOverlay.add(
          new PoseGraphic(
            graphicOverlay,
            poseWithClassification.pose,
            // The two lines below are used for debugging purposes
            // They shall be removed once development is done
            poseWithClassification.classificationResult,
            motionProcessor.isDoingSelectedPose(poseWithClassification.getResult(), poseClassifierProcessor.confidenceLevel)));
  }

  @Override
  protected boolean isMlImageEnabled(Context context) {
    // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
    return true;
  }

  /** Internal class to hold Pose and classification results. */
  protected static class PoseWithClassification {
    // Attributes
    private final Pose pose;
    private final List<String> classificationResult;

    // Constructors
    public PoseWithClassification(Pose pose, List<String> classificationResult) {
      this.pose = pose;
      this.classificationResult = classificationResult;
    }

    public Pose getPose() {
      return pose;
    }

    public List<String> getClassificationResult() {
      return classificationResult;
    }

    public String getResult(){
      if(classificationResult.isEmpty())
        return "null";
      else
        return classificationResult.get(classificationResult.size()-1);
    }
  }
}
