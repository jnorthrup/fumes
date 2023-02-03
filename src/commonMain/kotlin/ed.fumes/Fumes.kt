package ed.fumes.ed.fumes

import ed.fumes.ed.fumes.FrameShiftDrive.FSDClass.*
import ed.fumes.ed.fumes.FrameShiftDrive.FSDRating.*
import kotlin.jvm.JvmStatic
import kotlin.math.min
import kotlin.math.pow


typealias Tons = Double
typealias LY = Double

data class FrameShiftDrive(
    val rating: FSDRating,
    val fsdClass: FSDClass,
    var optimalMass: Tons,
    var maxFuelPerJump: Tons
) {
    enum class FSDRating(val linearConstant: Double) { A(12.0), B(10.0), C(8.0), D(10.0), E(11.0), }
    enum class FSDClass(val powerConstant: Double) { C2(2.00), C3(2.15), C4(2.30), C5(2.45), C6(2.60), C7(2.75), C8(2.90) }

    val linearConstant: Double get() = rating.linearConstant
    val powerConstant: Double get() = fsdClass.powerConstant
}

enum class BaseFSD(val fsd: FrameShiftDrive) {
    base2A(FrameShiftDrive(A, C2, 2.50, 0.60)),
    base2B(FrameShiftDrive(FrameShiftDrive.FSDRating.B, C2, 1.00, 0.60)),
    base2C(FrameShiftDrive(C, C2, 2.50, 0.60)),
    base2D(FrameShiftDrive(D, C2, 4.00, 0.80)),
    base2E(FrameShiftDrive(E, C2, 2.50, 0.90)),
    base3A(FrameShiftDrive(A, C3, 5.00, 1.20)),
    base3B(FrameShiftDrive(B, C3, 2.00, 1.20)),
    base3C(FrameShiftDrive(C, C3, 5.00, 1.20)),
    base3D(FrameShiftDrive(D, C3, 8.00, 1.50)),
    base3E(FrameShiftDrive(E, C3, 5.00, 1.80)),
    base4A(FrameShiftDrive(A, C4, 10.00, 2.00)),
    base4B(FrameShiftDrive(B, C4, 4.00, 2.00)),
    base4C(FrameShiftDrive(C, C4, 5.00, 2.00)),
    base4D(FrameShiftDrive(D, C4, 16.00, 2.50)),
    base4E(FrameShiftDrive(E, C4, 10.00, 3.00)),
    base5A(FrameShiftDrive(A, C5, 20.00, 3.30)),
    base5B(FrameShiftDrive(B, C5, 8.00, 3.30)),
    base5C(FrameShiftDrive(C, C5, 10.00, 3.30)),
    base5D(FrameShiftDrive(D, C5, 32.00, 4.10)),
    base5E(FrameShiftDrive(E, C5, 20.00, 5.00)),
    base6A(FrameShiftDrive(A, C6, 40.00, 5.30)),
    base6B(FrameShiftDrive(B, C6, 16.00, 5.30)),
    base6C(FrameShiftDrive(C, C6, 40.00, 5.30)),
    base6D(FrameShiftDrive(D, C6, 64.00, 6.60)),
    base6E(FrameShiftDrive(E, C6, 40.00, 8.00)),
    base7A(FrameShiftDrive(A, C7, 80.00, 8.50)),
    base7B(FrameShiftDrive(B, C7, 32.00, 8.50)),
    base7C(FrameShiftDrive(C, C7, 80.00, 8.50)),
    base7D(FrameShiftDrive(D, C7, 128.00, 10.60)),
    base7E(FrameShiftDrive(E, C7, 80.00, 12.80)),
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


data class Ship(
    val name: String,
    val unladedMass: Tons,
    val fsd: FrameShiftDrive,
    val fuelCapacity: Tons,
    val cargoCapacity: Tons,
    val fsdBooster: FSDBooster? = null,
    var fuelRemaining: Tons = fuelCapacity,
    var cargoRemaining: Tons = 0.0,
    var boostFactor: Double = 1.0,
) {
    val totalMass: Tons = unladedMass + cargoRemaining + fuelRemaining

    /**

    # Ship Jump calculations

    FSD Fuel consumption ([Elite: Dangerous Wiki](https://elite-dangerous.fandom.com/wiki/Frame_Shift_Drive#Hyperspace_Fuel_Equation)):

    Solving for $dist$ gives the jump range (in Ly) for a given amount of fuel (in tons)

    Assuming $f\_{max}$ tons of available fuel gives us the maximum jump range for a single jump

    the FSDBooster increases the maximum jump range by $B_g$ light years using the same amount of fuel at max jump range.  lower distances will use less fuel according to the equation below.

    $$dist = \frac{boost \cdot m\_{opt} \cdot \left(\frac{1000000.0 \cdot \left(\frac{boost^{2} \cdot m\_{opt} \cdot \left(\frac{1000.0 \cdot \min\left(f\_{max}, m\_{fuel}\right)}{l}\right)^{\frac{1}{p}}}{B\_{g} \cdot \left(m\_{fuel} + m\_{ship}\right) + boost^{2} \cdot m\_{opt} \cdot \left(\frac{1000.0 \cdot \min\left(f\_{max}, m\_{fuel}\right)}{l}\right)^{\frac{1}{p}}}\right)^{- p} \cdot \min\left(f\_{max}, m\_{fuel}\right)}{l^{2}}\right)^{\frac{1}{p}}}{m\_{fuel} + m\_{ship}}$$

    Where:

     * $Fuel$ is the fuel needed to jump (in tons)
     * $l$ is the linear constant of your FSD (depends on the rating)
     * $p$ is the power constant of your FSD (depends on the class)
     * $m\_{ship}$ is the mass of your ship (including cargo but not fuel)
     * $m\_{fuel}$ is the amount of fuel in tons currently stored in your tanks
     * $m\_{opt}$ is the optimized mass of your FSD (in tons)
     * $f\_{max}$ is the maximum amount of fuel your FSD can use per jump
     * $boost$ is the "boost factor" of your FSD (1.0 when jumping normally, 1.5 when supercharged by a white dwarf, 4.0 for a neutron star, etc)
     * $dist$ is the distance you can jump with a given fuel amount
     * $dist\_{max}$ is the maximum distance you can jump (when $m\_{fuel}=f\_{max}$)
     * $B\_{g}$ is the amount of Ly added by your Guardian FSD Booster
     * $e\_{fuel}$ is the efficiency increase added by the Guardian FSD Booster
     *
     * restating the above as kotlin with the current file's classes we get:
     */
    fun jumpRangeForFuel(fuelToSpend: Tons = min(fsd.maxFuelPerJump, fuelRemaining)): LY {
        val fsd = fsd
        val booster = fsdBooster
        val mOpt = fsd.optimalMass
        val fMax = fsd.maxFuelPerJump
        val boost = boostFactor
        val l = fsd.linearConstant
        val p = fsd.powerConstant
        val bg: LY = booster?.jumpRangeIncrease ?: 0.0
        val pInverse = 1.0 / p
        val fuelK = 1000.0 * min(fMax, fuelToSpend)
        val l2 = l * l
        val bp2mOptfklpinv = boost.pow(2) * mOpt * (fuelK / l).pow(pInverse)
        return ((boost * mOpt * (1000000.0 * (
                bp2mOptfklpinv /
                        (bg * totalMass + bp2mOptfklpinv)
                ).pow(-p) * min(fMax, fuelToSpend)) / l2).pow(pInverse)) / (totalMass)
    }

    fun fuelCostForDistance(distance: LY): Tons {
        val fsd = fsd
        val booster = fsdBooster
        val mOpt = fsd.optimalMass
        val fMax = fsd.maxFuelPerJump
        val boost = boostFactor
        val l = fsd.linearConstant
        val p = fsd.powerConstant
        val bg: LY = booster?.jumpRangeIncrease ?: 0.0
        val massK = 1000.0 * totalMass
        val pInverse = 1.0 / p
        val bp2MoptmKlpwInv = boost.pow(2) * mOpt * (massK / l).pow(pInverse)
        return bp2MoptmKlpwInv / (
                1_000_000.0 * (
                        boost * mOpt * (
                                1_000_000.0 * (
                                        bp2MoptmKlpwInv /
                                                (bg * totalMass + bp2MoptmKlpwInv)
                                        ).pow(-p) * totalMass
                                ) / l.pow(2)
                        ).pow(pInverse) / distance
                )
    }
}