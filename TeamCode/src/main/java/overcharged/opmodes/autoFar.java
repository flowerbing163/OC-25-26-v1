package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

import overcharged.actions.Actions;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;
import overcharged.pedroPathing.Constants;

@Autonomous(name = "auto far RP", group = "0Autonomous")
public class autoFar extends OpMode {
    private RobotMecanum robot;
    private ElapsedTime pathTimer;
    MultipleTelemetry telems;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private Limelight3A limelight;
    ElapsedTime temp; // lag time
    Actions actionHandler = new Actions(); //actions

    ElapsedTime total; // total amt of time, end if too close to 30s
    private int pathState;
    private int initState;
    public int motifID = 21;
    List<Character> motif = new ArrayList<>();
    int obbyID;
    private Follower follower;

    boolean autoTurret = false;
    int distance = 0;
    int calcPosition;

    boolean shootPIDupdate = false;

    colorState colorMode = colorState.NONE;
    public enum colorState {
        GPP, //1
        PGP, //2
        PPG, //3
        NONE
    }

    public static Pose startPose = new Pose(56, 6.75, Math.toRadians(90)); // heading: 90
    private static Pose firstShoot, firstBall, secondShoot, secondBall, endPose;

    public static PathChain firstScore, firstTake, secondScore, secondTake, thirdScore, forward;


    public void buildPoses() {
//        firstShoot = new Pose(99, 98, Math.toRadians(0));
//        firstBall = new Pose(126.5, 80.5, Math.toRadians(0));
//        secondShoot = new Pose(87.5, 78, Math.toRadians(0));
//        secondBall = new Pose(131.5, 56.5, Math.toRadians(0));
        endPose = new Pose(56, 40, Math.toRadians(90));

    }

    public void buildPaths() {
//        firstScore = follower.pathBuilder()
//                .addPath(new BezierCurve(startPose,new Pose(115, 113), firstShoot))
//                .setLinearHeadingInterpolation(startPose.getHeading(), firstShoot.getHeading())
//                .build();
//        firstTake = follower.pathBuilder()
//                .addPath(new BezierCurve(firstShoot,new Pose(81, 85), firstBall))
//                .setConstantHeadingInterpolation(Math.toRadians(0))
//                .build();
//        secondScore = follower.pathBuilder()
//                .addPath(new BezierLine(firstBall, secondShoot))
//                .setConstantHeadingInterpolation(Math.toRadians(0))
//                .build();
//        secondTake = follower.pathBuilder()
//                .addPath(new BezierCurve(secondShoot, new Pose(93, 57) ,secondBall))
//                .setConstantHeadingInterpolation(Math.toRadians(0))
//                .build();
//        thirdScore = follower.pathBuilder()
//                .addPath(new BezierLine(secondBall, secondShoot))
//                .setConstantHeadingInterpolation(Math.toRadians(0))
//                .build();
        forward = follower.pathBuilder().addPath(new BezierLine(startPose, endPose)).setConstantHeadingInterpolation(Math.toRadians(90)).build();
    }

    public void setInitState(int state) {
        initState = state;
        pathTimer.reset();
        initBody();
    }

    public void initBody() {
        switch(initState){
            case 10:
                robot.turret.setUseSquID(false);
                robot.turret.moveEncoderTo(turretSquid.redCloseAutoStart, 0.7f);
                setInitState(11);
                break;
            case 11:
                setInitState(12);
                break;
            case 12:
                try {
                    LLResult result = limelight.getLatestResult();
                    motifID = result.getFiducialResults().get(0).getFiducialId();
                } catch (IndexOutOfBoundsException e1) {
                    telemetry.addLine("Cannot see");
                }
                if(motifID == 21){
                    motif.add('G');
                    motif.add('P');
                    motif.add('P');
                    obbyID = motifID;
                    setInitState(13);
                }
                else if (motifID == 22){
                    motif.add('P');
                    motif.add('G');
                    motif.add('P');
                    obbyID = motifID;
                    setInitState(13);
                }
                else if (motifID == 23){
                    motif.add('P');
                    motif.add('P');
                    motif.add('G');
                    obbyID = motifID;
                    setInitState(13);
                }
                else {
                    obbyID = 21;
                }
                telemetry.addLine(String.valueOf(motif.get(0) + motif.get(1) + motif.get(2)));
                break;
            case 13:
                telemetry.addLine("INIT FINISHED");
                telemetry.addLine(String.valueOf(motif.get(0) + motif.get(1) + motif.get(2)));
                break;
        }
    }

