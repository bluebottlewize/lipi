package org.bluebottlewize.lipi;
import static android.content.ContentValues.TAG;

import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_GA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_GHA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_KA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_KHA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_NGA;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class Grahyam
{
    String[] letters = new String[]{
            MAL_VYANJANAKSHARAM_KA,
            MAL_VYANJANAKSHARAM_KHA,
            MAL_VYANJANAKSHARAM_GA,
            MAL_VYANJANAKSHARAM_GHA,
            MAL_VYANJANAKSHARAM_NGA
    };


    private Context context;
    private Interpreter tflite;

    public Grahyam(Context context)
    {
        this.context = context;

        tflite = new Interpreter(loadModelFile("model.tflite"));

        if (tflite != null)
        {
            printTensorDetails();
        }
    }

    public MappedByteBuffer loadModelFile(String modelPath)
    {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    // Method to run inference (example usage)
    private void runInference(float[][] input, float[][] output) {
        tflite.run(input, output);
    }

    private void printTensorDetails() {
        // Print input details
        for (int i = 0; i < tflite.getInputTensorCount(); i++) {
            Log.d(TAG, "Input Tensor " + i + ": " + Arrays.toString(tflite.getInputTensor(i).shape()));
        }

//        // Print output details
//        for (int i = 0; i < tflite.getOutputDetails().length; i++) {
//            Log.d(TAG, "Output Tensor " + i + ": " + tflite.getOutputDetails()[i].toString());
//        }
    }

    public String runInference(ArrayList<Point> points)
    {
        String result = "";

        ArrayList<Point> normalized_points = normalize(points);

        ArrayList<Point> padded_points = pad(normalized_points);

        float[][][] inputTensor = new float[1][137][2];

        for (int i = 0;i < 137;++i)
        {
            inputTensor[0][i][0] = padded_points.get(i).x;
            inputTensor[0][i][1] = padded_points.get(i).y;
        }

        float[][] outputTensor = new float[1][5];

        tflite.run(inputTensor, outputTensor);

        float max = outputTensor[0][0];
        int prediction = 0;

        for (int i = 1;i < 5;++i)
        {
            if (max < outputTensor[0][i])
            {
                max = outputTensor[0][i];
                prediction = i;
            }
        }

        System.out.println(max);
        System.out.println(prediction);

        result += letters[prediction];

        return result;
    }

    public ArrayList<Point> normalize(ArrayList<Point> points)
    {
        ArrayList<Point> normalized_points = new ArrayList<>();

        int x_min = points.get(0).x;
        int y_min = points.get(0).y;
        int x_max = points.get(0).x;
        int y_max = points.get(0).y;

        for (Point p : points)
        {
            if (p.x > x_max)
            {
                x_max = p.x;
            }

            if (p.x < x_min)
            {
                x_min = p.x;
            }

            if (p.y > y_max)
            {
                y_max = p.y;
            }

            if (p.y < y_min)
            {
                y_min = p.y;
            }
        }

        int n_range = 100;
        int x_range = x_max - x_min;
        int y_range = y_max - y_min;

        int n_factor_x = x_range / n_range;
        int n_factor_y = y_range / n_range;

        int n_factor = n_factor_x;

        if (y_range > x_range) {
            n_factor = n_factor_y;
        }

        int n_min_x = x_min / n_factor;
        int n_min_y = y_min / n_factor;

        for (Point p : points)
        {
            Point new_p = new Point();
            new_p.x = (p.x / n_factor) - n_min_x;
            new_p.y = (p.y / n_factor) - n_min_y;

            normalized_points.add(new_p);
        }

        return normalized_points;
    }

    public ArrayList<Point> pad(ArrayList<Point> points)
    {
        ArrayList<Point> padded_points = new ArrayList<>(points);

        if (points.size() < 137)
        {
            for (int i = 0;i < 137 - points.size();++i)
            {
                padded_points.add(new Point(-10, -10));
            }
        }

        return padded_points;
    }
}
