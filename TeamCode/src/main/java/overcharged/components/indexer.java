package overcharged.components;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class indexer {
    public OcServo indexer;

    public static final float ONE = 10f;//211f old
    public static final float TWO = 201f; //115f old
    public static final float THREE = 105f;//20f old

    public static final float RONE = 10f;//211f old
    public static final float RTWO = 201f; //115f old
    public static final float RTHREE = 105f;//20f old

    public static final float CRONE = 10f;//211f old
    public static final float CRTWO = 201f; //115f old
    public static final float CRTHREE = 105f;//20f old

    public static final float MIN = 0f; //115f old
    public static final float MAX = 255f;//20f old



    public indexer(HardwareMap hardwareMap) {
        indexer = new OcServo(hardwareMap, "indexer", TWO);
    }
    public void setPosition(float pos){
        indexer.setPosition(pos);
    }

    public float getDirection() {
        if (MAX-indexer.getPosition() > MIN-indexer.getPosition()) {
            return MAX;
        } else {
            return MIN;
        }
    }

    public void setOne() { indexer.setPosition(ONE); }

    public void setTwo() { indexer.setPosition(TWO); }

    public void setThree() { indexer.setPosition(THREE); }
}
