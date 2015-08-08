package com.kaissersoft.app.auctionsystem.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.greendao.ItemDao;
import com.kaissersoft.app.auctionsystem.ui.adapters.AuctionListAdapter;
import com.kaissersoft.app.auctionsystem.ui.loaders.AuctionListLoader;
import com.kaissersoft.app.auctionsystem.util.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eefret on 08/04/15.
 */
public class AuctionListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Item>>{

    //====================== FIELDS =========================
    public static final String TAG = "AuctionListTAG";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    private int LOADER_ID;
    private ListType mType;
    private long mUserID;
    private RecyclerView mRecyclerView;
    private AuctionListAdapter mAdapter;
    private ProgressBar mProgressDialog;
    Logger log = Logger.getLogger();
    private TextView tvMessage;
    OnListItemClicked callback;

    //=================== CONSTRUCTORS ======================
    public static AuctionListFragment newInstance(long userID, ListType type){
        //Creating instance
        AuctionListFragment instance = new AuctionListFragment();

        //validating and
        if(userID != 0 || type != null){
            Bundle args = new Bundle();
            if (userID != 0){
                args.putLong(EXTRA_USER_ID, userID);
            }
            if (type != null ){
                args.putSerializable(EXTRA_TYPE, type);
            }
            instance.setArguments(args);
        }

        return instance;
    }

    //===============+ OVERRIDEN METHODS ====================


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            callback = ((OnListItemClicked) activity);
        }catch (ClassCastException e){
            log.wtf("Must implement listener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auction, container, false);
        log.d("0");
        if(getArguments()!= null){
            mType = (ListType) getArguments().getSerializable(EXTRA_TYPE);
            mUserID = getArguments().getLong(EXTRA_USER_ID,0L);
        }else {
            throw new IllegalStateException("No type");
        }
        LOADER_ID = mType.ordinal();

        tvMessage = (TextView) view.findViewById(R.id.message);
        mProgressDialog = (ProgressBar)view.findViewById(R.id.pb);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        if(mAdapter!= null){
            mRecyclerView.setAdapter(mAdapter);
        }
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        log.d("0");
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        mProgressDialog.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);
        AuctionListLoader loader = new AuctionListLoader(getActivity(),mType, mUserID);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        if(mAdapter == null){
            mAdapter = new AuctionListAdapter(getActivity(), data);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new AuctionListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(long itemID) {
                    if(callback!= null){
                        log.d(""+itemID);
                        callback.onItemClicked(itemID);
                    }

                    log.d(""+itemID);
                }
            });
            mAdapter.notifyDataSetChanged();
        }else{
            if(isResumed()){
                log.d("data found setting...");
                mAdapter.swapData(data);
                mAdapter.notifyDataSetChanged();
            }
        }
        mProgressDialog.setVisibility(View.GONE);
        if (data.size() == 0 ){
            log.d("no data");
            tvMessage.setVisibility(View.VISIBLE);
        }
        log.d(data.size()+"");
    }


    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {
        if(mAdapter!= null){
            mAdapter.clearData();
        }
        tvMessage.setVisibility(View.VISIBLE);
        log.d("0");
    }


    //====================== METHODS ========================

    //============= INNER CLASSES / INTERFACES ==============
    public enum ListType implements Serializable{
        ALL, BIDDING, WON
    }

    public interface OnListItemClicked{
        void onItemClicked(long itemId);
    }
}
