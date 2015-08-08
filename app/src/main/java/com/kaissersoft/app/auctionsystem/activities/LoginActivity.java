package com.kaissersoft.app.auctionsystem.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.kaissersoft.app.auctionsystem.AuctionSystem;
import com.kaissersoft.app.auctionsystem.R;
import com.kaissersoft.app.auctionsystem.activities.fragments.LoginFragment;
import com.kaissersoft.app.auctionsystem.activities.fragments.RegisterFragment;
import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.AppError;
import com.kaissersoft.app.auctionsystem.greendao.DaoSession;
import com.kaissersoft.app.auctionsystem.greendao.User;
import com.kaissersoft.app.auctionsystem.greendao.UserDao;
import com.kaissersoft.app.auctionsystem.util.Logger;
import com.kaissersoft.app.auctionsystem.util.Preferences;
import com.kaissersoft.app.auctionsystem.util.Util;

import java.util.Date;

import de.greenrobot.dao.DaoException;
import me.alexrs.prefs.lib.Prefs;

public class LoginActivity extends AppCompatActivity
    implements  LoginFragment.OnLoginSelected,
        RegisterFragment.OnRegisterSelected{

    //====================== FIELDS =========================
    private DaoSession daoSession;
    private Logger log = Logger.getLogger();

    //===============+ OVERRIDEN METHODS ====================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Prefs.with(this).getLong(Preferences.CURRENT_USER_ID,0L)!=0L){
            Intent i = new Intent(this,AuctionActivity.class);
            i.putExtra(AuctionActivity.EXTRA_USER_ID,
                    Prefs.with(this).getLong(Preferences.CURRENT_USER_ID,0L));
            startActivity(i);
        }
        setContentView(R.layout.activity_login);

        if (findViewById(R.id.fragment_container) != null){

            if(savedInstanceState != null){
                return;
            }

            LoginFragment loginFragment = LoginFragment.newInstance(null, null);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, loginFragment, LoginFragment.TAG)
                    .commit();
        }

        daoSession = AuctionSystem.getInstance().getDaoSession();
    }

    @Override
    public void onLoginSubmit(String username, String password) {
        //TODO apply remote login here
        LoginFragment f = (LoginFragment) getSupportFragmentManager()
                .findFragmentByTag(LoginFragment.TAG);
        User user;
        try {
            user =  daoSession.getUserDao().queryBuilder()
                    .where(UserDao.Properties.Password.eq(Util.md5Encrypt(password))
                            , UserDao.Properties.Username.eq(username))
                    .uniqueOrThrow();
        }catch (DaoException e){
            if(f != null){
                f.btnSwitch(false);
            }
            onAppError(AppError.ErrorType.INVALID_CREDENTIALS);
            return;
        }

        if(f != null){
            f.btnSwitch(false);
        }



        //Saving ID and sending to AuctionActivity
        Prefs.with(this).save(Preferences.CURRENT_USER_ID, user.getId());
        Intent i = new Intent(this, AuctionActivity.class);
        i.putExtra(AuctionActivity.EXTRA_USER_ID, user.getId());

        //Resetting services
        Util.setAuctionWatcher(this);
        Util.setBidderBot(this);

        startActivity(i);

    }

    @Override
    public void onGoToRegister(String username, String password) {
        RegisterFragment registerFragment = RegisterFragment.newInstance(username, password);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, registerFragment, RegisterFragment.TAG)
                .commit();
    }

    @Override
    public void onRegisterSubmit(String username, String password) {
        //TODO apply remote registry here
        RegisterFragment f = (RegisterFragment) getSupportFragmentManager()
                .findFragmentByTag(RegisterFragment.TAG);
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(Util.md5Encrypt(password));
            user.setCreated_at(new Date());
            daoSession.getUserDao().insert(user);
        }catch (SQLiteConstraintException e1){
            onAppError(AppError.ErrorType.USER_ALREADY_EXIST);
            if(f != null){
                f.btnSwitch(false);
            }
            return;
        } catch (Exception e){
            log.e(e.getLocalizedMessage());
            onAppError(AppError.ErrorType.UNEXPECTED_ERROR);
            if(f != null){
                f.btnSwitch(false);
            }
            return;
        }
        //Send to login to apply login logic
        onLoginSubmit(username, password);
    }

    @Override
    public void onGoToLogin(String username, String password) {
        LoginFragment loginFragment = LoginFragment.newInstance(username, password);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, loginFragment, LoginFragment.TAG)
                .commit();
    }

    @Override
    public void onAppError(AppError.ErrorType error) {
        AppError dialog = AppError.newInstance(error);
        dialog.show(getSupportFragmentManager(), AppError.TAG);
    }

    //====================== METHODS ========================


    //============= INNER CLASSES / INTERFACES ==============

}
