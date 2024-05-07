package com.example.flutteropencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

public class ArucoHandler {

    public void detectArucoMarkers(Mat image, List<Point> centers, List<MatOfPoint2f> corners, Mat ids) {
        try {
            Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250);
            List<Mat> markerCorners = new ArrayList<>();
            Aruco.detectMarkers(image, dictionary, markerCorners, ids);

            for (int i = 0; i < ids.rows(); i++) {
                double[] id = ids.get(i, 0);
                if (id[0] == 0) {
                    MatOfPoint2f markerCorner = new MatOfPoint2f(markerCorners.get(i));
                    corners.add(markerCorner);

                    // Calculate the center point of the marker
                    Point center = new Point();
                    for (Point p : markerCorner.toArray()) {
                        center.x += p.x;
                        center.y += p.y;
                    }
                    center.x /= 4;
                    center.y /= 4;
                    centers.add(center);

                    markerCorners.get(i).release();
                }
            }
        } catch (NullPointerException e) {
            // Handle null pointer exception (e.g., null image)
            System.err.println("Image is null");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Handle argument-related issues
            System.err.println("Invalid arguments");
            e.printStackTrace();
        } catch (Exception e) {
            // Handle any other general exceptions
            System.err.println("An error occurred during Aruco marker detection");
            e.printStackTrace();
        }
    }

    public void drawDetectedMarkers(Mat image, List<Point> centers, List<MatOfPoint2f> corners) {
        try {
            for (int i = 0; i < corners.size(); i++) {
                MatOfPoint2f corner = corners.get(i);
                MatOfPoint points = new MatOfPoint();
                points.fromList(corner.toList());
                List<MatOfPoint> polylines = new ArrayList<>();
                polylines.add(points);
                Imgproc.polylines(image, polylines, true, new Scalar(0, 255, 0), 3);

                // Draw the index label at the center of the marker
                Point center = centers.get(i);
                Imgproc.putText(image, Integer.toString(i), center, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);

                points.release();
            }
        } catch (NullPointerException e) {
            // Handle null pointer exception (e.g., null corners)
            System.err.println("Corners list is null");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Handle argument-related issues
            System.err.println("Invalid arguments");
            e.printStackTrace();
        } catch (Exception e) {
            // Handle any other general exceptions
            System.err.println("An error occurred during drawing markers");
            e.printStackTrace();
        }
    }
}

