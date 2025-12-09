package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {
    public OcServo lift;

    public static final float INIT = 10f;
    public static final float TRANSFER = 201f;

    public Lift(HardwareMap hardwareMap) {
        lift = new OcServo(hardwareMap, "lift", INIT);
    }
    public void setPosition(float pos){
        lift.setPosition(pos);
    }

    public void setInit() { lift.setPosition(INIT); }

    public void setTransfer() { lift.setPosition(TRANSFER); }

}
