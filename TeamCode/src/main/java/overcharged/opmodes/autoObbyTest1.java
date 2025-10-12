package overcharged.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.components.RobotMecanum;

import com.pedropathing.follower.Follower;


@Autonomous(name = "obelisk move test", group = "dTest")
public class autoObbyTest1 extends OpMode {
    // Init
    private RobotMecanum robot;
    private DigitalChannel hlimitswitch;
    private DigitalChannel vlimitswitch;
    FtcDashboard dashboard = FtcDashboard.getInstance();
    MultipleTelemetry telems;
    private ElapsedTime pathTimer;

    private int pathState;

    private Follower follower;

    private Limelight3A limelight;

    private int obelisk = 0;

    ObbyID obbyID = ObbyID.NONE;

    private enum ObbyID {
        ONE,
        TWO,
        THREE,
        NONE
    }

    public void buildPoses() {

    }

    public void buildPaths() {

    }

    // TODO: HERE IS WHERE THE MAIN PATH IS
    // Main pathing
    public void autoPath() {
        switch (pathState) {
            case 10: //START
                break;
            case 100: //TODO: test
                telems.addLine("CASE 100 - IN TEST CASE!!");
                break;
        }
    }

    // path setter
    public void setPathState(int state){
        pathState = state;
        pathTimer.reset();
        autoPath();
    }

    //loop de loop
    @Override
    public void loop() {

    }

    // initialize robot
    @Override
    public void init() {
        telems = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);
        robot = new RobotMecanum(this, true, false);
        pathTimer = new ElapsedTime();

        limelight = hardwareMap.get(Limelight3A.class, "Ethernet Device");
        telemetry.setMsTransmissionInterval(11);
        limelight.pipelineSwitch(0);
        initPath();
        setPathState(10);
    }

    //loop de loop but initialized
    @Override
    public void init_loop() {
        telemetry.update();
        telemetry.addLine("in loop");
        initPath();
    }

    // path setter
    public void setInitState(int state){
        pathState = state;
        initPath();
    }

    public void initPath() {
        switch (pathState) {
            case 10: //START
                limelight.start();
                setInitState(11);
                break;
            case 11:
                LLResult result = limelight.getLatestResult();
                obelisk = result.getFiducialResults().get(0).getFiducialId();
                if(obelisk == 21) {
                    setInitState(111);
                } else if(obelisk == 22) {
                    setInitState(112);
                } else if(obelisk == 23) {
                    setInitState(113);
                } else {
                    telemetry.addLine("not yet");
                }
                break;
            case 111:
                obbyID = ObbyID.ONE;
                telemetry.addLine("1");
                break;
            case 112:
                obbyID = ObbyID.TWO;
                telemetry.addLine("2");
                break;
            case 113:
                obbyID = ObbyID.THREE;
                telemetry.addLine("3");
                break;
            case 100: //TODO: test
                telems.addLine("CASE 100 - IN TEST CASE!!");
                break;
        }
    }




    @Override
    public void start() {

    }

    public static void waitFor(int milliseconds) { //Waitor Function (WARNING IT IS BAD)
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < milliseconds) {
            // loop
        }
    }


}
