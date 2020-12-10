package com.example.fightbook;

public class Like
{
    String userID;
    String eventID;
    String type;
    String likeDate;
    String userNickName;

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getEventID() {
        return eventID;
    }
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLikeDate() {
        return likeDate;
    }
    public void setLikeDate(String likeDate) {
        this.likeDate = likeDate;
    }
    public String getUserNickName() {
        return userNickName;
    }
    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean isSame = false;
        //cast object to Like and check it is not null and is a Like object
        if((Like) o != null && o instanceof Like)
        {
            isSame = this.userID.equals(((Like) o).userID);
        }
        return isSame;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
