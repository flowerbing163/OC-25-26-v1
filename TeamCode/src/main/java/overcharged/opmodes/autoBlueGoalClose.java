package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.BezierCurve;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.components.RobotMecanum;

import java.util.List;
import java.util.ArrayList;

@Autonomous(name = "blue goal close", group = "0Autonomous")
public class autoBlueGoalClose extends OpMode {
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
    private static Follower follower;
    long tempTime;

    public static Pose startPose = new Pose(13.908, 110.099); //heading: 0
    public static Pose shootPose = new Pose(51.576, 94.452); //heading: 144 deg

    public static PathBuilder builder = new PathBuilder(follower);

    public static PathChain startToShoot, shootToPPG, PPGtoShoot, shootToPGP, PGPtoShoot;

    public void buildPaths() {
        startToShoot = builder.addPath(new BezierLine(startPose, shootPose)).setLinearHeadingInterpolation(0, 143.7).build();
        shootToPPG = builder.addPath(new BezierCurve(shootPose, new Pose(51, 86), new Pose(17.095, 86))).setTangentHeadingInterpolation().build();
        PPGtoShoot = builder.addPath(new BezierLine(new Pose(17.095, 86), shootPose)).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(144)).build();
        shootToPGP = builder.addPath(new BezierCurve(shootPose, new Pose(51, 63.5), new Pose(17.095, 63.5))).setTangentHeadingInterpolation().build();
        PGPtoShoot = builder.addPath(new BezierLine(new Pose(17.095, 63.5), shootPose)).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(144)).build();
    }
    public void setInitState(int state) {
        initState = state;
        //pathTimer.reset();
        initBody();
    }
    public void setPathState(int state) {
        pathState = state;
        pathTimer.reset();
        autoPath();
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

    public void autoPath() {
        switch(pathState){
            case 10:
                follower.followPath(startToShoot, true);
                setPathState(11);

        }
    }

    @Override
    public void loop() {

    }

    @Override
    public void init() {
        initBody();

        buildPaths();
    }

    @Override
    public void start() {
        setPathState(10);
        autoPath();
    }


}
