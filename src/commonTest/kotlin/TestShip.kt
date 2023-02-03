package  ed.fumes.ed.fumes

import ed.fumes.ed.fumes.Ship
import kotlin.test.*

class TestShip {
    @Test
    fun testShip1() {
        //   SideWinder ship with no booster
        val sidey = Ship("SideWinder", 43.2, BaseFSD.fsd2E.fsd.copy(), 2.0, 4.0)

        // anaconda with a 5H booster
        val anaconda = Ship("Anaconda", 1066.4, BaseFSD.fsd6E.fsd.copy(), 32.0, 114.0, FSDBooster.booster5H)

        listOf(sidey, anaconda).forEach {
            println(it)

            //            jump 2 LY
            val jump2 = it.fuelUse(2.0)
            println("jump 2 LY: uses $jump2 tons")

            //            jump 10 LY
            val jump10 = it.fuelUse(10.0)
            println("jump 10 LY: uses $jump10 tons")

            //jump with 2 tons of fuel
            val jumpRange2 = it.jumpRangeForFuel(2.0)
            println(" attempted jump with 2 tons of fuel: $jumpRange2 LY")

            //jump with full fuel empty cargo
            val jumpRangeFull = it.jumpRangeForFuel(it.fuelCapacity)
            println("jump with full fuel empty cargo: $jumpRangeFull LY")


            //show maxrange with a  copy of the ship
            val maxRange = it.copy().maxJumpRange()
            println("max range: $maxRange LY")
        }
    }
}