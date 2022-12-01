package com.example.motion.helpers.vision.posedetector;

import java.util.ArrayList;

public class Calculations {
    public double calculateAngles(double fpX, double fpY, double mpX, double mpY, double lpX, double lpY){
        double result = Math.toDegrees(Math.atan2(lpY - mpY, lpX - mpX) - Math.atan2(fpY-mpY,fpX-mpX));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    public double calculateAccuracy(ArrayList<Double> anglesArray){
        double sum = 0;

        // Getting Error Percentage
        // using sum as the summation
        for(int i = 0; i < anglesArray.size(); i++){
            sum += Math.abs((anglesArray.get(i) - 160)/anglesArray.get(i));
        }
        double errorPercentage = (sum/anglesArray.size()) * 100;

        // Accuracy = 100 - error percentage
        double accuracy = 100 - errorPercentage;
        return accuracy;
    }

}
