package overcharged.test;

import com.qualcomm.robotcore.hardware.DcMotor;

import overcharged.components.OcMotor;
import overcharged.components.OcMotorEx;

/**
 * Created by Parthiv on 9/15/2019.
 */
public class MotorTestInfo {
    public OcMotorEx motor;
    public String motorName = "Unknown";

    public MotorTestInfo(OcMotorEx motor, String name) {
        this.motor = motor;
        this.motorName = name;
    }

    public void stop()
    {
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motor.setPower(0f);
        this.motor.resetPosition();
    }
}
