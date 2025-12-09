package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import overcharged.components.RobotMecanum;

@Config
@TeleOp(name = "alignment test zero'd", group = "!!Teleop")
public class alignmentTestZero extends OpMode {
    RobotMecanum robot;
    Limelight3A limelight;

    float turretMove;
    int aligned;
    boolean testOn = false;
    boolean autoAiming = false;
    int calcPosition;


    public void init(){
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot = new RobotMecanum(this, false, false);
        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(1);
        limelight.start();
        robot.turret.setUseSquID(true,0,1f);
    }

    public void loop(){
        long timestamp = System.currentTimeMillis();
        robot.turret.update();

//        if(gamepad1.a && Button.TURRET_LEFT.canPress(timestamp) && !testOn){
//            robot.turret.setUseSquID(true, aligned, 0.5f);
//            testOn = true;
//        } else if(gamepad1.a && Button.TURRET_LEFT.canPress(timestamp) && testOn){
//            robot.turret.setUseSquID(false, 0);
//            testOn = false;
//        }
        try {
            float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
            telemetry.addData("tx: ", tx);
            if (Math.abs(tx) > 2f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) {
                calcPosition = -((int) ((2.3511574 * tx + .5)));
                telemetry.addData("current pos", robot.turret.getCurrentPosition());
                telemetry.addData("calc pos", calcPosition);
                robot.turret.setUseSquID(true, 0, 0.75f);
            }
        }
        catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see, manually adjust");
        }
//        if(gamepad1.x && Button.BTN_TRACKING.canPress(timestamp)) {
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
