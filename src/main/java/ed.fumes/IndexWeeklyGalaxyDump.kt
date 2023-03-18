package ed.fumes

import borg.trikeshed.common.Files.streamLines
import borg.trikeshed.common.collections._s
import borg.trikeshed.common.use
import borg.trikeshed.cursor.*
import borg.trikeshed.isam.IsamDataFile
import borg.trikeshed.isam.IsamDataFile.Companion.append
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.ExperimentalTime


enum class EdSystem(val typeMemento: TypeMemento, vararg pathKey: Any?) {
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
    val reifiedType by lazy { this.typeMemento in _s[IoDouble, IoBoolean, IoNothing] }

    companion object {
        val cache = EdSystem.values()
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

    val EdEnumCache = EdSystem.values()

    val varchars = mapOf(EdSystem.Name.name to 64)
    val meta by lazy {
        (EdSystem.cache α { (it.name j it.typeMemento) }).debug {
            println("galaxy coordinates schema: " + it.map(Join<*, *>::pair))
        }
    }

}


@OptIn(ExperimentalTime::class)
fun main(args: Array<String>) {
    val dataDir =  /* current working dir */System.getProperty("user.dir") + "/data"

    val prefix = args.takeIf { it.size > 0 }?.let { args[0] } ?: "https://downloads.spansh.co.uk/"
    val fname = args.takeIf { it.size > 1 }?.let { args[1] } ?: "galaxy_1day.json.gz"

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
    val fifoname = tmpdir.toString() + "/fifo1"
    val gziFname = fname.replace(".gz", ".gzi")
    val streamScript = """#!/bin/bash
            echo processing in $tmpdir
            pushd $tmpdir
            #  create 2 random fifos in bash
            mkfifo $tmpdir/fifo1
            set -x
         ${if (curl) "curl" else "cat"} "$prefix/$fname" | tee  >(gztool -z -I $gziFname && 
         mv -vv *isam* ${gziFname} ${dataDir};)|    
             gzip -d  >$fifoname 
          ls -lah ${dataDir}   
           """.trimIndent().trimIndent()
    val process = ProcessBuilder("/usr/bin/taskset", "-c", "12-15", "/bin/bash", "-c", streamScript).inheritIO().start()

    //64 = ~70
    //1024 = 500
    //4096 = 1000
    //16k = 1974.3422/s
    //32k = 2206.6177/s
    //128k 2885.577/s
    val streamLines = streamLines(fifoname, 128 * 1024)
//    val preOptDepths = _l[0, 0, 1]
    val decodeMe = EdSystem.cache.drop(1)
    var watermark = 0UL
    val readLogger: FibonacciReporter = FibonacciReporter(noun = "systems")
    val seqRows = sequence<RowVec> {
        /* spansh format is a json array, but the files are sub-terabyte so we just want a line-scanner, we chop the first line */
        for ((seek: Long, line: ByteArray) in streamLines) {
            watermark = seek.toULong()
            if (line.size < 8) continue
            //meaningless for this data; begin and end braces
            val charSeries: Series<Char> = CharSeries((line).decodeToChars())
            val sanityCheck: MutableList<Int> = mutableListOf()
            val jsElement: JsElement = JsonParser.index(charSeries, sanityCheck, 3)

            //  kotlin.assert(preOptDepths.equals(sanityCheck))
            val theJson = decodeMe α { it: EdSystem ->
                val path = it.path
                val typeMemento = it.typeMemento
                val reifyResult = it.reifiedType
                val value = JsonParser.jsPath(jsElement j charSeries, path, reifyResult, sanityCheck)
                if (!reifyResult) {
                    val charSeries: Series<Char> = value as Series<Char>
                    when (typeMemento) {
                        IoNothing -> null
                        IoShort -> charSeries.parseLong().toShort()
                        IoUShort -> charSeries.parseLong().toUShort()
                        IoInt -> charSeries.parseLong().toInt()
                        IoUInt -> charSeries.parseLong().toUInt()
                        IoLong -> charSeries.parseLong()
                        IoULong -> charSeries.parseLong().toULong()
                        IoCharSeries -> value
                        IoString -> charSeries.asString()
                        else -> throw RuntimeException("unhandled typeMemento $typeMemento")
                    }
                } else value
            }
            val row: RowVec = EdSystem.cache.size j { x: Int ->
                (if (x == 0) seek.toULong() else theJson[x - 1]) j { EdSystem.cache[x].run { name j typeMemento } }
            }
            yield(row).also { readLogger.report()?.let(::println) }
        }
        watermark.toLong().humanReadableByteCountIEC.let {
            //write the stats of total in how much time since readLogger started, avg per second
            val fullRun = readLogger.begin.elapsedNow()
            val elapsedNow = fullRun.toString()
            watermark.toLong().humanReadableByteCountIEC.let { sumBytes ->
                println(
                    "read $sumBytes in $elapsedNow -- avg ${
                        //lines per sec
                        readLogger.count.div(fullRun.inWholeSeconds)
                            .toLong().humanReadableByteCountIEC.replace("iB", "iLOC/json ")
                    },  ${
                        //watermark bytes per second 
                        watermark.div(fullRun.inWholeSeconds.toUInt()).toLong().humanReadableByteCountIEC
                    }  per second; avgline size ${
                        //avg line size
                        watermark.div(readLogger.count.toUInt()).toLong().humanReadableByteCountIEC
                    }"
                )
            }
        }
    }

    val isamName = fname.replace("\\.gz$".toRegex(), ".EdSystem.isam")
    val datafilename = "$tmpdir/$isamName"
    runBlocking {

        append(
            seqRows,
            datafilename,
            IndexWeeklyGalaxyDump.varchars,
            null
        )
        joinAll()
    }
    process.waitFor()
    runBlocking { //
        IsamDataFile(/*the one we just appended*/ datafileFilename = "$dataDir/$isamName").use { curs ->
            println("|+---open---+|")

            val s: Int = curs.size
//            assert(s == readLogger.count) { "expected ${readLogger.count} rows, got $s" }
            val meta: Join<Int, (Int) -> Join<String, TypeMemento>> = curs.meta
            readLogger.close()
            println("table summary: $s rows, ${meta.size} columns")
            println("|+---head---+|")
            curs.head()
        }
    }
    println("done")

}