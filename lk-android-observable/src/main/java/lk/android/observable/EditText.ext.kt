@file:JvmName("LkAndroidObservable")
@file:JvmMultifileClass

package lk.android.observable



import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import lk.android.lifecycle.lifecycle
import lk.kotlin.observable.property.MutableObservableProperty
import lk.kotlin.observable.property.lifecycle.bind
import java.text.NumberFormat
import java.text.ParseException

abstract class TextWatcherAdapter : TextWatcher {
    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindString(bond: MutableObservableProperty<String>) {
    setText(bond.value)
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bond.value != s) {
                bond.value = (s.toString())
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != text.toString()) {
            this.setText(bond.value)
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the text here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableString(bond: MutableObservableProperty<String?>) {
    setText(bond.value)
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bond.value != s) {
                bond.value = (s.toString())
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != text.toString()) {
            this.setText(bond.value)
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindInt(bond: MutableObservableProperty<Int>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Int? = null
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = null

            try {
                value = format.parse(s.toString()).toInt()
            } catch (e: ParseException) {
                //do nothing.
            }

            try {
                value = s.toString().toInt()
            } catch (e: NumberFormatException) {
                //do nothing.
            }

            if (value == null) {
                setTextColor(0xFFFF0000.toInt())
            } else {
                setTextColor(originalTextColor)
                if (bond.value != value) {
                    bond.value = (value!!)
                }
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the integer here will be updated.
 */
inline fun EditText.bindNullableInt(bond: MutableObservableProperty<Int?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    var value: Int? = null
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = null
            if (!s.isNullOrBlank()) {
                try {
                    value = format.parse(s.toString()).toInt()
                } catch (e: ParseException) {
                    //do nothing.
                }

                try {
                    value = s.toString().toInt()
                } catch (e: NumberFormatException) {
                    //do nothing.
                }

            }

            if (bond.value != value) {
                bond.value = (value)
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableFloat(bond: MutableObservableProperty<Float?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    var value: Float? = null
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = null
            if (!s.isNullOrBlank()) {

                try {
                    value = format.parse(s.toString()).toFloat()
                } catch (e: ParseException) {
                    //do nothing.
                }

                try {
                    value = s.toString().toFloat()
                } catch (e: NumberFormatException) {
                    //do nothing.
                }
            }

            println("PRE value $value obs ${bond.value}")
            if (bond.value != value) {
                bond.value = (value)
            }
            println("PST value $value obs ${bond.value}")
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindNullableDouble(bond: MutableObservableProperty<Double?>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value: Double? = null
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = null

            if (!s.isNullOrBlank()) {
                try {
                    value = format.parse(s.toString()).toDouble()
                } catch (e: ParseException) {
                    //do nothing.
                }

                try {
                    value = s.toString().toDouble()
                } catch (e: NumberFormatException) {
                    //do nothing.
                }
            }

            setTextColor(originalTextColor)
            if (bond.value != value) {
                bond.value = (value)
            }
        }
    })

    lifecycle.bind(bond) {
        if (bond.value != value) {
            if (bond.value == null) this.setText("")
            else this.setText(format.format(bond.value!!))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindFloat(bond: MutableObservableProperty<Float>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Float.NaN
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = Float.NaN

            try {
                value = format.parse(s.toString()).toFloat()
            } catch (e: ParseException) {
                //do nothing.
            }

            try {
                value = s.toString().toFloat()
            } catch (e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                setTextColor(0xFFFF0000.toInt())
            } else {
                setTextColor(originalTextColor)
                if (bond.value != value) {
                    bond.value = (value)
                }
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}

/**
 * Binds this [EditText] two way to the bond.
 * When the user edits this, the value of the bond will change.
 * When the value of the bond changes, the number here will be updated.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun EditText.bindDouble(bond: MutableObservableProperty<Double>, format: NumberFormat = NumberFormat.getNumberInstance()) {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    val originalTextColor = this.textColors.defaultColor
    var value = Double.NaN
    addTextChangedListener(object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            value = Double.NaN

            try {
                value = format.parse(s.toString()).toDouble()
            } catch (e: ParseException) {
                //do nothing.
            }

            try {
                value = s.toString().toDouble()
            } catch (e: NumberFormatException) {
                //do nothing.
            }

            if (value.isNaN()) {
                setTextColor(0xFFFF0000.toInt())
            } else {
                setTextColor(originalTextColor)
                if (bond.value != value) {
                    bond.value = (value)
                }
            }
        }
    })
    lifecycle.bind(bond) {
        if (bond.value != value) {
            this.setText(format.format(bond.value))
        }
    }
}
