package com.example.fightbook;

import java.util.Date;

public class Fight
{
    Date fightDate;
    User redFighter;
    User blueFighter;
    String winnerNickname;
    String winnerID;
    String loserID;
    String loserNickname;
    String redFightSystem;
    String blueFightSystem;
    String winnerFightSystem;
    String loserFightSystem;
    String draw;
    String dateCreated;
    String nodeID;

    //fight results
    User winner;
    User loser;
    int winnerTotalScore = 0;
    int loserTotalScore = 0;
    int redTotalScore = 0;
    int blueTotalScore = 0;

    int winnerHeadCutsScored = 0;
    int winnerHeadThrustsScored = 0;
    int winnerTorsoCutsScored = 0;
    int winnerTorsoThrustsScored = 0;
    int winnerLimbCutsScored = 0;
    int winnerLimbThrustsScored = 0;

    int loserHeadCutsScored = 0;
    int loserHeadThrustsScored = 0;
    int loserTorsoCutsScored = 0;
    int loserTorsoThrustsScored = 0;
    int loserLimbCutsScored = 0;
    int loserLimbThrustsScored = 0;


    //hit values
    int redHeadCutValue;
    int redHeadThrustValue;
    int redTorsoCutValue;
    int redTorsoThrustValue;
    int redLimbCutValue;
    int redLimbThrustValue;

    int blueHeadCutValue;
    int blueHeadThrustValue;
    int blueTorsoCutValue;
    int blueTorsoThrustValue;
    int blueLimbCutValue;
    int blueLimbThrustValue;

    //red fighter points
    //total hits against
    private int redTotalCutsDelivered = 0;
    private int redTotalThrustsDelivered = 0;

    int redHeadCutsDelivered = 0;
    int redHeadThrustsDelivered = 0;
    int redTorsoCutsDelivered = 0;
    int redTorsoThrustsDelivered = 0;
    int redLimbCutsDelivered = 0;
    int redLimbThrustsDelivered = 0;

    //hits scored
    int redHeadCutScore = 0;
    int redHeadThrustScore = 0;
    int redTorsoCutScore = 0;
    int redTorsoThrustScore = 0;
    int redLimbCutScore = 0;
    int redLimbThrustScore = 0;

    //blue fighter points
    //total hits against
    int blueHeadCutsDelivered = 0;
    int blueHeadThrustsDelivered = 0;
    int blueTorsoCutsDelivered = 0;
    int blueTorsoThrustsDelivered = 0;
    int blueLimbCutsDelivered = 0;
    int blueLimbThrustsDelivered = 0;
    //hits scored
    int blueHeadCutScore = 0;
    int blueHeadThrustScore = 0;
    int blueTorsoCutScore = 0;
    int blueTorsoThrustScore = 0;
    int blueLimbCutScore = 0;
    int blueLimbThrustScore = 0;

   //default constructor
    public Fight(){
    }

    //parameterized constructor
    public Fight(User redFighter, User blueFighter)
    {
        this.redFighter = redFighter;
        this.blueFighter = blueFighter;
    }

    //parameterized constructor
    public Fight(User redFighter, User blueFighter, String redFightSystem, String blueFightSystem)
    {
        this.redFighter = redFighter;
        this.blueFighter = blueFighter;
        setRedFightSystem(redFightSystem);
        setBlueFightSystem(blueFightSystem);
    }


    //ASSIGN POINTS METHODS
    public void addRedHeadCut(){
        blueHeadCutScore = blueHeadCutScore + blueHeadCutValue;
        blueHeadCutsDelivered = blueHeadCutsDelivered +1;
        blueTotalScore = blueTotalScore + blueHeadCutValue;
    }
    public void addRedHeadThrust(){
        blueHeadThrustScore = blueHeadThrustScore + blueHeadThrustValue;
        blueHeadThrustsDelivered = blueHeadThrustsDelivered +1;
        setBlueTotalScore();}
    public void addRedTorsoCut(){
        blueTorsoCutScore = blueTorsoCutScore + blueTorsoCutValue;
        blueTorsoCutsDelivered = blueTorsoCutsDelivered + 1;
        setBlueTotalScore();}
    public void addRedTorsoThrust(){
        blueTorsoThrustScore = blueTorsoThrustScore + blueTorsoThrustValue;
        blueTorsoThrustsDelivered = blueTorsoThrustsDelivered + 1;
        setBlueTotalScore();}
    public void addRedLimbCut(){
        blueLimbCutScore = blueLimbCutScore + blueLimbCutValue;
        blueLimbCutsDelivered = blueLimbCutsDelivered + 1;
        setBlueTotalScore();}
    public void addRedLimbThrust(){
        blueLimbThrustScore = blueLimbThrustScore + blueLimbThrustValue;
        blueLimbThrustsDelivered = blueLimbThrustsDelivered + 1;
        setBlueTotalScore();}
    public void addBlueHeadCut(){
        redHeadCutScore = redHeadCutScore + redHeadCutValue;
        redHeadCutsDelivered = redHeadCutsDelivered + 1;
        setRedTotalScore();}
    public void addBlueHeadThrust(){
        redHeadThrustScore = redHeadThrustScore + redHeadThrustValue;
        redHeadThrustsDelivered = redHeadThrustsDelivered + 1;
        setRedTotalScore();}
    public void addBlueTorsoCut(){
        redTorsoCutScore = redTorsoCutScore + redTorsoCutValue;
        redTorsoCutsDelivered = redTorsoCutsDelivered + 1;
        setRedTotalScore();}
    public void addBlueTorsoThrust(){
        redTorsoThrustScore = redTorsoThrustScore + redTorsoThrustValue;
        redTorsoThrustsDelivered = redTorsoThrustsDelivered + 1;
        setRedTotalScore();}
    public void addBlueLimbCut(){
        redLimbCutScore = redLimbCutScore + redLimbCutValue;
        redLimbCutsDelivered = redLimbCutsDelivered + 1;
        setRedTotalScore();}
    public void addBlueLimbThrust(){
        redLimbThrustScore = redLimbThrustScore + redLimbThrustValue;
        redLimbThrustsDelivered = redLimbThrustsDelivered + 1;
        setRedTotalScore();}

