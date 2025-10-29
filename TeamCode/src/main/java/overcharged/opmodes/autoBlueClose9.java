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

import overcharged.actions.actions;
import overcharged.components.RobotMecanum;
import overcharged.pedroPathing.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Autonomous(name = "blue goal close", group = "0Autonomous")
public class autoBlueClose9 extends OpMode {
    private RobotMecanum robot;
    private ElapsedTime pathTimer;
    MultipleTelemetry telems;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    private Limelight3A limelight;
    ElapsedTime temp; // lag time
    actions actionHandler = new actions();

    ElapsedTime total; // total amt of time, end if too close to 30s
    private int pathState;
    private int initState;
    public int motifID;
    List<Character> motif = new ArrayList<>();
    int obbyID;
    private static Follower follower;
    long tempTime;
    long shootTimer = 0;
    int shootStep = 0;
    int shootRepStep = 0;


    public static Pose startPose = new Pose(13.526, 111.907); // heading: 0
    public static Pose shootPose = new Pose(48, 96); // heading: 141 deg

    public static PathBuilder builder = new PathBuilder(follower);

    public static PathChain startToShoot, shootToPPG, PPGtoShoot, shootToPGP, PGPtoShoot, shootToEnd;

    public void buildPaths() {
        startToShoot = builder.addPath(new BezierLine(startPose, shootPose)).setLinearHeadingInterpolation(0, 141).build();
        shootToPPG = builder.addPath(new BezierCurve(shootPose, new Pose(48, 83.846), new Pose(17.139, 83.846))).setTangentHeadingInterpolation().build();
        PPGtoShoot = builder.addPath(new BezierLine(new Pose(17.139, 83.846), shootPose)).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(141)).build();
        shootToPGP = builder.addPath(new BezierCurve(shootPose, new Pose(48, 59.482), new Pose(17.139, 59.482))).setTangentHeadingInterpolation().build();
        PGPtoShoot = builder.addPath(new BezierLine(new Pose(17.139, 59.482), shootPose)).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(141)).build();
        shootToEnd = builder.addPath(new BezierLine(shootPose, new Pose(18, 69.732))).setLinearHeadingInterpolation(Math.toRadians(141), Math.toRadians(180)).build();
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
                    obbyID = motifID;
                }
                else if (motifID == 22){
                    motif.add('P');
                    motif.add('G');
                    motif.add('P');
                    obbyID = motifID;
                }
                else if (motifID == 23){
                    motif.add('P');
                    motif.add('P');
                    motif.add('G');
                    obbyID = motifID;
                }
                else {
                    obbyID = 21;
                }
                telemetry.addLine(String.valueOf(motif.get(0) + motif.get(1) + motif.get(2)));
        }
    }

    public void autoPath() {
        switch(pathState){
            case 10:
                follower.followPath(startToShoot, true);
                setPathState(11);
                break;
            case 11: // robot preload: one = green, two = purp, three = purp
                robot.intake.in();
                robot.shooter.shoot();
//                robot.hood.setClose(); TODO: calibrate hood position for (48, 96)
                if (obbyID == 21 || motif.equals(Arrays.asList('G', 'P', 'P'))) {
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                    if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
                        robot.indexer.setOne();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 2 && System.currentTimeMillis() - shootTimer > 250) {
                        robot.kicker.setKick();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 3 && System.currentTimeMillis() - shootTimer > 300) {
                        robot.kicker.setInit();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 0) {
                        robot.indexer.setTwo();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 1) {
                        robot.indexer.setThree();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 350 && shootRepStep == 2) {
                        robot.indexer.setTwo();
                        shootStep = 0;
                        shootRepStep = 0;
                        shootTimer = 0;
                    }
                }
                else if (obbyID == 22 || motif.equals(Arrays.asList('P', 'G', 'P'))) {
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                    if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
                        robot.indexer.setTwo();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 2 && System.currentTimeMillis() - shootTimer > 250) {
                        robot.kicker.setKick();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 3 && System.currentTimeMillis() - shootTimer > 300) {
                        robot.kicker.setInit();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 0) {
                        robot.indexer.setOne();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 1) {
                        robot.indexer.setThree();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 350 && shootRepStep == 2) {
                        robot.indexer.setTwo();
                        shootStep = 0;
                        shootRepStep = 0;
                        shootTimer = 0;
                    }
                }
                else if (obbyID == 23 || motif.equals(Arrays.asList('P', 'P', 'G'))) {
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                    if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
                        robot.indexer.setThree();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 2 && System.currentTimeMillis() - shootTimer > 250) {
                        robot.kicker.setKick();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 3 && System.currentTimeMillis() - shootTimer > 300) {
                        robot.kicker.setInit();
                        shootStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 0) {
                        robot.indexer.setTwo();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 1) {
                        robot.indexer.setOne();
                        shootStep = 2;
                        shootRepStep += 1;
                        shootTimer = System.currentTimeMillis();
                    }
                    if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 350 && shootRepStep == 2) {
                        robot.indexer.setTwo();
                        shootStep = 0;
                        shootRepStep = 0;
                        shootTimer = 0;
                    }
                }
                setPathState(12);
                break;
            case 12:
                robot.shooter.off();
                follower.followPath(shootToPPG);
                setPathState(131);
                break;
            case 131: // oops, forgot to follow PPGtoShoot before shooting so I split case 13
                follower.followPath(PPGtoShoot);
                setPathState(132);
                break;
            case 132:
                robot.shooter.shoot();
