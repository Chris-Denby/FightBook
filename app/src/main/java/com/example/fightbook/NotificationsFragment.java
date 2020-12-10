package com.example.fightbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;

public class NotificationsFragment extends Fragment
{

    RecyclerView eventList;
    LinkedList<Object> challengeArray = new LinkedList();
    NotificationFeedRecyclerAdapter challengeAdapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    //ArrayList newNotifications = new ArrayList<Notification>();
    int newNotifications = 0;

    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;
    String currentUserID;

    PopupWindow chatPopup;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();
        currentUserID = currentFirebaseUser.getUid();

        eventList = v.findViewById(R.id.event_list);
        challengeAdapter = new NotificationFeedRecyclerAdapter(getActivity(), challengeArray);
        eventList.setLayoutManager(layoutManager);
        //optimize recycler view
        eventList.setHasFixedSize(true);
        eventList.setAdapter(challengeAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(eventList.getContext(), layoutManager.getOrientation());
        eventList.addItemDecoration(divider);
        loadChallenges();
        //loadNotifications();

        return v;
    }

    public static NotificationsFragment newInstance(String text) {

        NotificationsFragment f = new NotificationsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public void loadChallenges()
    {
        DatabaseReference receivedRef = FirebaseDatabase.getInstance().getReference("Challenges").child(currentUserID).child("ReceivedChallenges");
        receivedRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                ChallengeToken token = dataSnapshot.getValue(ChallengeToken.class);
                if(!challengeArray.contains(token))
                {
                    //challengeArray.add(token);
                    challengeArray.addFirst(token);
                    loadChallengeAvatars(token.getIssuerID(), token.getChatID());
                    loadChallengeChats(token.chatID);
                    challengeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                ChallengeToken token = dataSnapshot.getValue(ChallengeToken.class);
                int position = challengeArray.indexOf(token);
                ChallengeToken tokenToChange = (ChallengeToken) challengeArray.get(position);
                tokenToChange.setTokenStatus(token.getTokenStatus());
                //challengeArray.set(position, token);
                challengeAdapter.notifyItemChanged(position);
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


        DatabaseReference issuedRef = FirebaseDatabase.getInstance().getReference("Challenges").child(currentUserID).child("IssuedChallenges");
        issuedRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                ChallengeToken token = dataSnapshot.getValue(ChallengeToken.class);

                if(!challengeArray.contains(token))
                {
                    //challengeArray.add(token);
                    challengeArray.addFirst(token);
                    loadChallengeAvatars(token.getReceiverID(), token.getChatID());
                    loadChallengeChats(token.chatID);
                    challengeAdapter.notifyDataSetChanged();
                }
            }


            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                ChallengeToken token = dataSnapshot.getValue(ChallengeToken.class);
                int position = challengeArray.indexOf(token);
                ChallengeToken tokenToChange = (ChallengeToken) challengeArray.get(position);
                tokenToChange.setTokenStatus(token.getTokenStatus());
                //challengeArray.set(position, token);
                challengeAdapter.notifyItemChanged(position);
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
    }

    public void loadNotifications()
    {
        final DatabaseReference receivedRef = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUserID);
        Query limitToLast = receivedRef.limitToLast(Constants.getInstance().NOTIFICATION_FEED_LIMIT);
        //receivedRef.addChildEventListener(new ChildEventListener()    //get all
        limitToLast.addChildEventListener(new ChildEventListener()      // limit to last 20
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Boolean isDuplicate = false;
                Notification notification = dataSnapshot.getValue(Notification.class);
                for(Object o :challengeArray)
                {
                    if(o instanceof Notification)
                    {
                        if(notification.getIssuerID().equals(((Notification) o).getIssuerID()) & notification.getNodeID().equals(((Notification) o).getNodeID()))
                        {
                            if(notification.getText().equals("likes your fight with") & ((Notification) o).getText().equals("likes your fight with"))
                            {
                                //Toast.makeText(getActivity(), "Duplicate like found", Toast.LENGTH_SHORT).show();
                                isDuplicate = true;
                            }
                            else
                            {
                                //add to array
                                isDuplicate = false;
                            }
                        }
                    }
                }
                if(!isDuplicate)
                {
                    challengeArray.add(notification);
                    loadNotificationAvatars(notification.getIssuerID(), notification.getNodeID());
                    challengeAdapter.notifyDataSetChanged();

                    //SHOW NOTIFICATION BUBBLE
                    if(notification.getIsNew().equals("true"))
                    {
                        MainActivity.incrementNotificationBubble();
                    }
                    isDuplicate = false;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                //Notification notification = dataSnapshot.getValue(Notification.class);
                //int position = challengeArray.indexOf(notification);
                //Notification notificationToChange = (Notification) challengeArray.get(position);
                //notificationToChange.setIsNew(notification.getIsNew());
                //challengeAdapter.notifyItemChanged(position);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                //challengeArray.clear();
                //ChallengeToken token = dataSnapshot.getValue(ChallengeToken.class);
                //challengeArray.add(token);
                //challengeAdapter.notifyDataSetChanged();
                //MainActivity.notificationBubble.setText(challengeArray.size());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    final public void beginFight(String opponentID)
    {
        Intent intent = new Intent(getActivity(), FightActivity.class);
        intent.putExtra("blueUID", opponentID);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {

        super.onResume();
        challengeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    public void loadChallengeChats(final String chatID)
    {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatID);
        ChildEventListener commentsCountListener = new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Chat chat = dataSnapshot.getValue(Chat.class);
                //update challenge in challenge array and notify adapter of change
                for (Object object : challengeArray)
                {
                    if(object instanceof ChallengeToken)
                    {
                        ChallengeToken challenge = (ChallengeToken) object;
                        if (challenge.getChatID().equals(chatID))
                        {
                            if(!challenge.chatArray.contains(chat))
                            {
                                challenge.chatArray.add(chat);
                                challenge.chatCount = challenge.chatArray.size();
                                challengeAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
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
        };
        chatRef.addChildEventListener(commentsCountListener);
    }

    public void loadChallengeAvatars(final String userID, final String challengeID)
    {
        //if a user created the event
        if(Constants.getInstance().avatarImageCache.containsKey(userID)) {
            //if image already exists in the cache - assign image from cache to respective user object
            for (Object object : challengeArray) {
                if (object instanceof ChallengeToken) {
                    ChallengeToken challenge = (ChallengeToken) object;
                    if (challenge.getChatID().equals(challengeID)) {
                        challenge.avatarImage = (Bitmap) Constants.getInstance().avatarImageCache.get(userID);
                        challengeAdapter.notifyDataSetChanged();
                    }
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
                        for (Object object : challengeArray)
                        {
                            if(object instanceof ChallengeToken)
                            {
                                ChallengeToken challenge = (ChallengeToken) object;
                                if (challenge.getChatID().equals(challengeID))
                                {
                                    challenge.avatarImage = bmp;
                                    challengeAdapter.notifyDataSetChanged();
                                }
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

    public void loadNotificationAvatars(final String userID, final String nodeID)
    {
        //if a user created the event
        if(Constants.getInstance().avatarImageCache.containsKey(userID))
        {
            //if image already exists in the cache - assign image from cache to respective user object
            for (Object object : challengeArray)
            {
                if (object instanceof Notification)
                {
                    Notification notification = (Notification) object;
                    if(notification.getIssuerID().equals(userID) && notification.getNodeID().equals(nodeID))
                    {
                        notification.avatarImage = (Bitmap) Constants.getInstance().avatarImageCache.get(userID);
                        challengeAdapter.notifyItemChanged(challengeArray.indexOf(object));
                    }
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
                        for (Object object : challengeArray)
                        {
                            if (object instanceof Notification)
                            {
                                Notification notification = (Notification) object;
                                if(notification.getIssuerID().equals(userID) && notification.getNodeID().equals(nodeID))
                                {
                                    notification.avatarImage = bmp;
                                    challengeAdapter.notifyItemChanged(challengeArray.indexOf(object));
                                }
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
}







//https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts