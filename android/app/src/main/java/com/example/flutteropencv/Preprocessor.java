package com.example.flutteropencv;

import  android.content.Context;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Preprocessor {
    // Add an instance of ArucoHandler
    private final ArucoHandler arucoHandler = new ArucoHandler();

    // Desired dimensions for the warped and cropped image
    private final int desiredWidth = 800;
    private final int desiredHeight = 800;

    public Mat loadMat(String imagePath) {
        return Imgcodecs.imread(imagePath); // Read the image from the file path directly into a Mat object
    }

    public byte[] convertMatToByteArray(Mat processedMat) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", processedMat, matOfByte);
        return matOfByte.toArray();
    }

    public Mat processMat(Mat mat,Context context) {
        Mat bgrMat = new Mat();
        Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_RGBA2BGR);
        List<Point> centers = new ArrayList<>(); // Added to store center points
        List<MatOfPoint2f> corners = new ArrayList<>();
        Mat ids = new Mat();

        // Call ArucoHandler methods
        arucoHandler.detectArucoMarkers(bgrMat, centers, corners, ids);
        arucoHandler.drawDetectedMarkers(bgrMat, centers, corners);

        // Get perspective transformation matrix
        //Mat transformMatrix = getPerspectiveTransformationMatrix(centers);
        Mat transformMatrix = getPerspectiveTransformationMatrixRANSAC(centers);

        // Warp the perspective
        Mat warpedMat = new Mat(desiredWidth, desiredHeight, bgrMat.type());
        Imgproc.warpPerspective(bgrMat, warpedMat, transformMatrix, new Size(desiredWidth, desiredHeight));

        bgrMat.release();
        transformMatrix.release();

        // Pass the processed Mat object to DetectionUtility for further processing
        DetectionUtility detectionUtility = new DetectionUtility();
        return detectionUtility.detectOMRMarks(warpedMat, context);// Assuming the method name is detectOMRMarks

    }
    public Mat prepareMatForTemplateOverlay(Mat mat,Context context) {
        Mat bgrMat = new Mat();
        Imgproc.cvtColor(mat, bgrMat, Imgproc.COLOR_RGBA2BGR);
        List<Point> centers = new ArrayList<>(); // Added to store center points
        List<MatOfPoint2f> corners = new ArrayList<>();
        Mat ids = new Mat();

        // Call ArucoHandler methods
        arucoHandler.detectArucoMarkers(bgrMat, centers, corners, ids);
        arucoHandler.drawDetectedMarkers(bgrMat, centers, corners);

        // Get perspective transformation matrix
        //Mat transformMatrix = getPerspectiveTransformationMatrix(centers);
        Mat transformMatrix = getPerspectiveTransformationMatrixRANSAC(centers);
        // Warp the perspective
        Mat warpedMat = new Mat(desiredWidth, desiredHeight, bgrMat.type());
        Imgproc.warpPerspective(bgrMat, warpedMat, transformMatrix, new Size(desiredWidth, desiredHeight));

        TemplateOverlayUtility overlayTemplateCircles = new TemplateOverlayUtility();
        return overlayTemplateCircles.overlayTemplateCircles(warpedMat, context);// Assuming the method name is detectOMRMarks

    }

    private Mat getPerspectiveTransformationMatrix(List<Point> centers) { // Modified to use centers
        // Define destination points in the order: top-left, top-right, bottom-right, bottom-left
        Point[] destPoints = {
                new Point(0, 0),
                new Point(desiredWidth - 1, 0),
                new Point(desiredWidth - 1, desiredHeight - 1),
                new Point(0, desiredHeight - 1)
        };

        MatOfPoint2f dest = new MatOfPoint2f(destPoints);
        MatOfPoint2f src = new MatOfPoint2f(
                centers.get(3), // top-left
                centers.get(2), // top-right
                centers.get(0), // bottom-right
                centers.get(1)  // bottom-left
        );

        return Imgproc.getPerspectiveTransform(src, dest);
    }
    private Mat getPerspectiveTransformationMatrixRANSAC(List<Point> centers) {
        // Sort points based on Y-coordinate (and then X-coordinate)
        Collections.sort(centers, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                if (p1.y > p2.y) return 1;
                if (p1.y < p2.y) return -1;
                if (p1.x > p2.x) return 1;
                if (p1.x < p2.x) return -1;
                return 0;
            }
        });

        // Create sorted src
        MatOfPoint2f src = new MatOfPoint2f(
                centers.get(0), // Top-left
                centers.get(1), // Top-right
                centers.get(3), // Bottom-left
                centers.get(2)  // Bottom-right
        );

        // Define destination points
        Point[] dstPoints = new Point[4];
        dstPoints[0] = new Point(0, 0);
        dstPoints[1] = new Point(desiredWidth - 1, 0);
        dstPoints[2] = new Point(desiredWidth - 1, desiredHeight - 1);
        dstPoints[3] = new Point(0, desiredHeight - 1);

        MatOfPoint2f dst = new MatOfPoint2f(dstPoints);

        // Use findHomography with RANSAC
        Mat homography = Calib3d.findHomography(src, dst, Calib3d.RANSAC, 3);

        return homography;
    }
}



