package com.example.fightbook;

import android.media.Image;

public class Award implements Comparable
{

    String type;
    String title;
    String color = "nil";
    String caption;
    int xpRequired;
    Image icon;
    int qtyObtained;
    String iconAddress;

    String headhunterImagePath = "headhunteraward";
    String bodybreakerImagePath = "bodybreakeraward";
    String limbtakerImagePath = "limbtakeraward";
    String thrustmasterImagePath = "trident";
    String butcherImagePath = "butcheraward";
    //String onTouchMasterPath =@drawable/...

    public String getIconAddress()
    {
        return iconAddress;
    }


    //default constructor
    public Award(String type)
    {
        setAwardType(type);
    }

    public Award(String type, String color)
    {
        setAwardType(type);
        this.color = color;
    }

    public Award(String type, int num)
    {
        setAwardType(type);
        qtyObtained = num;
    }

    public Award(String type, int num, String color)
    {
        qtyObtained = num;
        this.color = color;
        setAwardType(type);
    }

    public void setQtyObtained(int num){qtyObtained = num;}


    public void setAwardType(String type)
    {
        if(type.equals("Headhunter"))
        {
            title = "Headhunter";
            caption = "Headhunter: Deliver 5 or more head hits in a fight";
            //set border color
            iconAddress = headhunterImagePath;
        }

        if(type.equals("Bodybreaker"))
        {
            title = "Bodybreaker";
            caption = "Bodybreaker: Deliver 5 or more torso hits in a fight";
            iconAddress = bodybreakerImagePath;
        }

        if(type.equals("Limbtaker"))
        {
            title = "Limbtaker";
            caption = "Limbtaker: Deliver 5 or more limb hits in a fight";
            iconAddress = limbtakerImagePath;
        }

        if(type.equals("Thrustmaster"))
        {
            title = "Thrustmaster";
            caption = "Thrustmaster: Use only thrusts in fight";
            iconAddress = thrustmasterImagePath;
        }

        if(type.equals("Butcher"))
        {
            title = "Butcher";
            caption = "Butcher: Use only cuts in fight";
            iconAddress = butcherImagePath;
        }

        //if(type.equals("OneTouchMaster"))
        //{
        //    title = "One-Touch Master";
        //    caption = "One-Touch Master: Win a fight with only hit";
        //    iconAddress = oneTouchMasterImagePath;
        //}
    }


    public String getTitle(){return title;}
    public String getCaption(){return caption;}
    public Image getImage(){return icon;}
    public int getQtyObtained(){return qtyObtained;}
    public String getColor(){return color;}

    @Override
    public int compareTo(Object o)
    {
        Award otherAward = (Award) o;

        int ordering = (qtyObtained > otherAward.getQtyObtained()) ? 1 : (qtyObtained < otherAward.getQtyObtained()) ? -1 : 0;
        return ordering;
    }





}
