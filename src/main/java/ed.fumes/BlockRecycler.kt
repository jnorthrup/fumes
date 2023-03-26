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
        val key = (buffer.size + blocksize - 1) / blocksize * blocksize
        theHeap.getOrPut(key, ::mutableSetOf).add(buffer)
    }

    fun recycleFrom(available: Int, alignedOnly: Boolean = false): ByteArray {
        if (disabled) return ByteArray(available)
        val newkey: Int = (available + blocksize - 1) / blocksize * blocksize
        return theHeap.tailMap(newkey, true).let { cnm ->
            cnm.values.firstOrNull { it.isNotEmpty() }?.let {
                val iterator = it.iterator()
                val next = iterator.next()
                iterator.remove()
                next
            } ?: ByteArray(if (alignedOnly) newkey else available)
        }
    }
}