@file:JvmName("LkKotlinEverythingDeprecated")
@file:JvmMultifileClass

package com.lightningkite.kotlin

import android.text.TextUtils
import android.widget.ImageButton
import android.widget.TextView
import com.lightningkite.kotlin.anko.selectableItemBackgroundBorderlessResource
import com.lightningkite.kotlin.anko.textColorResource
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.leftPadding

/**
 *
 * Created by jivie on 2/15/16.
 *
 */

fun TextView.materialStyleTertiary(dark: Boolean) {
    textColorResource = if (dark) android.R.color.tertiary_text_dark else android.R.color.tertiary_text_light
    ellipsize = TextUtils.TruncateAt.END
}

fun TextView.materialStyleSecondary(dark: Boolean) {
    textColorResource = if (dark) android.R.color.secondary_text_dark else android.R.color.secondary_text_light
    ellipsize = TextUtils.TruncateAt.END
}

fun TextView.materialStylePrimary(dark: Boolean) {
    textColorResource = if (dark) android.R.color.primary_text_dark else android.R.color.primary_text_light
    ellipsize = TextUtils.TruncateAt.END
}

fun ImageButton.materialStyleAction() {

    leftPadding = dip(16)
    backgroundResource = selectableItemBackgroundBorderlessResource
}