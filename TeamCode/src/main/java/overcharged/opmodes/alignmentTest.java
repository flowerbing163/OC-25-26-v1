package overcharged.opmodes;

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
import overcharged.components.turret;

@Config
@TeleOp(name = "alignment test TOTR", group = "!!Teleop")
public class alignmentTest extends OpMode {
    RobotMecanum robot;
    Limelight3A limelight;

    float turretMove; //gear ratio is 1:25, for every one rotation of motor, 1 tooth of 25-teeth gear moves
    int aligned;
    float ticksPerRev = 537.7f; //14 gear to 28 gear, motor rotates two small gear teeth to rotate 1 big gear tooth,
    //every big tooth is 360/28 = 12.8571429 degrees, divide tx by 12.8571429 to get big teeth to move
    //multiply big teeth to move by 2, which is number of small teeth to move
    //multiply number of small teeth to move by (537.7/14)
    float totalTicksPerRev = 1075;
    //-100 + (tx/360) * 1075
    boolean testOn = false;
    int position;


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
        telemetry.addData("turret pos: ", robot.turret.getCurrentPosition());
        robot.turret.update();

//        if(gamepad1.a && Button.TURRET_LEFT.canPress(timestamp) && !testOn){
//            robot.turret.setUseSquID(true, aligned, 0.5f);
//            testOn = true;
//        } else if(gamepad1.a && Button.TURRET_LEFT.canPress(timestamp) && testOn){
//            robot.turret.setUseSquID(false, 0);
//            testOn = false;
//        }
        //if(!testOn) {
        try {
            float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
            telemetry.addData("tx: ", tx);
            if (Math.abs(tx) > 3f) {
                position = (int) (-(2.7651 * tx) + 8.75);
                telemetry.addData("encoder pos: ", robot.turret.getCurrentPosition());
                telemetry.addData("calc. pos: ", position);
                robot.turret.setUseSquID(true, position, 0.05f);
            }
//                if (tx < -1.50) {
//                    aligned = (int) (-((tx / 360) * 1075.4));
//                }
//                else if (tx > 1.50) {
//                    aligned = (int) (((tx/360) * 1075.4));
//                }
//                robot.turret.setUseSquID(true, aligned, 0.1f);

//            if (tx > 2.00){
//                robot.turret.setPower(0.1f);
//            }
//            else if (tx < -2.00){
//                robot.turret.setPower(-0.1f);
//            }
//            else{
//                robot.turret.setPower(0f);
//            }
//
//                turretMove = Math.abs(gamepad1.right_stick_x);
//                if (Math.abs(gamepad1.right_stick_x) > 0.1 && !testOn) {
//                    robot.turret.setPower(turretMove);
//                }
        } catch (IndexOutOfBoundsException e1) {
                telemetry.addLine("MY EYEE I CANT SEE");
//                robot.turret.setUseSquID(true, 0, 1f);
                //robot.turret.setUseSquID(false, 0, 0.1f);
                //robot.turret.setPower(1f);
        }
        //}
        //telemetry.addData("aligned: ", aligned);


    }
}
