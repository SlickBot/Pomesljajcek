package eu.slicky.pomesljajcek.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleableRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference


@SuppressLint("Recycle")
inline fun Context.useStyledAttributes(
    attrs: AttributeSet?,
    @StyleableRes styleable: IntArray,
    op: TypedArray.() -> Unit
) = obtainStyledAttributes(
    attrs, styleable,
    0, 0
).apply(op).apply { recycle() }

inline fun newBitmap(
    width: Int,
    height: Int,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888,
    op: Canvas.(Bitmap) -> Unit
) = Bitmap.createBitmap(width, height, config).also { Canvas(it).op(it) }


fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }
    return Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
        Canvas(bitmap).also { canvas ->
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
        }
    }
}

//inline fun Transition.addListener(
//    crossinline onStart: (Transition) -> Unit = {},
//    crossinline onResume: (Transition) -> Unit = {},
//    crossinline onPause: (Transition) -> Unit = {},
//    crossinline onEnd: (Transition) -> Unit = {},
//    crossinline onCancel: (Transition) -> Unit = {}
//) = addListener(object: Transition.TransitionListener {
//    override fun onTransitionStart(transition: Transition) = onStart(transition)
//    override fun onTransitionResume(transition: Transition) = onResume(transition)
//    override fun onTransitionPause(transition: Transition) = onPause(transition)
//    override fun onTransitionEnd(transition: Transition) = onEnd(transition)
//    override fun onTransitionCancel(transition: Transition) = onCancel(transition)
//})
//
//inline fun TransitionSet.addListener(
//    crossinline onStart: (Transition) -> Unit = {},
//    crossinline onResume: (Transition) -> Unit = {},
//    crossinline onPause: (Transition) -> Unit = {},
//    crossinline onEnd: (Transition) -> Unit = {},
//    crossinline onCancel: (Transition) -> Unit = {}
//) = addListener(object: Transition.TransitionListener {
//    override fun onTransitionStart(transition: Transition) = onStart(transition)
//    override fun onTransitionResume(transition: Transition) = onResume(transition)
//    override fun onTransitionPause(transition: Transition) = onPause(transition)
//    override fun onTransitionEnd(transition: Transition) = onEnd(transition)
//    override fun onTransitionCancel(transition: Transition) = onCancel(transition)
//})

inline fun Animation.addListener(
    crossinline onStart: (Animation) -> Unit = {},
    crossinline onEnd: (Animation) -> Unit = {},
    crossinline onRepeat: (Animation) -> Unit = {}
) = apply {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) = onStart(animation)
        override fun onAnimationEnd(animation: Animation) = onEnd(animation)
        override fun onAnimationRepeat(animation: Animation) = onRepeat(animation)
    })
}

fun RecyclerView.addItemTouchHelper(
    onLeftSwipe: ((RecyclerView.ViewHolder) -> Unit)? = null,
    onRightSwipe: ((RecyclerView.ViewHolder) -> Unit)? = null,
    onDrag: ((RecyclerView.ViewHolder, RecyclerView.ViewHolder) -> Boolean)? = null
) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {

            var dragFlags = 0
            if (onDrag != null)
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

            var swipeFlags = 0
            if (onLeftSwipe != null)
                swipeFlags = swipeFlags or ItemTouchHelper.LEFT
            if (onRightSwipe != null)
                swipeFlags = swipeFlags or ItemTouchHelper.RIGHT

            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            first: RecyclerView.ViewHolder,
            second: RecyclerView.ViewHolder
        ): Boolean {
            return onDrag?.invoke(first, second) ?: false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.LEFT -> onLeftSwipe?.invoke(viewHolder)
                ItemTouchHelper.RIGHT -> onRightSwipe?.invoke(viewHolder)
            }
        }

    }).attachToRecyclerView(this)
}

fun Context.getNavBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun View.showAsyncKeyboard(delayMillis: Long = 325) {
    val viewRef = WeakReference(this)
    postDelayed({
        viewRef.get()?.showKeyboard()
    }, delayMillis)
}

fun View.showKeyboard() {
    isFocusableInTouchMode = true
    requestFocus()

    if (context == null || context !is Activity?)
        return

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun BooleanArray.indicesOf(function: (Boolean) -> Boolean): List<Int> {
    val list = mutableListOf<Int>()
    forEachIndexed { index, b -> if (function(b)) list += index }
    return list.toList()
}

fun Context.getVersionName(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        "?"
    }
}
