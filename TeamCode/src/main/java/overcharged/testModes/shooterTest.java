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

    //FtcDashboard dashboard = FtcDashboard.getInstance();

    public static float kP = 0.07f;
    public static int target = 0;
    public static double p = 10;
    public static double i = 0.0001;
    public static double d = 0.0005;
    public static double f = 0;
    public static float powerCoeff = 1.612f;

    float hoodStick;
    float tempHood;

    boolean kicker = false;
    long kickTimer;


    public void init() {
        robot = new RobotMecanum(this, false, false);
        //telemetry = dashboard.getTelemetry();
    }

    public void loop() {
        long timestamp = System.currentTimeMillis();
        telemetry.addData("shooting speed", robot.shooter.getCurrentSpeed());
        telemetry.addData("shoot target", robot.shooter.targetSpeed);
        telemetry.addData("top motor speed", robot.shooter.getPowerT());
        telemetry.addData("shoot PID", robot.shooter.getPID());
        telemetry.addData("error", robot.shooter.getError());
        telemetry.addData("encoder pos: ", robot.shooter.getCurrentPos());
        telemetry.addData("hood angle: ", Math.toDegrees(robot.hood.getCurrentAngle()));
        telemetry.addData("power coeff: ", powerCoeff);

        telemetry.update();

        //robot.shooter.setUsePID(true, target);
        robot.shooter.setKp(kP);
        robot.shooter.setPIDF(p, i, d, f);
        robot.shooter.setPowerCoeff(powerCoeff);
        robot.shooter.update();

        //manual hood
        hoodStick = ((float) gamepad1.left_stick_y)*1f;
        if(Math.abs(hoodStick) >= 0.07) {
            tempHood = robot.hood.getCurrentPos() + hoodStick;
            tempHood = Math.max(robot.hood.MAX+2, Math.min(tempHood, robot.hood.INIT-2));
            robot.hood.setPosition(tempHood);
        }

        if (gamepad1.y && Button.BTN_KICKER.canPress(timestamp)) {
            kicker = true;
            kickTimer = System.currentTimeMillis();
            robot.kicker.setKick();
        }
        if(kicker && System.currentTimeMillis()-kickTimer > 600){
            robot.kicker.setInit();
            kicker = false;
            kickTimer = 0;
        }



    }
}
