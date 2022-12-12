package com.example.motion.helpers.vision.posedetector;

import com.google.mlkit.vision.pose.PoseLandmark;

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

    public double calculateAngles(PoseLandmark fp, PoseLandmark mp, PoseLandmark lp){
        double result = Math.toDegrees(Math.atan2(lp.getPosition().y - mp.getPosition().y, lp.getPosition().x - mp.getPosition().x) - Math.atan2(fp.getPosition().y-mp.getPosition().y,fp.getPosition().x-mp.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    public double calculateAccuracy(ArrayList<Double> anglesArray, int idealAngle){
        double sum = 0;

        // Getting Error Percentage
        // using sum as the summation
        for(int i = 0; i < anglesArray.size(); i++){
            sum += Math.abs((anglesArray.get(i) - idealAngle)/anglesArray.get(i));
        }
        double errorPercentage = (sum/anglesArray.size()) * 100;

        // Accuracy = 100 - error percentage
        double accuracy = 100 - errorPercentage;
        return accuracy;
    }

    public double totalAccuracy(ArrayList<Double> anglesAccuracy){
        double sum = 0;

        for(int i = 0; i < anglesAccuracy.size(); i++){
            sum += Math.abs((anglesAccuracy.get(i) - 100)/100);
        }

        double errorPercentage = (sum/anglesAccuracy.size()) * 100;

        // Accuracy = 100 - error percentage
        double accuracy = 100 - errorPercentage;
        return accuracy;
    }

    public double calculateAngleConsistency(ArrayList<Double> anglesArray){
        // calculate the mean
        double sum = 0;
        for (double val : anglesArray) {
            sum += val;
        }
        double mean = sum / anglesArray.size();

        // calculate the sum of squares
        double sumOfSquares = 0;
        for (double val : anglesArray) {
            sumOfSquares += Math.pow(val - mean, 2);
        }

        // calculate the standard deviation
        double stdDev = Math.sqrt(sumOfSquares / anglesArray.size());

        // print the standard deviation
        System.out.println("Standard deviation: " + stdDev);
        return stdDev;

    }

    public double totalConsistency(ArrayList<Double> consistencyArray){
        // calculate the mean
        double sum = 0;
        for (double val : consistencyArray) {
            sum += val;
        }
        double mean = sum / consistencyArray.size();

        // calculate the sum of squares
        double sumOfSquares = 0;
        for (double val : consistencyArray) {
            sumOfSquares += Math.pow(val - mean, 2);
        }

        // calculate the standard deviation
        double stdDev = Math.sqrt(sumOfSquares / consistencyArray.size());

        // print the standard deviation
        System.out.println("Total Standard deviation: " + stdDev);

        // calculate the consistency as a percentage
        double consistencyPercentage = (stdDev / mean) * 100;
        return consistencyPercentage;
    }
}
