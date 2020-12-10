package com.example.fightbook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AwardAdapter extends ArrayAdapter<Award>
{

    TextView awardQty;
    ImageView awardImage;
    View colorDot;


    public AwardAdapter(Context context, ArrayList<Award> awardsList)
    {
        super(context, 0, awardsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //get the data item for this position
        Award award = getItem(position);
        // check if an esisting view is being reused, otherwise inflate the view
            if (convertView == null)
            {
                switch (award.getTitle())
                {
                    case "Headhunter":
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_headhunter_award, parent, false);
                        break;

                    case "Bodybreaker":
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bodybreaker_award, parent, false);
                        break;

                    case "Limbtaker":
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_limbtaker_award, parent, false);
                        break;

                    case "Thrustmaster":
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_thrustmaster_award, parent, false);
                        break;

                    case "Butcher":
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_butcher_award, parent, false);
                        break;
                }
            }

        //lookup view for data population
        awardQty = convertView.findViewById(R.id.awardQty);
        awardQty.setText("" + award.getQtyObtained());
        awardImage = convertView.findViewById(R.id.awardImage);
        colorDot = convertView.findViewById(R.id.colorDot);


        if(award.getColor().equals("red"))
        {
            awardQty.setVisibility(View.INVISIBLE);
            awardQty.setText("");
            colorDot.setBackgroundResource(R.drawable.circle_layout_red);
        }
        if(award.getColor().equals("blue"))
        {
            awardQty.setVisibility(View.INVISIBLE);
            awardQty.setText("");
            colorDot.setBackgroundResource(R.drawable.circle_layout_blue);
        }
        if(award.getColor().equals("grey"))
        {
            awardImage.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            awardImage.setAlpha(0.1f);
            awardQty.setAlpha(0.1f);
        }

        return convertView;
    }





}
