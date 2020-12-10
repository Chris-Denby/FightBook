package com.example.fightbook;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;

public class User implements Comparable
{
  //personal info
  private String userID;
  private String email = "not yet set";
  private String nickname = "not yet set";
  private String firstName = "not yet set";
  private String lastName = "not yet set";
  private int age= 0;
  private String gender = "not yet set";
  private String country ="not yet set";
  private String city = "not yet set";
  private Date dateJoined;
  private String lastSeasonUpdate = "-";
  private String userStatus = "";

  private String bannerImage = "";
  private String avatarImageId = "";

  //not persisted in DB
  Bitmap avatarImage;

  //fight history
  private int totalWins = 0;
  private int totalLosses = 0;
  private double killDeathRatio = 0;
  private String lastWin = "No fights recorded";
  private String lastLoss = "No fights recorded";

  private int headCutsGiven = 0;
  private int headThrustsGiven = 0;
  private int torsoCutsGiven = 0;
  private int torsoThrustsGiven = 0;
  private int limbCutsGiven = 0;
  private int limbThrustsGiven = 0;
  private int headCutsReceived = 0;
  private int headThrustsReceived = 0;
  private int torsoCutsReceived = 0;
  private int torsoThrustsReceived = 0;
  private int limbCutsReceived = 0;
  private int limbThrustsReceived = 0;

  //game attributes
  private int rank;
  private int level;
  private double xp;
  private double maxXP = 7500;
  private int bestLevel = 0;

  //private int bounty = 0; //gets calculated as a percentage of total points


  ArrayList awardsList = new ArrayList<Award>();
  int headHunterAwards = 0;
  int bodyBreakerAwards = 0;
  int limbTakerAwards = 0;
  int thrustmasterAwards = 0;
  int butcherAwards = 0;

  //other
  private String authenticationKey =""; //unique key generated when user object is constructed

  Boolean isFollowed;
  Boolean isFollower;

  //default constructor
  public User()
  {
    calculateLevel();
  }

  //parameterized constructor
  public User(String email, String password){
    email = email;
    calculateLevel();

  }

  public User(String email, String password, String firstName, String lastname, String nickname, String country, String city, String id)
  {
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastname;
    this.nickname = nickname;
    this.country = country;
    this.city = city;
    userID = id;
    calculateLevel();
  }

  //GET METHODS
  public String getUserID()
  {
    return userID;
  }
  public String getNickname()
  {
    return nickname;
  }
  public String getFirstName()
  {
    return firstName;
  }
  public String getLastName()
  {
    return lastName;
  }
  public String getWholeName(){
    String wholeName = firstName + " " + lastName;
    return wholeName;
  }

  public String getCountry() { return country;}
  public String getCity()
  {
    return city;
  }
  public String getGender()
  {
    return gender;
  }
  public Date getDateJoined(){return dateJoined;}
  public int getAge()
  {
    return age;
  }
  public int getLevel(){calculateLevel(); return level;}
  public double getxp(){return xp;}
  public String getEmail(){return email;}
  public String getAuthKey(){return authenticationKey;}
  public int getBestLevel(){return bestLevel;}
  public String getBannerImage() {
    return bannerImage;
  }
  public String getAvatarImageId() {
    return avatarImageId;
  }
  public String getLastSeasonUpdate() {return lastSeasonUpdate;}
  public String getUserStatus(){return userStatus;}

  //SET METHODS
  public void setUserID(String id){userID = id;}
  public void setFirstName(String name){firstName = name;}
  public void setLastName(String name){lastName = name;}
  public void setAge(int num){age = num;};
  public void setGender(String sex){gender = sex;}
  public void setCountry(String country){this.country = country;}
  public void setCity(String city){this.city = city;}
  public void setNickname(String nickname)

  {
    this.nickname = nickname;
  }
  public void setDateJoined(Date joined){dateJoined = joined;}
  public void setEmailAddress(String email){email = email;}
  public void setAuthKey(String authKey){authenticationKey = authKey;}
  public void setBannerImage(String bannerImage) {
    this.bannerImage = bannerImage;
  }
  public void setAvatarImageId(String avatarImageId) {
    this.avatarImageId = avatarImageId;
  }
  public void setLastSeasonUpdate(String date) {lastSeasonUpdate = date;}
  public void setUserStatus(String status){userStatus = status;}



  public void calculateLevel()
  {
    int tempLevel = (int) xp/300;
    int level;
    if(tempLevel < 1)
    {
      level = 1;
      setLevel(1);
    }
    else
    if(tempLevel > 25)
    {
      level = 25;
      setLevel(25);
    }
    else
    {
      level = tempLevel;
      setLevel(level);
    }

    //sets the users best level value to the current level if it is higher
    if(level > bestLevel)
    {
      setBestLevel(level);
    }
    else
    {
      //nothing level stays as it is
    }
    setRank();
  }


  public void setRank()
  {

    if(level > 0 & level < 5)
    {
      rank = 1;
    }
    if(level > 4 & level < 10)
    {
      rank = 2;
    }
    if(level > 9 & level < 15)
    {
      rank = 3;
    }
    if(level > 15 & level < 20)
    {
      rank = 4;
    }
    if(level > 19 & level < 25)
    {
      rank = 5;
    }
    if(level == 25)
    {
      rank = 6;
    }
  }

