package com.example.fopsmart

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private var mMatrix = Matrix()
    private var mode = NONE
    private var last = PointF()
    private var start = PointF()
    private var minScale = 1f
    private var maxScale = 5f
    private var m: FloatArray = FloatArray(9)
    private var viewWidth = 0
    private var viewHeight = 0
    private var saveScale = 1f
    private var origWidth = 0f
    private var origHeight = 0f
    private var oldMeasuredWidth = 0
    private var oldMeasuredHeight = 0
    private var scaleDetector: ScaleGestureDetector

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val CLICK = 3
    }

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = mMatrix

        scaleDetector = ScaleGestureDetector(context, ScaleListener())

        setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)

            val curr = PointF(event.x, event.y)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    last.set(curr)
                    start.set(last)
                    mode = DRAG
                }

                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), origWidth * saveScale)
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), origHeight * saveScale)
                        mMatrix.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last.set(curr.x, curr.y)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    mode = NONE
                    val xDiff = abs(curr.x - start.x).toInt()
                    val yDiff = abs(curr.y - start.y).toInt()
                    if (xDiff < 3 && yDiff < 3) performClick()
                }

                MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }

            imageMatrix = mMatrix
            true
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        // Скидаємо масштаб при зміні зображення
        saveScale = 1f
        mMatrix.reset()

        post {
            if (drawable != null && viewWidth > 0 && viewHeight > 0) {
                val bmWidth = drawable.intrinsicWidth
                val bmHeight = drawable.intrinsicHeight

                val scaleX = viewWidth.toFloat() / bmWidth.toFloat()
                val scaleY = viewHeight.toFloat() / bmHeight.toFloat()
                val scale = min(scaleX, scaleY)
                mMatrix.setScale(scale, scale)

                var redundantYSpace = viewHeight.toFloat() - (scale * bmHeight.toFloat())
                var redundantXSpace = viewWidth.toFloat() - (scale * bmWidth.toFloat())
                redundantYSpace /= 2.toFloat()
                redundantXSpace /= 2.toFloat()

                mMatrix.postTranslate(redundantXSpace, redundantYSpace)

                origWidth = viewWidth.toFloat() - 2 * redundantXSpace
                origHeight = viewHeight.toFloat() - 2 * redundantYSpace
                imageMatrix = mMatrix
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight || viewWidth == 0 || viewHeight == 0) {
            return
        }
        oldMeasuredHeight = viewHeight
        oldMeasuredWidth = viewWidth

        if (saveScale == 1f) {
            drawable?.let {
                val bmWidth = it.intrinsicWidth.toFloat()
                val bmHeight = it.intrinsicHeight.toFloat()

                val scaleX = viewWidth.toFloat() / bmWidth
                val scaleY = viewHeight.toFloat() / bmHeight
                val scale = min(scaleX, scaleY)
                mMatrix.setScale(scale, scale)

                var redundantYSpace = viewHeight.toFloat() - (scale * bmHeight)
                var redundantXSpace = viewWidth.toFloat() - (scale * bmWidth)
                redundantYSpace /= 2f
                redundantXSpace /= 2f

                mMatrix.postTranslate(redundantXSpace, redundantYSpace)

                origWidth = viewWidth.toFloat() - 2 * redundantXSpace
                origHeight = viewHeight.toFloat() - 2 * redundantYSpace
                imageMatrix = mMatrix
            }
        }
        fixTrans()
    }

    private fun fixTrans() {
        mMatrix.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]

        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), origWidth * saveScale)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), origHeight * saveScale)

        if (fixTransX != 0f || fixTransY != 0f) {
            mMatrix.postTranslate(fixTransX, fixTransY)
        }
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float

        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }

        if (trans < minTrans) return -trans + minTrans
        if (trans > maxTrans) return -trans + maxTrans
        return 0f
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) 0f else delta
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val origScale = saveScale
            saveScale *= mScaleFactor

            if (saveScale > maxScale) {
                saveScale = maxScale
                mScaleFactor = maxScale / origScale
            } else if (saveScale < minScale) {
                saveScale = minScale
                mScaleFactor = minScale / origScale
            }

            if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) {
                mMatrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2f, viewHeight / 2f)
            } else {
                mMatrix.postScale(mScaleFactor, mScaleFactor, detector.focusX, detector.focusY)
            }

            fixTrans()
            return true
        }
    }
}