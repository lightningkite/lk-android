@file:JvmName("LkKotlinUtils")
@file:JvmMultifileClass

package lk.kotlin.utils.math


/**
 * Casts a [Byte] to an [Int] as if it were unsigned.
 * Created by jivie on 5/19/16.
 */
fun Byte.toIntUnsigned(): Int {
    return (this.toInt() + 0xFF) and 0xFF
}