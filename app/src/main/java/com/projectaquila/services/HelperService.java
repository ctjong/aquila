package com.projectaquila.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import com.projectaquila.common.Callback;
import com.projectaquila.contexts.AppContext;

import java.util.HashMap;
import java.util.Locale;

public class HelperService {
    /**
     * Convert the given integer to string
     * @param i integer
     * @return string
     */
    public static String toString(int i){
        Locale currentLocale;
        Context ctx = AppContext.getCurrent().getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = ctx.getResources().getConfiguration().getLocales().get(0);
        }else{
            currentLocale = ctx.getResources().getConfiguration().locale;
        }
        return String.format(currentLocale, "%d", i);
    }

    /**
     * Safe-print the given string. If it is null, return "null"
     * @param str input string
     * @return safe print
     */
    public static String safePrint(String str){
        if(str == null)
            return "null";
        return str;
    }

    /**
     * Create a map with one string-object pair
     * @param key pair key
     * @param val pair value
     * @return map
     */
    public static HashMap<String, Object> getSinglePairMap(String key, Object val){
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, val);
        return map;
    }

    /**
     * Show an alert dialog with yes/no buttons
     * @param titleStrId string ID for dialog title
     * @param msgStrId string ID for dialog message
     * @param yesCb click handler for YES
     * @param noCb click handler for NO
     */
    public static void showAlert(int titleStrId, int msgStrId, final Callback yesCb, final Callback noCb){
        AlertDialog.Builder builder;
        Context ctx = AppContext.getCurrent().getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ctx);
        }
        builder.setTitle(titleStrId)
            .setMessage(msgStrId)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(yesCb != null) yesCb.execute(null);
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(noCb != null) noCb.execute(null);
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    /**
     * Return a spanned element containing a HTML text
     * @param raw raw HTML text
     * @return span element containing HTML parsed text
     */
    public static Spanned fromHtml(String raw){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(raw, Html.FROM_HTML_MODE_COMPACT);
        }else{
            return Html.fromHtml(raw);
        }
    }
}
