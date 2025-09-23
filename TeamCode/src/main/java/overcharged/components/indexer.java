package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class indexer {
    public OcServo indexer;

    public static final float INIT = 0f;

    public indexer(HardwareMap hardwareMap) {
        indexer = new OcServo(hardwareMap, "indexer", INIT);
    }
    public void setPosition(float pos){
        indexer.setPosition(pos);
    }

    public void setInit() { indexer.setPosition(INIT); }
}
