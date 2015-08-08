package com.kaissersoft.app.auctionsystem.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.greendao.Bid;
import com.kaissersoft.app.auctionsystem.greendao.BidDao;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.greendao.ItemDao;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Util;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class BidderBotService extends IntentService {
    public BidderBotService() {
        super("BidderBotService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DaoSession daoSession = AuctionSystem.getInstance().getDaoSession();
        Logger log = Logger.getLogger();
        List<Item> itemList = daoSession.getItemDao().queryBuilder()
                .where(ItemDao.Properties.STATUS.eq(Util.BidStatus.BIDDING.name()))
                .list();
        //If no items then finish
        if(itemList.size()==0){
            log.d("0");
            return;
        }

        //RNGEsus
        Random r = new Random();
        Item item = itemList.get(r.nextInt(itemList.size()));

        List<Bid> biddersList = daoSession.getBidDao().queryBuilder()
                .where(BidDao.Properties.BidItem.eq(item.getId()))
                .orderDesc(BidDao.Properties.Created_at)
                .list();
        Bid lastBidder = null;

        if(biddersList.size() >0){
            lastBidder = biddersList.get(0);
        }

        if(lastBidder != null && lastBidder.getId() != Util.DABOT_ID){
            log.d("inserting bot bid");
            Bid bid = new Bid();
            bid.setBidItem(item.getId());
            bid.setAmmount(r.nextDouble() * 10);
            bid.setBidder(Util.DABOT_ID);
            bid.setCreated_at(new Date());
            daoSession.getBidDao().insert(bid);
        }

    }
}
