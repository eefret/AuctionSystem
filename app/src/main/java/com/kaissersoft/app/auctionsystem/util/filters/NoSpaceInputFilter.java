package com.kaissersoft.app.auctionsystem.util.filters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by eefret on 08/03/15.
 */
public class NoSpaceInputFilter implements InputFilter{
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
        for (int i= start; i < end; i++){
            if (Character.isSpaceChar(source.charAt(i))){
                return "";
            }
        }
        return null;
    }
}

