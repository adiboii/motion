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

package com.example.motion.helpers.vision.posedetector.classification;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.WorkerThread;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


// Accepts a stream of Poses for classification
public class PoseClassifierProcessor {
  private static final String TAG = "PoseClassifierProcessor";
  private static final String YOGA_DATASET = "pose/yoga_poses.csv";
  private static final String TRAINING_DATASET = "pose/training_dataset.csv";
  private static final String TESTING_DATASET = "pose/testing_dataset.csv";

  // Specify classes for which we want rep counting.
  // These are the labels in the given {@code POSE_SAMPLES_FILE}. You can set your own class labels
  // for your pose samples.
  private static final String GODDESS_CLASS = "goddess";
  private static final String  TREE_RIGHT_CLASS = "tree";
  private static final String TREE_LEFT_CLASS = "tree-left";
  private static final String WARRIOR2_LEFT_CLASS = "warrior2-left";
  private static final String WARRIOR2_RIGHT_CLASS = "warrior2-right";
  private static final String NULL = "null";

  private static final String[] POSE_CLASSES = {
        GODDESS_CLASS, TREE_LEFT_CLASS, TREE_RIGHT_CLASS, WARRIOR2_LEFT_CLASS, WARRIOR2_RIGHT_CLASS, NULL
  };

  private final boolean isStreamMode;

  private EMASmoothing emaSmoothing;
  private List<RepetitionCounter> repCounters;
  private PoseClassifier poseClassifier;
  private String lastRepResult;
  public double confidenceLevel;
  private  List<PoseSample> testPoseSamples = new ArrayList<>();
  @WorkerThread
  public PoseClassifierProcessor(Context context, boolean isStreamMode) {
    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    this.isStreamMode = isStreamMode;
    if (isStreamMode) {
      emaSmoothing = new EMASmoothing();
      repCounters = new ArrayList<>();
      lastRepResult = "";
    }
    loadPoseSamples(context);
    loadTesting(context);
    displayTestingResult(testPoseSamples);
    displayConfusionMatrix(testPoseSamples);
  }

