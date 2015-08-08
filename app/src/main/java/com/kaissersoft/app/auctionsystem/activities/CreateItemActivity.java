package com.kaissersoft.app.auctionsystem.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.AppError;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.DatePickerFragment;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.TimePickerFragment;
import com.kaissersoft.app.auctionsystem.activities.listeners.OnTaskCompleted;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.Item;
import com.kaissersoft.app.auctionsystem.ui.watchers.MoneyTextWatcher;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;
import com.kaissersoft.app.auctionsystem.util.Util;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import me.alexrs.prefs.lib.Prefs;

/**
 * Created by eefret on 08/04/15.
 */
public class CreateItemActivity extends AppCompatActivity
    implements  DatePickerDialog.OnDateSetListener,
                TimePickerDialog.OnTimeSetListener,
                OnTaskCompleted {

    //====================== FIELDS =========================
    public final static String EXTRA_USER_ID = "EXTRA_USER_ID";
    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";
    public static final int REQUEST_CODE = 3465;
    public static final int SELECT_IMAGE_REQUEST_CODE = 6547;
    private long currentUserId;
    private ImageView ivImagePicker;
    private EditText etName;
    private EditText etPrice;
    private EditText etDateTime;

    private MutableDateTime dateTime;
    private Bitmap image;
    private boolean sendFlag = true;
    private ProgressDialog pd;
    //===============+ OVERRIDEN METHODS ====================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        currentUserId = getIntent().getLongExtra(EXTRA_USER_ID, 0L);
        if(currentUserId == 0L){
            currentUserId = Prefs.with(this).getLong(Preferences.CURRENT_USER_ID, 0L);
        }
        dateTime = new MutableDateTime();

        //Setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //setting widgets
        ivImagePicker = (ImageView) findViewById(R.id.a_create_item_iv_picker);
        etName = (EditText) findViewById(R.id.a_create_item_et_name);
        etPrice = (EditText) findViewById(R.id.a_create_item_et_price);
        etDateTime = (EditText) findViewById(R.id.a_create_item_et_datetime);

        //working with the widgets
        etPrice.addTextChangedListener(new MoneyTextWatcher(etPrice));
        etDateTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    DatePickerFragment dateF = DatePickerFragment.newInstance(dateTime.toDateTime());
                    dateF.setOnDateSetListener(CreateItemActivity.this);
                    dateF.show(getSupportFragmentManager(), DatePickerFragment.TAG);
                }
            }
        });
        ivImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture..."),
                        SELECT_IMAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case SELECT_IMAGE_REQUEST_CODE:
                    try {
                        BitmapFactory.decodeFile(data.getData().toString());
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        image = BitmapFactory.decodeStream(inputStream);
                        ivImagePicker.setImageBitmap(image);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_done_btn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.send:
                if(sendFlag){
                    sendFlag = false;
                    if (valid()){
                        Item item = new Item();
                        item.setName(etName.getText().toString());
                        item.setInitialPrice(Double.valueOf(etPrice.getText().toString().replace("$", "")));
                        item.setSTATUS("BIDDING");
                        item.setAuctionExpiration(dateTime.toDate());
                        item.setOwner(currentUserId);
                        item.setCurrentPrice(item.getInitialPrice());
                        new InsertTask(this).execute(item);
                    }else{
                        sendFlag = true;
                    }
                }
                break;
        }
        return true;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        dateTime.setYear(year);
        dateTime.setMonthOfYear(month);
        dateTime.setDayOfMonth(day);
        TimePickerFragment timeF = TimePickerFragment.newInstance(dateTime.toDateTime());
        timeF.setOnTimeSetListener(this);
        timeF.show(getFragmentManager(), TimePickerFragment.TAG);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        dateTime.setHourOfDay(hour);
        dateTime.setMinuteOfHour(minute);
        DateTimeFormatter dtf = DateTimeFormat.fullDateTime();
        etDateTime.setText(dtf.print(dateTime));
    }

    @Override
    public void onTaskCompleted(Long id) {
        Intent i = new Intent();
        i.putExtra(EXTRA_ITEM_ID, id);
        setResult(RESULT_OK, i);
        finish();
    }


    //====================== METHODS ========================

    private boolean valid(){
        if(ivImagePicker != null && etName != null && etPrice != null && etDateTime != null){
            if(image == null){
                showError(AppError.ErrorType.ITEM_HAS_NO_IMAGE);
                return false;
            }
            if(etName.getText().toString().isEmpty()){
                showError(AppError.ErrorType.ITEM_HAS_NO_NAME);
                etName.requestFocus();
                return false;
            }
            if(etPrice.getText().toString().isEmpty() ||
                    !Util.isNumeric(etPrice.getText().toString().replace("$",""))){
                showError(AppError.ErrorType.ITEM_HAS_NO_PRICE_OR_INCORRECT);
                return false;
            }
            if (etDateTime.getText().toString().isEmpty()){
                showError(AppError.ErrorType.ITEM_HAS_NO_EXPIRATION);
                return false;
            }
            if(dateTime.isBefore(DateTime.now())){
                showError(AppError.ErrorType.DATE_MUST_BE_GREATER_THAN_NOW);
                return false;
            }
            return true;
        }
        return false;
    }

    public void showError(AppError.ErrorType error){
        AppError dialog = AppError.newInstance(error);
        dialog.show(getSupportFragmentManager(), AppError.TAG);
    }

    //============= INNER CLASSES / INTERFACES ==============

    private class InsertTask extends AsyncTask<Item,Void,Long>{
        OnTaskCompleted callback;

        public InsertTask(OnTaskCompleted callback) {
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(CreateItemActivity.this, "Loading...", "");
        }

        @Override
        protected Long doInBackground(Item... items) {
            DaoSession daoSession = AuctionSystem.getInstance().getDaoSession();
            Item item = items[0];
            long id = daoSession.insert(item);
            if(Util.saveImage(getBaseContext(), image, Util.generateImageName(id))){
                item.setImgPath(Util.generateImageName(id));
                daoSession.startAsyncSession().update(item);
            }
            return id;
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            pd.dismiss();
            callback.onTaskCompleted(id);
        }
    }
}
