package com.example.fightbook;

import android.graphics.Bitmap;



import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Comparable
{
    String eventText = "";
    String dateCreated ="";
    String userBID = "";
    String userBNickName;
    String photoPath = "";
    String nodeID = "";
    String createdById = "";
    String createdByNickname = "";
    int commentsCount = 0;
    ArrayList<Chat> commentsArray = new ArrayList<>();
    int likesCount = 0;
    ArrayList<Like> likesArray = new ArrayList<>();
    String lastComment = "";
    String lastCommentSender = "";
    Boolean liked = false;
    Fight fight;

    //used to tell recyclerview adapter if items a header and use appro
    Boolean isHeader = false;


    //not persisted in DB
    Bitmap image;
    Bitmap avatarImage;

    //public void setCountryContextA(String country){countryContextA = country;}
    //public void setCountryContextB(String country){countryContextB = country;}
    //public void setcityContextA(String city){cityContextA = city;}
    //public void setcityContextB(String city){cityContextB = city;}

    //public String getCityContextA(){return cityContextA;}
    //public String getCityContextB(){return cityContextB;}

    public String getEventText() {
        return eventText;
    }
    public void setEventText(String eventText) {
        this.eventText = eventText;
    }
    public String getUserBID() {
        return userBID;
    }
    public void setUserBID(String userBID) {
        this.userBID = userBID;
    }
    public String getUserBNickName() {
        return userBNickName;
    }
    public void setUserBNickName(String userBNickName) {
        this.userBNickName = userBNickName;
    }


    public String getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public void setnodeID(String id){nodeID = id;}
    public String getnodeID(){return nodeID;}
    public Bitmap getImageBitmap() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public int getCommentsCount() {
        return commentsCount;
    }
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    public int getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public String getCreatedById() {
        return createdById;
    }
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }
    public void setCreatedByNickname(String nickname){this.createdByNickname = nickname;}
    public String getCreatedByNickname(){return createdByNickname;}

    //DEFAULT CONSTRUCTOR
    public Event()
    {
    }

    public Event(String nnameA, String nnameB, String text, String date)
    {
        this.createdByNickname = nnameA;
        this.userBNickName = nnameB;
        this.eventText = text;
        this.dateCreated = date;
    }

    @Override
    public int compareTo(Object o)
    {
        //(LOCAL VALUE > OTHER VALUE) INCREASE VALUE
        //(LOCAL VALUE < OTHER VALUE) DECREASE VALUE
        //OTHERWISE VALUE IS 0
        Event otherEvent = (Event) o;


        Date thisEventDate = null;
        Date otherEventDate = null;
        //convert date strings to date objects so they can be compared
        try
        {
            thisEventDate = new SimpleDateFormat("dd-MM-YY H:mm").parse(dateCreated);
            otherEventDate = new SimpleDateFormat("dd-MM-YY H:mm").parse(otherEvent.getDateCreated());

        }
        catch (ParseException e)
        {

        }

        //SORTING CONFIRMED WORKING CORRECTLY
        //int ordering = (thisEventDate.after(otherEventDate)) ? 1 : (thisEventDate.before(otherEventDate)) ? -1 : 0; //oldest to newest ascending
        int ordering = (thisEventDate.before(otherEventDate)) ? 1 : (thisEventDate.after(otherEventDate)) ? -1 : 0; // newest to oldest descending
        return ordering;
    }

    @Override
    public boolean equals(Object o)
    {
        boolean isSame = false;
        //cast object to Event and check it is not null and is an Event object
        if(o != null && o instanceof Event)
        {
            isSame = this.nodeID.equals(((Event) o).nodeID);
        }
        return isSame;
    }
}
