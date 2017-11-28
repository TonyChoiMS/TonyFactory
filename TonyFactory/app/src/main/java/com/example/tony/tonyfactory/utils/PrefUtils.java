package com.example.tony.tonyfactory.utils;

import android.content.Context;

import com.example.tony.tonyfactory.facebook.fbUser;

/**
 * Created by Administrator on 2016-11-29.
 */

public class PrefUtils {

    public static void setCurrentUser(fbUser currentUser, Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.putObject("current_user_value", currentUser);
        complexPreferences.commit();
    }

    public static fbUser getCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        fbUser currentUser = complexPreferences.getObject("current_user_value", fbUser.class);
        return currentUser;
    }

    public static void clearCurrentUser(Context ctx) {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_prefs", 0);
        complexPreferences.clearObject();
        complexPreferences.commit();
    }
}
