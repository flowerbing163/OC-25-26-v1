package overcharged.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class IndexerLift {
    public OcServo indexerlift;

    public static final float INIT = 51f;
    public static final float MID = 189f;
    public static final float UP = 255f;

    public IndexerLift(HardwareMap hardwareMap) {

        indexerlift = new OcServo(hardwareMap, "indexerlift", INIT);
    }

    public void setPosition(float pos) {
        indexerlift.setPosition(pos);
    }

    public void setDown() {
        setPosition(INIT);
    }
    public void setInit() {
        setPosition(INIT);
    }
    public void setMid() {setPosition(MID);}

    public void setUp() {
        setPosition(UP);
    }

}