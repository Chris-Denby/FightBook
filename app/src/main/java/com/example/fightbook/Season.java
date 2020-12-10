package com.example.fightbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Season implements Comparable
{
    String startDate;
    String seasonName;

    public Season(String date, String name)
    {
        startDate = date;
        seasonName = name;
    }

    public Season()
    {
        //default constructor
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    @Override
    public int compareTo(Object o) {


        Date otherSeasonDate = new Date();
        Date thisSeasonDate = new Date();

        Season otherSeason = (Season) o;

        //convert other seasons start date to a date object to be able to compare against
        String otherSeasonDateString = otherSeason.getStartDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        try
        {
            otherSeasonDate = dateFormat.parse(otherSeason.getStartDate());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        String thisSeasonDateString = startDate;
        try
        {
            thisSeasonDate = dateFormat.parse(thisSeasonDateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        int ordering = (thisSeasonDate.after(otherSeasonDate)) ? 1 : (thisSeasonDate.before(otherSeasonDate)) ? -1 : 0;
        return ordering;
    }
}
