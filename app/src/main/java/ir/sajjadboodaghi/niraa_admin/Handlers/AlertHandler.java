package ir.sajjadboodaghi.niraa_admin.Handlers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertNoticeCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertProblemCallback;
import ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces.AlertQuestionCallback;
import ir.sajjadboodaghi.niraa_admin.R;

/**
 * Created by Sajjad on 05/15/2018.
 */

public class AlertHandler {
    public static void alertQuestionMaker(Context context, String message, final AlertQuestionCallback callback) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(context.getResources().getString(R.string.alert_dialog_title_danger));
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        alertBuilder.setPositiveButton(context.getResources().getString(R.string.alert_dialog_button_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.yes();
            }
        });

        alertBuilder.setNegativeButton(context.getResources().getString(R.string.alert_dialog_button_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.no();
            }
        });

        alertBuilder.show();
    }

    public static void alertProblemMaker(Context context, String message, final AlertProblemCallback callback) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(context.getResources().getString(R.string.alert_dialog_title_error));
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        alertBuilder.setNeutralButton(context.getResources().getString(R.string.alert_dialog_button_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.tryAgain();
            }
        });

        alertBuilder.setNegativeButton(context.getResources().getString(R.string.alert_dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.cancel();
            }
        });

        alertBuilder.show();
    }
    public static void alertNoticeMaker(Context context, String message, final AlertNoticeCallback callback) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(context.getResources().getString(R.string.alert_dialog_title_notice));
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        alertBuilder.setNeutralButton(context.getResources().getString(R.string.alert_dialog_button_understand), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.okay();
            }
        });

        alertBuilder.show();
    }
}
