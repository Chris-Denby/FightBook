package com.example.fightbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AwardsFragment extends Fragment
{

    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    User currentUser;
    GridView awardsGrid;
    AwardAdapter awardAdapter;
    ArrayList<Award> awardsArray = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();
        awardAdapter = new AwardAdapter(getActivity(), awardsArray);
        populateCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.awards_fragment, container, false);

        //awardsGrid.setAdapter(awardAdapter);
        awardsGrid = v.findViewById(R.id.awardsGrid);
        awardsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //what to do when award is clicked
                Toast.makeText(getActivity(), "" + awardAdapter.getItem(position).getCaption(), Toast.LENGTH_SHORT).show();
            }

        });
        return v;
    }

    public static AwardsFragment newInstance(String text)
    {
        AwardsFragment f = new AwardsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        awardsGrid.setAdapter(awardAdapter);
        populateCurrentUser();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        awardsGrid.setAdapter(awardAdapter);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        awardsGrid.setAdapter(awardAdapter);
    }

    public void populateCurrentUser()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        awardsArray.clear();
                        final User user = dataSnapshot.getValue(User.class);
                        currentUser = user;


                        int headHunterAwards = user.getHeadHunterAwards();
                        if(headHunterAwards > 0)
                        {
                            awardsArray.add(new Award("Headhunter", headHunterAwards));
                        }
                        else
                        {
                            //add greyed out award with zero qty
                            awardsArray.add(new Award("Headhunter", 0, "grey"));
                        }

                        int bodyBreakerAwards = user.getBodyBreakerAwards();
                        if(bodyBreakerAwards > 0)
                        {
                            awardsArray.add(new Award("Bodybreaker", bodyBreakerAwards));
                        }
                        else
                        {
                            //add greyed out award with zero qty
                            awardsArray.add(new Award("Bodybreaker", 0, "grey"));
                        }

                        int limbTakerAwards = user.getLimbTakerAwards();
                        if(limbTakerAwards > 0)
                        {
                            awardsArray.add(new Award("Limbtaker", limbTakerAwards));
                            //Toast.makeText(getActivity(), "Limbtaker" +  limbTakerAwards, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //add greyed out award with zero qty
                            awardsArray.add(new Award("Limbtaker", 0, "grey"));
                        }

                        int thrustmasterAwards = user.getThrustmasterAwards();
                        if(thrustmasterAwards > 0)
                        {
                            awardsArray.add(new Award("Thrustmaster", thrustmasterAwards));
                            //Toast.makeText(getActivity(), "Thrustmaster" +  thrustmasterAwards, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //add greyed out award with zero qty
                            awardsArray.add(new Award("Thrustmaster", 0, "grey"));
                        }

                        int butcherAwards = user.getButcherAwards();
                        if(butcherAwards > 0)
                        {
                            awardsArray.add(new Award("Butcher", butcherAwards));
                            //Toast.makeText(getActivity(), "Butcher" +  butcherAwards, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //add greyed out award with zero qty
                            awardsArray.add(new Award("Butcher", 0, "grey"));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }}
        );
    }
}





//https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts