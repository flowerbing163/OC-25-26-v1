package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import overcharged.components.Button;

import overcharged.components.RobotMecanum;

@Config
@TeleOp(name = "alignment test sonic", group = "!!Teleop")
public class alignmentTestBlue extends OpMode {
    RobotMecanum robot;
    Limelight3A limelight;

    float turretMove; //gear ratio is 1:25, for every one rotation of motor, 1 tooth of 25-teeth gear moves
    int aligned;
    boolean testOn = false;
    boolean autoAiming = true;
    int calcPosition;


    public void init(){
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot = new RobotMecanum(this, false, false);
        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(0);
        limelight.start();
        robot.turret.setUseSquID(true, 0);
    }

    public void loop(){
        //Driving
        double y = gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x * 1.1;
        double rx = -gamepad1.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        double frontLeftPower = ((y + x + rx) / denominator);
        double backLeftPower = ((y - x + rx) / denominator);
        double frontRightPower = ((y - x - rx) / denominator);
        double backRightPower = ((y + x - rx) / denominator);

        robot.driveLeftFront.setPower(frontLeftPower);
        robot.driveLeftBack.setPower(backLeftPower);
        robot.driveRightFront.setPower(frontRightPower);
        robot.driveRightBack.setPower(backRightPower);
        long timestamp = System.currentTimeMillis();
        robot.turret.update();

        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) {
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
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                telemetry.addData("current pos", robot.turret.getCurrentPosition());
                telemetry.addData("tx: ", tx);
                if (Math.abs(tx) >= 1.75f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) { //20 blue, 24 red
                    calcPosition = -((int) (2.3511574*tx));
                    if (calcPosition >= -423 && calcPosition <= 423) {
                        telemetry.addData("calc pos", calcPosition);
                        robot.turret.setUseSquID(true, calcPosition, 0.7f);
                    }

                }
            } catch (IndexOutOfBoundsException e1) {
                telemetry.addLine("Cannot see, manually adjust");
            }
        }
        if(!autoAiming) {
            robot.turret.setUseSquID(false);
            float turretTurn = -(gamepad2.right_stick_x)*0.5f;
            if(Math.abs(turretTurn) >= 0.05) {
                robot.turrets.setPower((float)turretTurn);
            } else if (Math.abs(turretTurn) < 0.05){
                robot.turrets.setPower(0);
            }
        }
//        if(gamepad1.x && Button.BTN_LIMELIGHT.canPress(timestamp)) {
//            if(!autoAiming) {
//                try {
//                    float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
//                    telemetry.addData("tx: ", tx);
//                    if (Math.abs(tx) > 2f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) {
//                        calcPosition = (int) (-(2.35115741) * tx);
//                        telemetry.addData("encoder pos: ", robot.turret.getCurrentPosition());
//                        telemetry.addData("calc. pos: ", calcPosition);
//                        robot.turret.setUseSquID(true, calcPosition, .7f);
//                    }
//                } catch (IndexOutOfBoundsException e1){
//                    telemetry.addLine("I can't see, manual adjust");
//                }
//                autoAiming = true;
//            }
//            else {
//                robot.turret.setUseSquID(false, 0);
//                if (Math.abs(gamepad2.right_stick_x) >= 0.2) {
//                    robot.turret.setPower(gamepad2.right_stick_x);
//                }
//                autoAiming = false;
//            }
//        }
        //}
        //telemetry.addData("aligned: ", aligned);


    }
}
