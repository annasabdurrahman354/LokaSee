package com.bangkit.lokasee.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.bangkit.lokasee.data.Kabupaten
import com.bangkit.lokasee.data.Kecamatan
import com.bangkit.lokasee.data.Provinsi
import com.bangkit.lokasee.data.User
import com.bangkit.lokasee.data.retrofit.ApiConfig.HOST
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val FILENAME_FORMAT = "dd-MMM-yyyy"
const val AVATAR_URL_BASE = "https://ui-avatars.com/api/?bold=true&size=128&background=random&name="


val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)
    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    return myFile
}
fun getAvatarUrl(user: User): String {
    return if (user.avatarUrl == "") StringBuilder(AVATAR_URL_BASE).append(user.name.replace(' ', '+')).toString()
    else "${HOST}/${user.avatarUrl}"
}

fun getStorageUrl(url: String): String {
    return "${HOST}/${url}"
}

fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString.toRequestBody(MultipartBody.FORM)
}

fun buildImageBodyPart(bitmap: Bitmap, context: Context):  MultipartBody.Part {
    val leftImageFile = convertBitmapToFile(bitmap, context)
    val reqFile = leftImageFile.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(leftImageFile.nameWithoutExtension, leftImageFile.name, reqFile)
}

fun convertBitmapToFile(bitmap: Bitmap, context: Context): File {
    //create a file to write bitmap data
    val file = createCustomTempFile(context)

    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitMapData = bos.toByteArray()

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos?.write(bitMapData)
        fos?.flush()
        fos?.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

fun <K, V> Map<out K?, V?>.filterNotNull(): Map<K, V> = this.mapNotNull {
    it.key?.let { key ->
        it.value?.let { value ->
            key to value
        }
    }
}.toMap()

fun <K, V> Map<out K?, V?>.isMapEmpty(): Boolean {
    for (v in this.values) {
        if (v != null) {
            return false
        }
    }
    return true
}

fun getProvinsi( param: Any, list : List<Provinsi>) : Provinsi {
    val temp = list
    return if (param is String)
        temp.filter {
            it.title == param
        }.first()

    else temp.filter {
        it.id == param
    }.first()
}

fun getKabupaten( param: Any, list : List<Kabupaten>) : Kabupaten {
    val temp = list
    return if (param is String)
        temp.filter {
            it.title == param
        }.first()

    else temp.filter {
        it.id == param
    }.first()
}

fun getKecamatan( param: Any, list : List<Kecamatan>) : Kecamatan {
    val temp = list
    return if (param is String)
        temp.filter {
            it.title == param
        }.first()

    else temp.filter {
        it.id == param
    }.first()
}

fun String.capitalizeWords(): String = split(" ").map { it.toLowerCase().capitalize() }.joinToString(" ")