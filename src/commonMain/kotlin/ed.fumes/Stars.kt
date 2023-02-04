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

    /**  a fitness function with start point, hop_depth , a segment_length, and destination point
     *  the expected fuel is the segmentDistance being jumped in a temp ship, hop_depth times with fuel
     *  tank subtraction , the fuelError is abs(expected - actual)
     * the fitness is measured by the ((most progress gained towards the destination point) / fuelError.pow(2))
     */
    fun segmentsFitness(
        ship: Ship,
        start: PointRecord3d,
        hopDepth: Int,
        segmentDistance: LY,
        target: PointRecord3d,
        vararg attempts: Array<PointRecord3d>
    ): Double {
        val goalDistance = start.distanceTo(target)

        //iterates hopDepth times, subtracting the used fuel
        val expectedFuel: Tons = ship.fuelRemaining - (0 until hopDepth).fold(ship.copy()) { tmpShip, _ ->
            tmpShip.fuelRemaining -= tmpShip.fuelUse(segmentDistance)
            tmpShip
        }.fuelRemaining

        //each attempt needs to perform the distance of each hop in order, and sum the fuelcosts of each hop to get the attempt's fuel cost
        attempts.maxOfOrNull { attempt ->
            val attemptFuelCost: Tons = attempt.foldIndexed(ship.copy()) { index, tmpShip, point ->
                tmpShip.fuelRemaining -= tmpShip.fuelUse(
                    (if (index == 0) start else attempt[attempt.indexOf(point) - 1]).distanceTo(point)
                )
                tmpShip
            }.fuelRemaining

            //the fitness is measured by the ((most progress gained towards the destination point) / fuelError.pow(2))
            val attemptDistance =
                start.distanceTo(attempt.last()) //the distance from the start to the last point in the attempt


            val attemptProgress =
                goalDistance - attemptDistance // the distance from the destination to the last point in the attempt

            val attemptFuelError =
                abs(attemptFuelCost - (hopDepth * segmentDistance)) //  the fuelError is abs(expected - actual)

            attemptProgress / max(
                Double.MIN_VALUE,
                attemptFuelError.pow(2)
            )// the fitness is measured by the ((most progress gained towards the destination point) / fuelError.pow(2))
        } ?: 0.0 t2 expectedFuel

    }

}




