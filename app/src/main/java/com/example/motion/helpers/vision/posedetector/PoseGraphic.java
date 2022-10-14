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
  private static final float DOT_RADIUS = 10.0f;
  private static final float STROKE_WIDTH = 8.0f;
  private final Pose pose;
  private final Paint whitePaint = new Paint();
  private final PoseSkeleton poseSkeleton;
  private final HashMap<Integer, PoseLandmark> essentialLandmarks;

  // Constructors
  PoseGraphic(
      GraphicOverlay overlay,
      Pose pose) {
    super(overlay);
    this.pose = pose;
    this.poseSkeleton = new PoseSkeleton(pose);
    this.essentialLandmarks = poseSkeleton.getEssentialLandmarks();

    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
  }

  @Override
  public void draw(Canvas canvas) {
    if (poseSkeleton.getLandmarks().isEmpty())
      return;
    drawLandmarks(canvas);
    drawLandmarkConnections(canvas);
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
}