package eu.slicky.pomesljajcek.ui.activity

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import eu.slicky.pomesljajcek.R
import eu.slicky.pomesljajcek.util.indicesOf


class Dialogs(val context: Context) {

    fun showSimpleOk(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onOk: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.ok) { _, _ -> onOk() }
        setOnCancelListener { onCancel() }
    }

    fun showSimpleCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showSimpleOkCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onOk: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.ok) { _, _ -> onOk() }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showSimpleYesCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onYes: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.yes) { _, _ -> onYes() }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showSimpleYesNo(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onYes: () -> Unit = {},
        onNo: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.yes) { _, _ -> onYes() }
        setPositiveButton(R.string.no) { _, _ -> onNo() }
        setOnCancelListener { onCancel() }
    }

    fun showSimpleYesNoCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        message: CharSequence? = null,
        @StringRes messageId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onYes: () -> Unit = {},
        onNo: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setMessage(message, messageId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.yes) { _, _ -> onYes() }
        setPositiveButton(R.string.no) { _, _ -> onNo() }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showSaveCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        view: View? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onSave: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setView(view)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.save) { _, _ -> onSave() }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showEditCancel(
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        view: View? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onSave: () -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setView(view)
        setIcon(icon, iconId, iconTint, iconTintId)
        setCancelable(cancelable)
        setPositiveButton(R.string.edit) { _, _ -> onSave() }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }

    fun showListOkCancel(
        textList: Array<String>,
        checkedList: BooleanArray,
        title: CharSequence? = null,
        @StringRes titleId: Int? = null,
        icon: Drawable? = null,
        @DrawableRes iconId: Int? = null,
        @ColorInt iconTint: Int? = null,
        @ColorRes iconTintId: Int? = null,
        cancelable: Boolean = true,
        onYes: (List<Int>) -> Unit = {},
        onCancel: () -> Unit = {}

    ) = showAlertDialog {
        setTitle(title, titleId)
        setIcon(icon, iconId, iconTint, iconTintId)
        setMultiChoiceItems(textList, checkedList) { _, _, _ ->  }
        setCancelable(cancelable)
        setPositiveButton(R.string.yes) { _, _ -> onYes(checkedList.indicesOf { it }) }
        setNegativeButton(R.string.cancel) { _, _ -> onCancel() }
        setOnCancelListener { onCancel() }
    }



    fun showAlertDialog(op: AlertDialog.Builder.() -> Unit): AlertDialog {
        return AlertDialog.Builder(context, R.style.AlertDialogTheme)
            .apply(op)
            .create()
            .also { it.show() }
    }

    fun AlertDialog.Builder.setTitle(title: CharSequence?, titleId: Int?) {
        if (title != null)
            setTitle(title)
        else if (titleId != null)
            setTitle(titleId)
    }

    fun AlertDialog.Builder.setMessage(message: CharSequence?, messageId: Int?) {
        if (message != null)
            setMessage(message)
        else if (messageId != null)
            setMessage(messageId)
    }

    fun AlertDialog.Builder.setIcon(icon: Drawable?, iconId: Int?, iconTint: Int?, iconTintId: Int?) {
        val drawable = icon ?: iconId?.let { ContextCompat.getDrawable(context, it) } ?: return
        val color: Int? = iconTint ?: iconTintId?.let { ContextCompat.getColor(context, it) }

        if (color != null) {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        setIcon(drawable)
    }

}
