package kg.flashcards.android

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

const val tmp = "tmp-finder"

class FileFinder(private val context: Context) {
    private val compressor = Compressor(context)
    private val maxWidth = 500
    private val maxHeight = 500
    private val quality = 70

    fun getTmpFileUri(uri: Uri): File? {
        try {
            var fullFilePath: File? = null
            context.contentResolver.openInputStream(uri)?.use { cursor ->
                val byteArray = ByteArray(16000)
                ByteArrayOutputStream().use { buffer ->
                    while (true) {
                        val data = cursor.read(byteArray, 0, byteArray.size)
                        if (data == -1) break
                        buffer.write(byteArray, 0, data)
                    }

                    buffer.flush()
                    val dir = context.getDir(tmp, Context.MODE_PRIVATE)
                    dir.listFiles().forEach { it.delete() }
                    val file = File(dir, "tmp_image.jpg")
                    fullFilePath = file
                    FileOutputStream(file).use {
                        it.write(buffer.toByteArray())
                    }
                }
            }

            return compressor
                .setMaxHeight(maxHeight)
                .setMaxWidth(maxWidth)
                .setQuality(quality)
                .compressToFile(fullFilePath)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun saveFile(oldFile: File, value: String): File? {
        try {
            val file = File(context.filesDir!!, "${value}_${System.currentTimeMillis()}.jpg")
            oldFile.renameTo(file)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteFile(item: FlashcardItem) {
        try {
            item.file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class FinderResult(val file: File? = null)

