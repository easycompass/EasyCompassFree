package com.namdin.easycompass.ui.rate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.namdin.easycompass.R;
import com.namdin.easycompass.utils.Constants;

public class PolicyDialog extends DialogFragment {

    public static final String DISABLE_POLICY = "DISABLE_POLICY";
    public static final String PREF_NAME_POLICY = "dialog_policy";

    public PolicyDialog() {
    }

    public static void showDialog (Context context, FragmentManager fragmentManager){

        boolean shouldShow = false;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (!sharedPreferences.getBoolean(DISABLE_POLICY, false)) {
            shouldShow = true;
        }
        if (shouldShow) {
            new PolicyDialog().show(fragmentManager, "policy");
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME_POLICY, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.message_dialog_policy_app)
                .setPositiveButton(R.string.button_agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences(getActivity()).edit().putBoolean(DISABLE_POLICY,true).commit();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.button_onlie_help, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_POLICY));
                        startActivity(browserIntent);
                    }
                }).create();

    }
}
