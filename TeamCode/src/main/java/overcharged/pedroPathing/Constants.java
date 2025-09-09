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
            .mass(5)
            .forwardZeroPowerAcceleration(-39.7394)
            .lateralZeroPowerAcceleration(-78.5414)
            .useSecondaryTranslationalPIDF(true)
            .useSecondaryHeadingPIDF(true)
            .useSecondaryDrivePIDF(true)
            .centripetalScaling(0.00036)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.15, 0, 0, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.95, 0, 0, 0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(1, 0.000001, 0, 0.6, 0))
            .secondaryTranslationalPIDFCoefficients(
                    new PIDFCoefficients(0.19, 0, 0.02, 0)
            )
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(2.5, 0, 0.05, 0))
            .secondaryDrivePIDFCoefficients(
                    new FilteredPIDFCoefficients(0.02, 0.00001, 0.0001, 0.6, 0)
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
            .xVelocity(65.5534)
            .yVelocity(48.6396);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(2.8346456693)
            .strafePodX(3.4377953)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint")
            .yawScalar(1.0)
            .encoderResolution(
                    GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
            )
            .customEncoderResolution(13.26291192)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
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