//                robot.hood.setClose(); TODO: calibrate hood position for (48, 96)
                shootStep += 1;
                shootTimer = System.currentTimeMillis();
                if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
                    robot.indexer.setOne();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 2 && System.currentTimeMillis() - shootTimer > 250) {
                    robot.kicker.setKick();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 3 && System.currentTimeMillis() - shootTimer > 300) {
                    robot.kicker.setInit();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 0) {
                    robot.indexer.setTwo();
                    shootStep = 2;
                    shootRepStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 1) {
                    robot.indexer.setThree();
                    shootStep = 2;
                    shootRepStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 350 && shootRepStep == 2) {
                    robot.indexer.setTwo();
                    shootStep = 0;
                    shootRepStep = 0;
                    shootTimer = 0;
                }
                setPathState(14);
                break;
            case 14:
                robot.shooter.off();
                follower.followPath(shootToPGP);
                setPathState(15);
                break;
            case 15:
                follower.followPath(PGPtoShoot);
                setPathState(16);
                break;
            case 16:
                robot.shooter.shoot();
//                robot.hood.setClose(); TODO: calibrate hood position for (48, 96)
                shootStep += 1;
                shootTimer = System.currentTimeMillis();
                if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
                    robot.indexer.setOne();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 2 && System.currentTimeMillis() - shootTimer > 250) {
                    robot.kicker.setKick();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 3 && System.currentTimeMillis() - shootTimer > 300) {
                    robot.kicker.setInit();
                    shootStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 0) {
                    robot.indexer.setTwo();
                    shootStep = 2;
                    shootRepStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 250 && shootRepStep == 1) {
                    robot.indexer.setThree();
                    shootStep = 2;
                    shootRepStep += 1;
                    shootTimer = System.currentTimeMillis();
                }
                if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 350 && shootRepStep == 2) {
                    robot.indexer.setTwo();
                    shootStep = 0;
                    shootRepStep = 0;
                    shootTimer = 0;
                }
                setPathState(17);
                break;
            case 17:
                robot.intake.off();
                follower.followPath(shootToEnd);
                telems.addLine("Auto completed");
                break;
        }
    }

    @Override
    public void loop() {
        temp.reset();
        follower.update();
        autoPath();
        telemetry.addLine("Position: " + follower.getPose());
        telemetry.addLine("heading: " + follower.getTotalHeading());

    }

    @Override
    public void init() {
        telems = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);
        robot = new RobotMecanum(this, true, false);
        pathTimer = new ElapsedTime();
        temp = new ElapsedTime();

        robot.turret.setUseSquID(true, 0, 1f);
        robot.indexer.setTwo();

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        limelight.pipelineSwitch(0);
        limelight.start();

        follower = Constants.createFollower(hardwareMap);
        initBody();
        buildPaths();
        follower.setStartingPose(startPose);

        actionHandler.fastShootSys(robot);
    }

    @Override
    public void start() {
        total = new ElapsedTime();
        setPathState(10);
        autoPath();
    }


}
