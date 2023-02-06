package ed.fumes

import ed.fumes.Ship.FrameShiftDrive.FSDClass.*
import ed.fumes.Ship.FrameShiftDrive.FSDRating.*
import ed.fumes.ed.fumes.LY
import ed.fumes.ed.fumes.Tons
import kotlin.math.min
import kotlin.math.pow

data class Ship(
    val name: String,
    val unladedMass: Tons,
    val fsd: FrameShiftDrive,
    val fuelCapacity: Tons,
    val cargoCapacity: Tons,
    val fsdBooster: FSDBooster? = null,
    var fuelRemaining: Tons = fuelCapacity,
    var cargoRemaining: Tons = 0.0,
) {
    data class FrameShiftDrive(
        val fsdClass: FSDClass,
        val rating: FSDRating,
        var optimalMass: Tons,
        var maxFuelPerJump: Tons
    ) {
        enum class FSDRating(val linearConstant: Double) { A(12.0), B(10.0), C(8.0), D(10.0), E(11.0), }
        enum class FSDClass(val powerConstant: Double) {
            C2(2.00), C3(2.15), C4(2.30), C5(2.45), C6(2.60), C7(2.75), C8(
                2.90
            )
        }

        enum class BaseFSD(val fsd: FrameShiftDrive) {
            fsd2E(FrameShiftDrive(C2, E, 48.0, 0.6)),
            fsd2D(FrameShiftDrive(C2, D, 54.0, 0.6)),
            fsd2C(FrameShiftDrive(C2, C, 60.0, 0.6)),
            fsd2B(FrameShiftDrive(C2, B, 75.0, 0.8)),
            fsd2A(FrameShiftDrive(C2, A, 90.0, 0.9)),
            fsd3E(FrameShiftDrive(C3, E, 80.0, 1.2)),
            fsd3D(FrameShiftDrive(C3, D, 90.0, 1.2)),
            fsd3C(FrameShiftDrive(C3, C, 100.0, 1.2)),
            fsd3B(FrameShiftDrive(C3, B, 125.0, 1.5)),
            fsd3A(FrameShiftDrive(C3, A, 150.0, 1.8)),
            fsd4E(FrameShiftDrive(C4, E, 280.0, 2.0)),
            fsd4D(FrameShiftDrive(C4, D, 315.0, 2.0)),
            fsd4C(FrameShiftDrive(C4, C, 350.0, 2.0)),
            fsd4B(FrameShiftDrive(C4, B, 438.0, 2.5)),
            fsd4A(FrameShiftDrive(C4, A, 525.0, 3.0)),
            fsd5E(FrameShiftDrive(C5, E, 560.0, 3.3)),
            fsd5D(FrameShiftDrive(C5, D, 630.0, 3.3)),
            fsd5C(FrameShiftDrive(C5, C, 700.0, 3.3)),
            fsd5B(FrameShiftDrive(C5, B, 875.0, 4.1)),
            fsd5A(FrameShiftDrive(C5, A, 1050.0, 5.0)),
            fsd6E(FrameShiftDrive(C6, E, 960.0, 5.3)),
            fsd6D(FrameShiftDrive(C6, D, 1080.0, 5.3)),
            fsd6C(FrameShiftDrive(C6, C, 1200.0, 5.3)),
            fsd6B(FrameShiftDrive(C6, B, 1500.0, 6.6)),
            fsd6A(FrameShiftDrive(C6, A, 1800.0, 8.0)),
            fsd7E(FrameShiftDrive(C7, E, 1440.0, 8.5)),
            fsd7D(FrameShiftDrive(C7, D, 1620.0, 8.5)),
            fsd7C(FrameShiftDrive(C7, C, 1800.0, 8.5)),
            fsd7B(FrameShiftDrive(C7, B, 2250.0, 10.6)),
            fsd7A(FrameShiftDrive(C7, A, 2700.0, 12.8)),
        }

        var boostFactor = 1.0
        val linearConstant: Double get() = rating.linearConstant
        val powerConstant: Double get() = fsdClass.powerConstant
    }

    enum class FSDBooster(
        /** jump range increase in LY */
        val jumpRangeIncrease: LY
    ) {
        booster1H(4.00),
        booster2H(6.00),
        booster3H(7.75),
        booster4H(9.25),
        booster5H(10.50),
    }

    var boostFactor: Double
        get() = fsd.boostFactor
        set(value) {
            fsd.boostFactor = value
        }

    val totalMass: Tons = unladedMass + cargoRemaining + fuelRemaining

    fun jumpRangeForFuel(fuel1: Tons): LY {
        val charge: Tons = minOf(fuel1, fsd.maxFuelPerJump, fuelRemaining)
        val mass = totalMass
        val massf = fsd.optimalMass / mass
        val fuelmultiplier = fsd.linearConstant * 0.001
        val grdnBoost: LY = fsdBooster?.jumpRangeIncrease ?: 0.0

        return if (fsd.maxFuelPerJump == charge) {
            val powerf = (fsd.maxFuelPerJump / fuelmultiplier).pow(1 / fsd.powerConstant)
            val basev = powerf * massf
            (basev + grdnBoost) * boostFactor
        } else {
            val basemaxrange: LY =
                fsd.optimalMass / mass * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
            val boostfactor = basemaxrange.pow(fsd.powerConstant) / (basemaxrange + grdnBoost).pow(fsd.powerConstant)
            (charge / (boostfactor * fuelmultiplier)).pow(1 / fsd.powerConstant) * massf * boostFactor
        }
    }


    fun fuelUse(distance: LY): Double? {
        val baseMaxRange =
            (fsd.optimalMass / totalMass) * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
        val boostFactor = baseMaxRange.pow(fsd.powerConstant) / (baseMaxRange + (fsdBooster?.jumpRangeIncrease
            ?: 0.0)).pow(fsd.powerConstant)
        val d = boostFactor * fsd.linearConstant * 0.001 * ((distance / boostFactor) * totalMass / fsd.optimalMass).pow(
            fsd.powerConstant
        )
        return if (d > fsd.maxFuelPerJump) null else d
    }

    /**
     * returns the max jump range and number of hops
     */
    fun maxJumpRange(remaining: Tons = Double.MAX_VALUE, throttle: Tons = remaining): Pair<LY, Int> = copy().run {
        fuelRemaining = min(fuelCapacity, remaining)
        //loop through jumps until we run out of fuel
        var range = 0.0
        var hops = 0
        do {
            val charge = minOf(fuelRemaining, throttle, fsd.maxFuelPerJump)
            val jumpRange = jumpRangeForFuel(charge)
            assert(jumpRange > 0.0) { "jumpRange must be > 0.0" }
            fuelRemaining -= charge
            range += jumpRange
            hops++
        } while (fuelRemaining > 0.0)
        range to hops
    }

    /**
    we use the maxJumpRange function to simulate a series of jumps to account for distance and mass reduction over
    multiple hops to dial in the logarithmic fuel and linear mass-discounted jump progress
    when throttle goes down weight reduction is lower per hop but the distance log(x) higher

    @param PointRecord3d start the starting point
    @param PointRecord3d target the target point
    @param Tons fuelReserve the amount of fuel to reserve for docking and thrusters
    @return Tons the throttle setting to use to reach the target
     */
    fun cruiseControl(
        start: PointRecord3d,
        target: PointRecord3d,
        fuelReserve: Tons = fuelCapacity * .03,// reserve 3% of fuel for maneuvering
    ): Tons {
        val goalDistance = start.distanceTo(target)
        val fuelGoal = fuelRemaining - fuelReserve


        val a: Tons = min(fsd.maxFuelPerJump, fuelRemaining) //always max throttle available
        var b: Tons = a * .3 //exponentially lower cost per LY
        var dA: LY
        var dB: LY
        var slope: Double
        var intercept: Double
        var solution: Tons

        do {
            //dA,dB,slope are the results of the maxRange function for the two throttle settings
            val (dA1, _) = maxJumpRange(fuelGoal, a)
            val (dB1, _) = maxJumpRange(fuelGoal, b)
            dA = dA1
            dB = dB1

            //determine the gradient of the line between the two points
            slope = (dA - dB) / (a - b)

            //determine the intercept of the line between the two points
            intercept = dA - slope * a  //dA = slope * a + intercept

            //return the throttle setting that will give us the desired range
            solution = (goalDistance - intercept) / slope

            b = solution * .3
        } while (b > solution)

        return solution

        /**
         * unit test example:
         *
         * val ship = Ship("test", 100.0, FrameShiftDrive.BaseFSD.Fsd5A.fsd.copy(),  fuelCapacity = 20.0, cargoCapacity = 0.0 )
         * val start = PointRecord3d(0.0, 0.0, 0.0)
         * val target = PointRecord3d(100.0, 0.0, 0.0)
         * val throttle = ship.cruiseControl(start, target)
         * println(throttle)
         *
         * output:
         */
    }

    fun jumpTo(target: PointRecord3d, fuelReserve: Tons = fuelCapacity * .03): Boolean {
        val goalDistance = target.distanceTo(target)
        val fuelGoal = fuelRemaining - fuelReserve
        val throttle = cruiseControl(target, target, fuelReserve)
        val (range, hops) = maxJumpRange(fuelGoal, throttle)
        return range >= goalDistance
    }
}