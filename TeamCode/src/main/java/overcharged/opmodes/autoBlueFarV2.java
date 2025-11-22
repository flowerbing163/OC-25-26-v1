package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
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

@Autonomous(name = "blue FAR", group = "0Autonomous")
public class autoBlueFarV2 extends OpMode {
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
    public int motifID = 67;
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

    public static Pose startPose = new Pose(43.8, 8.2, Math.toRadians(180)); // heading: 90
    private static Pose firstBall, secondShoot, secondBall1, secondBall2, endPose;

    public static PathChain firstTake, secondScore, secondTake, thirdScore, goEnd;

    public void buildPoses() {
        firstBall = new Pose(15, 13.5, Math.toRadians(180));
        secondShoot = new Pose(58, 78, Math.toRadians(180));
        secondBall1 = new Pose(8.7, 26, Math.toRadians(180));
        secondBall2 = new Pose(8.7, 8.2, Math.toRadians(180));
        endPose = new Pose(24, 48, Math.toRadians(180));
    }

    public void buildPaths() {
        firstTake = follower.pathBuilder()
                .addPath(new BezierCurve(startPose,new Pose(51, 36.5), firstBall))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        secondScore = follower.pathBuilder()
                .addPath(new BezierLine(firstBall, startPose))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        secondTake = follower.pathBuilder()
                .addPath(new BezierCurve(startPose, new Pose(27.5, 39), secondBall1))
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(270))
                .addPath(new BezierLine(secondBall1, secondBall2))
                .setConstantHeadingInterpolation(Math.toRadians(270))
                .addPath(new BezierLine(secondBall2, startPose))
                .setConstantHeadingInterpolation(Math.toRadians(270))
                .build();
        goEnd = follower.pathBuilder()
                .addPath(new BezierLine(startPose, endPose))
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
                robot.turret.moveEncoderTo(turretSquid.blueCloseAutoStart, 0.8f);
                setInitState(11);
                break;
            case 11:
                robot.indexer.setTwo();
                setInitState(12);
                break;
            case 12:
                if(motifID == 21){
                    obbyID = motifID;
                    setInitState(13);
                }
                else if (motifID == 22){
                    obbyID = motifID;
                    setInitState(13);
                }
                else if (motifID == 23){
                    obbyID = motifID;
                    setInitState(13);
                }
                else {
                    obbyID = 21;
                }
                break;
            case 13:
                obbyID = motifID;
                telemetry.addLine("INIT FINISHED");
                telemetry.addData("motif ID: ", obbyID);
                break;
        }
    }

    public void autoPath() {
        switch(pathState){
            case 10:
                colorMode = colorState.GPP;
                autoTurret = true;
                shootPIDupdate = true;
                setPathState(11);
                break;
            case 11:
                if(pathTimer.milliseconds() > 400) {
                    actionHandler.startFastShoot();
                    setPathState(12);
                }
                break;
            case 12:
                if(pathTimer.milliseconds() > 3000) {
                    follower.followPath(firstTake);
                    robot.intake.in();
                    colorMode = colorState.PGP;
                    setPathState(13);
                }
                break;
            case 13:
                if(!follower.isBusy()) {
                    follower.followPath(secondScore);
                    setPathState(14);
                }
                break;
            case 14:
                if(!follower.isBusy()) {
                    actionHandler.startFastShoot();
                    setPathState(15);
                }
                break;
            case 15:
                if(pathTimer.milliseconds()>3300) {
                    follower.followPath(secondTake);
                    setPathState(16);
                }
                break;
            case 16:
                if(follower.getCurrentTValue()>0.4) {
                    follower.setMaxPower(0.5f);
                    setPathState(17);
                }
                break;
            case 17:
                if(follower.getCurrentTValue()>0.8) {
                    follower.setMaxPower(1f);
                    setPathState(18);
                }
                break;
            case 18:
                if(!follower.isBusy()) {
                    actionHandler.startFastShoot();
                    setPathState(19);
                }
                break;
            case 19:
                if(pathTimer.milliseconds()>3300) {
                    follower.followPath(goEnd);
                    setPathState(20);
                }
                break;
            case 20:
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
        telemetry.addLine("turret pos: " + robot.turret.getCurrentPosition());
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
                if (Math.abs(tx) >= 1f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) { //20 blue, 24 red
                    calcPosition = (int) (-2.8081 * tx - 0.7685);
                    telemetry.addData("calc pos", calcPosition);
                    if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() + 3 && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
                        robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.7f);
                    }
                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("cant see vro :skull:");
            }
        } else if (!autoTurret){
            robot.turret.setUseSquID(false);
        }

        if(shootPIDupdate) {
            robot.shooter.setUsePID(true, distance+260, robot.hood.getCurrentAngle());
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
        try {
            LLResult result = limelight.getLatestResult();
            motifID = result.getFiducialResults().get(0).getFiducialId();
        } catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see");
            motifID = 21;
        }
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
