@file:JvmName("LkAndroidExtensions")
@file:JvmMultifileClass

package lk.android.extensions



import android.graphics.Typeface
import android.text.Html
import android.widget.TextView
import java.util.*

/**
 * Extensions for TextView.
 * Created by josep on 3/3/2016.
 */


var TextView.textColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColorCompat(value))
    }
var TextView.hintTextColorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColorCompat(value))
    }
var TextView.textColorsResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setTextColor(resources.getColorStateListCompat(value))
    }
var TextView.hintTextColorsResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        setHintTextColor(resources.getColorStateListCompat(value))
    }

val fontCache: HashMap<String, Typeface> = HashMap()
fun TextView.setFont(fileName: String) {
    typeface = fontCache.getOrPut(fileName) { Typeface.createFromAsset(context.assets, fileName) }
}

fun TextView.leftDrawable(resourceId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(resources.getDrawableCompat(resourceId), null, null, null)
}

fun TextView.rightDrawable(resourceId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawableCompat(resourceId), null)
}

fun TextView.topDrawable(resourceId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawableCompat(resourceId), null, null)
}

fun TextView.bottomDrawable(resourceId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, resources.getDrawableCompat(resourceId))
}


@Suppress("DEPRECATION")
var TextView.html: String
    get() = throw IllegalAccessException()
    set(value) {
        val newVal = value
                .replace("<li>", "<p>&bull; ")
                .replace("</li>", "</p>")
                .replace("<ul>", "")
                .replace("</ul>", "")
        text = Html.fromHtml(newVal)
    }