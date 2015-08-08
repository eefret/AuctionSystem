package com.kaissersoft.app.auctionsystem.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.BidPickerDialogFragment;
import com.kaissersoft.app.auctionsystem.greendao.Bid;
import com.kaissersoft.app.auctionsystem.greendao.BidDao;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.greendao.ItemDao;
import com.kaissersoft.app.auctionsystem.greendao.User;
import com.kaissersoft.app.auctionsystem.greendao.UserDao;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;

import java.util.List;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.Query;
import me.alexrs.prefs.lib.Prefs;

public class ItemViewActivity extends AppCompatActivity
    implements BidPickerDialogFragment.OnBidPickerListener{

    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    public static final int REQUEST_CODE = 3458;

    private long itemID;
    private ImageView ivPicture;
    private TextView tvInitialPrice;
    private TextView tvCurrentPrice;
    private TextView tvEndTime;
    private TextView tvStatus;
    private TextView tvLastBidder;
    private DaoSession daoSession;
    private AsyncOperation itemQueryOperation;
    private AsyncOperation bidQueryOperation;
    private AsyncSession async;
    private AsyncOperation userQueryOperation;
    private Logger log = Logger.getLogger();
    private FloatingActionButton fbNewBid;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        //Getting and validatin the itemID
        itemID = getIntent().getLongExtra(EXTRA_ITEM_ID,0L);
        if( itemID == 0L){
            Toast.makeText(this,R.string.error_unexpected,Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED,null);
            finish();
        }

        daoSession = AuctionSystem.getInstance().getDaoSession();

        //setting Widgets
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        ivPicture = (ImageView) findViewById(R.id.a_item_view_iv_picture);
        tvInitialPrice = (TextView) findViewById(R.id.a_item_view_tv_initial_price);
        tvCurrentPrice = (TextView) findViewById(R.id.a_item_view_tv_current_price);
        tvEndTime = (TextView) findViewById(R.id.a_item_view_tv_end);
        tvStatus = (TextView) findViewById(R.id.a_item_view_tv_status);
        tvLastBidder = (TextView) findViewById(R.id.a_item_view_tv_last_bidder);
        fbNewBid = (FloatingActionButton) findViewById(R.id.fab);
        fbNewBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                BidPickerDialogFragment f= new BidPickerDialogFragment();
                f.show(fm, "fragment_edit_name");
            }
        });

        //Load data
        loadData(itemID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void loadData(long id){
        new getItemTask().execute(id);
    }

    private void setItemData(Item item){
        if(item!= null){
            mItem = item;
            Uri imageUri = Uri.fromFile(getFileStreamPath(item.getImgPath()+".png"));
            ImageLoader.getInstance().displayImage(imageUri.toString(), ivPicture);
            tvInitialPrice.setText("US$ " + item.getInitialPrice());
            tvCurrentPrice.setText("US$ " + item.getCurrentPrice());
            tvEndTime.setText(DateUtils.getRelativeTimeSpanString(
                    item.getAuctionExpiration().getTime(),
                    DateTime.now().getMillis(), DateUtils.SECOND_IN_MILLIS));
            tvStatus.setText(item.getSTATUS());
            if(getSupportActionBar()!= null){
                getSupportActionBar().setTitle(item.getName());
            }
        }
    }


    private void cleanData(){
        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle("");
        }
        ivPicture.setImageResource(android.R.color.transparent);
        tvInitialPrice.setText("");
        tvCurrentPrice.setText("");
        tvEndTime.setText("");
        tvStatus.setText("");
        tvLastBidder.setText("");
    }

    @Override
    public void onBidIncreased(Double amount) {
        log.d(amount + "");
        Bid b = new Bid();
        b.setAmmount(amount);
        b.setBidder(Prefs.with(this).getLong(Preferences.CURRENT_USER_ID, 0L));
        b.setBidItem(itemID);
        daoSession.startAsyncSession().insert(b);
        mItem.setCurrentPrice(mItem.getCurrentPrice() + amount);
        daoSession.startAsyncSession().update(mItem);
        new getItemTask().execute(itemID);
    }

    private class getItemTask extends AsyncTask<Long,Void,Item>{
        @Override
        protected Item doInBackground(Long... longs) {
            long itemId = longs[0];
            log.d(itemId+"");
            return daoSession.getItemDao().queryBuilder()
                    .where(ItemDao.Properties.Id.eq(itemId)).unique();
        }
        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);
            setItemData(item);
            mItem = item;
            new getLastBidderTask().execute(item);
        }
    }

    private class getLastBidderTask extends AsyncTask<Item, Void, User>{
        @Override
        protected User doInBackground(Item... items) {
            List<Bid> itemList =daoSession.getBidDao()
                    .queryBuilder().where(BidDao.Properties.BidItem.eq(items[0].getId()))
                    .orderDesc(BidDao.Properties.Created_at).list();
            if(itemList.size()==0){
                return null;
            }
            long userId = itemList.get(0).getBidder();
            return daoSession.getUserDao().queryBuilder()
                    .where(UserDao.Properties.Id.eq(userId)).unique();
        }
        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user!=null){
                tvLastBidder.setText(user.getUsername());
            }else{
                tvLastBidder.setText("N/A");
            }
        }
    }
}
