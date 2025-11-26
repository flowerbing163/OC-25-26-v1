package overcharged.components;

import static overcharged.config.RobotConstants.TAG_SL;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import java.util.concurrent.atomic.AtomicInteger;

public class turretSquid {
    public final OcMotorEx turret;

    public static int center = -215;
    public static final int START = 0;
    public static final int NORMAL = 0;
    public static final int blueCloseAutoStart = 120;
    public static final int blueCloseShootReset = 99;
    public static final int blueCloseFirstView = 99;
    public static final int redCloseAutoStart = -500;
    public static final int redCloseShootReset = center+116;
    public static final int redCloseFirstView = center+116;
    public static final int minimum = -555;
    public static final int maximum = 103;

    public float kp = 0.06f;
    public double start;
    public static double p = 18;
    public static double i = 0.0003f;
    public static double d = 0.0005;
    public static double f = 0.003f;

    private boolean useSquID = false;
    private double target = 0;
    private float multiplier = 1f;

    private double lastError = 0;
    private double integral = 0;
    private long lastTime = System.nanoTime();
    private double derivativePrev = 0;

    public turretSquid(HardwareMap hardwareMap) {
        turret = new OcMotorEx(hardwareMap, "turret", DcMotor.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
    }
    public float getSquid() {
        double error = target - getCurrentPosition();
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        integral += error * dt;
        integral = Math.min(500, Math.max(-500, integral));
        if (Math.abs(error) < 6) {
            integral = 0;
        }

        lastError = error;
        double feedforward = f;

        double power = multiplier * (feedforward + ((kp * Math.sqrt(Math.abs(error)) * Math.signum(error)) + (i * integral)));
        return (float) Math.max(-1, Math.min(1, power));
    }
    public double getCurrentPosition() {
        return turret.getCurrentPosition();
    }

    public void setPower(float power) {
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (turret != null) {
            RobotLog.ii(TAG_SL, "Set slide motor power to " + power);
            turret.setPower(power);
            if (power == 0f) {
                turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            RobotLog.ii(TAG_SL, "Not setting power for motorL");
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

    public void moveEncoderTo(int pos, float power){
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turret.setTargetPosition(pos);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(power);
    }

    public void setUseSquID(boolean useSquID) {this.useSquID = useSquID;}
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
            setPower(getSquid());
        }
    }

    public void setCenter(int mid) {
        center = mid;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public double getTarget() {
        return target;
    }

    public void setKi(float ki){this.i = ki;}

    public void setF(float f){this.f = f;}

    public void setPIDF(double p, double i, double d, double f) { this.p = p; this.i = i; this.d = d; this.f = f; }

    public int getMax() { return maximum;}
    public int getMin() {return minimum;}

    public float getKp() {
        return kp;
    }

    public double getI() {return i;}

    public double getF() {return f;}

}