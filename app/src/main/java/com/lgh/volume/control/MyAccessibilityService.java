package com.lgh.volume.control;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;


public class MyAccessibilityService extends AccessibilityService {

    public static MyMainFunction myMainFunction;

    @Override
    protected void onServiceConnected() {
        myMainFunction = new MyMainFunction(this);
    }

    public boolean onKeyEvent(KeyEvent event) {
        return myMainFunction.onKeyEvent(event);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        myMainFunction.onUnBind();
        myMainFunction = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
}
