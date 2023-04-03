package ed.fumes


import borg.trikeshed.common.use
import borg.trikeshed.lib.FibonacciReporter
import borg.trikeshed.lib.humanReadableByteCountIEC
import borg.trikeshed.lib.logDebug
import java.io.*
import java.nio.ByteBuffer.wrap
import java.nio.ByteOrder
import java.util.zip.Inflater


/** the first arg is mandatory, the second arg is optional.  first file is the index, second is the gzip file.  the default gzip file is the ending of the .gzi file to be the .gz filename.   `-` implies the inputstream is from stdin and causes an error if the gzip is not specified. the index is loaded into memory as an Access struct*/
fun showIndexCompressionRatios(args: Array<String>) {
    val indexFile = args[0]
    val gzipFile = args.getOrNull(1) ?: indexFile.substringBeforeLast(".gzi") + ".gz"

    //    istream we need is either stdin or unbuffereed fileinsputstream
    val istream = if (indexFile == "-") System.`in` else File(indexFile).inputStream()
    val access = GztoolIndexReader.deserialize_index_from_file(istream, gzipFile)
    println(access)

    val size = access.list.size
    FibonacciReporter(size, "distances").use { fibReporter ->
        //    take this index and show access rolling report of the position compressed version the uncompressed position.
        for ((ix, point) in access.list.withIndex())
            if (fibReporter.report() != null) indexSlotStats(ix, point)
        var c = size - 2
        access.list.takeLast(2).forEach { indexSlotStats(c++, it) }
    }
}

/** the first arg is mandatory, the second arg is optional.  first file is the index, second is the gzip file.  the default gzip file is the ending of the .gzi file to be the .gz filename.   `-` implies the inputstream is from stdin and causes an error if the gzip is not specified. the index is loaded into memory as an Access struct*/
fun showWindows(args: Array<String>) {
    val indexFile = args[0]
    val gzipFile = args.getOrNull(1) ?: indexFile.substringBeforeLast(".gzi") + ".gz"

    //    istream we need is either stdin or unbuffereed fileinsputstream
    val istream = if (indexFile == "-") System.`in` else File(indexFile).inputStream()
    val access = GztoolIndexReader.deserialize_index_from_file(istream, gzipFile)
    println(access)

    val size = access.list.size
    FibonacciReporter(size, "distances").use { fibReporter ->
        //    take this index and show access rolling report of the position compressed version the uncompressed position.
        for ((ix, point) in access.list.withIndex())
            if (fibReporter.report() != null) indexSlotStats2(ix, point)
        var c = size - 2
        access.list.takeLast(2).forEach { indexSlotStats2(c++, it) }
    }
}


private fun indexSlotStats(ix: Int, point: GztoolIndexReader.Point) {
    println("point $ix: in=${point.`in`.toLong().humanReadableByteCountIEC} out=${point.out.toLong().humanReadableByteCountIEC}  ratio %${point.`in`.toFloat() / point.out.toFloat() * 100}")
}

private fun indexSlotStats2(ix: Int, point: GztoolIndexReader.Point) {

    val samp = point.window.take(40)
    val sampString = samp.map { it.toInt().toChar() }.joinToString("")

    //inflate the window
    val inflater = Inflater()
    inflater.setInput(point.window)
    val out = ByteArray(40)
    val len = inflater.inflate(out)
    val outString = out.map { it.toInt().toChar() }.joinToString("")
    println("point $ix: in=${point.`in`.toLong().humanReadableByteCountIEC} out=${point.out.toLong().humanReadableByteCountIEC}  window: ${sampString}  out: ${outString}")
}

/**
The v0 and v1 gzip index file formats have the following fixed bytes:

---
### V0 format

| Field | Type | Size (bytes) |
| --- | --- | --- |
| Out file offset | uint32_t | 4 |
| In file offset | uint32_t | 4 |
| Number of bits | uint8_t | 1 |
| Window size | uint32_t | 4 |

---
### V1 format:

| Field | Type | Size (bytes) |
| --- | --- | --- |
| Out file offset | uint64_t | 8 |
| In file offset | uint64_t | 8 |
| Number of bits | uint8_t | 1 |
| Window size | uint32_t | 4 |
| Line number | uint64_t | 8 |

In both versions, the fields represent:

Out file offset: the uncompressed file offset at which the block starts.
In file offset: the compressed file offset at which the block starts.
Number of bits: the number of bits used to encode the distance between the current and the previous block. This number is used to calculate the maximum distance between two blocks in the same window.
Window size: the size of the compressed data window that starts at In file offset.
Line number (only in v1 format): the line number at which the block starts in the uncompressed file.
All fields have fixed sizes except for the compressed data window, which has a variable size.
 */

