package eu.slicky.pomesljajcek.util.transition

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.SharedElementCallback

class TextEnterSharedElementCallback : SharedElementCallback() {

    companion object {
        private const val TAG = "EnterSharedElementCall"
    }

    override fun onSharedElementEnd(
        sharedElementNames: List<String>?,
        sharedElements: List<View>?,
        sharedElementSnapshots: List<View>?
    ) {
        Log.i(TAG, "=== onSharedElementEnd(List<String>, List<View>, List<View>)")
        val textView = sharedElements!![0] as TextView

        // Record the TextView's old width/height.
        val oldWidth = textView.measuredWidth
        val oldHeight = textView.measuredHeight

        // Re-measure the TextView (since the yearField size has changed).
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthSpec, heightSpec)

        // Record the TextView's new width/height.
        val newWidth = textView.measuredWidth
        val newHeight = textView.measuredHeight

        // Layout the TextView in the center of its container, accounting for its new width/height.
        val widthDiff = newWidth - oldWidth
        val heightDiff = newHeight - oldHeight

        textView.layout(
            textView.left - widthDiff / 2, textView.top - heightDiff / 2,
            textView.right + widthDiff / 2, textView.bottom + heightDiff / 2
        )

    }

}
