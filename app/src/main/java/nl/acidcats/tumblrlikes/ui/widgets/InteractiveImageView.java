package nl.acidcats.tumblrlikes.ui.widgets;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by stephan on 24/04/2017.
 */

public class InteractiveImageView extends AppCompatImageView {
    private static final String TAG = InteractiveImageView.class.getSimpleName();

    public enum Gesture {
        TAP, DOUBLE_TAP, SIDE_SWIPE, LONG_PRESS
    }

    // min dpi to swipe before detection
    private static final int SWIPE_DETECTION_DIST_THRESHOLD = 30;
    private static final int SWIPE_DETECTION_ANG_THRESHOLD = 45;

    private GestureDetectorCompat _detector;
    private float _density;
    private GestureListener _gestureListener;

    public InteractiveImageView(Context context) {
        super(context);

        init();
    }

    public InteractiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public InteractiveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        _density = getResources().getDisplayMetrics().density;

        _detector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                onGesture(Gesture.DOUBLE_TAP);

                return false;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onGesture(Gesture.TAP);

                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                PointF point = new PointF(e2.getX() - e1.getX(), e2.getY() - e1.getY());
                if (point.length() / _density < SWIPE_DETECTION_DIST_THRESHOLD) return false;

                int angle = (int)(180 * Math.atan2(velocityY, velocityX) / Math.PI);

                if ((angle < SWIPE_DETECTION_ANG_THRESHOLD && angle > -SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle > (180 - SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle < -(180 - SWIPE_DETECTION_ANG_THRESHOLD)) {
                    onGesture(Gesture.SIDE_SWIPE);
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                onGesture(Gesture.LONG_PRESS);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        _detector.onTouchEvent(event);

        return true;
    }

    public interface GestureListener {
        void onGesture(Gesture gesture);
    }

    public void setGestureListener(GestureListener listener) {
        _gestureListener = listener;
    }

    private void onGesture(Gesture gesture) {
        if (_gestureListener != null) {
            _gestureListener.onGesture(gesture);
        }
    }
}
