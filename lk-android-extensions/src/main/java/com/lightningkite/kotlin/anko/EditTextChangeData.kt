@file:JvmName("LkAndroidExtensions")
@file:JvmMultifileClass

package com.lightningkite.kotlin.anko

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

data class EditTextChangeData(
        var before: String = "",
        var insertionPoint: Int = 0,
        var replacedCount: Int = 0,
        var replacement: String = "",
        var after: String = "",
        var beforeSelectionStart: Int = 0,
        var beforeSelectionEnd: Int = 0
) {
    val replaced: String get() = before.substring(insertionPoint, replacedCount)
    val afterSelectionStart: Int
        get() = if (beforeSelectionStart < insertionPoint)
            beforeSelectionStart
        else
            beforeSelectionStart + replacement.length
    val afterSelectionEnd: Int
        get() = if (beforeSelectionEnd < insertionPoint)
            beforeSelectionEnd
        else
            beforeSelectionEnd + replacement.length
}

fun EditText.textChanger(listener: (change: EditTextChangeData) -> Pair<String, IntRange>) {
    addTextChangedListener(object : TextWatcher {

        val change = EditTextChangeData()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            change.before = s.toString()
            change.insertionPoint = start
            change.replacedCount = count
            change.beforeSelectionStart = selectionStart
            change.beforeSelectionEnd = selectionEnd
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            change.replacement = s.substring(start, start + count)
            change.after = s.toString()
        }

        var ignore: Boolean = false
        override fun afterTextChanged(s: Editable) {
            if (ignore) {
                ignore = false
                return
            }
            ignore = true
            val (textResult, rangeResult) = listener(change)
            setText(textResult)
            setSelection(rangeResult.start, rangeResult.endInclusive)
            ignore = false
        }

    })
}

fun EditText.textListener(listener: (change: EditTextChangeData) -> Unit) {
    addTextChangedListener(object : TextWatcher {

        val change = EditTextChangeData()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            change.before = s.toString()
            change.insertionPoint = start
            change.replacedCount = count
            change.beforeSelectionStart = selectionStart
            change.beforeSelectionEnd = selectionEnd
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            change.replacement = s.substring(start, start + count)
            change.after = s.toString()
        }

        override fun afterTextChanged(s: Editable) {
            listener(change)
        }

    })
}