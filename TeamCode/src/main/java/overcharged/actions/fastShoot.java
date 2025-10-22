package overcharged.actions;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.robotcore.util.ElapsedTime;

import overcharged.components.Button;
import overcharged.components.RobotMecanum;
import overcharged.components.hood;
import overcharged.components.turrets;

public class fastShoot {
    static RobotMecanum robot;

    static int shootStep = 0;
    static int shootRepStep = 0;
    static float tempShootPower = 0;

    static long shootTimer = 0;

    public static void main() {
        if(shootStep == 0) {
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 1 && System.currentTimeMillis()-shootTimer > 10){
            robot.indexer.setOne();
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 2 && System.currentTimeMillis()-shootTimer > 250){
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 3 && System.currentTimeMillis()-shootTimer > 300){
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 4 && System.currentTimeMillis()-shootTimer > 250 && shootRepStep == 0){
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 4 && System.currentTimeMillis()-shootTimer > 250 && shootRepStep == 1){
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if(shootStep == 4 && System.currentTimeMillis()-shootTimer > 250 && shootRepStep == 2){
            robot.indexer.setTwo();
            shootStep = 0;
            shootRepStep = 0;
            shootTimer = 0;
        }
    }
}
