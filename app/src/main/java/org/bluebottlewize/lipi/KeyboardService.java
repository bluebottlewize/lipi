package org.bluebottlewize.lipi;

import static org.bluebottlewize.lipi.Alphabets.MAL_VOWEL_E;
import static org.bluebottlewize.lipi.Alphabets.ZERO_WIDTH_SPACE;

import android.graphics.Point;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class KeyboardService extends InputMethodService implements KeyboardCanvas.OnKeyboardActionListener
{

    InputConnection inputConnection;

    TextView prediction_box_1;
    TextView prediction_box_2;
    TextView prediction_box_3;

    View handwriting_view;
    View typing_view;

    String composingText = "";

    Grahyam grahyam;

    boolean isHandwritingMode = true;

    @Override
    public View onCreateInputView()
    {
        // get the KeyboardView and add our Keyboard layout to it


        View keyboardLayout = getLayoutInflater().inflate(R.layout.keyboard_layout, null);
//        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200); // Convert 200dp to pixels);
//        keyboardLayout.setLayoutParams(params);

        handwriting_view = keyboardLayout.findViewById(R.id.handwriting_view);
        typing_view = keyboardLayout.findViewById(R.id.typing_view);

        EnglishKeyboardManager keyboardManager = new EnglishKeyboardManager(typing_view);

        keyboardManager.setAllKeysClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputConnection.commitText(((TextView) v).getText().toString(), 1);

                if (keyboardManager.isCapslock())
                {
                    keyboardManager.toggleCapslock();
                }
            }
        });

        keyboardManager.setDeleteButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputConnection.deleteSurroundingText(1, 0);
            }
        });

        keyboardManager.setEnterButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputConnection.commitText("\n", 1);
            }
        });

        keyboardManager.setSpaceButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputConnection.commitText(" ", 1);
            }
        });

        ImageButton spaceButton = keyboardLayout.findViewById(R.id.space_button);
        ImageButton backspaceButton = keyboardLayout.findViewById(R.id.backspace_button);
        ImageButton hKeyboardButton = keyboardLayout.findViewById(R.id.keyboard_button);
        ImageButton tKeyboardButton = keyboardLayout.findViewById(R.id.typing_keyboard_button);


        grahyam = new Grahyam(this);

        inputConnection = getCurrentInputConnection();

        spaceButton.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
                return false;
            }
        });

        spaceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                inputConnection.commitText(" ", 1);
            }
        });

        backspaceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                    inputConnection.deleteSurroundingText(1, 0);

                System.out.println("composeing " + composingText);
                System.out.println(inputConnection.getTextBeforeCursor(1, 0) + " " + inputConnection.getTextAfterCursor(1, 0));
                if (composingText.isEmpty() && inputConnection.getTextBeforeCursor(1, 0).equals(MAL_VOWEL_E))
                {
                    inputConnection.deleteSurroundingText(2, 0);
                    composingText = "";
                    composingText += MAL_VOWEL_E;
                    inputConnection.setComposingText(composingText, 1);
                }
                else
                {
                    inputConnection.finishComposingText();
                    composingText = "";
                    inputConnection.deleteSurroundingText(1, 0);
                }

                if (inputConnection.getTextBeforeCursor(1, 0).equals(ZERO_WIDTH_SPACE))
                {
                    inputConnection.deleteSurroundingText(1, 0);
                }
            }
        });

        hKeyboardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handwriting_view.setVisibility(View.INVISIBLE);
                typing_view.setVisibility(View.VISIBLE);
                isHandwritingMode = false;
            }
        });

        tKeyboardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                handwriting_view.setVisibility(View.VISIBLE);
                typing_view.setVisibility(View.INVISIBLE);
                isHandwritingMode = true;
            }
        });

        prediction_box_1 = keyboardLayout.findViewById(R.id.prediction_box_1);
        prediction_box_2 = keyboardLayout.findViewById(R.id.prediction_box_2);
        prediction_box_3 = keyboardLayout.findViewById(R.id.prediction_box_3);

        KeyboardCanvas canvas = keyboardLayout.findViewById(R.id.keyboard_canvas);
        canvas.isDataCollection = false;
        canvas.setOnKeyboardActionListener(this);

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

        return keyboardLayout;
    }

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting)
    {
        super.onStartInputView(editorInfo, restarting);

        inputConnection = getCurrentInputConnection();
    }

    @Override
    public void onWritten(ArrayList<Point> points, ArrayList<Point> previous_points, String[] predictions)
    {

        System.out.println(composingText);
//        inputConnection.commitText(predictions[0], 1);

        ArrayList<Point> combined_points = new ArrayList<>(previous_points);
        combined_points.addAll(points);

        if (combined_points.size() < 137)
        {
            String[] new_prediction = grahyam.runCombinedInference(combined_points);

            if (new_prediction != null)
            {
                inputConnection.deleteSurroundingText(1, 0);
                inputConnection.commitText(new_prediction[0], 1);
                return;
            }
        }

        if (predictions[0] == MAL_VOWEL_E)
        {
            inputConnection.commitText(ZERO_WIDTH_SPACE, 1);
            composingText = "";
            composingText += MAL_VOWEL_E;
            inputConnection.setComposingText(composingText, 1);
        }
        else if (composingText.equals(MAL_VOWEL_E))
        {
            composingText = predictions[0] + composingText;
            inputConnection.setComposingText(composingText, 2);
            inputConnection.finishComposingText();
            // inputConnection.commitText(composingText, 1);
            composingText = "";
        }
        else
        {
            inputConnection.commitText(predictions[0], 1);
        }

        Toast.makeText(this.getApplicationContext(), predictions[0], Toast.LENGTH_SHORT).show();

        prediction_box_1.setText(predictions[0]);
        prediction_box_2.setText(predictions[1]);
        prediction_box_3.setText(predictions[2]);

        for (Point p : points)
        {
            System.out.println(p.x + " " + p.y);
        }
    }

    @Override
    public boolean onEvaluateFullscreenMode()
    {
        return false;
    }
}
