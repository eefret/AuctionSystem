package com.kaissersoft.app.auctionsystem.activities.fragments.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by eefret on 08/04/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "TimePickerFragmentTAG";
    private TimePickerDialog.OnTimeSetListener listener;

    public static TimePickerFragment newInstance(DateTime dt) {

        Bundle args = new Bundle();
        args.putInt("HOUR", dt.getHourOfDay());
        args.putInt("MINUTE", dt.getMinuteOfHour());
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this,
                getArguments().getInt("HOUR"),
                getArguments().getInt("MINUTE"),
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(listener != null){
            listener.onTimeSet(view, hourOfDay, minute);
        }
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
    }
}
