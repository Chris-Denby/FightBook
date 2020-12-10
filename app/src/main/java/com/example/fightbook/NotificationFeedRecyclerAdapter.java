package com.example.fightbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

//https://guides.codepath.com/android/using-the-recyclerview
//https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView


public class NotificationFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private LinkedList<Object> notificationsArray;
    PopupWindow commentsPopup;
    SimpleDateFormat formatter;
    Date currentTime;
    Context c;
    ChatAdapter adapter;
    PopupWindow chatPopup;

    public NotificationFeedRecyclerAdapter(Context context, LinkedList notifications)
    {

        notificationsArray =  notifications;  //make own copy of the list so it can't be edited externaly
        //notificationsArray.add(new ChallengeToken("123", "123", "TestUser", "TestUser"));
        Notification notif = new Notification("Test notification");
        //notificationsArray.add(notif);

        c = context;
        formatter = new SimpleDateFormat("d-MM H:mm");
        currentTime = new Date(System.currentTimeMillis());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //THIS METHOD INFLATES THE ITEM LAYOUT AND CREATE THE VIEW HOLDER
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        switch (viewType)
        {
            case 1:
            //object is a challenge
            View view1 = inflater.inflate(R.layout.item_challenge, parent, false);
            viewHolder = new ViewHolder1(view1);
            break;

            case 2:
            //object is a notification
            View view2 = inflater.inflate(R.layout.item_notification, parent, false);
            viewHolder = new ViewHolder2(view2);
            break;

            default:
            View v = inflater.inflate(R.layout.item_challenge, parent, false);
            viewHolder = new ViewHolder2(v);
            break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position)
    {
        //DETERMINES WHICH LAYOUT TO INFLATE FOR LIST ITEM
        if (notificationsArray.get(position) instanceof ChallengeToken)
        {
            //if the object is a challenge
            return 1;
        }
        else if (notificationsArray.get(position)instanceof Notification)
        {
            //if object is a notification
            return 2;
        }
        else
        {
            return 0;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        //THIS METHOD SETS THE VIEW ATTRIBUTES BASED ON THE DATA PROVIDED

        switch(viewHolder.getItemViewType())
        {
            case 1: //CHALLENGE TOKEN
            ViewHolder1 viewHolder1 = (ViewHolder1) viewHolder;
            configureViewHolder1(viewHolder1,position);
            break;

            case 2: //EVENT NOTIFICATION
            ViewHolder2 viewHolder2 = (ViewHolder2) viewHolder;
            configureViewHolder2(viewHolder2,position);
            break;

            default:
            ViewHolder1 viewHolder0 = (ViewHolder1) viewHolder;
            configureViewHolder1(viewHolder0, position);
            break;
        }
    }

    @Override
    public int getItemCount()
    {
        //THIS METHOD DETERMINES THE NUMBER OF ITEMS
        return notificationsArray.size();
    }

    public void configureViewHolder1(final ViewHolder1 viewHolder1, final int position)
    {
        //Configure view holder for challenge token
        final ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        SpannableStringBuilder stringBuilder;

        //reset views of challenge on recycle
        viewHolder1.actionButton.setVisibility(View.INVISIBLE);

        //set date created
        viewHolder1.eventDateLabel.setText(token.getDateCreated());

        try
        {
            if (token.getIssuerID().equals(Constants.getInstance().currentUser.getUserID()))
            {
                //token is issued by current user
                switch (token.getTokenStatus()) {
                    case "REJECTED":
                        //if an issued challenge that has been rejected
                        stringBuilder = new SpannableStringBuilder(token.getReceiverNickname() + "has rejected your challenge");
                        stringBuilder.setSpan(styleSpan, 0, token.getReceiverNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_rejected_icon);
                        viewHolder1.actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                        viewHolder1.rejectButton.setText("REMOVE");
                        //add listener to button to remove challenge
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //remove received challenge from database
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getIssuerID()).child("IssuedChallenges").child(token.getReceiverID());
                                issuerRef.removeValue();
                                notificationsArray.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;

                    case "ACCEPTED":
                        //if an issued challenge that is accepted
                        stringBuilder = new SpannableStringBuilder(token.getReceiverNickname() + " has accepted your challenge");
                        stringBuilder.setSpan(styleSpan, 0, token.getReceiverNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_go_icon);
                        viewHolder1.actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Animation challengeIconAnimation = AnimationUtils.loadAnimation(c, R.anim.challenge_icon_animation);
                                v.startAnimation(challengeIconAnimation);
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                beginFight(token);
                            }
                        });
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //remove received challenge from database
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                revokeChallenge(token, position);
                            }
                        });
                        break;

                    case "ISSUED":
                        //if an issued challenge that is pending
                        stringBuilder = new SpannableStringBuilder("Challenge issued to " + token.getReceiverNickname());
                        stringBuilder.setSpan(styleSpan, (stringBuilder.length() - token.getReceiverNickname().length()), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_awaiting_icon);
                        viewHolder1.actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(c, "Awaiting response from " + token.getReceiverNickname(), Toast.LENGTH_SHORT).show();

                            }
                        });
                        viewHolder1.rejectButton.setText("CANCEL");
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //remove received challenge from database
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                revokeChallenge(token, position);
                            }
                        });
                        break;

                    case "COMPLETED":
                        //if an issued challenge that has been completed
                        stringBuilder = new SpannableStringBuilder("Completed challenge with " + token.getReceiverNickname());
                        stringBuilder.setSpan(styleSpan, (stringBuilder.length() - token.getReceiverNickname().length()), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.rejectButton.setText("REMOVE");
                        viewHolder1.actionButton.setVisibility(View.INVISIBLE);
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getIssuerID()).child("IssuedChallenges").child(token.getReceiverID());
                                //delete the node (rejected received challenge)
                                issuerRef.removeValue();
                                notificationsArray.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;
                }

                viewHolder1.chatButton.setText("Chat (" + token.chatCount + ")");

                viewHolder1.chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String receiverNN;
                        if (token.getIssuerNickname().equals(MainActivity.currentUser.getNickname())) {
                            //if the issuer is the current user
                            //use the senders name
                            receiverNN = token.getReceiverNickname();

                        } else {
                            //else if the issuer is not hte current user
                            receiverNN = MainActivity.currentUser.getNickname();
                        }
                        showChatPopup(v, token.getIssuerNickname(), receiverNN, token.getChatID(), token.chatArray);
                    }

                });
            } else //token is not issued by current user
            {
                switch (token.getTokenStatus()) {
                    case "ACCEPTED":
                        //if a received challenge that has been accepted
                        stringBuilder = new SpannableStringBuilder("Accepted challenge from " + token.getIssuerNickname());
                        stringBuilder.setSpan(styleSpan, (stringBuilder.length() - token.getIssuerNickname().length()), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_accepted_icon);
                        viewHolder1.actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {//do nothing

                            }
                        });
                        viewHolder1.rejectButton.setText("REJECT");
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                rejectChallenge(token, position);
                            }
                        });
                        break;

                    case "ISSUED":
                        //if a received challenge that is pending
                        stringBuilder = new SpannableStringBuilder("Challenge received from " + token.getIssuerNickname());
                        stringBuilder.setSpan(styleSpan, 24, stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.rejectButton.setText("REJECT");
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_toaccept_icon);
                        viewHolder1.actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Animation challengeIconAnimation = AnimationUtils.loadAnimation(c, R.anim.challenge_icon_animation);
                                v.startAnimation(challengeIconAnimation);
                                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                acceptChallenge(token);
                                viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_accepted_icon);
                            }
                        });
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                rejectChallenge(token, position);
                            }
                        });
                        break;

                    case "REVOKED":
                        //if received challenge that is revoked
                        stringBuilder = new SpannableStringBuilder(token.getIssuerNickname() + " has cancelled their challenge");
                        stringBuilder.setSpan(styleSpan, 0, token.getIssuerNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.rejectButton.setText("REMOVE");
                        viewHolder1.actionButton.setVisibility(View.VISIBLE);
                        viewHolder1.actionButton.setBackgroundResource(R.drawable.challenge_rejected_icon);
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getReceiverID()).child("ReceivedChallenges").child(token.getIssuerID());
                                //delete the node (rejected received challenge)
                                receiverRef.setValue(token);
                                receiverRef.removeValue();
                                notificationsArray.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;

                    case "COMPLETED":
                        //if received challenge that is revoked
                        stringBuilder = new SpannableStringBuilder("Completed challenge with " + token.getIssuerNickname());
                        stringBuilder.setSpan(styleSpan, (stringBuilder.length() - token.getIssuerNickname().length()), stringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        viewHolder1.textLabel.setText(stringBuilder);

                        viewHolder1.rejectButton.setVisibility(View.VISIBLE);
                        viewHolder1.rejectButton.setText("REMOVE");
                        viewHolder1.actionButton.setVisibility(View.INVISIBLE);
                        viewHolder1.rejectButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChallengeToken token = (ChallengeToken) notificationsArray.get(position);
                                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getReceiverID()).child("ReceivedChallenges").child(token.getIssuerID());
                                //delete the node (rejected received challenge)
                                receiverRef.removeValue();
                                notificationsArray.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;
                }
                viewHolder1.chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChatPopup(v, token.getIssuerNickname(), token.getReceiverNickname(), token.getChatID(), token.chatArray);
                    }
                });
            }
        }
        catch(Exception e)
        {
            //Toast.makeText(c, "Unable to connect - try restarting the app", Toast.LENGTH_SHORT).show();
        }


        viewHolder1.avatarBubble.setImageBitmap(token.avatarImage);

    }

    public void configureViewHolder2(final ViewHolder2 viewHolder2, final int position)
    {
        //Configure view holder for challenge token
        final Notification notification = (Notification) notificationsArray.get(position);


        SpannableStringBuilder stringBuilder;
        String userBNickName;
        String notificationText;

        //reset view holder views on recycler
        viewHolder2.avatarBubble.setImageBitmap(null);
        viewHolder2.textLabelA.setText("");
        viewHolder2.parent.setBackgroundColor(Color.WHITE);

        //set date created
        viewHolder2.eventDateLabel.setText(notification.getCreatedDate());


        //if the notification is newly loaded - highlight in blue
        if(notification.isNew.equals("true"))
        {
            viewHolder2.parent.setBackgroundColor(Color.parseColor("#1A33B5E5"));
        }


        if(notification.getUserBNickname().equals(""))
        {
            //if the notification does NOT have a secondary user associated
            //build string to style the name in bold
            stringBuilder = new SpannableStringBuilder(notification.getIssuerNickname() + " " + notification.getText());
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            stringBuilder.setSpan(styleSpan,0,notification.getIssuerNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else
        {
            //if the notification DOES have a secondary user associated
            //build string to style BOTH names in bold
            if(notification.getUserBID().equals(Constants.getInstance().currentUser.getUserID()))
            {
                //if the current user is user B associated with the post
                userBNickName = "";
                notificationText = notification.getText();

                if(notification.getText().equals("likes your fight with"))
                {
                    //if the notifcation refers to a fight the current user had - correct text to make sense
                    userBNickName = "them";
                    notificationText = "likes your fight with";
                }
            }
            else
            {
                //if user B is another user
                userBNickName = notification.getUserBNickname();
                notificationText = notification.getText();
            }
            stringBuilder = new SpannableStringBuilder(notification.getIssuerNickname() + " " + notificationText + " " + userBNickName);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            stringBuilder.setSpan(styleSpan,0,notification.getIssuerNickname().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //set views
        viewHolder2.textLabelA.setText(stringBuilder);
        viewHolder2.avatarBubble.setImageBitmap(notification.avatarImage);



        viewHolder2.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String userID = ((Notification) notificationsArray.get(position)).getIssuerID();
                String nodeID = ((Notification) notificationsArray.get(position)).getNodeID();
                String notifKey = ((Notification) notificationsArray.get(position)).getNotificationKey();

                //mark notification as not being new
                if(notification.isNew.equals("true"))
                {
                    notification.setIsNew("false");
                    DatabaseReference receivedRef = FirebaseDatabase.getInstance().getReference("Notifications").child(Constants.getInstance().currentUser.getUserID());
                    receivedRef.child(notifKey).setValue(notification);
                    viewHolder2.parent.setBackgroundColor(Color.WHITE);
                    MainActivity.decrementNotificationBubble();
                }

                if(notification.getText().equals("is now following you") || notification.getText().equals("has joined in your area"))
                {
                    goToProfileActivity(notification.getIssuerID(), notification.getIssuerNickname(), Constants.getInstance().currentUser.getUserID(), Constants.getInstance().currentUser.getNickname());
                }
                else
                {
                    goToEventActivity(userID, nodeID);
                }

            }
        });
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder
    {
        //VIEW HOLDER FOR CHALLENGE TOKEN
        Context context;

        TextView textLabel;
        TextView eventDateLabel;
        Button rejectButton;
        Button actionButton;
        Button chatButton;
        ImageView avatarBubble;

        public ViewHolder1(@NonNull View itemView)
        {
            super(itemView);
            this.context = context;
            chatButton = itemView.findViewById(R.id.chatButton);
            //generic layout elements
            textLabel = (TextView) itemView.findViewById(R.id.event_text_label);
            eventDateLabel = (TextView) itemView.findViewById(R.id.event_date_label);
            rejectButton = itemView.findViewById(R.id.reject_button);
            actionButton = itemView.findViewById(R.id.action_button);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder
    {
        //VIEW HOLDER FOR NOTIFICATION
        Context context;

        TextView textLabelA;
        TextView eventDateLabel;
        ImageView avatarBubble;
        View parent;

        public ViewHolder2(@NonNull View itemView)
        {
            super(itemView);
            this.context = context;
            //generic layout elements
            textLabelA = (TextView) itemView.findViewById(R.id.event_text_label);
            eventDateLabel = (TextView) itemView.findViewById(R.id.event_date_label);
            avatarBubble = itemView.findViewById(R.id.avatarBubble);
            parent = itemView.findViewById(R.id.parent);
        }
    }


    //###############  APPLICATION METHODS  #############

    public void acceptChallenge(ChallengeToken token)
    {
        token.setTokenStatus("ACCEPTED");
        //update status of tokens in database
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getReceiverID()).child("ReceivedChallenges").child(token.getIssuerID());
        DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getIssuerID()).child("IssuedChallenges").child(token.getReceiverID());
        receiverRef.setValue(token);
        issuerRef.setValue(token);
        //notifyDataSetChanged();
    }

    public void rejectChallenge(ChallengeToken token, int position)
    {
        token.setTokenStatus("REJECTED");
        //update status of tokens in database
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getReceiverID()).child("ReceivedChallenges").child(token.getIssuerID());
        DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getIssuerID()).child("IssuedChallenges").child(token.getReceiverID());
        //delete the node (rejected received challenge)
        receiverRef.removeValue();
        issuerRef.setValue(token);
        notificationsArray.remove(position);
        notifyDataSetChanged();
    }

    public void revokeChallenge(ChallengeToken token, int position)
    {
        token.setTokenStatus("REVOKED");
        //update status of tokens in database
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getReceiverID()).child("ReceivedChallenges").child(token.getIssuerID());
        DatabaseReference issuerRef = FirebaseDatabase.getInstance().getReference("Challenges").child(token.getIssuerID()).child("IssuedChallenges").child(token.getReceiverID());
        //delete the node (rejected received challenge)
        receiverRef.setValue(token);
        issuerRef.removeValue();
        notificationsArray.remove(position);
        notifyDataSetChanged();
    }

    public void beginFight(ChallengeToken token)
    {
        Intent intent = new Intent(c, FightActivity.class);
        intent.putExtra("blueUID", token.getReceiverID());
        c.startActivity(intent);
    }

    public void showChatPopup(View view, final String senderNN, final String recipientNN, final String chatID, ArrayList<Chat> chatArray)
    {
        //inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        View chatView = inflater.inflate(R.layout.popup_chat, null);
        //create the popup window

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final ListView chatList = chatView.findViewById(R.id.chat_list);
        adapter = new ChatAdapter(c, chatArray);
        chatList.setAdapter(adapter);
        Button commentButton = chatView.findViewById(R.id.chat_button);
        final EditText chatField = chatView.findViewById(R.id.chat_field);

        chatField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendChatMessage(senderNN, recipientNN, chatField.getText().toString(), chatID);
                chatField.setText("");
                chatList.smoothScrollToPosition(adapter.getCount());
                hideKeyboardFrom(c, chatField);
            }
        });


        TextView chatTitle = chatView.findViewById(R.id.chat_title);
        chatTitle.setText("Chat with " + recipientNN);
        chatPopup = new PopupWindow(chatView, width, height, focusable);
        chatPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        chatList.smoothScrollToPosition(adapter.getCount());
    }

    public void sendChatMessage(String senderNN, String receipientNN, String msg, String chatID)
    {
        //Chat chat = new Chat(senderNN, msg, "1 Jan"); //sender name / message text / date
        Chat chat = new Chat(MainActivity.currentUser.getNickname(), MainActivity.currentUser.getUserID(), msg, formatter.format(currentTime)); //sender name / message text / date
        chat.setChatID(chatID);
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatID);
        chatRef.push().setValue(chat);
        adapter.notifyDataSetChanged();

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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
