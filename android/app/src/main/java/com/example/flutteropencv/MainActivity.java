package com.example.flutteropencv;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import android.os.Bundle;
import android.util.Log;
import java.util.List;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "com.example.omr/processor";
    private static final String TAG = "MainActivity";
    private List<Point> currentDetectedCircles;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed.");
        } else {
            Log.d(TAG, "OpenCV initialization succeeded.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MethodChannel(getFlutterEngine().getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("processImage")) {
                                String imagePath = call.argument("imagePath");
                                Preprocessor preprocessor = new Preprocessor();
                                Mat mat = preprocessor.loadMat(imagePath);

                                Mat preprocessedMat = preprocessor.processMat(mat, this);

                                DetectionUtility detector = new DetectionUtility();
                                Mat processedMat = detector.detectOMRMarks(preprocessedMat, getApplicationContext());

                                currentDetectedCircles = detector.getDetectedCircles();

                                byte[] byteArray = preprocessor.convertMatToByteArray(processedMat);
                                result.success(byteArray);

                            }else if (call.method.equals("prepareMatForTemplateOverlay")) {
                                // New logic for processing image for template overlay
                                String imagePath = call.argument("imagePath");
                                Preprocessor preprocessor = new Preprocessor();
                                Mat mat = preprocessor.loadMat(imagePath);

                                Mat preparedMat = preprocessor.prepareMatForTemplateOverlay(mat, this);

                                byte[] byteArray = preprocessor.convertMatToByteArray(preparedMat);
                                result.success(byteArray);

                            } else if (call.method.equals("saveTemplate")) {
                                TemplateCircleStore.getInstance().saveTemplateCircles(currentDetectedCircles, getApplicationContext());
                                result.success("Template saved successfully.");

                            } else if (call.method.equals("loadTemplate")) {
                                List<Point> loadedCircles = TemplateCircleStore.getInstance().loadTemplateCircles(getApplicationContext());
                                result.success("Template loaded successfully.");

                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }
}
