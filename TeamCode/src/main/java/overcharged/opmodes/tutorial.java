package overcharged.opmodes;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// extends OpMode is a key part of programming


@TeleOp(name = "tutorial")
public class tutorial extends OpMode {


    @Override
    public void init() {

        telemetry.addData("init","completed");
        telemetry.update();

    }

    @Override
    public void loop() {

        telemetry.addData("loop","is running");
        telemetry.update();

    }
}
