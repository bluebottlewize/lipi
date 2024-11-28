package org.bluebottlewize.lipi;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

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

        expandTouchArea();
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

    public void setDeleteButtonClickListener(View.OnClickListener listener)
    {
        tDeleteButton.setOnClickListener(listener);
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

    public void expandTouchArea()
    {
        TextView key = typingView.findViewById(R.id.key_label_k);

        ((View) key.getParent()).post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect delegateArea = new Rect();

                ((CardView) key.getParent()).getHitRect(delegateArea);

                delegateArea.top -= 0;
                delegateArea.bottom += 20;
                delegateArea.left -= 5;
                delegateArea.right += 5;

                System.out.println(delegateArea.top + " " + delegateArea.left + " " + delegateArea.right + " " + delegateArea.bottom);

                TouchDelegate expandedArea = new TouchDelegate(delegateArea, (View) key.getParent());

                // give the delegate to an ancestor of the view we're
                // delegating the
                // area to
                if (key.getParent() instanceof View)
                {
                    System.out.println("set delegate");
                    ((View) key.getParent().getParent()).setTouchDelegate(expandedArea);
                }
            }
        });

//        TouchDelegateComposite touchDelegateComposite = new TouchDelegateComposite(typingView.findViewById(R.id.typing_row_3));

//        for (TextView key : letterMap.values())
//        {
//            ((View) key.getParent()).post(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    Rect delegateArea = new Rect();
//                    Rect delegateArea2 = new Rect();
//                    key.getHitRect(delegateArea);
//                    ((View) key.getParent()).getHitRect(delegateArea2);
//                    delegateArea.top -= 200;
//                    delegateArea.bottom += 200;
//                    delegateArea.left -= 200;
//                    delegateArea.right += 200;
//
//                    delegateArea2.top -= 200;
//                    delegateArea2.bottom += 200;
//                    delegateArea2.left -= 200;
//                    delegateArea2.right += 200;
//
//                    TouchDelegate expandedArea = new TouchDelegate(delegateArea, key);
//                    TouchDelegate expandedArea2 = new TouchDelegate(delegateArea2, (View) key.getParent());
//
//                    // give the delegate to an ancestor of the view we're
//                    // delegating the
//                    // area to
//                    if (key.getParent() instanceof View)
//                    {
//                        System.out.println("set delegate");
//                        ((View) key.getParent()).setTouchDelegate(expandedArea);
//                        touchDelegateComposite.addDelegate(expandedArea2);
//                    }
//                }
//            });
//        }
    }
}
