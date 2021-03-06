package com.kaissersoft.app.auctionsystem.greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.kaissersoft.app.auctionsystem.greendao.Item;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table item.
*/
public class ItemDao extends AbstractDao<Item, Long> {

    public static final String TABLENAME = "item";

    /**
     * Properties of entity Item.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Owner = new Property(1, Long.class, "owner", false, "OWNER");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Winner = new Property(3, Long.class, "winner", false, "WINNER");
        public final static Property STATUS = new Property(4, String.class, "STATUS", false, "STATUS");
        public final static Property ImgPath = new Property(5, String.class, "imgPath", false, "IMG_PATH");
        public final static Property InitialPrice = new Property(6, Double.class, "initialPrice", false, "INITIAL_PRICE");
        public final static Property CurrentPrice = new Property(7, Double.class, "currentPrice", false, "CURRENT_PRICE");
        public final static Property AuctionExpiration = new Property(8, java.util.Date.class, "auctionExpiration", false, "AUCTION_EXPIRATION");
    };

    private DaoSession daoSession;


    public ItemDao(DaoConfig config) {
        super(config);
    }
    
    public ItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'item' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'OWNER' INTEGER," + // 1: owner
                "'NAME' TEXT," + // 2: name
                "'WINNER' INTEGER," + // 3: winner
                "'STATUS' TEXT," + // 4: STATUS
                "'IMG_PATH' TEXT," + // 5: imgPath
                "'INITIAL_PRICE' REAL," + // 6: initialPrice
                "'CURRENT_PRICE' REAL," + // 7: currentPrice
                "'AUCTION_EXPIRATION' INTEGER);"); // 8: auctionExpiration
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'item'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Item entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long owner = entity.getOwner();
        if (owner != null) {
            stmt.bindLong(2, owner);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Long winner = entity.getWinner();
        if (winner != null) {
            stmt.bindLong(4, winner);
        }
 
        String STATUS = entity.getSTATUS();
        if (STATUS != null) {
            stmt.bindString(5, STATUS);
        }
 
        String imgPath = entity.getImgPath();
        if (imgPath != null) {
            stmt.bindString(6, imgPath);
        }
 
        Double initialPrice = entity.getInitialPrice();
        if (initialPrice != null) {
            stmt.bindDouble(7, initialPrice);
        }
 
        Double currentPrice = entity.getCurrentPrice();
        if (currentPrice != null) {
            stmt.bindDouble(8, currentPrice);
        }
 
        java.util.Date auctionExpiration = entity.getAuctionExpiration();
        if (auctionExpiration != null) {
            stmt.bindLong(9, auctionExpiration.getTime());
        }
    }

    @Override
    protected void attachEntity(Item entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Item readEntity(Cursor cursor, int offset) {
        Item entity = new Item( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // owner
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // winner
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // STATUS
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // imgPath
            cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6), // initialPrice
            cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7), // currentPrice
            cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)) // auctionExpiration
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Item entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOwner(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setWinner(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setSTATUS(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setImgPath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setInitialPrice(cursor.isNull(offset + 6) ? null : cursor.getDouble(offset + 6));
        entity.setCurrentPrice(cursor.isNull(offset + 7) ? null : cursor.getDouble(offset + 7));
        entity.setAuctionExpiration(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Item entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Item entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM item T");
            builder.append(" LEFT JOIN user T0 ON T.'OWNER'=T0.'_id'");
            builder.append(" LEFT JOIN user T1 ON T.'WINNER'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Item loadCurrentDeep(Cursor cursor, boolean lock) {
        Item entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user_ownerFK = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser_ownerFK(user_ownerFK);
        offset += daoSession.getUserDao().getAllColumns().length;

        User user_winnerFK = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser_winnerFK(user_winnerFK);

        return entity;    
    }

    public Item loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Item> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Item> list = new ArrayList<Item>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Item> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Item> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
