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

package com.example.mlseriesdemonstrator.helpers.vision.posedetector;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.math.MathUtils;

import com.example.mlseriesdemonstrator.helpers.vision.GraphicOverlay;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

/** Draw the detected pose in preview. */
public class PoseGraphic extends GraphicOverlay.Graphic {

  private static final float DOT_RADIUS = 10.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 8.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;

  private final Pose pose;
  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private final boolean visualizeAngles;
  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  private final List<String> poseClassification;
  private final Paint classificationTextPaint;
  private final Paint leftPaint;
  private final Paint rightPaint;
  private final Paint whitePaint;

  PoseGraphic(
      GraphicOverlay overlay,
      Pose pose,
      boolean showInFrameLikelihood,
      boolean visualizeZ,
      boolean rescaleZForVisualization,
      boolean visualizeAngles,
      List<String> poseClassification) {
    super(overlay);
    this.pose = pose;
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;
    this.visualizeAngles = visualizeAngles;
    this.poseClassification = poseClassification;
    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    //TODO: change paint color to white
    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);
    leftPaint = new Paint();
    leftPaint.setStrokeWidth(STROKE_WIDTH);
    leftPaint.setColor(Color.WHITE);
    rightPaint = new Paint();
    rightPaint.setStrokeWidth(STROKE_WIDTH);
    rightPaint.setColor(Color.WHITE);
  }

  @Override
  public void draw(Canvas canvas) {
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    if (landmarks.isEmpty()) {
      return;
    }

    // Draw pose classification text.
    float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;
    for (int i = 0; i < poseClassification.size(); i++) {
      float classificationY = (canvas.getHeight() - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f
          * (poseClassification.size() - i));
      canvas.drawText(
          poseClassification.get(i),
          classificationX,
          classificationY,
          classificationTextPaint);
    }

    //TODO: remove face landmarks
    List<PoseLandmark> landmarksToAvoid = new ArrayList<>();
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.NOSE));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_EYE));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_EYE));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_EAR));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_EAR));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH));

    // Draw all the points
    for (PoseLandmark landmark : landmarks) {

      if(landmarksToAvoid.contains(landmark))
        continue;
      drawPoint(canvas, landmark, whitePaint);
      if (visualizeZ && rescaleZForVisualization) {
        zMin = min(zMin, landmark.getPosition3D().getZ());
        zMax = max(zMax, landmark.getPosition3D().getZ());
      }
    }


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

    PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
    PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
    PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
    PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
    PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
    PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
    PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
    PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
    PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
    PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

    // Face
