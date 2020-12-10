package com.example.fightbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class BannerAdapter extends ArrayAdapter<Banner>
{


    public BannerAdapter(Context context, ArrayList<Banner> images)
    {
        super(context, 0, images);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Banner banner = getItem(position);
        String imageUrl = banner.getUrl();

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_banner, parent, false);
        }

        //lookup view for data population
        ImageView bannerImage = (ImageView) convertView.findViewById(R.id.image);
        Context context = bannerImage.getContext();
        int id = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
        bannerImage.setBackgroundResource(id);

        return convertView;
    }
}
