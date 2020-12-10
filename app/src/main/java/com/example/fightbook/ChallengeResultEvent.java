package com.example.fightbook;

public class ChallengeResultEvent extends Event
{
    int userAPoints;
    int userBPoints;
    String userAFightSystem;
    String userBFightSystem;


    public ChallengeResultEvent()
    {
        super();

    }

    public int getWinnerPoints() {
        return userAPoints;
    }
    public void setWinnerPoints(int winnerPoints) {
        this.userAPoints = winnerPoints;
    }
    public int getLoserPoints() {
        return userBPoints;
    }
    public void setLoserPoints(int loserPoints) {
        this.userBPoints = loserPoints;
    }
    public String getWinnerFightSystem() {
        return userAFightSystem;
    }
    public void setWinnerFightSystem(String winnerFightSystem) {
        this.userAFightSystem = winnerFightSystem;
    }
    public String getLoserFightSystem() {
        return userBFightSystem;
    }
    public void setLoserFightSystem(String loserFightSystem) {
        this.userBFightSystem = loserFightSystem;
    }
}
