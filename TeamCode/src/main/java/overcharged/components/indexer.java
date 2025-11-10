package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class indexer {
    public OcServo indexer;

    public static final float ONE = 16f;//211f old
    public static final float TWO = 205f; //115f old
    public static final float THREE = 111f;//20f old

    public indexer(HardwareMap hardwareMap) {
        indexer = new OcServo(hardwareMap, "indexer", TWO);
    }
    public void setPosition(float pos){
        indexer.setPosition(pos);
    }

    public void setOne() { indexer.setPosition(ONE); }

    public void setTwo() { indexer.setPosition(TWO); }

    public void setThree() { indexer.setPosition(THREE); }
}
