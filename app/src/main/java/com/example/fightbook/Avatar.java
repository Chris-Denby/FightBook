package com.example.fightbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Avatar extends ConstraintLayout
{
    public ImageView image;
    public String url;

    public Avatar(Context context, String url)
    {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_avatar, this, true);
        image = (ImageView) findViewById(R.id.image);
        this.url = url;
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        if(selected)
        {
            Toast.makeText(getContext(), "selected", Toast.LENGTH_SHORT).show();
        }
        else
        {

        }
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }











}
