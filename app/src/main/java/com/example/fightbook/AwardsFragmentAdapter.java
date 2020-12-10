package com.example.fightbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AwardsFragmentAdapter extends ArrayAdapter<Award>
{

    TextView awardQty;

    public AwardsFragmentAdapter(Context context, ArrayList<Award> awardsList)
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

                    case "Bodybasher":
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

        return convertView;
    }





}