    public int getWinnerTotalScore(){return winnerTotalScore;}
    public int getLoserTotalScore(){return loserTotalScore;}
    public void setRedTotalScore() {
        redTotalScore = redHeadCutScore + redHeadThrustScore + redTorsoCutScore + redTorsoThrustScore + redLimbCutScore + redLimbThrustScore;
    }
    public void setBlueTotalScore() {
        blueTotalScore = blueHeadCutScore + blueHeadThrustScore + blueTorsoCutScore + blueTorsoThrustScore + blueLimbCutScore + blueLimbThrustScore;
    }

    public void setRedFightSystem(String system)
    {
        if(system.equals("Longsword"))
        {
            redHeadCutValue = 3;
            redHeadThrustValue = 3;
            redTorsoCutValue = 2;
            redTorsoThrustValue = 2;
            redLimbCutValue = 1;
            redLimbThrustValue = 1;
        }
        else
        if(system.equals("Rapier"))
        {
            redHeadCutValue = 3;
            redHeadThrustValue = 3;
            redTorsoCutValue = 0;
            redTorsoThrustValue = 2;
            redLimbCutValue = 0;
            redLimbThrustValue = 1;
        }
        else
        if(system.equals("Sabre"))
        {
            redHeadCutValue = 3;
            redHeadThrustValue = 3;
            redTorsoCutValue = 2;
            redTorsoThrustValue = 2;
            redLimbCutValue = 1;
            redLimbThrustValue = 1;
        }
        else
        if(system.equals("Dussack"))
        {
            redHeadCutValue = 3;
            redHeadThrustValue = 3;
            redTorsoCutValue = 2;
            redTorsoThrustValue = 2;
            redLimbCutValue = 1;
            redLimbThrustValue = 1;
        }
        else
            if(system.equals("Select Weapon"))
        {
            redHeadCutValue = 0;
            redHeadThrustValue = 0;
            redTorsoCutValue = 0;
            redTorsoThrustValue = 0;
            redLimbCutValue = 0;
            redLimbThrustValue = 0;

        }
        redFightSystem = system;
    }
    public void setBlueFightSystem(String system)
    {

        if(system.equals("Longsword"))
        {
            blueHeadCutValue = 3;
            blueHeadThrustValue = 3;
            blueTorsoCutValue = 2;
            blueTorsoThrustValue = 2;
            blueLimbCutValue = 1;
            blueLimbThrustValue = 1;
        }
        else
        if(system.equals("Rapier"))
        {
            blueHeadCutValue = 3;
            blueHeadThrustValue = 3;
            blueTorsoCutValue = 0;
            blueTorsoThrustValue = 2;
            blueLimbCutValue = 0;
            blueLimbThrustValue = 1;
        }
        else
        if(system.equals("Sabre"))
        {
            blueHeadCutValue = 3;
            blueHeadThrustValue = 3;
            blueTorsoCutValue = 2;
            blueTorsoThrustValue = 2;
            blueLimbCutValue = 1;
            blueLimbThrustValue = 1;
        }
        else
        if(system.equals("Dussack"))
        {
            blueHeadCutValue = 3;
            blueHeadThrustValue = 3;
            blueTorsoCutValue = 2;
            blueTorsoThrustValue = 2;
            blueLimbCutValue = 1;
            blueLimbThrustValue = 1;
        }
        else
            if(system.equals("Select Weapon"))
        {
            blueHeadCutValue = 0;
            blueHeadThrustValue = 0;
            blueTorsoCutValue = 0;
            blueTorsoThrustValue = 0;
            blueLimbCutValue = 0;
            blueLimbThrustValue = 0;
        }
        blueFightSystem = system;
    }

