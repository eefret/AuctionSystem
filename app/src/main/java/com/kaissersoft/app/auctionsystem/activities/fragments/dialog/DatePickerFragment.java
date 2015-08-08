package com.kaissersoft.app.auctionsystem.activities.fragments.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by eefret on 08/04/15.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DatePickerDialog.OnDateSetListener listener;
    public static final String TAG = "DatePickerFragmentTAG";

    public static DatePickerFragment newInstance(DateTime dt) {

        Bundle args = new Bundle();
        args.putInt("YEAR",dt.getYear());
        args.putInt("MONTH", dt.getMonthOfYear());
        args.putInt("DAY", dt.getDayOfMonth());
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this,
                getArguments().getInt("YEAR"),
                getArguments().getInt("MONTH"),
                getArguments().getInt("DAY"));
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(listener!= null){
            listener.onDateSet(view, year, month, day);
        }
    }


    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }
}
