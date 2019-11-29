package com.icerockdev.jetfinder.feature.mainMap.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import com.icerockdev.jetfinder.feature.mainMap.R

fun Context.alert(
    title: String? = null,
    message: String? = null,
    positiveAction: Pair<Int, () -> Unit>? = null,
    negativeAction: Pair<Int, () -> Unit>? = null,
    cancelable: Boolean = true,
    closable: Boolean = true,
    inputAction: ((String) -> Unit)? = null
) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
        title?.let { setTitle(it) }
        message?.let { setMessage(it) }
        positiveAction?.let {
            setPositiveButton(it.first) { dialog, _ ->
                it.second()
                if (closable)
                    dialog.dismiss()
            }
        }
        negativeAction?.let {
            setNegativeButton(it.first) { dialog, _ ->
                it.second()
                if (closable)
                    dialog.dismiss()
            }
        }
        setCancelable(cancelable)
        inputAction?.let {
            val editText = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_TEXT
            }
            setView(editText)
            setPositiveButton(R.string.retry) { dialog, _ ->
                it(editText.text.toString())
                dialog.dismiss()
            }
        }
    }.show()
}
