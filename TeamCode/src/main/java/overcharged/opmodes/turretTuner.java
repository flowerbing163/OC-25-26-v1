package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import overcharged.components.RobotMecanum;
import overcharged.components.turret;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

@Config
@TeleOp(name="turret test", group="Test")
public class turretTuner extends OpMode {
    private RobotMecanum robot;

    public static float kP = 0.07f;
    public static int target = 0;

    public void loop(){
        robot.turret.setUseSquID(true, target, 1f);
        robot.turret.setKp(kP);
        robot.turret.update();
    }

    public void init(){
        robot = new RobotMecanum(this, false, false);
        telemetry.addData("target: ", robot.turret.getTarget());
        telemetry.addData("position: ", robot.turret.getCurrentPosition());
    }
}
