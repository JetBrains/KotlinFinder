package com.icerockdev.shared.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import ccom.icerockdev.jetfinder.shared.R

fun Context.alert(
    messageDesc: String,
    okAction: () -> Unit,
    showCancel: Boolean = true,
    cancelAction: (() -> Unit)? = null
) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
        setMessage(messageDesc)
        setPositiveButton(R.string.ok) { dialog, _ ->
            okAction()
            dialog.dismiss()
        }
        if (showCancel) {
            if (cancelAction != null) {
                setNegativeButton(R.string.cancel) { _, _ -> cancelAction() }
            } else {
                setNegativeButton(R.string.cancel, null)
            }
        }
    }.show()
}

fun Context.alertRetry(
    messageDesc: String,
    action: (() -> Unit)?
) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
        setMessage(messageDesc)
        setCancelable(false)
        setPositiveButton(R.string.retry) { dialog, _ ->
            action?.invoke()
            dialog.dismiss()
        }
    }.show()
}

fun Context.alertYesOrNo(
    messageDesc: String,
    action: (() -> Unit)?
) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
        setMessage(messageDesc)
        setPositiveButton(R.string.yes) { dialog, _ ->
            action?.invoke()
            dialog.dismiss()
        }
        setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
    }.show()
}

fun Context.alertInputText(
    title: String,
    action: (name: String) -> Unit
) {
    val editText = EditText(this).apply {
        inputType = InputType.TYPE_CLASS_TEXT
    }
    AlertDialog.Builder(this, R.style.AlertDialogTheme).also {
        it.setTitle(title)
        it.setView(editText)
        it.setPositiveButton(R.string.retry) { dialog, _ ->
            action(editText.text.toString())
            dialog.dismiss()
        }
    }.show()
}