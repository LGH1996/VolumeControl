package com.lgh.volume.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyScreenOnOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        try {
            String action = intent.getAction();
            if (action != null && MyAccessibilityService.mainFunction != null) {
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    MyAccessibilityService.mainFunction.setInScreenOnOff(true);
                }
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    MyAccessibilityService.mainFunction.setInScreenOnOff(false);
                }
            }
        } catch (Throwable e) {
//            e.printStackTrace();
        }
    }
}
