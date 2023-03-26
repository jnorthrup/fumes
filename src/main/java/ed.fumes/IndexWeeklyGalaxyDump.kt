@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package ed.fumes

import borg.trikeshed.common.BFrag
import borg.trikeshed.common.collections._s
import borg.trikeshed.common.size
import borg.trikeshed.common.split1
import borg.trikeshed.cursor.*
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import ed.fumes.BlockRecycler.recycleFrom
import ed.fumes.BlockRecycler.recycleTo
import ed.fumes.EdSystemMetaLite.Companion.cache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


enum class EdSystemMetaLite(val typeMemento: TypeMemento, vararg pathKey: Any?) {
    Seek(IoULong),
    Id64(IoULong, 0),
    Name(IoCharSeries, 1),
    X(IoDouble, 2, 0),
    Y(IoDouble, 2, 1),
    Z(IoDouble, 2, 2),
    ;

    // @Spansh: If you were really interested in reducing size you could reduce the double to float, or even use fixed point integers (which is what the game actually uses), I think those are 32bit and the actual coordinate multiplied by 128 (and with an offset as well).
    // There's more some sample code from EDSM here: https://github.com/EDSM-NET/Component/blob/master/System/Coordinates.php but @Alot (EDTS) has a lot more knowledge about that in edts
    // https://bitbucket.org/Esvandiary/edts/src (edited)
    val path: JsPath = (pathKey).toList().toJsPath
    val needReify by lazy { this.typeMemento in _s[IoDouble, IoBoolean, IoNothing] }

    companion object {
        val cache = EdSystemMetaLite.values()
    }
}

/**
 * galactic index
 * Data Dumps
 * Nightly dumps of galaxy related data
 *
 *
 * | Filename | Filesize | Generated | Description |
 * | --- | --- | --- | --- |
 * | galaxy.json.gz | 65.5 GiB | 19 hours ago | The entire galaxy in gzip format |
 * | galaxy_1day.json.gz | 664.9 MiB | 19 hours ago | Systems where any information within that system was updated in the last 24 hours in gzip format |
 * | galaxy_1month.json.gz | 2.5 GiB | 19 hours ago | Systems where any information within that system was updated in the last 32 days in gzip format |
 * | galaxy_7days.json.gz | 1.3 GiB | 19 hours ago | Systems where any information within that system was updated in the last 7 days in gzip format |
 * | galaxy_populated.json.gz | 1.0 GiB | 19 hours ago | Systems with population greater than 0 in gzip format |
 * | galaxy_stations.json.gz | 1.0 GiB | 19 hours ago | Systems with a station in gzip format (including fleet carriers) |
 *
 *
 *   url prefix is https://downloads.spansh.co.uk/
 *
 *
 * dameon process:
 * weekly: fname=galaxy.json.gz
 * daily: fname=galaxy_7day.json.gz
 *
 *
 * process:
 *
 *
 * make random tmpdir in bash
 * create 2 random fifos in bash
 * mkdir,cd into ${fumesdata}
 * zcat <(curl prefix+fname ) |tee >(gztool -I fname.gzi ) fifo1 fifo2
 *
 *
 * fifo1 feeding createIsam of uncompressed_offset,id64,x,y,z,name
 * fifo2 building raw offset index to id64s
 *
 * @see [...](https://www.reddit.com/r/EliteDangerous/comments/hvuwb6/galmap_starnaming_from_id64/)
 */
object IndexWeeklyGalaxyDump {

    val varchars = mapOf(EdSystemMetaLite.Name.name to 64)
    val meta by lazy {
        (cache α { (it.name j it.typeMemento) }).debug {
            println("galaxy coordinates schema: " + it.map(Join<*, *>::pair))
        }
    }
}


