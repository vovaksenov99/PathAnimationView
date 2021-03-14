package com.aviasales.test.core.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.minus
import androidx.core.graphics.plus
import androidx.core.view.isVisible
import com.aviasales.test.R
import com.aviasales.test.core.utils.angleBetweenTwoVectors
import com.aviasales.test.core.utils.div
import com.aviasales.test.core.utils.px
import com.aviasales.test.core.utils.times


class LoadingOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {

        private const val SUPER_STATE_KEY = "SUPER_STATE_KEY"
        private const val PLANE_POS_KEY = "PLANE_POS_KEY"
        private const val PLANE_ANGLE_KEY = "PLANE_ANGLE_KEY"
        private const val ANIMATION_TIME_KEY = "ANIMATION_TIME_KEY"
        private const val IS_ANIMATION_FINISHED_KEY = "IS_ANIMATION_FINISHED_KEY"

        private const val ANIMATION_DURATION_MS = 6000L
        private const val CURVE_FLEX_STRENGTH = 1.3
        private const val CURVE_FLEX_DIRECTION = 2.0
        private const val LABEL_OPACITY = 160
        private val LABEL_HEIGHT_DP = 20.px
        private val LABEL_WIDTH_DP = 40.px
        private val LABEL_ROUND_DP = 12.px
        private val LABEL_PADDING_DP = 10.px

    }

    private val animatedIcon = ImageView(context).apply {
        isVisible = false
    }
    private var animatedIconSize = 0
    private var animatedIconRotation = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.PlaneLoadingView, defStyleAttr, 0) {
            if (hasValue(R.styleable.PlaneLoadingView_icon)) {
                animatedIcon.setImageResource(getResourceId(R.styleable.PlaneLoadingView_icon, 0))
            }
            if (hasValue(R.styleable.PlaneLoadingView_iconSize)) {
                animatedIconSize = getDimensionPixelSize(R.styleable.PlaneLoadingView_iconSize, 0)
                animatedIcon.layoutParams = LayoutParams(animatedIconSize, animatedIconSize)
            }
        }
        setWillNotDraw(false)
        addView(animatedIcon)
    }

    private var curveBasePoint1 = Point()
    private var curveBasePoint2 = Point()
    private var curveBasePoint3 = Point()
    private var curveBasePoint4 = Point()

    private var pointFromName = ""
    private var pointToName = ""
    private var textSizePointFrom: Float = 0F
    private var textSizePointTo: Float = 0F

    private var currentIconPosition: Point? = null

    private var animationStartTime: Long = 0

    private var isAnimationFinished: Boolean = false

    private var curvePath = calculatePath()

    private var curveStyle = Paint()

    private val valueAnimator by lazy {
        ValueAnimator.ofPropertyValuesHolder().apply {
            duration = ANIMATION_DURATION_MS - animationStartTime
            addUpdateListener { animator ->
                val t = (animator.currentPlayTime.toDouble()
                        + animationStartTime) / ANIMATION_DURATION_MS
                animatedIconRotation = (-angleBetweenTwoVectors(
                    getCurveTangentVector(
                        curveBasePoint1,
                        curveBasePoint2,
                        curveBasePoint3,
                        curveBasePoint4,
                        t
                    ),
                    Point(100, 0) //BaseCoordinateVector
                )).toFloat()
                currentIconPosition?.let { pos -> setIconPosition(pos) }
                animatedIcon.rotation = animatedIconRotation
            }
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    isAnimationFinished = true
                }
            })

        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? Bundle)?.let { state ->
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE_KEY))
            currentIconPosition = state.getParcelable(PLANE_POS_KEY)
            animatedIconRotation = state.getFloat(PLANE_ANGLE_KEY)
            animationStartTime = state.getLong(ANIMATION_TIME_KEY)
            isAnimationFinished = state.getBoolean(IS_ANIMATION_FINISHED_KEY)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelable(SUPER_STATE_KEY, super.onSaveInstanceState())
            putParcelable(PLANE_POS_KEY, currentIconPosition)
            putFloat(PLANE_ANGLE_KEY, animatedIcon.rotation)
            putLong(ANIMATION_TIME_KEY, valueAnimator.currentPlayTime + animationStartTime)
            putBoolean(IS_ANIMATION_FINISHED_KEY, isAnimationFinished)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(curvePath, curveStyle)
        drawAirportLabel(
            canvas,
            pointFromName,
            textSizePointFrom,
            curveBasePoint1
        )
        drawAirportLabel(
            canvas,
            pointToName,
            textSizePointTo,
            curveBasePoint4
        )
    }

    private fun calculateTextSize(text: String): Float {
        val startTextSize = 128f
        val paint = Paint()
        paint.textSize = startTextSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return startTextSize * (LABEL_WIDTH_DP - LABEL_PADDING_DP * 2) / bounds.width()
    }

    private fun drawAirportLabel(canvas: Canvas, text: String, size: Float, position: Point) {
        if (text.isEmpty()) return
        val backgroundPaint = Paint().apply {
            color = context.getColor(R.color.label_color)
            style = Paint.Style.FILL
            isAntiAlias = true
            alpha = LABEL_OPACITY
        }
        val borderPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = 5f
            alpha = LABEL_OPACITY
        }
        val left = position.x - LABEL_WIDTH_DP / 2f
        val bottom = position.y + LABEL_HEIGHT_DP / 2f
        val right = position.x + LABEL_WIDTH_DP / 2f
        val top = position.y + -LABEL_HEIGHT_DP / 2f
        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            LABEL_ROUND_DP.toFloat(),
            LABEL_ROUND_DP.toFloat(),
            backgroundPaint
        )
        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            LABEL_ROUND_DP.toFloat(),
            LABEL_ROUND_DP.toFloat(),
            borderPaint
        )
        drawAirportLabelText(canvas, text, size, position)
    }

    private fun drawAirportLabelText(canvas: Canvas, text: String, size: Float, position: Point) {
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            textSize = size
            color = Color.WHITE
        }
        val width = textPaint.measureText(text).toInt()
        val staticLayout = StaticLayout.Builder.obtain(
            text,
            0,
            text.length,
            textPaint,
            width
        )
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()

        canvas.save()
        canvas.translate(
            position.x - width / 2f,
            position.y - size / 2f - 2f
        )
        staticLayout.draw(canvas)
        canvas.restore()
    }

    private fun calculatePath(): Path {
        val centerPoint = (curveBasePoint1 + curveBasePoint4) / CURVE_FLEX_DIRECTION
        val dirVector = (curveBasePoint1 + centerPoint) / CURVE_FLEX_DIRECTION
        val dirVector2 = (curveBasePoint4 + centerPoint) / CURVE_FLEX_DIRECTION

        val a1V = (curveBasePoint1 - dirVector)
        val b1V = (curveBasePoint4 - dirVector2)
        curveBasePoint2 = (Point(a1V.y, -a1V.x) * CURVE_FLEX_STRENGTH + dirVector)
        curveBasePoint3 = (Point(b1V.y, -b1V.x) * CURVE_FLEX_STRENGTH + dirVector2)

        return Path().apply {
            moveTo(curveBasePoint1.x.toFloat(), curveBasePoint1.y.toFloat())
            cubicTo(
                curveBasePoint2.x.toFloat(),
                curveBasePoint2.y.toFloat(),
                curveBasePoint3.x.toFloat(),
                curveBasePoint3.y.toFloat(),
                curveBasePoint4.x.toFloat(),
                curveBasePoint4.y.toFloat()
            )
        }
    }

    private fun continueAnimation() {
        curvePath = calculatePath()
        invalidate()
        animatedIcon.isVisible = true
        if (!isAnimationFinished) {
            currentIconPosition?.let { pos -> setIconPosition(pos) }
            valueAnimator.start()
        } else {
            setIconPosition(curveBasePoint4)
            animatedIcon.rotation = animatedIconRotation
        }
    }

    private fun setIconPosition(position: Point) {
        animatedIcon.x = position.x - animatedIconSize / 2f
        animatedIcon.y = position.y - animatedIconSize / 2f
    }

    private fun getCurveTangentVector(
        a1: Point,
        a2: Point,
        a3: Point,
        a4: Point,
        t: Double
    ): Point {
        val v1 = (a2 - a1) * t + a1
        val v2 = (a3 - a2) * t + a2
        val v3 = (a4 - a3) * t + a3

        val v4 = (v2 - v1) * t + v1
        val v5 = (v3 - v2) * t + v2

        currentIconPosition = (v5 - v4) * t + v4

        return v5 - v4
    }

    fun setStartPoints(pointFrom: Point, pointTo: Point) {
        this.curveBasePoint1 = pointFrom
        this.curveBasePoint4 = pointTo
        currentIconPosition = currentIconPosition ?: this.curveBasePoint1
        continueAnimation()
    }

    fun setPointsNames(pointFromName: String, pointToName: String) {
        this.pointFromName = pointFromName
        this.pointToName = pointToName

        textSizePointFrom = calculateTextSize(pointFromName)
        textSizePointTo = calculateTextSize(pointToName)
    }

    fun setCurveStyle(curveStyle: Paint) {
        this.curveStyle = curveStyle
    }

}