package com.icerockdev.jetfinder.feature.mainMap.utils

import android.view.View
import androidx.databinding.BindingAdapter

object ViewBindingAdapters {
    @JvmStatic
    @BindingAdapter("invisibleOrGone")
    fun setInvisibleOrGone(view: View, value: Boolean) {
        view.visibility = if (value) View.INVISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun setVisibleOrGone(view: View, value: Boolean) {
        view.visibility = if (value) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("visibleOrInvisible")
    fun setVisibleOrInvisible(view: View, value: Boolean) {
        view.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }
}