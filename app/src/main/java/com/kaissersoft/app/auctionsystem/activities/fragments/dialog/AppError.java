package com.kaissersoft.app.auctionsystem.activities.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kaissersoft.app.auctionsystem.R;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine;

/**
 * Created by eefret on 08/03/15.
 */
public class AppError extends DialogFragment {
    //========================================================
    //CONSTANTS
    //========================================================
    public static final String BUNDLE_ARG_ERROR_TYPE = "ERROR_TYPE";
    public static final String TAG = "AppErrorTAG";
    //========================================================
    //FIELDS
    //========================================================
    private BlurDialogEngine mBlurDialogEngine;
    private MaterialDialog mDialog;
    //========================================================
    //CONSTRUCTORS
    //========================================================
    public static AppError newInstance(AppError.ErrorType errorType) {
        AppError instance = new AppError();

        //validating and
        if(errorType != null){
            Bundle args = new Bundle();
            args.putParcelable(BUNDLE_ARG_ERROR_TYPE, errorType);
            instance.setArguments(args);
        }
        return instance;
    }
    //========================================================
    //OVERRIDDEN METHODS
    //========================================================


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurDialogEngine = new BlurDialogEngine(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initialize();
        return mDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurDialogEngine.onResume(getRetainInstance());
    }

    @Override
    public void onDestroy() {
        mBlurDialogEngine.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mBlurDialogEngine.onDismiss();
        super.onDismiss(dialog);
    }

    //========================================================
    //METHODS
    //========================================================

    private void initialize() {
        final ErrorType errorTypeInt = getArguments().getParcelable(BUNDLE_ARG_ERROR_TYPE);

        if(errorTypeInt != null){
            String title = getString(R.string.app_name);
            String message = getString(errorTypeInt.getResource());

            mDialog = new MaterialDialog.Builder(getActivity())
                    .title(title)
                    .content(message)
                    .positiveText(android.R.string.ok)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    }).build();
        }

    }


    //========================================================
    //INNER CLASSES
    //========================================================

    public enum ErrorType implements Parcelable {
        INVALID_TEXT_LENGHT(R.string.error_invalid_text_lenght),
        USERNAME_CANNOT_BE_EMPTY(R.string.error_username_cannot_be_empty),
        PASSWORD_CANNOT_BE_EMPTY(R.string.error_pass_cannot_be_empty),
        INVALID_CREDENTIALS(R.string.error_invalid_credentials),
        USER_ALREADY_EXIST(R.string.error_user_already_exist),
        PASSWORD_CONFIRMATION_CANNOT_BE_EMPTY(R.string.error_pass_confirm_cannot_be_empty),
        PASSWORD_CONFIRMATION_DIFFERENT(R.string.error_pass_are_different),
        UNEXPECTED_ERROR(R.string.error_unexpected),
        ITEM_HAS_NO_IMAGE(R.string.error_item_has_no_image),
        ITEM_HAS_NO_NAME(R.string.error_item_has_no_name),
        ITEM_HAS_NO_PRICE_OR_INCORRECT(R.string.error_item_has_no_price_or_incorrect),
        ITEM_HAS_NO_EXPIRATION(R.string.error_item_has_no_expiration),
        DATE_MUST_BE_GREATER_THAN_NOW(R.string.error_date_must_be_greater);


        private int resource;

        ErrorType(int resource) {
            this.resource = resource;
        }

        public int getResource() {
            return resource;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }

        public static final Creator<ErrorType> cCREATOR = new Creator<ErrorType>() {
            @Override
            public ErrorType createFromParcel(Parcel source) {
                return ErrorType.values()[source.readInt()];
            }

            @Override
            public ErrorType[] newArray(int size) {
                return new ErrorType[size];
            }
        };
    }
    //========================================================
    //GETTERS AND SETTERS
    //========================================================
}
