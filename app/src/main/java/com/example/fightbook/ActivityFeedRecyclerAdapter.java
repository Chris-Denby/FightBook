package com.example.fightbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedList;

//https://guides.codepath.com/android/using-the-recyclerview
//https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView


public class ActivityFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private LinkedList<Event> eventsArray;
    PopupWindow commentsPopup;
    SimpleDateFormat formatter;
    Date currentTime;
    Context c;
    CommentAdapter commentAdapter;

    public ActivityFeedRecyclerAdapter(Context context, LinkedList<Event> events) {
        this.eventsArray = events;  //make own copy of the list so it can't be edited externaly
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
                View view3 = inflater.inflate(R.layout.item_activity_feed_header, parent, false);
                viewHolder = new ViewHolder3(view3);
                break;

            case 4:
                View view4 = inflater.inflate(R.layout.item_activity_feed_event_no_comment, parent, false);
                viewHolder = new ViewHolder4(view4);
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
        //DETERMINES WHICH LAYOUT TO INFLATE FOR LIST ITEM
        if(eventsArray.get(position).isHeader == true)
        {
            //if the event is a header item for the recycler view
            return 3;
        }
        else
        {
            if (eventsArray.get(position).photoPath.equals("no photo"))
            {
                //if event does not have a photo associated
                return 1;
            }
            if(eventsArray.get(position).commentsCount == 0)
            {
                //event has a photo but no comments
                return 4;
            }
            else
            {
                //if the event does have a photo associated
                return 2;
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        //THIS METHOD SETS THE VIEW ATTRIBUTES BASED ON THE DATA PROVIDED

        //viewHolder.setIsRecyclable(false);
        switch(viewHolder.getItemViewType())
        {

            case 1: //NO PHOTO
            ViewHolder1 viewHolder1 = (ViewHolder1) viewHolder;
            configureViewHolder1(viewHolder1,position);
            break;

            case 2: //WITH PHOTO
            ViewHolder2 viewHolder2 = (ViewHolder2) viewHolder;
            configureViewHolder2(viewHolder2,position);
            break;

            case 3: //header
            ViewHolder3 viewHolder3 = (ViewHolder3) viewHolder;
            configureViewHolder3(viewHolder3, position);

            case 4: //event with photo but no comments
            ViewHolder4 viewHolder4 = (ViewHolder4) viewHolder;
            configureViewHolder4(viewHolder4, position);
            break;
        }
    }

    @Override
    public int getItemCount()
    {
        //THIS METHOD DETERMINES THE NUMBER OF ITEMS
        return eventsArray.size();
    }



    //############## VIEW HOLDERS #######################

    public void configureViewHolder1(ViewHolder1 viewHolder1, final int position)
    {
        //if the event DOES NOT have an associated image
        final Event event = eventsArray.get(position);

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
            if(event.lastComment.length() > 80)
            {
                lastComment = event.lastComment.substring(0,80) + "...";
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

            viewHolder1.commentsCountLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    goToEventActivity(event.getCreatedById(), event.getnodeID());
                }
            });
        }

        if(!event.lastComment.equals(""))
        {
            viewHolder1.commentsCountLabel.setText("View all " + event.commentsCount + " comments");
            viewHolder1.commentsCountLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    goToEventActivity(event.getCreatedById(), event.getnodeID());
                }
            });
        }

        //ADD FUNCTIONALITY TO BUTTONS
        viewHolder1.nameLabel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder1.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToEventActivity(event.getCreatedById(), event.getnodeID());
            }
        });

        viewHolder1.removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                removeEvent(event, event.getnodeID());
                Toast.makeText(c, "Post removed from activity feed", Toast.LENGTH_SHORT).show();
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
         final Event event = eventsArray.get(position);
         //if event DOES have an associated image

        //reset view elements
        viewHolder2.commentText.setText("");
        viewHolder2.likesLabel.setText("");
        viewHolder2.avatarBubble.setImageBitmap(null);
        viewHolder2.eventImage.setImageBitmap(null);
        viewHolder2.eventDateLabel.setText("");
        viewHolder2.eventTextLabel.setText("");
        viewHolder2.commentsCountLabel.setText("");

        viewHolder2.winnerLabel.setText("");
        viewHolder2.loserLabel.setText("");

        viewHolder2.winnerNicknameLabel.setText("");
        viewHolder2.loserNicknameLabel.setText("");
        viewHolder2.winnerSystemLabel.setText("");
        viewHolder2.loserSystemLabel.setText("");
        viewHolder2.winnerPointsLabel.setText("");
        viewHolder2.loserPointsLabel.setText("");

        viewHolder2.winnerHeadCuts.setText("");
        viewHolder2.winnerHeadThrusts.setText("");
        viewHolder2.winnerTorsoCuts.setText("");
        viewHolder2.winnerTorsoThrusts.setText("");
        viewHolder2.winnerLimbCuts.setText("");
        viewHolder2.winnerLimbThrusts.setText("");

        viewHolder2.loserHeadCuts.setText("");
        viewHolder2.loserHeadThrusts.setText("");
        viewHolder2.loserTorsoCuts.setText("");
        viewHolder2.loserTorsoThrusts.setText("");
        viewHolder2.loserLimbCuts.setText("");
        viewHolder2.loserLimbThrusts.setText("");

        viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
        viewHolder2.showResultsButton.setVisibility(View.INVISIBLE);


        //test if the event has a fight result
        if(event.fight == null)
        {
            viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
            viewHolder2.showResultsButton.setVisibility(View.INVISIBLE);
        }

        //if the event has a photo and a fight loaded
        if(event.fight != null && !event.getPhotoPath().equals(""))
        {
            //populate with results
            viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
            viewHolder2.showResultsButton.setVisibility(View.VISIBLE);

            viewHolder2.resultsOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    viewHolder2.resultsOverlay.setVisibility(View.INVISIBLE);
                    viewHolder2.showResultsButton.setVisibility(View.VISIBLE);
                }
            });

            viewHolder2.eventImage.setImageBitmap(event.getImageBitmap());

            if(event.fight.getDraw().equals("YES"))
            {
                // set labels to show "Draw" instead of "WINNER" & "LOSER"
                viewHolder2.winnerLabel.setText("Draw");
                viewHolder2.loserLabel.setText("Draw");
            }
            else
            {
                viewHolder2.winnerLabel.setText("Winner");
                viewHolder2.loserLabel.setText("Loser");
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

        //if the event has no photo and a fight loaded
        if(event.fight != null && event.getPhotoPath().equals(""))
        {
            //populate with results
            viewHolder2.resultsOverlay.setVisibility(View.VISIBLE);
            viewHolder2.showResultsButton.setVisibility(View.INVISIBLE);
            viewHolder2.resultsOverlay.setOnClickListener(null);
            viewHolder2.showResultsButton.setOnClickListener(null);

            if(event.fight.getDraw().equals("YES"))
            {
                // set labels to show "Draw" instead of "WINNER" & "LOSER"
                viewHolder2.winnerLabel.setText("Draw");
                viewHolder2.loserLabel.setText("Draw");
            }
            else
            {
                viewHolder2.winnerLabel.setText("Winner");
                viewHolder2.loserLabel.setText("Loser");
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


         viewHolder2.eventTextLabel.setText(event.getCreatedByNickname() + " " + event.getEventText() + " " + event.getUserBNickName());
         viewHolder2.nameLabel2.setText("@"+ event.getUserBNickName());
         viewHolder2.nameLabel1.setText(event.getCreatedByNickname());
         viewHolder2.eventDateLabel.setText(event.getDateCreated());

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

        //load last comment elements
        if(!event.lastComment.equals("")) {

            //load last comment elements
            String lastComment;
            if(event.lastComment.length() > 80)
            {
                lastComment = event.lastComment.substring(0,80) + "...";
            }
            else
            {
                lastComment = event.lastComment;
            }

            String name = event.lastCommentSender;
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder("● " + name + " " + lastComment);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            stringBuilder.setSpan(styleSpan,0,2 + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder2.commentText.setText(stringBuilder);

            viewHolder2.commentsCountLabel.setText("View all " + event.commentsCount + " comments");
            viewHolder2.commentsCountLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    goToEventActivity(event.getCreatedById(), event.getnodeID());
                }
            });
        }

         if(!event.lastComment.equals(""))
         {
            viewHolder2.commentsCountLabel.setText("View all " + event.commentsCount + " comments");
            viewHolder2.commentsCountLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToEventActivity(event.getCreatedById(), event.getnodeID());
            }
            });
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

         viewHolder2.commentButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            goToEventActivity(event.getCreatedById(), event.getnodeID());
        }
        });

         viewHolder2.removeEventButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            removeEvent(event, event.getnodeID());
            Toast.makeText(c, "Toast removed from activity feed", Toast.LENGTH_SHORT).show();
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

             Event event = eventsArray.get(position);

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
        final Event event = eventsArray.get(position);

        viewHolder3.avatarBubble.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder3.icon1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        viewHolder3.icon2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        viewHolder3.icon3.setScaleType(ImageView.ScaleType.FIT_CENTER);
        viewHolder3.icon4.setScaleType(ImageView.ScaleType.FIT_CENTER);
        viewHolder3.icon5.setScaleType(ImageView.ScaleType.FIT_CENTER);
        viewHolder3.icon6.setScaleType(ImageView.ScaleType.FIT_CENTER);

        viewHolder3.icon1.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.follow_icon));
        viewHolder3.icon2.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.challenge_icon));
        viewHolder3.icon3.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.create_post_icon));
        viewHolder3.icon4.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.liked_icon));
        viewHolder3.icon5.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.comment_icon));
        viewHolder3.icon6.setImageBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.pie_icon));

        //set on click listeners here
        viewHolder3.findFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.pager.setCurrentItem(2, true);
            }
        });

    }

    public void configureViewHolder4(final ViewHolder4 viewHolder4, final int position)
    {
        final Event event = eventsArray.get(position);
        //if event DOES have an associated image but no comments

        //reset view elements
        viewHolder4.likesLabel.setText("");
        viewHolder4.avatarBubble.setImageBitmap(null);
        viewHolder4.eventImage.setImageBitmap(null);
        viewHolder4.eventDateLabel.setText("");
        viewHolder4.eventTextLabel.setText("");

        viewHolder4.winnerLabel.setText("");
        viewHolder4.loserLabel.setText("");

        viewHolder4.winnerNicknameLabel.setText("");
        viewHolder4.loserNicknameLabel.setText("");
        viewHolder4.winnerSystemLabel.setText("");
        viewHolder4.loserSystemLabel.setText("");
        viewHolder4.winnerPointsLabel.setText("");
        viewHolder4.loserPointsLabel.setText("");

        viewHolder4.winnerHeadCuts.setText("");
        viewHolder4.winnerHeadThrusts.setText("");
        viewHolder4.winnerTorsoCuts.setText("");
        viewHolder4.winnerTorsoThrusts.setText("");
        viewHolder4.winnerLimbCuts.setText("");
        viewHolder4.winnerLimbThrusts.setText("");

        viewHolder4.loserHeadCuts.setText("");
        viewHolder4.loserHeadThrusts.setText("");
        viewHolder4.loserTorsoCuts.setText("");
        viewHolder4.loserTorsoThrusts.setText("");
        viewHolder4.loserLimbCuts.setText("");
        viewHolder4.loserLimbThrusts.setText("");

        viewHolder4.resultsOverlay.setVisibility(View.INVISIBLE);
        viewHolder4.showResultsButton.setVisibility(View.INVISIBLE);


        //test if the event has a fight result
        if(event.fight == null)
        {
            viewHolder4.resultsOverlay.setVisibility(View.INVISIBLE);
            viewHolder4.showResultsButton.setVisibility(View.INVISIBLE);
        }

        //if the event has a photo and a fight loaded
        if(event.fight != null && !event.getPhotoPath().equals(""))
        {
            //populate with results
            viewHolder4.resultsOverlay.setVisibility(View.INVISIBLE);
            viewHolder4.showResultsButton.setVisibility(View.VISIBLE);

            viewHolder4.resultsOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    viewHolder4.resultsOverlay.setVisibility(View.INVISIBLE);
                    viewHolder4.showResultsButton.setVisibility(View.VISIBLE);
                }
            });

            viewHolder4.eventImage.setImageBitmap(event.getImageBitmap());

            if(event.fight.getDraw().equals("YES"))
            {
                // set labels to show "Draw" instead of "WINNER" & "LOSER"
                viewHolder4.winnerLabel.setText("Draw");
                viewHolder4.loserLabel.setText("Draw");
            }
            else
            {
                viewHolder4.winnerLabel.setText("Winner");
                viewHolder4.loserLabel.setText("Loser");
            }

            viewHolder4.winnerNicknameLabel.setText(event.fight.getWinnerNickname());
            viewHolder4.loserNicknameLabel.setText(event.fight.getLoserNickname());
            viewHolder4.winnerSystemLabel.setText(""+event.fight.getWinnerFightSystem());
            viewHolder4.loserSystemLabel.setText(""+event.fight.getLoserFightSystem());
            viewHolder4.winnerPointsLabel.setText(event.fight.getWinnerTotalScore() + " pts");
            viewHolder4.loserPointsLabel.setText(""+event.fight.getLoserTotalScore() + " pts");

            viewHolder4.winnerHeadCuts.setText(""+event.fight.getWinnerHeadCutsScored());
            viewHolder4.winnerHeadThrusts.setText(""+event.fight.getWinnerHeadThrustsScored());
            viewHolder4.winnerTorsoCuts.setText(""+event.fight.getWinnerTorsoCutsScored());
            viewHolder4.winnerTorsoThrusts.setText(""+event.fight.getWinnerTorsoThrustsScored());
            viewHolder4.winnerLimbCuts.setText(""+event.fight.getWinnerLimbCutsScored());
            viewHolder4.winnerLimbThrusts.setText(""+event.fight.getWinnerLimbThrustsScored());

            viewHolder4.loserHeadCuts.setText(""+event.fight.getLoserHeadCutsScored());
            viewHolder4.loserHeadThrusts.setText(""+event.fight.getLoserHeadThrustsScored());
            viewHolder4.loserTorsoCuts.setText(""+event.fight.getLoserTorsoCutsScored());
            viewHolder4.loserTorsoThrusts.setText(""+event.fight.getLoserTorsoThrustsScored());
            viewHolder4.loserLimbCuts.setText(""+event.fight.getLoserLimbCutsScored());
            viewHolder4.loserLimbThrusts.setText(""+event.fight.getLoserLimbThrustsScored());
        }

        //if the event has no photo and a fight loaded
        if(event.fight != null && event.getPhotoPath().equals(""))
        {
            //populate with results
            viewHolder4.resultsOverlay.setVisibility(View.VISIBLE);
            viewHolder4.showResultsButton.setVisibility(View.INVISIBLE);
            viewHolder4.resultsOverlay.setOnClickListener(null);
            viewHolder4.showResultsButton.setOnClickListener(null);

            if(event.fight.getDraw().equals("YES"))
            {
                // set labels to show "Draw" instead of "WINNER" & "LOSER"
                viewHolder4.winnerLabel.setText("Draw");
                viewHolder4.loserLabel.setText("Draw");
            }
            else
            {
                viewHolder4.winnerLabel.setText("Winner");
                viewHolder4.loserLabel.setText("Loser");
            }

            viewHolder4.winnerNicknameLabel.setText(event.fight.getWinnerNickname());
            viewHolder4.loserNicknameLabel.setText(event.fight.getLoserNickname());
            viewHolder4.winnerSystemLabel.setText(""+event.fight.getWinnerFightSystem());
            viewHolder4.loserSystemLabel.setText(""+event.fight.getLoserFightSystem());
            viewHolder4.winnerPointsLabel.setText(event.fight.getWinnerTotalScore() + " pts");
            viewHolder4.loserPointsLabel.setText(""+event.fight.getLoserTotalScore() + " pts");

            viewHolder4.winnerHeadCuts.setText(""+event.fight.getWinnerHeadCutsScored());
            viewHolder4.winnerHeadThrusts.setText(""+event.fight.getWinnerHeadThrustsScored());
            viewHolder4.winnerTorsoCuts.setText(""+event.fight.getWinnerTorsoCutsScored());
            viewHolder4.winnerTorsoThrusts.setText(""+event.fight.getWinnerTorsoThrustsScored());
            viewHolder4.winnerLimbCuts.setText(""+event.fight.getWinnerLimbCutsScored());
            viewHolder4.winnerLimbThrusts.setText(""+event.fight.getWinnerLimbThrustsScored());

            viewHolder4.loserHeadCuts.setText(""+event.fight.getLoserHeadCutsScored());
            viewHolder4.loserHeadThrusts.setText(""+event.fight.getLoserHeadThrustsScored());
            viewHolder4.loserTorsoCuts.setText(""+event.fight.getLoserTorsoCutsScored());
            viewHolder4.loserTorsoThrusts.setText(""+event.fight.getLoserTorsoThrustsScored());
            viewHolder4.loserLimbCuts.setText(""+event.fight.getLoserLimbCutsScored());
            viewHolder4.loserLimbThrusts.setText(""+event.fight.getLoserLimbThrustsScored());
        }


        viewHolder4.eventTextLabel.setText(event.getCreatedByNickname() + " " + event.getEventText() + " " + event.getUserBNickName());
        viewHolder4.nameLabel2.setText("@"+ event.getUserBNickName());
        viewHolder4.nameLabel1.setText(event.getCreatedByNickname());
        viewHolder4.eventDateLabel.setText(event.getDateCreated());

        if(event.likesArray.size()>0)
        {
            if(event.likesArray.size()>1)
            {
                //have more than one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName() + " and others");
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder4.likesLabel.setText(stringBuilder);
            }
            else
            {
                //only one like
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder("Liked by " + event.likesArray.get(0).getUserNickName());
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                stringBuilder.setSpan(styleSpan, 9, 9 + event.likesArray.get(0).getUserNickName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder4.likesLabel.setText(stringBuilder);
            }
        }

        //load event avatar image
        if(!event.getCreatedById().equals("") && event.avatarImage!=null)
        {
            //load avatar image so long as the event properties are valid
            viewHolder4.avatarBubble.setImageBitmap(event.avatarImage);
        }

        //update liked image as appropriate
        if(event.liked == true)
        {
            //if the event is liked by the current user
            viewHolder4.likeButton.setBackgroundResource(R.drawable.liked_icon);
        }
        if(event.liked == false)
        {
            viewHolder4.likeButton.setBackgroundResource(R.drawable.like_icon);
        }

        //ADD FUNCTIONALITY TO BUTTONS
        viewHolder4.nameLabel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder4.nameLabel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getUserBID(), event.getUserBNickName(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        //populate event results elements
        viewHolder4.showResultsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //DO SOMETHING
                if(event.fight != null)
                {
                    viewHolder4.resultsOverlay.setVisibility(View.VISIBLE);
                    viewHolder4.showResultsButton.setVisibility(View.INVISIBLE);

                    //populate with results

                    if(event.fight.getDraw().equals("YES"))
                    {
                        // set labels to show "Draw" instead of "WINNER" & "LOSER"
                        viewHolder4.winnerLabel.setText("Draw");
                        viewHolder4.loserLabel.setText("Draw");
                    }

                    viewHolder4.winnerNicknameLabel.setText(event.fight.getWinnerNickname());
                    viewHolder4.loserNicknameLabel.setText(event.fight.getLoserNickname());
                    viewHolder4.winnerSystemLabel.setText(""+event.fight.getWinnerFightSystem());
                    viewHolder4.loserSystemLabel.setText(""+event.fight.getLoserFightSystem());
                    viewHolder4.winnerPointsLabel.setText(event.fight.getWinnerTotalScore() + " pts");
                    viewHolder4.loserPointsLabel.setText(""+event.fight.getLoserTotalScore() + " pts");

                    viewHolder4.winnerHeadCuts.setText(""+event.fight.getWinnerHeadCutsScored());
                    viewHolder4.winnerHeadThrusts.setText(""+event.fight.getWinnerHeadThrustsScored());
                    viewHolder4.winnerTorsoCuts.setText(""+event.fight.getWinnerTorsoCutsScored());
                    viewHolder4.winnerTorsoThrusts.setText(""+event.fight.getWinnerTorsoThrustsScored());
                    viewHolder4.winnerLimbCuts.setText(""+event.fight.getWinnerLimbCutsScored());
                    viewHolder4.winnerLimbThrusts.setText(""+event.fight.getWinnerLimbThrustsScored());

                    viewHolder4.loserHeadCuts.setText(""+event.fight.getLoserHeadCutsScored());
                    viewHolder4.loserHeadThrusts.setText(""+event.fight.getLoserHeadThrustsScored());
                    viewHolder4.loserTorsoCuts.setText(""+event.fight.getLoserTorsoCutsScored());
                    viewHolder4.loserTorsoThrusts.setText(""+event.fight.getLoserTorsoThrustsScored());
                    viewHolder4.loserLimbCuts.setText(""+event.fight.getLoserLimbCutsScored());
                    viewHolder4.loserLimbThrusts.setText(""+event.fight.getLoserLimbThrustsScored());

                }
            }
        });

        viewHolder4.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToEventActivity(event.getCreatedById(), event.getnodeID());
            }
        });

        viewHolder4.removeEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                removeEvent(event, event.getnodeID());
                Toast.makeText(c, "Toast removed from activity feed", Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder4.avatarBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(event.getCreatedById(), event.getCreatedByNickname(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder4.likeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation likeButtonAnimation = AnimationUtils.loadAnimation(c, R.anim.like_button_animation);
                v.startAnimation(likeButtonAnimation);

                Event event = eventsArray.get(position);

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
        //header
        TextView eventTextLabel;
        ImageView avatarBubble;
        TextView commentText;
        ImageView icon1;
        ImageView icon2;
        ImageView icon3;
        ImageView icon4;
        ImageView icon5;
        ImageView icon6;
        Button findFriendsButton;

        public ViewHolder3(@NonNull View itemView)
        {
            super(itemView);

            eventTextLabel = itemView.findViewById(R.id.event_text_label);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
            commentText = itemView.findViewById(R.id.comment_text);

            icon1 = itemView.findViewById(R.id.icon_1);
            icon2 = itemView.findViewById(R.id.icon_2);
            icon3 = itemView.findViewById(R.id.icon_3);
            icon4 = itemView.findViewById(R.id.icon_4);
            icon5 = itemView.findViewById(R.id.icon_5);
            icon6 = itemView.findViewById(R.id.icon_6);

            findFriendsButton = itemView.findViewById(R.id.find_friends_button);
        }
    }

    public class ViewHolder4 extends RecyclerView.ViewHolder
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

        TextView likesLabel;

        public ViewHolder4(@NonNull View itemView)
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

            likesLabel = itemView.findViewById(R.id.likes_label);

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
                if(event.eventText.length() > 80)
                {
                    //truncate string to 50 characters
                    String postText = event.eventText.substring(0,80);
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

    public void removeEvent(Event event, String nodeID)
    {
        try
        {
            //remove event node for current user - but leave for the other user
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            //DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(currentUser.getUid()).child(nodeID);
            //eventRef.removeValue();
            eventsArray.remove(eventsArray.indexOf(event));
            DatabaseReference blockedEventsRef = FirebaseDatabase.getInstance().getReference("Blocked Events").child(currentUser.getUid()).child(nodeID);
            blockedEventsRef.setValue(nodeID);
            notifyDataSetChanged();
            Toast.makeText(c, "Blocked event", Toast.LENGTH_SHORT).show();
        }
        catch(ConcurrentModificationException e)
        {
            e.printStackTrace();
        }
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

    public void goToEventActivity(final String userID, final String nodeID)
    {
        /**
         DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Events").child(userID).child(nodeID);
         eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        final Event selectedEvent = dataSnapshot.getValue(Event.class);

        DatabaseReference fightRef = FirebaseDatabase.getInstance().getReference("Fights").child(userID).child(nodeID);
        fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        Fight fight = dataSnapshot.getValue(Fight.class);
        if(selectedEvent != null)
        {
        selectedEvent.fight = fight;
        Intent intent = new Intent(c, EventActivity.class);
        Event e = new Event();
        intent.putExtra("Event", e);
        c.startActivity(intent);
        }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {
        Toast.makeText(c, "Failed to retrieve event - please try again", Toast.LENGTH_SHORT).show();
        }
        });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError)
        {
        Toast.makeText(c, "Failed to retrieve event - please try again", Toast.LENGTH_SHORT).show();
        }
        });
         **/

        Intent intent = new Intent(c, EventActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("SELECTED_USER_ID", userID);
        bundle.putString("SELECTED_NODE_ID", nodeID);
        intent.putExtras(bundle);

        c.startActivity(intent);
    }

    //on bind view holder is called when the view comes back into visibilityy
    //this is what is determining how the view is populated
}