//    drawLine(canvas, nose, lefyEyeInner, whitePaint);
//    drawLine(canvas, lefyEyeInner, lefyEye, whitePaint);
//    drawLine(canvas, lefyEye, leftEyeOuter, whitePaint);
//    drawLine(canvas, leftEyeOuter, leftEar, whitePaint);
//    drawLine(canvas, nose, rightEyeInner, whitePaint);
//    drawLine(canvas, rightEyeInner, rightEye, whitePaint);
//    drawLine(canvas, rightEye, rightEyeOuter, whitePaint);
//    drawLine(canvas, rightEyeOuter, rightEar, whitePaint);
//    drawLine(canvas, leftMouth, rightMouth, whitePaint);

    drawLine(canvas, leftShoulder, rightShoulder, whitePaint);
    drawLine(canvas, leftHip, rightHip, whitePaint);

    // Left body
    drawLine(canvas, leftShoulder, leftElbow, leftPaint);
    drawLine(canvas, leftElbow, leftWrist, leftPaint);
    drawLine(canvas, leftShoulder, leftHip, leftPaint);
    drawLine(canvas, leftHip, leftKnee, leftPaint);
    drawLine(canvas, leftKnee, leftAnkle, leftPaint);
    drawLine(canvas, leftWrist, leftThumb, leftPaint);
    drawLine(canvas, leftWrist, leftPinky, leftPaint);
    drawLine(canvas, leftWrist, leftIndex, leftPaint);
    drawLine(canvas, leftIndex, leftPinky, leftPaint);
    drawLine(canvas, leftAnkle, leftHeel, leftPaint);
    drawLine(canvas, leftHeel, leftFootIndex, leftPaint);

    // Right body
    drawLine(canvas, rightShoulder, rightElbow, rightPaint);
    drawLine(canvas, rightElbow, rightWrist, rightPaint);
    drawLine(canvas, rightShoulder, rightHip, rightPaint);
    drawLine(canvas, rightHip, rightKnee, rightPaint);
    drawLine(canvas, rightKnee, rightAnkle, rightPaint);
    drawLine(canvas, rightWrist, rightThumb, rightPaint);
    drawLine(canvas, rightWrist, rightPinky, rightPaint);
    drawLine(canvas, rightWrist, rightIndex, rightPaint);
    drawLine(canvas, rightIndex, rightPinky, rightPaint);
    drawLine(canvas, rightAnkle, rightHeel, rightPaint);
    drawLine(canvas, rightHeel, rightFootIndex, rightPaint);

    // Left Angles
    float leftElbowAngle = calculateAngles(leftWrist, leftElbow, leftShoulder);
    float leftShoulderAngle = calculateAngles(leftElbow, leftShoulder, leftHip);
    float leftHipAngle = calculateAngles(leftShoulder, leftHip, leftKnee);
    float leftKneeAngle = calculateAngles(leftHip, leftKnee, leftHeel);
    System.out.println("Left elbow angle: " + leftElbowAngle);
    // Right Angles
    float rightElbowAngle = calculateAngles(rightWrist, rightElbow, rightShoulder);
    float rightShoulderAngle = calculateAngles(rightElbow, rightShoulder, rightHip);
    float rightHipAngle = calculateAngles(rightShoulder,rightHip, rightKnee);
    float rightKneeAngle = calculateAngles(rightHip, rightKnee, rightHeel);

    List<PoseLandmark> anglesToVisualize = new ArrayList<>();
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_HIP));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE));

    HashMap<Integer, Float> angles = new HashMap<Integer, Float>();

    angles.put(PoseLandmark.LEFT_ELBOW, leftElbowAngle);
    angles.put(PoseLandmark.LEFT_SHOULDER, leftShoulderAngle);
    angles.put(PoseLandmark.LEFT_HIP, leftHipAngle);
    angles.put(PoseLandmark.LEFT_KNEE, leftKneeAngle);
    angles.put(PoseLandmark.RIGHT_ELBOW, rightElbowAngle);
    angles.put(PoseLandmark.RIGHT_SHOULDER, rightShoulderAngle);
    angles.put(PoseLandmark.RIGHT_HIP, rightHipAngle);
    angles.put(PoseLandmark.RIGHT_KNEE, rightKneeAngle);

    // Draw angles
    if(visualizeAngles){
      for(PoseLandmark landmark : landmarks){
        if(anglesToVisualize.contains(landmark)){
          canvas.drawText(
                  String.format("%.0f", angles.get(landmark.getLandmarkType())),
                  translateX(landmark.getPosition().x),
                  translateY(landmark.getPosition().y),
                  whitePaint);
        }
      }
    }


    // Draw inFrameLikelihood for all points
    if (showInFrameLikelihood) {
      for (PoseLandmark landmark : landmarks) {
        canvas.drawText(
            String.format(Locale.US, "%.2f", landmark.getInFrameLikelihood()),
            translateX(landmark.getPosition().x),
            translateY(landmark.getPosition().y),
            whitePaint);
      }
    }




  }



  float calculateAngles(PoseLandmark a, PoseLandmark b, PoseLandmark c){
    float angle = (float) Math.abs((Math.toDegrees(Math.atan2(c.getPosition().y - b.getPosition().y, c.getPosition().x - b.getPosition().x) - Math.atan2(a.getPosition().y - b.getPosition().y, a.getPosition().x - b.getPosition().x))));
    if(angle > 180){
      angle -= 180;
    }
    return angle;
  }

  void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    maybeUpdatePaintColor(paint, canvas, point.getZ());
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;
    maybeUpdatePaintColor(paint, canvas, avgZInImagePixel);

    canvas.drawLine(
          translateX(start.getX()),
          translateY(start.getY()),
          translateX(end.getX()),
          translateY(end.getY()),
          paint);
  }

  private void maybeUpdatePaintColor(Paint paint, Canvas canvas, float zInImagePixel) {
    if (!visualizeZ) {
      return;
    }

    // When visualizeZ is true, sets up the paint to different colors based on z values.
    // Gets the range of z value.
    float zLowerBoundInScreenPixel;
    float zUpperBoundInScreenPixel;

    if (rescaleZForVisualization) {
      zLowerBoundInScreenPixel = min(-0.001f, scale(zMin));
      zUpperBoundInScreenPixel = max(0.001f, scale(zMax));
    } else {
      // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
      float defaultRangeFactor = 1f;
      zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.getWidth();
      zUpperBoundInScreenPixel = defaultRangeFactor * canvas.getWidth();
    }

    float zInScreenPixel = scale(zInImagePixel);

    if (zInScreenPixel < 0) {
      // Sets up the paint to draw the body line in red if it is in front of the z origin.
      // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
      // color. The larger the value is, the more red it will be.
      int v = (int) (zInScreenPixel / zLowerBoundInScreenPixel * 255);
      v = MathUtils.clamp( v, 0, 255);
      paint.setARGB(255, 255, 255 - v, 255 - v);
    } else {
      // Sets up the paint to draw the body line in blue if it is behind the z origin.
      // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
      // color. The larger the value is, the more blue it will be.
      int v = (int) (zInScreenPixel / zUpperBoundInScreenPixel * 255);
      v = MathUtils.clamp( v, 0, 255);
      paint.setARGB(255, 255 - v, 255 - v, 255);
    }
  }
}

