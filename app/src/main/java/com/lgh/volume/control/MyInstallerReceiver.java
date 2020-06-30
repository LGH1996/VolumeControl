package com.lgh.volume.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyInstallerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            String action = intent.getAction();
            if (action != null) {
                String dataString = intent.getDataString();
                String packageName = dataString != null ? dataString.substring(8) : null;
                if (packageName != null && (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_FULLY_REMOVED)) && MyAccessibilityService.mainFunction != null) {
                    MyAccessibilityService.mainFunction.updatePlayerSet();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