fun InputStream.readInt(): Int {
    val intx = ByteArray(4)
    read(intx)
    return wrap(intx).order(ByteOrder.BIG_ENDIAN).int
}

fun InputStream.readLong(): Long {
    val longx = ByteArray(8)
    read(longx)
    return wrap(longx).order(ByteOrder.BIG_ENDIAN).long
}

fun InputStream.readUInt(): UInt {
    return readInt().toUInt()
}

fun InputStream.readULong(): ULong {
    return readLong().toULong()
}

fun InputStream.readIntSE(): Int {
    val intx = ByteArray(4)
    read(intx)
    return wrap(intx).order(ByteOrder.LITTLE_ENDIAN).int
}

fun InputStream.readLongSE(): Long {
    val longx = ByteArray(8)
    read(longx)
    return wrap(longx).order(ByteOrder.LITTLE_ENDIAN).long
}

fun InputStream.readUIntSE(): UInt {
    return readIntSE().toUInt()
}

fun InputStream.readULongSE(): ULong {
    return readLongSE().toULong()
}


object GztoolIndexReader {
    val emptiestByteArray = ByteArray(0)

    data class Point(
        var out: ULong = 0u,
        var `in`: ULong = 0u,
        var bits: UInt = 0u,
        var windowSize: UInt = 0u,
        var window: ByteArray = emptiestByteArray,
        var windowBeginning: ULong = 0u,
        var lineNumber: ULong = 0u,
    ) {
        //make a toString function that blots out window data
        override fun toString(): String {
            return "Point(out=$out, `in`=$`in`, bits=$bits, windowSize=$windowSize, window=[...], windowBeginning=$windowBeginning, lineNumber=$lineNumber)"
        }
    }

    data class Access(
//        uint64_t have;      /* number of list entries filled in */
//    uint64_t size;      /* number of list entries allocated */
        var have: ULong = 0u, //size
        var size: ULong = 0u, //size
        var fileSize: ULong = 0u,
        var list: MutableList<Point> = mutableListOf(),//of size size
        var fileName: String = "",// NOTE: file_name, index_complete and index_version are not stored on disk (on-memory-only values)
        var indexComplete: UInt = 0u,// NOTE: file_name, index_complete and index_version are not stored on disk (on-memory-only values)
        var indexVersion: UInt = 0u,// NOTE: file_name, index_complete and index_version are not stored on disk (on-memory-only values)
        var lineNumberFormat: UInt = 0u,/* 0: linux \r | windows \n\r; 1: mac \n */
        var numberOfLines: ULong = 0u,
    ) {
        //make a toString function that blots out window data
        override fun toString(): String {
            return "Access(have=$have, size=$size, fileSize=$fileSize, list=${list.size}, fileName='$fileName', indexComplete=$indexComplete, indexVersion=$indexVersion, lineNumberFormat=$lineNumberFormat, numberOfLines=$numberOfLines)"
        }
    }

    const val GZIP_INDEX_HEADER_SIZE = 16
    const val GZIP_INDEX_IDENTIFIER_STRING = "gzipindx"
    const val GZIP_INDEX_IDENTIFIER_STRING_V1 = "gzipindX"


