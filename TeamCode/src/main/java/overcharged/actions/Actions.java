package overcharged.actions;

import overcharged.components.RobotMecanum;

public class Actions {
    private RobotMecanum robot;



    //fast shooting vars
    private long shootTimer;
    private int shootStep;
    private int shootRepStep;

    private int kickPause = 450;
    private int retractPause = 250;
    private int setIndPause = 250;

    public void fastShootSys(RobotMecanum robot) {
        this.robot = robot;
        this.shootStep = 0;
        this.shootRepStep = 0;
        this.shootTimer = 0;
    }
    public void fastShootSeq() { //231
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setTwo();
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
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setOne();
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
    public void fastShootSeq132() { //132
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setOne();
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
            robot.indexer.setThree();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setTwo();
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
    public void fastShootSeq213() { //213
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setTwo();
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
            robot.indexer.setOne();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setThree();
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
    public void fastShootSeq123() { //123
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setOne();
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
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setThree();
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

    public void fastShootSeq312() { //312
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setThree();
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
            robot.indexer.setOne();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setTwo();
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
    public void fastShootSeq321() { //321
        if (shootStep == 1 && System.currentTimeMillis() - shootTimer > 10) {
            robot.indexer.setThree();
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
            robot.indexer.setTwo();
            shootStep = 2;
            shootRepStep += 1;
            shootTimer = System.currentTimeMillis();
        }
        if (shootStep == 4 && System.currentTimeMillis() - shootTimer > setIndPause && shootRepStep == 1) {
            robot.indexer.setOne();
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


    // @param int[3]
    public void fastShootSeqByOrder(int[] currentOrder) { //321
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
