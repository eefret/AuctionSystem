package com.kaissersoft.app.auctionsystem.activities.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.AppError;
import com.kaissersoft.app.auctionsystem.activities.listeners.OnAppErrorListener;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.filters.NoSpaceInputFilter;

/**
 * Created by eefret on 08/03/15.
 */
public class RegisterFragment extends Fragment {

    //====================== FIELDS =========================
    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
    public static final String TAG = "RegisterFragmentTAG";

    private Logger log = Logger.getLogger();
    OnRegisterSelected mCallback;
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtPasswordConfirm;
    private Button mBtnRegister;
    private Button mBtnGoLogin;
    private ProgressDialog pDialog;

    //=================== CONSTRUCTORS ======================

    public static RegisterFragment newInstance(@Nullable String username, @Nullable String password){
        //Creating instance
        RegisterFragment instance = new RegisterFragment();

        //validating and
        if(username != null || password != null){
            Bundle args = new Bundle();
            if (username != null && !username.isEmpty()){
                args.putString(EXTRA_USERNAME, username);
            }
            if (password != null && !password.isEmpty()){
                args.putString(EXTRA_PASSWORD, password);
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
            mCallback = (OnRegisterSelected) activity;
        }catch (ClassCastException e){
            log.wtf("must Implement all fragment listeners");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating view
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Assigning widgets
        mEtUsername = (EditText) view.findViewById(R.id.f_register_et_username);
        mEtPassword = (EditText) view.findViewById(R.id.f_register_et_password);
        mEtPasswordConfirm = (EditText) view.findViewById(R.id.f_register_et_password_confirm);
        mBtnRegister = (Button) view.findViewById(R.id.f_register_btn_register);
        mBtnGoLogin = (Button) view.findViewById(R.id.f_register_btn_login);

        //Working with widgets
        mEtUsername.setFilters(new InputFilter[]{new NoSpaceInputFilter()});
        mEtPassword.setFilters(new InputFilter[]{new NoSpaceInputFilter()});
        mEtPasswordConfirm.setFilters(new InputFilter[]{new NoSpaceInputFilter()});

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSwitch(true);
                if(valid()){
                    mCallback.onRegisterSubmit(mEtUsername.getText().toString(),
                            mEtPassword.getText().toString());
                }else{
                    btnSwitch(false);
                }
            }
        });


        mBtnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onGoToLogin(mEtUsername.getText().toString(),
                        mEtPassword.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mEtUsername != null && mEtPassword != null){
            if(getArguments() != null){
                mEtUsername.setText(getArguments().getString(EXTRA_USERNAME, ""));
                mEtPassword.setText(getArguments().getString(EXTRA_PASSWORD, ""));
            }
            if(mEtUsername.getText().toString().isEmpty()){
                mEtUsername.requestFocus();
            }
        }
        if(getView()!= null){
            getView().post(
                    new Runnable() {
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInputFromWindow(mEtUsername.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                            mEtUsername.requestFocus();
                        }
                    });
        }
    }

    //====================== METHODS ========================

    private boolean valid(){
        if(mEtUsername != null && mEtPassword != null && mEtPasswordConfirm != null){
            if(mEtUsername.length() < 4 || mEtUsername.length() > 10){
                mCallback.onAppError(AppError.ErrorType.INVALID_TEXT_LENGHT);
                mEtUsername.requestFocus();
                return false;
            }
            if(mEtUsername.getText().toString().isEmpty()){
                mCallback.onAppError(AppError.ErrorType.USERNAME_CANNOT_BE_EMPTY);
                mEtUsername.requestFocus();
                return false;
            }
            if(mEtPassword.getText().toString().isEmpty()){
                mCallback.onAppError(AppError.ErrorType.PASSWORD_CANNOT_BE_EMPTY);
                mEtPassword.hasFocus();
                return false;
            }
            if(mEtPasswordConfirm.getText().toString().isEmpty()){
                mCallback.onAppError(AppError.ErrorType.PASSWORD_CONFIRMATION_CANNOT_BE_EMPTY);
                mEtPasswordConfirm.requestFocus();
                return false;
            }
            if(!mEtPassword.getText().toString().equals(mEtPasswordConfirm.getText().toString())){
                mCallback.onAppError(AppError.ErrorType.PASSWORD_CONFIRMATION_DIFFERENT);
                mEtPasswordConfirm.requestFocus();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Method that block buttons so they are not pressed twice, also sets the progressDialog
     * @param value boolean: True to block/ False to unlock
     */
    public void btnSwitch(boolean value){
        if(value){
            mBtnRegister.setEnabled(false);
            mBtnGoLogin.setEnabled(false);
            pDialog = ProgressDialog.show(getActivity(), getString(R.string.loading), "", true, false);
        }else {
            mBtnRegister.setEnabled(true);
            mBtnGoLogin.setEnabled(true);
            pDialog.dismiss();
        }

    }

    //============= INNER CLASSES / INTERFACES ==============
    public interface OnRegisterSelected extends OnAppErrorListener{
        void onRegisterSubmit(String username, String password);
        void onGoToLogin(String username, String password);
    }
}
