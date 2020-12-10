package com.example.fightbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Chat> {

    User currentUser;
    FirebaseAuth auth;
    FirebaseUser currentFirebaseUser;
    Context c;


    public CommentAdapter(Context context, ArrayList<Chat> events)
    {
        super(context, 0, events);
        currentUser = MainActivity.currentUser;
        c = context;
    }

    @Override
    public int getItemViewType(int position)
    {
        int viewType;
        Chat chat = getItem(position);
        if(chat.getSenderID().equals(MainActivity.currentUser.getUserID()))
        {
            //if the chat is sent by the current user
            viewType = 0;
        }
        else
        {
            //if the chat is sent by someone else
            viewType = 1;
        }

        return viewType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        final Chat chat = getItem(position);
        String senderNN = chat.getSender();

        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();

            if(getItemViewType(position) == 0)
            {
                rowView = inflater.inflate(R.layout.item_comment, parent, false);

            }
            else if(getItemViewType(position) == 1)
            {
                rowView = inflater.inflate(R.layout.item_comment, parent, false);
            }

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.chatText = rowView.findViewById(R.id.chat_text_label);
            viewHolder.senderLabel = rowView.findViewById(R.id.sender_name_label);
            viewHolder.dateLabel = rowView.findViewById(R.id.date_label);
            viewHolder.avatarBubble = rowView.findViewById(R.id.avatarBubble);

            rowView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.senderLabel.setText(senderNN);
        viewHolder.chatText.setText(chat.getChatText());
        viewHolder.dateLabel.setText(chat.getDate());

        viewHolder.avatarBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(chat.getSenderID(), chat.getSender(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        viewHolder.senderLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goToProfileActivity(chat.getSenderID(), chat.getSender(), MainActivity.currentUser.getUserID(), MainActivity.currentUser.getNickname());
            }
        });

        if(Constants.getInstance().avatarImageCache.containsKey(chat.getSenderID()))
        {
            viewHolder.avatarBubble.setImageBitmap((Bitmap) Constants.getInstance().avatarImageCache.get(chat.getSenderID()));
        }
        else
        {
            //download avatar and load to cache
            //load downloaded image from cache
        }



        return rowView;
    }

    @Override
    public int getViewTypeCount()
    {
        //the numger of different views to inflate
        return 2;
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



    public static class ViewHolder
    {
        TextView senderLabel;
        TextView chatText;
        TextView dateLabel;
        ImageView avatarBubble;
    }
}