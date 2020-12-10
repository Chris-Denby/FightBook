package com.example.fightbook;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    String selectedUserID;
    String currentUserID;
    String currentUserNickName;
    String selectedUserNickName;
    User currentUser;

    boolean following;

    TextView nickNameLabel;
    TextView realNameLabel;
    TextView locationLabel;
    CircleImageView profileAvatar;

    TextView followersLabel;
    TextView followingLabel;
    TextView dateJoinedLabel;

    Button followButton;
    Button challengeButton;
    ImageView changeAvatarIcon;

    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();

        Bundle intentBundle = getIntent().getExtras();
        selectedUserID = intentBundle.getString("SELECTED_USER_ID");
        selectedUserNickName = intentBundle.getString("SELECTED_USER_NICKNAME");
        currentUserID = intentBundle.getString("CURRENT_USER_ID");
        currentUserNickName = intentBundle.getString("CURRENT_USER_NICKNAME");

        //############ FILL USER INTERFACE HERE ##############
        nickNameLabel = findViewById(R.id.nickname_label);
        realNameLabel = findViewById(R.id.real_name_label);
        locationLabel = findViewById(R.id.location_label);
        profileAvatar = findViewById(R.id.profile_avatar);

        followersLabel = findViewById(R.id.followers_label);
        followingLabel = findViewById(R.id.following_label);
        dateJoinedLabel = findViewById(R.id.date_joined_label);

        //buttons & icons
        followButton = findViewById(R.id.follow_button);
        challengeButton = findViewById(R.id.challenge_button);
        changeAvatarIcon = findViewById(R.id.change_avatar_icon);

        scrollView = findViewById(R.id.scroll_view);

        populateUser();

        final String issuerID = currentUserID;
        final String receiverID = selectedUserID;

        //check if the profile is for the current user
        if(selectedUserID.equals(currentUserID))//if profile is for the current user - disable the challenge button
        {
            challengeButton.setText("NA");
            //challengeButton.setBackgroundResource(R.drawable.challenge_icon_greyed);
            challengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    Toast.makeText(getApplicationContext(), "You can't challenge yourself!", Toast.LENGTH_SHORT).show();
                }
            });

            //if profile is for the current user - disable the follow button and challenge button
            challengeButton.setVisibility(View.INVISIBLE);
            followButton.setVisibility(View.INVISIBLE);

            //if the profile is for the current user, enable the change avatar button
            changeAvatarIcon.setVisibility(View.VISIBLE);
            changeAvatarIcon.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {openGallery(); }});
                profileAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openGallery();

                    }
                });
            }
        else
        // profile is for a different user
        {
            DatabaseReference issuedRef = FirebaseDatabase.getInstance().getReference("Challenges").child(issuerID).child("IssuedChallenges").child(receiverID);
            //check if path Challenges > ME > Issued Challenges > RECEIVER exists
            issuedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    //if challenge already exists with the selected user - disable the challenge button
                    if(dataSnapshot.exists())
                    {
                        challengeButton.setText("Challenged");
                    }
                    else
                    {
                        //if challenge does not exist with the selected user, enable challenge button
                        challengeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                issueChallenge();
                            }
                        });
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference followRef = FirebaseDatabase.getInstance().getReference("Follows").child(currentFirebaseUser.getUid()).child("Following").child(selectedUserID);
            followRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        //if the user is already on the current users follow list
                        following = true;
                        followButton.setText("Unfollow");
                    }
                    else
                    {
                        //if the user is not on the current users follow list
                        following = false;
                        followButton.setText("Follow");
                    }

                    followButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            follow();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //IF SOMEONE ELSES PROFILE
            changeAvatarIcon.setVisibility(View.INVISIBLE);
            profileAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //do nothing

                }
            });
        }
    }

    public void populateUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(selectedUserID);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);

                        nickNameLabel.setText(user.getNickname());
                        realNameLabel.setText(user.getFirstName() + " " + user.getLastName());
                        locationLabel.setText(user.getCity() + ", " + user.getCountry());



                        loadAvatarImage(user.getAvatarImageId());

                                    //##############STATISTICS FOR CURRENT USER##################################
                                    //TOTAL HITS GIVEN/RECEIVED
                                    int currentUserTotalHeadHitsReceived = user.getHeadCutsReceived() + user.getHeadThrustsReceived();
                                    int currentUserTotalTorsoHitsReceived = user.getTorsoCutsReceived() + user.getTorsoThrustsReceived();
                                    int currentUserTotalLimbHitsReceived = user.getLimbCutsReceived() + user.getLimbThrustsReceived();

                                    int currentUserTotalHeadHitsGiven =  user.getHeadCutsGiven() + user.getHeadThrustsGiven();
                                    int currentUserTotalTorsoHitsGiven = user.getTorsoCutsGiven() + user.getTorsoThrustsGiven();
                                    int currentUserTotalLimbHitsGiven = user.getLimbCutsGiven() + user.getLimbThrustsGiven();

                        //ARRAY TO HOLD COLOURS FOR PIE CHARTS
                        ArrayList<Integer> coloursThree = new ArrayList<>();
                        coloursThree.add(getColor(R.color.holo_blue_light));
                        coloursThree.add(getColor(R.color.holo_red_light));
                        coloursThree.add(getColor(R.color.holo_light_green));

                        ArrayList<Integer> hitColours = new ArrayList<>();
                        hitColours.add(getColor(R.color.holo_blue_light));
                        hitColours.add(getColor(R.color.holo_orange));

                        ArrayList<Integer> winlossColours = new ArrayList<>();
                        hitColours.add(getColor(R.color.holo_light_green));
                        hitColours.add(getColor(R.color.holo_red_light));

                        //TOTAL FIGHTS
                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        final ArrayList<PieEntry> totalFightsEntries = new ArrayList<PieEntry>();
                        if(user.getTotalWins() == 0 && user.getTotalLosses() > 0)
                        {
                            totalFightsEntries.add(new PieEntry((float) 0, "Wins"));
                            totalFightsEntries.add(new PieEntry((float) 100, "Losses"));
                        }
                        else if(user.getTotalLosses() == 0 && user.getTotalWins() > 0)
                        {
                            totalFightsEntries.add(new PieEntry((float) 100, "Wins"));
                            totalFightsEntries.add(new PieEntry((float) 0, "Losses"));
                        }
                        else if (user.getTotalWins() > 0 && user.getTotalLosses() > 0)
                        {
                            totalFightsEntries.add(new PieEntry((float) ((user.getTotalWins() * 100) / user.getTotalWins() + user.getTotalLosses()), "Wins"));
                            totalFightsEntries.add(new PieEntry((float)((user.getTotalLosses() * 100) / user.getTotalWins() + user.getTotalLosses()), "Losses"));
                        }
                        PieChart totalFightsPieChart = createPieChart(R.id.win_loss_pie_chart, totalFightsEntries, hitColours, (user.getTotalLosses() + user.getTotalWins())+"", "Total Fights");
                        //totalFightsPieChart.setCenterTextOffset(0f, -3f);
                        totalFightsPieChart.getDescription().setPosition(365f, 310f);
                        totalFightsPieChart.getDescription().setTextSize(12);

                        //HITS TAKEN BY ZONE
                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> headHitsReceivedEntries = new ArrayList<PieEntry>();
                        if(currentUserTotalHeadHitsReceived == 0)
                        {
                            headHitsReceivedEntries.add(new PieEntry((float)0, "Cuts"));
                            headHitsReceivedEntries.add(new PieEntry((float)0, "Thrusts"));
                        }
                        else
                        {
                            headHitsReceivedEntries.add(new PieEntry((float)((user.getHeadCutsReceived()*100)/currentUserTotalHeadHitsReceived), "Cuts"));
                            headHitsReceivedEntries.add(new PieEntry((float)((user.getHeadThrustsReceived()*100)/currentUserTotalHeadHitsReceived), "Thrusts"));
                        }
                        PieChart headHitsReceivedPieChart = createPieChart(R.id.head_hits_taken_pie_chart, headHitsReceivedEntries, hitColours, currentUserTotalHeadHitsReceived+"", "Head");
                        //headHitsReceivedPieChart.setCenterTextOffset(0f, -3f);
                        headHitsReceivedPieChart.getDescription().setPosition(240f, 250f);

                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> torsoHitsReceivedEntries = new ArrayList<PieEntry>();
                        if(currentUserTotalTorsoHitsReceived == 0)
                        {
                            torsoHitsReceivedEntries.add(new PieEntry((float)0, "Cuts"));
                            torsoHitsReceivedEntries.add(new PieEntry((float)0, "Thrusts"));
                        }
                        else
                        {
                            torsoHitsReceivedEntries.add(new PieEntry((float)((user.getTorsoCutsReceived()*100)/currentUserTotalTorsoHitsReceived), "Cuts"));
                            torsoHitsReceivedEntries.add(new PieEntry((float)((user.getTorsoThrustsReceived()*100)/currentUserTotalTorsoHitsReceived), "Thrusts"));
                        }
                        PieChart torsoHitsReceivedPieChart = createPieChart(R.id.torso_hits_taken_pie_chart, torsoHitsReceivedEntries, hitColours, currentUserTotalTorsoHitsReceived+"", "Torso");
                        //torsoHitsReceivedPieChart.setCenterTextOffset(0f, -3f);
                        torsoHitsReceivedPieChart.getDescription().setPosition(240f, 250f);

                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> limbHitsReceivedEntries = new ArrayList<PieEntry>();
                        if(currentUserTotalLimbHitsReceived == 0)
                        {
                            limbHitsReceivedEntries.add(new PieEntry((float)0, "Cuts"));
                            limbHitsReceivedEntries.add(new PieEntry((float)0, "Thrusts"));
                        }
                        else
                        {
                            limbHitsReceivedEntries.add(new PieEntry((float)((user.getLimbCutsReceived()*100)/currentUserTotalLimbHitsReceived), "Cuts"));
                            limbHitsReceivedEntries.add(new PieEntry((float)((user.getLimbThrustsReceived()*100)/currentUserTotalLimbHitsReceived), "Thrusts"));
                        }
                        PieChart limbHitsReceivedPieChart = createPieChart(R.id.limb_hits_taken_pie_chart, limbHitsReceivedEntries, hitColours, currentUserTotalLimbHitsReceived+"", "Limbs");
                        //limbHitsReceivedPieChart.setCenterTextOffset(0f, -3f);
                        limbHitsReceivedPieChart.getDescription().setPosition(250f, 250f);

                        //HITS RECEIVED BY ZONE
                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> headHitsGivenEntries = new ArrayList<PieEntry>();
                        if (currentUserTotalHeadHitsGiven == 0)
                        {
                            headHitsGivenEntries.add(new PieEntry((float)0, "Cuts"));
                            headHitsGivenEntries.add(new PieEntry((float)0, "Thrusts"));
                        }
                        else
                        {
                            headHitsGivenEntries.add(new PieEntry((float)((user.getHeadCutsGiven()*100)/currentUserTotalHeadHitsGiven), "Cuts"));
                            headHitsGivenEntries.add(new PieEntry((float)((user.getHeadThrustsGiven()*100)/currentUserTotalHeadHitsGiven), "Thrusts"));

                        }
                        PieChart headHitsGivenPieChart = createPieChart(R.id.head_hits_given_pie_chart, headHitsGivenEntries, hitColours, currentUserTotalHeadHitsGiven+"", "Head");
                        //headHitsGivenPieChart.setCenterTextOffset(0f, -3f);
                        headHitsGivenPieChart.getDescription().setPosition(240f, 250f);

                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> torsoHitsGivenEntries = new ArrayList<PieEntry>();
                        if (currentUserTotalTorsoHitsGiven == 0)
                        {
                            torsoHitsGivenEntries.add(new PieEntry((float) 0, "Cuts"));
                            torsoHitsGivenEntries.add(new PieEntry((float) 0, "Thrusts"));
                        }
                        else
                        {
                            torsoHitsGivenEntries.add(new PieEntry((float)((user.getTorsoCutsGiven()*100)/currentUserTotalTorsoHitsGiven), "Cuts"));
                            torsoHitsGivenEntries.add(new PieEntry((float)((user.getTorsoThrustsGiven()*100)/currentUserTotalTorsoHitsGiven), "Thrusts"));
                        }
                        PieChart torsoHitsGivenPieChart = createPieChart(R.id.torso_hits_given_pie_chart, torsoHitsGivenEntries, hitColours, currentUserTotalTorsoHitsGiven+"", "Torso");
                        //torsoHitsGivenPieChart.setCenterTextOffset(0f, -3f);
                        torsoHitsGivenPieChart.getDescription().setPosition(240f, 250f);

                        //createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
                        ArrayList<PieEntry> limbHitsGivenEntries = new ArrayList<PieEntry>();
                        if (currentUserTotalLimbHitsGiven == 0)
                        {
                            limbHitsGivenEntries.add(new PieEntry((float) 0, "Cuts"));
                            limbHitsGivenEntries.add(new PieEntry((float) 0, "Thrusts"));
                        }
                        else
                        {
                            limbHitsGivenEntries.add(new PieEntry((float) ((user.getLimbCutsGiven() * 100) / currentUserTotalLimbHitsGiven), "Cuts"));
                            limbHitsGivenEntries.add(new PieEntry((float) ((user.getLimbThrustsGiven() * 100) / currentUserTotalLimbHitsGiven), "Thrusts"));
                        }
                        PieChart limbHitsGivenPieChart = createPieChart(R.id.limb_hits_given_pie_chart, limbHitsGivenEntries, hitColours, currentUserTotalLimbHitsGiven+"", "Limbs");
                        //limbHitsGivenPieChart.setCenterTextOffset(0f, -3f);
                        limbHitsGivenPieChart.getDescription().setPosition(250f, 250f);
                  }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


        final ArrayList<Follow> followedUsersArray = new ArrayList<Follow>();
        final ArrayList<Follow> followingUsersArray = new ArrayList<Follow>();
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference().child("Follows").child(selectedUserID).child("Following");
        followingRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow followedUser = dataSnapshot.getValue(Follow.class);
                followedUsersArray.add(followedUser);
                followingLabel.setText(""+followedUsersArray.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference().child("Follows").child(selectedUserID).child("Followers");
        followsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow follower = dataSnapshot.getValue(Follow.class);
                followingUsersArray.add(follower);
                followersLabel.setText(""+followingUsersArray.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void follow()
    {
        if(following == false)
        {

            //add selected user ID to the list of users the current user is following
            database.getReference("Follows").child(currentFirebaseUser.getUid()).child("Following").child(selectedUserID).setValue(new Follow(selectedUserID, selectedUserNickName));
            //add current user ID to the list of users the selectged user is following
            database.getReference("Follows").child(selectedUserID).child("Followers").child(currentFirebaseUser.getUid()).setValue(new Follow(currentFirebaseUser.getUid(), currentUserNickName));

            //update appearance of follow button
            followButton.setText("Following");
            following = true;


            String notificationText = "is now following you";

            Date currentTime = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YY H:mm");

            Notification notification = new Notification();
            notification.setCreatedDate(formatter.format(currentTime));
            notification.setText(notificationText);
            notification.setNodeID("follow");
            notification.setIssuerID(currentUserID);
            notification.setIssuerNickname(currentUserNickName);
            notification.setUserBID("");
            notification.setUserBNickname("");
            String notificationKey = database.getReference("Notifications").child(selectedUserID).push().getKey();
            notification.setNotificationKey(notificationKey);
            DatabaseReference notificationsRef = database.getReference("Notifications").child(selectedUserID).child(notificationKey);
            notificationsRef.setValue(notification);
        }
        else if(following == true)
        {
            database.getReference("Follows").child(currentFirebaseUser.getUid()).child("Following").child(selectedUserID).removeValue();
            database.getReference("Follows").child(selectedUserID).child("Followers").child(currentFirebaseUser.getUid()).removeValue();

            //update appearance of follow button
            followButton.setText("Follow");
            following = false;
        }


    }

    public void issueChallenge()
    {
        final String issuerID = currentUserID;
        final String issuerNN = currentUserNickName;
        final String receiverID = selectedUserID;
        final String receiverNN = selectedUserNickName;

        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YY H:mm");

        ChallengeToken challenge = new ChallengeToken(issuerID, receiverID, issuerNN, receiverNN);
        //get a database reference to the chat node
        String chatID = database.getReference("Chats").push().getKey();
        //add creference to change node to the challenge token
        challenge.setChatID(chatID);
        //created date
        challenge.setDateCreated(formatter.format(currentTime));
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //save issued challenge to current user node
        database.child("Challenges").child(issuerID).child("IssuedChallenges").child(receiverID).setValue(challenge);
        //save received challenge to receivers user node
        database.child("Challenges").child(receiverID).child("ReceivedChallenges").child(issuerID).setValue(challenge);
        challengeButton.setText("Challenged");
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            }
        });
        MainActivity.notificationCount ++;
    }

    public void openGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public void loadAvatarImage(String photoPath)
    {
        if(!photoPath.equals(""))
        {
            if (selectedUserID.equals(currentUserID))
            {
                //if the profile is viewing the current logged in user
                Toast.makeText(this, "Your profile", Toast.LENGTH_SHORT).show();
                if (Constants.getInstance().avatarImageCache.containsKey(selectedUserID))
                {
                    //if the selected users avatar image already exists in the cache in the Constants singleton
                    profileAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(selectedUserID));
                    //Toast.makeText(this, "Avatar loaded from cache", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    //no avatar image loaded yet - download and load to cache in Constants singleton
                    //###############DOWNLOAD THE IMAGE TO MEMORY#####################
                    final long ONE_MEGABYTE = 1024 * 1024;
                    StorageReference storage = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storage.child("AvatarImages").child(selectedUserID).child(photoPath);
                    try {
                        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileAvatar.setImageBitmap(bitmap);
                                Constants.getInstance().avatarImageCache.put(selectedUserID, bitmap);
                                Toast.makeText(ProfileActivity.this, "Avatar loaded to cache", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "Image", e.getMessage());
                    }
                }
            }
            else
                {
                //if the viewed profile is of another user
                //Toast.makeText(this, "Someone elses profile", Toast.LENGTH_SHORT).show();
                if (Constants.getInstance().avatarImageCache.containsKey(selectedUserID)) {
                    //if the selected users avatar image already exists in the cache in the Constants singleton
                    profileAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(selectedUserID));
                }
                else {
                    //no avatar image loaded yet - download and load to cache in Constants singleton
                    //###############DOWNLOAD THE IMAGE TO MEMORY#####################
                    final long ONE_MEGABYTE = 1024 * 1024;
                    StorageReference storage = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storage.child("AvatarImages").child(selectedUserID).child(photoPath);
                    try {
                        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profileAvatar.setImageBitmap(bitmap);
                                Constants.getInstance().avatarImageCache.put(selectedUserID, bitmap);
                                Toast.makeText(ProfileActivity.this, "Avatar loaded to cache", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "Image", e.getMessage());
                    }

                }
            }
        }
    }

    public File createImageFile() throws IOException
    {
        //String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = selectedUserID;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile
                (
                        imageFileName, /* prefix */
                        ".jpg", /* suffix */
                        storageDir
                );

        //save a file: path for use with ACTION_VIEW intents
        return imageFile;
    }

    public File saveBitmapToFile(File file, Bitmap selectedBitmap)
    {
        try {
            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e)
        {
            Toast.makeText(this, "Error converting image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void checkOrientationAndUpload(File file, String nodeID)
    {
        String filepath = file.getPath();
        int exiforientation = 0;
        int orientationCorrection = 0;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            exiforientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            //Toast.makeText(getApplicationContext(), "Orientation: " + exiforientation, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        switch(exiforientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                Toast.makeText(getApplicationContext(), "photo: verticle", Toast.LENGTH_SHORT).show();
                orientationCorrection = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                orientationCorrection = 180;
                Toast.makeText(getApplicationContext(), "photo: landscape (right)", Toast.LENGTH_SHORT).show();
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                orientationCorrection = 270;
                Toast.makeText(getApplicationContext(), "photo: upside-down", Toast.LENGTH_SHORT).show();
                break;

            case ExifInterface.ORIENTATION_NORMAL:
                orientationCorrection = 0;
                Toast.makeText(getApplicationContext(), "photo: landscape (left)", Toast.LENGTH_SHORT).show();

            default:
                Toast.makeText(getApplicationContext(), "photo: default", Toast.LENGTH_SHORT).show();
        }

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("rotation", ""+ orientationCorrection)
                .build();

        uploadPhoto(saveBitmapToFile(file, rotateImage(createBitmap(file), orientationCorrection)), metadata);
    }

    public Bitmap rotateImage(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public String uploadPhoto(final File bitmapFile, final StorageMetadata metadata)
    {
        //continue to upload file
        File convertedImage = bitmapFile;

        Uri file = Uri.fromFile(convertedImage);

        //final String filename = file.getLastPathSegment();
        String filename = selectedUserID;

        //update user node to record image nodeID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("avatarImageId");
        userRef.setValue(filename);

        //upload image to storage database
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference photoRef = storage.getReference().child("AvatarImages").child(selectedUserID).child(filename);
        try
        {
            final UploadTask uploadTask = photoRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //to do when upload unsuccessfull
                    Toast.makeText(getApplicationContext(), "Photo failed to upload", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //to do when upload successfull
                    Toast.makeText(getApplicationContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                    // Update metadata properties
                    photoRef.updateMetadata(metadata)
                            .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    // Updated metadata is in storageMetadata
                                    Toast.makeText(getApplicationContext(), "Metadata updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!
                                    Toast.makeText(getApplicationContext(), "Metadata failed to update", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    public Bitmap createBitmap(File file)
    {
        Bitmap selectedBitmap = null;
        //CREATE BITMAP
        // BitmapFactory options to downsize the image
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            //SCALE BITMAP
            // The new size we want to scale to - scale to adjust quality
            final int REQUIRED_SIZE = 75;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            //FINISH CREATING BITMAP


        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return selectedBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri selectedImage = data.getData();
                    try
                    {
                        //get seletected image as a bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                        //update storage database
                        StorageReference avatarRef = FirebaseStorage.getInstance().getReference("AvatarImages").child(selectedUserID).child(selectedUserID);
                        avatarRef.delete();

                        //create the bitmap file - re-orientate it and upload to storage
                        checkOrientationAndUpload(saveBitmapToFile(createImageFile(), bitmap), currentUserID);
                        //update cache
                        Constants.getInstance().userAvatar = bitmap;

                        //set selected picture to imageview
                        profileAvatar.setImageBitmap(bitmap);
                    }
                    catch (IOException e)
                    {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedUserID.equals(currentUserID))
        {

        }
        else
        {

        }
    }

    public PieChart createPieChart(int resourceID, ArrayList<PieEntry> entries, ArrayList<Integer> colours, String centreText, String description)
    {
        PieChart pieChart = (PieChart) findViewById(resourceID);
        final List<PieEntry> pieEntries;
        PieDataSet dataSet;
        if(centreText.equals("0"))
        {
            //IF THE DATA IS ZERO, SHOW GREY 100% CHART
            ArrayList<Integer> coloursGrey = new ArrayList<>();
            coloursGrey.add(getColor(R.color.darker_gray));
            pieEntries = new ArrayList<PieEntry>();
            pieEntries.add(new PieEntry((float)100, "Blank"));
            dataSet = new PieDataSet(pieEntries, "");
            dataSet.setColors(coloursGrey);

        }
        else
        {
            pieEntries = entries;
            dataSet = new PieDataSet(pieEntries, "");
            dataSet.setColors(colours);
        }
        PieData pieData = new PieData(dataSet);
        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.setCenterText(centreText);
        pieChart.setCenterTextOffset(0f, -6f);
        pieChart.animateY(750);
        pieChart.getDescription().setEnabled(true);
        //pieChart.getDescription().setPosition(315f, 305f);
        pieChart.getDescription().setText(description);
        pieChart.getDescription().setTypeface(Typeface.DEFAULT_BOLD);
        pieChart.getDescription().setTextSize(13);
        pieChart.getLegend().setEnabled(false);
        pieChart.setCenterTextSize(25);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(pieChart.getHoleRadius());
        pieChart.setDrawEntryLabels(false);

        pieChart.setOnChartValueSelectedListener(new com.github.mikephil.charting.listener.OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
            }
            @Override
            public void onNothingSelected() {

            }
        });
        return pieChart;
    }











}
