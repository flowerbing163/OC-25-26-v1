package overcharged.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class IndexerLift {
    public OcServo indexerlift;

    public static final float INIT = 25f;
    public static final float MID = 170f;
    public static final float UP = 231f;

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