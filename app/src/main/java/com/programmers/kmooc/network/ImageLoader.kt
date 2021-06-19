package com.programmers.kmooc.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageLoader {
    fun loadImage(url: String, completed: (Bitmap?) -> Unit) {
        try {



            GlobalScope.launch(Dispatchers.IO) {
                val url = URL(url)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                launch(Dispatchers.Main) {
                    completed(bmp)
                }
            }
            /*val url = URL(url)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)*/
        } catch (e: IOException) {
            completed(null)
            Log.d("TEST", "error get image from url : $e")
        }
    }
}