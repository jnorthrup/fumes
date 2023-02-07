package ed.fumes


/**this file holds all the classes and mixins for a
 *  r-star-tree biased to provide spherical point distances with 4-tuple ID,x,y,z coordinates
 * */


import borg.trikeshed.lib.Series
import kotlin.io.path.createTempDirectory
import kotlin.math.*

/**a 4-tuple of ID,x,y,z coordinates*/
data class PointRecord3d(
    val
    /** 55 bit id, 9 upper bits of color info*/
    id: ULong,val name:String, val x: Double, val y: Double, val z: Double
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
fun circuitHunt(ship:Ship, start: PointRecord3d, target: PointRecord3d, fanout:Int=5): List<PointRecord3d> {
    val throttle = ship.cruiseControl(start, target)

    val throttleDistance = ship.jumpRangeForFuel(throttle)

    // create a sphere boundary function which is the distance of the (throttleDistance*fanout radius) around the
    // startpoint, translated 1/3 radius toward the target
    val sphereBoundary = fun(p: PointRecord3d): Boolean {
        val dx =start.x - p.x
        val dy =start.y - p.y
        val dz =start.z - p.z
        val distance = sqrt(dx * dx + dy * dy + dz * dz)
        return distance < throttleDistance * fanout
    }

    // pre-fetch the points within the sphere boundary
 // todo   val points = starTree.pointsInSphere(start, throttleDistance * fanout)
    TODO()
}

