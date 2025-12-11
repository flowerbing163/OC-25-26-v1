package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;


@Config
@TeleOp(name="turret test", group="Test")
public class turretTuner extends OpMode {
    RobotMecanum robot;

    public static float kP = turretSquid.getKp();
    public static float kI = (float) turretSquid.getI();
    public static float f = (float) turretSquid.getF();
    public static int target = 0;

    public void loop(){
        robot.turret.setUseSquID(true, target, 1f);
        robot.turret.setKp(kP);
        robot.turret.setKi(kI);
        robot.turret.setF(f);
        robot.turret.update();
        telemetry.addData("current target: ", robot.turret.getTarget());
        telemetry.addData("current pos: ", robot.turret.getCurrentPosition());
    }

    public void init(){
        robot = new RobotMecanum(this, false, false);
        telemetry.addData("target: ", robot.turret.getTarget());
        telemetry.addData("position: ", robot.turret.getCurrentPosition());
    }
}
