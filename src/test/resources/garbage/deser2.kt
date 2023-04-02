package garbage
import java.io.File

data class Access(
    val list: MutableList<IndexEntry> = mutableListOf(),
    var file_size: Long = 0L,
    var number_of_lines: Long = 0L,
    var index_complete: Boolean = false
)

data class IndexEntry(
    var out: Long,
    var `in`: Long,
    var bits: Int,
    var window_size: Int,
    var window: ByteArray?,
    var window_beginning: Long,
    var line_number: Long
)
fun deserializeIndexFromFile(
    input_file: File,
    load_windows: Boolean,
    file_name: String,
    extend_index_with_lines: Int
): Access? {
    val header = ByteArray(GZIP_INDEX_HEADER_SIZE)
    val here = Point()
    var index: Access? = null
    var index_complete = true
    var number_of_index_points: Long = 0
    var index_have: Long = 0
    var index_size: Long = 0
    var file_size: Long = 0
    var position_at_file: Long = 0
    var index_version = 0 // 0: default; 1: index with line numbers

    // get index file size to calculate on-the-fly how many registers are
    // in order to being able to read still-growing index files (see `-S`)
    if (file_name.isNotEmpty()) {
        file_size = File(file_name).length()
    } else {
        // for stdin use max value
        file_size = Long.MAX_VALUE
    }

    // check index size == 0
    if (file_size != 0L) {
        input_file.read(header)
        if (header.toUInt64(0) == 0uL) {
            val identifierString = header.copyOfRange(GZIP_INDEX_HEADER_SIZE / 2, GZIP_INDEX_HEADER_SIZE)
            when {
                identifierString.contentEquals(GZIP_INDEX_IDENTIFIER_STRING.toByteArray()) -> {
                    index_version = 0
                }
                identifierString.contentEquals(GZIP_INDEX_IDENTIFIER_STRING_V1.toByteArray()) -> {
                    index_version = 1
                }
                else -> {
                    printToStderr(VERBOSITY_NORMAL, "ERROR: File is not a valid gzip index file.\n")
                    return null
                }
            }
        } else {
            printToStderr(VERBOSITY_NORMAL, "ERROR: File is not a valid gzip index file.\n")
            return null
        }
    } else {
        // for an empty index, return a pointer with zero data
        index = createEmptyIndex()
        if (extend_index_with_lines > 0) {
            index.index_version = 1
            if (extend_index_with_lines == 2) index.line_number_format = 1
            else index.line_number_format = 0 // extend_index_with_lines = 1 || 3
        }
        return index
    }

    if (0 == index_version && extend_index_with_lines > 0) {
        if (3 != extend_index_with_lines) {
            printToStderr(VERBOSITY_NORMAL, "ERROR: Existing index has no line number information.\n")
            printToStderr(VERBOSITY_NORMAL, "ERROR: Aborting on `-[LRxX]` parameter(s).\n")
            gotoDeserializeIndexFromFileError()
        } else {
            // transparently handle v0 files, as `-x` was implicitly tried (v>1.4.2), but it is not compulsory
            extend_index_with_lines = 0 // this value is not used - set here only for clarity
            printToStderr(VERBOSITY_EXCESSIVE, "implicit `-x`: 0 (deserialize_index_from_file).\n")
        }
    }

    index = createEmptyIndex()
    index.index_version = index_version

    // if index is v1, there's a previous register with line separator format info:
    if (index_version == 1) {
        freadEndian(index.line_number_format, input_file)