  private void loadTesting(Context context) {

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(TESTING_DATASET)));
      String csvLine = reader.readLine();
      while(csvLine != null) {
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if(poseSample != null) {
          testPoseSamples.add(poseSample);
        }
        csvLine = reader.readLine();
      }
    } catch (Exception e) {
      System.out.println("Error on Displaying Model's Accuracy: " + e.toString());
    }
  }

  private void displayConfusionMatrix(List<PoseSample> testPoseSamples){
    PoseClassifier testPoseClassifier = new PoseClassifier(testPoseSamples);
    int[][] matrix = new int[6][6];
    int row = 0;
    int column = 0;

    for(PoseSample sample : testPoseSamples){
      try{
        List<PointF3D> points = sample.getLandmarks();
        ClassificationResult classification = testPoseClassifier.classify(points);

        if(sample.getClassName().equalsIgnoreCase("goddess")){
          row = 0;
        } else if (sample.getClassName().equalsIgnoreCase("warrior2-right")){
          row = 1;
        } else if(sample.getClassName().equalsIgnoreCase("warrior2-left")){
          row = 2;
        } else if (sample.getClassName().equalsIgnoreCase("tree-right")){
          row = 3;
        } else if (sample.getClassName().equalsIgnoreCase("tree-left")){
          row = 4;
        } else if (sample.getClassName().equalsIgnoreCase("null")){
          row = 5;
        };

        if(classification.getMaxConfidenceClass().equalsIgnoreCase("goddess")){
          column = 0;
        } else if (classification.getMaxConfidenceClass().equalsIgnoreCase("warrior2-right")){
          column = 1;
        } else if(classification.getMaxConfidenceClass().equalsIgnoreCase("warrior2-left")){
          column = 2;
        } else if (classification.getMaxConfidenceClass().equalsIgnoreCase("tree-right")){
          column = 3;
        } else if (classification.getMaxConfidenceClass().equalsIgnoreCase("tree-left")){
          column = 4;
        } else if (classification.getMaxConfidenceClass().equalsIgnoreCase("null")){
          column = 5;
        };

        matrix[row][column]++;
      } catch (Exception e){
        System.out.println("Error on Display Testing Result: " + e);
      }
    }

    int total = 0;
    for(int i = 0; i<matrix.length; i++){
      for(int j=0; j<matrix[i].length;j++){
        System.out.print(matrix[i][j] + " ");
        if(i==j){
          total+=matrix[i][j];
        }
      }
      System.out.println("---");
    }

    double accuracy =  ((double)total / (double) testPoseSamples.size());

    System.out.println("Confusion Matrix Accuracy: " + accuracy);

  }

  private void displayTestingResult(List<PoseSample> testPoseSamples) {
    PoseClassifier testPoseClassifier = new PoseClassifier(testPoseSamples);
    double modelAccuracy = 0;
    double goddessAccuracy = 0;
    double warriorLeftAccuracy = 0;
    double warriorRightAccuracy = 0;
    double treeLeftAccuracy = 0;
    double treeRightAccuracy = 0;
    double nullAccuracy = 0;

    for (PoseSample sample : testPoseSamples) {
      try {
        List<PointF3D> points = sample.getLandmarks();
        ClassificationResult classification = testPoseClassifier.classify(points);
        System.out.println("AAA Class: " + sample.getClassName());
        System.out.println(String.format("AAA Predicted Class: %s - Confidence: %.2f", classification.getMaxConfidenceClass(), classification.getClassConfidence(sample.getClassName())));
        if(sample.getClassName().equals(classification.getMaxConfidenceClass())){
          modelAccuracy++;
          if( sample.getClassName().equalsIgnoreCase("goddess")){
            goddessAccuracy++;
          }
          if( sample.getClassName().equalsIgnoreCase("warrior2-right")){
            warriorRightAccuracy++;
          }
          if( sample.getClassName().equalsIgnoreCase("warrior2-left")){
            warriorLeftAccuracy++;
          }
          if( sample.getClassName().equalsIgnoreCase("tree-right")){
            treeRightAccuracy++;
          }
          if( sample.getClassName().equalsIgnoreCase("tree-left")){
            treeLeftAccuracy++;
          }
          if( sample.getClassName().equalsIgnoreCase("null")){
            nullAccuracy++;
          }
        }

      } catch (Exception e) {
        System.out.println("Error on Display Testing Result: " + e);
      }
    }
    System.out.println("Model Accuracy: " + modelAccuracy / testPoseSamples.size());
    System.out.println("Goddess Accuracy: " + goddessAccuracy / 50);
    System.out.println("Tree-Right Accuracy: " + treeRightAccuracy / 50);
    System.out.println("Tree-Left Accuracy: " + treeLeftAccuracy / 50);
    System.out.println("Warrior-Right Accuracy: " + warriorRightAccuracy / 50);
    System.out.println("Warrior-Left Accuracy: " + warriorLeftAccuracy / 50);
    System.out.println("Null Accuracy: " + nullAccuracy/50 );
  }



  private void loadPoseSamples(Context context) {
    List<PoseSample> poseSamples = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(context.getAssets().open(TRAINING_DATASET)));
      String csvLine = reader.readLine();
      while (csvLine != null) {
        // If line is not a valid {@link PoseSample}, we'll get null and skip adding to the list.
        PoseSample poseSample = PoseSample.getPoseSample(csvLine, ",");
        if (poseSample != null) {
          poseSamples.add(poseSample);
        }
        csvLine = reader.readLine();
      }
    } catch (IOException e) {
      Log.e(TAG, "Error when loading pose samples.\n" + e);
    }
    poseClassifier = new PoseClassifier(poseSamples);
  }

  private static List<PointF3D> extractPoseLandmarks(Pose pose) {
    List<PointF3D> landmarks = new ArrayList<>();
    for (PoseLandmark poseLandmark : pose.getAllPoseLandmarks()) {
      landmarks.add(poseLandmark.getPosition3D());
    }
    return landmarks;
  }


  /**
   * Given a new {@link Pose} input, returns a list of formatted {@link String}s with Pose
   * classification results.
   *
   * <p>Currently it returns up to 2 strings as following:
   * 0: PoseClass : X reps
   * 1: PoseClass : [0.0-1.0] confidence
   */
  @WorkerThread
  public List<String> getPoseResult(Pose pose) {
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    boolean allPartsDetected = true;
    Preconditions.checkState(Looper.myLooper() != Looper.getMainLooper());
    List<String> result = new ArrayList<>();
    ClassificationResult classification = poseClassifier.classify(pose);

    // Update {@link RepetitionCounter}s if {@code isStreamMode}.
    if (isStreamMode) {
      // Feed pose to smoothing even if no pose found.
      classification = emaSmoothing.getSmoothedResult(classification);

      // Return early without updating repCounter if no pose found.
      if (pose.getAllPoseLandmarks().isEmpty()) {
        //result.add(lastRepResult);
        return result;
      }
    }



    //TODO: fix not all body parts can be seen bug
    // Add maxConfidence class of current frame to result if pose is found.
    if (!pose.getAllPoseLandmarks().isEmpty()) {
      String maxConfidenceClass = " ";

      maxConfidenceClass = classification.getMaxConfidenceClass();

//        String maxConfidenceClassResult = String.format(
//            Locale.US,
//            "%s : %.2f",
//            maxConfidenceClass,
//            classification.getClassConfidence(maxConfidenceClass)
//                / poseClassifier.confidenceRange());

      confidenceLevel = classification.getClassConfidence(maxConfidenceClass)
              / poseClassifier.confidenceRange();


      String maxConfidenceClassResult = String.format(
              Locale.US,
              "%s",
              maxConfidenceClass);

      result.add(maxConfidenceClassResult);
    }

    return result;
  }

}
