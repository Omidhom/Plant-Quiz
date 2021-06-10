package com.example.plantquiz.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class DownloadingObjects {

    @Throws(IOException::class)
    fun downloadJSONDataFromLink(link : String) : String {
        val stringBuilder : StringBuilder = StringBuilder()

        val url : URL = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val bufferedInputString: BufferedInputStream = BufferedInputStream(urlConnection.inputStream)

            val bufferedReader : BufferedReader = BufferedReader(InputStreamReader(bufferedInputString))
            // temporary string to hold each line read from the Reader.
            var inputLine: String?
            inputLine = bufferedReader.readLine()
            while (inputLine != null) {

                stringBuilder.append(inputLine)
                inputLine = bufferedReader.readLine()
            }
        } finally {
            //regardless of succes or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }

        return stringBuilder.toString()
    }
    fun downloadPlantPicture(pictureName: String?) : Bitmap? {

        var bitmap : Bitmap? = null

        val pictureLink : String = PLANTPLACES_COM + "/photos/$pictureName"
        val pictureURL = URL(pictureLink)
        val inputStream = pictureURL.openConnection().getInputStream()
        if (inputStream != null) {
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
    }

    companion object {
        val PLANTPLACES_COM = "http://www.plantplaces.com"
    }
}