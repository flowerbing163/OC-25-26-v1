package overcharged.testModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;


@Config
@TeleOp(name = "turret tele", group = "test")
public class turretTesting extends OpMode {
    RobotMecanum robot;

    double turretTurn;
    boolean checker = false;
    boolean shooting = false;

    float hoodStick;
    float tempHood;

    float shootPower;

    public void init() {

    }

    public void loop() {
        long timestamp = System.currentTimeMillis();
        telemetry.addData("turretTurn: ", turretTurn);
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        telemetry.addData("test: ", checker);
        telemetry.addData("shoot power: ", shootPower);

        turretTurn = (gamepad1.right_stick_x)*0.5;
        if(Math.abs(turretTurn) >= 0.05) {
            robot.turrets.setPower((float)turretTurn);
            checker = true;
        } else if (Math.abs(turretTurn) < 0.05){
            robot.turrets.setPower(0);
            checker = false;
        }

        shootPower += gamepad1.right_stick_y;
        shootPower = Math.max(-1, Math.min(shootPower, 1));
        if(gamepad2.right_trigger > 0.8 && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if(!shooting) {
                robot.shooter.shoot(shootPower);
                shooting = true;
            } else if(shooting) {
                robot.shooter.off();
                shooting = false;
            }
        }

        hoodStick = -((float) gamepad1.left_stick_y)*1f;
        if(Math.abs(hoodStick) >= 0.07) {
            tempHood = robot.hood.getCurrentPos() + hoodStick;
            tempHood = Math.max(robot.hood.INIT, Math.min(tempHood, robot.hood.MAX-4));
            robot.hood.setPosition(tempHood);
        }
    }
}
