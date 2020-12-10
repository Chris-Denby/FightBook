package com.example.fightbook;

public class SeasonResult implements Comparable
{
    String nickname;
    String userID;
    int seasonLevel;
    int bestLevel;
    double xp;
    String lastSeasonUpdate;

    public SeasonResult(String nickname,String userID, int seasonLevel,int bestLevel,double xp,String lastSeasonUpdate)
    {
        this.nickname = nickname;
        this.userID = userID;
        this.seasonLevel = seasonLevel;
        this.bestLevel = bestLevel;
        this.xp = xp;
        this.lastSeasonUpdate = lastSeasonUpdate;
    }

    public SeasonResult()
    {
        //default constructor
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public int getSeasonLevel() {
        return seasonLevel;
    }
    public void setSeasonLevel(int seasonLevel) {
        this.seasonLevel = seasonLevel;
    }
    public int getBestLevel() {
        return bestLevel;
    }
    public void setBestLevel(int bestLevel) {
        this.bestLevel = bestLevel;
    }
    public double getXp() {
        return xp;
    }
    public void setXp(double xp) {
        this.xp = xp;
    }
    public String getLastSeasonUpdate() {
        return lastSeasonUpdate;
    }
    public void setLastSeasonUpdate(String lastSeasonUpdate) {
        this.lastSeasonUpdate = lastSeasonUpdate;
    }

    @Override
    public int compareTo(Object o)
    {
        SeasonResult otherResult = (SeasonResult) o;
        int ordering = (seasonLevel > otherResult.getSeasonLevel()) ? 1 : (seasonLevel < otherResult.getSeasonLevel()) ? -1 : 0;
        return ordering;
    }






}
