package overcharged.opmodes;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.actions.ActionsV2;
import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;

import com.qualcomm.robotcore.hardware.VoltageSensor;

@Config
@TeleOp(name = "correct tele", group = "0tele")
public class NewTeleOp extends OpMode {

    RobotMecanum robot;

    ActionsV2 actionHandler = new ActionsV2();

    Limelight3A limelight;
    //private Follower follower;
    public static Pose startPose;

    private VoltageSensor chubVoltageSensor;

    double slowPower = 1;
    int calcPosition;

    boolean intakeOn = false;
    boolean down = true;
    boolean shooting = false;
    boolean shooterTaking = false;
    boolean autoAiming = true;
    boolean ll = true;
    boolean canTurn = true;

    boolean hoodPID = false;
    boolean hoodPIDupdate = false;

    boolean shootPID = true;
    boolean shootPIDupdate = false;

    boolean checker = false;
    boolean turretReset = false;
    long turretResetTimer;

    float hoodStick;
    float tempHood;
    double turretTurn;

    long kickTimer = 0;
    long shootTimer = 0;

    int distance = 0;
    String sideColor = "BLUE";
    int sideID = 20;
    float robotYaw;
    float goalAngle;
    int imuAdjust;

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
            //actionHandler.sortShootSys(robot);
            robot.indexerlift.setInit();
            chubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");

        } catch (Exception e) {
            telemetry.addData("Init Failed", e.getMessage());
            telemetry.update();
        }

        temp = new ElapsedTime();
        //robot.hood.

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(0);
        limelight.start();

        startPose = new Pose(0,0, Math.toRadians(0));
//        follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(startPose == null ? new Pose() : startPose);
//        follower.update();
        robot.imu.resetYaw();

        robot.turret.setUseSquID(true, turretSquid.center, .8f);
    }

    public void loop() {
        //TODO:telemetry
        telemetry.addData("lag: ", temp);
        telemetry.addData("imu robotYaw: ", robotYaw);
        telemetry.addData("unchanged robotYaw: ", robot.imu.getYaw());
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        telemetry.addData("shoot PID: ", robot.shooter.getPID());
        telemetry.addData("distance to goal: ", distance);
        telemetry.addData("Hood Angle", robot.hood.getCurrentAngle());
        telemetry.addData("Hood Position", robot.hood.getCurrentPos());
        telemetry.addData("volt", chubVoltageSensor.getVoltage());

        //color thing
        telemetry.addData("SIDE: ", sideColor, sideID);
        if(sideID == 20) {
            sideColor = "BLUE";
        } else if(sideID == 24) {
            sideColor = "RED";
        }

        //TODO: loop updates
        robot.clearBulkCache();
        robot.turret.update();
        robot.shooter.update();
        robot.hood.update();
        //follower.update();

        //actions updates
        actionHandler.fastShoot();

        long timestamp = System.currentTimeMillis();
        long time = System.currentTimeMillis();
        temp.reset();

        //TODO: driving
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

        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) {
                float ty = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetYDegrees();
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                telemetry.addData("goal tx: ", tx);
                distance = (int) ((646.1125 - 317.5) / Math.tan(Math.toRadians(ty)));
            }
        } catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("No vision");
        }

        if (gamepad1.dpad_down || gamepad2.b && Button.BTN_TRACKING.canPress(timestamp)) {
            autoAiming = !autoAiming;
        }

//        if (gamepad2.touchpad && Button.BTN_LIMELIGHT.canPress(timestamp)) {
//            ll = !ll;
//        }

        if (autoAiming) {
            robot.turret.setUseSquID(true);
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                telemetry.addLine("aiming: limelight");
                if (distance > 2000) {
                    if (sideID == 20) {
                        if ((tx < -0.8f || tx > 1.65) && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) {
                            calcPosition = (int) (2.15 * tx); //2.3511740740740
                            telemetry.addData("calc pos: ", calcPosition);
                            if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                                robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, .7f);
                            }
                        }
                    }
                    else {
                        if ((tx > 0.8f || tx < -1.65) && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) {
                            calcPosition = (int) (2.15 * tx); //2.3511740740740
                            telemetry.addData("calc pos: ", calcPosition);
                            if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                                robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, .7f);
                            }
                        }
                    }
                }
                else {
                    if (Math.abs(tx) >= 1.31f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) { //20 blue, 24 red
                        calcPosition = (int) (2.15 * tx); //2.3511740740740
                        telemetry.addData("calc pos: ", calcPosition);
                        if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                            robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, .7f);
                        }
                    }
                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("Can't autoaim, no vision");
            }
        }
