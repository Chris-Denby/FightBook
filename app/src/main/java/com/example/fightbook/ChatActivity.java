package com.example.fightbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fightbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener
{

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    User currentUser;
    String currentUserNickname;

    Intent previousIntent;
    String issuerID;
    String receiverID;
    String issuerNickName;
    String receiverNickName;
    Bundle bundle;


    ListView chatList;
    Button chatButton;
    EditText chatField;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        issuerID = bundle.getString("issuerID");
        receiverID = bundle.getString("receiverID");
        issuerNickName = bundle.getString("issuerNickName");
        receiverNickName = bundle.getString("receiverNickName");

        chatList = findViewById(R.id.chat_list);
        chatButton = findViewById(R.id.sent_button);
        chatField = findViewById(R.id.chat_field);
        chatButton.setOnClickListener(this);
    }

    public void sendChatMessage()
    {
        Toast.makeText(this, "" + issuerID, Toast.LENGTH_SHORT).show();

        //String message = chatField.getText().toString();
        //DatabaseReference issuedRef = FirebaseDatabase.getInstance().getReference("Challenges").child(issuerID).child("IssuedChallenges");
        //DatabaseReference receivedRef = FirebaseDatabase.getInstance().getReference("Challenges").child(issuerID).child("IssuedChallenges");
    }


    @Override
    public void onClick(View v)
    {
        sendChatMessage();
    }

    public void populateCurrentUser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(currentFirebaseUser.getUid());
        ref.addListenerForSingleValueEvent
                (
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                currentUserNickname = user.getNickname();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }
}