  public void setxp(double xp) {

    //this.xp = xp;

    if ((this.xp + xp) < 300) {
      this.xp = 300;
    }
    else
    if ((this.xp + xp) > maxXP) {
      this.xp = maxXP;
    }
    else
    {
      this.xp = this.xp + xp;
    }
  }
  public void setLevel(int lvl)
  {
    level = lvl;
  }
  public void setLastWin(String opponent){lastWin = opponent;}
  public void setLastLoss(String opponent){lastLoss = opponent;}
  public void setBestLevel(int num){bestLevel = num;}
  public int getRank() {return rank;}
  public int getTotalWins(){return totalWins;}
  public void addWin(){totalWins = totalWins + 1;}
  public void addLoss(){totalLosses = totalLosses + 1;}
  public int getTotalLosses(){return totalLosses;}
  public String getLastWin(){return lastWin;}
  public String getLastLoss(){return lastLoss;}


  public void addAward(Award award){awardsList.add(award);}
  public void addHeadHunterAward(){headHunterAwards = headHunterAwards + 1;}
  public void addBodyBreakerAward(){bodyBreakerAwards = bodyBreakerAwards + 1;}
  public void addLimbTakerAward(){limbTakerAwards = limbTakerAwards + 1;}
  public void addThrustmasterAward(){thrustmasterAwards = thrustmasterAwards + 1;}
  public void addButcherAward(){butcherAwards = butcherAwards + 1;}
  public int getHeadHunterAwards() {
    return headHunterAwards;
  }
  public int getBodyBreakerAwards() {
    return bodyBreakerAwards;
  }
  public int getLimbTakerAwards() {
    return limbTakerAwards;
  }
  public int getThrustmasterAwards() {
    return thrustmasterAwards;
  }
  public int getButcherAwards() {
    return butcherAwards;
  }

  public int getHeadCutsGiven() {
    return headCutsGiven;
  }
  public int getHeadThrustsGiven() {
    return headThrustsGiven;
  }
  public int getTorsoCutsGiven() {
    return torsoCutsGiven;
  }
  public int getTorsoThrustsGiven() {
    return torsoThrustsGiven;
  }
  public int getLimbCutsGiven() {
    return limbCutsGiven;
  }
  public int getLimbThrustsGiven() {
    return limbThrustsGiven;
  }
  public int getHeadCutsReceived() {
    return headCutsReceived;
  }
  public int getHeadThrustsReceived() {
    return headThrustsReceived;
  }
  public int getTorsoCutsReceived() {
    return torsoCutsReceived;
  }
  public int getTorsoThrustsReceived() {
    return torsoThrustsReceived;
  }
  public int getLimbCutsReceived() {
    return limbCutsReceived;
  }
  public int getLimbThrustsReceived() {
    return limbThrustsReceived;
  }

  public void setHeadCutsGiven(int headCutsGiven) {
    this.headCutsGiven = this.headCutsGiven + headCutsGiven;
  }
  public void setHeadThrustsGiven(int headThrustsGiven) {
    this.headThrustsGiven = this.headThrustsGiven +headThrustsGiven;
  }
  public void setTorsoCutsGiven(int torsoCutsGiven) {
    this.torsoCutsGiven = this.torsoCutsGiven + torsoCutsGiven;
  }
  public void setTorsoThrustsGiven(int torsoThrustsGiven) {
    this.torsoThrustsGiven = this.torsoThrustsGiven + torsoThrustsGiven;
  }
  public void setLimbCutsGiven(int limbCutsGiven) {
    this.limbCutsGiven = this.limbCutsGiven + limbCutsGiven;
  }
  public void setLimbThrustsGiven(int limbThrustsGiven) {
    this.limbThrustsGiven = this.limbThrustsGiven + limbThrustsGiven;
  }
  public void setHeadCutsReceived(int headCutsReceived) {
    this.headCutsReceived = this.headCutsReceived + headCutsReceived;
  }
  public void setHeadThrustsReceived(int headThrustsReceived) {
    this.headThrustsReceived = this.headThrustsReceived + headThrustsReceived;
  }
  public void setTorsoCutsReceived(int torsoCutsReceived) {
    this.torsoCutsReceived = this.torsoCutsReceived + torsoCutsReceived;
  }
  public void setTorsoThrustsReceived(int torsoThrustsReceived) {
    this.torsoThrustsReceived = this.torsoThrustsReceived + torsoThrustsReceived;
  }
  public void setLimbCutsReceived(int limbCutsReceived) {
    this.limbCutsReceived = this.limbCutsReceived + limbCutsReceived;
  }
  public void setLimbThrustsReceived(int limbThrustsReceived) {
    this.limbThrustsReceived = this.limbThrustsReceived +limbThrustsReceived;
  }



  //to String
  public String toString()
  {
    return nickname;
  }

  @Override
  public int compareTo(Object o)
  {
    User otherUser = (User) o;

    int ordering = (level > otherUser.getLevel()) ? 1 : (level < otherUser.getLevel()) ? -1 : 0;
    return ordering;
  }

}
