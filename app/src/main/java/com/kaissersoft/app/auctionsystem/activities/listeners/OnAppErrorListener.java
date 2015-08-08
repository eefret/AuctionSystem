package com.kaissersoft.app.auctionsystem.activities.listeners;

import com.kaissersoft.app.auctionsystem.activities.fragments.dialog.AppError;

/**
 * Created by eefret on 08/03/15.
 */
public interface OnAppErrorListener {
    void onAppError(AppError.ErrorType error);
}
