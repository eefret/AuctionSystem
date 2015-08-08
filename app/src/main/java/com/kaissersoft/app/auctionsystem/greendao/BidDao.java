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
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.kaissersoft.app.auctionsystem.greendao.Bid;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table bid.
*/
public class BidDao extends AbstractDao<Bid, Long> {

    public static final String TABLENAME = "bid";

    /**
     * Properties of entity Bid.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property BidItem = new Property(1, Long.class, "bidItem", false, "BID_ITEM");
        public final static Property Bidder = new Property(2, Long.class, "bidder", false, "BIDDER");
        public final static Property Ammount = new Property(3, Double.class, "ammount", false, "AMMOUNT");
        public final static Property Created_at = new Property(4, java.util.Date.class, "created_at", false, "CREATED_AT");
    };

    private DaoSession daoSession;

    private Query<Bid> item_BidListQuery;

    public BidDao(DaoConfig config) {
        super(config);
    }
    
    public BidDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'bid' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'BID_ITEM' INTEGER," + // 1: bidItem
                "'BIDDER' INTEGER," + // 2: bidder
                "'AMMOUNT' REAL," + // 3: ammount
                "'CREATED_AT' INTEGER);"); // 4: created_at
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'bid'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Bid entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long bidItem = entity.getBidItem();
        if (bidItem != null) {
            stmt.bindLong(2, bidItem);
        }
 
        Long bidder = entity.getBidder();
        if (bidder != null) {
            stmt.bindLong(3, bidder);
        }
 
        Double ammount = entity.getAmmount();
        if (ammount != null) {
            stmt.bindDouble(4, ammount);
        }
 
        java.util.Date created_at = entity.getCreated_at();
        if (created_at != null) {
            stmt.bindLong(5, created_at.getTime());
        }
    }

    @Override
    protected void attachEntity(Bid entity) {
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
    public Bid readEntity(Cursor cursor, int offset) {
        Bid entity = new Bid( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // bidItem
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // bidder
            cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3), // ammount
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)) // created_at
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Bid entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBidItem(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setBidder(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setAmmount(cursor.isNull(offset + 3) ? null : cursor.getDouble(offset + 3));
        entity.setCreated_at(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Bid entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Bid entity) {
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
    
    /** Internal query to resolve the "bidList" to-many relationship of Item. */
    public List<Bid> _queryItem_BidList(Long bidItem) {
        synchronized (this) {
            if (item_BidListQuery == null) {
                QueryBuilder<Bid> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.BidItem.eq(null));
                item_BidListQuery = queryBuilder.build();
            }
        }
        Query<Bid> query = item_BidListQuery.forCurrentThread();
        query.setParameter(0, bidItem);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getUserDao().getAllColumns());
            builder.append(" FROM bid T");
            builder.append(" LEFT JOIN user T0 ON T.'BIDDER'=T0.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Bid loadCurrentDeep(Cursor cursor, boolean lock) {
        Bid entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        User user = loadCurrentOther(daoSession.getUserDao(), cursor, offset);
        entity.setUser(user);

        return entity;    
    }

    public Bid loadDeep(Long key) {
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
    public List<Bid> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Bid> list = new ArrayList<Bid>(count);
        
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
    
    protected List<Bid> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Bid> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
