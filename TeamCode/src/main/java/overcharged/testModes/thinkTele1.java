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
    boolean shooting = false;
    boolean canTurn = true;

    float hoodStick;
    float tempHood;
    float turretTurn;

    long kickTimer = 0;

    ElapsedTime temp;

    intakeState intakeMode = intakeState.OFF;
    public enum intakeState {
        OFF,
        IN,
        OUT,
    }

    public void init() {
        try {
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            robot = new RobotMecanum(this, false, false);
            robot.setBulkReadManual();
        } catch (Exception e) {
            telemetry.addData("Init Failed", e.getMessage());
            telemetry.update();
        }
        temp = new ElapsedTime();
    }

    public void loop() {
        // telemetry
        telemetry.addData("lag: ", temp);
        telemetry.addData("hoodstick: ", hoodStick);
        telemetry.addData("temphood: ", tempHood);
        telemetry.addData("hood pos: ", robot.hood.getCurrentPos());


        //per loop things
        robot.clearBulkCache();
        long timestamp = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        temp.reset();


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
            if(intakeMode == intakeState.OFF || intakeMode == intakeState.OUT) {
                robot.intake.in();
                intakeMode = intakeState.IN;
            } else if(intakeMode == intakeState.IN) {
                robot.intake.off();
                intakeMode = intakeState.OFF;
            }
        }

        if(gamepad1.left_trigger > 0.8 && Button.INTAKE.canPress(timestamp)) {
            if(intakeMode == intakeState.OFF || intakeMode == intakeState.IN) {
                robot.intake.out();
                intakeMode = intakeState.OUT;
            } else if(intakeMode == intakeState.OUT) {
                robot.intake.off();
                intakeMode = intakeState.OFF;
            }
        }

        if (gamepad2.x && Button.BTN_TTABLE.canPress(timestamp) && canTurn) {
            robot.indexer.setOne();
        }
        if (gamepad2.a && Button.BTN_TTABLE.canPress(timestamp) && canTurn) {
            robot.indexer.setTwo();
        }
        if (gamepad2.b && Button.BTN_TTABLE.canPress(timestamp) && canTurn) {
            robot.indexer.setThree();
        }

        if (gamepad2.y && Button.BTN_KICKER.canPress(timestamp)) {
            kicker = true;
            canTurn = false;
            kickTimer = System.currentTimeMillis();
            robot.kicker.setKick();
        }
        if(kicker && System.currentTimeMillis()-kickTimer > 600){
            robot.kicker.setInit();
            kicker = false;
            canTurn = true;
            kickTimer = 0;
        }

        if(gamepad2.right_trigger > 0.8 && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if(!shooting) {
                robot.shooter.shoot();
                shooting = true;
            } else if(shooting) {
                robot.shooter.off();
                shooting = false;
            }
        }

        hoodStick = -((float) gamepad2.left_stick_y)*1f;
        if(Math.abs(hoodStick) >= 0.07) {
            tempHood = robot.hood.getCurrentPos() + hoodStick;
            tempHood = Math.max(robot.hood.INIT, Math.min(tempHood, 255));
            robot.hood.setPosition(tempHood);
        }

        turretTurn = ((float) gamepad2.right_stick_x)*0.4f;
        if(Math.abs(turretTurn) > 0.04) {
            robot.turret.setPower(turretTurn);
        }
    }
}
