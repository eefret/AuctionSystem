package com.kaissersoft.app.auctionsystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.kaissersoft.app.auctionsystem.greendao.DaoMaster;

/**
 * Created by eefret on 08/03/15.
 */
public class AuctionSystemOpenHelper extends DaoMaster.OpenHelper{

    //====================== FIELDS =========================
    public static final String DB_NAME = "auction_system";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;


    //=================== CONSTRUCTORS ======================
    public AuctionSystemOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, factory);
        db = getWritableDatabase();
    }

    //===============+ OVERRIDEN METHODS ====================
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        //Switch used to perform upgrade changes in database
        switch (oldV){
            //Switch between version changes
            default:
                return;
        }
    }
}
