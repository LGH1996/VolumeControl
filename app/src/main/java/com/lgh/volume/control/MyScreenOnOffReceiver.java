package com.lgh.volume.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyScreenOnOffReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action != null && MyAccessibilityService.myMainFunction != null) {
                if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    MyAccessibilityService.myMainFunction.setInScreenOnOff(true);
                }
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    MyAccessibilityService.myMainFunction.setInScreenOnOff(false);
                }
            }
        } catch (Throwable e) {
            // e.printStackTrace();
        }
    }
}
