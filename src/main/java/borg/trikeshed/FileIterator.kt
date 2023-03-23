package borg.trikeshed

import java.io.ByteArrayOutputStream
import java.io.FileInputStream

class FileIterator(val filename: String) : Iterator<ByteArray> {
    val inputStream = FileInputStream(filename)
    val bufferSize = 64 * 1024
    val buffer = ByteArray(bufferSize)
    var bufferStart = 0
    var bufferEnd = 0
    var nextLine: ByteArray? = null

    init {
        nextLine = readNextLine()
    }

    override fun hasNext(): Boolean {
        return nextLine != null
    }

    override fun next(): ByteArray {
        val line = nextLine
        nextLine = readNextLine()
        return line ?: throw NoSuchElementException()
    }

    private fun readNextLine(): ByteArray? {
        val lineBuffer = ByteArrayOutputStream()

        while (true) {
            if (bufferStart >= bufferEnd) {
                // refill buffer
                bufferEnd = inputStream.read(buffer)
                bufferStart = 0

                if (bufferEnd == -1) {
                    // end of file reached
                    break
                }
            }

            var eolIndex = -1

            for (i in bufferStart until bufferEnd) {
                if (buffer[i].toChar() == '\n') {
                    eolIndex = i
                    break
                }
            }

            if (eolIndex != -1) {
                // EOL found in buffer
                lineBuffer.write(buffer, bufferStart, eolIndex - bufferStart)
                bufferStart = eolIndex + 1

                return lineBuffer.toByteArray()
            } else {
                // EOL not found in buffer
                lineBuffer.write(buffer, bufferStart, bufferEnd - bufferStart)
                bufferStart = bufferEnd
            }
        }

        if (lineBuffer.size() > 0) {
            return lineBuffer.toByteArray()
        } else {
            return null
        }
    }
}