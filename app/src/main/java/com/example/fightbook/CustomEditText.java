package com.example.fightbook;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Toast;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText
{
    Context context;

    public CustomEditText(Context context)
    {
        super(context);
    }


    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            clearFocus();
            Toast.makeText(getContext(), "CustomSearchView Back Pressed", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return super.dispatchKeyEventPreIme(event);
        }
    }

}
