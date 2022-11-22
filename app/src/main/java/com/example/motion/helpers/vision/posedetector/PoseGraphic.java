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

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.motion.helpers.vision.GraphicOverlay;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.HashMap;
import java.util.List;

/** Draw the detected pose in preview. */
public class PoseGraphic extends GraphicOverlay.Graphic {
  // Attributes
  private static final float DOT_RADIUS = 20.0f;
  private static final float STROKE_WIDTH = 8.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;
  private final Pose pose;
  private final Paint whitePaint = new Paint();
  private final Paint anglePaint = new Paint();
  private final PoseSkeleton poseSkeleton;
  private final HashMap<Integer, PoseLandmark> essentialLandmarks;
  private final List<String> poseClassification;
  private final Paint classificationTextPaint;
  private final boolean isDoingSelectedPose;
  // Constructors
  PoseGraphic(
      GraphicOverlay overlay,
      Pose pose,
      List<String> poseClassification,
      boolean isDoingSelectedPose) {
    super(overlay);
    this.pose = pose;
    this.poseSkeleton = new PoseSkeleton(pose);
    this.essentialLandmarks = poseSkeleton.getEssentialLandmarks();
    this.poseClassification = poseClassification;
    this.isDoingSelectedPose = isDoingSelectedPose;
    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    anglePaint.setColor(Color.WHITE);
    anglePaint.setTextSize(25);
  }

  @Override
  public void draw(Canvas canvas) {
    if (poseSkeleton.getLandmarks().isEmpty())
      return;

    float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;

//    for (int i = 0; i < poseClassification.size(); i++) {
//      float classificationY = (canvas.getHeight() - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f
//              * (poseClassification.size() - i));
//      canvas.drawText(
//              poseClassification.get(i),
//              classificationX,
//              classificationY,
//              classificationTextPaint);
//    }
//      canvas.drawText(
//              String.format("Is doing selected pose: %s", isDoingSelectedPose),
//              classificationX,
//              classificationY,
//              classificationTextPaint);

    drawLandmarks(canvas);
    drawLandmarkConnections(canvas);
    drawAngles(canvas);
  }

  // This function draws all essential landmarks
  // and skips unnecessary ones
  public void drawLandmarks(Canvas canvas) {
    for (PoseLandmark landmark : poseSkeleton.getLandmarks())
      if(essentialLandmarks.containsValue(landmark))
        drawPoint(canvas, landmark);
  }

  public void drawLandmarkConnections(Canvas canvas) {
    // Draws Left Body Connections
    int lastLeftBodyIndex = essentialLandmarks.size() / 2 - 1;
    for(int landmarkIndex = 0; landmarkIndex < lastLeftBodyIndex; landmarkIndex++) {
      // Draws a connection between the current landmark
      // and the landmark adjacent to it (landmarkIndex + 1)
      drawLine(canvas, essentialLandmarks.get(landmarkIndex), essentialLandmarks.get(landmarkIndex + 1));
    }
    // Draws Right Body Connections
    for(int landmarkIndex = lastLeftBodyIndex + 1; landmarkIndex < essentialLandmarks.size() - 1; landmarkIndex++) {
      drawLine(canvas, essentialLandmarks.get(landmarkIndex), essentialLandmarks.get(landmarkIndex + 1));
    }
    // Draws Hip and Shoulder Connections
    drawLine(canvas, essentialLandmarks.get(2), essentialLandmarks.get(8));
    drawLine(canvas, essentialLandmarks.get(3), essentialLandmarks.get(9));
  }

  // Predefined Functions
  void drawPoint(Canvas canvas, PoseLandmark landmark) {
    PointF3D point = landmark.getPosition3D();
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, whitePaint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark) {
    PointF3D startPosition = startLandmark.getPosition3D();
    PointF3D endPosition = endLandmark.getPosition3D();
    canvas.drawLine(
            translateX(startPosition.getX()),
            translateY(startPosition.getY()),
            translateX(endPosition.getX()),
            translateY(endPosition.getY()),
            whitePaint);
  }

  double calculateAngles(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
    double fpX = firstPoint.getPosition().x;
    double fpY = firstPoint.getPosition().y;
    double mpX = midPoint.getPosition().x;
    double mpY = midPoint.getPosition().y;
    double lpX = lastPoint.getPosition().x;
    double lpY = lastPoint.getPosition().y;
    Calculations solve = new Calculations();

    double result = solve.calculateAngles(fpX, fpY, mpX, mpY, lpX, lpY);
    return result;
  }

  void drawAngles(Canvas canvas){
    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
    PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
    PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

    // Left Angles
    double leftElbowAngle = calculateAngles(leftWrist, leftElbow, leftShoulder);
    double leftShoulderAngle = calculateAngles(leftElbow, leftShoulder, leftHip);
    double leftHipAngle = calculateAngles(leftShoulder, leftHip, leftKnee);
    double leftKneeAngle = calculateAngles(leftHip, leftKnee, leftAnkle);

    // Right Angles
    double rightElbowAngle = calculateAngles(rightWrist, rightElbow, rightShoulder);
    double rightShoulderAngle = calculateAngles(rightElbow, rightShoulder, rightHip);
    double rightHipAngle = calculateAngles(rightShoulder,rightHip, rightKnee);
    double rightKneeAngle = calculateAngles(rightHip, rightKnee, rightAnkle);

    HashMap<Integer, Double> angles = new HashMap<Integer, Double>();
    angles.put(PoseLandmark.LEFT_ELBOW, leftElbowAngle);
    angles.put(PoseLandmark.LEFT_SHOULDER, leftShoulderAngle);
    angles.put(PoseLandmark.LEFT_HIP, leftHipAngle);
    angles.put(PoseLandmark.LEFT_KNEE, leftKneeAngle);
    angles.put(PoseLandmark.RIGHT_ELBOW, rightElbowAngle);
    angles.put(PoseLandmark.RIGHT_SHOULDER, rightShoulderAngle);
    angles.put(PoseLandmark.RIGHT_HIP, rightHipAngle);
    angles.put(PoseLandmark.RIGHT_KNEE, rightKneeAngle);

    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    for(PoseLandmark landmark : landmarks){
        canvas.drawText(
                String.format("%.0f", angles.get(landmark.getLandmarkType())),
                translateX(landmark.getPosition().x),
                translateY(landmark.getPosition().y - 30),

                anglePaint);
    }
  }
}