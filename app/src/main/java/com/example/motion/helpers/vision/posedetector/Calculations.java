package com.example.motion.helpers.vision.posedetector;

public class Calculations {
    public double calculateAngles(double fpX, double fpY, double mpX, double mpY, double lpX, double lpY){
        double result = Math.toDegrees(Math.atan2(lpY - mpY, lpX - mpX) - Math.atan2(fpY-mpY,fpX-mpX));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }
}
