package misc

import borg.trikeshed.lib.*
import kotlin.math.*

/*
the requirements for implementing a function in Kotlin that finds points within a given hop radius of a line segment
between two 3D points: The function should take as input a pair of 3D points representing the endpoints of the line
segment, a hop radius, and sorted lists of x, y, and z coordinates represented as
Triple<List<Pair<Double,Int>>,List<Pair<Double,Int>>,List<Pair<Double,Int>>>, where each list contains pairs of
coordinate values and their corresponding point IDs. The function should use binary search to find the range of
points within the bounding box defined by the coordinates of the line segment endpoints plus/minus the hop radius.
The function should iterate through this range of points and calculate their distance to the line segment using the
formula for the shortest distance from a point to a line in 3D space. If the calculated distance is less than or
equal to the hop radius, it should add that point’s ID to a list of neighboring point IDs. The function should return
this list of neighboring point IDs in the order of A to B along the line segment.
return Pair</*Radius:*/Double,List<Pair</*id:*/Int, Triple<Double,Double,Double>>>> with first and last being A,B and
add a cache of prior returns passed in that will be used preferentially to determine intersection
and overlapping stars as a resource to speed the search along.
*/
typealias Triple<A, B, C> = Join3<A, B, C>
typealias Point = Triplet<Double>
typealias Pair<F, S> = Join<F, S> // from borg.trikeshed.lib
typealias LineSegment = Twin<Point>


// A data class to represent a 3D point with an ID
data class Point3D(val id: Int, val x: Double, val y: Double, val z: Double)

// A function to calculate the shortest distance from a point to a line in 3D space
// Based on https://stackoverflow.com/questions/50727961/shortest-distance-between-a-point-and-a-line-in-3-d-space
fun distanceToLine(point: Point3D, lineStart: Point3D, lineEnd: Point3D): Double {
    // Find the vector from lineStart to lineEnd
    val lineVector = Point3D(
        0,
        lineEnd.x - lineStart.x,
        lineEnd.y - lineStart.y,
        lineEnd.z - lineStart.z
    )

    // Find the parameter t that minimizes the distance
    val t = ((point.x - lineStart.x) * lineVector.x +
            (point.y - lineStart.y) * lineVector.y +
            (point.z - lineStart.z) * lineVector.z) /
            (lineVector.x * lineVector.x +
                    lineVector.y * lineVector.y +
                    lineVector.z * lineVector.z)

    // Find the closest point on the line
    val closestPoint = Point3D(
        0,
        t * lineVector.x + lineStart.x,
        t * lineVector.y + lineStart.y,
        t * lineVector.z + lineStart.z
    )

    // Return the distance between point and closestPoint
    return sqrt(
        (point.x - closestPoint.x) * (point.x - closestPoint.x) +
                (point.y - closestPoint.y) * (point.y - closestPoint.y) +
                (point.z - closestPoint.z) * (point.z - closestPoint.z)
    )
}

