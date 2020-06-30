package com.lgh.volume.control;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;


public class MyAccessibilityService extends AccessibilityService {

    public static MyMainFunction mainFunction;

    @Override
    protected void onServiceConnected() {
        mainFunction = new MyMainFunction(this);
        mainFunction.onConnect();

    }

    public boolean onKeyEvent(KeyEvent event) {
        return mainFunction.onKeyEvent(event);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mainFunction.onUnBind();
        mainFunction = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}
