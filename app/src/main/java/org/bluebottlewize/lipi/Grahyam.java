package org.bluebottlewize.lipi;

import static android.content.ContentValues.TAG;
import static org.bluebottlewize.lipi.Alphabets.MAL_HALF_CONSONANTS;
import static org.bluebottlewize.lipi.Alphabets.MAL_HALF_VOWELS;
import static org.bluebottlewize.lipi.Alphabets.MAL_KOOTTAKSHARAM_SSA;
import static org.bluebottlewize.lipi.Alphabets.SWARAKSHARAMS_STANDALONE;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_CA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_KA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_PA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_TA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_TTA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_YA;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Grahyam
{
    String[] letters;

    private Context context;
    private Interpreter tflite;

    public Grahyam(Context context)
    {
        this.context = context;

        tflite = new Interpreter(loadModelFile("model.tflite"));

        if (tflite != null)
        {
            printTensorDetails();

            letters = loadLetters();
        }
    }

    public MappedByteBuffer loadModelFile(String modelPath)
    {
        try
        {
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
    private void runInference(float[][] input, float[][] output)
    {
        tflite.run(input, output);
    }

    private void printTensorDetails()
    {
        // Print input details
        for (int i = 0; i < tflite.getInputTensorCount(); i++)
        {
            Log.d(TAG, "Input Tensor " + i + ": " + Arrays.toString(tflite.getInputTensor(i).shape()));
        }

//        // Print output details
//        for (int i = 0; i < tflite.getOutputDetails().length; i++) {
//            Log.d(TAG, "Output Tensor " + i + ": " + tflite.getOutputDetails()[i].toString());
//        }
    }

    public String[] runInference(ArrayList<Point> points)
    {
        String[] result = new String[3];

        ArrayList<Point> normalized_points = normalize(points);

        ArrayList<Point> padded_points = pad(normalized_points);

        float[][][] inputTensor = new float[1][137][2];

        for (int i = 0; i < 137; ++i)
        {
            inputTensor[0][i][0] = padded_points.get(i).x;
            inputTensor[0][i][1] = padded_points.get(i).y;
        }

        float[][] outputTensor = new float[1][letters.length];

        tflite.run(inputTensor, outputTensor);

        float max = outputTensor[0][0];
        int prediction = 0;

        result[0] = letters[prediction];
        result[1] = letters[prediction];
        result[2] = letters[prediction];

        float[] topThree = {-1, -1, -1};

        for (int i = 0; i < letters.length; ++i)
        {
            float num = outputTensor[0][i];

            if (num > topThree[0])
            {
                topThree[2] = topThree[1];
                topThree[1] = topThree[0];
                topThree[0] = num;

                result[2] = result[1];
                result[1] = result[0];
                result[0] = letters[i];
            }
            else if (num > topThree[1])
            {
                topThree[2] = topThree[1];
                topThree[1] = num;

                result[2] = result[1];
                result[1] = letters[i];
            }
            else if (num > topThree[2])
            {
                topThree[2] = num;

                result[2] = letters[i];
            }
        }


//        for (int i = 1;i < 12;++i)
//        {
//            if (max < outputTensor[0][i])
//            {
//                max = outputTensor[0][i];
//                prediction = i;
//                result[2] = result[1];
//                result[1] = result[0];
//                result[0] = letters[prediction];
//            }
//        }

//        System.out.println(max);
//        System.out.println(prediction);

        return result;
    }


    public String[] runCombinedInference(ArrayList<Point> points)
    {
        String[] result = new String[3];

        ArrayList<Point> normalized_points = normalize(points);

        ArrayList<Point> padded_points = pad(normalized_points);

        float[][][] inputTensor = new float[1][137][2];

        for (int i = 0; i < 137; ++i)
        {
            inputTensor[0][i][0] = padded_points.get(i).x;
            inputTensor[0][i][1] = padded_points.get(i).y;
        }

        float[][] outputTensor = new float[1][letters.length];

        tflite.run(inputTensor, outputTensor);

        float max = outputTensor[0][0];
        int prediction = 0;

        result[0] = letters[prediction];
        result[1] = letters[prediction];
        result[2] = letters[prediction];

        float[] topThree = {-1, -1, -1};

        for (int i = 0; i < letters.length; ++i)
        {
            float num = outputTensor[0][i];

            if (num > topThree[0])
            {
                topThree[2] = topThree[1];
                topThree[1] = topThree[0];
                topThree[0] = num;

                result[2] = result[1];
                result[1] = result[0];
                result[0] = letters[i];
            }
            else if (num > topThree[1])
            {
                topThree[2] = topThree[1];
                topThree[1] = num;

                result[2] = result[1];
                result[1] = letters[i];
            }
            else if (num > topThree[2])
            {
                topThree[2] = num;

                result[2] = letters[i];
            }
        }


//        for (int i = 1;i < 12;++i)
//        {
//            if (max < outputTensor[0][i])
//            {
//                max = outputTensor[0][i];
//                prediction = i;
//                result[2] = result[1];
//                result[1] = result[0];
//                result[0] = letters[prediction];
//            }
//        }

        if (!result[0].equals(MAL_KOOTTAKSHARAM_SSA))
        {
            return null;
        }

        System.out.println(result[0]);
        System.out.println(topThree[0]);

        if (topThree[0] > 0.9)
        {
            return result;
        }
        else
        {
            return null;
        }
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

        float n_factor_x = (float) x_range / n_range;
        float n_factor_y = (float) y_range / n_range;

        float n_factor = n_factor_x;

        if (y_range > x_range)
        {
            n_factor = n_factor_y;
        }

        int n_min_x = (int) (x_min / n_factor);
        int n_min_y = (int) (y_min / n_factor);

        for (Point p : points)
        {
            Point new_p = new Point();
            new_p.x = ((int) (p.x / n_factor) - n_min_x);
            new_p.y = ((int) (p.y / n_factor) - n_min_y);

            normalized_points.add(new_p);
        }

        return normalized_points;
    }

    public ArrayList<Point> pad(ArrayList<Point> points)
    {
        ArrayList<Point> padded_points = new ArrayList<>(points);

        if (points.size() < 137)
        {
            for (int i = 0; i < 137 - points.size(); ++i)
            {
                padded_points.add(new Point(-10, -10));
            }
        }

        return padded_points;
    }

    public String[] loadLetters()
    {
        String[] result = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            result = Stream.of(
                            SWARAKSHARAMS_STANDALONE,
                            MAL_HALF_VOWELS,
                            MAL_HALF_CONSONANTS,
                            VYANJANAKSHARAMS_KA,
                            VYANJANAKSHARAMS_CA,
                            VYANJANAKSHARAMS_TA,
                            VYANJANAKSHARAMS_TTA,
                            VYANJANAKSHARAMS_PA,
                            VYANJANAKSHARAMS_YA
                    )
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }
        return result;
    }
}
