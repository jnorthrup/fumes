@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package ed.fumes

import borg.trikeshed.common.Files.iterateLines
import borg.trikeshed.common.collections._s
import borg.trikeshed.cursor.*
import borg.trikeshed.isam.meta.IOMemento.*
import borg.trikeshed.lib.*
import borg.trikeshed.parse.*
import ed.fumes.EdSystemMetaLite.Companion.cache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.time.ExperimentalTime


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
    val fifoname = tmpdir.toString() + "/fifo1"
    val gziFname = fname.replace(".gz", ".gzi")
    runBlocking {
        val procJob = launch {
            val streamScript = """#!/bin/bash
              set -x    
                echo processing in $tmpdir
            pushd $tmpdir
            #  create 2 random fifos in bash
            mkfifo $tmpdir/fifo1
            set -x
         ${if (curl) "curl" else "cat"} "$prefix/$fname" |gztool >$fifoname -z -b0 -I $gziFname && mv -vv *isam* ${gziFname} ${dataDir};
         
          ls -lah ${dataDir}   
           """.trimIndent()
            val process =
                ProcessBuilder("/usr/bin/taskset", "-c", "12-15", "/bin/bash", "-c", streamScript).inheritIO().start()
            withContext(Dispatchers.IO) {
                process.waitFor()
            }
        }
        launch(Dispatchers.Unconfined ) {
            val rowInstructions = EdSystemMetaLite.cache.map { it.needReify j it.path.toList().toJsPath }
            val datafilename = "$tmpdir/$isamName"

            val rowMeta: Series<ColumnMeta> = EdSystemMetaLite.cache.map { (it.name j it.typeMemento) }.toSeries()
            val metashadow = rowMeta.map { (it.first j it.second).`↺` }
            val readLogger = FibonacciReporter(noun = "systems")
            while (true) {
                if (Files.exists(Paths.get(fifoname))) {
                    println("datafile exists: $datafilename")
                    break
                } else {
                    println("waiting for datafile: $datafilename")
                    delay(1000)
                }
            }

            val iterateLines = iterateLines(fifoname,156 )
            //print the line to stdout

            val count = iterateLines.count()
            println("counted $count lines")

        }
        joinAll()
    }
}


//            val lineIterable = iterateLines where { (_: Long, buf: Series<Byte>) -> buf.size > 3 }
//
//
//
//            val rowVecIterator: Iterable<RowVec> = lineIterable select { (offset, buf: Series<Byte>) ->
//                ByteSeries(buf).trim.decodeUtf8().let { chBuf ->
//                    (JsonParser.index(chBuf, takeFirst = 3) j chBuf).let { jsContext: JsContext ->
//                        val watermark = offset
//                        rowInstructions.mapIndexed { x, (reifyMe, jsPath) ->
//                            when (x) {
//                                0 -> watermark j rowMeta[x]
//                                else -> {
//                                    JsonParser.jsPath(jsContext, jsPath, reifyMe).let { jse ->
//                                        if (reifyMe) jse
//                                        else when (rowMeta[x].second) {
//                                            IoDouble -> (jse as Series<Char>).parseDouble()
//                                            IoLong -> (jse as Series<Char>).parseLong()
//                                            IoCharSeries -> {
//                                                var cs = CharSeries(jse as Series<Char>)
//                                                if (cs.seekTo('"')) {
//                                                    cs = cs.slice
//                                                    if (cs.seekTo('"', '\\'))
//                                                        cs.flip()
//                                                    else TODO("trashed quote content charseries in path $jsPath")
//                                                }
//                                                cs.trim
//                                            }
//
//                                            else -> {
//                                                println("unhandled type ${rowMeta[x].second} in path $jsPath")
//                                                (jse as Series<Char>).asString()
//                                            }
//                                        }
//                                    }
//                                }
//                            } j metashadow[x]
//
//                        }.toSeries().also {
//                            readLogger.report()
//                        }
//                    }
//                }
//            }
//            rowVecIterator.forEach({ println(it) })
//
//            IsamDataFile.append(rowVecIterator, datafilename, IndexWeeklyGalaxyDump.varchars)
//        }
//    joinAll()
//    }
//
//
////    process.waitFor()
//    tmpdir?.toFile()?.deleteRecursively()
//    println("done")
//
//}
//
//
//
