package com.example.fightbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class AvatarAdapter extends ArrayAdapter<Avatar>
{


    public AvatarAdapter(Context context, ArrayList<Avatar> images)
    {
        super(context, 0, images);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Avatar avatar = getItem(position);
        String imageUrl = avatar.getUrl();

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_avatar, parent, false);
        }

        //lookup view for data population
        ImageView avatarImage = (ImageView) convertView.findViewById(R.id.image);
        Context context = avatarImage.getContext();
        int id = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
        avatarImage.setBackgroundResource(id);

        return convertView;
    }
}
