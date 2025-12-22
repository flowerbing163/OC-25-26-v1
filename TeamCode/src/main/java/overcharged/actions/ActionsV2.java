package overcharged.actions;

import overcharged.components.RobotMecanum;
import overcharged.components.indexer;

public class ActionsV2 {
    private RobotMecanum robot;



    //fast shooting vars
    private long shootTimer;
    private int shootStep;
    private float endPos = indexer.FORWARD1;

    private int raiseTime = 400;

    public void fastShootSys(RobotMecanum robot) {
        this.robot = robot;
        this.shootStep = 0;
        this.shootTimer = 0;
    }

    public void fastShoot() {
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setInit();
            robot.intake.in();
            robot.indexerlift.setUp();
            shootStep++; // 1 -> 2
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 2 && System.currentTimeMillis() - shootTimer > raiseTime-20) {
            robot.intake.shooter();
            shootStep++; // 2 -> 3
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 3 && System.currentTimeMillis() - shootTimer > raiseTime+50) {
            robot.indexer.setPosition(endPos);
            shootStep++; // 3 -> 4
            shootTimer = System.currentTimeMillis();
        }

        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 1200) {
//            robot.indexer.reset();
//            robot.intake.in();
//            robot.indexerlift.setInit();
            shootStep = 0;
            shootTimer = 0;
        }
    }

    public void startFastShoot() {
        shootStep++; // 0 -> 1
        shootTimer = System.currentTimeMillis();
        this.endPos = indexer.FORWARD1;

    }
    public void startFastShoot(float finalPos) {
        shootStep++; // 0 -> 1
        shootTimer = System.currentTimeMillis();
        this.endPos = finalPos;
    }

    // Sort Shoot Sys
    private int shootRepStep;

    private int kickPause = 450;
    private int retractPause = 250;
    private int setIndPause = 250;

    public void sortShootSys(RobotMecanum robot) {
        this.robot = robot;
        this.shootStep = 0;
        this.shootRepStep = 0;
        this.shootTimer = 0;
    }

    // @param int[3]
    public void sortShootSeqOrder(int[] currentOrder) { //321
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            rollIndexer(currentOrder[0]);
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 2 && System.currentTimeMillis() - shootTimer > kickPause) {
            robot.kicker.setKick();
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 3 && System.currentTimeMillis() - shootTimer > retractPause) {
            robot.kicker.setInit();
            shootStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 0) {
            rollIndexer(currentOrder[1]);
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            rollIndexer(currentOrder[2]);
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > 280 && shootRepStep == 2) {
            robot.indexer.setTwo();
            shootStep = 0;
            shootRepStep = 0;
            shootTimer = 0;
        }
    }

    private void rollIndexer(int first) {
        if(1 == first){
            robot.indexer.setOne();
        } else if (2 == first) {
            robot.indexer.setTwo();
        } else if (3 == first) {
            robot.indexer.setThree();
        }else{
            robot.indexer.setOne();
        }
    }

    public void startSortShoot() {
        shootStep += 1;
        shootTimer = System.currentTimeMillis();
    }

    //
    /*
    private float goTo = robot.indexer.MAX;

    private boolean shootNow = false;

    public void specIndSys(RobotMecanum robot) {
        this.robot = robot;
    }

    public void fastShootAction(){
        if(!shootNow) {
            return;
        } else if (shootNow) {
            robot.indexer.setPosition(goTo);
            shootNow = false;
        }
    }

    public void fastShootStart() {
        this.goTo = robot.indexer.getDirection();
    }
     */
    //


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
                robot.indexer.setThree();
                indexerPos = indexerState.THREE;
                break;
            case ONE:
                robot.indexer.setTwo();
                indexerPos = indexerState.INIT;
                break;
            case TWO:
                robot.indexer.setThree();
                indexerPos = indexerState.THREE;
                break;
            case THREE:
                robot.indexer.setOne();
                indexerPos = indexerState.ONE;
                break;
        }
        indMove = false;
    }

    public void startIndMove() {
        indMove = true;
    }
    //



}