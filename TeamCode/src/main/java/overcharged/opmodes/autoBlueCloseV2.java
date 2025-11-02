package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathBuilder;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.List;

import overcharged.actions.actions;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;
import overcharged.pedroPathing.Constants;
import overcharged.testModes.thinkTele1;

import com.bylazar.configurables.PanelsConfigurables;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.configurables.annotations.IgnoreConfigurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

@Autonomous(name = "blue goal close NEW", group = "0Autonomous")
public class autoBlueCloseV2 extends OpMode {
    private RobotMecanum robot;
    private ElapsedTime pathTimer;
    MultipleTelemetry telems;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private Limelight3A limelight;
    ElapsedTime temp; // lag time
    actions actionHandler = new actions(); //actions

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

    public static Pose startPose = new Pose(15, 114, Math.toRadians(90)); // heading: 90
    private static Pose firstShoot, firstBall, secondShoot, secondBall, endPose;

    public static PathChain firstScore, firstTake, secondScore, secondTake, thirdScore, goEnd;

    public void buildPoses() {
        firstShoot = new Pose(45, 98, Math.toRadians(180));
        firstBall = new Pose(17, 81, Math.toRadians(180));
        secondShoot = new Pose(58, 78, Math.toRadians(180));
        secondBall = new Pose(12, 56.5, Math.toRadians(180));
        endPose = new Pose(20, 72, Math.toRadians(180));
    }

    public void buildPaths() {
        firstScore = follower.pathBuilder()
                .addPath(new BezierCurve(startPose,new Pose(29, 113), firstShoot))
                .setLinearHeadingInterpolation(startPose.getHeading(), firstShoot.getHeading())
                .build();
        firstTake = follower.pathBuilder()
                .addPath(new BezierCurve(firstShoot,new Pose(63, 85), firstBall))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        secondScore = follower.pathBuilder()
                .addPath(new BezierLine(firstBall, secondShoot))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        secondTake = follower.pathBuilder()
                .addPath(new BezierCurve(secondShoot, new Pose(51, 57) ,secondBall))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        thirdScore = follower.pathBuilder()
                .addPath(new BezierLine(secondBall, secondShoot))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        goEnd = follower.pathBuilder()
                .addPath(new BezierLine(secondShoot, endPose))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
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
                robot.turret.moveEncoderTo(turretSquid.blueCloseAutoStart, 0.7f);
                setInitState(11);
                break;
            case 11:
                robot.indexer.setTwo();
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
                telemetry.addData("motif ID: ", obbyID);
                break;
        }
    }

    public void autoPath() {
        switch(pathState){
            case 10:
                follower.followPath(firstScore);
                robot.turret.setUseSquID(false, turretSquid.blueCloseFirstView);
                robot.turrets.moveEncoderTo(turretSquid.blueCloseFirstView, 0.7f);
                colorMode = colorState.GPP;
                setPathState(11);
                break;
            case 11:
                if(follower.getCurrentTValue() >0.08) {
                    autoTurret = true;
                    robot.shooter.setUsePID(true, distance, robot.hood.getCurrentAngle());
                    shootPIDupdate = true;
                    setPathState(12);
                }
                break;
            case 12:
                if(!follower.isBusy()) {
                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(39)));
                    setPathState(121);
                }
                break;
            case 121:
                if(pathTimer.milliseconds()>200) {
                    robot.intake.in();
                    actionHandler.startFastShoot();
                    setPathState(13);
                }
                break;
            case 13:
                if(pathTimer.milliseconds() > 2600) {
                    follower.followPath(firstTake);
                    //autoTurret = false;
                    setPathState(130);
                }
                break;
            case 130:
                if(follower.getCurrentTValue() > 0.3) {
                    follower.setMaxPower(0.5);
                    setPathState(14);
                }
                break;
            case 14:
                if(!follower.isBusy()){
                    follower.setMaxPower(1);
                    //robot.turrets.moveEncoderTo(turretSquid.blueCloseShootReset, 0.7f);
                    robot.turret.setUseSquID(false, turretSquid.blueCloseShootReset);
                    colorMode = colorState.GPP;
                    follower.followPath(secondScore);
                    setPathState(15);
                }
                break;
            case 15:
                if(!follower.isBusy()) {
                    //robot.intake.off();
                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(40.)));
                    setPathState(151);
                }
                break;
            case 151:
                if(pathTimer.milliseconds()>300) {
                    actionHandler.startFastShoot();
                    setPathState(16);
                }
                break;
            case 16:
                if(pathTimer.milliseconds() > 2600) {
                    follower.followPath(secondTake);
                    setPathState(160);
                }
                break;
            case 160:
                if(follower.getCurrentTValue() > 0.3) {
                    follower.setMaxPower(0.5);
                    setPathState(17);
                    break;
                }
                break;
            case 17:
                if(!follower.isBusy()){
                    follower.setMaxPower(1);
                    colorMode = colorState.PPG;
                    follower.followPath(thirdScore);
                    setPathState(18);
                }
                break;
            case 18:
                if(!follower.isBusy()) {
                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(40.5)));
                    setPathState(19);
                }
                break;
            case 19:
                if(pathTimer.milliseconds() > 300) {
                    actionHandler.startFastShoot();
                    setPathState(20);
                }
                break;
            case 20:
                if(pathTimer.milliseconds() > 2600) {
                    shootPIDupdate = false;
                    autoTurret = false;
                    robot.intake.off();
                    setPathState(21);
                }
                break;
            case 21:
                if(!follower.isBusy()) {
                    follower.followPath(goEnd);
                    setPathState(22);
                }
                break;
            case 22:
                if(!follower.isBusy()) {
                    setPathState(100);
                }
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
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) { //20 blue 24 red
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
                if (Math.abs(tx) >= 1.2f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) { //20 blue, 24 red
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


        if(obbyID == 21) { //gpp
            if(colorMode == colorState.GPP){ // yyy
                actionHandler.fastShootSeq();
            } else if (colorMode == colorState.PGP) { // nny
                actionHandler.fastShootSeq213();
            } else if (colorMode == colorState.PPG) { // nyn
                actionHandler.fastShootSeq321();
            }
        }
        else if (obbyID == 22) { //pgp
            if(colorMode == colorState.GPP){
                actionHandler.fastShootSeq213();
            } else if (colorMode == colorState.PGP) {
                actionHandler.fastShootSeq();
            } else if (colorMode == colorState.PPG) {
                actionHandler.fastShootSeq132();
            }
        }
        else if (obbyID == 23){ //ppg
            if(colorMode == colorState.GPP){
                actionHandler.fastShootSeq321();
            } else if (colorMode == colorState.PGP) {
                actionHandler.fastShootSeq132();
            } else if (colorMode == colorState.PPG) {
                actionHandler.fastShootSeq();
            }
        }

    }

    @Override
    public void init_loop() {
        initBody();
        telemetry.addLine("Init looping");
        telemetry.addLine("case: "+initState);
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
