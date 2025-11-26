package overcharged.components;

import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class imu {
    public final IMU imu;
    public imu(HardwareMap hardwareMap) {
        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot.LogoFacingDirection logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        RevHubOrientationOnRobot.UsbFacingDirection usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;

        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoFacingDirection, usbFacingDirection);

        imu.initialize(new IMU.Parameters(orientationOnRobot));
    }

    public void resetYaw() { imu.resetYaw();}

    public float getYaw() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return (float) orientation.getYaw(AngleUnit.DEGREES);
    }

    public float getPitch() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return (float) orientation.getPitch(AngleUnit.DEGREES);
    }

    public float getRoll() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return (float) orientation.getRoll(AngleUnit.DEGREES);
    }

}
