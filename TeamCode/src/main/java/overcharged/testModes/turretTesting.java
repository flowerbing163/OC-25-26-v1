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

    public void init() {

    }

    public void loop() {
        telemetry.addData("turretTurn: ", turretTurn);
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        telemetry.addData("test: ", checker);

        turretTurn = (gamepad2.right_stick_x)*0.5;
        if(Math.abs(turretTurn) >= 0.05) {
            robot.turrets.setPower((float)turretTurn);
            checker = true;
        } else if (Math.abs(turretTurn) < 0.05){
            robot.turrets.setPower(0);
            checker = false;
        }
    }
}
