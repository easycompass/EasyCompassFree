package com.namdin.easycompass.ui.rate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.namdin.easycompass.R;


public class RateAppDialog extends DialogFragment {

    private static final int DAYS_UNTIL_PROMPT = 3;
    private static final int LAUNCHES_UNTIL_PROMPT = 10;
    private static final int MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
    private static final String PREF_NAME = "app_rater";
    private static final String LAST_PROMPT = "LAST_PROMPT";
    private static final String LAUNCHES = "LAUNCHES";
    private static final String DISABLED = "DISABLED";

    public static void showDialog(Context context, FragmentManager fragmentManager) {
        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTime = System.currentTimeMillis();
        long lastPromptTime = sharedPreferences.getLong(LAST_PROMPT, 0);
        if (lastPromptTime == 0) {
            lastPromptTime = currentTime;
            editor.putLong(LAST_PROMPT, lastPromptTime);
        }
        if (!sharedPreferences.getBoolean(DISABLED, false)) {
            int launchs = sharedPreferences.getInt(LAUNCHES, 0) + 1;
            if (launchs > LAUNCHES_UNTIL_PROMPT || currentTime > lastPromptTime + MILLIS_UNTIL_PROMPT) {
                shouldShow = true;
            }
            editor.putInt(LAUNCHES, launchs);
        }

        if (shouldShow) {
            editor.putInt(LAUNCHES, 0)
                    .putLong(LAST_PROMPT, System.currentTimeMillis()).commit();
            new RateAppDialog().show(fragmentManager, "rate");
        } else {
            editor.commit();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_rate_app)
                .setIcon(R.drawable.ic_emoticon_white)
                .setMessage(R.string.message_dialog_rate_app)
                .setPositiveButton(R.string.button_rate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doPositiveClick();
                    }
                })
                .setNegativeButton(R.string.button_rate_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doNegativeClick();
                    }
                })
                .setNeutralButton(R.string.button_no_thanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doNeutralClick();

                    }
                }).create();
    }

    private void doNeutralClick() {
        getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).commit();
        getDialog().dismiss();
    }

    private void doNegativeClick() {
        getDialog().dismiss();
    }

    private void doPositiveClick() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
        getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).commit();
        getDialog().dismiss();
    }
}
