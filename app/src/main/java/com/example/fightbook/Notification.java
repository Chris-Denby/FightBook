package com.example.fightbook;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification implements Comparable
{
    String createdDate;
    String nodeID;
    String text;
    Bitmap avatarImage;

    String issuerID;
    String issuerNickname;

    String userBID = "";
    String userBNickname = "";
    String isNew = "true";

    String notificationKey = "";


    public Notification()
    {

    }

    public Notification(String text)
    {
        this.text = text;
    }

    public String getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    public String getNodeID() {
        return nodeID;
    }
    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getIssuerID() {
        return issuerID;
    }
    public void setIssuerID(String issuerID) {
        this.issuerID = issuerID;
    }
    public String getIssuerNickname() {
        return issuerNickname;
    }
    public void setIssuerNickname(String issuerNickname) {
        this.issuerNickname = issuerNickname;
    }
    public void setUserBID(String userBID) {
        this.userBID = userBID;
    }
    public void setUserBNickname(String userBNickname) {
        this.userBNickname = userBNickname;
    }
    public String getUserBID() {
        return userBID;
    }
    public String getUserBNickname()
    {
        return userBNickname;
    }
    public String getIsNew(){return isNew;}
    public void setIsNew (String isNew){this.isNew = isNew;}
    public void setNotificationKey(String key){this.notificationKey = key;}
    public String getNotificationKey(){return notificationKey;}

    @Override
    public int compareTo(Object o)
    {
        int ordering;

        if(o instanceof Notification)
        {

            //(LOCAL VALUE > OTHER VALUE) INCREASE VALUE
            //(LOCAL VALUE < OTHER VALUE) DECREASE VALUE
            //OTHERWISE VALUE IS 0

            Date thisEventDate = null;
            Date otherEventDate = null;
            Event otherEvent = (Event) o;

            //convert date stringsd to date objects so they can be compared
            try {
                thisEventDate = new SimpleDateFormat("dd-MM-YY H:mm").parse(((Notification) o).getCreatedDate());
                otherEventDate = new SimpleDateFormat("dd-MM-YY H:mm").parse(otherEvent.getDateCreated());
            } catch (Exception e) {

            }
            //int ordering = (thisEventDate.after(otherEventDate)) ? 1 : (thisEventDate.before(otherEventDate)) ? -1 : 0; //oldest to newest ascending
            ordering =  (thisEventDate.before(otherEventDate)) ? 1 :
                    (thisEventDate.after(otherEventDate)) ? -1 :
                            0; // newest to oldest descending
        }
        else
        {
            ordering = 0;
        }

        return ordering;
    }









}
