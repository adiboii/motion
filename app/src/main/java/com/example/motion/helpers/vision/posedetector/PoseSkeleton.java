package com.example.motion.helpers.vision.posedetector;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.HashMap;
import java.util.List;

public class PoseSkeleton {
    // Attributes
    private List<PoseLandmark> landmarks;
    private HashMap<Integer, PoseLandmark> essentialLandmarks = new HashMap<Integer, PoseLandmark>();
    private boolean isVisible;

    // Constructors
    public PoseSkeleton() {}

    public void updatePose(Pose pose){
        landmarks = pose.getAllPoseLandmarks();
        setEssentialLandmarks(pose);
    }
    // Methods
    public List<PoseLandmark> getLandmarks() {
        return landmarks;
    }

    public HashMap<Integer, PoseLandmark> getEssentialLandmarks() {
        return essentialLandmarks;
    }

    public void setEssentialLandmarks(Pose pose) {
        // Left Body
        essentialLandmarks.put(0, pose.getPoseLandmark(PoseLandmark.LEFT_WRIST));
        essentialLandmarks.put(1, pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW));
        essentialLandmarks.put(2, pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER));
        essentialLandmarks.put(3, pose.getPoseLandmark(PoseLandmark.LEFT_HIP));
        essentialLandmarks.put(4, pose.getPoseLandmark(PoseLandmark.LEFT_KNEE));
        essentialLandmarks.put(5, pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE));
        // Right Body
        essentialLandmarks.put(6, pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST));
        essentialLandmarks.put(7, pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW));
        essentialLandmarks.put(8, pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER));
        essentialLandmarks.put(9, pose.getPoseLandmark(PoseLandmark.RIGHT_HIP));
        essentialLandmarks.put(10, pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE));
        essentialLandmarks.put(11, pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE));
    }

    public void allLandmarksVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    public boolean getIsVisible(){
        return isVisible;
    }
}
