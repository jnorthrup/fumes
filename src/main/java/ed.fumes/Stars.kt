package ed.fumes


/**this file holds all the classes and mixins for a
 *  r-star-tree biased to provide spherical point distances with 4-tuple ID,x,y,z coordinates
 * */


import kotlin.math.*


/**a 4-tuple of ID,x,y,z coordinates*/
data class PointRecord3d(
    val
    /** 55 bit id, 9 upper bits of color info*/
    id: ULong, val x: Double, val y: Double, val z: Double
) {
    /**distance from this point to another point*/
    fun distanceTo(other: PointRecord3d): LY {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}
