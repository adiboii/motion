package com.example.motion.helpers.vision.posedetector;

import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;

public class Calculations {

    // calculating landmark angles with x and y provided
    public double calculateAngles(double fpX, double fpY, double mpX, double mpY, double lpX, double lpY){
        double result = Math.toDegrees(Math.atan2(lpY - mpY, lpX - mpX) - Math.atan2(fpY-mpY,fpX-mpX));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    // calculating landmark angles with pose provided
    public double calculateAngles(PoseLandmark fp, PoseLandmark mp, PoseLandmark lp){

        double result = Math.toDegrees(Math.atan2(lp.getPosition().y - mp.getPosition().y, lp.getPosition().x - mp.getPosition().x) -
                                            Math.atan2(fp.getPosition().y-mp.getPosition().y, fp.getPosition().x-mp.getPosition().x));

        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    public double calculateMean(ArrayList<Double> arr) {
        double sum = 0;
        for (double val : arr) {
            sum += val;
        }
        double mean = sum / arr.size();
        return mean;
    }

    // calculating individual landmark angle accuracy
    // using deviation with moving filter average
    public double calculateAccuracy(ArrayList<Double> anglesArray, int idealAngle) {
        double sum = 0;
        int windowSize = 5;
        double windowSum = 0;
        int windowCount = 0;

        // moving average filter
        for (double val : anglesArray) {
            // add new value to the window sum
            windowSum += val;
            // increment window count
            windowCount++;
            // remove oldest value if window is full
            if (windowCount > windowSize) {
                windowSum -= anglesArray.get(windowCount - windowSize - 1);
            }
            // calculate moving average
            double average = windowSum / Math.min(windowCount, windowSize);
            //summation
            sum += Math.abs((average - idealAngle)/idealAngle);
        }
        // deviation = (summation/N) * 100
        double deviationPercentage = (sum/anglesArray.size()) * 100;
        // Accuracy = 100 - deviation percentage
        double accuracy = 100 - deviationPercentage;

        return accuracy;
    }

    // calculating the pose accuracy by getting mean
    public double totalAccuracy(ArrayList<Double> landmarkAccuracies) {
       return calculateMean(landmarkAccuracies);
    }

    //calculating individual angle consistency
    //using standard deviation with moving average filter
    public double calculateConsistency(ArrayList<Double> anglesArray){
        int windowSize = 10;
        double windowSum = 0;
        int windowCount = 0;
        ArrayList<Double> filteredValues = new ArrayList<>();
        for (double val : anglesArray) {
            // add new value to the window sum
            windowSum += val;
            // increment window count
            windowCount++;
            // remove oldest value if window is full
            if (windowCount > windowSize) {
                windowSum -= anglesArray.get(windowCount - windowSize - 1);
            }
            // calculate moving average
            double average = windowSum / Math.min(windowCount, windowSize);
            filteredValues.add(average);
        }

        // calculate the mean with filtered values
        double sum = 0;
        for (double val : filteredValues) {
            sum += val;
        }
        double mean = sum / filteredValues.size();

        // calculate the sum of squares with filtered values
        double sumOfSquares = 0;
        for (double val : filteredValues) {
            sumOfSquares += Math.pow(val - mean, 2);
        }

        // calculate the standard deviation with filtered values
        double stdDev = Math.sqrt(sumOfSquares / filteredValues.size());

        return stdDev;
    }

    // calculating total consistency
    // using standard deviation
    public double totalConsistency(ArrayList<Double> landmarkConsistencies){
        // calculate the mean with original values
        double mean = calculateMean(landmarkConsistencies);

        // calculate the sum of squares with original values
        double sumOfSquares = 0;
        for (double val : landmarkConsistencies) {
            sumOfSquares += Math.pow(val - mean, 2);
        }

        // calculate the standard deviation with original values
        double stdDev = Math.sqrt(sumOfSquares / landmarkConsistencies.size());

        // calculate the consistency as a percentage
        double consistencyPercentage = 100 - ((stdDev / mean) * 100);

        return consistencyPercentage;
    }


}
