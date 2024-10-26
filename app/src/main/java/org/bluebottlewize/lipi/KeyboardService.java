package org.bluebottlewize.lipi;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.graphics.Point;
import android.inputmethodservice.InputMethodService;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.util.ArrayList;

public class KeyboardService extends InputMethodService implements KeyboardCanvas.OnKeyboardActionListener {

    @Override
    public View onCreateInputView() {
        // get the KeyboardView and add our Keyboard layout to it


        View keyboardLayout = getLayoutInflater().inflate(R.layout.keyboard_layout, null);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200); // Convert 200dp to pixels);
//        keyboardLayout.setLayoutParams(params);

        KeyboardCanvas canvas = keyboardLayout.findViewById(R.id.keyboard_canvas);
        canvas.setOnKeyboardActionListener(this);

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

        return keyboardLayout;
    }

    @Override
    public void onWritten(ArrayList<Point> points) {
        int letter = 97;
        InputConnection ic = getCurrentInputConnection();
        ic.commitText("" + (char) letter, 1);
        Toast.makeText(this.getApplicationContext(), "" + letter, Toast.LENGTH_SHORT).show();

        for (Point p : points)
        {
            System.out.println(p.x + " " + p.y);
        }
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }
}