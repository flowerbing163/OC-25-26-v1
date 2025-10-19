package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class indexer {
    public OcServo indexer;

    public static final float ONE = 211f;//17f;
    public static final float TWO = 115f;
    public static final float THREE = 20f;//206f;

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
