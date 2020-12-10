package com.example.fightbook;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<Chat> {

    User currentUser;
    FirebaseAuth auth;
    FirebaseUser currentFirebaseUser;


    public ChatAdapter(Context context, ArrayList<Chat> events)
    {
        super(context, 0, events);
        currentUser = MainActivity.currentUser;
    }

    @Override
    public int getItemViewType(int position)
    {
        int viewType;
        Chat chat = getItem(position);
        if(chat.getSender().equals(MainActivity.currentUser.getNickname()))
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
                rowView = inflater.inflate(R.layout.item_chat_sent, parent, false);
            }
            else if(getItemViewType(position) == 1)
            {
                rowView = inflater.inflate(R.layout.item_chat_received, parent, false);
            }

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.chatText = rowView.findViewById(R.id.chat_text_label);
            viewHolder.senderLabel = rowView.findViewById(R.id.sender_name_label);
            viewHolder.dateLabel = rowView.findViewById(R.id.date_label);

            rowView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.senderLabel.setText(senderNN);
        viewHolder.chatText.setText(chat.getChatText());
        viewHolder.dateLabel.setText(chat.getDate());

        return rowView;
    }

    @Override
    public int getViewTypeCount()
    {
        //the numger of different views to inflate
        return 2;
    }



    public static class ViewHolder
    {
        TextView senderLabel;
        TextView chatText;
        TextView dateLabel;
        ImageView avatarBubble;
    }
}