// A function to find points within a given hop radius of a
//line segment between two 3D points
fun findPointsWithinHopRadius(
    lineStart: Point3D,
    lineEnd: Point3D,
    hopRadius: Double,
    sortedXs: List<Pair<Double, Int>>,
    sortedYs: List<Pair<Double, Int>>,
    sortedZs: List<Pair<Double, Int>>,
): Pair<Double, List<Pair<Int, Triple<Double, Double, Double>>>> {

    // Initialize an empty list of neighboring point IDs
    val neighborIDs = mutableListOf<Pair<Int, Triple<Double, Double, Double>>>()

    // Find the minimum and maximum coordinates of the bounding box
    //defined by the endpoints of the
    //line segment plus/minus the hop radius
    val minX = min(lineStart.x, lineEnd.x) - hopRadius
    val maxX = max(lineStart.x, lineEnd.x) + hopRadius
    val minY = min(lineStart.y, lineEnd.y) - hopRadius
    val maxY = max(lineStart.y, lineEnd.y) + hopRadius
    val minZ = min(lineStart.z, lineEnd.z) - hopRadius
    val maxZ = max(lineStart.z, lineEnd.z) + hopRadius

    // Use binary search to find the range of points within
    //the bounding box in each dimension

    fun binarySearch(list: List<Pair<Double, Int>>, value: Double): Int {
        var low = 0
        var high = list.size - 1
        while (low <= high) {
            val mid = (low + high) / 2
            if (list[mid].first == value) {
                return mid
            } else if (list[mid].first < value) {
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return low //returning insertion point if not found.
    }

    val startX = binarySearch(sortedXs, minX)
    val endX = binarySearch(sortedXs, maxX)
    val startY = binarySearch(sortedYs, minY)
    val endY = binarySearch(sortedYs, maxY)
    val startZ = binarySearch(sortedZs, minZ)
    val endZ = binarySearch(sortedZs, maxZ)

    // Create a map from point IDs to their coordinates for easy lookup

    fun createMapFromList(list: List<Pair<Double, Int>>): Map<Int, Double> {
        val map = mutableMapOf<Int, Double>()
        for (pair in list) {
            map[pair.second] = pair.first
        }
        return map
    }

    val xMap = createMapFromList(sortedXs)
    val yMap = createMapFromList(sortedYs)
    val zMap = createMapFromList(sortedZs)

    // Iterate through the range of points and check their distance to
    //the line segment
    for (i in startX..endX) {
        for (j in startY..endY) {
            for (k in startZ..endZ) {
                // Get the point ID and coordinates from the maps
                val pointID = sortedXs[i].second
                val x = xMap[pointID]!!
                val y = yMap[pointID]!!
                val z = zMap[pointID]!!

                // Create a Point3D object
                val point = Point3D(pointID, x, y, z)

                // Calculate the distance to the line segment
                val distance = distanceToLine(point, lineStart, lineEnd)

                // If the distance is less than or equal to
                //the hop radius, add the point ID to
                //the list of neighbors
                if (distance <= hopRadius) {
                    neighborIDs.add(Pair(pointID, Triple(x, y, z)))
                }
            }
        }
    }

    // Sort the list of neighbors by their order along
    //the line segment from A to B
    fun comparePoints(
        p1: Pair<Int, Triple<Double, Double, Double>>,
        p2: Pair<Int, Triple<Double, Double, Double>>,
    ): Int {
        // Find the vector from lineStart to lineEnd
        val lineVector = Point3D(
            0,
            lineEnd.x - lineStart.x,
            lineEnd.y - lineStart.y,
            lineEnd.z - lineStart.z
        )

        // Find the parameter t for each point on
        //the projection onto the line
        val t1 = (
                (p1.second.first - lineStart.x) * lineVector.x +
                        (p1.second.second - lineStart.y) * lineVector.y +
                        (p1.second.third - lineStart.z) * lineVector.z) /
                (lineVector.x * lineVector.x +
                        lineVector.y * lineVector.y +
                        lineVector.z * lineVector.z)

        val t2 = ((p2.second.first - lineStart.x) * lineVector.x +
                (p2.second.second - lineStart.y) * lineVector.y +
                (p2.second.third - lineStart.z) * lineVector.z) /
                (lineVector.x * lineVector.x +
                        lineVector.y * lineVector.y +
                        lineVector.z * lineVector.z)

        // Compare t1 and t2
        return when {
            t1 < t2 -> -1
            t1 > t2 -> 1
            else -> 0
        }
    }

    neighborIDs.sortWith(::comparePoints)

    return Pair(hopRadius, neighborIDs)
}


// A function to find the point of intersection of two lines in 3D space
// Based on https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
fun intersectLines(
    lineStart1: Point3D,
    lineEnd1: Point3D,
    lineStart2: Point3D,
    lineEnd2: Point3D,
): Point3D? {
    // Find the coefficients of the equations of the lines
    val a1 = lineEnd1.y - lineStart1.y
    val b1 = lineStart1.x - lineEnd1.x
    val c1 = a1 * lineStart1.x + b1 * lineStart1.y

    val a2 = lineEnd2.y - lineStart2.y
    val b2 = lineStart2.x - lineEnd2.x
    val c2 = a2 * lineStart2.x + b2 * lineStart2.y

    // Find the determinant of the coefficient matrix
    val det = a1 * b2 - a2 * b1

    // If determinant is zero, lines are parallel or coincident
    if (det == 0.0) {
        return null
    }

    // Otherwise, find the point of intersection using Cramer's rule
    else {
        val x = (b2 * c1 - b1 * c2) / det
        val y = (a1 * c2 - a2 * c1) / det

        // Assume z coordinate is zero for simplicity
        return Point3D(0, x, y, 0.0)
    }
}

// A function to find the nearest points of two
//line segments in 3D space and their distance

fun nearestPoints(
    lineStart1: Point3D,
    lineEnd1: Point3D,
    lineStart2: Point3D,
    lineEnd2: Point3D,
): Triple<Point3D?, Point3D?, Double?> {

    // Initialize variables to store
    //the nearest points and their distance

    val pointOnLineSegmentOne: Point3D? = null
    val pointOnLineSegmentTwo: Point3D? = null
    val distanceBetweenPoints: Double? = null

    // Find the point of intersection
    //of two lines passing through
    //the endpoints of each segment

    val intersectionPoint = intersectLines(lineStart1, lineEnd1, lineStart2, lineEnd2)

    // If there is an intersection point,
    //check if it lies on both segments
    if (intersectionPoint != null) {
        // Find the parameter s for
        //the intersection point on L1
        val s = ((intersectionPoint.x - lineStart1.x) * (lineEnd1.x - lineStart1.x) +
                (intersectionPoint.y - lineStart1.y) * (lineEnd1.y - lineStart1.y) +
                (intersectionPoint.z - lineStart1.z) * (lineEnd1.z - lineStart1.z)) /
                ((lineEnd1.x - lineStart1.x) * (lineEnd1.x - lineStart1.x) +
                        (lineEnd1.y - lineStart1.y) * (lineEnd1.y - lineStart1.y) +
                        (lineEnd1.z - lineStart1.z) * (lineEnd1.z - lineStart1.z))

        // Find the parameter t for
        //the intersection point on L2
        val t = ((intersectionPoint.x - lineStart2.x) * (lineEnd2.x - lineStart2.x) +
                (intersectionPoint.y - lineStart2.y) * (lineEnd2.y - lineStart2.y) +
                (intersectionPoint.z - lineStart2.z) * (lineEnd2.z - lineStart2.z)) /
                ((lineEnd2.x - lineStart2.x) * (lineEnd2.x - lineStart2.x) +
                        (lineEnd2.y - lineStart2.y) * (lineEnd2.y - 0.0 - lineStart2.y) +
                        (lineEnd2.z - lineStart2.z) * (lineEnd2.z - lineStart2.z))

    }
    return Triple(
        pointOnLineSegmentOne,
        pointOnLineSegmentTwo,
        distanceBetweenPoints
    )

}
// A function to find the overlap of radius 1 and 2 respectively

fun findOverlapRadius(
    pointOnLineSegmentOne: Point3D?,
    pointOnLineSegmentTwo: Point3D?,
    distanceBetweenPoints: Double?,
    radiusOne: Double,
    radiusTwo: Double,
): Double {

    // Initialize a variable to store
    //the overlap radius
    val overlapRadius: Double = 0.0

    // If there are no nearest points,
    //there is no overlap
    if (pointOnLineSegmentOne == null || pointOnLineSegmentTwo == null || distanceBetweenPoints == null) return overlapRadius

    // Otherwise, check if
    //the distance between points is less than
    //the sum of radii
    else if (distanceBetweenPoints < radiusOne + radiusTwo) {
        // Find the angle subtended by each point at
        //the center of its circle using cosine rule
        val angleOne = acos(
            (radiusOne * radiusOne + distanceBetweenPoints * distanceBetweenPoints -
                    radiusTwo * radiusTwo) / (distanceBetweenPoints * radiusOne * radiusOne)
        )
        val angleTwo = acos(
            (radiusTwo * radiusTwo + distanceBetweenPoints * distanceBetweenPoints -
                    radiusOne * radiusOne) / (distanceBetweenPoints * radiusTwo * radiusTwo)
        )

        // Find the area of each sector formed by
        //the angle and the circle
        val areaSectorOne = angleOne / 360.0 * PI * radiusOne * radiusOne
        val areaSectorTwo = angleTwo / 360.0 * PI * radiusTwo * radiusTwo

        // Find the area of each triangle formed by
        //the points and their circle centers
        val areaTriangleOne = sqrt(radiusOne * (sin(angleOne / 180.0 * PI)) * distanceBetweenPoints)
        val areaTriangleTwo = sqrt(radiusTwo * (sin(angleTwo / 180.0 * PI)) * distanceBetweenPoints)

        // Find the area of overlap by subtracting
        //the triangles from sectors
        val areaOverlap = areaSectorOne + areaSectorTwo -
                areaTriangleOne - areaTriangleTwo

        return sqrt(areaOverlap / PI)

    } else {
        return overlapRadius
    }
}