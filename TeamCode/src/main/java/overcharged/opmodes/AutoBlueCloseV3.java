package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;

import overcharged.actions.Actions;
import overcharged.actions.PathStateHandler;
import overcharged.components.RobotMecanum;
import overcharged.components.turretSquid;
import overcharged.pedroPathing.Constants;

@Autonomous(name = "blue close v3", group = "0Autonomous")
public class AutoBlueCloseV3 extends OpMode {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoBlueCloseV3.class);

    private RobotMecanum robot;
    private ElapsedTime pathTimer;
    private MultipleTelemetry telems;
    private FtcDashboard dashboard = FtcDashboard.getInstance();
    private Limelight3A limelight;
    private ElapsedTime temp; // lag time
    private Actions actionHandler = new Actions(); //actions

    private ElapsedTime total; // total amt of time, end if too close to 30s
    private PathState pathState;
    private int initState;
    private boolean initDone;
    private int motifID = 67;
    private int obbyID;
    private Follower follower;

    private boolean autoTurret = false;
    private int distance = 0;

    static{
        firstShoot = new Pose(45, 98, Math.toRadians(180));
        firstBall = new Pose(17.5, 80, Math.toRadians(180));
        secondShoot = new Pose(58, 78, Math.toRadians(180));
        secondBall = new Pose(12, 55.5, Math.toRadians(180));
        thirdBall = new Pose(15, 31, Math.toRadians(180));
        endPose = new Pose(33, 85, Math.toRadians(180));
    }

    private int calcPosition;

    private boolean shootPIDupdate = false;

    private COLORSTATE colorMode = COLORSTATE.NONE;
    private enum COLORSTATE {
        GPP, //1
        PGP, //2
        PPG, //3
        NONE
    }

    public enum PathState {
        S10_FOLLOW_PATH(10),
        S101_START_TURRET(101),
        S11_START_AUTOTURRET(11),
        S12_SET_HOOD(12),
        S121_SHOOT(121),

        S100_TEST(100),
        ;

        private final int code;
        PathState(int code) { this.code = code; }
        public int getCode() { return code; }

        public static PathState fromCode(int code) {
            for (PathState s : values()) {
                if (s.code == code) return s;
            }
            throw new IllegalArgumentException("Unknown path state: " + code);
        }
    }



    private static Pose startPose = new Pose(15, 114, Math.toRadians(90)); // heading: 90
    private static Pose firstShoot, firstBall, secondShoot, secondBall, thirdBall, endPose;

    private static PathChain firstScore, firstTake, secondScore, secondTake, thirdScore, thirdTake, fourthScore, goEnd;

   public void buildPaths() {
       firstScore = follower.pathBuilder()
               .addPath(new BezierCurve(startPose,new Pose(29, 113), firstShoot))
               .setLinearHeadingInterpolation(startPose.getHeading(), firstShoot.getHeading())
               .build();
       firstTake = follower.pathBuilder()
               .addPath(new BezierCurve(firstShoot,new Pose(50, 76), firstBall))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       secondScore = follower.pathBuilder()
               .addPath(new BezierLine(firstBall, secondShoot))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       secondTake = follower.pathBuilder()
               .addPath(new BezierCurve(secondShoot, new Pose(52, 50) ,secondBall))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       thirdScore = follower.pathBuilder()
               .addPath(new BezierLine(secondBall, secondShoot))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       thirdTake = follower.pathBuilder()
               .addPath(new BezierCurve(secondShoot, new Pose(51, 31), thirdBall))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       fourthScore = follower.pathBuilder()
               .addPath(new BezierLine(thirdBall, secondShoot))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
       goEnd = follower.pathBuilder()
               .addPath(new BezierLine(secondShoot, endPose))
               .setConstantHeadingInterpolation(Math.toRadians(180))
               .build();
   }

    public void setInitState(int state) {
        LOGGER.info("setInitState start. state:"+state);
        initState = state;
        pathTimer.reset();
        //initBody();
        LOGGER.info("setInitState end. state:"+state);
    }

    public void initBody() {
        LOGGER.info("initBody start. initState:"+initState);
        if(initDone) {
            return;
        }
        switch(initState){
            case 10:
                // initialize turret
                robot.turret.setUseSquID(false);
                robot.turret.moveEncoderTo(turretSquid.blueCloseAutoStart, 0.8f);
                setInitState(11);
                break;
            case 11:
                // initialize indexer
                robot.indexer.setTwo();
                setInitState(13);
                break;
            case 13:
                //obbyID = motifID;
                // init done
                telemetry.addLine("INIT FINISHED");
                telemetry.addData("motif ID: ", obbyID);
                initDone = true;
                break;
        }
        LOGGER.info("initBody end. obbyID:"+obbyID);
        LOGGER.info("initBody end. initState:"+initState);
    }

    public void autoPath() {
        LOGGER.info("autoPath start. pathState:" + pathState);
        PathStateHandler handler = pathHandlers.get(pathState);
        if (handler != null) {
            handler.handle();
        } else {
            LOGGER.warn("No handler for pathState " + pathState);
        }
    }

