package com.example.motion.helpers;


public interface MotionListener {
    public void verifyUserPose(boolean isDoingSelectedPose);

    public boolean ensureAllLandmarksCanBeSeen(boolean isVisible);
}


