package overcharged.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Unified shooting system that coordinates hood angle and shooter power
 * to eliminate circular dependencies
 */
public class ShooterSystem {
    private shooter shooter;
    private hood hood;

    // Target parameters
    private double targetDistance = 1000; // mm
    private double targetHeight = 0.70485; // meters (goal height)

    // Auto-adjust control
    private boolean autoAdjustEnabled = false;

    // Cached optimal values
    private double optimalAngle = Math.toRadians(40); // radians
    private double optimalVelocity = 6.5; // m/s

    public ShooterSystem(HardwareMap hardwareMap) {
        this.shooter = new shooter(hardwareMap);
        this.hood = new hood(hardwareMap);
    }

    /**
     * Enable/disable auto-adjustment for a target distance
     */
    public void setAutoAdjust(boolean enable, double targetDistanceMM) {
        this.autoAdjustEnabled = enable;
        this.targetDistance = targetDistanceMM;

        if (enable) {
            calculateOptimalShot();
        }
    }

    /**
     * Calculate the optimal angle and velocity for current target distance
     * This solves the projectile motion equations to find the best trajectory
     */
    private void calculateOptimalShot() {
        double distMeters = targetDistance / 1000.0;
        double h = targetHeight;

        // We want to find the angle that minimizes required velocity
        // (lower angle = flatter trajectory = more consistent)
        double bestAngle = Math.toRadians(40); // default
        double bestVelocity = Double.MAX_VALUE;

        // Try angles from 25° to 50° in small increments
        for (double angleDeg = 25; angleDeg <= 50; angleDeg += 0.5) {
            double angle = Math.toRadians(angleDeg);

            // Calculate required velocity for this angle
            // Derived from: range = (v² * sin(2θ)) / g  and  y = x*tan(θ) - (g*x²)/(2*v²*cos²(θ))
            double cosTheta = Math.cos(angle);
            double tanTheta = Math.tan(angle);

            double numerator = 9.81 * distMeters * distMeters;
            double denominator = 2.0 * cosTheta * cosTheta * (distMeters * tanTheta - h);

            if (denominator <= 0) continue; // Skip invalid solutions

            double requiredVelocity = Math.sqrt(numerator / denominator);

            // Prefer lower velocities (more controllable) and mid-range angles
            if (requiredVelocity < bestVelocity && requiredVelocity > 0.5 && requiredVelocity < 10.0) {
                bestVelocity = requiredVelocity;
                bestAngle = angle;
            }
        }

        this.optimalAngle = bestAngle;
        this.optimalVelocity = bestVelocity;
    }

    /**
     * Update both hood and shooter to optimal positions
     * Call this in your main loop
     */
    public void update() {
        if (autoAdjustEnabled) {
            // Recalculate if needed (in case distance changed)
            calculateOptimalShot();

            // Set hood to optimal angle
            float hoodPosition = hood.angToPos(optimalAngle);
            hood.setPosition(hoodPosition);

            // Set shooter to optimal power
            float motorPower = (float)(optimalVelocity / shooter.powerCoeff);
            motorPower = Math.max(0f, Math.min(1f, motorPower)); // Clamp to [0,1]
            shooter.setPowerBoth(motorPower);
        }

        // Always update shooter (in case it's using PID for velocity control)
        shooter.update();
    }

    /**
     * Manual shooting with preset positions
     */
    public void shootClose() {
        hood.setClose();
        shooter.shoot(0.7f);
    }

    public void shootMiddle() {
        hood.setMiddle();
        shooter.shoot(0.85f);
    }

    public void shootFar() {
        hood.setPosition(hood.INIT);
        shooter.shoot(1.0f);
    }

    /**
     * Stop shooting
     */
    public void stop() {
        shooter.off();
    }

    /**
     * Intake mode
     */
    public void intake() {
        shooter.intake();
    }

    // Getters for telemetry
    public double getOptimalAngleDegrees() {
        return Math.toDegrees(optimalAngle);
    }

    public double getOptimalVelocity() {
        return optimalVelocity;
    }

    public double getTargetDistance() {
        return targetDistance;
    }

    public float getCurrentHoodPosition() {
        return hood.getCurrentPos();
    }

    public float getCurrentShooterPower() {
        return shooter.getPowerT();
    }

    public void setTargetDistance(double distanceMM) {
        this.targetDistance = distanceMM;
        if (autoAdjustEnabled) {
            calculateOptimalShot();
        }
    }

    // Direct access to components if needed
    public shooter getShooter() {
        return shooter;
    }

    public hood getHood() {
        return hood;
    }
}