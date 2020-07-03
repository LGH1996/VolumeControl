package com.lgh.volume.control;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingData {
    public static String SETTING_DATA = "Setting.Data";
    public static String SETTING_ON_OFF = "Setting.On.Off";
    public static String SETTING_MODEL = "Setting.Model";
    public static String SETTING_VIBRATION_STRENGTH = "Setting.Vibration.Strength";
    public static String SETTING_AUTO_HIDE_IN_TASK = "Setting.Auto.Hide.In.Task";
    public static String SETTING_ONLY_EFFECT_IN_SCREEN_OFF = "Setting.Only.Effect.In.Screen.Off";

    public SharedPreferences sharedPreferences;
    public boolean onOff;
    public int model;
    public int vibrationStrength;
    public boolean autoHideInTask;
    public boolean onlyEffectInScreenOff;

    public SettingData(Context context) {
        sharedPreferences = context.getSharedPreferences(SETTING_DATA, Context.MODE_PRIVATE);
        onOff = sharedPreferences.getBoolean(SETTING_ON_OFF, true);
        model = sharedPreferences.getInt(SETTING_MODEL, 1);
        vibrationStrength = sharedPreferences.getInt(SETTING_VIBRATION_STRENGTH, 50);
        autoHideInTask = sharedPreferences.getBoolean(SETTING_AUTO_HIDE_IN_TASK, true);
        onlyEffectInScreenOff = sharedPreferences.getBoolean(SETTING_ONLY_EFFECT_IN_SCREEN_OFF, false);
    }

    public void setOnOff(boolean onOff) {
        this.onOff = onOff;
        sharedPreferences.edit().putBoolean(SETTING_ON_OFF, onOff).apply();
        if (MyAccessibilityService.mainFunction != null) {
            MyAccessibilityService.mainFunction.setInOnOff(onOff);
        }
    }

    public void setModel(int model) {
        this.model = model;
        sharedPreferences.edit().putInt(SETTING_MODEL, model).apply();
    }

    public void setVibrationStrength(int vibrationStrength) {
        this.vibrationStrength = vibrationStrength;
        sharedPreferences.edit().putInt(SETTING_VIBRATION_STRENGTH, vibrationStrength).apply();
    }

    public void setAutoHideInTask(boolean autoHideInTask) {
        this.autoHideInTask = autoHideInTask;
        sharedPreferences.edit().putBoolean(SETTING_AUTO_HIDE_IN_TASK, autoHideInTask).apply();
    }

    public void setOnlyEffectInScreenOff(boolean onlyEffectInScreenOff) {
        this.onlyEffectInScreenOff = onlyEffectInScreenOff;
        sharedPreferences.edit().putBoolean(SETTING_ONLY_EFFECT_IN_SCREEN_OFF, onlyEffectInScreenOff).apply();
        if (MyAccessibilityService.mainFunction != null) {
            MyAccessibilityService.mainFunction.setInOnlyEffectInScreenOffOnOff(onlyEffectInScreenOff);
        }
    }
}
