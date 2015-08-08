package com.kaissersoft.app.auctionsystem.ui.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.activities.fragments.AuctionListFragment;
import com.kaissersoft.app.auctionsystem.greendao.Bid;
import com.kaissersoft.app.auctionsystem.greendao.BidDao;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.greendao.ItemDao;
import com.kaissersoft.app.auctionsystem.greendao.UserDao;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eefret on 08/04/15.
 */
public class AuctionListLoader extends AsyncTaskLoader<List<Item>> {

    private final AuctionListFragment.ListType type;
    private final long userId;
    private Context mContext;
    private DaoSession daoSession;
    private List<Item> mData;
    Logger log = Logger.getLogger();

    public AuctionListLoader(Context context,
                             AuctionListFragment.ListType type,
                             long userID) {
        super(context);
        this.mContext = context;
        this.daoSession = AuctionSystem.getInstance().getDaoSession();
        this.type = type;
        this.userId = userID;
    }

    @Override
    public void deliverResult(List<Item> data) {
        log.d("0");
        if(isReset()){
            data.clear();
        }
        List<Item> oldData = mData;
        mData = data;

        if(isStarted()){
            super.deliverResult(data);
        }
        if(oldData != null && oldData != data){
            oldData.clear();
        }
    }

    @Override
    public List<Item> loadInBackground() {
        switch (type){
            case ALL:
                return daoSession.getItemDao().queryBuilder()
                        .where(ItemDao.Properties.STATUS.notEq(Util.BidStatus.ENDED))
                        .list();
            case BIDDING:
                ArrayList<Bid> bids = (ArrayList<Bid>) daoSession.getBidDao()
                       .queryBuilder().where(
                                BidDao.Properties.Bidder.eq(userId)).list();
                ArrayList<Long> itemIds = new ArrayList<>();
                for (Bid bid: bids){
                    if(!itemIds.contains(bid.getBidItem())){
                        itemIds.add(bid.getBidItem());
                    }
                }
                return daoSession.getItemDao().queryBuilder().where(
                        ItemDao.Properties.Id.in(itemIds),
                        ItemDao.Properties.STATUS.eq("BIDDING")
                        ).list();
            case WON:
                return daoSession.getItemDao().queryBuilder().where(
                        ItemDao.Properties.Winner.eq(userId),
                        ItemDao.Properties.STATUS.eq("ENDED")
                ).list();
            default:
                return daoSession.getItemDao().queryBuilder().list();

        }
    }

    @Override
    protected void onStartLoading() {
        log.d("0");
        if (mData!= null){
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null || mData.size()==0){
            forceLoad();
        }
    }

    @Override
    public void onCanceled(List<Item> data) {
        log.d("0");
        if(data != null && data.size() > 0){
            data.clear();
        }
    }

    @Override
    protected void onStopLoading() {
        log.d("0");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        log.d("0");
        onStopLoading();
        if(mData != null && mData.size() > 0){
            mData.clear();
        }
        mData = null;
    }
}
