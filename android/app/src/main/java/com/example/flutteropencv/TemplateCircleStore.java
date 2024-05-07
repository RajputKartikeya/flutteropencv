package com.example.flutteropencv;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.opencv.core.Point;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TemplateCircleStore {
    private static TemplateCircleStore instance;
    private List<Point> circleCenters;
    private static final String FILE_NAME = "templateCircles.json";

    private TemplateCircleStore() {
        circleCenters = new ArrayList<>();
    }

    public static TemplateCircleStore getInstance() {
        if (instance == null) {
            instance = new TemplateCircleStore();
        }
        return instance;
    }

    public void saveTemplateCircles(List<Point> circleCenters, Context context) {
        this.circleCenters = new ArrayList<>(circleCenters);
        Gson gson = new Gson();
        String jsonString = gson.toJson(this.circleCenters);

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(jsonString);
            osw.flush();
            Log.d("TemplateCircleStore", "Circles saved to " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Point> loadTemplateCircles(Context context) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[1024];
            int charRead;
            StringBuilder jsonString = new StringBuilder();
            while ((charRead = isr.read(inputBuffer)) > 0) {
                jsonString.append(String.copyValueOf(inputBuffer, 0, charRead));
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<Point>>() {}.getType();
            circleCenters = gson.fromJson(jsonString.toString(), type);
            return new ArrayList<>(circleCenters);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
