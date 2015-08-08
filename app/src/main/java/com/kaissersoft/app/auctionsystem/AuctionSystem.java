package com.kaissersoft.app.auctionsystem;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.kaissersoft.app.auctionsystem.db.AuctionSystemOpenHelper;
import com.kaissersoft.app.auctionsystem.greendao.DaoMaster;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;
import com.kaissersoft.app.auctionsystem.util.Util;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by eefret on 08/03/15.
 */
public class AuctionSystem extends Application {
    private static DaoSession daoSession;
    public static AuctionSystem instance;
    private Logger log = Logger.getLogger();

    @Override
    public void onCreate() {
        super.onCreate();
        log.d("0");
        //Set up database
        try {
            SQLiteOpenHelper helper = new AuctionSystemOpenHelper(this, null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            daoSession = daoMaster.newSession();
        }catch (Exception e){
            Toast.makeText(this, R.string.error_could_not_load_database, Toast.LENGTH_SHORT).show();
        }

        //Default Display Options
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .delayBeforeLoading(200)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .build();
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(15)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSizePercentage(30)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(options)
                .writeDebugLogs()

                .build();

        ImageLoader.getInstance().init(config);

        if(Prefs.with(this).getBoolean(Preferences.IS_FIRST_RUN,true)){
            Util.setAuctionWatcher(this);
            Util.setBidderBot(this);
            Util.insertBotUser();
        }
        Prefs.with(this).save(Preferences.IS_FIRST_RUN,false);
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }
    public static AuctionSystem getInstance(){
        return (instance == null) ? instance = new AuctionSystem() : instance;
    }
}
