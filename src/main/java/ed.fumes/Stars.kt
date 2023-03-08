package ed.fumes


/**this file holds all the classes and mixins for a
 *  r-star-tree biased to provide spherical point distances with 4-tuple ID,x,y,z coordinates
 * */


import kotlin.math.*
import kotlin.time.times

/**a 4-tuple of ID,x,y,z coordinates*/
data class PointRecord3d(
    val
    /** 55 bit id, 9 upper bits of body info*/
    id: ULong, val name: String, val x: Double, val y: Double, val z: Double
) {
    /**distance from this point to another point*/
    fun distanceTo(other: PointRecord3d): LY {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
    /** routing among a point cloud of stars is not like graph or maze route plotting,
     * the stars are not connected by edges, but by the distance between them
     *
     * we want to treat intra-galactic route plotting like a lightning bolt seeking the least resistance
     * path to ground in order to close the circuit.
     */
}

fun circuitHunt(ship: Ship, start: PointRecord3d, target: PointRecord3d, fanout: Int = 5): List<PointRecord3d> {
    val throttle = ship.cruiseControl(start, target)

    val throttleDistance = ship.jumpRangeForFuel(throttle.first)

    // create a sphere boundary function which is the distance of the (throttleDistance*fanout radius) around the
    // startpoint, translated 1/3 radius toward the target
    val sphereBoundary = fun(p: PointRecord3d): Boolean {
        val dx = start.x - p.x
        val dy = start.y - p.y
        val dz = start.z - p.z
        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        return distance < throttleDistance * fanout
    }

    // pre-fetch the points within the sphere boundary
    // todo   val points = starTree.pointsInSphere(start, throttleDistance * fanout)
    TODO()
}

const val x0 = -49985
const val y0 = -40985
const val z0 = -24105

fun id64Tocoord(id64: Long) =
    (id64 and 7).toInt().let { masscode ->
        Triple(
            (((id64 shr 30) - (masscode * 2) and ((0x3FFF shr masscode).toLong()) shl masscode) * 10 + x0).toInt(),
            (((id64 shr 17) - masscode and ((0x1FFF shr masscode).toLong()) shl masscode) * 10 + y0).toInt(),
            ((id64 shr 3 and ((0x3FFF shr masscode).toLong()) shl masscode) * 10 + z0).toInt()
        )
    }