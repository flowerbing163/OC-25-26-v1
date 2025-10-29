package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class hood {
    public OcServo hood;

    public static final float INIT = 225f; //50
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

    public float getCurrentAngle() {
        return (float) (Math.toRadians(getCurrentPos()/9+25));
        // when hood is init: 40 = 225f // 50
        // when hood is max: 65 = 1f // 25
    }

    public void setInit() { hood.setPosition(INIT); }

    public void setClose() {hood.setPosition(CLOSE); }

    public void setMiddle() {hood.setPosition(MIDDLE); }
}
