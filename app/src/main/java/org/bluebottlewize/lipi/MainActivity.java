package org.bluebottlewize.lipi;

import static org.bluebottlewize.lipi.Alphabets.CHILLAKSHARAMS;
import static org.bluebottlewize.lipi.Alphabets.MAL_HALF_CONSONANT_RA;
import static org.bluebottlewize.lipi.Alphabets.MAL_HALF_CONSONANT_VA;
import static org.bluebottlewize.lipi.Alphabets.MAL_HALF_CONSONANT_YA;
import static org.bluebottlewize.lipi.Alphabets.MAL_KOOTTAKSHARAMS;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_EE;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_I;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_II;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_OU;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_R;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_U;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_UU;
import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_VIRAMAM;
import static org.bluebottlewize.lipi.Alphabets.SWARAKSHARAMS_STANDALONE;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_CA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_KA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_PA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_TA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_TTA;
import static org.bluebottlewize.lipi.Alphabets.VYANJANAKSHARAMS_YA;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{

    KeyboardCanvas canvas;

    Uri savedDirectory;

    private static final int REQUEST_CODE_OPEN_DIRECTORY = 200;

    TextView letterbox;
    TextView dataNumberBox;

    EditText load_box;

    String currentLetter = "";
    int i = 0;

//    String letters[] = new String[]{
//            MAL_VYANJANAKSHARAM_KA,
//            MAL_VYANJANAKSHARAM_KHA,
//            MAL_VYANJANAKSHARAM_GA,
//            MAL_VYANJANAKSHARAM_GHA,
//            MAL_VYANJANAKSHARAM_NGA
//    };

//    String[] letters = new String[]{
//            MAL_SWARAKSHARAM_A,
//            MAL_SWARAKSHARAM_AA,
//            MAL_SWARAKSHARAM_I,
//            MAL_SWARAKSHARAM_U,
//            MAL_SWARAKSHARAM_ERU,
//            MAL_SWARAKSHARAM_E,
//            MAL_SWARAKSHARAM_O,
//            MAL_VYANJANAKSHARAM_KA,
//            MAL_VYANJANAKSHARAM_KHA,
//            MAL_VYANJANAKSHARAM_GA,
//            MAL_VYANJANAKSHARAM_GHA,
//            MAL_VYANJANAKSHARAM_NGA,
//            MAL_VOWEL_AA,
//            MAL_VOWEL_E
//            MAL_KOOTTAKSHARAM_SSA
//    };

    String[] letters;

    String[] letters_0 = new String[]{
//            MAL_VOWEL_AA,
            MAL_VOWEL_I,
            MAL_VOWEL_II,
            MAL_VOWEL_U,
            MAL_VOWEL_UU,
            MAL_VOWEL_R,
//            MAL_VOWEL_E,
            MAL_VOWEL_EE,
//            MAL_VOWEL_AI,
//            MAL_VOWEL_O,
//            MAL_VOWEL_OO,
            MAL_VOWEL_OU,
            MAL_VOWEL_VIRAMAM,
            MAL_HALF_CONSONANT_YA,
            MAL_HALF_CONSONANT_RA,
            MAL_HALF_CONSONANT_VA
//            MAL_VOWEL_DOT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Grahyam grahyam = new Grahyam(this);

        canvas = findViewById(R.id.keyboard_canvas);
        letterbox = findViewById(R.id.letter_view);
        dataNumberBox = findViewById(R.id.data_last_number_box);

        ViewTreeObserver vto = canvas.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                canvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = canvas.getMeasuredWidth();
                int height = canvas.getMeasuredHeight();
                canvas.init(height, width);
            }
        });

        load_box = findViewById(R.id.load_box);

        letters = Arrays.copyOf(letters_0, letters_0.length);

        nextLetter();

        canvas.isDataCollection = true;

        canvas.setOnKeyboardActionListener(new KeyboardCanvas.OnKeyboardActionListener()
        {
            @Override
            public void onWritten(ArrayList<Point> points, ArrayList<Point> previous_points, String[] predictions)
            {
                // String result = grahyam.runInference(points)[0];
                // System.out.println(predictions[0]);
//                System.out.println(points.size());
//                writeToFile(currentLetter, points);
//                nextLetter();
            }
        });


        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK)
        {
            Uri directoryUri = data.getData();
            takePersistableUriPermission(directoryUri);
            savedDirectory = directoryUri;
        }
    }

    private void takePersistableUriPermission(Uri uri)
    {
        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public void writeToFile(String letter, ArrayList<Point> points)
    {

        File train_dir = this.getDir("mal-htr", MODE_PRIVATE);

        DocumentFile savedFolder = DocumentFile.fromFile(train_dir);
        String filename = null;

        assert savedFolder != null;

        DocumentFile letterFolder = savedFolder.findFile(letter);

        if (letterFolder == null)
        {
            letterFolder = savedFolder.createDirectory(letter);
        }

        assert letterFolder != null;
        DocumentFile[] files = letterFolder.listFiles();

        DocumentFile lastModifiedFile = null;

        for (DocumentFile file : files)
        {
            if (file.isFile())
            {
                if (lastModifiedFile == null || file.lastModified() > lastModifiedFile.lastModified())
                {
                    lastModifiedFile = file;
                }
            }
        }

        if (lastModifiedFile != null)
        {
            filename = lastModifiedFile.getName();
        }

        String newFileName = null;

        if (filename == null)
        {
            newFileName = letter + "_" + String.format("%06d", 1);
        }
        else
        {
            int last = Integer.parseInt(filename.substring(filename.indexOf('_') + 1, filename.indexOf('_') + 1 + 6));
            ++last;
            newFileName = letter + "_" + String.format("%06d", last);
        }

        DocumentFile outputFile = letterFolder.createFile("text/plain", newFileName);

        if (outputFile != null)
        {
            try (OutputStream outputStream = getContentResolver().openOutputStream(outputFile.getUri());
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream)))
            {

                // Write content to the file

                StringBuilder buffer = new StringBuilder();

                for (Point p : points)
                {
                    buffer.append(p.x).append(" ").append(p.y).append("\n");
                }

                writer.write(buffer.toString());
                writer.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    void nextLetter()
    {
        currentLetter = letters[i % (letters.length)];
        letterbox.setText(currentLetter);
        setDataNumber(currentLetter);

        ++i;
    }

    public void exportData(View view)
    {
        ZipUtils.zipFolderAndSaveToExternalStorage(this, "mal-htr", savedDirectory);
    }

    public void saveCoordinates(View view)
    {
        System.out.println(canvas.points.size());
        writeToFile(currentLetter, canvas.points);
        nextLetter();

        canvas.newCoordinateList();
        canvas.clearBoard();
    }

    public void cancelCoordinates(View view)
    {
        canvas.newCoordinateList();
        canvas.clearBoard();
    }


    public void loadLetters(View view)
    {
        String load_no = load_box.getText().toString();

        int no = 0;

        if (load_no.isEmpty())
        {
            no = 0;
        }
        else
        {
            try
            {
                no = Integer.parseInt(load_no);
            }
            catch (Exception e)
            {
                no = 0;
            }
        }

        switch (no)
        {
            case 0:
                letters = Arrays.copyOf(SWARAKSHARAMS_STANDALONE, SWARAKSHARAMS_STANDALONE.length);
                break;
            case 1:
                letters = Arrays.copyOf(letters_0, letters_0.length);
                break;
            case 2:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_KA, VYANJANAKSHARAMS_KA.length);
                break;
            case 3:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_CA, VYANJANAKSHARAMS_CA.length);
                break;
            case 4:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_TTA, VYANJANAKSHARAMS_TTA.length);
                break;
            case 5:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_TA, VYANJANAKSHARAMS_TA.length);
                break;
            case 6:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_PA, VYANJANAKSHARAMS_PA.length);
                break;
            case 7:
                letters = Arrays.copyOf(VYANJANAKSHARAMS_YA, VYANJANAKSHARAMS_YA.length);
                break;
            case 8:
                letters = Arrays.copyOf(CHILLAKSHARAMS, CHILLAKSHARAMS.length);
                break;
            case 9:
                letters = Arrays.copyOf(MAL_KOOTTAKSHARAMS, MAL_KOOTTAKSHARAMS.length);
                break;
            default:
                letters = Arrays.copyOf(SWARAKSHARAMS_STANDALONE, SWARAKSHARAMS_STANDALONE.length);
                break;
        }

        i = 0;
        nextLetter();
    }

    public void setDataNumber(String letter)
    {
        File train_dir = this.getDir("mal-htr", MODE_PRIVATE);

        DocumentFile savedFolder = DocumentFile.fromFile(train_dir);
        String filename = null;

        assert savedFolder != null;

        DocumentFile letterFolder = savedFolder.findFile(letter);

        if (letterFolder == null)
        {
            letterFolder = savedFolder.createDirectory(letter);
        }

        assert letterFolder != null;
        DocumentFile[] files = letterFolder.listFiles();

        dataNumberBox.setText(Integer.toString(files.length));
    }
}