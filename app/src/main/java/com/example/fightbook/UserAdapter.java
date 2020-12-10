package com.example.fightbook;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends ArrayAdapter<User>
{
    ArrayList<Follow> followedUsersArray;
    ArrayList<Follow> followersArray;


    public UserAdapter(Context context, ArrayList<User> users, ArrayList<Follow> followedUsersArray, ArrayList<Follow> followersArray)
    {
        super(context, 0, users);
        this.followedUsersArray = followedUsersArray;
        this.followersArray = followersArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //get the data item for this position
        User user = getItem(position);
        // check if an esisting view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        //lookup view for data population
        CircleImageView avatarBubble = convertView.findViewById(R.id.avatarBubble);
        CircleImageView followerBubble = convertView.findViewById(R.id.followerBubble);
        TextView nicknameLabel = (TextView) convertView.findViewById(R.id.nicknameLabel);
        TextView realnameLabel = (TextView) convertView.findViewById(R.id.realnameLabel);
        //populate the data into the template view using the data object
        avatarBubble.setImageBitmap(user.avatarImage);
        nicknameLabel.setText(user.getNickname());
        realnameLabel.setText(user.getWholeName());

        //scale avatar bubble if its a header displaying the search_icon image
        if(user.getUserID().equals("header"))
        {
            avatarBubble.setScaleType(ImageView.ScaleType.CENTER_CROP);
            avatarBubble.setScaleX(0.7f);
            avatarBubble.setScaleY(0.7f);
        }

        Follow checkFollow = new Follow(user.getUserID(), user.getNickname());
        if(followedUsersArray.contains(checkFollow))
        {
            avatarBubble.setBorderColor(Color.parseColor("#ff33b5e5"));
            avatarBubble.setBorderWidth(10);
        }

        if(followersArray.contains(checkFollow))
        {
            followerBubble.setVisibility(View.VISIBLE);
        }
        else
        {
            followerBubble.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }





}
