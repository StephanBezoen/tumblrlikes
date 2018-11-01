package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 * Created on 01/11/2018.
 */

class InteractiveImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {
    enum class Gesture {
        TAP, DOUBLE_TAP, SIDE_SWIPE, LONG_PRESS
    }

    val SWIPE_DETECTION_DIST_THRESHOLD: Int = 30
    val SWIPE_DETECTION_ANG_THRESHOLD = 45

    private var gestureDetector: GestureDetectorCompat
    private var scaleDetector: ScaleGestureDetector
    private var gestureListener: ((InteractiveImageView.Gesture) -> Unit)? = null
    private var density: Float = 0f
    private var startScale: Float = 1f
    private var scale: Float = 1f
    private var lastTouchX: Float = 1f
    private var lastTouchY: Float = 1f
    private var translateX: Float = 0f
    private var translateY: Float = 0f
    private var activePointerId: Int = 0
    private var hasScaled: Boolean = false

    private val isScaled: Boolean
        get() = Math.abs(scale - 1.0f) > .00001

    init {
        density = resources.displayMetrics.density

        gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                gestureListener?.invoke(Gesture.DOUBLE_TAP)

                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                gestureListener?.invoke(Gesture.TAP)

                return false
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (isScaled) return false

                val point = PointF(e2.x - e1.x, e2.y - e1.y)
                if (point.length() / density < SWIPE_DETECTION_DIST_THRESHOLD) return false

                val angle = (180 * Math.atan2(velocityY.toDouble(), velocityX.toDouble()) / Math.PI).toInt()
                if ((angle < SWIPE_DETECTION_ANG_THRESHOLD && angle > -SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle > (180 - SWIPE_DETECTION_ANG_THRESHOLD)
                        || angle < -(180 - SWIPE_DETECTION_ANG_THRESHOLD)) {
                    gestureListener?.invoke(Gesture.SIDE_SWIPE)
                }

                return false
            }

            override fun onLongPress(e: MotionEvent?) {
                if (isScaled) return

                gestureListener?.invoke(Gesture.LONG_PRESS)
            }
        })

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                startScale = scale

                return true
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                setScale(Math.min(Math.max(1.0f, startScale * detector.scaleFactor), 5.0f))

                hasScaled = true

                return false
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        canvas.translate(translateX + 0.5f * (1.0f - scale) * width, translateY + 0.5f * (1.0f - scale) * height)

        canvas.scale(scale, scale)

        super.onDraw(canvas)

        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        hasScaled = false

        gestureDetector.onTouchEvent(event)
        scaleDetector.onTouchEvent(event)

        if (!hasScaled && isScaled) {
            checkDrag(event)
        }

        return true
    }

    private fun checkDrag(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> storeDownEvent(event)
            MotionEvent.ACTION_MOVE -> onDrag(event)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> activePointerId = -1
            MotionEvent.ACTION_POINTER_UP -> checkDragEnd(event)
        }
    }

    private fun storeDownEvent(event: MotionEvent) {
        val pointerIndex = event.actionIndex

        lastTouchX = event.getX(pointerIndex)
        lastTouchY = event.getY(pointerIndex)

        activePointerId = event.getPointerId(pointerIndex)
    }

    private fun onDrag(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(activePointerId)
        if (pointerIndex == -1) return

        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)

        translateX += (x - lastTouchX)
        translateY += (y - lastTouchY)

        invalidate()

        lastTouchX = x
        lastTouchY = y
    }

    private fun checkDragEnd(event: MotionEvent) {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        if (pointerId == activePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0

            lastTouchX = event.getX(newPointerIndex)
            lastTouchY = event.getY(newPointerIndex)

            activePointerId = event.getPointerId(newPointerIndex)
        }
    }

    fun resetScale() {
        scale = 1f
        translateX = 0f
        translateY = 0f
    }

    private fun setScale(scale: Float) {
        this.scale = scale

        invalidate()
    }

    fun setGestureListener(listener:(InteractiveImageView.Gesture) -> Unit) {
        gestureListener = listener
    }

    fun onDestroyView() {
        gestureListener = null
    }
}
