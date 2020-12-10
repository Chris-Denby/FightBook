package com.example.fightbook;

import java.io.Serializable;

public class Follow
{
    String userID;
    String nickName;

    public Follow(String id, String nname)
    {
        userID = id;
        nickName = nname;
    }

    public Follow()
    {}


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public boolean equals(Object o)
    {
        Follow other = (Follow) o;
        if(this.userID.equals(((Follow) o).getUserID()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
