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

import androidx.core.math.MathUtils;

import com.example.motion.helpers.vision.GraphicOverlay;
import com.google.mlkit.vision.common.PointF3D;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.HashMap;
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
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_PINKY));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_HEEL));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_INDEX));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_THUMB));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX));
    landmarksToAvoid.add(pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX));


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

    drawLine(canvas, leftShoulder, rightShoulder, whitePaint);
    drawLine(canvas, leftHip, rightHip, whitePaint);

    // Left body
    drawLine(canvas, leftShoulder, leftElbow, leftPaint);
    drawLine(canvas, leftElbow, leftWrist, leftPaint);
    drawLine(canvas, leftShoulder, leftHip, leftPaint);
    drawLine(canvas, leftHip, leftKnee, leftPaint);
    drawLine(canvas, leftKnee, leftAnkle, leftPaint);

    // Right body
    drawLine(canvas, rightShoulder, rightElbow, rightPaint);
    drawLine(canvas, rightElbow, rightWrist, rightPaint);
    drawLine(canvas, rightShoulder, rightHip, rightPaint);
    drawLine(canvas, rightHip, rightKnee, rightPaint);
    drawLine(canvas, rightKnee, rightAnkle, rightPaint);

    // Left Angles
    double leftElbowAngle = calculateAngles(leftWrist, leftElbow, leftShoulder);
    double leftShoulderAngle = calculateAngles(leftElbow, leftShoulder, leftHip);
    double leftHipAngle = calculateAngles(leftShoulder, leftHip, leftKnee);
    double leftKneeAngle = calculateAngles(leftHip, leftKnee, leftAnkle);
    System.out.println("Left elbow angle: " + leftElbowAngle);
    // Right Angles
    double rightElbowAngle = calculateAngles(rightWrist, rightElbow, rightShoulder);
    double rightShoulderAngle = calculateAngles(rightElbow, rightShoulder, rightHip);
    double rightHipAngle = calculateAngles(rightShoulder,rightHip, rightKnee);
    double rightKneeAngle = calculateAngles(rightHip, rightKnee, rightAnkle);

    List<PoseLandmark> anglesToVisualize = new ArrayList<>();
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_HIP));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP));
    anglesToVisualize.add(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE));

    HashMap<Integer, Double> angles = new HashMap<Integer, Double>();

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



  double calculateAngles(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint){
    double result =
            Math.toDegrees(
                    Math.atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                            lastPoint.getPosition().x - midPoint.getPosition().x)
                            - Math.atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                            firstPoint.getPosition().x - midPoint.getPosition().x));
    result = Math.abs(result); // Angle should never be negative
    if (result > 180) {
      result = (360.0 - result); // Always get the acute representation of the angle
    }
    return result;
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

