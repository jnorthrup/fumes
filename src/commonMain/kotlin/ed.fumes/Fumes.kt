package ed.fumes

import ed.fumes.FrameShiftDrive.FSDClass.*
import ed.fumes.FrameShiftDrive.FSDRating.*
import kotlin.math.min
import kotlin.math.pow


typealias Tons = Double
typealias LY = Double

data class FrameShiftDrive(
    val fsdClass: FSDClass,
    val rating: FSDRating,
    var optimalMass: Tons,
    var maxFuelPerJump: Tons
) {
    enum class FSDRating(val linearConstant: Double) { A(12.0), B(10.0), C(8.0), D(10.0), E(11.0), }
    enum class FSDClass(val powerConstant: Double) { C2(2.00), C3(2.15), C4(2.30), C5(2.45), C6(2.60), C7(2.75), C8(2.90) }
    enum class BaseFSD(val fsd: FrameShiftDrive) {
        Fsd2E(FrameShiftDrive(C2, E, 48.0, 0.6)),
        Fsd2D(FrameShiftDrive(C2, D, 54.0, 0.6)),
        Fsd2C(FrameShiftDrive(C2, C, 60.0, 0.6)),
        Fsd2B(FrameShiftDrive(C2, B, 75.0, 0.8)),
        Fsd2A(FrameShiftDrive(C2, A, 90.0, 0.9)),
        Fsd3E(FrameShiftDrive(C3, E, 80.0, 1.2)),
        Fsd3D(FrameShiftDrive(C3, D, 90.0, 1.2)),
        Fsd3C(FrameShiftDrive(C3, C, 100.0, 1.2)),
        Fsd3B(FrameShiftDrive(C3, B, 125.0, 1.5)),
        Fsd3A(FrameShiftDrive(C3, A, 150.0, 1.8)),
        Fsd4E(FrameShiftDrive(C4, E, 280.0, 2.0)),
        Fsd4D(FrameShiftDrive(C4, D, 315.0, 2.0)),
        Fsd4C(FrameShiftDrive(C4, C, 350.0, 2.0)),
        Fsd4B(FrameShiftDrive(C4, B, 438.0, 2.5)),
        Fsd4A(FrameShiftDrive(C4, A, 525.0, 3.0)),
        Fsd5E(FrameShiftDrive(C5, E, 560.0, 3.3)),
        Fsd5D(FrameShiftDrive(C5, D, 630.0, 3.3)),
        Fsd5C(FrameShiftDrive(C5, C, 700.0, 3.3)),
        Fsd5B(FrameShiftDrive(C5, B, 875.0, 4.1)),
        Fsd5A(FrameShiftDrive(C5, A, 1050.0, 5.0)),
        Fsd6E(FrameShiftDrive(C6, E, 960.0, 5.3)),
        Fsd6D(FrameShiftDrive(C6, D, 1080.0, 5.3)),
        Fsd6C(FrameShiftDrive(C6, C, 1200.0, 5.3)),
        Fsd6B(FrameShiftDrive(C6, B, 1500.0, 6.6)),
        Fsd6A(FrameShiftDrive(C6, A, 1800.0, 8.0)),
        Fsd7E(FrameShiftDrive(C7, E, 1440.0, 8.5)),
        Fsd7D(FrameShiftDrive(C7, D, 1620.0, 8.5)),
        Fsd7C(FrameShiftDrive(C7, C, 1800.0, 8.5)),
        Fsd7B(FrameShiftDrive(C7, B, 2250.0, 10.6)),
        Fsd7A(FrameShiftDrive(C7, A, 2700.0, 12.8)),
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


//get the fuel function
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
    var boostFactor: Double
        get() = fsd.boostFactor
        set(value) {
            fsd.boostFactor = value
        }

    val totalMass: Tons = unladedMass + cargoRemaining + fuelRemaining

    fun jumpRangeForFuel(fuel1: Tons = fuelRemaining): LY {
        val fuel = min(min(fuel1, fsd.maxFuelPerJump), fuelRemaining)
        val mass = totalMass
        val massf = fsd.optimalMass / mass
        val fuelmultiplier = fsd.linearConstant * 0.001
        val grdnBoost: LY = fsdBooster?.jumpRangeIncrease ?: 0.0

        return if (fsd.maxFuelPerJump == fuel) {
            val powerf = (fsd.maxFuelPerJump / fuelmultiplier).pow(1 / fsd.powerConstant)
            val basev = powerf * massf
            (basev + grdnBoost) * boostFactor
        } else {
            val basemaxrange: LY =
                fsd.optimalMass / mass * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
            val boostfactor = basemaxrange.pow(fsd.powerConstant) / (basemaxrange + grdnBoost).pow(fsd.powerConstant)
            (fuel / (boostfactor * fuelmultiplier)).pow(1 / fsd.powerConstant) * massf * boostFactor
        }
    }


    fun fuelUse(distance: LY): Tons {
        val baseMaxRange =
            (fsd.optimalMass / totalMass) * (fsd.maxFuelPerJump * 1000 / fsd.linearConstant).pow(1 / fsd.powerConstant)
        val boostFactor = baseMaxRange.pow(fsd.powerConstant) / (baseMaxRange + (fsdBooster?.jumpRangeIncrease
            ?: 0.0)).pow(fsd.powerConstant)
        val d = boostFactor * fsd.linearConstant * 0.001 * ((distance / boostFactor) * totalMass / fsd.optimalMass).pow(
            fsd.powerConstant
        )
        return if (d > fsd.maxFuelPerJump) -1.0 else d
    }

    fun jump(distance: LY): Boolean {
        val fuel = fuelUse(distance)
        if (fuel == -1.0 || fuel > fuelRemaining) {
            return false
        }
        fuelRemaining -= fuel
        return true
    }

    fun refuel(): Ship {
        fuelRemaining = fuelCapacity
        return this
    }

    fun maxJumpRange(remaining: Tons = Double.MAX_VALUE): LY = copy().run {
        fuelRemaining = min(fuelCapacity, remaining)
        //loop through jumps until we run out of fuel
        var range = 0.0
        do {
            val jumpRange = jumpRangeForFuel(fuelRemaining)
            fuelRemaining -= fuelUse(jumpRange)
            range += jumpRange
        } while (fuelRemaining > 0.0)
        range
    }
}