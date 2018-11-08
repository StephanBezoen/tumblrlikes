package nl.acidcats.tumblrlikes.ui.screens.photo_screen.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GestureDetectorCompat
import nl.acidcats.tumblrlikes.util.clamp

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
    private var screenWidth: Float = 0f
    private var screenHeight: Float = 0f
    private var screenAspectRatio: Float = 0f
    private var activePointerId: Int = 0
    private var hasScaled: Boolean = false
    private var maxTranslateX: Float = 0f
    private var maxTranslateY: Float = 0f

    val isScaled: Boolean
        get() = Math.abs(scale - 1.0f) > .00001
    var screenSize: Point = Point()
        set(value) {
            screenWidth = value.x.toFloat()
            screenHeight = value.y.toFloat()
            screenAspectRatio = screenWidth / screenHeight
        }

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

        val dx = (translateX + 0.5f * (1.0f - scale) * width)
        val dy = translateY + 0.5f * (1.0f - scale) * height

        canvas.translate(dx, dy)

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

        clampTranslation()

        invalidate()

        lastTouchX = x
        lastTouchY = y
    }

    private fun clampTranslation() {
        translateX = translateX.clamp(-maxTranslateX, maxTranslateX)
        translateY = translateY.clamp(-maxTranslateY, maxTranslateY)
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

    fun scaleToView() {
        if (isScaled) return

        val imWidth = drawable.intrinsicWidth.toFloat()
        val imHeight = drawable.intrinsicHeight.toFloat()

        val imageAspectRatio = imWidth / imHeight
        if (imageAspectRatio > screenAspectRatio) {
            // wide image, scale height up to screen height
            setScale(imageAspectRatio / screenAspectRatio)
        } else {
            // high image, scale width up to screen width
            setScale(screenAspectRatio / imageAspectRatio)
        }
    }

    fun resetScale() {
        scale = 1f
        translateX = 0f
        translateY = 0f
        maxTranslateX = 0f
        maxTranslateY = 0f

        invalidate()
    }

    private fun setScale(scale: Float) {
        this.scale = scale

        val imageWidth = drawable.intrinsicWidth.toFloat()
        val imageHeight = drawable.intrinsicHeight.toFloat()

        val imageAspectRatio = imageWidth / imageHeight
        if (imageAspectRatio > screenAspectRatio) {
            maxTranslateX = Math.max(0f, (scale - 1) * screenWidth / 2)
            maxTranslateY = Math.max(0f, ((scale - imageAspectRatio / screenAspectRatio) * (screenWidth / imageAspectRatio)) / 2)
        } else {
            maxTranslateX = Math.max(0f, ((scale - screenAspectRatio / imageAspectRatio) * (screenHeight * imageAspectRatio)) / 2)
            maxTranslateY = Math.max(0f, (scale - 1) * screenHeight / 2)
        }

        clampTranslation()

        invalidate()
    }

    fun setGestureListener(listener: (InteractiveImageView.Gesture) -> Unit) {
        gestureListener = listener
    }

    fun onDestroyView() {
        gestureListener = null
    }
}
