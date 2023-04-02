package ed.fumes

import borg.trikeshed.isam.meta.PlatformCodec.Companion.currentPlatformCodec.readInt
import borg.trikeshed.isam.meta.PlatformCodec.Companion.currentPlatformCodec.readLong
import borg.trikeshed.isam.meta.PlatformCodec.Companion.currentPlatformCodec.readULong
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel




/** the first arg is mandatory, the second arg is optional.  first file is the index, second is the gzip file.  the default gzip file is the ending of the .gzi file to be the .gz filename.   `-` implies the inputstream is from stdin and causes an error if the gzip is not specified. the index is loaded into memory as an Access struct*/
fun main(args: Array<String>) {
    val indexFile = args[0]
    val gzipFile = args.getOrNull(1) ?: indexFile.substringBeforeLast(".gzi") + ".gz"

//    istream we need is either stdin or unbuffereed fileinsputstream
    val istream= if(indexFile=="-") System.`in` else File(indexFile).inputStream()
    val access = GzipIndexReader.deserialize_index_from_file(istream, gzipFile)
    println(access)
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

val intx = ByteArray(4)
fun InputStream.readInt(): Int {
    read(intx)
    return readInt(intx)
}

val longx = ByteArray(8)
fun InputStream.readLong(): Long {
    read(longx)
    return readLong(longx)
}

fun InputStream.readUInt(): UInt {
    return readInt().toUInt()
}

fun InputStream.readULong(): ULong {
    return readLong().toULong()
}


object GzipIndexReader {
    val emptiestByteArray = ByteArray(0)

    data class Point(
        var out: ULong = 0u,
        var `in`: ULong = 0u,
        var bits: UInt = 0u,
        var windowSize: UInt = 0u,
        var window: ByteArray = emptiestByteArray,
        var windowBeginning: ULong = 0u,
        var lineNumber: ULong = 0u,
    ){
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
    ){
        //make a toString function that blots out window data
        override fun toString(): String {
            return "Access(have=$have, size=$size, fileSize=$fileSize, list=${list.size}, fileName='$fileName', indexComplete=$indexComplete, indexVersion=$indexVersion, lineNumberFormat=$lineNumberFormat, numberOfLines=$numberOfLines)"
        }
    }

    const val GZIP_INDEX_HEADER_SIZE = 16
    const val GZIP_INDEX_IDENTIFIER_STRING = "gzipindx"
    const val GZIP_INDEX_IDENTIFIER_STRING_V1 = "gzipindX"

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


    fun FileChannel.readIntBE(): Int {
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        read(buffer)
        buffer.flip()
        return buffer.int
    }

    fun FileChannel.readLongBE(): Long {
        val buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN)
        read(buffer)
        buffer.flip()
        return buffer.long
    }

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
    ): Access {
        val theIndex = Access()
        require(0L == istream.readLong()) { "failed magic bytes 0" }
        val indexVersion = run {
            val v0 = readULong(GZIP_INDEX_IDENTIFIER_STRING.encodeToByteArray())
            val v1 = readULong(GZIP_INDEX_IDENTIFIER_STRING_V1.encodeToByteArray())
            val vers = istream.readULong()
            require(vers in arrayOf(v0, v1)) {
                "failed magic header string"
            }
            if (vers == v0) 0u else 1u
        }

//        uint64_t have;      /* number of list entries filled in */
//        uint64_t size;      /* number of list entries allocated */
        theIndex.have = istream.readULong()
        theIndex.size = istream.readULong()
        theIndex.fileName = file_name ?: ""
        theIndex.fileSize = istream.readULong()
        theIndex.indexComplete = 1u
        theIndex.indexVersion = indexVersion
        theIndex.lineNumberFormat = istream.readUInt()
        theIndex.numberOfLines = istream.readULong()

        for (i in 0 until theIndex.size.toInt()) {
            var wsize = -1
            theIndex.list.add(
                Point(
                    out = istream.readULong(),
                    `in` = istream.readULong(),
                    bits = istream.readUInt(),
                    windowSize = istream.readUInt().also { wsize = it.toInt() },
                    window = istream.readNBytes(wsize),

                    lineNumber = if (indexVersion == 1u) istream.readULong() else 0u,
                )
            )
        }
        return theIndex
    }
}
