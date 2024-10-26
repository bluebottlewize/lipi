package org.bluebottlewize.lipi;

import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    KeyboardCanvas canvas;

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

        canvas = findViewById(R.id.keyboard_canvas);

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
    }
}