    public void autoPath() {
        switch(pathState){
            case 10:
                follower.followPath(forward);
                autoTurret = true;
                setPathState(100);
                break;
            case 100:
                telemetry.addLine("TEST CASE TIME");
                break;
        }
    }

    public void setPathState(int state) {
        pathState = state;
        pathTimer.reset();
        autoPath();
    }

    @Override
    public void loop() {
        temp.reset();
        follower.update();
        autoPath();
        robot.turret.update();
        robot.shooter.update();

        telemetry.addLine("lag: " + temp);
        telemetry.addLine("position: " + follower.getPose());
        telemetry.addLine("heading: " + follower.getTotalHeading());
        telemetry.addLine("case: " + pathState);
        telemetry.addLine("SHOOTER PID: " + robot.shooter.getPID());

        try {
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) { //20 blue 24 red
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                float ty = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetYDegrees();
                telemetry.addData("BLUE GOAL TX: ", tx);
                distance = (int) ((646.1125 - 406.15381) / Math.tan(Math.toRadians(ty)));
                telemetry.addData("ty: ", ty);
            }
        }
        catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see, manually adjust");
        }

        if(autoTurret) {
            robot.turret.setUseSquID(true);
            try {
                float tx = (float) limelight.getLatestResult().getFiducialResults().get(0).getTargetXDegrees();
                if (Math.abs(tx) >= 1.2f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 24) { //20 blue, 24 red
                    calcPosition = (int) (-2.8081 * tx - 0.7685);
                    telemetry.addData("calc pos", calcPosition);
                    if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                        robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.68f);
                    }
                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("cant see vro :skull:");
            }
        } else if (!autoTurret){
            robot.turret.setUseSquID(false, calcPosition );
        }

        if(shootPIDupdate) {
            robot.shooter.setUsePID(true, distance, robot.hood.getCurrentAngle());
            telemetry.addLine("shooter PID ON!!!");
        } else if(!shootPIDupdate){
            robot.shooter.setUsePID(false);
        }


//        if(obbyID == 21) { //gpp
//            if(colorMode == colorState.GPP){ // yyy
//                actionHandler.fastShootSeq();
//            } else if (colorMode == colorState.PGP) { // nny
//                actionHandler.fastShootSeq213();
//            } else if (colorMode == colorState.PPG) { // nyn
//                actionHandler.fastShootSeq321();
//            }
//        }
//        else if (obbyID == 22) { //pgp
//            if(colorMode == colorState.GPP){
//                actionHandler.fastShootSeq213();
//            } else if (colorMode == colorState.PGP) {
//                actionHandler.fastShootSeq();
//            } else if (colorMode == colorState.PPG) {
//                actionHandler.fastShootSeq132();
//            }
//        }
//        else if (obbyID == 23){ //ppg
//            if(colorMode == colorState.GPP){
//                actionHandler.fastShootSeq321();
//            } else if (colorMode == colorState.PGP) {
//                actionHandler.fastShootSeq132();
//            } else if (colorMode == colorState.PPG) {
//                actionHandler.fastShootSeq();
//            }
//        }

    }

    @Override
    public void init_loop() {
//        initBody();
//        telemetry.addLine("Init looping");
//        telemetry.addLine("case: "+initState);
    }

    @Override
    public void init() {
        telems = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);
        robot = new RobotMecanum(this, true, false);
        pathTimer = new ElapsedTime();
        temp = new ElapsedTime();

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(0);
        limelight.start();

        follower = Constants.createFollower(hardwareMap);
        buildPoses();
        buildPaths();
        follower.setStartingPose(startPose);

        actionHandler.fastShootSys(robot);
        setInitState(10);
    }

    @Override
    public void start() {
        total = new ElapsedTime();
        setPathState(10);
        autoPath();
    }
}