//    public void autoPath() {
//        LOGGER.info("autoPath start. pathState:"+pathState);
//        switch(pathState){
//            case 10:
//                follower.followPath(firstScore);
//                colorMode = COLORSTATE.GPP;
//               //orderMode= EnumOrderMode.213;
//                setPathState(101);
//                break;
//            case 101:
//                //robot.turrets.moveEncoderTo(turretSquid.blueCloseFirstView, 0.8f);
//                robot.turret.setUseSquID(true, 115, 0.82f);
//                setPathState(11);
//                break;
//            case 11:
//                autoTurret = true;
//                if(follower.getCurrentTValue() > 0.2 || pathTimer.milliseconds()>400) {
////                    robot.turret.setUseSquID(true);
////                    autoTurret = true;
////                    robot.turret.setUseSquID(true, robot.turret.getMax(), 1f);
//                    robot.shooter.setUsePID(true, distance+272, robot.hood.getCurrentAngle());
//                    shootPIDupdate = true;
//                    setPathState(12);
//                }
//                break;
//            case 12:
//                if(!follower.isBusy()) {
//                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(46)));
//                    setPathState(121);
//                }
//                break;
//            case 121:
//                if(pathTimer.milliseconds()>200) {
//                    robot.intake.in();
//                    actionHandler.startFastShoot();
//                    setPathState(13);
//                }
//                break;
//            case 13:
//                if(pathTimer.milliseconds() > 2900) {
//                    //autoTurret = true;
//                    follower.followPath(firstTake);
//                    //autoTurret = false;
//                    setPathState(130);
//                }
//                break;
//            case 130:
//                if(follower.getCurrentTValue() > 0.3) {
//                    follower.setMaxPower(0.5);
//                    setPathState(14);
//                }
//                break;
//            case 14:
//                if(!follower.isBusy()){
//                    follower.setMaxPower(1);
//                    //robot.turrets.moveEncoderTo(turretSquid.blueCloseShootReset, 0.7f);
//                    //robot.turret.setUseSquID(false, turretSquid.blueCloseShootReset);
//                    colorMode = COLORSTATE.GPP;
//                    follower.followPath(secondScore);
//                    setPathState(15);
//                }
//                break;
//            case 15:
//                if(!follower.isBusy()) {
//                    //robot.intake.off();
//                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(41)));
//                    setPathState(151);
//                }
//                break;
//            case 151:
//                if(pathTimer.milliseconds()>300) {
//                    actionHandler.startFastShoot();
//                    setPathState(16);
//                }
//                break;
//            case 16:
//                if(pathTimer.milliseconds() > 2900) {
//                    follower.followPath(secondTake);
//                    setPathState(160);
//                }
//                break;
//            case 160:
//                if(follower.getCurrentTValue() > 0.3) {
//                    follower.setMaxPower(0.5);
//                    setPathState(17);
//                }
//                break;
//            case 17:
//                if(!follower.isBusy()){
//                    follower.setMaxPower(1);
//                    colorMode = COLORSTATE.PPG;
//                    follower.followPath(thirdScore);
//                    setPathState(18);
//                }
//                break;
//            case 18:
//                if(!follower.isBusy()) {
//                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(41)));
//                    setPathState(19);
//                }
//                break;
//            case 19:
//                if(pathTimer.milliseconds() > 300) {
//                    actionHandler.startFastShoot();
//                    setPathState(20);
//                }
//                break;
//            case 20:
//                if(pathTimer.milliseconds() > 3000) {
////                    shootPIDupdate = false;
//////                    autoTurret = false;
////                    robot.intake.off();
//                    follower.followPath(thirdTake);
//                    setPathState(21);
//                }
//                break;
//            case 21:
//                if(follower.getCurrentTValue() > 0.3) {
//                    follower.setMaxPower(0.6);
//                    setPathState(22);
//                }
//                break;
//            case 22:
//                if(!follower.isBusy()) {
//                    follower.setMaxPower(1);
//                    colorMode = COLORSTATE.PGP;
//                    follower.followPath(fourthScore);
//                    setPathState(23);
//                }
//                break;
//            case 23:
//                if(!follower.isBusy()) {
//                    robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(41)));
//                    setPathState(24);
//                }
//                break;
//            case 24:
//                if(pathTimer.milliseconds() > 300) {
//                    actionHandler.startFastShoot();
//                    setPathState(25);
//                }
//                break;
//            case 25:
//                if(pathTimer.milliseconds() > 2900) {
//                    shootPIDupdate = false;
//                    robot.intake.off();
//                    setPathState(26);
//                }
//                break;
//            case 26:
//                if(!follower.isBusy()) {
//                    follower.followPath(goEnd);
//                    setPathState(27);
//                }
//                break;
//            case 27:
//                if(!follower.isBusy()) {
//                    setPathState(100);
//                }
//                break;
//            case 100:
//                telemetry.addLine("TEST CASE TIME");
//                break;
//        }
//        LOGGER.info("autoPath end. pathState:"+pathState);
//    }

    public void setPathState(PathState state) {
        LOGGER.info("setPathState start. state:"+state);
        pathState = state;
        pathTimer.reset();
        //autoPath();
        LOGGER.info("setPathState end. state:"+state);
    }

    @Override
    public void loop() {
        LOGGER.info("loop start");

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
            if (limelight.isRunning() && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) { //TODO: 20 blue 24 red
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
                if (Math.abs(tx) >= 1f && limelight.getLatestResult().getFiducialResults().get(0).getFiducialId() == 20) { //TODO: 20 blue, 24 red
                    calcPosition = (int) (-2.351157407 * tx);
                    telemetry.addData("calc pos", calcPosition);
//                    if (robot.turret.getCurrentPosition() + calcPosition <= robot.turret.getMax() + 3 && robot.turret.getCurrentPosition() + calcPosition >= robot.turret.getMin()) {
//                        robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.7f);
//                    }
                    robot.turret.setUseSquID(true, (int) robot.turret.getCurrentPosition() + calcPosition, 0.7f);
                }
            }
            catch (IndexOutOfBoundsException e1){
                telemetry.addLine("cant see vro :skull:");
            }
        } else if (!autoTurret){
            robot.turret.setUseSquID(false);
        }

        if(shootPIDupdate) {
            robot.shooter.setUsePID(true, distance+282, robot.hood.getCurrentAngle());
            telemetry.addLine("shooter PID ON!!!");
        } else if(!shootPIDupdate){
            robot.shooter.setUsePID(false);
        }

        
        int[] currentOrder = this.getFastShootOrder(obbyID, colorMode);
        actionHandler.fastShootSeqByOrder(currentOrder);

        LOGGER.info("loop end");
    }

    public int[] getFastShootOrder(int obbyID , COLORSTATE currentColorState){
        if(obbyID == 21) { //gpp
            if(currentColorState == COLORSTATE.GPP){ // yyy
                //231
                return new int[]{1,3,2};
            } else if (colorMode == COLORSTATE.PGP) { // nny
                //213
                return new int[]{2,1,3};
            } else if (colorMode == COLORSTATE.PPG) { // nyn
                //321
                return new int[]{3,1,2};
            }
        }

        else if (obbyID == 22) { //pgp
            if(colorMode == COLORSTATE.GPP){
                return new int[]{2,1,3};
            } else if (colorMode == COLORSTATE.PGP) {
                return new int[]{1,2,3};
            } else if (colorMode == COLORSTATE.PPG) {
                return new int[]{2,3,1};
            }
        }
        else if (obbyID == 23){ //ppg
            if(colorMode == COLORSTATE.GPP){
                actionHandler.fastShootSeq321();
                return new int[]{2,3,1};
            } else if (colorMode == COLORSTATE.PGP) {
                return new int[]{1,3,2};
            } else if (colorMode == COLORSTATE.PPG) {
                return new int[]{1,2,3};
            }
        }

        return new int[]{2,3,1};
    }

    @Override
    public void init_loop() {
        LOGGER.info("init_loop start");
        if(!initDone) {
            initBody();
        }
        try {
            LLResult result = limelight.getLatestResult();
            motifID = result.getFiducialResults().get(0).getFiducialId();
        } catch (IndexOutOfBoundsException e1) {
            telemetry.addLine("Cannot see");
            motifID = 21;
        }
        telemetry.addLine("Init looping");
        telemetry.addLine("case: "+initState);
        LOGGER.info("init_loop end");
    }

    private final EnumMap<PathState, PathStateHandler> pathHandlers =
            new EnumMap<>(PathState.class);

    private void initPathHandlers() {
        pathHandlers.put(PathState.S10_FOLLOW_PATH, () -> {
            follower.followPath(firstScore);
            colorMode = COLORSTATE.GPP;
            setPathState(PathState.S101_START_TURRET);
        });

        pathHandlers.put(PathState.S101_START_TURRET, () -> {
            //robot.turrets.moveEncoderTo(turretSquid.blueCloseFirstView, 0.8f);
            robot.turret.setUseSquID(true, 115, 0.82f);
            setPathState(PathState.S11_START_AUTOTURRET);
        });

        pathHandlers.put(PathState.S11_START_AUTOTURRET, () -> {
            autoTurret = true;
            if (follower.getCurrentTValue() > 0.2 || pathTimer.milliseconds() > 400) {
                robot.shooter.setUsePID(true, distance + 272, robot.hood.getCurrentAngle());
                shootPIDupdate = true;
                setPathState(PathState.S12_SET_HOOD);
            }
        });

        pathHandlers.put(PathState.S12_SET_HOOD, () -> {
            if(!follower.isBusy()) {
                robot.hood.setPosition(robot.hood.angToPos(Math.toRadians(46)));
                setPathState(PathState.S121_SHOOT);
            }
        });

        pathHandlers.put(PathState.S121_SHOOT, () -> {
            if(pathTimer.milliseconds()>200) {
                robot.intake.in();
                actionHandler.startFastShoot();
                setPathState(PathState.S100_TEST);
            }
        });

        pathHandlers.put(PathState.S100_TEST, () -> {
            telemetry.addLine("TEST CASE TIME");
        });


    }

    @Override
    public void init() {
        LOGGER.info("init start");
        telems = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);
        robot = new RobotMecanum(this, true, false);
        pathTimer = new ElapsedTime();
        temp = new ElapsedTime();
        initDone = false;

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(0);
        limelight.start();

        this.initPathHandlers();
        follower = Constants.createFollower(hardwareMap);

        //
        //buildPoses();
        buildPaths();
        follower.setStartingPose(startPose);

        pathState = PathState.S10_FOLLOW_PATH;

        actionHandler.fastShootSys(robot);
        setInitState(10);
        LOGGER.info("init end");
    }

    @Override
    public void start() {
        LOGGER.info("start start");
        total = new ElapsedTime();
        autoPath();
        LOGGER.info("start end");
    }
}
