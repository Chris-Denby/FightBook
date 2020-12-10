package com.example.fightbook;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailField;
    EditText passwordField;
    Button loginButton;
    Button registerButton;
    Button createProfileButton;
    private FirebaseAuth auth;
    DatabaseReference myRef;
    FirebaseDatabase database;
    FirebaseUser currentFirebaseUser;
    PopupWindow registerPopupWindow;
    EditText registerEmailField;
    EditText registerPasswordField;
    EditText registerFirstNameField;
    EditText registerLastNameField;
    EditText registerNicknameField;
    Spinner countryDropDown;
    Spinner cityDropDown;
    ArrayAdapter<String> spinnerAdapter;
    String selectedNickname;

    ArrayList<String> countryArray = new ArrayList<String>();
    ArrayList<String> australiaArray = new ArrayList<String>();
    ArrayList<String> usaArray = new ArrayList<>();
    ArrayList<String> ukArray = new ArrayList<>();
    ArrayList<String> nullArray = new ArrayList<String>();

    Boolean userExists;

    String localDateString;

    //String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    //Date currentDate = Calendar.getInstance().getTime();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Get local date from device
        Calendar calendar = GregorianCalendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        localDateString = dateFormat.format(calendar.getTime());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(); //gets reference to root of database

        emailField = findViewById(R.id.registerEmailField);
        passwordField = findViewById(R.id.passwordField);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        countryArray = new ArrayList<String>();
        countryArray.add("Select country");
        countryArray.add("Australia");
        countryArray.add("USA");
        countryArray.add("UK");

        nullArray.add("-");

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

    }

    public void onStart()
    {
        super.onStart();;
        //check if internet is connected
        if(!checkInternetConnectivity())
        {
            Toast.makeText(SignInActivity.this, "Internet connection required to sign in or register", Toast.LENGTH_LONG).show();
        }
        else
        if(checkPreviousSession())
        {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPreviousSession()
    {
        //check if user is already signed in
        //WORKING !! RETURNS EMAIL IF TOKEN EXISTS
        currentFirebaseUser = auth.getCurrentUser();
        if(currentFirebaseUser == null)
        {
            //Toast.makeText(this, "No session exists", Toast.LENGTH_SHORT).show();
            return false;


        }
        else
        {
            return true;
        }
    }

    public void register()
    {
        String password = registerPasswordField.getText().toString();
        String email = registerEmailField.getText().toString();
        String firstName = registerFirstNameField.getText().toString();
        String lastName = registerLastNameField.getText().toString();
        String nickname = registerNicknameField.getText().toString();
        selectedNickname = nickname;
        String country = countryDropDown.getSelectedItem().toString();
        String city = cityDropDown.getSelectedItem().toString();

        if(firstName.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
        }
        else
        if(lastName.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter your surname", Toast.LENGTH_SHORT).show();
        }
        else
        if(nickname.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter a nickname", Toast.LENGTH_SHORT).show();
        }
        else
        if(email.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please your email address", Toast.LENGTH_SHORT).show();
        }
        else
        if(password.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
        else
        if (city.equals("Select city") || country.equals("Select country"))
        {
            Toast.makeText(SignInActivity.this, "Please select your location", Toast.LENGTH_SHORT).show();
        }
        else
        if (nickname.length() > 18)
        {
            Toast.makeText(SignInActivity.this, "Nickname must be less than 18 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                //check with Firebase auth to see if email address has already been registered
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(SignInActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
                                }
                                if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(SignInActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                }
                                if (task.getException() instanceof Exception) {
                                    Toast.makeText(SignInActivity.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            } else if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                writeNewUser(registerEmailField.getText().toString(), registerPasswordField.getText().toString(), registerFirstNameField.getText().toString(), registerLastNameField.getText().toString(), registerNicknameField.getText().toString(), countryDropDown.getSelectedItem().toString(), cityDropDown.getSelectedItem().toString());
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }


    ArrayList<User> existingNickNameList = new ArrayList<User>();
    ArrayList<String> nicknameList = new ArrayList<String>();

    public void checkNicknameExistsArray()
    {
        existingNickNameList.clear();
        selectedNickname = registerNicknameField.getText().toString();

        existingNickNameList.clear();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();
                for (DataSnapshot userSnapshot : userChildren) {
                    User user = userSnapshot.getValue(User.class);

                    //TO DO: CHECK BELOW VARIABLES AGAINST USER OBJECT VALUE IN UPPER AND LOWER CASE VARIANTS
                    String existingNickname = user.getNickname().toString();
                    String chosenNickname = selectedNickname;
                    int nicknameResult = existingNickname.compareToIgnoreCase(chosenNickname);
                    if(nicknameResult == 0)
                    {
                        //strings are identical (ignoring case)
                        existingNickNameList.add(user);
                    }
                }
                if(existingNickNameList.isEmpty())
                {
                    Toast.makeText(SignInActivity.this, "nickname is original", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(SignInActivity.this, "nickname found", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retreiveAllNickames()
    {
        nicknameList.clear();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userChildren = dataSnapshot.getChildren();
                for (DataSnapshot userSnapshot : userChildren) {
                    User user = userSnapshot.getValue(User.class);
                    String existingNickname = user.getNickname().toString();
                    nicknameList.add(existingNickname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void writeNewUser(String email, String pw, String name1, String name2, String name3, String country, String city)
    {
        // create user object
        // create write user object to FirebaseDB using current profile user ID
        String userID = currentFirebaseUser.getUid();
        String authKey = userID.substring(0,5).toUpperCase();
        User user = new User(email, pw, name1, name2, name3, country, city, userID);
        user.setAuthKey(authKey);
        //user.setUserID(userID);
        myRef.child("Users").child(userID).setValue(user);
        // create event notifying user has joined
    }

    public void signInUser()
    {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(email.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
        }
        if(password.equals(""))
        {
            Toast.makeText(SignInActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //if login successful
                                        currentFirebaseUser = auth.getCurrentUser();
                                        //Toast.makeText(SignInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //if login failed
                                        Toast.makeText(SignInActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    );
        }
    }

    public boolean checkInternetConnectivity()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showRegisterPopup(View view) {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View registerPopupView;
        registerPopupView = inflater.inflate(R.layout.popup_register, null);
        registerPopupView.setElevation(3);
        //create the popup window
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow registerPopupWindow = new PopupWindow(registerPopupView, width, height, focusable);
        //show the popup window
        registerPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //define register popup window fields
        registerFirstNameField = registerPopupView.findViewById(R.id.refereeKeyField);
        registerLastNameField = registerPopupView.findViewById(R.id.registerLastNameField);
        registerNicknameField = registerPopupView.findViewById(R.id.registerNicknameField);
        registerEmailField = registerPopupView.findViewById(R.id.registerEmailField);
        registerPasswordField = registerPopupView.findViewById(R.id.registerPasswordField);
        createProfileButton = registerPopupView.findViewById(R.id.validateButton);
        createProfileButton.setOnClickListener(this);
        countryDropDown = registerPopupView.findViewById(R.id.countrySpinner);
        cityDropDown = registerPopupView.findViewById(R.id.citySpinner);

        //spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryArray);
        countryDropDown.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nullArray);
        cityDropDown.setAdapter(spinnerAdapter);

        countryDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = countryDropDown.getSelectedItem().toString();
                if(selectedItem.equals("Australia"))
                {
                    //cityDropDown.setAdapter(null);
                    //load array of Australian cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, australiaArray);
                    cityDropDown.setAdapter(spinnerAdapter);
                }
                if(selectedItem.equals("USA"))
                {
                    //cityDropDown.setAdapter(null);
                    //load array of American cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, usaArray);
                    cityDropDown.setAdapter(spinnerAdapter);
                }
                if(selectedItem.equals("UK"))
                {
                    //cityDropDown.setAdapter(null);
                    //load array of UK cities to the cityFilter spinner
                    spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, ukArray);
                    cityDropDown.setAdapter(spinnerAdapter);
                }
                if(selectedItem.equals("All") || selectedItem.equals("Select country"))
                {
                    spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, nullArray);
                    cityDropDown.setAdapter(spinnerAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId())
        {
            case R.id.registerButton:
                if(!checkInternetConnectivity())
                {
                    Toast.makeText(SignInActivity.this, "Internet connection required to sign in or register", Toast.LENGTH_LONG).show();
                }
                else
                    showRegisterPopup(emailField);
                break;

            case R.id.loginButton:
                if(!checkInternetConnectivity())
                {
                    Toast.makeText(SignInActivity.this, "Internet connection required to sign in or register", Toast.LENGTH_LONG).show();
                }
                else
                    signInUser();
                break;

            case R.id.validateButton:
                if(!checkInternetConnectivity())
                {
                    Toast.makeText(SignInActivity.this, "Internet connection required to sign in or register", Toast.LENGTH_LONG).show();
                }
                else
                    //checkNicknameExistsArray();
                    register();
                break;

        }//END SWITCH
    }// END ON CLICK METHOD

    public class Async extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //show loading dialog etc...
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            //execute background methods here
            checkNicknameExistsArray();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            //what happens after background tasks executed
            //close loading dialog etc...
        }
    }

    public void sendNotifications(String nodeID)
    {
        /**
        //send notification to following users

        final Notification notification = new Notification();
        notification.setText("has joined in your area");
        notification.setNodeID(nodeID);
        notification.setIssuerID();
        notification.setIssuerNickname();
        notification.setUserBID();
        notification.setUserBNickname();
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
         **/
    }

}// END CLASS


