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

package com.example.motion.helpers.vision;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import com.google.android.gms.common.images.Size;
import java.io.IOException;

/** Preview the camera image in the screen. */
public class CameraSourcePreview extends ViewGroup {
  // Attributes with Predefined Classes
  private static final String TAG = "MIDemoApp:Preview";

  private final Context context;
  private final SurfaceView surfaceView;
  private boolean startRequested;
  private boolean surfaceAvailable;
  private CameraSource cameraSource;

  private GraphicOverlay overlay;

  public CameraSourcePreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    startRequested = false;
    surfaceAvailable = false;

    surfaceView = new SurfaceView(context);
    surfaceView.getHolder().addCallback(new SurfaceCallback());

    addView(surfaceView);
  }

  private void start(CameraSource cameraSource) throws IOException {
    this.cameraSource = cameraSource;

    if (this.cameraSource != null) {
      startRequested = true;
      startIfReady();
    }
  }

  public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
    this.overlay = overlay;
    start(cameraSource);
  }

  public void stop() {
    if (cameraSource != null) {
      cameraSource.stop();
    }
  }

  public void release() {
    if (cameraSource != null) {
      cameraSource.release();
      cameraSource = null;
    }
    surfaceView.getHolder().getSurface().release();
  }

  private void startIfReady() throws IOException, SecurityException {
    if (startRequested && surfaceAvailable) {
      cameraSource.start();

      requestLayout();

      if (overlay != null) {
        Size size = cameraSource.getPreviewSize();
        System.out.println("Size:" + size);

        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        if (isPortraitMode()) {
          // Swap width and height sizes when in portrait, since it will be rotated by 90 degrees.
          // The camera preview and the image being processed have the same size.
          overlay.setImageSourceInfo(max, min, true);
        } else {
          overlay.setImageSourceInfo(min, max, true);
        }
        overlay.clear();
      }
      startRequested = false;
    }
  }

  private class SurfaceCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder surface) {
      surfaceAvailable = true;
      try {
        startIfReady();
      } catch (IOException e) {
        Log.e(TAG, "Could not start camera source.", e);
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surface) {
      surfaceAvailable = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
  }
//
//  @Override
//  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//    int width = 1280;
//    int height = 960;
//    if (cameraSource != null) {
//      Size size = cameraSource.getPreviewSize();
//      if (size != null) {
//        width = size.getWidth();
//        height = size.getHeight();
//      }
//    }
//    System.out.println("Height: " + height + "\nWidth: " + width);
//    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
//    if (isPortraitMode()) {
//      int tmp = width;
//      width = height;
//      height = tmp;
//    }
//
//    float previewAspectRatio = (float) width / height;
//    int layoutWidth = right - left;
//    int layoutHeight = bottom - top;
//    float layoutAspectRatio = (float) layoutWidth / layoutHeight;
//    if (previewAspectRatio > layoutAspectRatio) {
//      // The preview input is wider than the layout area. Fit the layout height and crop
//      // the preview input horizontally while keep the center.
//      int horizontalOffset = (int) (previewAspectRatio * layoutHeight - layoutWidth) / 2;
//      surfaceView.layout(-horizontalOffset, 0, layoutWidth + horizontalOffset, layoutHeight);
//    } else {
//      // The preview input is taller than the layout area. Fit the layout width and crop the preview
//      // input vertically while keep the center.
//      int verticalOffset = (int) (layoutWidth / previewAspectRatio - layoutHeight) / 2;
//      surfaceView.layout(0, -verticalOffset, layoutWidth, layoutHeight + verticalOffset);
//    }
//  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int previewWidth = 320;
    int previewHeight = 240;
    if (cameraSource != null) {
      Size size = cameraSource.getPreviewSize();
      if (size != null) {
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();
      }
    }

    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
    if (isPortraitMode()) {
      int tmp = previewHeight;
      previewHeight = previewWidth;
      previewWidth = tmp;
    }

    final int viewWidth = right - left;
    final int viewHeight = bottom - top;

    int childWidth;
    int childHeight;
    int childXOffset = 0;
    int childYOffset = 0;
    float widthRatio = (float) viewWidth / (float) previewWidth;
    float heightRatio = (float) viewHeight / (float) previewHeight;

    // To fill the view with the camera preview, while also preserving the correct aspect ratio,
    // it is usually necessary to slightly oversize the child and to crop off portions along one
    // of the dimensions.  We scale up based on the dimension requiring the most correction, and
    // compute a crop offset for the other dimension.
    if (widthRatio > heightRatio) {
      childWidth = viewWidth;
      childHeight = (int) ((float) previewHeight * widthRatio);
      childYOffset = (childHeight - viewHeight) / 2;
    } else {
      childWidth = (int) ((float) previewWidth * heightRatio);
      childHeight = viewHeight;
      childXOffset = (childWidth - viewWidth) / 2;
    }

    for (int i = 0; i < getChildCount(); ++i) {
      // One dimension will be cropped.  We shift child over or up by this offset and adjust
      // the size to maintain the proper aspect ratio.
      getChildAt(i).layout(
              -1 * childXOffset, -1 * childYOffset,
              childWidth - childXOffset, childHeight - childYOffset);
    }

    try {
      startIfReady();
    } catch (IOException e) {
      Log.e(TAG, "Could not start camera source.", e);
    }
  }

  private boolean isPortraitMode() {
    int orientation = context.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return false;
    }
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      return true;
    }

    Log.d(TAG, "isPortraitMode returning false by default");
    return false;
  }
}
