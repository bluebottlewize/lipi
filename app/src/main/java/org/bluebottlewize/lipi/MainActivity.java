package org.bluebottlewize.lipi;

import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_A;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_AA;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_E;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_ERU;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_I;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_O;
import static org.bluebottlewize.lipi.Alphabets.MAL_SWARAKSHARAM_U;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_GA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_GHA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_KA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_KHA;
import static org.bluebottlewize.lipi.Alphabets.MAL_VYANJANAKSHARAM_NGA;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    KeyboardCanvas canvas;

    Uri savedDirectory;

    private static final int REQUEST_CODE_OPEN_DIRECTORY = 200;

    TextView letterbox;

    String currentLetter = "";
    int i = 0;

//    String letters[] = new String[]{
//            MAL_VYANJANAKSHARAM_KA,
//            MAL_VYANJANAKSHARAM_KHA,
//            MAL_VYANJANAKSHARAM_GA,
//            MAL_VYANJANAKSHARAM_GHA,
//            MAL_VYANJANAKSHARAM_NGA
//    };

    String letters[] = new String[]{
            MAL_SWARAKSHARAM_A,
            MAL_SWARAKSHARAM_AA,
            MAL_SWARAKSHARAM_I,
            MAL_SWARAKSHARAM_U,
            MAL_SWARAKSHARAM_ERU,
            MAL_SWARAKSHARAM_E,
            MAL_SWARAKSHARAM_O,
            MAL_VYANJANAKSHARAM_KA,
            MAL_VYANJANAKSHARAM_KHA,
            MAL_VYANJANAKSHARAM_GA,
            MAL_VYANJANAKSHARAM_GHA,
            MAL_VYANJANAKSHARAM_NGA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        ViewTreeObserver vto = canvas.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                canvas.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = canvas.getMeasuredWidth();
                int height = canvas.getMeasuredHeight();
                canvas.init(height, width);
            }
        });

        nextLetter();

        canvas.setOnKeyboardActionListener(new KeyboardCanvas.OnKeyboardActionListener() {
            @Override
            public void onWritten(ArrayList<Point> points, String letter) {
                String result = grahyam.runInference(points);
                System.out.println(letter);
                writeToFile(currentLetter, points);
                nextLetter();
            }
        });



        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_OPEN_DIRECTORY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DIRECTORY && resultCode == Activity.RESULT_OK) {
            Uri directoryUri = data.getData();
            takePersistableUriPermission(directoryUri);
            savedDirectory = directoryUri;
        }
    }

    private void takePersistableUriPermission(Uri uri) {
        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public void writeToFile(String letter, ArrayList<Point> points)
    {
        DocumentFile savedFolder = DocumentFile.fromTreeUri(this, savedDirectory);
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

        for (DocumentFile file : files) {
            if (file.isFile()) {
                if (lastModifiedFile == null || file.lastModified() > lastModifiedFile.lastModified()) {
                    lastModifiedFile = file;
                }
            }
        }

        if (lastModifiedFile != null) {
            filename = lastModifiedFile.getName();
        }

        String newFileName = null;

        if (filename == null)
        {
            newFileName = letter + "_" + String.format("%06d", 1);
        }
        else {
            int last = Integer.parseInt(filename.substring(filename.indexOf('_') + 1, filename.indexOf('_') + 1 + 6));
            ++last;
            newFileName = letter + "_" + String.format("%06d", last);
        }

        DocumentFile outputFile = letterFolder.createFile("text/plain", newFileName);

        if (outputFile != null) {
            try (OutputStream outputStream = getContentResolver().openOutputStream(outputFile.getUri());
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                // Write content to the file

                for (Point p : points)
                {
                    writer.write(p.x + " " + p.y + "\n");
                }

                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void nextLetter()
    {
        currentLetter = letters[i % (letters.length)];
        letterbox.setText(currentLetter);

        ++i;
    }
}