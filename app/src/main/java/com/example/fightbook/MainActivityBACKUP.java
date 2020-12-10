package com.example.fightbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivityBACKUP extends FragmentActivity implements View.OnClickListener {

    ViewPager pager;
    public static User currentUser;
    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    public static int challengeCount = 0;

    Button profileButton;
    Button homeButton;
    Button challengeButton;
    Button fencersButton;
    Button logoutButton;
    public static TextView notificationBubble;

    View topBar;

    //profile popupWindow elements
    PopupWindow profilePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_slide_page);
        currentFirebaseUser = auth.getCurrentUser();
        populateCurrentUser();

        notificationBubble = findViewById(R.id.notification_bubble);
        pager = (ViewPager) findViewById(R.id.viewPager);
        profileButton = findViewById(R.id.profileButton);
        homeButton = findViewById(R.id.homeButton);
        challengeButton = findViewById(R.id.challengesButton);
        fencersButton = findViewById(R.id.fencersButton);
        logoutButton = findViewById(R.id.logoutButton);
        topBar = findViewById(R.id.topBar);
        profileButton.setOnClickListener(this);
        homeButton.setOnClickListener(this);
        challengeButton.setOnClickListener(this);
        fencersButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);


        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1)
            {
                //highlight top bar button text on swipe to reflect current fragment
                switch(i)
                {
                    case 0://home
                        profileButton.setTextColor(getResources().getColor(R.color.black));
                        homeButton.setTextColor(getResources().getColor(R.color.holo_red_light));
                        challengeButton.setTextColor(getResources().getColor(R.color.black));
                        fencersButton.setTextColor(getResources().getColor(R.color.black));
                        //topBar.setVisibility(View.INVISIBLE);

                    break;

                    case 1://challenges
                        profileButton.setTextColor(getResources().getColor(R.color.black));
                        homeButton.setTextColor(getResources().getColor(R.color.black));
                        challengeButton.setTextColor(getResources().getColor(R.color.holo_red_light));
                        fencersButton.setTextColor(getResources().getColor(R.color.black));
                        //topBar.setVisibility(View.VISIBLE);
                    break;

                    case 2://fencers
                        profileButton.setTextColor(getResources().getColor(R.color.black));
                        homeButton.setTextColor(getResources().getColor(R.color.black));
                        challengeButton.setTextColor(getResources().getColor(R.color.black));
                        fencersButton.setTextColor(getResources().getColor(R.color.holo_red_light));
                        //topBar.setVisibility(View.VISIBLE);
                    break;
                }
            }

            @Override
            public void onPageSelected(int i)
            {
            }

            @Override
            public void onPageScrollStateChanged(int i)
            {
            }
        });
        pager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profileButton:
                Intent intent = new Intent(this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("CURRENT_USER_ID", currentUser.getUserID());
                    bundle.putString("CURRENT_USER_NICKNAME", currentUser.getNickname());
                    bundle.putString("SELECTED_USER_ID", currentUser.getUserID());
                    bundle.putString("SELECTED_USER_NICKNAME", currentUser.getNickname());
                }
                catch(Exception e)
                {

                }
                intent.putExtras(bundle);
                startActivity(intent);
            break;

            case R.id.homeButton:
            pager.setCurrentItem(0);
            break;

            case R.id.challengesButton:
            pager.setCurrentItem(1);
            break;

            case R.id.fencersButton:
            pager.setCurrentItem(2);
            break;

            case R.id.logoutButton:
            logout();
            break;
        }

    }



    public void populateCurrentUser()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid());
        ref.addListenerForSingleValueEvent
                (
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if(!(dataSnapshot == null)) {
                                    currentUser = dataSnapshot.getValue(User.class);
                                    Constants.getInstance().currentUser = currentUser;
                                }
                                else{
                                Toast.makeText(MainActivityBACKUP.this, "no user retrieved", Toast.LENGTH_SHORT).show();}

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }

    public void logout()
    {
        auth.signOut();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        this.finish();
    }

    public MainActivityBACKUP()
    {

        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();
    }

    @Override
    public void onBackPressed()
    {
        if(pager.getCurrentItem() == 2)
        {
            //if roster page is showing


        }
        if(pager.getCurrentItem() <2)
        {
            //if any other page position
            pager.setCurrentItem(0);
        }
        else
        {
            finish();
        }


    }

    private class MyPagerAdapter extends FragmentPagerAdapter
    {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos)
        {
            switch(pos) {
                case 0: return HomeFragment.newInstance("SecondFragment, Instance 1");
                case 1: return NotificationsFragment.newInstance("ForthFragment, Instance 1");
                case 2: return RosterFragment.newInstance("ThirdFragment, instance 1", getApplicationContext());
                default:return HomeFragment.newInstance("SecondFragment, Instance 1");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}

//https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts