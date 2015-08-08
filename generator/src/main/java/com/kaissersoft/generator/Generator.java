package com.kaissersoft.generator;

import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class Generator {

    public static void main(String args[]) {

        //Database schema
        Schema schema = new Schema(1, "com.kaissersoft.app.auctionsystem.greendao");

        //Entities
        Entity user = schema.addEntity("User");
        user.setTableName("user");
        user.addIdProperty().autoincrement();
        user.addStringProperty("username").unique();
        user.addStringProperty("password");
        user.addDateProperty("created_at");

        Entity item = schema.addEntity("Item");
        item.setTableName("item");
        item.addIdProperty().autoincrement();
        Property itemOwnerFK = item.addLongProperty("owner").getProperty();
        item.addStringProperty("name");
        Property itemWinnerFK = item.addLongProperty("winner").getProperty();
        item.addStringProperty("STATUS"); //BIDDING, ENDED
        item.addStringProperty("imgPath");
        item.addDoubleProperty("initialPrice");
        item.addDoubleProperty("currentPrice");
        item.addDateProperty("auctionExpiration");

        Entity bid = schema.addEntity("Bid");
        bid.setTableName("bid");
        bid.addIdProperty().autoincrement();
        Property bidItemFK = bid.addLongProperty("bidItem").getProperty();
        Property bidUserFK = bid.addLongProperty("bidder").getProperty();
        bid.addDoubleProperty("ammount");
        bid.addDateProperty("created_at");

        //foreign keys and etc...
        item.addToOne(user, itemOwnerFK, "user_ownerFK");
        item.addToOne(user, itemWinnerFK, "user_winnerFK");
        item.addToMany(bid, bidItemFK);
        bid.addToOne(user, bidUserFK);

        try {
            new DaoGenerator().generateAll(schema, args[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
