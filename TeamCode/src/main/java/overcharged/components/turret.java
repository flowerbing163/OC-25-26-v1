package overcharged.components;

import static overcharged.config.RobotConstants.TAG_H;
import static overcharged.config.RobotConstants.TAG_SL;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.ArrayList;
import java.util.List;

import overcharged.config.RobotConstants;

public class turret {
    public final OcMotorEx turret;

    public float kp = 0.04f;
    public double start;

    public static double p = 18;
    public static double i = 0.0002;
    public static double d = 0.0005;
    public static double f = 0.003;

    private boolean useSquID = false;
    private double target = 0;
    private float multiplier = 1f;

    private double lastError = 0;
    private double integral = 0;
    private long lastTime = System.nanoTime();
    private double derivativePrev = 0;

    public turret(HardwareMap hardwareMap) {
        turret = new OcMotorEx(hardwareMap, "turret", DcMotor.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
        start = turret.getCurrentPosition();
    }

    public float getSquid() {
        double error = target - getCurrentPosition();
        double power = kp * Math.sqrt(Math.abs(error)) * Math.signum(error);
        return (float) power;
    }

    public double getCurrentPosition() {
        return turret.getCurrentPosition();
    }

    public void setPowerBoth(float power){
        turret.setPower(power);
    }

    public void setPower(float power) {
        if (turret != null) {
            RobotLog.ii(TAG_SL, "Set slide motor power to " + power);
            turret.setPower(getPowerR());
            if (power == 0f) {
                turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            RobotLog.ii(TAG_SL, "Not setting power for motorL");
        }
    }

    public void setUseSquID(boolean useSquID, int target) {
        this.useSquID = useSquID;
        this.target = target;
        multiplier = 1f;
    }

    public void setUseSquID(boolean useSquID, int target, float multiplier) {
        this.useSquID = useSquID;
        this.target = target;
        this.multiplier = multiplier;
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void update() {
        if (useSquID) {
            setPowerBoth(getSquid());
        }
    }

    public void reset(OcMotorEx motor) {
        motor.setPower(0f);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.resetPosition();
    }

    public float getPowerR() {
        return turret.getPower();
    }

    public double getTarget() {
        return target;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public void setPIDF(double p, double i, double d, double f) { this.p = p; this.i = i; this.d = d; this.f = f; }

    public void moveEncoderTo(int pos, float power){
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setTargetPosition(pos);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(power);
    }
}
