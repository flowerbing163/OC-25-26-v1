package overcharged.actions;

import com.qualcomm.robotcore.robot.Robot;

import overcharged.components.RobotMecanum;
import overcharged.testModes.thinkTele1;

public class actions {
    private RobotMecanum robot;



    //fast shooting vars
    private long shootTimer;
    private int shootStep;
    private int shootRepStep;

    private int kickPause = 340;
    private int retractPause = 310;
    private int setIndPause = 320;

    public void fastShootSys(RobotMecanum robot) {
        this.robot = robot;
        this.shootStep = 0;
        this.shootRepStep = 0;
        this.shootTimer = 0;
    }
    public void fastShootSeq() { //123
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setOne();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
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
    public void fastShootSeq132() { //132
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setOne();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setTwo();
            shootStep = 0;
            shootRepStep = 0;
            shootTimer = 0;
        }
    }
    public void fastShootSeq213() { //213
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setTwo();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setOne();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
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
    public void fastShootSeq231() { //231
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setTwo();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setOne();
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

    public void fastShootSeq312() { //312
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setThree();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setOne();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setTwo();
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
    public void fastShootSeq321() { //321
        long timestamp = System.currentTimeMillis();

        if (shootStep == 1 && timestamp - shootTimer > 10) {
            robot.indexer.setThree();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 2 && timestamp - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 3 && timestamp - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 0) {
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = timestamp;
        }
        if (shootStep == 4 && timestamp - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setOne();
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

    //indexer(turntable) move
    private enum indexerState {
        INIT,
        ONE,
        TWO,
        THREE,
    }
    indexerState indexerPos;
    private boolean indMove;

    public void indexerSys(RobotMecanum robot) {
        this.robot = robot;
        this.indexerPos = indexerState.INIT;
        this.indMove = false;
    }

    public void indMoveSeq() {
        if (!indMove) {
            return;
        }
        switch (indexerPos) {
            case INIT:
                robot.indexer.setOne();
                indexerPos = indexerState.ONE;
                break;
            case ONE:
                robot.indexer.setTwo();
                indexerPos = indexerState.TWO;
                break;
            case TWO:
                robot.indexer.setThree();
                indexerPos = indexerState.THREE;
                break;
            case THREE:
                robot.indexer.setTwo();
                indexerPos = indexerState.INIT;
                break;
        }
        indMove = false;
    }

    public void startIndMove() {
        indMove = true;
    }
    //

}
