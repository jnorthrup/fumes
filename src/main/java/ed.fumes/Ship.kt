package ed.fumes

import ed.fumes.Ship.FrameShiftDrive.FSDClass.*
import ed.fumes.Ship.FrameShiftDrive.FSDRating.*
import kotlin.math.min
import kotlin.math.pow

typealias Tons = Double
typealias LY = Double

data class Ship(
    val name: String,
    val unladenMass: Tons,
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
        var optimizedMass: Tons,
        var maxFuelPerJump: Tons
    ) {
        enum class FSDRating(val linearConstant: Double) { A(12.0), B(10.0), C(8.0), D(10.0), E(11.0), }
        enum class FSDClass(val powerConstant: Double) {
            C2(2.00), C3(2.15), C4(2.30), C5(2.45), C6(2.60), C7(2.75)
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

        companion object {
            fun fromClassAndRating(clas: Int, rating: Char): FrameShiftDrive {
                val name = "fsd${clas}${rating.uppercaseChar()}"
                return BaseFSD.valueOf(name).fsd
            }
        }
    }

    enum class FSDBooster(
        /** jump range increase in LY */
        val jumpRangeIncrease: LY
    ) {
        booster1H(4.00),
        booster2H(6.00),
        booster3H(7.75),
        booster4H(9.25),
        booster5H(10.50), ;

        companion object {
            fun fromClass(clas: Int): FSDBooster {
                "booster${clas}H".let { name ->
                    return FSDBooster.valueOf(name)
                }
            }
        }
    }

    var boostFactor: Double
        get() = fsd.boostFactor
        set(value) {
            fsd.boostFactor = value
        }

    val totalMass: Tons = unladenMass + cargoRemaining + fuelRemaining

    fun jumpRangeForFuel(fuel1: Tons): LY {
        val charge: Tons = minOf(fuel1, fsd.maxFuelPerJump, fuelRemaining)
        val mass = totalMass
        val massf = fsd.optimizedMass / mass
        val fuelmultiplier = fsd.linearConstant * 0.001
        val grdnBoost: LY = fsdBooster?.jumpRangeIncrease ?: 0.0

        return if (fsd.maxFuelPerJump == charge) {
            val powerf = (fsd.maxFuelPerJump / fuelmultiplier).pow(1 / fsd.powerConstant)
            val basev = powerf * massf
            (basev + grdnBoost) * boostFactor
        } else {
            val basemaxrange: LY =
                fsd.optimizedMass / mass * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
            val boostfactor =
                basemaxrange.pow(fsd.powerConstant) / (basemaxrange + grdnBoost).pow(fsd.powerConstant)
            (charge / (boostfactor * fuelmultiplier)).pow(1 / fsd.powerConstant) * massf * boostFactor
        }
    }


    fun fuelUse(distance: LY): Tons {
        val baseMaxRange =
            (fsd.optimizedMass / totalMass) * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
        val boostFactor = baseMaxRange.pow(fsd.powerConstant) / (baseMaxRange + (fsdBooster?.jumpRangeIncrease
            ?: 0.0)).pow(fsd.powerConstant)
        val d: Tons =
            boostFactor * fsd.linearConstant * 0.001 * ((distance / boostFactor) * totalMass / fsd.optimizedMass).pow(
                fsd.powerConstant
            )
        assert(d <= fsd.maxFuelPerJump) { "where maxFuelPerJump is ${fsd.maxFuelPerJump} we attempted to use $d Tons" }
        return d
    }

    /**
     * returns the max jump range and number of hops
     */
    fun maxJumpRange(
        remaining: Tons = Double.MAX_VALUE,
        throttle: Tons = remaining,
        reserveFuel: Tons = 0.0
    ): Pair<LY, Int> = copy().run {
        fuelRemaining = min(fuelCapacity, remaining)
        //loop through jumps until we run out of fuel
        var range = 0.0
        var hops = 1
        do {
            val charge = minOf(fuelRemaining - reserveFuel, throttle, fsd.maxFuelPerJump)
            val jumpRange = jumpRangeForFuel(charge)
            assert(jumpRange > 0.0) { "jumpRange must be > 0.0" }
            fuelRemaining -= charge
            range += jumpRange
            hops++
        } while (fuelRemaining > reserveFuel)
        range to hops
    }

    fun jumpTo(target: PointRecord3d, fuelReserve: Tons = fuelCapacity * .0): Boolean {
        val goalDistance: LY = target.distanceTo(target)
        val fuelGoal: Double = fuelRemaining - fuelReserve
        val throttle: Triple<Tons, Double, Int> = cruiseControl(target, target, fuelReserve)
        val (range: LY, hops: Int) = maxJumpRange(fuelGoal, throttle.first)
        return range >= goalDistance
    }


    fun cruiseControl(
        start: PointRecord3d,
        target: PointRecord3d,
        /** serves as the  fudge factor for distance and also the default minimum fuel headroom fraction*/
        tolerance: LY = 0.03,
        /** reserve subtracted from capacity up front*/
        fuelReserve: Tons = fuelCapacity * tolerance// reserve 3% of fuel for maneuvering
    ): Triple<Tons, Double, Int> {
        val goalRange: LY = start.distanceTo(target)
        val fuelGoal: Tons = fuelRemaining - fuelReserve
        val minFuel: Tons = fuelGoal - (fuelGoal * tolerance)

        val maxFuelPerJump = fsd.maxFuelPerJump
        var curThrottle: Tons = maxFuelPerJump

        var counter = 1
        var hops: Double
        //we need to make multiple jumps
        do {
            require(curThrottle <= maxFuelPerJump) { "curThrottle must be <= maxFuelPerJump but max is $maxFuelPerJump and curThrottle is $curThrottle" }
            /** this accounts for totalMass mass alpha hop which will be affected by fuel depletion weight adjustment after the jump */
            val curHopDistance = jumpRangeForFuel(curThrottle)

            //divide the goal range by the curHopDistance to get the number of hops curThrottle produces
            hops = goalRange / curHopDistance

            //obtain fuel*hops
            val proposedFuel: Tons = fuelUse(curHopDistance) * hops

            if (hops <= 1.0) break

            if (proposedFuel in minFuel..fuelGoal) break //we are within tolerance

            //we need to adjust the throttle
            curThrottle = if (proposedFuel > fuelGoal) (curThrottle) / 2.0
            else {
                // increase the  curHopDistance by the divisor of the
                // fuelGoal and proposedFuel to get the new throttle
                val b = curThrottle * fuelGoal / proposedFuel
                if (b <= maxFuelPerJump) b
                else arrayOf(curThrottle, maxFuelPerJump).average()
            }
            counter++
        } while (counter < 1000)
        return Triple(curThrottle, hops, counter)
    }
}

