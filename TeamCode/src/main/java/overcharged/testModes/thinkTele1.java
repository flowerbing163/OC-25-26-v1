package overcharged.testModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.actions.actions;
import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.hood;
import overcharged.components.turretSquid;
import overcharged.components.turrets;


@Config
@TeleOp(name = "imagine tele", group = "0tele")
public class thinkTele1 extends OpMode{

    RobotMecanum robot;

    actions actionHandler = new actions();

    Limelight3A limelight;

    BHI260IMU imu;

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
        robot.turret.setUseSquID(true, turretSquid.center, 1f);
    }

    public void loop() {
        //TODO:telemetry
        telemetry.addData("lag: ", temp);
        //telemetry.addData("hoodstick: ", hoodStick);
        //telemetry.addData("temphood: ", tempHood);
        //telemetry.addData("hood pos: ", robot.hood.getCurrentPos());
        //telemetry.addData("turretTurn: ", turretTurn);
        telemetry.addData("turret pos: ", robot.turrets.getCurrentPosition());
        //telemetry.addData("test: ", checker);
        //telemetry.addData("shoot power: ", tempShootPower);
        //telemetry.addData("shooter power: ", robot.shooter.getCurrentSpeed());
        telemetry.addData("shoot PID: ", robot.shooter.getPID());
        //telemetry.addData("shoot pid on: ", shootPID);
        telemetry.addData("distance to goal: ", distance);
        //telemetry.addData("Hood Angle", robot.hood.getCurrentAngle());
        telemetry.addData("Hood Position", robot.hood.getCurrentPos());
        telemetry.addData("SIDE: ", sideID);



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


        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) { //20 blue 24 red
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                float ty = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetYDegrees();
                telemetry.addData("RED GOAL TX: ", tx);
                distance = (int) ((646.1125 - 410.15381) / Math.tan(Math.toRadians(ty)));
                telemetry.addData("ty: ", ty);
            }
        }
        catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see, manually adjust");
        }

        if (gamepad2.touchpad && Button.BTN_LIMELIGHT.canPress(timestamp)) {
            autoAiming = !autoAiming;
        }
        if (autoAiming) {
            robot.turret.setUseSquID(true);
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                if (Math.abs(tx) >= 1f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == sideID) { //20 blue, 24 red
                    calcPosition = (int) (-2.8081 * tx - 0.7685);
                    telemetry.addData("calc pos", calcPosition);
                    if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                        robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.61f);
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

        if(gamepad1.touchpad && Button.BTN_MIN.canPress(timestamp)) {
            turretReset = true;
            turretResetTimer = System.currentTimeMillis();
            autoAiming = false;
            robot.turret.setUseSquID(false,turretSquid.center);
            robot.turret.moveEncoderTo(turretSquid.center, 0.7f);
        }
        if(turretReset && System.currentTimeMillis()-turretResetTimer>400) {
            turretReset = false;
            turretResetTimer = 0;
            autoAiming = true;
        }

        if(gamepad1.dpad_up && Button.BTN_PLUS.canPress(timestamp)) {
            if(sideID == 20) {
                sideID = 24;
            } else if(sideID == 24) {
                sideID = 20;
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

        //indexer hard reset
        if(gamepad1.y && Button.INTAKE.canPress(timestamp) && canTurn) {
            robot.indexer.setTwo();
        }

        //auto kicker
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

        //manual kicker
        if (gamepad2.x && Button.BTN_KICKER.canPress(timestamp)) {
            if(!kicker){
                kicker = true;
                robot.kicker.setKick();
            } else if (kicker) {
                kicker = false;
                robot.kicker.setInit();
            }
        }


        //shoot
        if((gamepad2.right_trigger > 0.8 || gamepad1.right_bumper) && Button.BTN_FLYWHEEL.canPress(timestamp)) {
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

        if((gamepad1.dpad_left || gamepad2.right_bumper) && Button.INTAKE.canPress(timestamp)) {
            if(shootPID) {
                shootPID = false;
                hoodPID = false;
            } else if (!shootPID) {
                gamepad2.rumble(100);
                shootPID = true;
                hoodPID = false;
            }
        }
        if((gamepad1.dpad_right || gamepad2.left_bumper) && Button.INTAKE.canPress(timestamp)) {
            if(hoodPID) {
                shootPID = false;
                hoodPID = false;
            } else if (!hoodPID) {
                gamepad2.rumble(100);
                shootPID = false;
                hoodPID = true;
            }
        }

        if(shootPIDupdate) {
            robot.shooter.setUsePID(true, distance, robot.hood.getCurrentAngle());
            telemetry.addLine("shooter PID ON!!!");
        } else if(!shootPIDupdate){
            robot.shooter.setUsePID(false);
            telemetry.addLine("pid off");
        }
        if(hoodPIDupdate) {
            robot.hood.setAutoAdjust(true, robot.shooter, distance);
            robot.hood.setTargetDistance(distance);
            telemetry.addLine("hood PID on!");
        } else if(!hoodPIDupdate){
            robot.hood.setAutoAdjust(false, robot.shooter, 2000);
            robot.hood.setTargetDistance(2000);
            telemetry.addLine("pid off");
        }



        //shooter intake
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


        //manual hood
        if(!hoodPIDupdate){
            hoodStick = ((float) gamepad2.left_stick_y)*1f;
            if(Math.abs(hoodStick) >= 0.07) {
                tempHood = robot.hood.getCurrentPos() + hoodStick;
                tempHood = Math.max(robot.hood.MAX, Math.min(tempHood, robot.hood.INIT-1));
                robot.hood.setPosition(tempHood);
            }
        }

        //fast shoot
        if(gamepad1.left_bumper && Button.BTN_TTABLE.canPress(timestamp) && shooting){
            actionHandler.startFastShoot();
        } else if (gamepad1.left_bumper && Button.BTN_TTABLE.canPress(timestamp) && !shooting) {
            gamepad1.rumble(100);
        }

        if(gamepad2.guide && Button.BTN_MINUS.canPress(timestamp)) {
            robot.turret.setCenter((int)robot.turret.getCurrentPosition());
        }

        if(gamepad2.dpad_up && Button.BTN_128.canPress(timestamp) && !hoodPID) {
            robot.hood.setPosition(128);
        }

        if(gamepad2.dpad_right && Button.BTN_95.canPress(timestamp) && !hoodPID) {
            robot.hood.setPosition(95);
        }

    }
}
