package com.example.fightbook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class EventActivity extends AppCompatActivity implements View.OnClickListener
{
    FirebaseAuth auth;
    FirebaseUser currentFirebaseUser;

    EventActivityRecyclerAdapter recyclerAdapter;
    LinkedList eventCommentsList;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    //declare layout elements
    TextView nameLabel1;
    TextView nameLabel2;
    TextView eventTextLabel;
    TextView eventDateLabel;
    ImageView eventImage;
    ImageView avatarBubble;
    Button likeButton;
    Button removeEventButton;
    Button showResultsButton;
    TextView commentsCountLabel;

    //results overlay elements
    ConstraintLayout resultsOverlay;
    TextView winnerLabel;
    TextView loserLabel;
    TextView winnerNicknameLabel;
    TextView loserNicknameLabel;
    TextView winnerSystemLabel;
    TextView loserSystemLabel;
    TextView winnerPointsLabel;
    TextView loserPointsLabel;

    //Scores tables
    TextView winnerHeadCuts;
    TextView winnerHeadThrusts;
    TextView winnerTorsoCuts;
    TextView winnerTorsoThrusts;
    TextView winnerLimbCuts;
    TextView winnerLimbThrusts;

    TextView loserHeadCuts;
    TextView loserHeadThrusts;
    TextView loserTorsoCuts;
    TextView loserTorsoThrusts;
    TextView loserLimbCuts;
    TextView loserLimbThrusts;

    EditText commentsField;
    Button commentButton;

    TextView likesLabel;
    RecyclerView commentsRecyclerView;

    Event selectedEvent;

    SimpleDateFormat formatter = new SimpleDateFormat("d-MM H:mm");
    Date currentTime = new Date(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        auth  = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();

        eventCommentsList = new LinkedList();
        commentsRecyclerView = findViewById(R.id.comments_list);
        commentsRecyclerView.setHasFixedSize(true);

        commentsRecyclerView.setLayoutManager(layoutManager);

        SpaceItemDecoration divider = new SpaceItemDecoration(4);
        commentsRecyclerView.addItemDecoration(divider);

        recyclerAdapter = new EventActivityRecyclerAdapter(getApplicationContext(), eventCommentsList);
        commentsRecyclerView.setAdapter(recyclerAdapter);

        nameLabel1 = findViewById(R.id.name_label_1);
        nameLabel2 = findViewById(R.id.name_label_2);
        eventTextLabel = findViewById(R.id.event_text_label);
        eventDateLabel = findViewById(R.id.event_date_label);
        eventImage = findViewById(R.id.event_image);
        avatarBubble = findViewById(R.id.avatarBubble);
        likesLabel = findViewById(R.id.likes_label);
        likeButton = findViewById(R.id.like_button);
        showResultsButton = findViewById(R.id.show_result_button);
        commentsCountLabel = findViewById(R.id.comment_count_label);

        //results overlay elements
        resultsOverlay = findViewById(R.id.result_overlay);
        winnerLabel = findViewById(R.id.winner_label);
        loserLabel = findViewById(R.id.loser_label);
        winnerNicknameLabel = findViewById(R.id.winner_nickname_label);
        loserNicknameLabel = findViewById(R.id.loser_nickname_label);
        winnerSystemLabel = findViewById(R.id.winner_sytem_label);
        loserSystemLabel = findViewById(R.id.loser_system_label);
        winnerPointsLabel = findViewById(R.id.winner_points_label);
        loserPointsLabel = findViewById(R.id.loser_points_label);

        //Scores tables
        winnerHeadCuts = findViewById(R.id.winner_head_cuts);
        winnerHeadThrusts = findViewById(R.id.winner_head_thrusts);
        winnerTorsoCuts = findViewById(R.id.winner_torso_cuts);
        winnerTorsoThrusts = findViewById(R.id.winner_torso_thrusts);
        winnerLimbCuts = findViewById(R.id.winner_limb_cuts);
        winnerLimbThrusts = findViewById(R.id.winner_limb_thrusts);

        loserHeadCuts = findViewById(R.id.loser_head_cuts);
        loserHeadThrusts = findViewById(R.id.loser_head_thrusts);
        loserTorsoCuts = findViewById(R.id.loser_torso_cuts);
        loserTorsoThrusts = findViewById(R.id.loser_torso_thrusts);
        loserLimbCuts = findViewById(R.id.loser_limb_cuts);
        loserLimbThrusts = findViewById(R.id.loser_limb_thrusts);

        commentsField =findViewById(R.id.comments_field);
        commentButton = findViewById(R.id.comment_button);
        commentButton.setOnClickListener(this);

        loadEventData();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public void loadEventData()
    {
        Bundle intentBundle = getIntent().getExtras();
        final String selectedUserID = intentBundle.getString("SELECTED_USER_ID");
        final String selectedNodeID = intentBundle.getString("SELECTED_NODE_ID");

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(selectedUserID).child(selectedNodeID);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                selectedEvent = dataSnapshot.getValue(Event.class);

                DatabaseReference fightRef = FirebaseDatabase.getInstance().getReference("Fights").child(selectedUserID).child(selectedNodeID);
                fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        Fight fight = dataSnapshot.getValue(Fight.class);
                        if(selectedEvent != null)
                        {
                            //once event is loaded and fight data associated, populate the view
                            selectedEvent.fight = fight;
                            eventCommentsList.addFirst(selectedEvent);

                            loadEventImage(selectedEvent.getPhotoPath(), selectedEvent.getnodeID());
                            loadEventComments(selectedEvent.getnodeID());
                            loadEventLikes(selectedEvent.getnodeID());
                            selectedEvent.avatarImage = loadAvatars(selectedEvent.getCreatedById(), null);

                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to retrieve event - please try again", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(getApplicationContext(), "Failed to retrieve event - please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadEventImage(final String photoPath, final String eventID)
    {
        if(photoPath.equals("no photo") || photoPath.equals(""))
        {
            //if no photo uploaded for the event
        }
        else
        {
            //if a photo was uploaded for the event
            if(Constants.getInstance().activityFeedImageCache.containsKey(eventID))
            {
                //if image already exists in the cache
                //assign image from cache to respective Event object
                selectedEvent.setImage((Bitmap) Constants.getInstance().activityFeedImageCache.get(eventID));
                recyclerAdapter.notifyItemChanged(0);

            }
            else
            {
                //if image doesnt exist in the cache - download from storage asynchronously to cache and update event array
                //###############DOWNLOAD THE IMAGE TO MEMORY#####################
                StorageReference storage = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storage.child("EventImages").child(eventID).child(photoPath);

                try
                {
                    imageRef.getBytes(Constants.getInstance().ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            //when the image is loaded to the cache- add image to the Event in the array
                            Constants.getInstance().activityFeedImageCache.put(eventID, bmp);
                            selectedEvent.setImage(bmp);
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

    public void loadEventComments(final String eventID)
    {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments").child(eventID);
        ChildEventListener commentsCountListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Chat comment = dataSnapshot.getValue(Chat.class);
                //update event in activity feed array and notify adapter of change

                selectedEvent.commentsCount = eventCommentsList.size();
                comment.avatarImage = loadAvatars(comment.getSenderID(), comment.getChatID());
                eventCommentsList.add(comment);
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
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
        commentsRef.addChildEventListener(commentsCountListener);
    }

    public void loadEventLikes(final String eventID)
    {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("EventLikes").child(eventID);
        ChildEventListener commentsCountListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Like eventLike = dataSnapshot.getValue(Like.class);
                //update event in activity feed array and notify adapter of change
                selectedEvent.likesArray.add(eventLike);
                selectedEvent.likesCount = selectedEvent.likesArray.size();

                //if the event like is from the current user, mark the event as being liked by the current user
                if(eventLike.getUserID().equals(currentFirebaseUser.getUid()))
                {
                    selectedEvent.liked = true;
                }
                recyclerAdapter.notifyItemChanged(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                Like eventLike = dataSnapshot.getValue(Like.class);
                if(selectedEvent.likesArray.contains(eventLike))
                {
                    //remove the like from the events like array
                    //Toast.makeText(getContext(), "Removing like: " + eventLike.getUserNickName(), Toast.LENGTH_SHORT).show();
                    selectedEvent.likesArray.remove(eventLike);
                    selectedEvent.likesCount = selectedEvent.likesArray.size();
                }
                recyclerAdapter.notifyItemChanged(0);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        likesRef.addChildEventListener(commentsCountListener);
    }

    public void sendNotification(String likedByUserID, String likedByNickname, String userBID, String userBNickName, String notificationText, String nodeID)
    {
        Date currentTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YY H:mm");

        //create notification for associated user
        Notification notification = new Notification();
        notification.setCreatedDate(formatter.format(currentTime));
        notification.setText(notificationText);
        notification.setNodeID(nodeID);
        notification.setIssuerID(likedByUserID);
        notification.setIssuerNickname(likedByNickname);
        notification.setUserBID(userBID);
        notification.setUserBNickname(userBNickName);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String notificationKey = database.getReference("Notifications").child(userBID).push().getKey();
        notification.setNotificationKey(notificationKey);
        DatabaseReference notificationsRef = database.getReference("Notifications").child(userBID).child(notificationKey);
        notificationsRef.setValue(notification);


    }

    public Bitmap loadAvatars(final String userID, final String chatID)
    {
        Bitmap image;
        //if a user created the event
        if(Constants.getInstance().avatarImageCache.containsKey(userID))
        {

             image = (Bitmap) Constants.getInstance().avatarImageCache.get(userID);
        }
        else
        {
            //if image doesnt exist in the cache - download from storage asynchronously to cache and update user array
            //###############DOWNLOAD THE IMAGE TO MEMORY#####################
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storage.child("AvatarImages").child(userID).child(userID);
            try
            {
                imageRef.getBytes(Constants.getInstance().ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        //load image to cache
                        Constants.getInstance().avatarImageCache.put(userID, bmp);

                        for(Object o:eventCommentsList)
                        {
                            /**
                            if(o instanceof Chat)
                            {
                                if(((Chat)o).getChatID().equals(chatID))
                                {
                                    ((Chat)o).avatarImage = bmp;
                                    recyclerAdapter.notifyItemChanged(eventCommentsList.indexOf(o));
                                }
                            }
                            if(o instanceof Event) {
                                if (((Event) o).getnodeID().equals(chatID)) {
                                    ((Event) o).avatarImage = bmp;
                                    recyclerAdapter.notifyItemChanged(eventCommentsList.indexOf(o));
                                }
                            }
                             **/
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
            image = null;
        }
        return image;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration
    {
        private int verticleSpaceHeight;

        public SpaceItemDecoration(int height)
        {
            verticleSpaceHeight = height;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() -1)
            {
                outRect.bottom = verticleSpaceHeight;
            }
        }
    }

    public void sendCommentMessage(Event event, String senderNN,String msg, String nodeID, String userBID)
    {
        if(!msg.equals(""))
        {
            //Chat comments = new Chat(senderNN, msg, "1 Jan"); //sender name / message text / date
            Chat comment = new Chat(Constants.getInstance().currentUser.getNickname(), Constants.getInstance().currentUser.getUserID(), msg, formatter.format(currentTime)); //sender name / message text / date
            comment.setChatID(nodeID);
            DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments").child(nodeID);
            commentsRef.push().setValue(comment);
            commentsField.setText("");
            hideKeyboard(this);
            recyclerAdapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(eventCommentsList.size() - 1);
            String commentSub = msg;
            //if message is over X characters longm truncate for notification
            if(msg.length() > 30)
            {
            commentSub = msg.substring(0, 30) + "...";
            }

            if(selectedEvent.getCreatedById().equals(Constants.getInstance().currentUser.getUserID()))
            {
                //if current user created the event, dont send notification of chat
            }
            else
            {
                if(userBID != null)
                //if its someone elses event, send notfication of chat

                sendNotification(Constants.getInstance().currentUser.getUserID(), Constants.getInstance().currentUser.getNickname(), selectedEvent.getCreatedById(), selectedEvent.getCreatedByNickname(), "commented on your post: " + "\""+commentSub+"\"", selectedEvent.getnodeID());
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.comment_button:
                sendCommentMessage(selectedEvent, Constants.getInstance().currentUser.getNickname(), commentsField.getText().toString(), selectedEvent.nodeID, selectedEvent.userBID);
                break;

            case R.id.comments_field:
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }

}
