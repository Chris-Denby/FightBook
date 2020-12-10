package com.example.fightbook;

import android.graphics.Bitmap;

public class Chat
{
    String sender;
    String senderID;
    String chatText;
    String date;
    String chatID;
    Bitmap avatarImage;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Chat(String sender, String senderID, String chatText, String date)
    {
        this.sender = sender;
        this.chatText = chatText;
        this.date = date;
        this.senderID = senderID;
    }

    public String getSenderID() {return senderID;}

    public void setSenderID(String ID) {senderID = ID;}

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public Chat()
    {

    }

    @Override
    public boolean equals(Object o)
    {
        boolean isSame = false;
        //cast object to Like and check it is not null and is a Like object
        if((Chat) o != null && o instanceof Chat)
        {
            isSame = (this.date + this.chatText).equals(((Chat) o).date + ((Chat) o).chatText);
        }
        return isSame;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }


}
