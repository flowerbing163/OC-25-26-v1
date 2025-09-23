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
    float ticksPerRev = 751.8f; //ticks per revolution for turret motor


    public void init(){
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot = new RobotMecanum(this, false, false);
        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(1);
        limelight.start();
        robot.turret.setUseSquID(true,200,1f);
        robot.turret.setPower(0f);
    }

    public void loop(){
        robot.turret.update();
        try {
            float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
            telemetry.addData("tx: ", tx);
            /*aligned = (int) (tx/14.4 * 751.8);
            robot.turret.moveEncoderTo(aligned, 1f);
            if (Math.abs(tx) < 1.50) {
                robot.turret.setPower(0);
            }*/
            /*if (tx > 1.50){
                //robot.turret.setPower(0.5f);
            }
            if (tx < 1.50){
                //robot.turret.setPower(-0.5f);
            }*/
        } catch(IndexOutOfBoundsException e1) {
            telemetry.addLine("MY EYEE I CANT SEE");
        }
    }
}
