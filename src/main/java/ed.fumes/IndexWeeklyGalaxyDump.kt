package ed.fumes

import borg.trikeshed.common.Files.streamLines
import borg.trikeshed.common.collections._l
import borg.trikeshed.cursor.*
import borg.trikeshed.isam.IsamDataFile
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import ed.fumes.IndexWeeklyGalaxyDump.meta
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit


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
    enum class EdSystem(val typeMemento: TypeMemento,   vararg  pathKey: Any?) {
        Seek(IoLong),
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

    val varchars = mapOf(EdSystem.Name.name to 64)
    val meta by lazy {( EdSystem.cache α { (it.name j it.typeMemento) }).debug {
        println("galaxy coordinates schema: "+it.map (Join<*,*>::pair))
    }}


    }


fun main(args: Array<String>) {
    val DATADIR =  /* current working dir */System.getProperty("user.dir") + "/data"

    val prefix = args.takeIf { it.size > 0 }?.let { args[0] } ?: "https://downloads.spansh.co.uk/"
    val fname = args.takeIf { it.size > 1 }?.let { args[1] } ?: "galaxy_1day.json.gz"

    println( "processing $prefix/$fname using datadir $DATADIR")
    //make tempdir in java
    val tmpdir: Path? = try {
        Files.createTempDirectory("fumes").also {
            print("tempdir created: $it")
        }

    } catch (e: IOException) {
        throw RuntimeException(e)
    }

    val curl=(prefix.matches ( "^http[s]?://.*".toRegex()))
    val streamScript = """#!/bin/bash
            echo processing in $tmpdir
            pushd $tmpdir
            #  create 2 random fifos in bash
            mkfifo $tmpdir/fifo1
            set -x
            ${if(curl)"curl" else "cat"} "$prefix/$fname" |   gztool -I ${fname}.gzi -b 0 >fifo1 &&mv  ${fname}.gzi ${DATADIR}  
            """.trimIndent().trimIndent()
    val fifoname = tmpdir.toString() + "/fifo1"
    val process = ProcessBuilder("/bin/bash", "-c", streamScript).inheritIO().start()

    val streamLines = streamLines(fifoname)
    val preOptDepths = _l[0,0,1]
    val decodeMe = IndexWeeklyGalaxyDump.EdSystem.cache.drop(1)
    val seqRows =
        sequence<RowVec> {
            var readLogger: FibonacciReporter? =null
            debug { readLogger = FibonacciReporter(noun = "systems") }
            streamLines.drop(1).forEach { (seek: Long, line: ByteArray): Join<Long, ByteArray> ->
                val charSeries:Series<Char> =CharSeries( (line).decodeToChars())
                val sanityCheck: MutableList<Int> = mutableListOf()
                val jsElement: JsElement = JsonParser.index(charSeries, sanityCheck, 3)
                kotlin.assert(preOptDepths.equals(sanityCheck))
                val theJson = decodeMe α { it ->
                    val path = it.path
                    val typeMemento = it.typeMemento
                    val value = JsonParser.jsPath(jsElement j charSeries, path, true, sanityCheck)
                    value
                }
                val row: RowVec = IndexWeeklyGalaxyDump.EdSystem.cache.size j { x: Int ->
                    (if (x == 0) seek else theJson[x - 1]) j { IndexWeeklyGalaxyDump.EdSystem.cache[x].run { fifoname j typeMemento } }
                }
                yield(row).debug {  readLogger?.report()?.let(::println) }
            }
        }

    val datafilename = DATADIR + "/galaxy_1day.isam"
            println("writing isam to $datafilename")

            IsamDataFile.append(
                seqRows.asIterable(),
                meta, datafilename, IndexWeeklyGalaxyDump.varchars
            )
            println("done")
        process.waitFor(2, TimeUnit.MINUTES)

        }

