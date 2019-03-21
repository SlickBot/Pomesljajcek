package eu.slicky.pomesljajcek.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.util.newBitmap
import eu.slicky.pomesljajcek.util.toBitmap
import eu.slicky.pomesljajcek.util.useStyledAttributes


class DstInView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class Scale { FIT_XY, SCALE }

    private var mBitmap: Bitmap? = null
    private var mColor = Color.BLACK
    private var mScale = Scale.FIT_XY

    private val bitmapSrcBounds = Rect()
    private val bitmapDstBounds = RectF()

    private var colorPaint = Paint()
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    init {
        context.useStyledAttributes(attrs, R.styleable.DstInView) {
            val drawable = getDrawable(R.styleable.DstInView_src)
            setDrawable(drawable)
            val color = getColor(R.styleable.DstInView_color, Color.BLACK)
            setColor(color)
            val scale = getInt(R.styleable.DstInView_scale, 0)
            setScale(Scale.values()[scale])
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val bitmap = mBitmap ?: return

        val screenWidth = w.toFloat()
        val screenHeight = h.toFloat()

        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        val screenRatio = screenWidth / screenHeight
        val bitmapRatio = bitmapWidth / bitmapHeight

        // width main, height factored
        fun fitWidth() {
            val factor = screenWidth / bitmapWidth
            val halfScreenHeight = screenHeight / 2
            val halfHeightFactored = (bitmapHeight * factor) / 2
            bitmapDstBounds.set(
                0f,
                halfScreenHeight - halfHeightFactored,
                screenWidth,
                halfScreenHeight + halfHeightFactored
            )
        }

        // height main, width factored
        fun fitHeight() {
            val factor = screenHeight / bitmapHeight
            val halfScreenWidth = screenWidth / 2
            val halfWidthFactored = (bitmapWidth * factor) / 2
            bitmapDstBounds.set(
                halfScreenWidth - halfWidthFactored,
                0f,
                halfScreenWidth + halfWidthFactored,
                screenHeight
            )
        }

        // width main, height factored
        fun scaleWidth() {
            val factor = screenWidth / bitmapWidth
            val factoredHeight = bitmapHeight * factor
            val offset = (factoredHeight - screenHeight) / 2
            bitmapDstBounds.set(
                0f,
                -offset,
                screenWidth,
                screenHeight + offset
            )
        }

        // height main, width factored
        fun scaleHeight() {
            val factor = screenHeight / bitmapHeight
            val factoredWidth = bitmapWidth * factor
            val offset = (factoredWidth - screenWidth) / 2
            bitmapDstBounds.set(
                -offset,
                0f,
                screenWidth + offset,
                screenHeight
            )
        }

        when (mScale) {
            Scale.FIT_XY -> if (screenRatio < bitmapRatio) fitWidth() else fitHeight()
            Scale.SCALE -> if (screenRatio < bitmapRatio) scaleHeight() else scaleWidth()
        }
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = mBitmap ?: return

        canvas.drawBitmap(
            newBitmap(bitmap.width, bitmap.height) {
                drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), colorPaint)
                drawBitmap(bitmap, 0f, 0f, bitmapPaint)
            },
            bitmapSrcBounds,
            bitmapDstBounds,
            null
        )
    }

    fun setDrawable(@DrawableRes drawableId: Int) {
        val drawable = resources.getDrawable(drawableId, null)
        setDrawable(drawable)
    }

    fun setDrawable(drawable: Drawable?) {
        mBitmap = drawable?.toBitmap()
        bitmapSrcBounds.set(0, 0, mBitmap?.width ?: 0, mBitmap?.height ?: 0)
        invalidate()
    }

    fun setColorRes(@ColorRes colorId: Int) {
        val color = ContextCompat.getColor(context, colorId)
        setColor(color)
    }

    fun setColor(@ColorInt color: Int) {
        mColor = color
        colorPaint.color = color
        invalidate()
    }

    fun setScale(scale: Scale) {
        mScale = scale
        invalidate()
    }

}