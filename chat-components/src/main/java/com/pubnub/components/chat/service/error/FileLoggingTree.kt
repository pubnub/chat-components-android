package com.pubnub.components.chat.service.error

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.annotation.NonNull
import org.jetbrains.annotations.Nullable
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree(private val context: Context) : Timber.DebugTree() {
    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            val path = "Log"
            val fileNameTimeStamp: String = SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(Date())
            val logTimeStamp: String = SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa",
                Locale.getDefault()).format(Date())
            val fileName = "$fileNameTimeStamp.html"

            // Create file
            val file: File? = generateFile(context, path, fileName)

            // If file created or exists save logs
            if (file != null) {
                val writer = FileWriter(file, true)
                writer.append("<p style=\"background:lightgray;\"><strong "
                        + "style=\"background:lightblue;\">&nbsp&nbsp")
                    .append(logTimeStamp)
                    .append(" :&nbsp&nbsp</strong><strong>&nbsp&nbsp")
                    .append(tag)
                    .append("</strong> - ")
                    .append(message)
                    .append("</p>")
                writer.flush()
                writer.close()
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error while logging into file : $e")
        }
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        // Add log statements line number to the log
        return super.createStackElementTag(element).toString() + " - " + element.lineNumber
    }

    companion object {
        private val LOG_TAG = FileLoggingTree::class.java.simpleName

        /*  Helper method to create file*/
        @SuppressLint("LogNotTimber")
        @Nullable
        private fun generateFile(context: Context, @NonNull path: String, @NonNull fileName: String): File? {
            var file: File? = null
            if (isExternalStorageAvailable) {
//                val root = File(Environment.getExternalStorageDirectory().absolutePath,
//                    BuildConfig.APPLICATION_ID + File.separator.toString() + path)
                val root = File(context.getExternalFilesDir(null)!!.absolutePath + File.separator.toString() + path)
                var dirExists = true
                if (!root.exists()) {
                    dirExists = root.mkdirs()
                }
                if (dirExists) {
                    file = File(root, fileName)
                }
            }
            Log.i(LOG_TAG, "Log file created: ${file?.absolutePath}")
            return file
        }

        /* Helper method to determine if external storage is available*/
        private val isExternalStorageAvailable: Boolean
            get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}