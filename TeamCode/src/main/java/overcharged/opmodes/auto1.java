package overcharged.opmodes;

import static overcharged.config.RobotConstants.TAG_SL;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

//import com.pedropathing.follower.Follower;
//import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.drivetrains.Mecanum;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer;
//import com.pedropathing.localization.Localizer;
//import com.pedropathing.paths.PathConstraints;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.hardware.HardwareMap;

import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.colorSensor;

import java.util.List;
import java.util.ArrayList;

@Autonomous(name = "protoAuto", group = "0Autonomous")
public class auto1 extends OpMode {
    private RobotMecanum robot;
    private ElapsedTime pathTimer;
    MultipleTelemetry telems;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private Limelight3A limelight;
    ElapsedTime temp;
    private int pathState;
    private int initState;
    public int motifID;
    List<Character> motif = new ArrayList<>();
    //private Follower follower;
    long tempTime;


    public void setInitState(int state) {
        initState = state;
        //pathTimer.reset();
        initBody();
    }

    public void initBody() {
        switch(initState){
            case 10:
                telems = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);
                robot = new RobotMecanum(this, true, false);
                pathTimer = new ElapsedTime();
                temp = new ElapsedTime();
                setInitState(11);
            case 11:
                limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
                limelight.pipelineSwitch(0);
                limelight.start();
                setInitState(12);
            case 12:
                LLResult result = limelight.getLatestResult();
                motifID = result.getFiducialResults().get(0).getFiducialId();
                if(motifID == 21){
                    motif.add('G');
                    motif.add('P');
                    motif.add('P');
                }
                else if (motifID == 22){
                    motif.add('P');
                    motif.add('G');
                    motif.add('P');
                }
                else if (motifID == 23){
                    motif.add('P');
                    motif.add('P');
                    motif.add('G');
                }
                telemetry.addLine(String.valueOf(motif.get(0) + motif.get(1) + motif.get(2)));
        }
    }

    @Override
    public void loop() {

    }

    @Override
    public void init() {
        initBody();
    }

}
