package ed.fumes

import borg.trikeshed.common.Files.streamLines
import borg.trikeshed.common.collections._l
import borg.trikeshed.common.collections.s_
import borg.trikeshed.cursor.Cursor
import borg.trikeshed.cursor.RowVec
import borg.trikeshed.isam.IsamDataFile
import borg.trikeshed.isam.RecordMeta
import borg.trikeshed.isam.meta.IOMemento
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path


enum class EdSystem(val typeMemento: IOMemento, vararg pathKey: Any) {
    Id64(IoLong, 0),
    Name(IoString, 1),
    X(IoDouble, 2, 0),
    Y(IoDouble, 2, 1),
    Z(IoDouble, 2, 2),
    ;

    val path: JsPath = (pathKey).toList().toJsPath

    companion object {
        val cache = EdSystem.values()
    }
}

val EdEnumCache = EdSystem.values()
val meta: Series2<String, IOMemento> = combine(s_["seek" j IoLong], EdEnumCache α { it.name j it.typeMemento })
val readLogger = FibonacciReporter(noun = "systems")
val begin: Long by lazy { System.currentTimeMillis() }

class SeqCursor(val seq: Sequence<Join<Long, ByteArray>>) : Cursor {
    override val a: Int
        get() = TODO()
    override val b: (Int) -> RowVec
        get() = TODO()
}

val SeqCursor.meta: Series2<String, IOMemento>
    get() = combine(s_["seek" j IoLong], EdEnumCache α { it.name j it.typeMemento })

fun SeqCursor.iterator(): Iterator<RowVec> {
    val iterator = seq.iterator()

    return object : Iterator<RowVec> {
        override fun hasNext(): Boolean = iterator.hasNext()
        val rsize = EdSystem.cache.size + 1
        override fun next(): RowVec = iterator.next().let {
            val (offset: Long, line: ByteArray) = it
            val src = line.decodeToCharSeries()
            val json: JsElement = JsonParser.index(src, takeFirst = 3)

            rsize j { x: Int ->
                when (x) {
                    0 -> offset
                    else -> {
                        val ctx = (json j src)
                        ctx[EdSystem.cache[x - 1].path] ?: "null" as Any
                    }
                } j { meta[x] as RecordMeta }
            }
        }
    }
}

fun loadLine(v: Join<Long, Series<Char>>) {
    val (offset, line) = v
    val json = JsonParser.index(line, takeFirst = 3)
    val jsCtx: JsContext = json j line
    val decode: Series<Any?> = EdSystem.cache α { jsCtx[it.path] }
    val data = _l[offset] + decode.toList()

    readLogger.report()?.let {
        val bps = (offset / (System.currentTimeMillis() - begin)) * 1000
        println("$it total read: ${offset.humanReadableByteCountIEC} @ $bps/s ")
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
internal object IndexWeeklyGalaxyDump1 {
    @JvmStatic
    fun main(args: Array<String>) {
        val DATADIR =  /* current working dir */System.getProperty("user.dir") + "/data"
        val prefix = args[0]
        val fname = args[1]
        //make tempdir in java 
        val tmpdir: Path? = try {
            Files.createTempDirectory("fumes")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val streamScript = """#!/bin/bash
            echo processing in $tmpdir
            pushd $tmpdir
            #  create 2 random fifos in bash
            mkfifo $tmpdir/fifo1
            zcat <(curl "$prefix$fname"   ) |tee >(gztool -I ${fname}}.gzi &&mv  ${fname}.gzi ${DATADIR}) fifo1
            """.trimIndent().trimIndent()
        val name = tmpdir.toString() + "/fifo1"
        val process = ProcessBuilder("/bin/bash", "-c", streamScript).start()

        val streamLines: Sequence<Join<Long, ByteArray>> = streamLines(name)

        val curs = SeqCursor(streamLines)
        IsamDataFile.write(curs, DATADIR + "/SystemIndex.isam")
        println("done")
    }
}



