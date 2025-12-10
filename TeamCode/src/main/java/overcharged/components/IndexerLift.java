package overcharged.components;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class IndexerLift {
    public OcServo indexerliftR, indexerliftL;

    public static final float INIT = 0f;
    public static final float UP = 0f;

    public IndexerLift(HardwareMap hardwareMap) {
        indexerliftR = new OcServo(hardwareMap, "indexerliftR", INIT);
        indexerliftL = new OcServo(hardwareMap, "indexerliftL", INIT);
    }

    public void setPosition(float pos) {
        indexerliftR.setPosition(pos);
        indexerliftL.setPosition(pos);
    }

    public void setDown() {
        setPosition(INIT);
    }
    public void setInit() {
        setPosition(INIT);
    }

    public void setUp() {
        setPosition(UP);
    }

}
