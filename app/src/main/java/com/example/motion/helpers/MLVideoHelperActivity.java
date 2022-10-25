package com.example.motion.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motion.R;
import com.example.motion.helpers.vision.CameraSource;
import com.example.motion.helpers.vision.CameraSourcePreview;
import com.example.motion.helpers.vision.GraphicOverlay;

import java.io.IOException;
import java.util.List;

public abstract class MLVideoHelperActivity extends AppCompatActivity {
    // Attributes with predefined classes
    private GraphicOverlay graphicOverlay;
    protected CameraSource cameraSource;

    // Attributes with user-defined classes
    private static final int REQUEST_CAMERA = 1001;
    private static final String TAG = "MLVideoHelperActivity";
    private CameraSourcePreview preview;
    private static boolean selectedPose = false;
    private CountDownTimer countDownTimer;

    // Views
    private ImageView icon;
    private TextView prompt;
    private TextView timer;
    private ImageButton warriorButton;
    private ImageButton goddessButton;
    private ImageButton treeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_helper);

        preview = findViewById(R.id.preview_view);
        graphicOverlay = findViewById(R.id.graphic_overlay);

        // Connect Views (Activity and XML)
        icon = findViewById(R.id.iconID);
        prompt = findViewById(R.id.prompt);
        timer = findViewById(R.id.textTimer);
        warriorButton = findViewById(R.id.btn_warrior2);
        goddessButton = findViewById(R.id.btn_goddess);
        treeButton = findViewById(R.id.btn_tree);

            // Requests permission to access the device's camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            initSource();
            startCameraSource();
            clickPoseBtn();
        }
    }

    // Helper Functions
    public void selectPose(MLVideoHelperActivity context, ImageButton poseSelected, ImageButton secondPose, ImageButton thirdPose) {
        if(selectedPose) {
            timer.setVisibility(View.INVISIBLE);
            icon.setImageResource(R.drawable.warning_icon);
            countDownTimer.cancel();
            prompt.setText("Select a pose");
            poseSelected.setBackground(ContextCompat.getDrawable(context, R.drawable.button_normal));
            secondPose.setEnabled(true);
            thirdPose.setEnabled(true);
            selectedPose = false;
        } else {
            timer.setVisibility(View.VISIBLE);
            startCountdown();
            if(poseSelected == warriorButton) {
                prompt.setText("Perform the Warrior II Pose");
            } else if(poseSelected == goddessButton) {
                prompt.setText("Perform the Goddess Pose");
            } else if(poseSelected == treeButton) {
                prompt.setText("Perform the Tree Pose");
            }
            poseSelected.setBackground(ContextCompat.getDrawable(context, R.drawable.button_pressed));
            secondPose.setEnabled(false);
            thirdPose.setEnabled(false);
            selectedPose = true;
        }
    }

    public void startCountdown() {
        icon.setImageResource(R.drawable.record_icon);
        countDownTimer = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
                // logic to set the EditText could go here
            }
            public void onFinish() {
                timer.setText("0");
            }
        }.start();
    }

    //Actions to do for the buttons
    private void clickPoseBtn() {
        MLVideoHelperActivity context = MLVideoHelperActivity.this;
        warriorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPose(context, warriorButton, goddessButton, treeButton);
            }
        });

        goddessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPose(context, goddessButton, warriorButton, treeButton);
            }
        });

        treeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPose(context, treeButton, warriorButton, goddessButton);
            }
        });
    }

    // Pre-defined Methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSource();
            startCameraSource();
        }
    }

    private void initSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        setProcessor();
    }

    protected abstract void setProcessor();

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}
