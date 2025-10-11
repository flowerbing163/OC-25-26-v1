package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class hood {
    public OcServo hood;

    public static final float INIT = 92f;

    public hood(HardwareMap hardwareMap) {
        hood = new OcServo(hardwareMap, "hood", INIT);
    }
    public void setPosition(float pos){
        hood.setPosition(pos);
    }

    public float getCurrentPos() {return hood.getPosition(); }

    public void setInit() { hood.setPosition(INIT); }
}
