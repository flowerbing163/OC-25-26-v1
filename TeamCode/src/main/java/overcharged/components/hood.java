package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class hood {
    public OcServo hood;

    public static final float INIT = 189f; //50
    public static final float MAX = 0f; //25
    public static final float CLOSE = 148.4667f;
    public static final float MIDDLE = 155.4083f;
    public static final float DEFENSE = 160.65f;
    public hood(HardwareMap hardwareMap) {
        hood = new OcServo(hardwareMap, "hood", INIT);
    }
    public void setPosition(float pos){
        hood.setPosition(pos);
    }

    public float getCurrentPos() {return hood.getPosition(); }

    private boolean autoAdjust = false;
    private double targetDist = 1000; // in mm
    private double targetHeight = 0.70485; // in meters
    private shooter shooterRef = null;

    public float getCurrentAngle() {
        return (float) (Math.toRadians(getCurrentPos()/6.3+30));
        // when hood is init: 60 = 189f //
        // when hood is max: 30 = 0f //


    }
    public float angToPos(double angleRad) {
        double angleDeg = Math.toDegrees(angleRad);
        // Reverse the formula from getCurrentAngle()
        // angleDeg = pos/9 + 25
        // pos = (angleDeg - 25) * 9
        float pos = (float)((angleDeg - 30) * 6.3);

        return Math.max(MAX, Math.min(INIT, pos));
    }
    private double calcReqAng(double velocity, double distance) {
        double distMeters = distance / 1000.0; // Convert mm to m
        double h = targetHeight;

        double minAngle = Math.toRadians(25);
        double maxAngle = Math.toRadians(50);
        double target = 9.81 * distMeters * distMeters;

        for (int i = 0; i < 50; i++) {
            double midAngle = (minAngle + maxAngle) / 2.0;

            double cosTheta = Math.cos(midAngle);
            double tanTheta = Math.tan(midAngle);
            double value = velocity * velocity * 2.0 * cosTheta * cosTheta * (distMeters * tanTheta - h);

            if (Math.abs(value - target) < 0.01) {
                return midAngle;
            }

            if (value < target) {
                minAngle = midAngle;
            } else {
                maxAngle = midAngle;
            }
        }

        return (minAngle + maxAngle) / 2.0;
    }

    public void setAutoAdjust(boolean enable, shooter shooter, double targetDistance) {
        this.autoAdjust = enable;
        this.shooterRef = shooter;
        this.targetDist = targetDistance;
    }

    public void update() {
        if (autoAdjust && shooterRef != null) {
            double motorPower = shooterRef.getPowerT();
            double velocity = motorPower * shooterRef.powerCoeff;

            if (velocity < 0.5) {
                return;
            }

            double reqAng = calcReqAng(velocity, targetDist);
            float position = angToPos(reqAng) + 5;
            hood.setPosition(position);
        }
    }

    public void setTargetDistance(double distance) {
        this.targetDist = distance;
    }


    public void setInit() { hood.setPosition(INIT); }

    public void setClose() {hood.setPosition(CLOSE); }

    public void setMiddle() {hood.setPosition(MIDDLE); }
}
