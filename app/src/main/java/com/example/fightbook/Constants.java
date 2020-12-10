package com.example.fightbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Constants
{

    private static Constants constants = new Constants();
    public static Constants uniqueInstance;

    public static ArrayList<Follow> followedUsersArray = new ArrayList();
    public static ArrayList<Follow> followersArray = new ArrayList();

    User currentUser;
    Bitmap userAvatar;
    final long ONE_MEGABYTE = 1024 * 1024;
    final long HALF_MEGABYTE = 768 * 768;
    final int ACTIVITY_FEED_LIMIT = 20;
    final int NOTIFICATION_FEED_LIMIT = 20;

    Map activityFeedImageCache = new HashMap<String, Bitmap>(); //nodeID, Image
    Map avatarImageCache = new HashMap<String, Bitmap>();   //userID, Image



    public static Constants getConstants()
    {
        return constants;
    }

    public static Constants getInstance()
    {
        if(uniqueInstance == null)
        {
            if(uniqueInstance == null)
            {
                uniqueInstance = new Constants();
            }
        }
        return uniqueInstance;
    }

    private Constants()
    {

    }

    public void getAvatar(final String userID) {
        if (Constants.getInstance().avatarImageCache.containsKey(userID)) {

        } else {
            //if image doesnt exist in the cache - download from storage asynchronously to cache and update event array
            //###############DOWNLOAD THE IMAGE TO MEMORY#####################;
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storage.child("AvatarImages").child(userID).child(userID);
            try {
                imageRef.getBytes(Constants.getInstance().HALF_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        //when the image is loaded to the cache- add image to the Event in the array
                        Constants.getInstance().avatarImageCache.put(userID, bmp);
                        //assign image from cache to respective Event object

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } catch (Exception e) {
                Log.println(Log.ERROR, "Image", e.getMessage());
            }
        }
    }
}
