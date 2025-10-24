package overcharged.testModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.actions.actions;
import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.hood;
import overcharged.components.turrets;


@Config
@TeleOp(name = "imagine tele", group = "tele")
public class thinkTele1 extends OpMode{

    RobotMecanum robot;

    actions actionHandler = new actions();

    Limelight3A limelight;

    double slowPower = 1;
    int calcPosition;

    boolean intakeOn = false;
    boolean kicker = false;
    boolean shooting = false;
    boolean shooterTaking = false;
    boolean autoAiming = true;
    boolean canTurn = true;

    boolean checker = false;

    float hoodStick;
    float tempHood;
    double turretTurn;

    int shootStep = 0;
    int shootRepStep = 0;
    float tempShootPower = 0;
    float shootPower = 0;

    long kickTimer = 0;
    long shootTimer = 0;

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
            actionHandler.fastShootSys(robot);
            actionHandler.indexerSys(robot);
        } catch (Exception e) {
            telemetry.addData("Init Failed", e.getMessage());
            telemetry.update();
        }
        temp = new ElapsedTime();
        robot.indexer.setTwo();

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(1);
        limelight.start();
        robot.turret.setUseSquID(true, 0, 1f);
    }

    public void loop() {
        // telemetry
        telemetry.addData("lag: ", temp);
        //telemetry.addData("hoodstick: ", hoodStick);
        //telemetry.addData("temphood: ", tempHood);
        telemetry.addData("hood pos: ", robot.hood.getCurrentPos());
        //telemetry.addData("turretTurn: ", turretTurn);
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        //telemetry.addData("test: ", checker);
        //telemetry.addData("shoot power: ", tempShootPower);
        telemetry.addData("shooter power", robot.shooter.getCurrentSpeed());
        telemetry.addData("shoot target", robot.shooter.targetSpeed);
        telemetry.addData("shoot PID", robot.shooter.getPID());


        //per loop things
        robot.clearBulkCache();
        robot.turret.update();

        actionHandler.fastShootSeq();
        actionHandler.indMoveSeq();

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
        //


        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) { //20 blue 24 red
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                telemetry.addData("RED GOAL TX: ", tx);
            }
        }
        catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see, manually adjust");
        }

        if (gamepad1.a && Button.BTN_LIMELIGHT.canPress(timestamp)) {
            autoAiming = !autoAiming;
        }
        if (autoAiming) {
            robot.turret.setUseSquID(true);
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                if (Math.abs(tx) >= 1.75f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) { //20 blue, 24 red
                    calcPosition = -((int) (2.3511574 * tx));
                    if (calcPosition >= -423 && calcPosition <= 423) {
                        telemetry.addData("calc pos", calcPosition);
                        robot.turret.setUseSquID(true, calcPosition, 0.7f);
                    }

                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("cant see vro :skull:");
            }
        }
        else {
            robot.turret.setUseSquID(false);
            turretTurn = -(gamepad2.right_stick_x)*0.5;
            if(Math.abs(turretTurn) >= 0.05) {
                robot.turrets.setPower((float)turretTurn);
                checker = true;
            } else if (Math.abs(turretTurn) < 0.05){
                robot.turrets.setPower(0);
                checker = false;
            }
        }

        //intake
        if(gamepad1.right_trigger > 0.8 && Button.INTAKE.canPress(timestamp)) {
            if(intakeMode == intakeState.OFF || intakeMode == intakeState.OUT) {
                robot.intake.in();
                intakeMode = intakeState.IN;
            } else if(intakeMode == intakeState.IN) {
                robot.intake.off();
                intakeMode = intakeState.OFF;
            }
        }

        //outtake
        if(gamepad1.left_trigger > 0.8 && Button.INTAKE.canPress(timestamp)) {
            if(intakeMode == intakeState.OFF || intakeMode == intakeState.IN) {
                robot.intake.out();
                intakeMode = intakeState.OUT;
            } else if(intakeMode == intakeState.OUT) {
                robot.intake.off();
                intakeMode = intakeState.OFF;
            }
        }

        //indexer manual move
        if(gamepad2.a && Button.BTN_TTABLE.canPress(timestamp) && canTurn) {
            actionHandler.startIndMove();
        }

        //manual kicker
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

        //shoot
        if(gamepad2.right_trigger > 0.8 && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if(!shooting) {
                robot.shooter.shoot();
                shooting = true;
                shooterTaking = false;
            } else if(shooting) {
                robot.shooter.off();
                shooting = false;
                shooterTaking = false;
            }
        }

        //shooter intake
        if(gamepad2.left_trigger > 0.8 && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if(!shooterTaking) {
                robot.shooter.intake();
                shooting = false;
                shooterTaking = true;
            } else if(shooterTaking) {
                robot.shooter.off();
                shooting = false;
                shooterTaking = false;
            }
        }

        //manual hood
        hoodStick = ((float) gamepad2.left_stick_y)*1f;
        if(Math.abs(hoodStick) >= 0.07) {
            tempHood = robot.hood.getCurrentPos() + hoodStick;
            tempHood = Math.max(robot.hood.MAX, Math.min(tempHood, robot.hood.INIT-4));
            robot.hood.setPosition(tempHood);
        }

        //fast shoot
        if(gamepad2.left_bumper && Button.BTN_TTABLE.canPress(timestamp)){
            actionHandler.startFastShoot();
        }

        //preset hood pos
        if(gamepad2.dpad_left && Button.BTN_HOOD.canPress(timestamp)){
            robot.hood.setPosition(hood.CLOSE);
        }
        if(gamepad2.dpad_up && Button.BTN_HOOD.canPress(timestamp)){
            robot.hood.setPosition(hood.MIDDLE);
        }
        if(gamepad2.dpad_right && Button.BTN_HOOD.canPress(timestamp)){
            robot.hood.setPosition(hood.DEFENSE);
        }

    }
}
