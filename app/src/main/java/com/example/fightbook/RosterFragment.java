package com.example.fightbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RosterFragment extends Fragment
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    User currentUser;
    static Context c;

    View filterPopup;
    static PopupWindow filterOptionsPopup;
    ArrayList<String> countryArray = new ArrayList<String>();
    ArrayList<String> australiaArray = new ArrayList<String>();
    ArrayList<String> usaArray = new ArrayList<>();
    ArrayList<String> ukArray = new ArrayList<>();

    ArrayList<String> nullArray = new ArrayList<String>();
    SpinnerAdapter spinnerAdapter;
    Spinner countryFilter;
    Spinner cityFilter;
    EditText filterNameField;

    Button applyFilterButton;
    ListView userList;
    ArrayList<User> allUserArray = new ArrayList<User>();
    UserAdapter userAdapter;
    User selectedUser;
    View view;

    ArrayList<Follow> followedUsersArray = new ArrayList<>();
    ArrayList<Follow> followersArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_roster, container, false);


        userAdapter = new UserAdapter(c, allUserArray, followedUsersArray, followersArray);
        auth = FirebaseAuth.getInstance();
        userList = v.findViewById(R.id.userList);
        userList.setLongClickable(true);
        view = v.findViewById(R.id.til1_sub);
        filterNameField = v.findViewById(R.id.filter_name_field);

        userList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position==0)
                {
                    showFilterPopup(filterNameField);
                }
                else {
                    selectedUser = userAdapter.getItem(position);
                    goToProfileActivity(selectedUser.getUserID(), selectedUser.getNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
                }
            }
        });

        filterNameField.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            populateByNameSearchAll(filterNameField.getText().toString());
                            filterNameField.setText("");
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        nullArray.add("-");

        countryArray.add("Select country");
        countryArray.add("All");
        countryArray.add("Australia");
        countryArray.add("USA");
        countryArray.add("UK");

        australiaArray.add("Select city");
        australiaArray.add("All");
        australiaArray.add("Adelaide");
        australiaArray.add("Brisbane");
        australiaArray.add("Canberra");
        australiaArray.add("Darwin");
        australiaArray.add("Hobart");
        australiaArray.add("Melbourne");
        australiaArray.add("Perth");
        australiaArray.add("Sydney");

        usaArray.add("Select city");
        usaArray.add("All");
        usaArray.add("Alabama");
        usaArray.add("Alaska");
        usaArray.add("Arizona");
        usaArray.add("Arkansas");
        usaArray.add("California");
        usaArray.add("Colorado");
        usaArray.add("Connecticut");
        usaArray.add("Delaware");
        usaArray.add("Florida");
        usaArray.add("Georgia");
        usaArray.add("Hawaii");
        usaArray.add("Idaho");
        usaArray.add("Illinois");
        usaArray.add("Indiana");
        usaArray.add("Iowa");
        usaArray.add("Kansas");
        usaArray.add("Kentucky");
        usaArray.add("Louisiana");
        usaArray.add("Maine");
        usaArray.add("Maryland");
        usaArray.add("Massachusetts");
        usaArray.add("Michigan");
        usaArray.add("Minnesota");
        usaArray.add("Mississippi");
        usaArray.add("Missouri");
        usaArray.add("Montana");
        usaArray.add("Nebraska");
        usaArray.add("Nevada");
        usaArray.add("New Hampshire");
        usaArray.add("New Jersey");
        usaArray.add("New Mexico");
        usaArray.add("New York");
        usaArray.add("North Carolina");
        usaArray.add("North Dakota");
        usaArray.add("Ohio");
        usaArray.add("Oklahoma");
        usaArray.add("Oregon");
        usaArray.add("Pennsylvania");
        usaArray.add("Rhode Island");
        usaArray.add("South Carolina");
        usaArray.add("South Dakota");
        usaArray.add("Tennessee");
        usaArray.add("Texas");
        usaArray.add("Utah");
        usaArray.add("Vermont");
        usaArray.add("Virginia");
        usaArray.add("Washington");
        usaArray.add("West Virginia");
        usaArray.add("Wisconsin");
        usaArray.add("Wyoming");

        ukArray.add("Select city");
        ukArray.add("All");
        ukArray.add("Aberdeen");
        ukArray.add("Armagh");
        ukArray.add("Bangor");
        ukArray.add("Bath");
        ukArray.add("Belfast");
        ukArray.add("Birmingham");
        ukArray.add("Bradford");
        ukArray.add("Brighton & Hove");
        ukArray.add("Bristol");
        ukArray.add("Cambridge");
        ukArray.add("Canterbury");
        ukArray.add("Cardiff");
        ukArray.add("Carlisle");
        ukArray.add("Chelmsford");
        ukArray.add("Chester");
        ukArray.add("Chichester");
        ukArray.add("Coventry");
        ukArray.add("Derby");
        ukArray.add("Derry");
        ukArray.add("Dundee");
        ukArray.add("Durham");
        ukArray.add("Edinburgh");
        ukArray.add("Ely");
        ukArray.add("Exeter");
        ukArray.add("Glasgow");
        ukArray.add("Gloucester");
        ukArray.add("Hereford");
        ukArray.add("Inverness");
        ukArray.add("Kingston upon Hull");
        ukArray.add("Lancaster");
        ukArray.add("Leeds");
        ukArray.add("Leicester");
        ukArray.add("Lichfield");
        ukArray.add("Lincoln");
        ukArray.add("Lisburn");
        ukArray.add("Liverpool");
        ukArray.add("London");
        ukArray.add("Manchester");
        ukArray.add("Newcastle upon Tyne");
        ukArray.add("Newport");
        ukArray.add("Newry");
        ukArray.add("Norwich");
        ukArray.add("Nottingham");
        ukArray.add("Oxford");
        ukArray.add("Perth");
        ukArray.add("Peterborough");
        ukArray.add("Plymouth");
        ukArray.add("Portsmouth");
        ukArray.add("Preston");
        ukArray.add("Ripon");
        ukArray.add("St Albans");
        ukArray.add("St Asaph");
        ukArray.add("St Davids");
        ukArray.add("Salford");
        ukArray.add("Salisbury");
        ukArray.add("Sheffield");
        ukArray.add("Southampton");
        ukArray.add("Stirling");
        ukArray.add("Stoke-on-Trent");
        ukArray.add("Sunderland");
        ukArray.add("Swansea");
        ukArray.add("Truro");
        ukArray.add("Wakefield");
        ukArray.add("Wells");
        ukArray.add("Westminster");
        ukArray.add("Winchester");
        ukArray.add("Wolverhampton");
        ukArray.add("Worcester");
        ukArray.add("York");

        return v;
    }

    public static RosterFragment newInstance(String text) {

        RosterFragment f = new RosterFragment(c);
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public static RosterFragment newInstance(String text, Context context)
    {
        RosterFragment f = new RosterFragment(c);
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        c = context;
        return f;
    }

    public RosterFragment(Context c)
    {
        this.c = c;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        populateCurrentUser();
        loadFollowedUsers();
        loadFollowers();
        //populateByCountry(Constants.getInstance().currentUser.getCountry());
        //populateByCountry(MainActivity.currentUser.getCountry());
        userAdapter.notifyDataSetChanged();
    }

    public void showFilterPopup(View view) {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        filterPopup = inflater.inflate(R.layout.popup_filter, null);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = false;
        filterOptionsPopup = new PopupWindow(filterPopup, width, height, focusable);
        //show the popup window

        countryFilter = filterPopup.findViewById(R.id.countryFilter);
        cityFilter = filterPopup.findViewById(R.id.cityFilter);
        applyFilterButton = filterPopup.findViewById(R.id.applyFilterButton);

        spinnerAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item, countryArray);
        countryFilter.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item, nullArray);
        cityFilter.setAdapter(spinnerAdapter);


        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCountry = countryFilter.getSelectedItem().toString();
                Toast.makeText(getActivity(), selectedCountry + " selected", Toast.LENGTH_SHORT).show();
                String selectedCity = cityFilter.getSelectedItem().toString();

                try {
                    //no filter applied
                    if (selectedCountry.equals("Select country")) {
                        Toast.makeText(c, "No filter applied", Toast.LENGTH_SHORT).show();
                    }
                    //all countries selected
                    if (selectedCountry.equals("All")) {
                        populateWithAllUsers();
                        filterOptionsPopup.dismiss();
                    }
                    //if a country is selected
                    if (!selectedCountry.equals("All") && !selectedCountry.equals("Select country")) {
                        if (selectedCity.equals("Select city")) {
                            Toast.makeText(c, "No city selected", Toast.LENGTH_SHORT).show();
                        }

                        //if all cities within a country is selected
                        if (selectedCity.equals("All")) {
                            allUserArray.clear();
                            populateByCountry(selectedCountry);
                            filterOptionsPopup.dismiss();
                        }

                        //if country and city is selected
                        if (!selectedCity.equals("Select city") && !selectedCity.equals("All")) {
                            allUserArray.clear();
                            populateByCity(selectedCity);
                            filterOptionsPopup.dismiss();
                        }

                    }
                } catch (Exception e) {

                }
            }
        });

        countryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = countryFilter.getSelectedItem().toString();

                if (selectedItem.equals("Australia")) {

                    //cityFilter.setAdapter(null);
                    //load array of Australian cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item, australiaArray);
                    cityFilter.setAdapter(spinnerAdapter);
                }
                if (selectedItem.equals("USA")) {
                    cityFilter.setAdapter(null);
                    //load array of American cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item, usaArray);
                    cityFilter.setAdapter(spinnerAdapter);
                }
                if (selectedItem.equals("UK")) {
                    cityFilter.setAdapter(null);
                    //load array of UK cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_dropdown_item, ukArray);
                    cityFilter.setAdapter(spinnerAdapter);
                }
                if (selectedItem.equals("All") || selectedItem.equals("Select country")) {
                    spinnerAdapter = new ArrayAdapter<>(c, android.R.layout.simple_spinner_dropdown_item, nullArray);
                    cityFilter.setAdapter(spinnerAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(c, "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });


        //filterOptionsPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        filterOptionsPopup.showAsDropDown(userList);
    }

    public void populateByCountry(String country)
    {
        final ArrayList<User> userArray = new ArrayList<User>();
        userAdapter = new UserAdapter(c, userArray, followedUsersArray, followersArray);
        userList.setAdapter(userAdapter);

        final User header = new User();
        header.avatarImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.search_icon);
        header.setNickname("Showing people in " + country);
        header.setFirstName("Click to apply filter");
        header.setLastName("");
        header.setUserID("header");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query locationQuery = ref.orderByChild("country").equalTo(country);
        locationQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount()>0)
                {
                    userArray.add(header);
                    userAdapter.notifyDataSetChanged();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                        {
                            //creates uses and adds to array
                            User user = userSnapshot.getValue(User.class);
                            userArray.add(user);
                            loadUserAvatars(user.getUserID(), userArray);
                            Collections.sort(userArray);
                            userAdapter.notifyDataSetChanged();
                        }

                }
                //if no users returned
                else if(dataSnapshot.getChildrenCount() == 0)
                {
                    userArray.clear();
                    userArray.add(header);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "No users found", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void populateWithAllUsers()
    {
        final ArrayList<User> userArray = new ArrayList<User>();
        userAdapter = new UserAdapter(c, userArray, followedUsersArray, followersArray);
        userList.setAdapter(userAdapter);

        final User header = new User();
        header.avatarImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.search_icon);
        header.setNickname("Showing ALL users");
        header.setFirstName("Click to apply filter");
        header.setLastName("");
        header.setUserID("header");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount()>0)
                {
                    userArray.add(header);
                    userAdapter.notifyDataSetChanged();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                    {
                        //creates uses and adds to array
                        User user = userSnapshot.getValue(User.class);
                        userArray.add(user);
                        loadUserAvatars(user.getUserID(), userArray);
                        Collections.sort(userArray);
                        userAdapter.notifyDataSetChanged();
                    }

                }
                //if no users returned
                else if(dataSnapshot.getChildrenCount() == 0)
                {
                    userArray.clear();
                    userArray.add(header);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "No users found", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void populateByCity(String city)
    {
        final ArrayList<User> userArray = new ArrayList<User>();
        userAdapter = new UserAdapter(c, userArray, followedUsersArray, followersArray);
        userList.setAdapter(userAdapter);

        final User header = new User();
        header.avatarImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.search_icon);
        header.setNickname("Showing people in " + city);
        header.setFirstName("Click to apply filter");
        header.setLastName("");
        header.setUserID("header");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query locationQuery = ref.orderByChild("city").equalTo(city);
        locationQuery.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount() >0)
                {
                    userArray.add(header);
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                    {
                        //creates uses and adds to array
                        User user = userSnapshot.getValue(User.class);
                        userArray.add(user);
                        loadUserAvatars(user.getUserID(), userArray);
                        Collections.sort(userArray);
                        userAdapter.notifyDataSetChanged();
                    }
                }
                //if no users returned
                else if(dataSnapshot.getChildrenCount() == 0)
                {
                    userArray.clear();
                    userArray.add(header);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "No users found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void populateByNameSearchAll(final String lastname)
    {
        final ArrayList<User> userArray = new ArrayList<User>();
        userAdapter = new UserAdapter(c, userArray, followedUsersArray, followersArray);
        userList.setAdapter(userAdapter);

        final User header = new User();
        header.avatarImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.search_icon);
        header.setNickname("Showing results for " + lastname);
        header.setFirstName("Click to apply filter");
        header.setLastName("");
        header.setUserID("header");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getChildrenCount() >0)
                {
                    userArray.add(header);
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                    {
                        //creates uses and adds to array
                        User user = userSnapshot.getValue(User.class);
                        if(user.getLastName().equalsIgnoreCase(lastname))
                        {
                            userArray.add(user);
                        }
                        loadUserAvatars(user.getUserID(), userArray);
                        Collections.sort(userArray);
                        userAdapter.notifyDataSetChanged();
                    }
                }
                //if no users returned
                if(userArray.size() == 1)
                {
                    header.setFirstName("no users found...");
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void goToProfileActivity(String selectedUserId, String selectedUserNickName, String currentUserId, String currentUserNickName)
    {
        Intent intent = new Intent(c.getApplicationContext(), ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("SELECTED_USER_ID", selectedUserId);
        bundle.putString("SELECTED_USER_NICKNAME", selectedUserNickName);
        bundle.putString("CURRENT_USER_ID", currentUserId);
        bundle.putString("CURRENT_USER_NICKNAME", currentUserNickName);
        intent.putExtras(bundle);
        c.startActivity(intent);
    }

    public void loadUserAvatars(final String userID, final ArrayList <User> array)
    {
        //if a user created the event
        if(Constants.getInstance().avatarImageCache.containsKey(userID))
        {
            //if image already exists in the cache - assign image from cache to respective user object
            for (User user : array)
            {
                if (user.getUserID().equals(userID))
                {
                    user.avatarImage = (Bitmap) Constants.getInstance().avatarImageCache.get(userID);
                    userAdapter.notifyDataSetChanged();
                    //Toast.makeText(getActivity(), "Activity avatar leaded from cache: " + ((Bitmap) Constants.getInstance().avatarImageCache.get(userID)).getByteCount(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            //if image doesnt exist in the cache - download from storage asynchronously to cache and update user array
            //###############DOWNLOAD THE IMAGE TO MEMORY#####################
            final long ONE_MEGABYTE = 1024 * 1024;
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storage.child("AvatarImages").child(userID).child(userID);
            try
            {
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        //when the image is loaded to the cache- add image to the Event in the array
                        Constants.getInstance().avatarImageCache.put(userID, bmp);
                        //assign image from cache to respective Event object
                        for (User user : array)
                        {
                            if (user.getUserID().equals(userID))
                            {
                                user.avatarImage = bmp;
                                userAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
            catch(Exception e)
            {
                Log.println(Log.ERROR, "Image", e.getMessage());
            }
        }
    }

    public void populateCurrentUser()
    {

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                                    populateByCountry(currentUser.getCountry());
                                }
                                else
                                    {//no user founds
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }

    public void loadFollowedUsers()
    {
        //########### GET LIST OF USERS THE CURRENT USER IS FOLLOWS #############
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference("Follows").child(currentFirebaseUser.getUid()).child("Following");
        followsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow followedUser = dataSnapshot.getValue(Follow.class);
                if(!Constants.getInstance().followedUsersArray.contains(followedUser))
                {
                    Constants.getInstance().followedUsersArray.add(followedUser);
                }

                if(!followedUsersArray.contains(followedUser))
                {
                   followedUsersArray.add(followedUser);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "Followed user added to array", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                Follow followedUser = dataSnapshot.getValue(Follow.class);
                if(Constants.getInstance().followedUsersArray.contains(followedUser))
                {
                    Constants.getInstance().followedUsersArray.remove(followedUser);
                }

                if(followedUsersArray.contains(followedUser))
                {
                    followedUsersArray.remove(followedUser);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "Followed user removed from array", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void loadFollowers()
    {
        //########### GET LIST OF USERS THE CURRENT USER IS FOLLOWS #############
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference().child("Follows").child(currentFirebaseUser.getUid()).child("Followers");
        followsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow follower = dataSnapshot.getValue(Follow.class);
                if(!Constants.getInstance().followersArray.contains(follower))
                {
                    Constants.getInstance().followedUsersArray.add(follower);
                }

                if(!followersArray.contains(follower))
                {
                    followersArray.add(follower);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "Follower added to array", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                Follow follower = dataSnapshot.getValue(Follow.class);
                if(Constants.getInstance().followersArray.contains(follower))
                {
                    Constants.getInstance().followersArray.remove(follower);
                }

                if(followersArray.contains(follower))
                {
                    followersArray.remove(follower);
                    userAdapter.notifyDataSetChanged();
                    Toast.makeText(c, "Follower removed from array", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}


//https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts