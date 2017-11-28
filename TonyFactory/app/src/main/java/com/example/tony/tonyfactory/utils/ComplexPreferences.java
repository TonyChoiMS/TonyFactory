package com.example.tony.tonyfactory.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016-11-29.
 */

public class ComplexPreferences {

    private static ComplexPreferences complexPreferences;
    private Context ctx;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static Gson GSON = new Gson();
    Type typeOfObject = new TypeToken<Object>() {
    }.getType();

    public ComplexPreferences(Context ctx, String namePref, int mode) {
        this.ctx = ctx;
        if(namePref == null || namePref.equals("")) {
            namePref = "complex_preferences";
        }
        pref = ctx.getSharedPreferences(namePref, mode);
        editor = pref.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context ctx,
                                                           String namePref, int mode) {
        complexPreferences = new ComplexPreferences(ctx, namePref, mode);

        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if(object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if (key.equals("") || key == null) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object));
    }

    public void commit() {
        editor.commit();
    }

    public void clearObject() {
        editor.clear();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = pref.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");
            }
        }
    }
}
