package  ed.fumes

import borg.trikeshed.lib.logDebug
import ed.fumes.Ship.FSDBooster.booster5H
import ed.fumes.Ship.FrameShiftDrive.BaseFSD
import ed.fumes.Ship.FrameShiftDrive.BaseFSD.fsd2E
import ed.fumes.Ship.FrameShiftDrive.BaseFSD.fsd6E
import java.lang.Thread.sleep
import kotlin.test.*


class TestShip {        //   SideWinder ship with no booster
    val sidey = Ship("SideWinder", 43.2, fsd2E.fsd, 2.0, 4.0) // no booster

    // anaconda with a 5H booster
    val anaconda =
        Ship("Anaconda", 1066.4, fsd6E.fsd, 32.0, 114.0, booster5H) // 5H booster

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

        val ships = listOf(sidey, anaconda)
        val startPointerInfo = PointRecord3d(1.toULong(), name = "Sol", 0.0, 0.0, 0.0)

        ships.forEach { ship ->

            println("\n for ship: $ship: \n")

            //show ship default jumprange
            val jumpRange = ship.jumpRangeForFuel(ship.fsd.maxFuelPerJump)
            println("default jump range: $jumpRange LY and max jump range: ${ship.maxJumpRange()} LY")

            //show fuel cost of 2 LY jump
            val jump2 = ship.fuelUse(2.0)
            println("jump 2 LY: uses $jump2 tons")

            listOf(
                PointRecord3d(2.toULong(), name = "LAWD100", 100.0, 100.0, 100.0),
                PointRecord3d(2.toULong(), name = "LAWD1000", 1000.0, 1000.0, 1000.0)
            ).forEach { target ->

                val distanceTo = target.distanceTo(startPointerInfo)
                println("for the target: $target at distance: $distanceTo LY")


                //showCruiseControl
                val cruiseControl = ship.cruiseControl(startPointerInfo, target, .03)
                val jumpRangeForFuel = ship.jumpRangeForFuel(cruiseControl)
                println("cruiseControl throttle is: $cruiseControl and max hop distance is $jumpRangeForFuel LY giving estimated hops of ${(distanceTo / jumpRangeForFuel).toInt()}")
            }
        }
    }
}

