package overcharged.testModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.actions.Actions;
import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;


@Config
@TeleOp(name = "PID Test", group = "test")
public class PIDTest extends OpMode{
    RobotMecanum robot;

    Actions actionHandler = new Actions();

    Limelight3A limelight;

    double slowPower = 1;
    int calcPosition;

    boolean intakeOn = false;
    boolean kicker = false;
    boolean shooting = false;
    boolean shooterTaking = false;
    boolean autoAiming = true;
    boolean canTurn = true;

    boolean shootPID = false;
    boolean shootPIDupdate = false;

    boolean hoodPID = false;
    boolean hoodPIDupdate = false;

    boolean allPID = false;

    boolean checker = false;

    boolean turretReset = false;
    long turretResetTimer;

    float hoodStick;
    float tempHood;
    double turretTurn;

    int shootStep = 0;
    int shootRepStep = 0;
    float tempShootPower = 0;
    float shootPower = 0;

    long kickTimer = 0;
    long shootTimer = 0;

    int distance = 0;

    int sideID = 20;

    ElapsedTime temp;


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

        robot.turret.setUseSquID(true, turretSquid.center, 1f);
    }

    public void loop() {
        telemetry.addData("lag: ", temp);
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        //telemetry.addData("shoot power: ", tempShootPower);
        //telemetry.addData("shooter power: ", robot.shooter.getCurrentSpeed());
        telemetry.addData("shoot PID: ", robot.shooter.getPID());
        telemetry.addData("shoot pid on: ", shootPID);
        telemetry.addData("distance to goal: ", distance);
        telemetry.addData("Hood Angle", robot.hood.getCurrentAngle());
        telemetry.addData("Hood Position", robot.hood.getCurrentPos());
        //telemetry.addData("SIDE: ", sideID);

        //per loop things
        robot.clearBulkCache();
        robot.turret.update();
        robot.shooter.update();
        robot.hood.update();

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

        // limelight readings, ll distance calculations
        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) { //20 blue 24 red
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                float ty = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetYDegrees();
                telemetry.addData("goal tx: ", tx);
                distance = (int) ((646.1125 - 410.15381) / Math.tan(Math.toRadians(ty))) + 300;
//                telemetry.addData("ty: ", ty);
            }
        }
        catch (IndexOutOfBoundsException e1) { // imu stuff as backup/alternative
            telemetry.addLine("No LL vision, using IMU");
//            float yaw = (float) robot.imu.getYaw();
//            telemetry.addData("imu yaw: ", yaw);

        }

        if ((gamepad2.touchpad || gamepad1.touchpad) && Button.BTN_LIMELIGHT.canPress(timestamp)) {
            autoAiming = !autoAiming;
        }

        if (autoAiming) {
            robot.turret.setUseSquID(true);
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                if (Math.abs(tx) >= 1.2f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) { //20 blue, 24 red
                    calcPosition = (int) (-2.351157407 * tx);
                    telemetry.addData("calc pos", calcPosition);
                    if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                        robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.6f);
                    }
                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("cant see vro :skull:");
            }
        }
        else {
            robot.turret.setUseSquID(false,turretSquid.center);
//            robot.turret.moveEncoderTo(turretSquid.center, 0.7f);
            turretTurn = -(gamepad2.right_stick_x)*0.35;
            if(Math.abs(turretTurn) >= 0.03) {
                robot.turrets.setPower((float)turretTurn);
                checker = true;
            } else if (Math.abs(turretTurn) < 0.05){
                robot.turrets.setPower(0);
                checker = false;
            }
        }


        //shoot
        if((gamepad1.right_bumper) && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if (!shootPID && !hoodPID) {
                if (!shooting) {
                    gamepad1.rumble(100);
                    robot.shooter.shoot();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = true;
                    shooterTaking = false;
                } else if (shooting) {
                    robot.shooter.off();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = false;
                    shooterTaking = false;
                }
            }
            else if(shootPID && hoodPID) {
                if(!shooting) {
                    gamepad1.rumble(100);
                    shootPIDupdate = true;
                    hoodPIDupdate = true;
                    shooting = true;
                    shooterTaking = false;
                } else if(shooting) {
                    robot.shooter.off();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = false;
                    shooterTaking = false;
                }
            }
            else if (shootPID){
                if(!shooting) {
                    gamepad1.rumble(100);
                    shootPIDupdate = true;
                    hoodPIDupdate = false;
                    shooting = true;
                    shooterTaking = false;
                } else if(shooting) {
                    robot.shooter.off();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = false;
                    shooterTaking = false;
                }
            }
            else if (hoodPID) {
                if(!shooting) {
                    gamepad1.rumble(100);
                    robot.shooter.shoot();
                    shootPIDupdate = false;
                    hoodPIDupdate = true;
                    shooting = true;
                    shooterTaking = false;
                } else if(shooting) {
                    robot.shooter.off();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = false;
                    shooterTaking = false;
                }
            }
        }

        if((gamepad1.dpad_up) && Button.INTAKE.canPress(timestamp)) {
            if(allPID) {
                shootPID = false;
                hoodPID = false;
                allPID = false;
            } else if (!allPID) {
                shootPID = true;
                hoodPID = true;
                allPID = true;
            }
        }

        if((gamepad1.dpad_left) && Button.INTAKE.canPress(timestamp)) {
            if(shootPID) {
                shootPID = false;
                hoodPID = false;
                allPID = false;
            } else if (!shootPID) {
                shootPID = true;
                hoodPID = false;
                allPID = false;
            }
        }
        if((gamepad1.dpad_right) && Button.INTAKE.canPress(timestamp)) {
            if(hoodPID) {
                shootPID = false;
                hoodPID = false;
                allPID = false;
            } else if (!hoodPID) {
                shootPID = false;
                hoodPID = true;
                allPID = false;
            }
        }

        if(shootPIDupdate) {
            robot.shooter.setUsePID(true, distance, robot.hood.getCurrentAngle());
            telemetry.addLine("shooter PID ON!!!");
        } else if(!shootPIDupdate){
            robot.shooter.setUsePID(false);
            telemetry.addLine("shoot pid off");
        }
        if(hoodPIDupdate) {
            robot.hood.setAutoAdjust(true, robot.shooter, distance);
            robot.hood.setTargetDistance(distance);
            telemetry.addLine("hood PID on!");
        } else if(!hoodPIDupdate){
            robot.hood.setAutoAdjust(false, robot.shooter, 1000);
            robot.hood.setTargetDistance(10);
            telemetry.addLine("hood pid off");
        }

    }


}
