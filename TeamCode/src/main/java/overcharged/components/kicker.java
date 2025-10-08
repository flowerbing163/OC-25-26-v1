package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class kicker {
    public OcServo kicker;

    public static final float INIT = 109f;
    public static final float KICK = 0f;

    public kicker(HardwareMap hardwareMap) {
        kicker = new OcServo(hardwareMap, "kicker", INIT);
    }
    public void setPosition(float pos){
        kicker.setPosition(pos);
    }

    public void setInit() { kicker.setPosition(INIT); }

    public void setKick() { kicker.setPosition(KICK); }
}
