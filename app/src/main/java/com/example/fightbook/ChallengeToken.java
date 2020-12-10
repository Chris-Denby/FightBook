package com.example.fightbook;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChallengeToken implements Comparable
{
    //String dateCreated;
    String issuerID;
    String issuerNickname;
    String receiverID;
    String receiverNickname;
    String tokenStatus;
    String currentMessage;
    String previousMessage;
    String chatID;
    String dateCreated;
    int chatCount = 0;
    ArrayList<Chat> chatArray = new ArrayList<>();
    Bitmap avatarImage;

    public void setIssuerID(String userID){issuerID = userID;}
    public void setIssuerNickname(String name){issuerNickname = name;}
    public void setReceiverID(String userID){receiverID = userID;}
    public void setReceiverNickname(String name){receiverNickname = name;}
    public void setTokenStatus(String status){tokenStatus = status;}
    public void setCurrentMessage(String msg)
    {
        currentMessage = msg;
    }
    public void setPreviousMessage(String msg){previousMessage = msg;}
    public void setChatID(String id){chatID = id;}
    public void setChatCount(int count){chatCount = count;}
    public void setDateCreated(String date){dateCreated = date;}

    public String getIssuerID(){return issuerID;}
    public String getReceiverID(){return receiverID;}
    public String getIssuerNickname(){return issuerNickname;}
    public String getReceiverNickname(){return receiverNickname;}
    public String getTokenStatus(){return tokenStatus;}
    public String getCurrentMessage(){return currentMessage;}
    public String getPreviousMessage(){return previousMessage;}
    public String getChatID(){return chatID;}
    public int getChatCount(){return chatCount;}
    public String getDateCreated(){return dateCreated;}


    //DEFAULT CONSTRUCTOR
    public ChallengeToken()
    {
    }

    //PARAMETERIZED CONSTRUCTOR
    public ChallengeToken(String issuerID, String receiverID, String issuerNickname, String receiverNickname)
    {
        this.issuerID = issuerID;
        this.receiverID = receiverID;
        this.issuerNickname = issuerNickname;
        this.receiverNickname = receiverNickname;
        tokenStatus = "ISSUED";
    }

    @Override
    public boolean equals(Object o)
    {
        boolean isSame = false;
        //cast object to Like and check it is not null and is a Like object
        //if((ChallengeToken) o != null && o instanceof ChallengeToken)
        if(o instanceof Notification)
        {
            return isSame;
        }
        else if((ChallengeToken) o != null)
        {
            isSame = this.chatID.equals(((ChallengeToken) o).chatID);
        }
        return isSame;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public int compareTo(Object o)
    {
        int ordering;

        if(o instanceof ChallengeToken)
        {
            //(LOCAL VALUE > OTHER VALUE) INCREASE VALUE
            //(LOCAL VALUE < OTHER VALUE) DECREASE VALUE
            //OTHERWISE VALUE IS 0

            Date thisEventDate = null;
            Date otherEventDate = null;
            Event otherEvent = (Event) o;

            //convert date stringsd to date objects so they can be compared
            try {
                thisEventDate = new SimpleDateFormat("dd-MM-YY H:mm").parse(dateCreated);
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
            ordering = 1;
        }

        return ordering;
    }



}
