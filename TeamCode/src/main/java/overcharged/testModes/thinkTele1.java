package overcharged.testModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.indexer;


@Config
@TeleOp(name = "imagine tele", group = "tele")
public class thinkTele1 extends OpMode{

    RobotMecanum robot;

    double slowPower = 1;

    boolean intakeOn = false;
    boolean kicker = false;

    public void init() {
        try {
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            robot = new RobotMecanum(this, false, false);
            robot.setBulkReadManual();
        } catch (Exception e) {
            telemetry.addData("Init Failed", e.getMessage());
            telemetry.update();
        }
    }

    public void loop() {
        //per loop things
        robot.clearBulkCache();
        long timestamp = System.currentTimeMillis();

        //Driving
        double y = gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x * 1.1;
        double rx = -gamepad1.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double frontLeftPower = ((y + x + rx) / denominator) * slowPower;
        double backLeftPower = ((y - x + rx) / denominator) * slowPower;
        double frontRightPower = ((y - x - rx) / denominator) * slowPower;
        double backRightPower = ((y + x - rx) / denominator) * slowPower;

        robot.driveLeftFront.setPower(frontLeftPower);
        robot.driveLeftBack.setPower(backLeftPower);
        robot.driveRightFront.setPower(frontRightPower);
        robot.driveRightBack.setPower(backRightPower);

        if(gamepad1.right_trigger > 0.8 && Button.INTAKE.canPress(timestamp)) {
            if(!intakeOn) {
                robot.intake.in();
                intakeOn = true;
            } else if(intakeOn) {
                robot.intake.off();
                intakeOn = false;
            }
        }

        if (gamepad2.x && Button.BTN_TTABLE.canPress(timestamp)) {
            robot.indexer.setOne();
        }
        if (gamepad2.a && Button.BTN_TTABLE.canPress(timestamp)) {
            robot.indexer.setTwo();
        }
        if (gamepad2.b && Button.BTN_TTABLE.canPress(timestamp)) {
            robot.indexer.setThree();
        }

        if (gamepad2.y && Button.BTN_KICKER.canPress(timestamp)) {
            if(!kicker) {
                kicker = true;
                robot.kicker.setKick();
            } else if (kicker) {
                kicker = false;
                robot.kicker.setInit();
            }
        }
    }
}
