package com.kaissersoft.app.auctionsystem.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.greendao.Bid;
import com.kaissersoft.app.auctionsystem.greendao.BidDao;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.greendao.ItemDao;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;

import java.util.Date;
import java.util.List;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by eefret on 08/05/15.
 */
public class AuctionWatcherService extends IntentService{

    public AuctionWatcherService() {
        super("AuctionWatcherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long currentUser = Prefs.with(this).getLong(Preferences.CURRENT_USER_ID,0L);
        DaoSession daoSession = AuctionSystem.getInstance().getDaoSession();
        //Get all ended items
        List<Item> items = daoSession.getItemDao().queryBuilder()
                .where(ItemDao.Properties.AuctionExpiration.lt(new Date()))
                .where(ItemDao.Properties.STATUS.eq("BIDDING")).list();
        //If no items then finish
        if(items.size()==0){
            return;
        }
        for (Item item : items){
            Logger.getLogger().d("getting item " + item.getId());

            //Getting the winner
            List<Bid> biddersList = daoSession.getBidDao().queryBuilder()
                    .where(BidDao.Properties.BidItem.eq(item.getId()))
                    .orderDesc(BidDao.Properties.Created_at)
                    .list();
            Bid lastBid = null;
            if(biddersList.size() > 0){
                lastBid = biddersList.get(0);
                item.setWinner(lastBid.getUser().getId());
                Logger.getLogger().d("winer is :" + lastBid.getUser().getId());
            }

            item.setSTATUS("ENDED");

            //Updating status and winner
            daoSession.getItemDao().update(item);

            if(currentUser == 0L && lastBid != null && lastBid.getUser().getId() == currentUser){
                Logger.getLogger().d("notification");
                //If current user is the winner
                Notification noti = new Notification.Builder(this)
                        .setContentTitle("Auction Ended")
                        .setContentText("Congratulations you have won the auction in the item: " + item.getName())
                        .setSmallIcon(R.drawable.ic_announcement_white_18dp)
                        .build();
                //noti.flags |= Notification.FLAG_AUTO_CANCEL;
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(123558, noti);
            }
        }
    }
}
