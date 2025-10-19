package overcharged.components;

import static overcharged.config.RobotConstants.TAG_SL;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;

public class turrets {
    public final OcMotorEx turrets;

    public static final int START = 0;
    public static final int MIN = 300;
    public static final int MAX = -552;
    public static final int NORMAL = 0;

    public static double p = 18;
    public static double i = 0.00015;
    public static double d = 0.0005;
    public static double f = 0.01;


    public turrets(HardwareMap hardwareMap) {
        turrets = new OcMotorEx(hardwareMap, "turret", DcMotor.Direction.FORWARD, DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double getCurrentPosition() {
        return turrets.getCurrentPosition();
    }

    public void setPower(float power) {
        turrets.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (turrets != null) {
            RobotLog.ii(TAG_SL, "Set slide motor power to " + power);
            turrets.setPower(power);
            if (power == 0f) {
                turrets.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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
        return turrets.getPower();
    }

    public void moveEncoderTo(int pos, float power){
        turrets.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turrets.setTargetPosition(pos);
        turrets.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turrets.setPower(power);
    }


}