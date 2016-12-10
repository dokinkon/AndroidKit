package com.dokinkon.androidkit;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dokinkon on 2016/12/5.
 */

public class TextValidator {

    public static boolean isValidEmail(@NonNull String email) {
        return false;
    }

    public static boolean isValidPhone(@NonNull String phone) {
        Pattern pattern = Pattern.compile("\\d{10}");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
