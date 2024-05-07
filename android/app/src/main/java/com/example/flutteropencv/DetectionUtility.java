
package com.example.flutteropencv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class DetectionUtility {
    private List<Point> lastDetectedCircles;


    public Mat detectOMRMarks(Mat processedMat,Context context) {

        // Convert the image to grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(processedMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // Applying Gaussian blur
        Mat blurredMat = new Mat();
        Imgproc.GaussianBlur(grayMat, blurredMat, new Size(5, 5), 1);

        // Hough Circle Detection
        Mat circles = new Mat();
        Imgproc.HoughCircles(blurredMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 40, 100, 30, 10, 30);

        HashSet<Point> uniqueCircleCenters = new HashSet<>();

        for (int i = 0; i < circles.cols(); i++) {
            double[] circleParams = circles.get(0, i);
            uniqueCircleCenters.add(new Point(circleParams[0], circleParams[1]));  // Duplicates will not be added
        }

        // Convert HashSet back to a List for sorting
        List<Point> circleCenters = new ArrayList<>(uniqueCircleCenters);

        // Sort the circle centers
        Collections.sort(circleCenters, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                if (Math.abs(p1.y - p2.y) > 10) {
                    return Double.compare(p1.y, p2.y);
                } else {
                    return Double.compare(p1.x, p2.x);
                }
            }
        });
        System.out.println("Size of circleCenters: " + circleCenters.size());


        int[][] rollNumberIndexes   = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {15, 16, 17, 18, 19},
                {24, 25, 26, 27, 28},
                {33, 34, 35, 36, 37},
                {42, 43, 44, 45, 46},
                {51, 52, 53, 54, 55},
                {59, 56, 57, 58, 60},
                {69, 68, 66, 65, 67},
                {78, 77, 76, 75, 74},

                // ... Other rows for the roll number table
        };

        int[][] questionIndexes = {
                {11, 12, 13, 14},
                {20, 21, 22, 23},
                {29, 30, 31, 32},
                {38, 39, 40, 41},
                {47, 48, 49, 50},
                {61, 62, 63, 64},
                {70, 71, 72, 73},
                {79, 80, 81, 82},
                {83, 84, 85, 86},
                {87, 88, 89, 90},
                // ... Other rows for the question table
        };

        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.6;
        Scalar color = new Scalar(255, 0, 0);
        int thickness = 2;

        int[] rollNumberLabels = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        char[] optionLabels = {'A', 'B', 'C', 'D'};

        // Labeling roll numbers
        for (int i = 0; i < rollNumberIndexes.length; i++) {
            for (int j = 0; j < rollNumberIndexes[i].length; j++) {
                int index = rollNumberIndexes[i][j] - 1;

                // Bounds check
                if (index < circleCenters.size()) {
                    Point center = circleCenters.get(index);
                    Imgproc.putText(processedMat, Integer.toString(rollNumberLabels[i]), center, fontFace, fontScale, color, thickness);
                } else {
                    System.out.println("Index out of bounds: " + index);
                }
            }
        }

        // Labeling questions
        for (int i = 0; i < questionIndexes.length; i++) {
            for (int j = 0; j < questionIndexes[i].length; j++) {
                int index = questionIndexes[i][j] - 1;

                // Bounds check
                if (index < circleCenters.size()) {
                    Point center = circleCenters.get(index);
                    Imgproc.putText(processedMat, Character.toString(optionLabels[j]), center, fontFace, fontScale, color, thickness);
                } else {
                    System.out.println("Index out of bounds: " + index);
                }
            }
        }



        // Label the sorted circles
        for (int i = 0; i < circleCenters.size(); i++) {
            double[] circleParams = circles.get(0, i);
            double detectedRadius = circleParams[2]; // Use the detected radius
            Point center = circleCenters.get(i);
            //Imgproc.putText(processedMat, Integer.toString(i + 1), center, fontFace, fontScale, color, thickness);
            Imgproc.circle(processedMat, center, (int) Math.round(detectedRadius), new Scalar(0, 255, 0), 2); // Use detected radius here
        }
        // Store the detected circles for future reference
        lastDetectedCircles = new ArrayList<>(circleCenters);

        // Release intermediate Mats to free memory
        grayMat.release();
        blurredMat.release();
        circles.release();

        return processedMat; // Return the processed Mat object containing the circles and labels
    }
    public List<Point> getDetectedCircles() {
        return lastDetectedCircles;
    }

}
