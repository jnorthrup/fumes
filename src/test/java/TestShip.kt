package  ed.fumes

import ed.fumes.Ship.FSDBooster.booster5H
import ed.fumes.Ship.FrameShiftDrive.BaseFSD
import ed.fumes.Ship.FrameShiftDrive.BaseFSD.fsd2E
import ed.fumes.Ship.FrameShiftDrive.BaseFSD.fsd6E
import org.junit.Test

class TestShip {
    @Test
    fun testShip1() {
        //   SideWinder ship with no booster
        val sidey = Ship("SideWinder", 43.2, fsd2E.fsd, 2.0, 4.0) // no booster

        // anaconda with a 5H booster
        val anaconda =
            Ship("Anaconda", 1066.4, fsd6E.fsd, 32.0, 114.0, booster5H) // 5H booster

        listOf(sidey, anaconda).forEach {
            println(it)

            //            jump 2 LY
            val jump2 = it.fuelUse(2.0)
            println("jump 2 LY: uses $jump2 tons")

            //            jump 10 LY
            val jump10 = it.fuelUse(10.0)
            println("jump 10 LY: uses $jump10 tons")

            //jump with .2 tons of fuel
            val jumpRange2 = it.jumpRangeForFuel(0.20)
            println(" attempted jump with .2 tons of fuel: $jumpRange2 LY")

            //jump with full fuel empty cargo
            val jumpRangeFull = it.jumpRangeForFuel(it.fsd.maxFuelPerJump)
            println("max jump with full fuel empty cargo: $jumpRangeFull LY")

            //show maxrange with a  copy of the ship
            val maxRange = it.copy().maxJumpRange()
            println("max range: $maxRange LY")
        }
    }

    @Test
    fun testCruiseControl() {
        val ship = Ship("test", 100.0, BaseFSD.fsd5A.fsd.copy(), 20.0, 0.0)
        val start = PointRecord3d(1.toULong(), 0.0, 0.0, 0.0)
        val target = PointRecord3d(2.toULong(), 100.0, 100.0, 100.0)
        val throttle = ship.cruiseControl(start, target)
        println("ship: $ship")
        println("throttle for target $throttle")
    }
}