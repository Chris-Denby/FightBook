package com.example.fightbook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FightActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser currentFirebaseUser;
    DatabaseReference database;
    FirebaseAuth auth;
    // Get a non-default Storage bucket

    String photoFileName = null;
    File photoFile;


    //ShareDialog shareResultsDialog;

    Intent previousIntent;
    Bundle blueUserInfoBundle;

    String blueUserId;  //email address used as key to find node in firebase database
    Fight fight;
    User redFighter;
    User blueFighter;
    User winner;
    User loser;
    String resultString;
    double baseXP;
    double xpForWin = 100;
    double xpForLoss = -100;
    int timeRemaining = 5;
    static int fightLength = 5;
    ArrayList awardsArray = new ArrayList<Award>();

    //start popup window elements
    PopupWindow startPopupWindow;
    PopupWindow endPopupWindow;
    PopupWindow pausePopupWindow;
    PopupWindow exitPopupWindow;

    View startFightPopup;
    View endFightPopup;
    ImageView redAvatar;
    ImageView blueAvatar;
    ImageView startRedAvatar;
    ImageView startBlueAvatar;
    TextView startRedNickNameLabel;
    TextView startBlueNickNameLabel;
    TextView vsLabel;
    TextView startTimerLabel;
    SeekBar timerLength;
    Button beginFightButton;
    Spinner redSystemSelector;
    Spinner blueSystemSelector;
    ArrayList<String> systemArray;
    ArrayAdapter<String> spinnerAdapter;

    TextView redHeadCutValueLabel;
    TextView redHeadThrustValueLabel;
    TextView redTorsoCutValueLabel;
    TextView redTorsoThrustValueLabel;
    TextView redLimbThrustValueLabel;
    TextView redLimbCutValueLabel;
    TextView blueHeadCutValueLabel;
    TextView blueHeadThrustValueLabel;
    TextView blueTorsoCutValueLabel;
    TextView blueTorsoThrustValueLabel;
    TextView blueLimbThrustValueLabel;
    TextView blueLimbCutValueLabel;

    //fight screen elements
    TextView redNickname;
    TextView blueNickname;
    TextView redTotalScore;
    TextView blueTotalScore;
    ImageView getRedAvatar;
    ImageView getBlueAvatar;
    View redBanner;
    View blueBanner;

    Boolean pauseState = true;
    CountDownTimer timer;
    Button cancelFightButton;
    Button pauseFightButton;
    Button redHeadCutButton;
    Button redHeadThrustButton;
    Button redTorsoCutButton;
    Button redTorsoThrustButton;
    Button redLimbCutButton;
    Button redLimbThrustButton;
    Button blueHeadCutButton;
    Button blueHeadThrustButton;
    Button blueTorsoCutButton;
    Button blueTorsoThrustButton;
    Button blueLimbCutButton;
    Button blueLimbThrustButton;

    ScrollView scrollView;


    //end fight popup elements
    Button closeButton;
    Button fbShareButton;
    Button photoButton;
    ProgressBar timeBar;
    TextView timerLabel;
    String localDateString;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        //Get local date from device
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        localDateString = dateFormat.format(calendar.getTime());

        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();

        blueUserId = getIntent().getExtras().getString("blueUID");

        systemArray = new ArrayList<String>();
        systemArray.add("Select Weapon");
        systemArray.add("Longsword");
        systemArray.add("Rapier");
        systemArray.add("Sabre");
        systemArray.add("Dussack");

        redNickname = findViewById(R.id.redNickname);
        blueNickname = findViewById(R.id.blueNickname);
        redAvatar = findViewById(R.id.redAvatar);
        blueAvatar = findViewById(R.id.blueAvatar);

        redHeadCutButton = findViewById(R.id.redHeadCutButton);
        redHeadThrustButton = findViewById(R.id.redHeadThrustButton);
        redTorsoCutButton = findViewById(R.id.redTorsoCutButton);
        redTorsoThrustButton = findViewById(R.id.redTorsoThrustButton);
        redLimbCutButton = findViewById(R.id.redLimbCutButton);
        redLimbThrustButton = findViewById(R.id.redLimbThrustButton);
        blueHeadCutButton = findViewById(R.id.blueHeadCutButton);
        blueHeadThrustButton = findViewById(R.id.blueHeadThrustButton);
        blueTorsoCutButton = findViewById(R.id.blueTorsoCutButton);
        blueTorsoThrustButton = findViewById(R.id.blueTorsoThrustButton);
        blueLimbCutButton = findViewById(R.id.blueLimbCutButton);
        blueLimbThrustButton = findViewById(R.id.blueLimbThrustButton);

        redTotalScore = findViewById(R.id.redTotalScore);
        blueTotalScore = findViewById(R.id.blueTotalScore);

        cancelFightButton = findViewById(R.id.cancelFightButton);
        pauseFightButton = findViewById(R.id.pauseFightButton);
        timerLabel = findViewById(R.id.startTimerLabel);
        redHeadCutButton.setOnClickListener(this);
        redHeadThrustButton.setOnClickListener(this);
        redTorsoCutButton.setOnClickListener(this);
        redTorsoThrustButton.setOnClickListener(this);
        redLimbCutButton.setOnClickListener(this);
        redLimbThrustButton.setOnClickListener(this);
        blueHeadCutButton.setOnClickListener(this);
        blueHeadThrustButton.setOnClickListener(this);
        blueTorsoCutButton.setOnClickListener(this);
        blueTorsoThrustButton.setOnClickListener(this);
        blueLimbCutButton.setOnClickListener(this);
        blueLimbThrustButton.setOnClickListener(this);
        cancelFightButton.setOnClickListener(this);
        pauseFightButton.setOnClickListener(this);

        scrollView = findViewById(R.id.scrollView);

        fight = new Fight();
        populateFighters();

        timeBar = findViewById(R.id.timeBar);
        timeBar.setMax(fightLength);
        timeBar.setProgress(fightLength, true);
        createTimer(fightLength);

        CountDownTimer popupTimer = new CountDownTimer(5 * 100 + 100, 100)
        {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                showStartFightPopup(redHeadCutButton);
            }
        };

        popupTimer.start();
    }

    public void onStart() {
        super.onStart();
            }

    public void createTimer(int seconds) {
        timer = new CountDownTimer(seconds * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int hours = seconds / (60 * 60);
                int tempMin = (seconds - (hours * 60 * 60));
                int minutes = tempMin / 60;
                seconds = tempMin - (minutes * 60);
                timeRemaining = (timeRemaining -1);
                timerLabel.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    timeBar.setProgress(timeRemaining, true);
                }
            }
            @Override
            public void onFinish() {
                timerLabel.setText("--:--");
                showEndFightPopup(redTorsoCutButton);
            }
        };

    }

    public void startTimer() {
        timer.start();
    }

    //to finish
    public void pauseTimer()
    {
        if(pauseState)
        {
            timer.cancel();
            createTimer(timeRemaining);
            pauseState = false;
            return;
        }
    }

    public void cancelTimer() {
        if (timer != null)
        {
            timer.cancel();
            timerLabel.setText("--:--");
            showEndFightPopup(redHeadCutButton);
        }

    }

    public void showStartFightPopup(View view) {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        startFightPopup = inflater.inflate(R.layout.popup_start_fight, null);
        startFightPopup.setElevation(3);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = false;
        startPopupWindow = new PopupWindow(startFightPopup, width, height, focusable);
        //startPopupWindow.setAnimationStyle(R.style.popup_window_fade);
        //show the popup window
        startPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);



        startRedNickNameLabel = startFightPopup.findViewById(R.id.startRedNickNameLabel);
        startBlueNickNameLabel = startFightPopup.findViewById(R.id.startBlueNickNameLabel);

        redSystemSelector = startFightPopup.findViewById(R.id.redSystemSelector);
        blueSystemSelector = startFightPopup.findViewById(R.id.blueSystemSelector);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, systemArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        redSystemSelector.setAdapter(spinnerAdapter);
        blueSystemSelector.setAdapter(spinnerAdapter);
        redSystemSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                setRedFightSystem();
                redHeadCutValueLabel.setText("" + fight.redHeadCutValue);
                redHeadThrustValueLabel.setText("" + fight.redHeadThrustValue);
                redTorsoCutValueLabel.setText("" + fight.redTorsoCutValue);
                redTorsoThrustValueLabel.setText("" + fight.redTorsoThrustValue);
                redLimbCutValueLabel.setText("" + fight.redLimbCutValue);
                redLimbThrustValueLabel.setText("" + fight.redLimbThrustValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        blueSystemSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                setBlueFightSystem();
                blueHeadCutValueLabel.setText("" + fight.blueHeadCutValue);
                blueHeadThrustValueLabel.setText("" + fight.blueHeadThrustValue);
                blueTorsoCutValueLabel.setText("" + fight.blueTorsoCutValue);
                blueTorsoThrustValueLabel.setText("" + fight.blueTorsoThrustValue);
                blueLimbCutValueLabel.setText("" + fight.blueLimbCutValue);
                blueLimbThrustValueLabel.setText("" + fight.blueLimbThrustValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startRedAvatar = startFightPopup.findViewById(R.id.startRedAvatar);
        startBlueAvatar = startFightPopup.findViewById(R.id.startBlueAvatar);
        redBanner = startFightPopup.findViewById(R.id.redBanner);
        blueBanner = startFightPopup.findViewById(R.id.blueBanner);

        beginFightButton = startFightPopup.findViewById(R.id.beginFightButton);
        beginFightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String redSelection = redSystemSelector.getSelectedItem().toString();
                String blueSelection = blueSystemSelector.getSelectedItem().toString();

                if(redSelection.equals("Select Weapon") || blueSelection.equals("Select Weapon"))
                {
                    Toast.makeText(getApplicationContext(), "Please select weapon choices", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    setRedFightSystem();
                    setBlueFightSystem();
                    startTimer();
                    startPopupWindow.dismiss();
                    pauseFightButton.setEnabled(true);
                    cancelFightButton.setEnabled(true);
                }
            }
        });

        startRedNickNameLabel.setText(redFighter.getNickname());
        startBlueNickNameLabel.setText(blueFighter.getNickname());

        redHeadCutValueLabel = startFightPopup.findViewById(R.id.redHeadCutValueLabel);
        redHeadThrustValueLabel = startFightPopup.findViewById(R.id.redHeadThrustValueLabel);
        redTorsoCutValueLabel = startFightPopup.findViewById(R.id.redTorsoCutValueLabel);
        redTorsoThrustValueLabel = startFightPopup.findViewById(R.id.redTorsoThrustValueLabel);
        redLimbThrustValueLabel = startFightPopup.findViewById(R.id.redLimbThrustValueLabel);
        redLimbCutValueLabel = startFightPopup.findViewById(R.id.redLimbCutValueLabel);
        blueHeadCutValueLabel = startFightPopup.findViewById(R.id.blueHeadCutValueLabel);
        blueHeadThrustValueLabel = startFightPopup.findViewById(R.id.blueHeadThrustValueLabel);
        blueTorsoCutValueLabel = startFightPopup.findViewById(R.id.blueTorsoCutValueLabel);
        blueTorsoThrustValueLabel = startFightPopup.findViewById(R.id.blueTorsoThrustValueLabel);
        blueLimbThrustValueLabel = startFightPopup.findViewById(R.id.blueLimbThrustValueLabel);
        blueLimbCutValueLabel = startFightPopup.findViewById(R.id.blueLimbCutValueLabel);

        redAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(redFighter.getUserID()));
        startRedAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(redFighter.getUserID()));
        blueAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(redFighter.getUserID()));
        startBlueAvatar.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(blueFighter.getUserID()));

        //set red popup avatar as per profile
        //String redAvatarId = redFighter.getAvatarImageId();
        //Context contextRed = startRedAvatar.getContext();
        //int idRed = contextRed.getResources().getIdentifier(redAvatarId, "drawable", contextRed.getPackageName());
        //redAvatar.setImageResource(idRed);
        //startRedAvatar.setImageResource(idRed);

        //set red fight screen Avatar
        //Context contextRedFight = redAvatar.getContext();
        //int idRedFight = contextRedFight.getResources().getIdentifier(redAvatarId, "drawable", contextRed.getPackageName());
        //redAvatar.setImageResource(idRedFight);

        //set blue popup avatar as per profile
        //String blueAvatarId = blueFighter.getAvatarImageId();
        //Context contextBlue = startBlueAvatar.getContext();
        //int idBlue = contextBlue.getResources().getIdentifier(blueAvatarId, "drawable", contextBlue.getPackageName());
        //blueAvatar.setImageResource(idBlue);
        //startBlueAvatar.setImageResource(idBlue);

        //set blue fight screen Avatar
        //Context contextBlueFight = redAvatar.getContext();
        //int idBlueFight = contextBlueFight.getResources().getIdentifier(blueAvatarId, "drawable", contextBlue.getPackageName());
        //blueAvatar.setImageResource(idBlueFight);

        pauseFightButton.setEnabled(false);
        cancelFightButton.setEnabled(false);
    }

    public void populateFighters() {

        //get red fighter from current user query
        database = FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());
        database.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        redFighter = dataSnapshot.getValue(User.class);
                        redNickname.setText(redFighter.getNickname() + "");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FightActivity.this, "Failed to load red fighter", Toast.LENGTH_SHORT).show();
                    }
                }

        );
        //get BLUE fighter
        database = FirebaseDatabase.getInstance().getReference("Users").child(blueUserId);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                blueFighter = user;
                blueNickname.setText(blueFighter.getNickname() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /**
        database = FirebaseDatabase.getInstance().getReference("Users");
        database.orderByChild("email").equalTo(blueUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                blueFighter = user;
                blueNodeKey = dataSnapshot.getKey();
                blueNickname.setText(blueFighter.getNickname() + "");
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
         **/
   }

    public void showEndFightPopup(View view) {
        //inflate the layout of the popup windows
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        endFightPopup = inflater.inflate(R.layout.popup_end_fight, null);
        endFightPopup.setElevation(3);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        //HOW TO FIX EDIT TEXT VIEW FOCUS WHEN POPUPVIEW NOT FOCUSABLE
        //https://stackoverflow.com/questions/50636752/how-to-disable-back-button-when-popup-window-dialog-is-showing
        //https://stackoverflow.com/questions/39595131/keep-showing-popupwindow-when-back-button-of-keyboard-is-pressed

        endPopupWindow = new PopupWindow(endFightPopup, width, height, focusable);
        //show the popup window
        endPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        endPopupWindow.isOutsideTouchable();

        //popup window fields
        TextView winnerName = endFightPopup.findViewById(R.id.winnerNickName);
        TextView loserName = endFightPopup.findViewById(R.id.loserNickNameLabel);
        TextView winnerSystem = endFightPopup.findViewById(R.id.winnerSystemLabel);
        TextView loserSystem = endFightPopup.findViewById(R.id.loserSystemLabel);
        TextView winnerTotalPoints = endFightPopup.findViewById(R.id.winner_points_label);
        TextView loserTotalPoints = endFightPopup.findViewById(R.id.loser_points_label);
        TextView winnerLabel = endFightPopup.findViewById(R.id.winner_label);
        TextView loserLabel = endFightPopup.findViewById(R.id.loser_label);
        ImageView winnerAvatar = endFightPopup.findViewById(R.id.winnerAvatar);
        ImageView loserAvatar = endFightPopup.findViewById(R.id.loserAvatar);

        TextView winnerHeadCutsEnd = endFightPopup.findViewById(R.id.winner_head_cuts);
        TextView winnerHeadThrustsEnd = endFightPopup.findViewById(R.id.winner_head_thrusts);
        TextView winnerTorsoCutsEnd = endFightPopup.findViewById(R.id.winner_torso_cuts);
        TextView winnerTorsoThrustsEnd = endFightPopup.findViewById(R.id.winner_torso_thrusts);
        TextView winnerLimbCutsEnd = endFightPopup.findViewById(R.id.winner_limb_cuts);
        TextView winnerLimbThrustsEnd = endFightPopup.findViewById(R.id.winner_limb_thrusts);
        TextView loserHeadCutsEnd = endFightPopup.findViewById(R.id.loser_head_cuts);
        TextView loserHeadThrustsEnd = endFightPopup.findViewById(R.id.loser_head_thrusts);
        TextView loserTorsoCutsEnd = endFightPopup.findViewById(R.id.loser_torso_cuts);
        TextView loserTorsoThrustsEnd = endFightPopup.findViewById(R.id.loser_torso_thrusts);
        TextView loserLimbCutsEnd = endFightPopup.findViewById(R.id.loser_limb_cuts);
        TextView loserLimbThrustsEnd = endFightPopup.findViewById(R.id.loser_limb_thrusts);

        GridView awardsGrid = endFightPopup.findViewById(R.id.awardsGrid);
        final AwardAdapter awardAdapter = new AwardAdapter(this, awardsArray);
        awardsGrid.setAdapter(awardAdapter);
        awardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(FightActivity.this, "" + awardAdapter.getItem(position).getCaption(), Toast.LENGTH_SHORT).show();
                //what to do when award is clicked
            }
        });

        closeButton = endFightPopup.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(this);


        fbShareButton = endFightPopup.findViewById(R.id.fbShare);
        fbShareButton.setOnClickListener(this);

        photoButton = endFightPopup.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(this);

        pauseFightButton.setEnabled(false);
        cancelFightButton.setEnabled(false);

        fight.setRedTotalScore();
        fight.setBlueTotalScore();
        updateUserStats();
        //determineAwards();

        String blueAvatarId = blueFighter.getAvatarImageId();
        String redAvatarId = redFighter.getAvatarImageId();
        Context contextWinner = winnerAvatar.getContext();
        Context contextLoser = loserAvatar.getContext();

        int idRed = contextWinner.getResources().getIdentifier(redAvatarId, "drawable", contextWinner.getPackageName());
        int idBlue = contextLoser.getResources().getIdentifier(blueAvatarId, "drawable", contextLoser   .getPackageName());
        winnerAvatar.setImageResource(idRed);
        loserAvatar.setImageResource(idBlue);

        if(fight.redTotalScore > fight.blueTotalScore)
        {
            //IF RED FIGHTER IS THE WINNER
            //winner = redFighter;
            winnerAvatar.setImageResource(idRed);
            loserAvatar.setImageResource(idBlue);

            resultString = (redFighter.getNickname() + " defeated " + blueFighter.getNickname() + " by " + (fight.redTotalScore - fight.blueTotalScore) + "pts - referee: ");


            fight.setDraw("NO");
            fight.winner = redFighter;
            fight.loser = blueFighter;
            fight.setWinnerFightSystem(fight.redFightSystem);
            fight.setLoserFightSystem(fight.blueFightSystem);
            fight.setWinnerTotalScore(fight.redTotalScore);
            fight.setLoserTotalScore(fight.blueTotalScore);
            fight.setWinnerNickname(redFighter.getNickname());
            fight.setWinnerID(redFighter.getUserID());
            fight.setLoserNickname(blueFighter.getNickname());
            fight.setLoserID(blueFighter.getUserID());

            fight.setWinnerHeadCutsScored(fight.redHeadCutsDelivered);
            fight.setWinnerHeadThrustsScored(fight.redHeadThrustsDelivered);
            fight.setWinnerTorsoCutsScored(fight.redTorsoCutsDelivered);
            fight.setWinnerTorsoThrustsScored(fight.redTorsoThrustsDelivered);
            fight.setWinnerLimbCutsScored(fight.redLimbCutsDelivered);
            fight.setWinnerLimbThrustsScored(fight.redLimbThrustsDelivered);

            fight.setLoserHeadCutsScored(fight.blueHeadCutsDelivered);
            fight.setLoserHeadThrustsScored(fight.blueHeadThrustsDelivered);
            fight.setLoserTorsoCutsScored(fight.blueTorsoCutsDelivered);
            fight.setLoserTorsoThrustsScored(fight.blueTorsoThrustsDelivered);
            fight.setLoserLimbCutsScored(fight.blueLimbCutsDelivered);
            fight.setLoserLimbThrustsScored(fight.blueLimbThrustsDelivered);

            redFighter.addWin();
            blueFighter.addLoss();
            redFighter.setLastWin(blueFighter.getNickname());
            blueFighter.setLastLoss(redFighter.getNickname());

            //awardXP(redFighter.getRank(), blueFighter.getRank());
            //redFighter.setxp(xpForWin * xpModifier);
            //blueFighter.setxp(xpForLoss * xpModifier);
            //redFighter.calculateLevel();
            //blueFighter.calculateLevel();

            winnerAvatar.setBackgroundColor(getColor(R.color.holo_red_light));
            winnerName.setText("" + redFighter.getNickname());
            winnerTotalPoints.setText("" + fight.redTotalScore + " pts");
            winnerSystem.setText("" + fight.redFightSystem);
            winnerName.setTypeface(null, Typeface.BOLD);
            winnerTotalPoints.setTypeface(null, Typeface.BOLD);

            winnerHeadCutsEnd.setText("" + fight.redHeadCutsDelivered);
            winnerHeadThrustsEnd.setText("" + fight.redHeadThrustsDelivered);
            winnerTorsoCutsEnd.setText("" + fight.redTorsoCutsDelivered);
            winnerTorsoThrustsEnd.setText("" + fight.redTorsoThrustsDelivered);
            winnerLimbCutsEnd.setText("" + fight.redLimbCutsDelivered);
            winnerLimbThrustsEnd.setText("" + fight.redLimbThrustsDelivered);

            loserAvatar.setBackgroundColor(getColor(R.color.holo_blue_light));
            loserName.setText("" + blueFighter.getNickname());
            loserTotalPoints.setText("" + fight.blueTotalScore + " pts");
            loserSystem.setText("" + fight.blueFightSystem);

            loserHeadCutsEnd.setText("" + fight.blueHeadCutsDelivered);
            loserHeadThrustsEnd.setText("" + fight.blueHeadThrustsDelivered);
            loserTorsoCutsEnd.setText("" + fight.blueTorsoCutsDelivered);
            loserTorsoThrustsEnd.setText("" + fight.blueTorsoThrustsDelivered);
            loserLimbCutsEnd.setText("" + fight.blueLimbCutsDelivered);
            loserLimbThrustsEnd.setText("" + fight.blueLimbThrustsDelivered);
        }
        else
        if(fight.redTotalScore == fight.blueTotalScore)
        {
            //IF BOTH FIGHTERS ARE A DRAW
            winnerAvatar.setImageResource(idRed);
            loserAvatar.setImageResource(idBlue);

            resultString = (redFighter.getNickname() + " drew with " + blueFighter.getNickname() + " - referee: ");


            fight.setDraw("YES");
            fight.winner = redFighter;
            fight.loser = blueFighter;
            fight.setWinnerFightSystem(fight.redFightSystem);
            fight.setLoserFightSystem(fight.blueFightSystem);
            fight.setWinnerTotalScore(fight.redTotalScore);
            fight.setLoserTotalScore(fight.blueTotalScore);
            fight.setWinnerNickname(redFighter.getNickname());
            fight.setWinnerID(redFighter.getUserID());
            fight.setLoserNickname(blueFighter.getNickname());
            fight.setLoserID(blueFighter.getUserID());

            fight.setWinnerHeadCutsScored(fight.redHeadCutsDelivered);
            fight.setWinnerHeadThrustsScored(fight.redHeadThrustsDelivered);
            fight.setWinnerTorsoCutsScored(fight.redTorsoCutsDelivered);
            fight.setWinnerTorsoThrustsScored(fight.redTorsoThrustsDelivered);
            fight.setWinnerLimbCutsScored(fight.redLimbCutsDelivered);
            fight.setWinnerLimbThrustsScored(fight.redLimbThrustsDelivered);

            fight.setLoserHeadCutsScored(fight.blueHeadCutsDelivered);
            fight.setLoserHeadThrustsScored(fight.blueHeadThrustsDelivered);
            fight.setLoserTorsoCutsScored(fight.blueTorsoCutsDelivered);
            fight.setLoserTorsoThrustsScored(fight.blueTorsoThrustsDelivered);
            fight.setLoserLimbCutsScored(fight.blueLimbCutsDelivered);
            fight.setLoserLimbThrustsScored(fight.blueLimbThrustsDelivered);

            redFighter.addLoss();
            blueFighter.addLoss();
            redFighter.setLastLoss(blueFighter.getNickname());
            blueFighter.setLastLoss(redFighter.getNickname());
            
            //awardXP(redFighter.getRank(), blueFighter.getRank());
            //awardXP(blueFighter.getRank(), redFighter.getRank());
            //redFighter.setxp(xpForLoss * xpModifier);
            //blueFighter.setxp(xpForLoss * xpModifier);

            winnerAvatar.setBackgroundColor(getColor(R.color.holo_red_light));
            winnerLabel.setText("DRAW");
            winnerName.setText("" + redFighter.getNickname());
            winnerTotalPoints.setText("" + fight.redTotalScore + " pts");
            winnerSystem.setText("" + fight.redFightSystem);
            winnerName.setTypeface(null, Typeface.BOLD);
            winnerTotalPoints.setTypeface(null, Typeface.BOLD);

            winnerHeadCutsEnd.setText("" + fight.redHeadCutsDelivered);
            winnerHeadThrustsEnd.setText("" + fight.redHeadThrustsDelivered);
            winnerTorsoCutsEnd.setText("" + fight.redTorsoCutsDelivered);
            winnerTorsoThrustsEnd.setText("" + fight.redTorsoThrustsDelivered);
            winnerLimbCutsEnd.setText("" + fight.redLimbCutsDelivered);
            winnerLimbThrustsEnd.setText("" + fight.redLimbThrustsDelivered);

            loserAvatar.setBackgroundColor(getColor(R.color.light_blue));
            loserLabel.setText("DRAW");
            loserName.setText("" + blueFighter.getNickname());
            loserTotalPoints.setText("" + fight.blueTotalScore + " pts");
            loserSystem.setText("" + fight.blueFightSystem);
            loserName.setTypeface(null, Typeface.BOLD);
            loserTotalPoints.setTypeface(null, Typeface.BOLD);

            loserHeadCutsEnd.setText("" + fight.blueHeadCutsDelivered);
            loserHeadThrustsEnd.setText("" + fight.blueHeadThrustsDelivered);
            loserTorsoCutsEnd.setText("" + fight.blueTorsoCutsDelivered);
            loserTorsoThrustsEnd.setText("" + fight.blueTorsoThrustsDelivered);
            loserLimbCutsEnd.setText("" + fight.blueLimbCutsDelivered);
            loserLimbThrustsEnd.setText("" + fight.blueLimbThrustsDelivered);
        }
        else
        if(fight.redTotalScore < fight.blueTotalScore)
        {
            //IF BLUE FIGHTER IS THE WINNER
            //loser = blueFighter;
            winnerAvatar.setImageResource(idBlue);
            loserAvatar.setImageResource(idRed);

            resultString = (blueFighter.getNickname() + " defeated " + redFighter.getNickname() + " by " + (fight.blueTotalScore - fight.redTotalScore) + "pts - referee: ");

            fight.setDraw("NO");
            fight.winner = blueFighter;
            fight.loser = redFighter;
            fight.setWinnerFightSystem(fight.blueFightSystem);
            fight.setLoserFightSystem(fight.redFightSystem);
            fight.setWinnerTotalScore(fight.blueTotalScore);
            fight.setLoserTotalScore(fight.redTotalScore);
            fight.setWinnerNickname(blueFighter.getNickname());
            fight.setWinnerID(blueFighter.getUserID());
            fight.setLoserNickname(redFighter.getNickname());
            fight.setLoserID(redFighter.getUserID());

            fight.setWinnerHeadCutsScored(fight.blueHeadCutsDelivered);
            fight.setWinnerHeadThrustsScored(fight.blueHeadThrustsDelivered);
            fight.setWinnerTorsoCutsScored(fight.blueTorsoCutsDelivered);
            fight.setWinnerTorsoThrustsScored(fight.blueTorsoThrustsDelivered);
            fight.setWinnerLimbCutsScored(fight.blueLimbCutsDelivered);
            fight.setWinnerLimbThrustsScored(fight.blueLimbThrustsDelivered);

            fight.setLoserHeadCutsScored(fight.redHeadCutsDelivered);
            fight.setLoserHeadThrustsScored(fight.redHeadThrustsDelivered);
            fight.setLoserTorsoCutsScored(fight.redTorsoCutsDelivered);
            fight.setLoserTorsoThrustsScored(fight.redTorsoThrustsDelivered);
            fight.setLoserLimbCutsScored(fight.redLimbCutsDelivered);
            fight.setLoserLimbThrustsScored(fight.redLimbThrustsDelivered);

            blueFighter.addWin();
            redFighter.addLoss();
            blueFighter.setLastWin(redFighter.getNickname());
            redFighter.setLastLoss(blueFighter.getNickname());
            
            //awardXP(blueFighter.getRank(), redFighter.getRank());
            //redFighter.setxp(xpForLoss * xpModifier);
            //blueFighter.setxp(xpForWin * xpModifier);

            winnerAvatar.setBackgroundColor(getColor(R.color.light_blue));
            winnerName.setText("" + blueFighter.getNickname());
            winnerTotalPoints.setText("" + fight.blueTotalScore + " pts");
            winnerSystem.setText("" + fight.blueFightSystem);
            winnerName.setTypeface(null, Typeface.BOLD);
            winnerTotalPoints.setTypeface(null, Typeface.BOLD);

            winnerHeadCutsEnd.setText("" + fight.blueHeadCutsDelivered);
            winnerHeadThrustsEnd.setText("" + fight.blueHeadThrustsDelivered);
            winnerTorsoCutsEnd.setText("" + fight.blueTorsoCutsDelivered);
            winnerTorsoThrustsEnd.setText("" + fight.blueTorsoThrustsDelivered);
            winnerLimbCutsEnd.setText("" + fight.blueLimbCutsDelivered);
            winnerLimbThrustsEnd.setText("" + fight.blueLimbThrustsDelivered);

            loserAvatar.setBackgroundColor(getColor(R.color.holo_red_light));
            loserName.setText("" + redFighter.getNickname());
            loserTotalPoints.setText("" + fight.redTotalScore + " pts");
            loserSystem.setText("" + fight.redFightSystem);

            loserHeadCutsEnd.setText("" + fight.redHeadCutsDelivered);
            loserHeadThrustsEnd.setText("" + fight.redHeadThrustsDelivered);
            loserTorsoCutsEnd.setText("" + fight.redTorsoCutsDelivered);
            loserTorsoThrustsEnd.setText("" + fight.redTorsoThrustsDelivered);
            loserLimbCutsEnd.setText("" + fight.redLimbCutsDelivered);
            loserLimbThrustsEnd.setText("" + fight.redLimbThrustsDelivered);
        }
    }

    public void shareResultsToFacebook()
    {
        /**
        //create a view of the curren app screen
        View resultsScreen = endFightPopup;
        Bitmap bitmap = Bitmap.createBitmap(resultsScreen.getWidth(), resultsScreen.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        resultsScreen.draw(canvas);

        //########################## FACEBOOK SHARING ###########################
        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#Federsports")
                        .build())
                .build();
        shareResultsDialog = new ShareDialog(this);
        shareResultsDialog.show(content);
         **/
    }


    public void updateUserStats()
    {
        redFighter.setHeadCutsGiven(fight.redHeadCutsDelivered);
        redFighter.setHeadThrustsGiven(fight.redHeadThrustsDelivered);
        redFighter.setTorsoCutsGiven(fight.redTorsoCutsDelivered);
        redFighter.setTorsoThrustsGiven(fight.redTorsoThrustsDelivered);
        redFighter.setLimbCutsGiven(fight.redLimbCutsDelivered);
        redFighter.setLimbThrustsGiven(fight.redLimbThrustsDelivered);

        blueFighter.setHeadCutsGiven(fight.blueHeadCutsDelivered);
        blueFighter.setHeadThrustsGiven(fight.blueHeadThrustsDelivered);
        blueFighter.setTorsoCutsGiven(fight.blueTorsoCutsDelivered);
        blueFighter.setTorsoThrustsGiven(fight.blueTorsoThrustsDelivered);
        blueFighter.setLimbCutsGiven(fight.blueLimbCutsDelivered);
        blueFighter.setLimbThrustsGiven(fight.blueLimbThrustsDelivered);

        redFighter.setHeadCutsReceived(fight.blueHeadCutsDelivered);
        redFighter.setHeadThrustsReceived(fight.blueHeadThrustsDelivered);
        redFighter.setTorsoCutsReceived(fight.blueTorsoCutsDelivered);
        redFighter.setTorsoThrustsReceived(fight.blueTorsoThrustsDelivered);
        redFighter.setLimbCutsReceived(fight.blueLimbCutsDelivered);
        redFighter.setLimbThrustsReceived(fight.blueLimbThrustsDelivered);

        blueFighter.setHeadCutsReceived(fight.redHeadCutsDelivered);
        blueFighter.setHeadThrustsReceived(fight.redHeadThrustsDelivered);
        blueFighter.setTorsoCutsReceived(fight.redTorsoCutsDelivered);
        blueFighter.setTorsoThrustsReceived(fight.redTorsoThrustsDelivered);
        blueFighter.setLimbCutsReceived(fight.redLimbCutsDelivered);
        blueFighter.setLimbThrustsReceived(fight.redLimbThrustsDelivered);
    }

    /**
    public void determineAwards()
    {
//################################ HEADSHOT AWARD ####################################
        if((fight.redHeadCutsDelivered()+fight.hedHeadThrustsDelivered()) > 4)
        {
            //award head hunter award to Red
            Toast.makeText(FightActivity.this, "RED earned Headhunter", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Headhunter", "red"));
            redFighter.addHeadHunterAward();
        }

        if((fight.getBlueHeadCutsDelivered()+fight.getBlueHeadThrustsDelivered()) > 4)
        {
            Toast.makeText(FightActivity.this, "BLUE earned Headhunter", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Headhunter", "blue"));
            blueFighter.addHeadHunterAward();
        }
//################################ TORSOSHOT AWARD ####################################
        if((fight.getRedTorsoCutsDelivered()+fight.getRedTorsoThrustsDelivered()) > 4)
        {
            Toast.makeText(FightActivity.this, "RED earned Bodybreaker", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Bodybreaker", "red"));
            redFighter.addBodyBreakerAward();

        }

        if((fight.getBlueTorsoCutsDelivered()+fight.getBlueTorsoThrustsDelivered()) > 4)
        {
            Toast.makeText(FightActivity.this, "BLUE earned Bodybreaker", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Bodybreaker", "blue"));
            blueFighter.addBodyBreakerAward();
        }
//################################ LIMBSHOT AWARD ####################################
        if((fight.getRedLimbCutsDelivered()+fight.getRedLimbThrustsDelivered()) > 4)
        {
            Toast.makeText(FightActivity.this, "RED earned Limbtaker", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Limbtaker", "red"));
            redFighter.addLimbTakerAward();
        }

        if((fight.getBlueLimbCutsDelivered()+fight.getBlueLimbThrustsDelivered()) > 4)
        {
            Toast.makeText(FightActivity.this, "BLUE earned Limbtaker", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Limbtaker", "blue"));
            blueFighter.addLimbTakerAward();
        }
//################################ THRUSTMASTER AWARD ####################################
        if(fight.getRedTotalThrustsDelivered() > 4 & fight.getRedTotalCutsDelivered() == 0)
        {
            Toast.makeText(FightActivity.this, "RED earned Thrustmaster", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Thrustmaster","red"));
            redFighter.addThrustmasterAward();
        }

        if((fight.getBlueTotalThrustsDelivered() > 4 & fight.getBlueTotalCutsDelivered() == 0))
        {
            Toast.makeText(FightActivity.this, "BLUE earned Thrustmaster", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Thrustmaster", "blue"));
            blueFighter.addThrustmasterAward();
        }
//################################ BUTCHER AWARD ####################################
        if((fight.getRedTotalThrustsDelivered() == 0 & fight.getRedTotalCutsDelivered() > 4))
        {
            Toast.makeText(FightActivity.this, "RED earned Butcher", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Butcher", "red"));
            redFighter.addButcherAward();
        }

        if(fight.getBlueTotalThrustsDelivered() == 0 & fight.getBlueTotalCutsDelivered() > 4)
        {
            Toast.makeText(FightActivity.this, "BLUE earned Butcher", Toast.LENGTH_SHORT).show();
            awardsArray.add(new Award("Butcher", "blue"));
            blueFighter.addButcherAward();
        }
    }
     **/

    public void submitResults()
    {
        //point to Firebase "users" location
        database = FirebaseDatabase.getInstance().getReference("Users");
        //overwrite red fighter node in Firebase
        database.child(auth.getCurrentUser().getUid()).setValue(redFighter);
        //overwrite blue fighter node in Firebase
        database.child(blueUserId).setValue(blueFighter);
        /**
        CountDownTimer closeTimer = new CountDownTimer(10 * 1000 + 1000, 1000)
        {
            int timeToClose = 3;
            @Override
            public void onTick(long millisUntilFinished)
            {
                closeButton.setText("CLOSING IN..." + timeToClose);
                timeToClose = timeToClose - 1;
            }
            @Override
            public void onFinish()
            {
            finish();
            }
        };
        closeTimer.start();
        **/
    }

    public void updateChallengeTokens()
    {
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(blueFighter.getUserID()).child("ReceivedChallenges").child(redFighter.getUserID());
        DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(redFighter.getUserID()).child("IssuedChallenges").child(blueFighter.getUserID());
        receiverRef.child("tokenStatus").setValue("COMPLETED");
        issuerRef.child("tokenStatus").setValue("COMPLETED");
    }

    public void writeEvent(boolean withPhoto)
    {
        Date currentTime;
        SimpleDateFormat formatter;
        currentTime = new Date(System.currentTimeMillis());
        formatter = new SimpleDateFormat("dd-MM-YY H:mm");

        Event event = new Event();

        event.setCreatedById(MainActivity.currentUser.getUserID());
        event.setCreatedByNickname(MainActivity.currentUser.getNickname());
        event.setDateCreated(formatter.format(currentTime));

        event.setUserBID(blueFighter.getUserID());
        event.setUserBNickName(blueFighter.getNickname());

        if(fight.winner.getUserID().equals(MainActivity.currentUser.getUserID()))
        {
            //if the winners is the current user
            event.setEventText("has won a challenge with");;
        }
        else
        {
            //if the winner is not the current user
            event.setEventText("has lost a challenge with");
        }

        if(fight.getDraw().equals("YES"))
        {
            event.setEventText("has drawn in a challenge with");
        }

        database = FirebaseDatabase.getInstance().getReference();

        //allocate node ID to be used to link comments with the event
        String nodeID = database.child("Comments").push().getKey();
        event.setnodeID(nodeID);

        if(withPhoto==true)
        {
            checkOrientationAndUpload(photoFile, event.nodeID);
            event.setPhotoPath(photoFileName);
        }

        //create events in database for each user
        database.child("Events").child(redFighter.getUserID()).child(nodeID).setValue(event); //red fighter is the logged in user
        database.child("Events").child(blueFighter.getUserID()).child(nodeID).setValue(event);

        writeFight(fight, nodeID, formatter.format(currentTime));
        sendNotifications(nodeID);

        Toast.makeText(this, "event with photo created", Toast.LENGTH_SHORT).show();
        endPopupWindow.dismiss();
        finish();
    }

    public void writeFight(Fight fight, String nodeID, String date)
    {
        fight.setNodeID(nodeID);
        fight.setDateCreated(date);
        //Fired from withing writeEvent() method
        database = FirebaseDatabase.getInstance() .getReference();
        //Save the fight to both users, using the same nodeID as the event
        database.child("Fights").child(redFighter.getUserID()).child(nodeID).setValue(fight);
        database.child("Fights").child(blueFighter.getUserID()).child(nodeID).setValue(fight);
    }

    public void setRedFightSystem() {
        String system = redSystemSelector.getSelectedItem().toString();
        fight.setRedFightSystem(system);
    }

    public void setBlueFightSystem() {
        String system = blueSystemSelector.getSelectedItem().toString();
        fight.setBlueFightSystem(system);
    }

    double xpModifier = 1;
    public void awardXP(int winnerRank, int loserRank)
    {
        if((winnerRank - loserRank) == -5)
        {
            //if winner is 5 ranks lower
            xpModifier = (xpModifier * 1.5);
            return;
        }
        if((winnerRank - loserRank) == -4)
        {
            //if winner is 4 ranks lower
            xpModifier = (xpModifier * 1.4);
            return;
        }
        if((winnerRank - loserRank) == -3)
        {
            //if winner is 3 ranks lower
            xpModifier = (xpModifier * 1.3);
            return;
        }
        if((winnerRank - loserRank) == -2)
        {
            //if winner is 2 ranks lower
            xpModifier = (xpModifier * 1.2);
            return;
        }
        if((winnerRank - loserRank) == -1)
        {
            //if winner is 1 ranks lower
            xpModifier = (xpModifier * 1.1);
            return;
        }
        if((winnerRank - loserRank) == 0)
        {
            //if winner is same rank
            //FULL XP EARNED
            return;
        }
        if((winnerRank - loserRank) == 1)
        {
            //if winner is 1 rank higher
            xpModifier = (xpModifier * 0.9);
            return;
        }
        if((winnerRank - loserRank) == 2)
        {
            //if winner is 2 ranks higher
            xpModifier = (xpModifier * 0.8);
            return;
        }
        if((winnerRank - loserRank) == 3)
        {
            //if winner is 3 ranks higher
            xpModifier = (xpModifier * 0.7);
            return;
        }
        if((winnerRank - loserRank) == 4)
        {
            //if winner is 3 ranks higher
            xpModifier = (xpModifier * 0.6);
            return;
        }
        if((winnerRank - loserRank) == 5)
        {
            //if winner is 3 ranks higher
            xpModifier = (xpModifier * 0.5);
            return;
        }

    }

    @Override
    public void onBackPressed()
    {

        if(startPopupWindow.isShowing())
        {
            startPopupWindow.dismiss();
            finish();
            return;
        }
        else if(!endFightPopup.isShown())
        {
            showExitPopup(redNickname);
            return;
        }
    }

    public void showExitPopup(View view)
    {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_exit, null);
        popupView.setElevation(3);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        exitPopupWindow = new PopupWindow(popupView, width, height, focusable);
        //show the popup window
        exitPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button yesButton = popupView.findViewById(R.id.yesButton);
        Button noButton = popupView.findViewById(R.id.noButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //yes button clicked - exit fight activity
                timer.cancel();
                exitPopupWindow.dismiss();
                finish();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //no button clicked - dismiss popup
                exitPopupWindow.dismiss();
            }
        });
        }

    public void showPausePopup(View view)
    {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_pause, null);
        popupView.setElevation(3);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        pausePopupWindow = new PopupWindow(popupView, width, height, focusable);
        //show the popup window
        pausePopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button resumeButton = popupView.findViewById(R.id.resumeButton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //resume button clicked - resume timer, dismiss pause popup
                if(!pauseState);
                {
                    timer.start();
                    pauseState = true;
                    pausePopupWindow.dismiss();
                    return;
                }
            }
        });
    }


    public void takePhoto() throws IOException
    {
        Boolean camera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        if(camera = true)
        {
            //do camera function
            //use default android camera app to take a photo
            int captures = 1;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null)
            {
                photoFile = null;
                try
                {
                    photoFile = createImageFile();
                }
                catch (IOException e)
                {
                    //error occured while creating the file
                    e.printStackTrace();
                }
                //continue only if file was  successfully created
                if(photoFile != null)
                {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, captures);
                }
            }
        }
        else
        {
            //no camera hardware
            Toast.makeText(getApplicationContext(), "No camera found", Toast.LENGTH_SHORT).show();
        }
    }

    //fired from within takePhoto() method
    public File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile
                (
                        imageFileName, /* prefix */
                        ".jpg", /* suffix */
                        storageDir
                );

        //save a file: path for use with ACTION_VIEW intents
        photoFileName = imageFileName;
        return imageFile;
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

        uploadPhoto(saveBitmapToFile(file, rotateImage(createBitmap(file), orientationCorrection)), nodeID, metadata);
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

    public File saveBitmapToFile(File file, Bitmap selectedBitmap){
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

    public String uploadPhoto(File bitmapFile, String nodeID, final StorageMetadata metadata)
    {
        //continue to upload file
        File convertedImage = bitmapFile;

        Uri file = Uri.fromFile(convertedImage);

        final String filename = file.getLastPathSegment();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference photoRef = storage.getReference().child("EventImages").child(nodeID).child(filename);
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
        photoFileName = filename;
        return filename;
    }

    public void sendNotifications(String nodeID)
    {
        //send notification to following users

        final Notification notification = new Notification();
        notification.setText("has posted a fight with");
        notification.setNodeID(nodeID);
        notification.setIssuerID(redFighter.getUserID());
        notification.setIssuerNickname(redFighter.getNickname());
        notification.setUserBID(blueFighter.getUserID());
        notification.setUserBNickname(blueFighter.getNickname());
        //notification.setCreatedDate();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notificationsRef = database.getReference("Follows").child(currentFirebaseUser.getUid()).child("Followers");
        notificationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow follow = dataSnapshot.getValue(Follow.class);
                String followingUserID = follow.userID;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference notificationsRef = database.getReference("Notifications").child(followingUserID);
                notificationsRef.push().setValue(notification);
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

    @Override
    public void onClick (View v)
    {
        //switch statement increments score variables and displays accumulated points in the textview
        switch (v.getId())
        {
            case R.id.redHeadCutButton:
                fight.addRedHeadCut();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;

            case R.id.redHeadThrustButton:
                fight.addRedHeadThrust();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;
            case R.id.redTorsoCutButton:
                fight.addRedTorsoCut();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;

            case R.id.redTorsoThrustButton:
                fight.addRedTorsoThrust();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;

            case R.id.redLimbCutButton:
                fight.addRedLimbCut();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;

            case R.id.redLimbThrustButton:
                fight.addRedLimbThrust();
                blueTotalScore.setText(fight.blueTotalScore + "");
                break;

            case R.id.blueHeadCutButton:
                fight.addBlueHeadCut();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.blueHeadThrustButton:
                fight.addBlueHeadThrust();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.blueTorsoCutButton:
                fight.addBlueTorsoCut();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.blueTorsoThrustButton:
                fight.addBlueTorsoThrust();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.blueLimbCutButton:
                fight.addBlueLimbCut();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.blueLimbThrustButton:
                fight.addBlueLimbThrust();
                redTotalScore.setText(fight.redTotalScore + "");
                break;

            case R.id.cancelFightButton:
                //cancelTimer();
                showExitPopup(redNickname);
                break;

            case R.id.pauseFightButton:
                pauseTimer();
                showPausePopup(redNickname);
                break;

            case R.id.closeButton:
                submitResults();
                updateChallengeTokens();

                if(photoFileName == null)
                {
                    //if a photo has not been taken
                    writeEvent(false);
                }
                else
                {
                    //if a photo has been taken
                    writeEvent(true);
                }
                break;

            case R.id.fbShare:
                shareResultsToFacebook();
                break;

            case R.id.photoButton:
                try
                {
                    takePhoto();
                }
                catch(IOException e)
                {
                    Toast.makeText(this, "Photo capture failed", Toast.LENGTH_SHORT);
                }

                break;
        }
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }
}
