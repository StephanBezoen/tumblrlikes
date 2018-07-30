package nl.acidcats.tumblrlikes.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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

    private GestureDetectorCompat _gestureDetector;
    private ScaleGestureDetector _scaleDetector;
    private float _density;
    private GestureListener _gestureListener;
    private float _startScale;
    private float _scale;
    private boolean _hasScaled;
    private float _lastTouchX;
    private float _lastTouchY;
    private int _activePointerId;
    private float _translateX;
    private float _translateY;

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

    public void resetScale() {
        setScale(1.0f);

        _translateX = 0;
        _translateY = 0;
    }

    private boolean isScaled() {
        return Math.abs(_scale - 1.0f) > .00001;
    }

    private void setScale(float scale) {
        _scale = scale;

        invalidate();
    }

    private void init() {
        _density = getResources().getDisplayMetrics().density;

        _gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                onGesture(Gesture.DOUBLE_TAP);

                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                onGesture(Gesture.TAP);

                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (isScaled()) return false;

                PointF point = new PointF(e2.getX() - e1.getX(), e2.getY() - e1.getY());
                if (point.length() / _density < SWIPE_DETECTION_DIST_THRESHOLD) return false;

                int angle = (int) (180 * Math.atan2(velocityY, velocityX) / Math.PI);

                if ((angle < SWIPE_DETECTION_ANG_THRESHOLD && angle > -SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle > (180 - SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle < -(180 - SWIPE_DETECTION_ANG_THRESHOLD)) {
                    onGesture(Gesture.SIDE_SWIPE);
                }

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (isScaled()) return;

                onGesture(Gesture.LONG_PRESS);
            }
        });

        _scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                _startScale = _scale;

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                setScale(Math.min(Math.max(1.0f, _startScale * detector.getScaleFactor()), 5.0f));

                _hasScaled = true;

                return false;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(_translateX + 0.5f * (1.0f - _scale) * getWidth(), _translateY + 0.5f * (1.0f - _scale) * getHeight());

        canvas.scale(_scale, _scale);

        super.onDraw(canvas);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        _hasScaled = false;

        _gestureDetector.onTouchEvent(event);
        _scaleDetector.onTouchEvent(event);

        if (!_hasScaled && isScaled()) {
            checkDrag(event);
        }

        return true;
    }

    private void checkDrag(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                storeDownEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onDrag(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                _activePointerId = -1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                checkDragEnd(event);
                break;
        }
    }

    private void storeDownEvent(MotionEvent event) {
        final int pointerIndex = event.getActionIndex();

        _lastTouchX = event.getX(pointerIndex);
        _lastTouchY = event.getY(pointerIndex);

        _activePointerId = event.getPointerId(0);
    }

    private void onDrag(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(_activePointerId);
        if (pointerIndex == -1) return;

        final float x = event.getX(pointerIndex);
        final float y = event.getY(pointerIndex);

        _translateX += (x - _lastTouchX);
        _translateY += (y - _lastTouchY);

        invalidate();

        _lastTouchX = x;
        _lastTouchY = y;
    }

    private void checkDragEnd(MotionEvent event) {
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);

        if (pointerId == _activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            _lastTouchX = event.getX(newPointerIndex);
            _lastTouchY = event.getY(newPointerIndex);
            _activePointerId = event.getPointerId(newPointerIndex);
        }
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

    public void onDestroyView() {
        _gestureListener = null;
    }
}
