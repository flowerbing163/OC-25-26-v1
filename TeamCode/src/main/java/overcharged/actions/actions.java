package overcharged.actions;

import overcharged.components.RobotMecanum;

public class actions {
    private RobotMecanum robot;



    //fast shooting vars
    private long shootTimer;
    private int shootStep;
    private int shootRepStep;

    public void fastShootSys(RobotMecanum robot) {
        this.robot = robot;
        this.shootStep = 0;
        this.shootRepStep = 0;
        this.shootTimer = 0;
    }
    public void fastShootSeq() {
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setOne();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > 250) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > 300) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > 250 && shootRepStep == 0) {
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > 250 && shootRepStep == 1) {
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > 350 && shootRepStep == 2) {
            robot.indexer.setTwo();
            shootStep = 0;
            shootRepStep = 0;
            shootTimer = 0;
        }
    }
    public void startFastShoot() {
        shootStep += 1;
        shootTimer = System.currentTimeMillis();
    }
    //
    //more actions


}