@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val dataDir =  /* current working dir */System.getProperty("user.dir") + "/data"
    val prefix = args.takeIf { it.size > 0 }?.let { args[0] } ?: "https://downloads.spansh.co.uk/"
    val fname = args.takeIf { it.size > 1 }?.let { args[1] } ?: "galaxy_1day.json.gz"
    val isamName: String = fname.replace("\\.gz$".toRegex(), ".lite.isam").toString()

    println("processing $prefix/$fname using datadir $dataDir")
    //make tempdir in java
    val tmpdir: Path? = try {
        Files.createTempDirectory("fumes").also {
            print("tempdir created: $it")
        }

    } catch (e: IOException) {
        throw RuntimeException(e)
    }
    val curl = (prefix.matches("^http[s]?://.*".toRegex()))
    val gziFname = fname.replace(".gz", ".gzi")

    runBlocking {
        val procJob = launch {
            val process = ProcessBuilder(
                "/usr/bin/taskset", "-c", "12-15", "/bin/bash", "-c", """
    ${if (curl) "curl" else "cat"} "$prefix/$fname" |gztool  -z -b0 -I $gziFname  
    """.trimIndent()
            )
                //grab stdio but nullify stderr
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()

            //use the output of gztool to provide linescanner some input to work with


            val datafilename = "$tmpdir/$isamName"

            val rowMeta: Series<ColumnMeta> = EdSystemMetaLite.cache.map { (it.name j it.typeMemento) }.toSeries()
            val readLogger = FibonacciReporter(noun = "systems")

            var timer: Duration = Duration.ZERO
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
                val ret = recycleFrom(needed, false)
                assert(ret.size >= needed)
                var offset = 0
                for (accumNext in accum) {
//                    val (bounds, buf) = accumNext
//                    val (beg, end) = bounds
                    val (bounds, buf) = accumNext
                    val (beg, end) = bounds
                    buf.copyInto(ret, offset, beg, end)
                    offset += accumNext.size
                }

                accum.map { (_, buf) -> buf }.distinct().filter { it ->
                    val bytes: ByteArray? = tail?.b
                    it.size > 1 && it !== bytes
                }.forEach(::recycleTo)
                accum.clear()
                if (tail != null) {
                    val (beg, end) = tail.a
                    tail.b.copyInto(ret, offset, beg, end)
                }
                //update min/max linelength
                if (ret.size > maxlineLength) maxlineLength = ret.size
                if (ret.size < minlineLength) minlineLength = ret.size

                return ByteSeries(ret)
            }

            val lineSeq = sequence {
                timer = measureTime {
                    val eols = mutableListOf<Int>()
                    val iss = process.inputStream
                    var inputPos = 0L
                    while (true) {
                        val available = iss.available()
                        loops++
                        if (available > 0) {

                            if (available > maxAvail) maxAvail = available
                            if (available < minAvail) minAvail = available
                            var buffer: ByteArray? = null
                            var read: Int
                            do {
                                //the "trivial" recycling of the size or next largest buffer from the CSLM
                                buffer = buffer ?: recycleFrom(available, true)

                                read = iss.read(buffer)
                                if (0 == read) {
                                    (loops++);continue
                                } else break
                            } while (true)
                            reads++
                            avail += available
                            var active: BFrag? = 0 j read j buffer!!
                            do active = try {
                                assert(active != null)
                                active!!.split1('\n'.code.toByte()).let { (line, tail): Twin<BFrag?> ->
                                    if (line != null) line.let { terminal ->
                                        EOLs++
                                        readLogger.report()?.let(::println)
                                        yield(lineOffset j drainAccum(terminal))
                                        lineOffset += terminal.size
                                        tail
                                    } else let {
                                        kotlin.assert(tail != null)
                                        accum += tail!!
                                        null
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                debug { logDebug { "$e @ $lineOffset at EOLs $EOLs " } }
                                null
                            } while (active != null)
                        } else if (!process.isAlive) {
                            drainAccum().takeIf { it.isNotEmpty() }?.also { yield(lineOffset j it) }
                            break
                        }
                    }
                    process.waitFor()
                }
                val timer2 = timer
                println(" time taken to process: $timer2")
                println("\n-----------\navg avail: ${avail.toFloat() / reads}")
                println("min avail: $minAvail")
                println("max avail: $maxAvail")
                println("EOLs: ${EOLs}")
                println("throughput $lineOffset bytes in ${timer2.inWholeSeconds}s ${(lineOffset / timer2.inWholeSeconds.toDouble()).toLong().humanReadableByteCountIEC} bytes/sec")
                //min and max line lengths
                println("min line length: $minlineLength")
                println("max line length: ${maxlineLength.toLong().humanReadableByteCountIEC}")
                //show the loops and reads/loops ratio and bytesIEC/read ratio
                println("loops: $loops  reads: $reads  loops/read=${loops.toFloat() / reads.toFloat()}  bytes/read=${(lineOffset.toFloat() / reads.toFloat()).toLong().humanReadableByteCountIEC}")

                process.exitValue().run {
                    //if the process completed successfully move the gz index and isam to the data dir
                    if (this == 0) {
                        val gziFile = File("$tmpdir/$gziFname")
                        val isamFile = File("$tmpdir/$isamName")
                        if (gziFile.exists() && isamFile.exists()) {
                            gziFile.copyTo(File("$dataDir/$gziFname"), true)
                            isamFile.copyTo(File("$dataDir/$isamName"), true)
                        }
                    }
                }
                //print the stats for avail avg, excess loops as a percentage of reads, and total reads
            }
            val bigHeapHit = lineSeq
            println("lines returned is ${bigHeapHit.count()}")
            val rejects = (bigHeapHit.withIndex().asIterable() where { (ix, v) -> v.b.size <= 6 }).map { (ix, v) ->
                "$ix : ${v.a}: ${
                    (v.b).decodeToString().replace("\t", "T").replace("\r", "R").replace("\n", "N")
                        .replace("" + 0.toChar(), "0")
                }"
            }
            println("the non-viable line counts are ${rejects.count()}")
            for (c in rejects) {
                println(c)
            }
        }
    }
}


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