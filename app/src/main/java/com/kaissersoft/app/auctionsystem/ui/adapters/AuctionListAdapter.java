package com.kaissersoft.app.auctionsystem.ui.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by eefret on 08/04/15.
 */
public class AuctionListAdapter extends RecyclerView.Adapter<AuctionListAdapter.ViewHolder> {


    private List<Item> mItems;
    private Context c;
    private static OnItemClickListener mCallBack;

    public AuctionListAdapter(Context c, List<Item> items) {
        mItems = items;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_auction_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        vh.itemName.setText(mItems.get(i).getName());
        //int imgInt = c.getResources().getIdentifier(mItems.get(i).getImgPath(), null, null);
        //vh.image.setImageBitmap(Util.retrieveSavedImage(c, mItems.get(i).getImgPath()));
        Uri imageUri = Uri.fromFile(c.getFileStreamPath(mItems.get(i).getImgPath()+".png"));
        ImageLoader.getInstance().displayImage(imageUri.toString(),vh.image);
        if(mItems.get(i).getSTATUS().equals("BIDDING")){
            vh.status.setTextColor(c.getResources().getColor(R.color.links_blue));
        }else{
            vh.status.setTextColor(c.getResources().getColor(R.color.ColorPrimary));
        }
        vh.status.setText(mItems.get(i).getSTATUS());
        vh.expiresIn.setText(DateUtils.getRelativeTimeSpanString(
                mItems.get(i).getAuctionExpiration().getTime(),
                DateTime.now().getMillis(), DateUtils.SECOND_IN_MILLIS));
        vh.initialPrice.setText("US$ "+mItems.get(i).getInitialPrice());
        vh.currentPrice.setText("US$ " + mItems.get(i).getCurrentPrice());
        vh.itemId = mItems.get(i).getId();
        Logger.getLogger().d(mItems.get(i).getId()+"");

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void swapData(List<Item> items){
        if(items.size()>0){
            mItems.clear();
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        mItems.clear();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public CardView cv;
        public long itemId;
        public TextView itemName;
        public TextView initialPrice;
        public TextView currentPrice;
        public ImageView image;
        public TextView expiresIn;
        public TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            itemName = (TextView) itemView.findViewById(R.id.li_item_tv_name);
            initialPrice = (TextView) itemView.findViewById(R.id.li_item_tv_initial_price);
            currentPrice = (TextView) itemView.findViewById(R.id.li_item_tv_current_price);
            image = (ImageView) itemView.findViewById(R.id.li_item_iv_image);
            expiresIn = (TextView) itemView.findViewById(R.id.li_item_tv_expires_in);
            status = (TextView) itemView.findViewById(R.id.li_item_tv_status);
            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mCallBack != null){

                mCallBack.onItemClick(itemId);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(long itemID);
    }

    public void setOnItemClickListener(OnItemClickListener callback){
        mCallBack = callback;
    }
}
