package ed.fumes

import borg.trikeshed.common.BFrag
import borg.trikeshed.common.copyInto
import borg.trikeshed.common.size
import borg.trikeshed.common.split1
import borg.trikeshed.lib.*
import java.io.InputStream
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.assert
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun nonBlockingLineReader(inputStream: InputStream, predicate: () -> Boolean): Sequence<Join<Long, ByteSeries>> {
    val readLogger = FibonacciReporter(noun = "systems")
    var bytesRead = 0L
    var avail = 0L
    var minAvail = Int.MAX_VALUE
    var maxAvail = Int.MIN_VALUE
    var minlineLength = Int.MAX_VALUE
    var maxlineLength = Int.MIN_VALUE
    var loops = 0L
    var reads = 0L
    var EOLs = 0
    var lineOffset = 0L
    val accum = mutableListOf<BFrag>()

    //java ranked buckets of byte[] by size for reuse
    val fragHeap = ConcurrentSkipListMap<Int, MutableSet<ByteArray>>()

    fun drainAccum(tail: BFrag? = null): ByteSeries {
        val needed = (tail?.size ?: 0) + accum.sumOf(BFrag::size)
        val ret = BlockRecycler.recycleFrom(needed, false)
        borg.trikeshed.lib.assert(ret.size >= needed)
        var offset = 0
        for (accumNext in accum) {
            accumNext.copyInto(ret, offset)
            offset += accumNext.size
        }

        accum.map { (_, buf) -> buf }.distinct().filter { it ->
            val bytes: ByteArray? = tail?.b
            it.size > 1 && it !== bytes
        }.forEach(BlockRecycler::recycleTo)
        accum.clear()
        if (tail != null) {
            val (beg, end) = tail.a
            tail.b.copyInto(ret, offset, beg, end)
        }
        Unit.debug {       //update min/max linelength
            if (ret.size > maxlineLength) maxlineLength = ret.size
            if (ret.size < minlineLength) {
                minlineLength = ret.size
                if (minlineLength == 0) println("zero byte line at offset $lineOffset line# $EOLs")
            }
        }
        return ByteSeries(ret, 0, needed)
    }

    val lineSeq = sequence {
        measureTime {
            val iss = inputStream
            GOTO10@ while (true) {
                val available = iss.available()
                debug { loops++ }
                if (available > 0) {
                    debug {
                        if (available > maxAvail) maxAvail = available
                        if (available < minAvail) minAvail = available
                    }
                    var buffer: ByteArray? = null
                    var read: Int
                    GOTO100@ do {
                        //the "trivial" recycling of the size or next largest buffer from the CSLM
                        buffer = buffer ?: BlockRecycler.recycleFrom(available, true)

                        read = iss.read(buffer)
                        if (0 != read) break
                    } while (true)
                    var active: BFrag? = 0 j read j buffer!!
                    debug { reads++; avail += available; bytesRead += read }
                    do active = try {
                        borg.trikeshed.lib.assert(active != null)
                        active!!.split1('\n'.code.toByte()).let { (line, tail): Twin<BFrag?> ->
                            if (line != null) line.let { terminal ->
                                debug { EOLs++ }
                                val byteSeries = drainAccum(terminal)
                                yield(lineOffset j byteSeries)
                                lineOffset += byteSeries.rem//aining
                                readLogger.report()?.let(::println)
                                tail
                            } else let {
                                assert(tail != null)
                                accum += tail!!
                                null
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        logDebug { "$e @ $lineOffset at EOLs $EOLs " }
                        null
                    } while (active != null)
                } else if (!predicate()) {
                    drainAccum().takeIf { it.hasRemaining }?.also { yield(lineOffset j it) }
                    break
                }
            }

        }.debug {
            val seconds = it.inWholeSeconds
            println(
                "throughput ${lineOffset.humanReadableByteCountIEC} in ${
                    seconds
                }s ${(lineOffset / seconds.toDouble()).toLong().humanReadableByteCountIEC} bytes/sec"
            )
            println(" time taken to process: $it")
            println("\n-----------\navg avail: ${avail.toFloat() / reads}")
            println("min avail: $minAvail")
            println("max avail: $maxAvail")
            println("EOLs: ${EOLs}")
            //min and max line lengths
            println("min line length: $minlineLength")
            println("max line length: ${maxlineLength.toLong().humanReadableByteCountIEC}")
            //show the loops and reads/loops ratio and bytesIEC/read ratio
            println("loops: $loops  reads: $reads  loops/read=${loops.toFloat() / reads.toFloat()}  bytes/read=${(lineOffset.toFloat() / reads.toFloat()).toLong().humanReadableByteCountIEC}")
            //print bytesRead
            println("bytes read: ${bytesRead.humanReadableByteCountIEC}")
        }
        //print the stats for avail avg, excess loops as a percentage of reads, and total reads
    }
    return lineSeq
}