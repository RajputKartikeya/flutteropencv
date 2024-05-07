package com.example.flutteropencv;

import android.content.Context;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class TemplateOverlayUtility {

    public Mat overlayTemplateCircles(Mat warpedMat, Context context) {
        // Load the template circles
        List<Point> loadedCircles = TemplateCircleStore.getInstance().loadTemplateCircles(context);

        // Overlay the circles on the warpedMat
        for (Point center : loadedCircles) {
            Imgproc.circle(warpedMat, center, 10, new Scalar(0, 255, 0), 3);
        }

        return warpedMat;
    }
}
