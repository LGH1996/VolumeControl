package com.lgh.volume.control;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.media.AudioManager;
import android.os.Vibrator;
import android.view.KeyEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyMainFunction {

    public SettingData settingData;
    private AccessibilityService service;
    private boolean double_press;
    private boolean isUpPress, isDownPress;
    private long star_up, star_down;
    private long interval;
    private int clickCount;
    private boolean isBegin;
    private boolean isPlayPause;
    private ScheduledFuture future_v;
    private ScheduledExecutorService executorService;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private MyScreenOnOffReceiver screenOffReceiver;
    private AccessibilityServiceInfo serviceInfo;

    public MyMainFunction(AccessibilityService service) {
        this.service = service;
    }

    void onConnect() {

        audioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
        executorService = Executors.newSingleThreadScheduledExecutor();
        serviceInfo = service.getServiceInfo();
        settingData = new SettingData(service);
        screenOffReceiver = new MyScreenOnOffReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        service.registerReceiver(screenOffReceiver, intentFilter);
        setInOnOff(settingData.onOff);
        future_v = executorService.schedule(new Runnable() {
            @Override
            public void run() {
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    boolean onKeyEvent(KeyEvent event) {
//        Log.i(TAG, KeyEvent.keyCodeToString(event.getKeyCode())+"-"+event.getAction());
        if (settingData.model == 1) {
            return model_1(event);
        }
        if (settingData.model == 2) {
            return model_2(event);
        }
        return false;
    }

    void onUnBind() {
        service.unregisterReceiver(screenOffReceiver);
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

    boolean model_1(KeyEvent event) {
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
//                            Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_UP -> KeyEvent.ACTION_DOWN");
                            star_up = event.getEventTime();
                            isUpPress = true;
                            double_press = false;
                            if (!isDownPress) {
                                future_v = executorService.schedule(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_UP -> THREAD");
                                        if (isDownPress) {
                                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                                            vibrator.vibrate(settingData.vibrationStrength);
                                        } else if (isUpPress && audioManager.isMusicActive()) {
                                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT);
                                            vibrator.vibrate(settingData.vibrationStrength);
                                        }
                                    }
                                }, 800, TimeUnit.MILLISECONDS);
                            } else {
                                double_press = true;
                            }
                            break;
                        case KeyEvent.ACTION_UP:
//                            Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_UP -> KeyEvent.ACTION_UP");
                            future_v.cancel(false);
                            isUpPress = false;
                            if (!double_press && event.getEventTime() - star_up < 800) {
                                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                            }
                            break;
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_DOWN:
//                            Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN -> KeyEvent.ACTION_DOWN");
                            star_down = event.getEventTime();
                            isDownPress = true;
                            double_press = false;
                            if (!isUpPress) {
                                future_v = executorService.schedule(new Runnable() {
                                    @Override
                                    public void run() {
//                                        Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN -> THREAD");
                                        if (isUpPress) {
                                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                                            vibrator.vibrate(settingData.vibrationStrength);
                                        } else if (isDownPress && audioManager.isMusicActive()) {
                                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                                            vibrator.vibrate(settingData.vibrationStrength);
                                        }
                                    }
                                }, 800, TimeUnit.MILLISECONDS);

                            } else {
                                double_press = true;
                            }
                            break;
                        case KeyEvent.ACTION_UP:
//                            Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN -> KeyEvent.ACTION_UP");
                            future_v.cancel(false);
                            isDownPress = false;
                            if (!double_press && event.getEventTime() - star_down < 800) {
                                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                            }
                            break;
                    }
                    return true;
                default:
//                    Log.i(TAG,KeyEvent.keyCodeToString(event.getKeyCode()));
                    return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean model_2(KeyEvent event) {
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
//                        Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN");
                        if (isUpPress) {
                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                            vibrator.vibrate(settingData.vibrationStrength);
                            isPlayPause = true;
                        } else {
                            if (!isBegin) {
                                executorService.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isPlayPause) {
                                            if (clickCount == 0 || !audioManager.isMusicActive()) {
                                                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                                            } else {
                                                sendMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                                                vibrator.vibrate(settingData.vibrationStrength);
                                            }
                                        }
                                        isPlayPause = false;
                                        isBegin = false;
                                        isDownPress = false;
                                        clickCount = 0;
                                    }
                                }, 350, TimeUnit.MILLISECONDS);
                                isBegin = true;
                                isDownPress = true;
                            }

                            long curTimeDown = event.getEventTime();
                            if ((curTimeDown - interval) < 350) {
                                clickCount++;
                            }

                            interval = curTimeDown;
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
//                        Log.i(TAG, "KeyEvent.KEYCODE_VOLUME_UP");
                        if (isDownPress) {
                            sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
                            vibrator.vibrate(settingData.vibrationStrength);
                            isPlayPause = true;
                        } else {
                            if (!isBegin) {
                                executorService.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isPlayPause) {
                                            if (clickCount == 0 || !audioManager.isMusicActive()) {
                                                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                                            } else {
                                                sendMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT);
                                                vibrator.vibrate(settingData.vibrationStrength);
                                            }
                                        }
                                        isPlayPause = false;
                                        isBegin = false;
                                        isUpPress = false;
                                        clickCount = 0;
                                    }
                                }, 350, TimeUnit.MILLISECONDS);
                                isBegin = true;
                                isUpPress = true;
                            }

                            long curTimeUp = event.getEventTime();
                            if ((curTimeUp - interval) < 350) {
                                clickCount++;
                            }

                            interval = curTimeUp;

                        }
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

    private void sendMediaButton(int keycode) {
        KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keycode);
        audioManager.dispatchMediaKeyEvent(downEvent);
        KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, keycode);
        audioManager.dispatchMediaKeyEvent(upEvent);
    }
}
