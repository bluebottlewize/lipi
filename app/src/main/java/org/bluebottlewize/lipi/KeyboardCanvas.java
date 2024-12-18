package org.bluebottlewize.lipi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class KeyboardCanvas extends View
{

    public interface OnKeyboardActionListener
    {
        void onWritten(ArrayList<Point> points, ArrayList<Point> previous_points, String[] predictions);
    }

    private OnKeyboardActionListener mKeyboardActionListener;

    public ArrayList<Point> points;
    private ArrayList<Point> previous_points;

    private static final float TOUCH_TOLERANCE = 1;
    private float mX, mY;
    private Path mPath;

    // the Paint class encapsulates the color
    // and style information about
    // how to draw the geometries,text and bitmaps
    private Paint mPaint;
    private Paint mPaintBlur;

    // ArrayList to store all the strokes
    // drawn by the user on the Canvas
    private ArrayList<Stroke> paths = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    Grahyam grahyam;

    Handler clearBoardHandler;

    boolean isDataCollection = false;

    private final Runnable clearBoard = new Runnable()
    {

        public void run()
        {
//            uncomment in data collection
//            comment in new data collection. take points from main activity iteselt
//            if (!isDataCollection)
//                mKeyboardActionListener.onWritten(points, previous_points, null);

            newCoordinateList();
            clearBoard();
        }

    };


    // Constructors to initialise all the attributes
    public KeyboardCanvas(Context context)
    {
        this(context, null);
    }

    public KeyboardCanvas(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mPaint = new Paint();

        // the below methods smoothens
        // the drawings of the user
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(getResources().getColor(R.color.primary_foreground));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // 0xff=255 in decimal
        mPaint.setAlpha(0xff);

        mPaintBlur = new Paint();
        mPaintBlur.set(mPaint);
        mPaintBlur.setColor(getResources().getColor(R.color.primary_foreground));
        mPaintBlur.setStrokeWidth(5f);
        mPaintBlur.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL));

        grahyam = new Grahyam(context);

        clearBoardHandler = new Handler();

        points = new ArrayList<>();
        previous_points = new ArrayList<>();
    }

    // this method instantiate the bitmap and object
    public void init(int height, int width)
    {

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // set an initial color of the brush
        currentColor = getResources().getColor(R.color.primary_foreground);
        mCanvas.drawARGB(255, 255, 225, 255);

        // set an initial brush size
        strokeWidth = 5;
    }

    // sets the current color of stroke
    public void setColor(int color)
    {
        currentColor = color;
    }

    // sets the stroke width
    public void setStrokeWidth(int width)
    {
        strokeWidth = width;
    }

    public void undo()
    {
        // check whether the List is empty or not
        // if empty, the remove method will return an error
        if (paths.size() != 0)
        {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    // this methods returns the current bitmap
    public Bitmap save()
    {
        return mBitmap;
    }

    // this is the main method where
    // the actual drawing takes place
    @Override
    protected void onDraw(Canvas canvas)
    {
        // save the current state of the canvas before,
        // to draw the background of the canvas
        canvas.save();

        // DEFAULT color of the canvas
        int backgroundColor = getResources().getColor(R.color.primary_background);
        mCanvas.drawColor(backgroundColor);

        // now, we iterate over the list of paths
        // and draw each path on the canvas
        for (Stroke fp : paths)
        {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mCanvas.drawPath(fp.path, mPaint);
            mCanvas.drawPath(fp.path, mPaintBlur);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    // the below methods manages the touch
    // response of the user on the screen

    // firstly, we create a new Stroke
    // and add it to the paths list
    private void touchStart(float x, float y)
    {
        mPath = new Path();
        Stroke fp = new Stroke(currentColor, strokeWidth, mPath);
        paths.add(fp);

        // finally remove any curve
        // or line from the path
        mPath.reset();

        // this methods sets the starting
        // point of the line being drawn
        mPath.moveTo(x, y);

        // we save the current
        // coordinates of the finger
        mX = x;
        mY = y;
    }

    // in this method we check
    // if the move of finger on the
    // screen is greater than the
    // Tolerance we have previously defined,
    // then we call the quadTo() method which
    // actually smooths the turns we create,
    // by calculating the mean position between
    // the previous position and current position
    private void touchMove(float x, float y)
    {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
        {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            Point point = new Point((int) x, (int) y);
            points.add(point);
        }
    }

    // at the end, we call the lineTo method
    // which simply draws the line until
    // the end position
    private void touchUp()
    {
        mPath.lineTo(mX, mY);
    }

    // the onTouchEvent() method provides us with
    // the information about the type of motion
    // which has been taken place, and according
    // to that we call our desired methods
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                stopBoardClearTimer();

                // comment in Data collection
                if (!isDataCollection)
                {
                    newCoordinateList();
                }

                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                stopBoardClearTimer();
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // paths = new ArrayList<>();
                // comment in new data collection
                if (!isDataCollection)
                {
                    startBoardClearTimer();
                }
                touchUp();
                invalidate();
                try
                {
                    for (Point p : points)
                    {
                        System.out.println(p.x + " " + p.y);
                    }

                    String[] result = grahyam.runInference(points);

                    // comment in Data collection
                    if (!isDataCollection)
                    {
                        mKeyboardActionListener.onWritten(points, previous_points, result);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    invalidate();
                    return false;
                }
                break;
        }
        return true;
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener)
    {
        mKeyboardActionListener = listener;
    }

    /**
     * Returns the {@link OnKeyboardActionListener} object.
     *
     * @return the listener attached to this keyboard
     */
    protected OnKeyboardActionListener getOnKeyboardActionListener()
    {
        return mKeyboardActionListener;
    }

    public void newCoordinateList()
    {
        previous_points = points;
        points = new ArrayList<>();
    }

    public void clearBoard()
    {
        paths = new ArrayList<>();
        invalidate();
    }

    private void startBoardClearTimer()
    {
        clearBoardHandler.postDelayed(clearBoard, 1000);
    }

    private void stopBoardClearTimer()
    {
        clearBoardHandler.removeCallbacks(clearBoard);
    }

    private void resetBoardClearTimer()
    {
        stopBoardClearTimer();
        startBoardClearTimer();
    }
}
