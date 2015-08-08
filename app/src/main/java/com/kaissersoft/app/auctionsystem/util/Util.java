package com.kaissersoft.app.auctionsystem.util;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.greendao.User;
import com.kaissersoft.app.auctionsystem.services.AuctionWatcherService;
import com.kaissersoft.app.auctionsystem.services.BidderBotService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by eefret on 08/03/15.
 */
public class Util {

    public static long DABOT_ID = 5000L;

    public static String md5Encrypt(String text){
        String digest = null;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(text.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b: hash){
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString();

        }catch (UnsupportedEncodingException e1){
            e1.printStackTrace();
        }catch (NoSuchAlgorithmException e2){
            e2.printStackTrace();
        }
        return digest;
    }

    public static boolean saveImage(Context context, Bitmap image, String filename) {
        try {
            // Use the compress method on the Bitmap object to write image to
            // the OutputStream
            FileOutputStream fos = context.openFileOutput(filename+".png", Context.MODE_PRIVATE);
            // Writing the bitmap to the output stream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap retrieveSavedImage(Context context, String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = context.getFileStreamPath(filename + ".png");
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return thumbnail;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static String generateImageName(long itemId){
        StringBuilder sb = new StringBuilder();
        sb.append("item_");
        sb.append(md5Encrypt(itemId+""));
        return sb.toString();
    }

    public static void setAuctionWatcher(Context c){
        Intent serviceIntent = new Intent(c, AuctionWatcherService.class);
        PendingIntent pi = PendingIntent.getService(c, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES / 3,
                pi);
    }

    public static void setBidderBot(Context c){
        Intent serviceIntent = new Intent(c, BidderBotService.class);
        PendingIntent pi = PendingIntent.getService(c, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/5,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES/5,
                pi);
    }

    public static void insertBotUser(){
        User user = new User();
        user.setId(DABOT_ID);
        user.setCreated_at(new Date());
        user.setUsername("DaBot");
        user.setPassword(md5Encrypt("DaPassword"));
        AuctionSystem.getInstance().getDaoSession().startAsyncSession().insert(user);
    }

    public enum BidStatus{
        BIDDING, ENDED
    }
}
