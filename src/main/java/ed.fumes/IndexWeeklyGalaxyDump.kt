@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package ed.fumes

import borg.trikeshed.common.collections._s
import borg.trikeshed.cursor.*
import borg.trikeshed.isam.IsamDataFile
import borg.trikeshed.isam.IsamDataFile.Companion.append
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import ed.fumes.EdSystemMetaLite.*
import ed.fumes.EdSystemMetaLite.Companion.meta
import ed.fumes.EdSystemMetaLite.Companion.varchars
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.time.ExperimentalTime


enum class EdSystemMetaLite(val typeMemento: TypeMemento, vararg pathKey: Any?) {
    Seek(IoULong),
    Id64(IoULong, 0),
    Name(IoCharSeries, 1),

    //paths done below for shaking out the bugs
    X(IoDouble, 2, "x"),
    Y(IoDouble, "coords", 1),
    Z(IoDouble, "coords", "z"),
    ;

    // @Spansh: If you were really interested in reducing size you could reduce the double to float, or even use fixed point integers (which is what the game actually uses), I think those are 32bit and the actual coordinate multiplied by 128 (and with an offset as well).
    // There's more some sample code from EDSM here: https://github.com/EDSM-NET/Component/blob/master/System/Coordinates.php but @Alot (EDTS) has a lot more knowledge about that in edts
    // https://bitbucket.org/Esvandiary/edts/src (edited)
    val path: JsPath = (pathKey).toList().toJsPath
    val needReify by lazy { this.typeMemento in _s[IoDouble, IoBoolean, IoNothing] }

    companion object {
        val varchars = mapOf(Name.name to 64)
        val meta: Series<ColumnMeta> by lazy {
            (cache α { (it.name j it.typeMemento) }).debug {
                println("galaxy coordinates schema: " + it.map(Join<*, *>::pair))
            }
        }
        val cache = values()
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
object IndexWeeklyGalaxyDump


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
            val lineSeq = nonBlockingLineReader(process.inputStream) { process.isAlive }


            val rows = (lineSeq.withIndex().asIterable() where { (ix, v) -> v.b.size >= 6 }) α { (lineIndex, join) ->
                val (lineOffset1, line1) = join

                val src = (line1).decodeUtf8()
                val jsElement = JsonParser.index(src, takeFirst = 3)
                val jsContext = JsContext(jsElement, src)


                val size1 = EdSystemMetaLite.cache.size
                val rowVec: RowVec = size1 j { it: Int ->
                    when (it) {
                        0 -> lineOffset1
                        1 -> {
                            val segment: JsIndex = (JsonParser.jsPath(jsContext, Id64.path, false) as? JsIndex)!!
                            val (segOpenIdx, segCloseIdx) = segment.first
                            val src0 = CharSeries(src).lim(segCloseIdx).pos(segOpenIdx)
                            src0.parseLong().toULong()
                        }

                        2 -> {
                            val segment: JsIndex = (JsonParser.jsPath(jsContext, Name.path, false) as? JsIndex)!!
                            val (segOpenIdx, segCloseIdx) = segment.first
                            CharSeries(src).lim(segCloseIdx).pos(segOpenIdx).unquote
                        }

                        else -> JsonParser.jsPath(jsContext, EdSystemMetaLite.cache[it].path, true)
                    } j { meta[it] }
                }
                rowVec
            }
            append(rows, "$tmpdir/$isamName", varchars)


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
        }
    }
}



