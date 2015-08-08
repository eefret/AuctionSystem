package com.kaissersoft.app.auctionsystem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kaissersoft.app.auctionsystem.util.Util;

/**
 * Created by eefret on 08/05/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set both services here.
            Util.setAuctionWatcher(context);
            Util.setBidderBot(context);
        }
    }

}
