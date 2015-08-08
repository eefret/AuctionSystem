package com.kaissersoft.app.auctionsystem.activities.fragments.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.ui.watchers.MoneyTextWatcher;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

/**
 * Created by eefret on 08/05/15.
 */
public class BidPickerDialogFragment extends DialogFragment
    implements TextView.OnEditorActionListener{

    OnBidPickerListener callback;
    private EditText etAmount;
    private BlurDialogEngine mBlurEngine;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnBidPickerListener) activity;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBlurEngine != null){
            mBlurEngine.onResume(getRetainInstance());
        }
        getDialog().getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mBlurEngine != null){
            mBlurEngine.onDismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBlurEngine != null) {
            mBlurEngine.onDestroy();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.df_bid_picker, null);
        etAmount = (EditText) view.findViewById(R.id.df_bid_picker_amount);
        etAmount.requestFocus();
        etAmount.setOnEditorActionListener(this);
        etAmount.addTextChangedListener(new MoneyTextWatcher(etAmount));
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .positiveText(R.string.ok)
                .customView(view, true)
                .title(R.string.df_bid_picker_title)
                .autoDismiss(false)
                .cancelable(true)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        Double d;
                        try {
                            d = Double.valueOf(etAmount.getText().toString().replace("$", ""));
                            callback.onBidIncreased(d);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), R.string.error_unexpected, Toast.LENGTH_SHORT);
                        }
                        dismiss();
                    }
                });
        mBlurEngine = new BlurDialogEngine(getActivity());
        return builder.build();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(EditorInfo.IME_ACTION_DONE == i ){
            callback.onBidIncreased(Double.valueOf(etAmount.getText().toString().replace("$", "")));
            this.dismiss();
            return true;
        }
        return false;
    }

    public interface OnBidPickerListener{
        void onBidIncreased(Double amount);
    }
}
