package bot.utils

import java.io.FileReader
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*
import kotlin.random.Random

object UtilsKt {

    // based on https://stackoverflow.com/a/2218361
    fun getRandomLineFromFile(filePath: String, unbiased: Boolean = false, minLineLength: Int = 3): String {
        val raf = RandomAccessFile(filePath, "r")

        val filePointerPos = (0 until raf.length()).random()
        var word: String
        do {
            word = getLineFromFile(raf, filePointerPos)
        } while (unbiased && Random.nextDouble(0.0, 1.0) > minLineLength / word.length.toDouble())

        return word
    }

    fun getPropertiesFromResourceFile(fileInResourcesFolder: String): Properties? {
        var properties: Properties? = null
        try {
            val reader = FileReader("src/main/resources/$fileInResourcesFolder")
            properties = Properties()
            properties.load(reader)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return properties
    }

    private fun getLineFromFile(file: RandomAccessFile, position: Long): String {
        var filePointerPos = position
        file.seek(filePointerPos)
        while (file.read().toChar() != '\n')
            file.seek(--filePointerPos)

        val readWord = file.readLine().toCharArray()
        val bytes = ByteArray(readWord.size)
        for (i in readWord.indices)
            bytes[i] = readWord[i].code.toByte()

        return String(bytes, Charsets.UTF_8)
    }

}