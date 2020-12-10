package com.example.fightbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment
{
    PopupWindow createPostPopup;

    RecyclerView activityFeed;
    final static LinkedList<Event> activityFeedArray = new LinkedList<>();
    ActivityFeedRecyclerAdapter activityFeedAdapter;
    ArrayList<String> blockedEventsArray = new ArrayList<>();

    TextView profileName;
    Button createPostButton;
    ConstraintLayout homeBar;

    DatabaseReference database;
    DatabaseReference ref;
    FirebaseUser currentFirebaseUser;
    FirebaseAuth auth;

    User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activityFeedAdapter = new ActivityFeedRecyclerAdapter(getActivity(), activityFeedArray);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        showSplashPopup(v);


        auth = FirebaseAuth.getInstance();
        currentFirebaseUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        homeBar = v.findViewById(R.id.home_bar);

        activityFeed = v.findViewById(R.id.event_list);
        activityFeed.setLayoutManager(new LinearLayoutManager(getActivity()));

        //optimize recycler view
        activityFeed.setHasFixedSize(true);

        //remove on data changed animations
        //activityFeed.getItemAnimator().setSupportsChangeAnimations(false);
        activityFeed.getItemAnimator().setChangeDuration(0);

        profileName = v.findViewById(R.id.profileName);

        createPostButton = v.findViewById(R.id.post_button);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //showCreatePostPopup(homeBar);
            }
        });

        activityFeed.setAdapter(activityFeedAdapter);

        //add divider to recycler view - disabled as top margin on item used instead
        //SpaceItemDecoration divider = new SpaceItemDecoration(24);
        //activityFeed.addItemDecoration(divider);

        populateCurrentUser();
        loadBlockedEvents();
        populateActivityfeed();

        return v;
    }

    public static HomeFragment newInstance(String text)
    {
        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStart()
    {
        super.onStart();
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
                                    User user = dataSnapshot.getValue(User.class);
                                    MainActivity.currentUser = user;
                                    currentUser = user;

                                    //update user node to current user object
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUserID());
                                    ref.setValue(user);

                                    //checkNewSeason();

                                    profileName.setText(user.getNickname());
                                }
                                else{
                                    Toast.makeText(getActivity(), "no user retrieved", Toast.LENGTH_SHORT).show();}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }
                );
    }

    public void showSplashPopup(View view)
    {
        SplashPopup splash = new SplashPopup(view, getContext());
        splash.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        activityFeedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        activityFeedAdapter.notifyDataSetChanged();
    }

    public void populateActivityfeed()
    {
        //########### GET CURRENT USERS EVENTS ########

        activityFeedArray.clear();

        /**
        //check how many events in the list
        DatabaseReference currentUserEventsRef = FirebaseDatabase.getInstance().getReference("Events").child(currentFirebaseUser.getUid());
        currentUserEventsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Long countEvents = dataSnapshot.getChildrenCount();
                Toast.makeText(getActivity(), countEvents + " events", Toast.LENGTH_SHORT).show();

                if(countEvents<2)
                {
                    Event header = new Event();
                    header.isHeader = true;
                    activityFeedArray.addFirst(header);
                    activityFeedAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         **/

        DatabaseReference currentUserEventsRef = FirebaseDatabase.getInstance().getReference("Events").child(currentFirebaseUser.getUid());
        Query limitToLast = currentUserEventsRef.limitToLast(Constants.getInstance().ACTIVITY_FEED_LIMIT);
        limitToLast.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Event event = dataSnapshot.getValue(Event.class);

                if(!activityFeedArray.contains(event) && !blockedEventsArray.contains(event.getnodeID()))
                {
                    //if the Event doesnt already exist in the array - by comapring nodeID values
                    activityFeedArray.addFirst(event);



                    //load data for event
                    loadEventComments(event.nodeID);
                    loadEventLikes(event.nodeID);
                    loadEventFightData(event.nodeID, event.createdById);

                    //if the event has a photo, load the photo to memory
                    if(!event.getPhotoPath().equals(""))
                    {
                        loadActivityFeedImage(event.getPhotoPath(), event.getnodeID());
                    }

                    //load the event creators avatar to memory
                    loadActivityFeedAvatars(event.nodeID, event.getCreatedById());
                    activityFeedAdapter.notifyDataSetChanged();
                }
                else if(activityFeedArray.contains(event))
                {
                    activityFeedAdapter.notifyDataSetChanged();
                }
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
        });

        //########### GET LIST OF USERS THE CURRENT USER IS FOLLOWS #############
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference("Follows").child(currentFirebaseUser.getUid()).child("Following");
        followsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Follow followedUser = dataSnapshot.getValue(Follow.class);
                addFollowedUserToActivityFeed(followedUser.getUserID());
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
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public void addFollowedUserToActivityFeed(String followedUserID)
    {
        DatabaseReference followedUserEventsRef = FirebaseDatabase.getInstance().getReference("Events").child(followedUserID);
        Query limitToLast = followedUserEventsRef.limitToLast(Constants.getInstance().ACTIVITY_FEED_LIMIT);
        limitToLast.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Event event = dataSnapshot.getValue(Event.class);
                if(!activityFeedArray.contains(event) && !blockedEventsArray.contains(event.getnodeID()))
                {
                    //if the Event doesnt already exist in the activity feed array - by comapring nodeID values && if event node ID isnt listed in the users record of blocked events
                    loadEventComments(event.nodeID);
                    loadEventLikes(event.nodeID);
                    activityFeedArray.add(event);

                    loadEventFightData(event.nodeID, event.createdById);

                    if(!event.getPhotoPath().equals(""))
                    {
                        loadActivityFeedImage(event.getPhotoPath(), event.getnodeID());
                    }

                    loadActivityFeedAvatars(event.nodeID, event.getCreatedById());
                    activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                }
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
        });
    }

    public void loadActivityFeedImage(final String photoPath, final String eventID)
    {
        if(photoPath.equals("no photo"))
        {
            //if no photo uploaded for the event
        }
        else
            {
            //if a photo was uploaded for the event
            if(Constants.getInstance().activityFeedImageCache.containsKey(eventID))
            {
                //if image already exists in the cache
                //Toast.makeText(getActivity(), "Image already cached", Toast.LENGTH_SHORT).show();

                //assign image from cache to respective Event object
                for (Event event : activityFeedArray) {
                    if (event.getnodeID().equals(eventID))
                    {
                        event.setImage((Bitmap) Constants.getInstance().activityFeedImageCache.get(eventID));
                        activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                    }
                }
            }
            else
                {
                //if image doesnt exist in the cache - download from storage asynchronously to cache and update event array
                //###############DOWNLOAD THE IMAGE TO MEMORY#####################
                StorageReference storage = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storage.child("EventImages").child(eventID).child(photoPath);

                try
                {
                    imageRef.getBytes(Constants.getInstance().HALF_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            //when the image is loaded to the cache- add image to the Event in the array
                            Constants.getInstance().activityFeedImageCache.put(eventID, bmp);
                            for (Event event : activityFeedArray) {
                                if (event.getnodeID().equals(eventID)) {
                                    event.setImage(bmp);
                                    activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
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

    public void loadEventComments(final String eventID)
    {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("Comments").child(eventID);
        ChildEventListener commentsCountListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Chat comment = dataSnapshot.getValue(Chat.class);
                //update event in activity feed array and notify adapter of change
                for (Event event : activityFeedArray)
                {
                    if(event.getnodeID().equals(comment.getChatID()))
                    {
                        if(!event.commentsArray.contains(comment))
                        {
                            event.commentsArray.add(comment);
                            event.commentsCount = event.commentsArray.size();
                            event.lastComment = comment.getChatText();
                            event.lastCommentSender = comment.getSender();
                            activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                Chat comment = dataSnapshot.getValue(Chat.class);
                for (Event event : activityFeedArray) {
                    if (event.getnodeID().equals(eventID))
                    {
                        for(int x=0; x<event.commentsArray.size(); x=x+1)
                        {
                            if(event.commentsArray.get(x).chatText.equals(comment.chatText))
                            {
                                event.commentsArray.remove(x);
                                activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                            }
                        }
                    }
                }
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
                Like eventLike = dataSnapshot.getValue(Like.class);                //update event in activity feed array and notify adapter of change
                for (Event event : activityFeedArray)
                {
                    //add likes to array in respective event
                    if(event.getnodeID().equals(eventID))
                    {
                        //if matching event is found, do the following:

                        //if the event likes array doestn already contain hits like, add it
                        if(!event.likesArray.contains(eventLike))
                        {
                            event.likesArray.add(eventLike);
                            event.likesCount = event.likesArray.size();
                            activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                        }

                        //if the event like is from the current user, mark the event as being liked by the current user
                        if(eventLike.getUserID().equals(currentFirebaseUser.getUid()))
                        {
                           event.liked = true;
                            activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {
                Like eventLike = dataSnapshot.getValue(Like.class);
                for (Event event : activityFeedArray)
                {
                    //add likes to array in respective event
                    if(event.getnodeID().equals(eventLike.getEventID()))
                    {
                        //if matching event is found, do the following:

                        //if the event likes array already contain hits like, add it
                        if(event.likesArray.contains(eventLike))
                        {
                            //remove the like from the events like array
                            //Toast.makeText(getContext(), "Removing like: " + eventLike.getUserNickName(), Toast.LENGTH_SHORT).show();
                            event.likesArray.remove(eventLike);
                            event.liked = false;
                            event.likesCount = event.likesArray.size();
                            activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                        }
                    }
                }
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

    public void loadBlockedEvents()
    {

        DatabaseReference blockedEventsRef = FirebaseDatabase.getInstance().getReference("Blocked Events").child(currentFirebaseUser.getUid());
        blockedEventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                String nodeID = (String) dataSnapshot.getValue();
                blockedEventsArray.add(nodeID);
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

    public void loadActivityFeedAvatars(final String eventID, final String userID)
    {
        if(userID.equals(""))
        {
            //if event not created by a user
        }
        else
        {
            //if a user created the event
            if(Constants.getInstance().avatarImageCache.containsKey(userID))
            {
                //if image already exists in the cache
                //assign image from cache to respective Event object
                for (Event event : activityFeedArray)
                {
                    if (event.getCreatedById().equals(userID))
                    {
                        event.avatarImage = (Bitmap) Constants.getInstance().avatarImageCache.get(userID);
                        activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
                    }
                }
            }
            else
            {
                //if image doesnt exist in the cache - download from storage asynchronously to cache and update event array
                //###############DOWNLOAD THE IMAGE TO MEMORY#####################;
                StorageReference storage = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storage.child("AvatarImages").child(userID).child(userID);
                try
                {
                    imageRef.getBytes(Constants.getInstance().HALF_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            //when the image is loaded to the cache- add image to the Event in the array
                            Constants.getInstance().avatarImageCache.put(userID, bmp);
                            //assign image from cache to respective Event object
                            for (Event event : activityFeedArray) {
                                if (event.getCreatedById().equals(userID))
                                {
                                    event.avatarImage = bmp;
                                    activityFeedAdapter.notifyItemChanged(activityFeedArray.indexOf(event));
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

    public void loadEventFightData(final String eventID, String userID)
    {
        FirebaseDatabase.getInstance().getReference().child("Fights").child(userID).child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Fight fight = dataSnapshot.getValue(Fight.class);

                if (fight != null)
                {
                    for (Event event : activityFeedArray) {
                        if (fight.getNodeID().equals(event.getnodeID())) {
                            event.fight = fight;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     //###############DOWNLOAD THE IMAGE TO MEMORY#####################
     final long HALF_MEGABYTE = 1024 * 1024;
     StorageReference imageRef = storage.getReference().child(event.getUserAID()).child(event.photoPath);
     imageRef.getBytes(HALF_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
    @Override
    public void onSuccess(byte[] bytes) {
    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    viewHolder.eventImage.setImageBitmap(bmp);
    Toast.makeText(c, "Image loaded", Toast.LENGTH_SHORT).show();
    }
    }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
    Toast.makeText(c, "Image failed to load", Toast.LENGTH_SHORT).show();
    }
    });
     **/

    /**
     //###############DOWNLOAD THE IMAGE USING URL#####################
     storage.getReference().child(event.getUserAID()).child(event.photoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
     {
     @Override
     public void onSuccess(Uri uri)
     {
     //get the download URL for the photo in the
     viewHolder.eventImage.setImageURI(uri);
      DataSetChanged();
     return;
     }
     }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e)
    {
    //photo URL not retreived
    Toast.makeText(c, "Image failed to load" + e, Toast.LENGTH_SHORT).show();
    return;
    }
    });
     **/

    public void showCreatePostPopup(View view)
    {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View postView = inflater.inflate(R.layout.popup_create_post, null);
        //create the popup window

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        ImageView avatarBubble = postView.findViewById(R.id.avatarBubble);
        final TextView textField = postView.findViewById(R.id.text_field);
        Button postButton = postView.findViewById(R.id.post_button);
        final TextView charCountLabel = postView.findViewById(R.id.char_count);

        avatarBubble.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(currentFirebaseUser.getUid()));

        int charCount = 0;

        charCountLabel.setText(charCount + "/256");


        textField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                charCountLabel.setText(textField.getText().length() + "/256");
                return false;
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!textField.getText().toString().equals("")) {
                    createPost(textField.getText().toString());
                }
            }
        });

        createPostPopup = new PopupWindow(postView, width, height, focusable);
        createPostPopup.showAsDropDown(view);
    }

    public void createPost(String postText)
    {
        Date currentTime;
        SimpleDateFormat formatter;
        currentTime = new Date(System.currentTimeMillis());
        formatter = new SimpleDateFormat("dd-MM-YY H:mm");

        Event post = new Event();
        post.setDateCreated(formatter.format(currentTime));
        post.setCreatedByNickname(MainActivity.currentUser.getNickname());
        post.setCreatedById(MainActivity.currentUser.getUserID());
        post.setEventText(postText);
        post.setPhotoPath("no photo");

        String nodeID = database.child("Comments").push().getKey();
        post.setnodeID(nodeID);

        //create events in database for each user
        database.child("Events").child(MainActivity.currentUser.getUserID()).child(nodeID).setValue(post);

        createPostPopup.dismiss();
    }

    @Override
    public void onStop()
    {
        super.onStop();
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

    public void testActivityFeedSorting()
    {
        activityFeedArray.clear();

        for(int x = 0; x < 5; x++)
        {
            activityFeedArray.add(new Event("test", "test", "test" + x, "0"+x + "-" + "0"+x + "-" + "0"+x + " " + "0" +x + ":" + "0"+x));
        }

        Event eventp0 = new Event("test", "test", "test", "00-00-00 11:11");
        eventp0.setPhotoPath("no photo");
        activityFeedArray.add(eventp0);

        Event eventp1 = new Event("test", "test", "test", "01-01-01 11:11");
        eventp1.setPhotoPath("no photo");
        activityFeedArray.add(eventp1);

        Event eventp2 = new Event("test", "test", "test", "02-02-02 11:11");
        eventp2.setPhotoPath("no photo");
        activityFeedArray.add(eventp2);

        Collections.sort(activityFeedArray);
        activityFeedAdapter.notifyDataSetChanged();
    }

    PopupWindow popupWindowLogo;
    /**
     public void showSplashPopup(View view)
     {
     //TOP LAYER WITH ANIMATION
     LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
     final View popupView1 = inflater1.inflate(R.layout.popup_splash, null);
     //popupView1.setElevation(3);
     //create the popup window
     int width1 = ConstraintLayout.LayoutParams.MATCH_PARENT;
     int height1 = ConstraintLayout.LayoutParams.MATCH_PARENT;
     boolean focusable1 = false;
     popupWindowLogo = new PopupWindow(popupView1, width1, height1, focusable1);
     popupWindowLogo.showAtLocation(view, Gravity.CENTER, 0, 0);

     CountDownTimer splashTimer = new CountDownTimer(3000,1000)
     {
     @Override
     public void onTick(long millisUntilFinished)
     {

     }
     @Override
     public void onFinish()
     {
     popupWindowLogo.dismiss();
     }
     };
     splashTimer.start();
     }
     **/

}
