package com.aeropropeller.addon;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * All tunable numbers live in the server TOML config so server owners can rebalance
 * the addon without rebuilding it.
 */
public final class AddonConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.DoubleValue STRESS_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue THRUST_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue AIRFLOW_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue NORMAL_MAX_TILT_DEGREES;
    public static final ModConfigSpec.DoubleValue STABLE_MAX_TILT_DEGREES;
    public static final ModConfigSpec.DoubleValue GYRO_MAX_TILT_DEGREES;
    public static final ModConfigSpec.DoubleValue MANUAL_GYRO_MAX_TILT_DEGREES;
    public static final ModConfigSpec.DoubleValue VELOCITY_COMPENSATION_GAIN;
    public static final ModConfigSpec.DoubleValue ANTI_SWAY_DAMPING;
    public static final ModConfigSpec.DoubleValue TILT_LERP_SPEED;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment(
                "Integrated propeller bearings",
                "Stress demand = input RPM * (4 + blade count * blade level) * stressMultiplier."
        ).push("propeller");

        STRESS_MULTIPLIER = builder
                .comment("Multiplier applied to the stress demand. Vanilla Aeronautics-like default is 1.0.")
                .defineInRange("stressMultiplier", 1.0D, 0.1D, 4.0D);
        THRUST_MULTIPLIER = builder
                .comment("Global multiplier for the thrust produced by both integrated bearings.")
                .defineInRange("thrustMultiplier", 1.0D, 0.1D, 8.0D);
        AIRFLOW_MULTIPLIER = builder
                .comment("Global multiplier for the airflow produced by both integrated bearings.")
                .defineInRange("airflowMultiplier", 1.0D, 0.1D, 8.0D);

        builder.pop();

        builder.comment("Gyroscopic propeller bearing tilt limits and response")
                .push("gyroscopic");

        NORMAL_MAX_TILT_DEGREES = builder
                .comment("Maximum angle between the shaft axis and the propeller axis in normal mode.")
                .defineInRange("normalMaxTiltDegrees", 12.0D, 2.0D, 20.0D);
        STABLE_MAX_TILT_DEGREES = builder
                .comment("Maximum angle between the shaft axis and the propeller axis in stable mode.")
                .defineInRange("stableMaxTiltDegrees", 12.0D, 2.0D, 20.0D);
        GYRO_MAX_TILT_DEGREES = builder
                .comment("Maximum angle used by wireless redstone steering in gyroscope mode.")
                .defineInRange("gyroMaxTiltDegrees", 12.0D, 2.0D, 20.0D);
        MANUAL_GYRO_MAX_TILT_DEGREES = builder
                .comment("Maximum angle used by wireless redstone steering in manual gyroscope mode.")
                .defineInRange("manualGyroMaxTiltDegrees", 12.0D, 2.0D, 20.0D);
        VELOCITY_COMPENSATION_GAIN = builder
                .comment("Gain converting lateral speed into counter-tilt.")
                .defineInRange("velocityCompensationGain", 0.75D, 0.01D, 10.0D);
        ANTI_SWAY_DAMPING = builder
                .comment("Angular velocity damping per second for stable/gyroscope mode.")
                .defineInRange("antiSwayDamping", 2.0D, 0.1D, 20.0D);
        TILT_LERP_SPEED = builder
                .comment("Maximum gyroscopic tilt step per tick (Aeronautics uses 0.05).")
                .defineInRange("tiltLerpSpeed", 0.05D, 0.01D, 1.0D);

        builder.pop();

        SPEC = builder.build();
    }

    private AddonConfig() {
    }

    public static float stressMultiplier() {
        return STRESS_MULTIPLIER.get().floatValue();
    }

    public static float thrustMultiplier() {
        return THRUST_MULTIPLIER.get().floatValue();
    }

    public static float airflowMultiplier() {
        return AIRFLOW_MULTIPLIER.get().floatValue();
    }

    public static float normalMaxTiltDegrees() {
        return NORMAL_MAX_TILT_DEGREES.get().floatValue();
    }

    public static float stableMaxTiltDegrees() {
        return STABLE_MAX_TILT_DEGREES.get().floatValue();
    }

    public static float gyroMaxTiltDegrees() {
        return GYRO_MAX_TILT_DEGREES.get().floatValue();
    }

    public static float manualGyroMaxTiltDegrees() {
        return MANUAL_GYRO_MAX_TILT_DEGREES.get().floatValue();
    }

    public static float velocityCompensationGain() {
        return VELOCITY_COMPENSATION_GAIN.get().floatValue();
    }

    public static float antiSwayDamping() {
        return ANTI_SWAY_DAMPING.get().floatValue();
    }

    public static float tiltLerpSpeed() {
        return TILT_LERP_SPEED.get().floatValue();
    }
}
