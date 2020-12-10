package com.example.fightbook;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.constraintlayout.widget.ConstraintLayout;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SplashPopup implements Runnable
{

    View view;
    PopupWindow popupWindowLogo;
    Context c;

    public SplashPopup(View v, Context context)
    {
        this.view = v;
        c = context;
    }

    public void start()
    {
        run();
    }

    @Override
    public void run()
    {
        //TOP LAYER WITH ANIMATION
        LayoutInflater inflater1 = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView1 = inflater1.inflate(R.layout.popup_splash, null);
        //popupView1.setElevation(3);
        //create the popup window
        int width1 = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height1 = ConstraintLayout.LayoutParams.MATCH_PARENT;
        boolean focusable1 = false;
        popupWindowLogo = new PopupWindow(popupView1, width1, height1, focusable1);
        popupWindowLogo.showAtLocation(view, Gravity.CENTER, 0, 0);

        CountDownTimer splashTimer = new CountDownTimer(3000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {

            }
            @Override
            public void onFinish()
            {
                popupWindowLogo.dismiss();
                return;
            }
        };
        splashTimer.start();
    }


}
