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
@TeleOp(name = "shooter testing", group = "test")
public class shooterTest extends OpMode {
    private RobotMecanum robot;

    public static float kP = 0.07f;
    public static int target = 0;
    public static double p = 18;
    public static double i = 0.0001;
    public static double d = 0.0005;
    public static double f = 0;


    public void init() {
        robot = new RobotMecanum(this, false, false);
    }

    public void loop() {
        long timestamp = System.currentTimeMillis();
        telemetry.addData("shooting speed", robot.shooter.getCurrentSpeed());
        telemetry.addData("shoot target", robot.shooter.targetSpeed);
        telemetry.addData("shoot PID", robot.shooter.getPID());

        robot.shooter.setUsePID(true, target);
        robot.shooter.setKp(kP);
        robot.shooter.setPIDF(p, i, d, f);
        robot.shooter.update();

    }
}