//        else if (autoAiming & !ll) {
//            telemetry.addLine("aiming: imu");
//
//            if (sideID == 20) { // blue
//                goalAngle = (float) Math.toDegrees(Math.atan((Math.abs(follower.getPose().getX() + autoBlueCloseV2.getPoseX() - 7)) / (Math.abs(follower.getPose().getY() + autoBlueCloseV2.getPoseY() - 137))));
//                if (0 <= robotYaw && robotYaw <= 360) {
//                    imuAdjust = (int) (-215 + 2.35115740740740 * (180 - robotYaw + goalAngle));
//                    robot.turret.setUseSquID(true, imuAdjust, .75f);
//                }
//            }
//            else if (sideID == 24) {
//                goalAngle = (float) Math.toDegrees(Math.atan((Math.abs(follower.getPose().getX() + autoRedCloseV2.getPoseX() - 137))/(Math.abs(follower.getPose().getY() + autoRedCloseV2.getPoseY() - 137))));
//                if (-215 - 2.35115740740740 * (goalAngle + robotYaw) <= 122 && -215 - 2.35115740740740 * (goalAngle + robotYaw) >= -555) {
//                    robot.turret.setUseSquID(true, (int) (-215 - 2.35115740740740 * (goalAngle + robotYaw)), .75f);
//                }
//            }
//        }
        else {
            telemetry.addLine("aiming: manual");
            robot.turret.setUseSquID(false);
            turretTurn = (gamepad2.right_stick_x) * 0.35;
            if (Math.abs(turretTurn) >= 0.03) {
                robot.turrets.setPower((float) turretTurn);
                checker = true;
            } else if (Math.abs(turretTurn) < 0.05) {
                robot.turrets.setPower(0);
                checker = false;
            }
        }

        if (gamepad1.touchpad && Button.BTN_MIN.canPress(timestamp)) {
            turretReset = true;
            turretResetTimer = System.currentTimeMillis();
            autoAiming = false;
            robot.turret.setUseSquID(false,turretSquid.center);
            //robot.turret.moveEncoderTo(turretSquid.center, 0.7f);
        }

        if (turretReset && System.currentTimeMillis()-turretResetTimer>400) {
            turretReset = false;
            turretResetTimer = 0;
            autoAiming = true;
        }

        if((gamepad1.dpad_up || gamepad2.dpad_up) && Button.BTN_PLUS.canPress(timestamp)) {
            if(sideID == 20) {
                sideID = 24;
                gamepad1.setLedColor(255,0,0,10000);
            } else if(sideID == 24) {
                sideID = 20;
            }
            else {
                sideID = 20;
                gamepad1.setLedColor(0,0,255,10000);
            }
        }

        //intake
        if(gamepad1.right_trigger > 0.8 && Button.INTAKE.canPress(timestamp)) {
            if(intakeMode == intakeState.OFF || intakeMode == intakeState.OUT) {
                robot.intake.in();
                intakeMode = intakeState.IN;
            } else if (intakeMode == intakeState.IN) {
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

        //TODO: ADD INDEXER MANUAL MOVE

        //TODO: ADD INDEXER HARD RESET
        if(gamepad1.x && Button.INTAKE.canPress(timestamp)) {
            robot.indexer.reset();
            robot.indexerlift.setInit();
        }


        //TODO: SHOOT
        if ((gamepad1.right_bumper || gamepad2.right_trigger > 0.8) && Button.BTN_FLYWHEEL.canPress(timestamp)) {
            if (!shootPID && !hoodPID) {
                if (!shooting) {
                    gamepad1.rumble(100);
                    gamepad2.rumble(100);
                    robot.shooter.shoot();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = true;
                    shooterTaking = false;
                }
                else {
                    robot.shooter.off();
                    shootPIDupdate = false;
                    hoodPIDupdate = false;
                    shooting = false;
                    shooterTaking = false;
                }
            }
            else if (shootPID) {
                if(!shooting) {
                    gamepad1.rumble(100);
                    gamepad2.rumble(100);
                    shootPIDupdate = true;
                    hoodPIDupdate = true;
                    shooting = true;
                    shooterTaking = false;
                }
                else {
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
                    gamepad2.rumble(100);
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

        if ((gamepad1.dpad_left || gamepad2.right_bumper) && Button.BTN_SHOOT_PID.canPress(timestamp)) {
            if (shootPID) {
                shootPID = false;
                hoodPID = false;
            } else if (!shootPID) {
                gamepad2.rumble(100);
                shootPID = true;
                hoodPID = false;
            }
        }

//        if((gamepad1.dpad_right || gamepad2.left_bumper) && Button.INTAKE.canPress(timestamp)) {
//            if(hoodPID) {
//                shootPID = false;
//                hoodPID = false;
//            } else if (!hoodPID) {
//                gamepad2.rumble(100);
//                shootPID = false;
//                hoodPID = true;
//            }
//        }

        if (shootPIDupdate) {
            robot.shooter.setUsePID(true, distance, robot.hood.getCurrentAngle());
            telemetry.addLine("shooter PID ON!!!");
        }
        else if(!shootPIDupdate){
            robot.shooter.setUsePID(false);
            telemetry.addLine("pid off");
        }

//        if (hoodPIDupdate) {
//            robot.hood.setAutoAdjust(true, robot.shooter, distance);
//            robot.hood.setTargetDistance(distance);
//            telemetry.addLine("hood PID on!");
//        }
//        else if(!hoodPIDupdate){
//            robot.hood.setAutoAdjust(false, robot.shooter, 2000);
//            robot.hood.setTargetDistance(2000);
//            telemetry.addLine("pid off");
//        }

        //TODO: SHOOTER INTAKING
        if(gamepad2.left_trigger > 0.8 && Button.BTN_FLYWHEEL.canPress(timestamp) && !shootPID) {
            if(!shooterTaking) {
                robot.shooter.intake();
                shootPIDupdate = false;
                shooting = false;
                shooterTaking = true;
            } else if(shooterTaking) {
                robot.shooter.off();
                shootPIDupdate = false;
                shooting = false;
                shooterTaking = false;
            }
        }

//        if (gamepad2.x && Button.BTN_KICKER.canPress(timestamp)) {
//            robot.indexerlift.setMid();
//        }

        if(gamepad2.a && Button.BTN_95.canPress(timestamp)) {
            robot.indexerlift.setInit();
            robot.indexer.reset();
        }

        //TODO: MANUAL HOOD
        if(!hoodPIDupdate){
            hoodStick = (gamepad2.left_stick_y)*1f;
            if(Math.abs(hoodStick) >= 0.07) {
                tempHood = robot.hood.getCurrentPos() + hoodStick;
                tempHood = Math.max(robot.hood.MAX, Math.min(tempHood, robot.hood.INIT-1));
                robot.hood.setPosition(tempHood);
            }
        }

        //TODO: CHECK FAST SHOOT ACTION
        if(gamepad2.y && Button.BTN_SHOOT_PID.canPress(timestamp) && shooting) {
            intakeMode = intakeState.IN;
            actionHandler.startFastShoot();
        }

        //TODO: TURRET RECENTER
        if(gamepad2.guide && Button.BTN_MINUS.canPress(timestamp)) {
            robot.turret.setCenter((int)robot.turret.getCurrentPosition());
        }

        if((gamepad2.x || gamepad1.b) && Button.BTN_MINUS.canPress(timestamp)) {
            robot.indexer.rotateBit();
        }

        //TODO: MODIFY PRESET HOOD POSITIONS
        //TODO: FAR ZONE
        if(gamepad2.dpad_down && Button.BTN_128.canPress(timestamp) && !hoodPID) {
            robot.hood.setInit();
        }

        //TODO: CLOSE ZONE
        if(gamepad2.dpad_right && Button.BTN_95.canPress(timestamp) && !hoodPID) {
            robot.hood.setPosition(34);
        }

        //TODO: DOWN
        if(gamepad2.dpad_left && Button.BTN_HOODDOWN.canPress(timestamp) && !hoodPID) {
            robot.hood.setPosition(189);
        }
    }

}