package overcharged.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {

    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(12.25)
            .forwardZeroPowerAcceleration(-32.675105304505486)
            .lateralZeroPowerAcceleration(-66.64383989507478)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryDrivePIDF(false)
            .centripetalScaling(0.00036)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.25, 0, 0.1, 0.07))
            .headingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.09, 0.07))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.015, 0.000005, 0.0007, 0.6, 0))
            .secondaryTranslationalPIDFCoefficients(
                    new PIDFCoefficients(0.11, 0, 0.02, 0.025)
            )
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients( 0.8, 0.00001, 0.01, 0.01))
            .secondaryDrivePIDFCoefficients(
                    new FilteredPIDFCoefficients(0.008, 0.00001, 0.0001, 0.6, 0)
            );

    public static MecanumConstants driveConstants = new MecanumConstants()
            .leftFrontMotorName("driveLF")
            .leftRearMotorName("driveLB")
            .rightFrontMotorName("driveRF")
            .rightRearMotorName("driveRB")
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(74.7805925504429)
            .yVelocity(59.67721437469241);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(134.5/25.4)
            .strafePodX(-69.995/25.4)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .encoderResolution(
                    GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
            )
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static PathConstraints pathConstraints = new PathConstraints(
            0.995,
            500,
            0.95,
            1
    );

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}