    public void setWinnerTotalScore(int score)
    {
        winnerTotalScore = score;
    }
    public void setLoserTotalScore(int score){loserTotalScore = score;}
    public String getDraw() {
        return draw;
    }
    public void setDraw(String draw) {
        this.draw = draw;
    }
    public String getWinnerFightSystem() {
        return winnerFightSystem;
    }
    public void setWinnerFightSystem(String winnerFightSystem) {
        this.winnerFightSystem = winnerFightSystem;
    }
    public String getLoserFightSystem() {
        return loserFightSystem;
    }
    public void setLoserFightSystem(String loserFightSystem) {
        this.loserFightSystem = loserFightSystem;
    }
    public void setDateCreated(String date) {
        dateCreated = date;
    }
    public String getDateCreated() {
        return dateCreated;
    }
    public void setNodeID(String ID) {
        nodeID = ID;
    }
    public String getNodeID() {
        return nodeID;
    }
    public String getWinnerNickname() {
        return winnerNickname;
    }
    public void setWinnerNickname(String winnerNickname) {
        this.winnerNickname = winnerNickname;
    }
    public String getLoserNickname() {
        return loserNickname;
    }
    public void setLoserNickname(String loserNickname) {
        this.loserNickname = loserNickname;
    }
    public String getWinnerID() {
        return winnerID;
    }
    public void setWinnerID(String winnerID) {
        this.winnerID = winnerID;
    }
    public String getLoserID() {
        return loserID;
    }
    public void setLoserID(String loserID) {
        this.loserID = loserID;
    }

    public int getWinnerHeadCutsScored() {
        return winnerHeadCutsScored;
    }

    public void setWinnerHeadCutsScored(int winnerHeadCutsScored) {
        this.winnerHeadCutsScored = winnerHeadCutsScored;
    }

    public int getWinnerHeadThrustsScored() {
        return winnerHeadThrustsScored;
    }

    public void setWinnerHeadThrustsScored(int winnerHeadThrustsScored) {
        this.winnerHeadThrustsScored = winnerHeadThrustsScored;
    }

    public int getWinnerTorsoCutsScored() {
        return winnerTorsoCutsScored;
    }

    public void setWinnerTorsoCutsScored(int winnerTorsoCutsScored) {
        this.winnerTorsoCutsScored = winnerTorsoCutsScored;
    }

    public int getWinnerTorsoThrustsScored() {
        return winnerTorsoThrustsScored;
    }

    public void setWinnerTorsoThrustsScored(int winnerTorsoThrustsScored) {
        this.winnerTorsoThrustsScored = winnerTorsoThrustsScored;
    }

    public int getWinnerLimbCutsScored() {
        return winnerLimbCutsScored;
    }

    public void setWinnerLimbCutsScored(int winnerLimbCutsScored) {
        this.winnerLimbCutsScored = winnerLimbCutsScored;
    }

    public int getWinnerLimbThrustsScored() {
        return winnerLimbThrustsScored;
    }

    public void setWinnerLimbThrustsScored(int winnerLimbThrustsScored) {
        this.winnerLimbThrustsScored = winnerLimbThrustsScored;
    }

    public int getLoserHeadCutsScored() {
        return loserHeadCutsScored;
    }

    public void setLoserHeadCutsScored(int loserHeadCutsScored) {
        this.loserHeadCutsScored = loserHeadCutsScored;
    }

    public int getLoserHeadThrustsScored() {
        return loserHeadThrustsScored;
    }

    public void setLoserHeadThrustsScored(int loserHeadThrustsScored) {
        this.loserHeadThrustsScored = loserHeadThrustsScored;
    }

    public int getLoserTorsoCutsScored() {
        return loserTorsoCutsScored;
    }

    public void setLoserTorsoCutsScored(int loserTorsoCutsScored) {
        this.loserTorsoCutsScored = loserTorsoCutsScored;
    }

    public int getLoserTorsoThrustsScored() {
        return loserTorsoThrustsScored;
    }

    public void setLoserTorsoThrustsScored(int loserTorsoThrustsScored) {
        this.loserTorsoThrustsScored = loserTorsoThrustsScored;
    }

    public int getLoserLimbCutsScored() {
        return loserLimbCutsScored;
    }

    public void setLoserLimbCutsScored(int loserLimbCutsScored) {
        this.loserLimbCutsScored = loserLimbCutsScored;
    }

    public int getLoserLimbThrustsScored() {
        return loserLimbThrustsScored;
    }

    public void setLoserLimbThrustsScored(int loserLimbThrustsScored) {
        this.loserLimbThrustsScored = loserLimbThrustsScored;
    }
}
