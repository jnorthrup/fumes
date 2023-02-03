package  ed.fumes.ed.fumes

import ed.fumes.ed.fumes.FSDBooster
import ed.fumes.ed.fumes.Ship
import kotlin.test.*

class TestShip {
    @Test
    fun testShip1() {

        // show me a SideWinder ship with no booster
        val sidey = Ship("SideWinder", 43.2, BaseFSD.base2E.fsd.copy(), 2.0, 4.0)

        // anaconda with a 5H booster
        val anaconda = Ship("Anaconda", 1066.4, BaseFSD.base6E.fsd.copy(), 32.0, 114.0, FSDBooster.booster5H)

        // show me the jump range for the sidey with 2 tons of fuel
        println("Sidey jump range: ${sidey.jumpRangeForFuel(2.0)}")

        // show me the jump range for the anaconda with 32 tons of fuel
        println("Anaconda jump range: ${anaconda.jumpRangeForFuel(32.0)}")

        // show me the fuel cost for a 1000 ly jump in the sidey
        println("Sidey fuel cost for 2 ly jump: ${sidey.fuelCostForDistance(2.0)}")

        // show me the fuel cost for a 1000 ly jump in the anaconda
        println("Anaconda fuel cost for 2 ly jump: ${anaconda.fuelCostForDistance(2.0)}")
    }}
