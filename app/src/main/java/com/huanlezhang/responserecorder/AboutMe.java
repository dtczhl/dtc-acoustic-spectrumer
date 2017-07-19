package com.huanlezhang.responserecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * You can reuse my codes freely. I would appreciate if you could keep my name and website address.
 * Name: Huanle Zhang
 * Personal website: www.huanlezhang.com
 */
public class AboutMe {
    static public void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = "This app displays the freq response of received sounds. \n" +
                "X-axis: 0-24 kHz; Y-axis: log10-scale (dB). \n" +
                 "About me:\nMy name is Huanle Zhang. Welcome to my personal website for more info. www.huanlezhang.com";
        builder.setMessage(message).setTitle("About");
        builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
