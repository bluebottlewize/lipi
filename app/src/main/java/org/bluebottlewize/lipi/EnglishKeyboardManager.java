package org.bluebottlewize.lipi;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class EnglishKeyboardManager
{
    private final Map<String, TextView> numberMap = new HashMap<>();
    private final Map<String, TextView> letterMap = new HashMap<>();

    public View typingView;

    private ImageButton tKeyBoardButton;
    private ImageButton tCapslockButton;
    private ImageButton tDeleteButton;
    private ImageButton tSpaceButton;
    private ImageButton tEnterButton;

    private boolean capslock = true;

    public EnglishKeyboardManager(View view)
    {
        this.typingView = view;

        numberMap.put("1", view.findViewById(R.id.key_label_1));
        numberMap.put("2", view.findViewById(R.id.key_label_2));
        numberMap.put("3", view.findViewById(R.id.key_label_3));
        numberMap.put("4", view.findViewById(R.id.key_label_4));
        numberMap.put("5", view.findViewById(R.id.key_label_5));
        numberMap.put("6", view.findViewById(R.id.key_label_6));
        numberMap.put("7", view.findViewById(R.id.key_label_7));
        numberMap.put("8", view.findViewById(R.id.key_label_8));
        numberMap.put("9", view.findViewById(R.id.key_label_9));
        numberMap.put("0", view.findViewById(R.id.key_label_0));

        letterMap.put("Q", view.findViewById(R.id.key_label_q));
        letterMap.put("W", view.findViewById(R.id.key_label_w));
        letterMap.put("E", view.findViewById(R.id.key_label_e));
        letterMap.put("R", view.findViewById(R.id.key_label_r));
        letterMap.put("T", view.findViewById(R.id.key_label_t));
        letterMap.put("Y", view.findViewById(R.id.key_label_y));
        letterMap.put("U", view.findViewById(R.id.key_label_u));
        letterMap.put("I", view.findViewById(R.id.key_label_i));
        letterMap.put("O", view.findViewById(R.id.key_label_o));
        letterMap.put("P", view.findViewById(R.id.key_label_p));

        letterMap.put("A", view.findViewById(R.id.key_label_a));
        letterMap.put("S", view.findViewById(R.id.key_label_s));
        letterMap.put("D", view.findViewById(R.id.key_label_d));
        letterMap.put("F", view.findViewById(R.id.key_label_f));
        letterMap.put("G", view.findViewById(R.id.key_label_g));
        letterMap.put("H", view.findViewById(R.id.key_label_h));
        letterMap.put("J", view.findViewById(R.id.key_label_j));
        letterMap.put("K", view.findViewById(R.id.key_label_k));
        letterMap.put("L", view.findViewById(R.id.key_label_l));

        letterMap.put("Z", view.findViewById(R.id.key_label_z));
        letterMap.put("X", view.findViewById(R.id.key_label_x));
        letterMap.put("C", view.findViewById(R.id.key_label_c));
        letterMap.put("V", view.findViewById(R.id.key_label_v));
        letterMap.put("B", view.findViewById(R.id.key_label_b));
        letterMap.put("N", view.findViewById(R.id.key_label_n));
        letterMap.put("M", view.findViewById(R.id.key_label_m));

        tCapslockButton = view.findViewById(R.id.typing_capslock_button);
        tDeleteButton = view.findViewById(R.id.typing_delete_button);
        tSpaceButton = view.findViewById(R.id.typing_space_button);
        tEnterButton = view.findViewById(R.id.typing_enter_button);

        tCapslockButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleCapslock();
            }
        });

        expandTouchArea(R.id.typing_row_1, 5, -10, 10, -10, 10);
        expandTouchArea(R.id.typing_row_2, 5, 0, 10, -10, 10);
        expandTouchArea(R.id.typing_row_3, 5, 0, 10, -10, 10);
        expandTouchArea(R.id.typing_row_4, 5, 0, 10, -10, 10);
        expandTouchArea(R.id.typing_row_5, 5, 0, 10, -10, 10);
    }


    public void setKeyClickListener(int key, View.OnClickListener listener)
    {
        TextView keyView = letterMap.get(key);
        if (keyView != null)
        {
            keyView.setOnClickListener(listener);
        }
    }


    public void setAllKeysClickListener(View.OnClickListener listener)
    {
        for (TextView keyView : numberMap.values())
        {
            keyView.setOnClickListener(listener);
        }

        for (TextView keyView : letterMap.values())
        {
            keyView.setOnClickListener(listener);
        }
    }

    public void setDeleteButtonClickListener(View.OnTouchListener listener)
    {
        tDeleteButton.setOnTouchListener(listener);
    }

    public void setEnterButtonClickListener(View.OnClickListener listener)
    {
        tEnterButton.setOnClickListener(listener);
    }

    public void setSpaceButtonClickListener(View.OnClickListener listener)
    {
        tSpaceButton.setOnClickListener(listener);
    }

    public TextView getKeyView(String key)
    {
        return letterMap.get(key);
    }


    public boolean isCapslock()
    {
        return capslock;
    }


    public void toggleCapslock()
    {
        capslock = !capslock;

        for (TextView keyView : letterMap.values())
        {
            if (capslock)
            {
                keyView.setText(keyView.getText().toString().toUpperCase());
            }
            else
            {
                keyView.setText(keyView.getText().toString().toLowerCase());
            }
        }
    }

    public void expandTouchArea(int rowID, int middle, int extraPixelsTop, int extraPixelsBottom, int extraPixelsLeft, int extraPixelsRight)
    {
        View row = typingView.findViewById(rowID);
        TouchDelegateComposite touchDelegateComposite = new TouchDelegateComposite(row);

        for (int i = 0; i < ((ViewGroup) row).getChildCount(); ++i)
        {
            ViewGroup key = (ViewGroup) ((ViewGroup) row).getChildAt(i);

//            System.out.println(((TextView) (key).getChildAt(0)).getText());

//            System.out.println(key.getText());
            int finalI = i;
            key.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Rect delegateArea = new Rect();
                    key.getHitRect(delegateArea);

                    if (finalI < middle)
                    {
                        delegateArea.left += extraPixelsLeft;
                    }
                    else
                    {
                        delegateArea.right += extraPixelsRight;
                    }
                    delegateArea.top += extraPixelsTop;
                    delegateArea.bottom += extraPixelsBottom;

                    TouchDelegate expandedArea = new TouchDelegate(delegateArea, key);

                    touchDelegateComposite.addDelegate(expandedArea);
                }
            });
        }

        row.setTouchDelegate(touchDelegateComposite);
    }
}
