@file:JvmName("LkAndroidExtensions")
@file:JvmMultifileClass

package lk.android.extensions.files



import android.content.ContentResolver
import android.net.Uri

/**
 * Functions for ContentResolver.
 * Created by jivie on 4/12/16.
 */
fun ContentResolver.fileSize(uri: Uri): Long? {
    return openFileDescriptor(uri, "r")?.statSize
}