package overcharged.components;

import static overcharged.config.RobotConstants.TAG_SL;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

@Config
public class shooter {
    public final OcMotorEx topShooter;
    public final OcMotorEx botShooter;

    public double targetSpeed;
    public double lastError;
    public double integral;
    public double lastTime;
    public double kp = 10;
    public double d = 0.0002;
    public double i = 0.0005;
    public double f = 0.003;

    //TODO: motor velocity at max spin(1f) = 30.15929
    //TODO: make

    private double derivativePrev = 0;

    private boolean usePID = false;


    public shooter(HardwareMap hardwareMap) {
        topShooter = new OcMotorEx(hardwareMap, "topShooter", DcMotor.Direction.REVERSE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        botShooter = new OcMotorEx(hardwareMap, "botShooter", DcMotor.Direction.REVERSE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPowerBoth(float power){
        topShooter.setPower(power);
        botShooter.setPower(power);
    }


    public void shoot(float power) {
        setPowerBoth(power);
    }
    public void shoot() {
        setPowerBoth(1);
    }

    public void intake() {
        setPowerBoth(-0.5f);
    }


    public void off() {
        setPowerBoth(0);
    }

    public void setPower(float power)
    {
        int cnt = 1;
        //try {
        if (topShooter != null) {
            RobotLog.ii(TAG_SL, "Set slide left motor power to " + power);
            topShooter.setPower(getPowerT());
            if (power == 0f) {
                topShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            RobotLog.ii(TAG_SL, "Not setting power for motorL");
        }
        cnt = 2;
        if (botShooter != null) {
            RobotLog.ii(TAG_SL, "Set slide right motor power to " + power);
            botShooter.setPower(getPowerB());
            if (power == 0f) {
                botShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        } else {
            RobotLog.ii(TAG_SL, "Not setting power for motorR");
        }
    }

    public float getPowerT() {
        return topShooter.getPower();
    }

    public float getPowerB() {
        return botShooter.getPower();
    }

    public void update() {
        if (usePID) {
            setPowerBoth(getPID());
        }
    }

    public float getPID() {
        double error = targetSpeed - getCurrentSpeed();
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;
        if(error<0.05){
            return (float) targetSpeed;
        }
        else {
            integral += error * dt;
            integral = Math.min(500, Math.max(-500, integral));
            if (Math.abs(error) < 0.05) {
                integral = 0;
            }

            double derivative = (error - lastError) / dt;


            double feedforward = f;

            double power = (feedforward + ((kp + (i * integral) + (d * derivative))));
            lastError = error;
            return (float) Math.max(-1, Math.min(1, power));
        }
    }

    public double getCurrentSpeed() {
        return (getPowerB()+getPowerT())/2;
    }

    public void setUsePID(boolean usePID, double target) {
        this.usePID = usePID;
        this.targetSpeed = target;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public void setPIDF(double p, double i, double d, double f) { this.kp = p; this.i = i; this.d = d; this.f = f; }

    public double getError() {return targetSpeed - getCurrentSpeed();}


}
