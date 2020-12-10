package com.example.fightbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

//https://guides.codepath.com/android/using-the-recyclerview
//https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView


public class EventActivityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private LinkedList commentsArray;
    PopupWindow commentsPopup;

    SimpleDateFormat formatter;
    Date currentTime;
    Context c;
    CommentAdapter commentAdapter;

    public EventActivityRecyclerAdapter(Context context, LinkedList events) {
        this.commentsArray = events;  //make own copy of the list so it can't be edited externaly
        c = context;
        formatter = new SimpleDateFormat("d-MM H:mm");
        currentTime = new Date(System.currentTimeMillis());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //THIS METHOD INFLATES THE ITEM LAYOUT AND CREATE THE VIEW HOLDER
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType)
        {
            case 1:
            View view1 = inflater.inflate(R.layout.item_activity_feed_event_no_image, parent, false);
            viewHolder = new ViewHolder1(view1, c);
            break;

            case 2:
            View view2 = inflater.inflate(R.layout.item_activity_feed_event_with_comment, parent, false);
            viewHolder = new ViewHolder2(view2);
            break;

            case 3:
            View view3 = inflater.inflate(R.layout.item_comment, parent, false);
            viewHolder = new ViewHolder3(view3);
            break;

            default:
            View v = inflater.inflate(R.layout.item_activity_feed_event_with_comment, parent, false);
            viewHolder = new ViewHolder2(v);
            break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(commentsArray.get(position) instanceof Event)
        {
            //if the object is an event
            Event event = (Event) commentsArray.get(position);
            //DETERMINES WHICH LAYOUT TO INFLATE FOR LIST ITEM
            if (event.photoPath.equals("no photo"))
            {
                //if event does not have a photo associated
                return 2;
            }
            else
            {
                //if the event does have a photo associated
                return 2;
            }
        }
        else if(commentsArray.get(position) instanceof Chat)
        {
            //if the object is a comment
            return 3;
        }
        else
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        //THIS METHOD SETS THE VIEW ATTRIBUTES BASED ON THE DATA PROVIDED

        //viewHolder.setIsRecyclable(false);
        switch(viewHolder.getItemViewType())
        {
            case 1: //EVENT NO PHOTO
            ViewHolder1 viewHolder1 = (ViewHolder1) viewHolder;
            configureViewHolder1(viewHolder1,position);
            break;

            case 2: //EVENT WITH PHOTO
            ViewHolder2 viewHolder2 = (ViewHolder2) viewHolder;
            configureViewHolder2(viewHolder2,position);
            break;

            case 3: //COMMENT
            ViewHolder3 viewHolder3 = (ViewHolder3) viewHolder;
            configureViewHolder3(viewHolder3, position);
            break;
        }
    }

    @Override
    public int getItemCount()
    {
        //THIS METHOD DETERMINES THE NUMBER OF ITEMS
        return commentsArray.size();
    }

    public void configureViewHolder1(ViewHolder1 viewHolder1, final int position)
    {
        //if the event DOES NOT have an associated image
        final Event event = (Event) commentsArray.get(position);

        //reset view elements
        viewHolder1.commentText.setText("");
        viewHolder1.likesLabel.setText("");
        viewHolder1.avatarBubble.setImageBitmap(null);
        viewHolder1.eventDateLabel.setText("");
        viewHolder1.eventTextLabel.setText("");
        viewHolder1.commentsCountLabel.setText("");

        viewHolder1.nameLabel1.setText(event.getCreatedByNickname());
        viewHolder1.eventDateLabel.setText(event.getDateCreated());
        viewHolder1.eventTextLabel.setText(event.getEventText());

        viewHolder1.commentButton.setVisibility(View.INVISIBLE);

        //update liked image as appropriate
        if(event.liked == true)
        {
            //if the event is liked but the current user
            viewHolder1.likeButton.setBackgroundResource(R.drawable.liked_icon);

        }
        else if(event.liked == false)
        {
            viewHolder1.likeButton.setBackgroundResource(R.drawable.like_icon);
        }

        if(event.likesArray.size()>0)
        {
            if(event.likesArray.size()>1)
            {
                //have more than one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName() + " and " + event.likesArray.size() + " others");
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder1.likesLabel.setText(stringBuilder);
            }
            else
            {
                //only one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName());
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder1.likesLabel.setText(stringBuilder);
            }
        }

        //load event avatar image
        if(!event.getCreatedById().equals("") && event.avatarImage!=null)
        {
            //load avatar image so long as the event properties are valid
            viewHolder1.avatarBubble.setImageBitmap(event.avatarImage);
        }

        //load last comment elements
        if(!event.lastComment.equals("")) {

            //load last comment elements
            String lastComment;
            if(event.lastComment.length() > 50)
            {
                lastComment = event.lastComment.substring(0,50) + "...";
            }
            else
            {
                lastComment = event.lastComment;
            }

            String name = event.lastCommentSender;
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder("● " + name + " " + lastComment);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            stringBuilder.setSpan(styleSpan,0,2 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder1.commentText.setText(stringBuilder);

            viewHolder1.commentsCountLabel.setText("View all " + event.commentsCount + " comments");
        }

        if(!event.lastComment.equals(""))
        {
            viewHolder1.commentsCountLabel.setText("View all " + event.commentsCount + " comments");
        }

        //ADD FUNCTIONALITY TO BUTTONS
        viewHolder1.nameLabel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder1.avatarBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder1.likeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation likeButtonAnimation = AnimationUtils.loadAnimation(c, R.anim.like_button_animation);
                v.startAnimation(likeButtonAnimation);

                //Event event = commentsArray.get(position);

                //DO SOMETHING WHEN LIKE BUTTON PRESSED
                if(event.liked == true)
                {
                    //unlike event
                    unlikeEvent(event.nodeID, MainActivity.currentUser.getUserID());
                    event.liked = false;
                }
                else if (event.liked == false)
                {
                    //like event
                    likeEvent(event);
                    event.liked = true;
                }
                notifyItemChanged(position);
            }
        });



    }

    public void configureViewHolder2(final ViewHolder2 viewHolder2, final int position)
    {
         final Event event = (Event) commentsArray.get(position);
         //if event DOES have an associated image

        //reset view elements
        viewHolder2.commentText.setText("");
        viewHolder2.likesLabel.setText("");
        viewHolder2.avatarBubble.setImageBitmap(null);
        viewHolder2.eventDateLabel.setText("");
        viewHolder2.eventTextLabel.setText("");
        viewHolder2.commentsCountLabel.setText("");

        viewHolder2.commentButton.setVisibility(View.INVISIBLE);

        viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
        viewHolder2.showResultsButton.setVisibility(View.VISIBLE);


        if(event.getImageBitmap()==null)
        {
            viewHolder2.resultsOverlay.setVisibility(View.VISIBLE);
            viewHolder2.showResultsButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            viewHolder2.eventImage.setImageBitmap(event.getImageBitmap());
        }


        viewHolder2.eventTextLabel.setText(event.getCreatedByNickname() + " " + event.getEventText() + " " + event.getUserBNickName());
        viewHolder2.nameLabel2.setText("@"+ event.getUserBNickName());
        viewHolder2.nameLabel1.setText(event.getCreatedByNickname());
        viewHolder2.eventDateLabel.setText(event.getDateCreated());

        viewHolder2.commentsCountLabel.setText( (commentsArray.size() -1) + " comments");

        if(event.likesArray.size()>0)
        {
            if(event.likesArray.size()>1)
            {
                //have more than one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName() + " and others");
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder2.likesLabel.setText(stringBuilder);
            }
            else
            {
                //only one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName());
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder2.likesLabel.setText(stringBuilder);
            }
        }

         //load event avatar image
         if(!event.getCreatedById().equals("") && event.avatarImage!=null)
         {
         //load avatar image so long as the event properties are valid
         viewHolder2.avatarBubble.setImageBitmap(event.avatarImage);
         }

         //update liked image as appropriate
         if(event.liked == true)
         {
            //if the event is liked by the current user
            viewHolder2.likeButton.setBackgroundResource(R.drawable.liked_icon);
         }
         if(event.liked == false)
         {
             viewHolder2.likeButton.setBackgroundResource(R.drawable.like_icon);
         }

         //ADD FUNCTIONALITY TO BUTTONS
         viewHolder2.nameLabel1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
        }
        });

         viewHolder2.nameLabel2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            goToProfileActivity(event.getUserBID(), event.getUserBNickName(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
        }
        });

         //populate event results elements
         viewHolder2.showResultsButton.setOnClickListener(new View.OnClickListener()
         {
         @Override
         public void onClick(View v)
         {
            //DO SOMETHING
            if(event.fight != null)
            {
                viewHolder2.resultsOverlay.setVisibility(View.VISIBLE);
                viewHolder2.showResultsButton.setVisibility(View.INVISIBLE);

         //populate with results

                if(event.fight.getDraw().equals("YES"))
                {
                 // set labels to show "Draw" instead of "WINNER" & "LOSER"
                    viewHolder2.winnerLabel.setText("Draw");
                    viewHolder2.loserLabel.setText("Draw");
                }

                viewHolder2.winnerNicknameLabel.setText(event.fight.getWinnerNickname());
                viewHolder2.loserNicknameLabel.setText(event.fight.getLoserNickname());
                viewHolder2.winnerSystemLabel.setText(""+event.fight.getWinnerFightSystem());
                viewHolder2.loserSystemLabel.setText(""+event.fight.getLoserFightSystem());
                viewHolder2.winnerPointsLabel.setText(event.fight.getWinnerTotalScore() + " pts");
                viewHolder2.loserPointsLabel.setText(""+event.fight.getLoserTotalScore() + " pts");

                viewHolder2.winnerHeadCuts.setText(""+event.fight.getWinnerHeadCutsScored());
                viewHolder2.winnerHeadThrusts.setText(""+event.fight.getWinnerHeadThrustsScored());
                viewHolder2.winnerTorsoCuts.setText(""+event.fight.getWinnerTorsoCutsScored());
                viewHolder2.winnerTorsoThrusts.setText(""+event.fight.getWinnerTorsoThrustsScored());
                viewHolder2.winnerLimbCuts.setText(""+event.fight.getWinnerLimbCutsScored());
                viewHolder2.winnerLimbThrusts.setText(""+event.fight.getWinnerLimbThrustsScored());

                viewHolder2.loserHeadCuts.setText(""+event.fight.getLoserHeadCutsScored());
                viewHolder2.loserHeadThrusts.setText(""+event.fight.getLoserHeadThrustsScored());
                viewHolder2.loserTorsoCuts.setText(""+event.fight.getLoserTorsoCutsScored());
                viewHolder2.loserTorsoThrusts.setText(""+event.fight.getLoserTorsoThrustsScored());
                viewHolder2.loserLimbCuts.setText(""+event.fight.getLoserLimbCutsScored());
                viewHolder2.loserLimbThrusts.setText(""+event.fight.getLoserLimbThrustsScored());

            }
         }
         });

         viewHolder2.resultsOverlay.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v)
             {
                 viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
                 viewHolder2.showResultsButton.setVisibility(View.VISIBLE);
             }
         });

         viewHolder2.avatarBubble.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
        }
        });

         viewHolder2.likeButton.setOnClickListener(new View.OnClickListener()
         {
             @Override
             public void onClick(View v)
             {
             Animation likeButtonAnimation = AnimationUtils.loadAnimation(c, R.anim.like_button_animation);
             v.startAnimation(likeButtonAnimation);

             Event event = (Event) commentsArray.get(position);

             //DO SOMETHING WHEN LIKE BUTTON PRESSED
             if(event.liked == true)
             {
                 //unlike event
                 unlikeEvent(event.nodeID, MainActivity.currentUser.getUserID());
                 event.liked = false;
             }
             else if (event.liked == false)
             {
                 //like event
                 likeEvent(event);
                 event.liked = true;
             }
             notifyItemChanged(position);
         }
         });
    }

    public void configureViewHolder3(final ViewHolder3 viewHolder3, final int position)
    {
        //if its just a comment

        //reset view elements

        viewHolder3.senderNameLabel.setText(null);
        viewHolder3.dataLabel.setText(null);
        viewHolder3.chatTextLabel.setText(null);
        viewHolder3.avatarBubble.setImageBitmap(null);

        //populate view elements
        Chat chat = (Chat) commentsArray.get(position);
        viewHolder3.senderNameLabel.setText(chat.getSender());
        viewHolder3.dataLabel.setText(chat.getDate());
        viewHolder3.chatTextLabel.setText(chat.getChatText());
        viewHolder3.avatarBubble.setImageBitmap(chat.avatarImage);
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder
    {

        Context context;

        TextView nameLabel1;
        TextView nameLabel2;
        TextView eventTextLabel;
        TextView eventDateLabel;
        ImageView avatarBubble;
        Button commentButton;
        Button likeButton;
        Button removeEventButton;
        TextView commentsCountLabel;
        TextView likesLabel;

        TextView commentText;

        public ViewHolder1(@NonNull View itemView, Context context)
        {
            super(itemView);
            this.context = context;
            nameLabel1 = itemView.findViewById(R.id.name_label_1);
            nameLabel2 = itemView.findViewById(R.id.name_label_2);
            eventTextLabel = itemView.findViewById(R.id.event_text_label);
            eventDateLabel = itemView.findViewById(R.id.event_date_label);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeButton = itemView.findViewById(R.id.like_button);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
            removeEventButton = itemView.findViewById(R.id.remove_event_button);
            commentsCountLabel = itemView.findViewById(R.id.comment_count_label);

            commentText = itemView.findViewById(R.id.comment_text);

            likesLabel = itemView.findViewById(R.id.likes_label);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder
    {
        //RESULTS EVENT WITH IMAGE

        TextView nameLabel1;
        TextView nameLabel2;
        TextView eventTextLabel;
        TextView eventDateLabel;
        ImageView eventImage;
        ImageView avatarBubble;
        Button commentButton;
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

        TextView commentText;

        TextView likesLabel;

        public ViewHolder2(@NonNull View itemView)
        {
            super(itemView);

            showResultsButton = itemView.findViewById(R.id.show_result_button);
            nameLabel1 = itemView.findViewById(R.id.name_label_1);
            nameLabel2 = itemView.findViewById(R.id.name_label_2);
            eventTextLabel = itemView.findViewById(R.id.event_text_label);
            eventDateLabel = itemView.findViewById(R.id.event_date_label);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeButton = itemView.findViewById(R.id.like_button);
            eventImage = itemView.findViewById(R.id.event_image);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
            removeEventButton = itemView.findViewById(R.id.remove_event_button);
            commentsCountLabel = itemView.findViewById(R.id.comment_count_label);

            resultsOverlay = itemView.findViewById(R.id.result_overlay);
            winnerLabel = itemView.findViewById(R.id.winner_label);
            loserLabel = itemView.findViewById(R.id.loser_label);
            winnerNicknameLabel = itemView.findViewById(R.id.winner_nickname_label);
            loserNicknameLabel = itemView.findViewById(R.id.loser_nickname_label);
            winnerSystemLabel = itemView.findViewById(R.id.winner_sytem_label);
            loserSystemLabel = itemView.findViewById(R.id.loser_system_label);
            winnerPointsLabel = itemView.findViewById(R.id.winner_points_label);
            loserPointsLabel = itemView.findViewById(R.id.loser_points_label);

            winnerHeadCuts = itemView.findViewById(R.id.winner_head_cuts);
            winnerHeadThrusts = itemView.findViewById(R.id.winner_head_thrusts);
            winnerTorsoCuts = itemView.findViewById(R.id.winner_torso_cuts);
            winnerTorsoThrusts = itemView.findViewById(R.id.winner_torso_thrusts);
            winnerLimbCuts = itemView.findViewById(R.id.winner_limb_cuts);
            winnerLimbThrusts = itemView.findViewById(R.id.winner_limb_thrusts);

            loserHeadCuts = itemView.findViewById(R.id.loser_head_cuts);
            loserHeadThrusts = itemView.findViewById(R.id.loser_head_thrusts);
            loserTorsoCuts = itemView.findViewById(R.id.loser_torso_cuts);
            loserTorsoThrusts = itemView.findViewById(R.id.loser_torso_thrusts);
            loserLimbCuts = itemView.findViewById(R.id.loser_limb_cuts);
            loserLimbThrusts = itemView.findViewById(R.id.loser_limb_thrusts);

            commentText = itemView.findViewById(R.id.comment_text);

            likesLabel = itemView.findViewById(R.id.likes_label);
        }
    }

    public class ViewHolder3 extends RecyclerView.ViewHolder
    {
        //COMMENT

        TextView senderNameLabel;
        TextView dataLabel;
        TextView chatTextLabel;
        ImageView avatarBubble;


        public ViewHolder3(@NonNull View itemView)
        {
            super(itemView);

            senderNameLabel = itemView.findViewById(R.id.sender_name_label);
            dataLabel = itemView.findViewById(R.id.date_label);
            chatTextLabel = itemView.findViewById(R.id.chat_text_label);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
        }
    }

    //###############  APPLICATION METHODS  #############

    public void likeEvent(Event event)
    {

        Like newLike = new Like();
        newLike.setEventID(event.getnodeID());
        newLike.setUserNickName(MainActivity.currentUser.getNickname());
        newLike.setUserID(MainActivity.currentUser.getUserID());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventLikesRef = database.getReference("EventLikes").child(event.getnodeID()).child(MainActivity.currentUser.getUserID());
        eventLikesRef.setValue(newLike);

        if(!event.createdById.equals(MainActivity.currentUser.getUserID()))
        {
            //if the event being liked is not created by the current user - created notification
            String notificationText;
            String userBNickName =  event.getCreatedByNickname();
            String userBID = event.getCreatedById();

            if(event.getPhotoPath().equals("no photo"))
            {
                //if the event is a text post
                if(event.eventText.length() > 50)
                {
                    //truncate string to 50 characters
                    String postText = event.eventText.substring(0,50);
                    notificationText = "likes your post: \"" + postText + "\"" + "...";
                    userBNickName = "";
                }
                else
                {
                    notificationText = "likes your post: \"" + event.eventText + "\"";
                    userBNickName = "";
                }
            }
            else
            {
                //if the event is a fight result event
                notificationText = "likes your fight with";
            }

            //create notification for associated user
            sendNotification(MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname(), userBID, userBNickName, notificationText, event.getnodeID());
        }
    }

    public void unlikeEvent(String nodeID, String userID)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventLikesRef = database.getReference("EventLikes").child(nodeID).child(userID);
        eventLikesRef.removeValue();
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
        //notification.setCreatedDate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notificationsRef = database.getReference("Notifications").child(userBID);
        notificationsRef.push().setValue(notification);
    }

    //on bind view holder is called when the view comes back into visibilityy
    //this is what is determining how the view is populated
}
