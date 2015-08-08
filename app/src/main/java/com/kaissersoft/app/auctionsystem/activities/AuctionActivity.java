package com.kaissersoft.app.auctionsystem.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.activities.fragments.AuctionListFragment;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.ui.adapters.AuctionListAdapter;
import com.kaissersoft.app.auctionsystem.ui.adapters.ViewPagerAdapter;
import com.kaissersoft.app.auctionsystem.ui.widget.SlidingTabLayout;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;

import me.alexrs.prefs.lib.Prefs;

public class AuctionActivity extends AppCompatActivity
    implements AuctionListFragment.OnListItemClicked{

    //====================== FIELDS =========================
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private FloatingActionButton fab;
    int numOfTabs = 3;
    private long currentUserId;
    private Logger log = Logger.getLogger();
    private CoordinatorLayout container;

    //===============+ OVERRIDEN METHODS ====================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);
        log.d("0");
        currentUserId = getIntent().getLongExtra(EXTRA_USER_ID, 0L);
        if(currentUserId == 0L){
            currentUserId = Prefs.with(this).getLong(Preferences.CURRENT_USER_ID, 0L);
        }
        log.d("UserID: "+currentUserId);


       CharSequence titles[] ={
               getString(R.string.a_auction_all_title),
               getString(R.string.a_auction_bidding_title),
               getString(R.string.a_auction_won_title)};

        //Setting toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(), titles, numOfTabs, currentUserId);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        container = (CoordinatorLayout) findViewById(R.id.coordinator);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        //setting floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AuctionActivity.this, CreateItemActivity.class);
                i.putExtra(CreateItemActivity.EXTRA_USER_ID, currentUserId);
                startActivityForResult(i, CreateItemActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            switch (requestCode){
                case CreateItemActivity.REQUEST_CODE:

                    Snackbar.make(container, R.string.item_created, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final long id = data.getLongExtra(CreateItemActivity.EXTRA_ITEM_ID, 0l);
                                    final DaoSession daoSession = AuctionSystem.getInstance().getDaoSession();
                                    new AsyncTask<Void, Void, Boolean>() {
                                        @Override
                                        protected Boolean doInBackground(Void... params) {
                                            daoSession.getItemDao().deleteByKey(id);
                                            return true;
                                        }
                                    }.execute();

                                }
                            }).show();
                    break;
                case ItemViewActivity.REQUEST_CODE:
                    if(pager != null ) {
                        pager.setAdapter(adapter);
                    }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auction,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                //Remove the UserID
                Prefs.with(this).save(Preferences.CURRENT_USER_ID,0L);
                //Programatically restart the app
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            break;
        }
        return true;
    }

    @Override
    public void onItemClicked(long itemId) {
        log.d(itemId+"");
        Intent i = new Intent(this, ItemViewActivity.class);
        i.putExtra(ItemViewActivity.EXTRA_ITEM_ID,itemId);
        startActivityForResult(i, ItemViewActivity.REQUEST_CODE);
    }


    //====================== METHODS ========================

    //============= INNER CLASSES / INTERFACES ==============


}
