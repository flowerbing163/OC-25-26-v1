package overcharged.components;

import static overcharged.config.RobotConstants.TAG_SL;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.opencv.core.Mat;

@Config
public class shooter {

    RobotMecanum robot;
    private VoltageSensor chubVoltageSensor;

    public final OcMotorEx topShooter;
    public final OcMotorEx botShooter;

    public double targetSpeed;
    public double targetDist = 1.5;
    public double hood = Math.toRadians(50);
    public double lastError;
    public double integral;
    public double lastTime;
    public double kp = 10;
    public double d = 0.0002;
    public double i = 0.0005;
    public double f = 0.003;

    public float power = 0f;
    public float powerCoeff = 10f; //ball initial speed
    public float volPowerCoeff;
    public float maxPow = 1.0f;
    private float curVolt;
    private float powerCoeffAdjuster = 0;
    private float secondaryAdjuster = 0;

    private double curVel;
    private double addCoeff;

    //TODO: motor velocity at max spin(1f) = 1.612
    //TODO:

    private double derivativePrev = 0;

    private boolean usePID = false;


    public shooter(HardwareMap hardwareMap) {
        topShooter = new OcMotorEx(hardwareMap, "topShooter", DcMotor.Direction.FORWARD, DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        botShooter = new OcMotorEx(hardwareMap, "botShooter", DcMotor.Direction.REVERSE, DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        chubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
    }

    public void setPowerBoth(float power){
        topShooter.setPower(power);
        botShooter.setPower(power);
    }


    public void shoot(float power) {
        setPowerBoth(power);
    }
    public void shoot() {
    if ((float) chubVoltageSensor.getVoltage() > 12) {
        setPowerBoth(0.43f);
    }
    else {
        setPowerBoth(0.63f);
    }

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

    public float getPowerBoth() {
        return (botShooter.getPower()+topShooter.getPower())/2;
    }

    public double getVelocityBoth() {return (botShooter.getVelocity() + topShooter.getVelocity())/2 ;}

    public void update() {
        if (usePID) {
            setPowerBoth(getPID());
        }
    }

    public float getPID() {
        //power outputs in velocity of shoot, finpower converts to motor power through vel of ball
        float power = (float)Math.sqrt((9.81*Math.pow(targetDist, 2))/(2*Math.pow(Math.cos(hood), 2)*(targetDist*Math.tan(hood)-0.70485)));

        if(targetDist>2.000 && targetDist<2.800) {
            powerCoeffAdjuster = (float)targetDist/10-0.2f;
        } else if(targetDist>2.800) {
            powerCoeffAdjuster = (float)targetDist/10-0.2f;
        } else {
            powerCoeffAdjuster = 0f;
        }
        curVolt = (float)chubVoltageSensor.getVoltage();
        if(curVolt < 12.01) {
            volPowerCoeff = powerCoeff - 0.4f*Math.max(-1, Math.min((12 - curVolt), 1));
        } else {
            volPowerCoeff = powerCoeff;
        }
        if(getVelocityBoth() - power > 0.02)  {
            secondaryAdjuster = 0.03f;
        } else if (getVelocityBoth() - power > 0.05 && getVelocityBoth() - power < 0.325) {
            secondaryAdjuster = 0.065f;
        } else {
            secondaryAdjuster = 0;
        }
        float maxPow = (float)(power/(volPowerCoeff + powerCoeffAdjuster)) + secondaryAdjuster;
        return Math.max(-1, Math.min(maxPow, 1));
    }

    public double getCurrentSpeed() {
        return getPowerB();
    }

    public double getCurrentPos() {return Math.round((topShooter.getCurrentPosition()+botShooter.getCurrentPosition())/2);}

    public void setUsePID(boolean usePID) {
        this.usePID = usePID;
    }

    public void setUsePID(boolean usePID, double target, double hood) {
        this.usePID = usePID;
        this.targetDist = target/1000; //convert mm to m
        this.hood = hood;
    }

    public void setKp(float kp) {
        this.kp = kp;
    }

    public void setPIDF(double p, double i, double d, double f) { this.kp = p; this.i = i; this.d = d; this.f = f; }

    public void setPowerCoeff(float power) {this.powerCoeff = power;}

    public double getError() {return targetSpeed - getCurrentSpeed();}


}