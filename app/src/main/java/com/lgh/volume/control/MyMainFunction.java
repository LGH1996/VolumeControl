package com.lgh.volume.control;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyMainFunction {

    private AccessibilityService service;
    private static String TAG = "MyAccessibilityService";
    private boolean double_press;
    private boolean isUpPress, isDownPress;
    private long star_up, star_down;
    private long interval;
    private int clickCount;
    private boolean isBegin;
    private boolean isPlayPause;
    private ScheduledFuture future_v;
    private ScheduledExecutorService executorService;
    private MediaButtonControl mediaButtonControl;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private int vibration_strength;
    private MyScreenOffReceiver screenOffReceiver;

    public MyMainFunction(AccessibilityService service){
        this.service = service;
    }

    void onConnect(){
        executorService = Executors.newSingleThreadScheduledExecutor();
        mediaButtonControl = new MediaButtonControl(service);
        audioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) service.getSystemService(Context.VIBRATOR_SERVICE);
        vibration_strength = 50;
        screenOffReceiver = new MyScreenOffReceiver();
        service.registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        future_v = executorService.schedule(new Runnable() {
            @Override
            public void run() {
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    boolean onKeyEvent(KeyEvent event){
//        Log.i(TAG, KeyEvent.keyCodeToString(event.getKeyCode())+"-"+event.getAction());
        try {
            switch (event.getKeyCode()) {
//                case KeyEvent.KEYCODE_VOLUME_UP:
//                    switch (event.getAction()) {
//                        case KeyEvent.ACTION_DOWN:
//                            Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_UP -> KeyEvent.ACTION_DOWN");
//                            if (!audioManager.isMusicActive()){
//                                return false;
//                            }
//                            star_up = System.currentTimeMillis();
//                            is_release_up = false;
//                            double_press = false;
//                            if (is_release_down) {
//                                future_v = executorService.schedule(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_UP -> THREAD");
//                                        if (!is_release_down) {
//                                            mediaButtonControl.play_pause_Music();
//                                            vibrator.vibrate(vibration_strength);
//                                        } else if (!is_release_up ) {
//                                            mediaButtonControl.nextMusic();
//                                            vibrator.vibrate(vibration_strength);
//                                        }
//                                    }
//                                }, 800, TimeUnit.MILLISECONDS);
//                            } else {
//                                double_press = true;
//                            }
//                            break;
//                        case KeyEvent.ACTION_UP:
//                            Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_UP -> KeyEvent.ACTION_UP");
//                            future_v.cancel(false);
//                            is_release_up = true;
//                            if (!double_press && System.currentTimeMillis() - star_up < 800) {
//                                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
//                            }
//                            break;
//                    }
//                    return true;
//                case KeyEvent.KEYCODE_VOLUME_DOWN:
//                    switch (event.getAction()) {
//                        case KeyEvent.ACTION_DOWN:
//                            Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN -> KeyEvent.ACTION_DOWN");
//                            if (!audioManager.isMusicActive()){
//                                return false;
//                            }
//                            star_down = System.currentTimeMillis();
//                            is_release_down = false;
//                            double_press = false;
//                            if (is_release_up) {
//                                future_v = executorService.schedule(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN -> THREAD");
//                                        if (!is_release_up) {
//                                            mediaButtonControl.play_pause_Music();
//                                            vibrator.vibrate(vibration_strength);
//                                        } else if (!is_release_down) {
//                                            mediaButtonControl.previousMusic();
//                                            vibrator.vibrate(vibration_strength);
//                                        }
//                                    }
//                                }, 800, TimeUnit.MILLISECONDS);
//
//                            } else {
//                                double_press = true;
//                            }
//                            break;
//                        case KeyEvent.ACTION_UP:
//                            Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN -> KeyEvent.ACTION_UP");
//                            future_v.cancel(false);
//                            is_release_down = true;
//                            if (!double_press && System.currentTimeMillis() - star_down < 800) {
//                                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
//                            }
//                            break;
//                    }
//                    return true;
//                default:
////                    Log.i(TAG,KeyEvent.keyCodeToString(event.getKeyCode()));
//                    return false;
                case KeyEvent.KEYCODE_VOLUME_DOWN:

                   if (event.getAction()==KeyEvent.ACTION_DOWN ){
                       Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN");
                       if (isUpPress){
                           mediaButtonControl.play_pause_Music();
                           vibrator.vibrate(vibration_strength);
                           isPlayPause = true;
                           Log.i(TAG,"fffffffffffffffffffffffffffff");
                           return true;
                       }

                       if (!isBegin) {
                           executorService.schedule(new Runnable() {
                               @Override
                               public void run() {
                                   if (!isPlayPause){

                                       if (clickCount==0 || !audioManager.isMusicActive()) {
                                           audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                                       } else {
                                           mediaButtonControl.previousMusic();
                                           vibrator.vibrate(vibration_strength);
                                       }

                                   }
                                   isPlayPause = false;
                                   isBegin = false;
                                   isDownPress = false;
                                   clickCount =0;
                               }
                           }, 200, TimeUnit.MILLISECONDS);
                           isBegin = true;
                           isDownPress= true;
                       }

                       long curTimeDown = event.getEventTime();
                       if ((curTimeDown - interval)<200){
                           clickCount++;
                       }

                       interval = curTimeDown;
                       return true;
                   }

                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (event.getAction()==KeyEvent.ACTION_DOWN ){
                        Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_UP");
                        if (isDownPress){
                            mediaButtonControl.play_pause_Music();
                            vibrator.vibrate(vibration_strength);
                            isPlayPause = true;
                            Log.i(TAG,"fffffffffffffffffffffffffffff");
                            return true;
                        }


                        if (!isBegin){
                            executorService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isPlayPause){
                                        if (clickCount ==0 ||  !audioManager.isMusicActive()) {
                                        audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                                    } else {
                                        mediaButtonControl.nextMusic();
                                        vibrator.vibrate(vibration_strength);
                                    }

                                    }
                                    isPlayPause = false;
                                    isBegin = false;
                                    isUpPress = false;
                                    clickCount = 0;
                                }
                            }, 200, TimeUnit.MILLISECONDS);
                            isBegin = true;
                            isUpPress = true;
                        }

                        long curTimeUp = event.getEventTime();
                        if ((curTimeUp - interval)<200){
                            clickCount++;
                        }

                        interval = curTimeUp;
                        return true;
                    }

                    default:
                        return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    void onUnBind(){
        service.unregisterReceiver(screenOffReceiver);
    }

    void onScreenOff(){

    }

    void model_1(){

    }

    boolean model_2(KeyEvent event){
        try {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_VOLUME_DOWN:

                    if (event.getAction()==KeyEvent.ACTION_DOWN ){
                        Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_DOWN");
                        if (isUpPress){
                            mediaButtonControl.play_pause_Music();
                            vibrator.vibrate(vibration_strength);
                            isPlayPause = true;
                            Log.i(TAG,"fffffffffffffffffffffffffffff");
                            return true;
                        }

                        if (!isBegin) {
                            executorService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isPlayPause){

                                        if (clickCount==0 || !audioManager.isMusicActive()) {
                                            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                                        } else {
                                            mediaButtonControl.previousMusic();
                                            vibrator.vibrate(vibration_strength);
                                        }

                                    }
                                    isPlayPause = false;
                                    isBegin = false;
                                    isDownPress = false;
                                    clickCount =0;
                                }
                            }, 200, TimeUnit.MILLISECONDS);
                            isBegin = true;
                            isDownPress= true;
                        }

                        long curTimeDown = event.getEventTime();
                        if ((curTimeDown - interval)<200){
                            clickCount++;
                        }

                        interval = curTimeDown;
                        return true;
                    }

                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (event.getAction()==KeyEvent.ACTION_DOWN ){
                        Log.i(TAG,"KeyEvent.KEYCODE_VOLUME_UP");
                        if (isDownPress){
                            mediaButtonControl.play_pause_Music();
                            vibrator.vibrate(vibration_strength);
                            isPlayPause = true;
                            Log.i(TAG,"fffffffffffffffffffffffffffff");
                            return true;
                        }


                        if (!isBegin){
                            executorService.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isPlayPause){
                                        if (clickCount ==0 ||  !audioManager.isMusicActive()) {
                                            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                                        } else {
                                            mediaButtonControl.nextMusic();
                                            vibrator.vibrate(vibration_strength);
                                        }

                                    }
                                    isPlayPause = false;
                                    isBegin = false;
                                    isUpPress = false;
                                    clickCount = 0;
                                }
                            }, 200, TimeUnit.MILLISECONDS);
                            isBegin = true;
                            isUpPress = true;
                        }

                        long curTimeUp = event.getEventTime();
                        if ((curTimeUp - interval)<200){
                            clickCount++;
                        }

                        interval = curTimeUp;
                        return true;
                    }

                default:
                    return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
            }
    }
