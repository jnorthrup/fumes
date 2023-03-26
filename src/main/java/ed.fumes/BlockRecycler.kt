package ed.fumes

import java.util.concurrent.ConcurrentSkipListMap

object BlockRecycler {
    const val blocksize = 4096
    val disabled: Boolean = (System.getProperty("DISABLE_BLOCKCACHE") == "true").apply {
        System.err.println("block cache is ${if (this) "disabled" else "enabled"}")
    }

    //java ranked buckets of byte[] by size for reuse
    val theHeap = ConcurrentSkipListMap<Int, MutableSet<ByteArray>>()
    fun recycleTo(buffer: ByteArray): Unit {
        if (disabled) return
        val alignedKey = alignedCeiling(buffer.size)
        theHeap.getOrPut(alignedKey, ::mutableSetOf).add(buffer)
    }

    fun recycleFrom(minimum: Int, alignedOnly: Boolean = false): ByteArray {
        if (disabled) return ByteArray(minimum)
        return theHeap.tailMap(minimum, true).let { cnm ->
            cnm.values.firstOrNull { it.isNotEmpty() }?.run {
                iterator().run { next().also { remove() } }
            } ?: ByteArray(if (alignedOnly)          alignedCeiling(minimum)
            else minimum)
        }
    }

    private fun alignedCeiling(minimum: Int): Int {
        return (minimum + blocksize - 1) / blocksize * blocksize
    }
}