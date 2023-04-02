package ed.fumes

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files




data class Point(
    var out: Long,
    var `in`: Long,
    var bits: Int,
    var windowSize: Int,
    var window: ByteArray?,
    var windowBeginning: Long,
    var lineNumber: Long
)

data class Access(
    var have: Long,
    var size: Long,
    var fileSize: Long,
    var list: MutableList<Point>,
    var fileName: String,
    var indexComplete: Boolean,
    var indexVersion: Int,
    var lineNumberFormat: Int,
    var numberOfLines: Long
)

class GzipIndexReader {
    private val GZIP_INDEX_HEADER_SIZE = 16
    private val GZIP_INDEX_IDENTIFIER_STRING = "gzipindx"
    private val GZIP_INDEX_IDENTIFIER_STRING_V1 = "gzipindX"

      fun ByteBuffer.readLongBE(): Long {
        order(ByteOrder.BIG_ENDIAN)
        return long
    }

      fun ByteBuffer.readIntBE(): Int {
        order(ByteOrder.BIG_ENDIAN)
        return int
    }

      fun ByteBuffer.readBytes(n: Int): ByteArray {
        val arr = ByteArray(n)
        get(arr)
        return arr
    }

      fun createEmptyIndex(): Access {
        return Access(
            have = 0L,
            size = 0L,
            fileSize = 0L,
            list = mutableListOf(),
            fileName = "",
            indexComplete = false,
            indexVersion = 0,
            lineNumberFormat = 0,
            numberOfLines = 0L
        )
    }

      fun addPoint(index: Access, point: Point) {
        index.list.add(point)
        index.have++
        index.size++
    }

    fun deserializeIndexFromFile(inputFile: File, loadWindows: Boolean=true, fileName: String): Access? {
        val fileData = Files.readAllBytes(inputFile.toPath())
        val buffer = ByteBuffer.wrap(fileData)

        val header = buffer.readBytes(GZIP_INDEX_HEADER_SIZE)

        if (header.sliceArray(0 until 8).toString(Charsets.UTF_8) != "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" ||
            !(header.sliceArray(8 until 16).toString(Charsets.UTF_8) == GZIP_INDEX_IDENTIFIER_STRING || header.sliceArray(8 until 16).toString(Charsets.UTF_8) == GZIP_INDEX_IDENTIFIER_STRING_V1)
        ) {
            println("ERROR: File is not a valid gzip index file.")
            return null
        }

        val indexVersion = if (header.sliceArray(8 until 16).toString(Charsets.UTF_8) == GZIP_INDEX_IDENTIFIER_STRING_V1) 1 else 0
        val index = createEmptyIndex()
        index.indexVersion = indexVersion

        if (indexVersion == 1) index.lineNumberFormat = buffer.readIntBE()

        val indexHave = buffer.readLongBE()
        val indexSize = buffer.readLongBE()

        if (indexHave != indexSize) {
            println("Index file is incomplete.")
            index.indexComplete = false
        } else index.indexComplete = true

        for (i in 0 until indexSize) {
            val out = buffer.readLongBE()
            val `in` = buffer.readLongBE()
            val bits = buffer.readIntBE()
            val windowSize = buffer.readIntBE()

            val window: ByteArray? = if (loadWindows) {
                val windowBytes = ByteArray(windowSize)
                buffer.get(windowBytes)
                windowBytes
            } else {
                buffer.position(buffer.position() + windowSize)
                null
            }

            val windowBeginning = buffer.readLongBE()
            val lineNumber = if (indexVersion == 1) {
                buffer.readLongBE()
            } else {
                0L
            }

            val point = Point(
                out = out,
                `in` = `in`,
                bits = bits,
                windowSize = windowSize,
                window = window,
                windowBeginning = windowBeginning,
                lineNumber = lineNumber
            )

            addPoint(index, point)
        }

        index.fileName = fileName
        index.numberOfLines = if (indexVersion == 1) buffer.readLongBE() else 0L
        index.fileSize = inputFile.length()

        return index

    }

    fun checkIndexFile(inputFile: File): Boolean {
        val fileData = Files.readAllBytes(inputFile.toPath())
        val buffer = ByteBuffer.wrap(fileData)

        val header = buffer.readBytes(GZIP_INDEX_HEADER_SIZE)

        return (header.sliceArray(0 until 8).toString(Charsets.UTF_8) == "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000") &&
                (header.sliceArray(8 until 16).toString(Charsets.UTF_8) == GZIP_INDEX_IDENTIFIER_STRING ||
                        header.sliceArray(8 until 16).toString(Charsets.UTF_8) == GZIP_INDEX_IDENTIFIER_STRING_V1)
    }


    companion object {
        fun main(args: Array<String>) {
            val inputFile = args[0]
            val gzfile = args.getOrNull(1) ?: (inputFile.replace(".gzi$".toRegex(), ".gz"))

            val gzipIndexReader = GzipIndexReader()
            val index = gzipIndexReader.deserializeIndexFromFile(File(inputFile), true, gzfile)

            if (index != null) {
                println("Index loaded successfully.")
                println("Points: ${index.list.size}")
            } else {
                println("Failed to load index.")
            }
        }
    }
}
