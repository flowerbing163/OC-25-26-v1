package overcharged.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import overcharged.components.Button;
import overcharged.components.MecanumDrive;
import overcharged.components.OcServo;
import overcharged.components.RobotMecanum;

/**
 * Overcharged Team #12599 Tester
 * This tester program has 16 separate tests.
 */
@Config
@TeleOp(name="Tester", group="Test")
public class
tester123
        extends LinearOpMode {

    ///Overcharged Autonomous Robot class
    private RobotMecanum robot;
    MecanumDrive drive;
    private ServoTestInfo[] servoTestInfos;
    /**
     * Counter of servos in servo test
     */
    private int servoTestCounter = 0;

    private final List<OcServo> servos = new ArrayList<>();

    /**
     * Counter for servos for servo calibrate test
     */
    private int servoCalibrateCounter = 0;

    private final static int MIN_SERVO_TICK = 1;
    private final static DecimalFormat integerFormatter = new DecimalFormat("######");
    private final static DecimalFormat decimalFormatter = new DecimalFormat("###.##");

    /**
     * Enumeration for the different tests
     */
    public enum ETest {
        NONE,
        //SWITCH,
        //ENCODER,
        MOTOR,
        DRIVE,
        SERVO_CALIBRATE,
        //DEPO_CALIBRATE,
        //GYRO,
        //LED,
        //HSLIDE,
        //VSLIDE,
        SENSOR,

        ;

        private static int numberTests = 0;

        /**
         * Get the test according to number
         * @param ordinal the test number
         * @return the test according to the number given
         */
        public static ETest getTest(int ordinal)
        {
            for (ETest e : values()) {
                if (e.ordinal() == ordinal) {
                    return e;
                }
            }

            return NONE;
        }

        /**
         * Get the number of tests
         * @return the number of tests
         */
        public static int getNumberTests() {
            if (numberTests == 0) {
                for (ETest ignored : values()) {
                    numberTests++;
                }
            }
            return numberTests;
        }
    }

    /**
     * Function for running tests
     */
    @Override
    public void runOpMode() {
        //Initialization
        robot = new RobotMecanum(this, true, false);
        drive = robot.getDrive();
        drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        OcServo hood = robot.hood.hood;
        OcServo indexerlift = robot.indexerlift.indexerlift;
        OcServo indexer = robot.indexer.indexer;
        //servos.add(intakeTilt);
        servos.add(indexer);
        servos.add(hood);
        servos.add(indexerlift);




        servoTestInfos = new ServoTestInfo[]{

                new ServoTestInfo(
                        indexer,
                        robot.indexer.ONE,
                        robot.indexer.THREE),
                new ServoTestInfo(
                        indexerlift,
                        robot.indexerlift.INIT),
                new ServoTestInfo(
                        hood,
                        robot.hood.INIT)
        };

        int testCounter = 0;
        ///Set current test to NONE
        ETest currentTest = ETest.NONE;

        telemetry.addData("Waiting", "Tester");
        telemetry.update();
        ///Waiting for start to be pressed
        waitForStart();

        while (opModeIsActive()) {
            long timeStamp = System.currentTimeMillis();

            ///Choosing the desired test
            if(gamepad1.right_trigger > 0.9 && Button.BTN_NEXT.canPress(timeStamp)) {
                testCounter++;
                if(testCounter >= ETest.getNumberTests()){
                    testCounter = 0;
                }
                currentTest = ETest.getTest(testCounter);
            } else if(gamepad1.left_trigger > 0.9 && Button.BTN_PREV.canPress(timeStamp)) {
                testCounter--;
                if(testCounter < 0){
                    testCounter = ETest.getNumberTests() - 1;
                }
                currentTest = ETest.getTest(testCounter);
            }

            telemetry.addData("Test", currentTest);
            telemetry.addData("Select", "Next:RightTrigger Prev:LeftTrigger");
            telemetry.addData("Run", "Start");

            ///Loop tests
            if (gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                switch(currentTest) {
                    case MOTOR:
                        motorTest();
                        break;
                    case DRIVE:
                        driveTest();
                        break;
                    case SERVO_CALIBRATE:
                        servoCalibrate(servos);
                        break;
                    case NONE:
                    default:
                        break;
                }
            }

            telemetry.update();
            idle();
        }
    }

    /**
     * Test the motors
     */
    private void motorTest () {
        float[] powers = new float[]{0.25f, -0.25f};

        MotorTestInfo[] motorTestInfos = new MotorTestInfo[]{
                new MotorTestInfo(robot.shooter.topShooter, "topShooter"),
                new MotorTestInfo(robot.shooter.botShooter, "botShooter"),
                new MotorTestInfo(robot.turret.turret, "turret"),
        };

        back:
        for (MotorTestInfo motorTestInfo : motorTestInfos) {
            for (float power : powers) {
                motorTestInfo.stop();

                long startTimestamp = System.currentTimeMillis();
                long timeStamp = startTimestamp;

                while (opModeIsActive() &&
                        timeStamp - startTimestamp < 5000) {
                    telemetry.addData("Test", "Motors");
                    telemetry.addData("Motor", motorTestInfo.motorName);
                    telemetry.addData("Power", power);
                    telemetry.addData("CurrentPosition", motorTestInfo.motor.getCurrentPosition());

                    motorTestInfo.motor.setPower(power);

                    telemetry.addData("Reset", "Start");
                    telemetry.addData("Back", "LeftStick");

                    if (gamepad1.start && Button.BTN_START.canPress(timeStamp)) {

                        robot.drive.resetPosition();
                        //robot.slides.resetSlidePosition();
                        idle();
                    }
                    else if (gamepad1.left_stick_button && Button.BTN_BACK.canPress(timeStamp)) {
                        break back;
                    }

                    telemetry.update();
                    idle();
                    timeStamp = System.currentTimeMillis();
                }
                motorTestInfo.stop();
            }
        }

        idle();
    }


    /**
     * Calibration of servos in robot
     * @param servoCalibrateList servos to be tested
     */
    private void servoCalibrate(List<OcServo> servoCalibrateList) {
        int posJoy1 = (int)(servoCalibrateList.get(servoCalibrateCounter).getPosition());

        while (opModeIsActive()) {
            long timeStamp = System.currentTimeMillis();

            ///Choose a servo for calibration
            if(gamepad1.right_trigger > 0.9 && Button.BTN_NEXT.canPress(timeStamp)) {
                servoCalibrateCounter++;
                if(servoCalibrateCounter >= servoCalibrateList.size()){
                    servoCalibrateCounter = 0;
                }
                posJoy1 = (int)(servoCalibrateList.get(servoCalibrateCounter).getPosition());
            } else if(gamepad1.left_trigger > 0.9 && Button.BTN_PREV.canPress(timeStamp)) {
                servoCalibrateCounter--;
                if(servoCalibrateCounter < 0){
                    servoCalibrateCounter = servoCalibrateList.size() - 1;
                }
                posJoy1 = (int)(servoCalibrateList.get(servoCalibrateCounter).getPosition());
            }
            else if (gamepad1.left_stick_button && Button.BTN_BACK.canPress(timeStamp)) {
                return;
            }

            ///Change servo position for calibration
            if (gamepad1.x && Button.BTN_MINUS.canPress4Short(timeStamp)) {
                posJoy1 -= MIN_SERVO_TICK;
            } else if (gamepad1.b && Button.BTN_PLUS.canPress4Short(timeStamp)) {
                posJoy1 += MIN_SERVO_TICK;
            } else if (gamepad1.y && Button.BTN_MAX.canPress(timeStamp)) {
                posJoy1 = 255;
            } else if (gamepad1.a && Button.BTN_MIN.canPress(timeStamp)) {
                posJoy1 = 0;
            } else if (gamepad1.right_stick_button && Button.BTN_MID.canPress(timeStamp)) {
                posJoy1 = 128;
            }
            posJoy1 = Range.clip(posJoy1, 0, 255);
            servoCalibrateList.get(servoCalibrateCounter).setPosition(posJoy1);

            telemetry.addData("Test", "ServoCalibrate");
            telemetry.addData("Adjust", "+:B -:X Max:Y Min:A Mid:RStick");
            telemetry.addData("Position", integerFormatter.format(posJoy1));
            telemetry.addData("Servo", servoCalibrateList.get(servoCalibrateCounter));
            telemetry.addData("Select", "Next: RightTrigger Prev: LeftTrigger");
            telemetry.addData("Back", "LeftStick");

            telemetry.update();
            idle();
        }
    }


    /**
     * Tests each individual servo
     * @param servoTestInfos the servos to be tested
     */
    private void servoTest(ServoTestInfo[] servoTestInfos) {
        while (opModeIsActive()) {
            long timeStamp = System.currentTimeMillis();

            if (gamepad1.right_trigger > 0.9 && Button.BTN_NEXT.canPress(timeStamp)) {
                servoTestCounter++;
                if (servoTestCounter >= servoTestInfos.length) {
                    servoTestCounter = 0;
                }
            }
            else if (gamepad1.left_trigger > 0.9 && Button.BTN_PREV.canPress(timeStamp)) {
                servoTestCounter--;
                if (servoTestCounter < 0) {
                    servoTestCounter = servoTestInfos.length - 1;
                }
            }
            else if (gamepad1.left_stick_button && Button.BTN_BACK.canPress(timeStamp)) {
                return;
            }
            else if (gamepad1.start && Button.BTN_START.canPress(timeStamp)) {
                servoTest(servoTestInfos[servoTestCounter]);
            }

            telemetry.addData("Test", "Servo");
            telemetry.addData("Select", "Next:RightTrigger Prev:LeftTrigger");
            telemetry.addData("Servo", servoTestInfos[servoTestCounter].servo);
            telemetry.addData("Run", "Start");
            telemetry.addData("Back", "LeftStick");

            telemetry.update();
            idle();
        }
    }

    /**
     * Tests each individual servo
     * @param servoTestInfo the servos to be tested
     */
    private void servoTest(ServoTestInfo servoTestInfo) {
        for (int i = 0; i <= servoTestInfo.positions.length; i++) {
            float currentPosition = servoTestInfo.servo.getPosition();
            float position;
            if (i < servoTestInfo.positions.length) {
                position = servoTestInfo.positions[i];
            }
            else {
                position = servoTestInfo.servo.getInitialPosition();
            }

            long startTimestamp = System.currentTimeMillis();
            long timeStamp = startTimestamp;
            long timeout = (long)(Math.abs(position - currentPosition) * 5 * servoTestInfo.timeScale);

            while (opModeIsActive() &&
                    timeStamp - startTimestamp < timeout) {
                servoTestInfo.servo.setPosition(position);

                if (gamepad1.left_stick_button && Button.BTN_BACK.canPress(timeStamp)) {
                    return;
                }

                telemetry.addData("Test", "Servo");
                telemetry.addData("Position", integerFormatter.format(servoTestInfo.servo.getPosition()));
                telemetry.addData("Servo", servoTestInfo.servo);
                telemetry.addData("Back", "LeftStick");

                telemetry.update();
                idle();
                timeStamp = System.currentTimeMillis();
            }
        }
    }

    /**
     * Test the drive train, motors, and servos
     */
    private void driveTest () {
        float[][] powers = new float[][]{
                {0.25f, 0.25f},
                {-0.25f, -0.25f},
                {0.25f, -0.25f},
                {-0.25f, 0.25f},
        };

        back:
        for (float[] power : powers) {
            robot.drive.resetPosition();

            long startTimestamp = System.currentTimeMillis();
            long timeStamp = startTimestamp;

            while (opModeIsActive() &&
                    timeStamp - startTimestamp < 5000) {

                robot.drive.setPower(power[0], power[1]);

                telemetry.addData("Test", "Drive");
                telemetry.addData("Front",
                        "Left:" + integerFormatter.format(robot.driveLeftFront.getCurrentPosition()) +
                                " Right:" + integerFormatter.format(robot.driveRightFront.getCurrentPosition()));
                telemetry.addData("Back",
                        "Left:" + integerFormatter.format(robot.driveLeftBack.getCurrentPosition()) +
                                " Right:" + integerFormatter.format(robot.driveRightBack.getCurrentPosition()));
                telemetry.addData("Back", "LeftStick");

                if (gamepad1.left_stick_button && Button.BTN_BACK.canPress(timeStamp)) {
                    break back;
                }

                telemetry.update();
                idle();
                timeStamp = System.currentTimeMillis();
            }

            robot.drive.stop();
        }

        robot.drive.stop();
        robot.drive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        idle();
    }
}