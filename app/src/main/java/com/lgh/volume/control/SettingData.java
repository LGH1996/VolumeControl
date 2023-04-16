package com.lgh.volume.control;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingData {
    public static String SETTING_DATA = "Setting.Data";
    public static String SETTING_ON_OFF = "Setting.On.Off";
    public static String SETTING_AUTO_HIDE_IN_TASK = "Setting.Auto.Hide.In.Task";
    public static String SETTING_ONLY_EFFECT_IN_SCREEN_OFF = "Setting.Only.Effect.In.Screen.Off";
    public static String SETTING_VIBRATION_STRENGTH = "Setting.Vibration.Strength";
    public static String SETTING_LONG_PRESS_TIME = "Setting.Long.Press.Time";

    public SharedPreferences sharedPreferences;
    public boolean onOff;
    public boolean autoHideInTask;
    public boolean onlyEffectInScreenOff;
    public int vibrationStrength;
    public int longPressTime;

    public SettingData(Context context) {
        sharedPreferences = context.getSharedPreferences(SETTING_DATA, Context.MODE_PRIVATE);
        onOff = sharedPreferences.getBoolean(SETTING_ON_OFF, true);
        autoHideInTask = sharedPreferences.getBoolean(SETTING_AUTO_HIDE_IN_TASK, true);
        onlyEffectInScreenOff = sharedPreferences.getBoolean(SETTING_ONLY_EFFECT_IN_SCREEN_OFF, false);
        vibrationStrength = sharedPreferences.getInt(SETTING_VIBRATION_STRENGTH, 50);
        longPressTime = sharedPreferences.getInt(SETTING_LONG_PRESS_TIME, 800);
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
        sharedPreferences.edit().putBoolean(SETTING_ON_OFF, onOff).apply();
        if (MyAccessibilityService.myMainFunction != null) {
            MyAccessibilityService.myMainFunction.setInOnOff(onOff);
        }
    }

    public void setAutoHideInTask(boolean autoHideInTask) {
        this.autoHideInTask = autoHideInTask;
        sharedPreferences.edit().putBoolean(SETTING_AUTO_HIDE_IN_TASK, autoHideInTask).apply();
    }

    public void setOnlyEffectInScreenOff(boolean onlyEffectInScreenOff) {
        this.onlyEffectInScreenOff = onlyEffectInScreenOff;
        sharedPreferences.edit().putBoolean(SETTING_ONLY_EFFECT_IN_SCREEN_OFF, onlyEffectInScreenOff).apply();
        if (MyAccessibilityService.myMainFunction != null) {
            MyAccessibilityService.myMainFunction.setInOnlyEffectInScreenOffOnOff(onlyEffectInScreenOff);
        }
    }

    public void setVibrationStrength(int vibrationStrength) {
        this.vibrationStrength = vibrationStrength;
        sharedPreferences.edit().putInt(SETTING_VIBRATION_STRENGTH, vibrationStrength).apply();
    }

    public void setLongPressTime(int longPressTime) {
        this.longPressTime = longPressTime;
        sharedPreferences.edit().putInt(SETTING_LONG_PRESS_TIME, longPressTime).apply();
    }
}
