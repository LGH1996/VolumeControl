package com.lgh.volume.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;

public class MyMainFunction {
    private final AccessibilityService service;
    private final AccessibilityServiceInfo serviceInfo;
    private final SettingData settingData;
    private final AudioManager audioManager;
    private final Vibrator vibrator;
    private final Handler handler;
    private final MyScreenOnOffReceiver screenOffReceiver;
    private boolean doublePress;
    private boolean isUpPress, isDownPress;
    private long starUp, starDown;

    private final Runnable volumeUpRun = new Runnable() {
        @Override
        public void run() {
            if (isDownPress) {
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                vibrator.vibrate(settingData.vibrationStrength);
            } else if (isUpPress && audioManager.isMusicActive()) {
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                vibrator.vibrate(settingData.vibrationStrength);
            }
        }
    };

    private final Runnable volumeDownRun = new Runnable() {
        @Override
        public void run() {
            if (isUpPress) {
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                vibrator.vibrate(settingData.vibrationStrength);
            } else if (isDownPress && audioManager.isMusicActive()) {
                sendMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT);
                vibrator.vibrate(settingData.vibrationStrength);
            }
        }
    };

    public MyMainFunction(AccessibilityService accessibilityService) {
        service = accessibilityService;
        serviceInfo = accessibilityService.getServiceInfo();
        settingData = new SettingData(accessibilityService);
        audioManager = accessibilityService.getSystemService(AudioManager.class);
        vibrator = accessibilityService.getSystemService(Vibrator.class);
        handler = new Handler();
        screenOffReceiver = new MyScreenOnOffReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        accessibilityService.registerReceiver(screenOffReceiver, intentFilter);
        setInOnOff(settingData.onOff);
    }

    boolean onKeyEvent(KeyEvent event) {
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            starUp = event.getEventTime();
                            isUpPress = true;
                            doublePress = false;
                            if (!isDownPress) {
                                handler.postDelayed(volumeUpRun, settingData.longPressTime);
                            } else {
                                doublePress = true;
                            }
                            break;
                        case KeyEvent.ACTION_UP:
                            handler.removeCallbacks(volumeUpRun);
                            isUpPress = false;
                            if (!doublePress && event.getEventTime() - starUp < settingData.longPressTime) {
                                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            }
                            break;
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
                            starDown = event.getEventTime();
                            isDownPress = true;
                            doublePress = false;
                            if (!isUpPress) {
                                handler.postDelayed(volumeDownRun, settingData.longPressTime);
                            } else {
                                doublePress = true;
                            }
                            break;
                        case KeyEvent.ACTION_UP:
                            handler.removeCallbacks(volumeDownRun);
                            isDownPress = false;
                            if (!doublePress && event.getEventTime() - starDown < settingData.longPressTime) {
                                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            }
                            break;
                    }
                    return true;
                default:
                    return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    void onUnBind() {
        service.unregisterReceiver(screenOffReceiver);
    }

    public SettingData getSettingData() {
        return settingData;
    }

    void setInScreenOnOff(boolean b) {
        if (settingData.onOff && settingData.onlyEffectInScreenOff) {
            setRealOnOff(!b);
        }
    }

    void setInOnOff(boolean onOff) {
        if (!settingData.onlyEffectInScreenOff) {
            setRealOnOff(onOff);
        }
    }

    void setInOnlyEffectInScreenOffOnOff(boolean b) {
        if (settingData.onOff) {
            setRealOnOff(!b);
        }
    }

    void setRealOnOff(boolean onOff) {
        if (onOff) {
            serviceInfo.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        } else {
            serviceInfo.flags &= ~AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        }
        service.setServiceInfo(serviceInfo);
    }

    private void sendMediaButton(int keycode) {
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keycode);
        audioManager.dispatchMediaKeyEvent(downEvent);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, keycode);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }
}