    /**
    read index from a file
    ### INPUT:
    FILE *input_file         : input stream
    int load_windows         : 0 do not yet load windows on memory; 1 to load them
    char *file_name : file path to index file, needed in case
    load_windows==0, so a later extract() can access the
    index file again to read the window data.
    Can be "" if using stdin, but not NULL.
    int extend_index_with_lines: if >0 (index v1) but index is v0, operation cannot be performed
    unless:
     * the index is an **empty file**, in which case the index
    pointer is correctly initialized with corresponding ->index_version
     * extend_index_with_lines == 3, in which case extend_index_with_lines
    is implicit, not compulsory, so process can transparently proceed
    ### OUTPUT:
    `struct access *index : pointer to index, or NULL on error`
     */
    fun deserialize_index_from_file(
        istream: InputStream,
        file_name: String? = null,
        uncompressedIndex: ULong? = null,
    ): Access = FibonacciReporter(noun = "indexes").use { reporter ->
        val theIndex = Access()
        require(0L == istream.readLong()) { "failed magic bytes 0" }

        val indexVersion = run {
            val v0: ULong =
                wrap(GZIP_INDEX_IDENTIFIER_STRING.encodeToByteArray()).order(ByteOrder.BIG_ENDIAN).long.toULong()
            val v1: ULong =
                wrap(GZIP_INDEX_IDENTIFIER_STRING_V1.encodeToByteArray()).order(ByteOrder.BIG_ENDIAN).long.toULong()
            val vers: ULong = istream.readULong()
            require(value = vers in arrayOf(v0, v1)) { "failed magic header string" }
            if (vers == v0) 0u else 1u
        }

        theIndex.indexVersion = indexVersion // Moved indexVersion assignment up
//        theIndex.indexComplete = if (istream.read() == 0) 0u else 1u // Read indexComplete here
        if (indexVersion == 1u) {
            val readUInt = istream.readUInt()
            theIndex.lineNumberFormat = readUInt
        }

        val readULong1 = istream.readULong()
        theIndex.have = readULong1
        val readULong2 = istream.readULong()
        theIndex.size = readULong2
        theIndex.fileName = file_name ?: ""
        // Read file_size conditionally
        if (theIndex.indexComplete == 1u) theIndex.fileSize = istream.readULong()
        // Read lineNumberFormat conditionally
        // Read numberOfLines conditionally
        if (indexVersion == 1u && theIndex.indexComplete == 1u) theIndex.numberOfLines = istream.readULong()

        while (istream.available() > 0) {
            var wsize = -1
            var element: Point
            val out = istream.readULong()
            if (uncompressedIndex != null && uncompressedIndex <= out) break

            theIndex.list.add(
                Point(
                    out,
                    `in` = istream.readULong(),
                    bits = istream.readUInt(),
                    windowSize = istream.readUInt().also { wsize = it.toInt() },
                    window = istream.readNBytes(wsize),
                    lineNumber = if (indexVersion == 1u) istream.readULong() else 0u,
                ).also { element = it }

            )
            reporter.report()?.let { r -> logDebug { r } }
        }
        return@use theIndex
    }

    fun decompress_index_window(
        indexFilename: String,
        gzipFile: String = indexFilename.substringBeforeLast(".gzi") + ".gz",
        uncompressedIndex: ULong = 0U,
    ): ByteArray {
        val access: Access =
            deserialize_index_from_file(FileInputStream(indexFilename), uncompressedIndex = uncompressedIndex)
        val happy = access.list.last()
        val out = happy.out
        require(out < uncompressedIndex) { "index underrun seeking $uncompressedIndex hitting up against $out" }

        //inflate happy.window
        val inflater = Inflater()
        inflater.setInput(happy.window)
        val outBuffer = ByteArray(1024)
        val outStream = ByteArrayOutputStream()
        while (!inflater.finished()) {
            val count = inflater.inflate(outBuffer)
            outStream.write(outBuffer, 0, count)
            if (inflater.needsInput()) {
            }
        }
        return outStream.toByteArray()

    }

    fun calculateCompressedOffset(point: Point): ULong {
        return point.out - (if (point.bits > 0) 1UL else 0UL) - point.bits.toULong()
    }


    fun inflateSourceFile(
        inputStream: InputStream,
        point: Point,
        uncompressedEntryPoint: ULong,
        outputStream: OutputStream
    ) {
        val inflater = Inflater()

        val windowBuffer = ByteArray(point.window_size.toInt())
        inputStream.read(windowBuffer)

        inflater.setInput(windowBuffer)

        if (point.bits > 0) {
            inflater.inflatePrime(point.bits, inputStream.read() shr (8 - point.bits))
        }

        val decompressedWindow = ByteArray(1024)

        var bytesInflated = 0
        while (!inflater.needsInput()) {
            bytesInflated = inflater.inflate(decompressedWindow)
        }

        inflater.setDictionary(decompressedWindow, 0, bytesInflated)

        // Read and decompress the source file
        val compressedDataBuffer = ByteArray(1024) // Adjust the buffer size as needed

        var compressedOffset = point.out.toULong() //
        var uncompressedOffset = point.`in`.toULong()

        while (true) {
            val bytesRead: Int = inputStream.read(compressedDataBuffer)
            if (bytesRead != -1) {
                compressedOffset += bytesRead.toUInt()
                inflater.setInput(compressedDataBuffer, 0, bytesRead)
                val decompressedDataBuffer = ByteArray(1024) // Adjust the buffer size as needed

                while (!inflater.needsInput()) {
                    val bytesInflated = inflater.inflate(decompressedDataBuffer)
                    uncompressedOffset += bytesInflated.toUInt()

                    if (uncompressedOffset >= uncompressedEntryPoint.toUInt()) {
                        val bytesToWrite = uncompressedOffset - uncompressedEntryPoint.toUInt()
                        outputStream.write(
                            decompressedDataBuffer,
                            bytesInflated - bytesToWrite.toInt(),
                            bytesToWrite.toInt()
                        )
                    }
                }
            } else break
        }
        inflater.end()
    }


}

fun main(args: Array<String>) {
    showWindows(args)

}





