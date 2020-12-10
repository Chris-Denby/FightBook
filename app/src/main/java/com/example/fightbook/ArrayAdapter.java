package com.example.fightbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class ArrayAdapter extends android.widget.ArrayAdapter<Image>
{


    public ArrayAdapter(Context context, ArrayList<Image> images)
    {
        super(context, 0, images);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Image image = getItem(position);
        String imageUrl = image.getUrl();

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_banner, parent, false);
        }

        //lookup view for data population
        ImageView bannerImage = (ImageView) convertView.findViewById(R.id.image);
        //bannerImage.setBackgroundResource(R.drawable.banner1);
        Context context = bannerImage.getContext();
        int id = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
        bannerImage.setBackgroundResource(id);

        return convertView;
    }
}
