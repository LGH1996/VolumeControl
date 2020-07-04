package com.lgh.volume.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends Activity {

    Context context;

    RelativeLayout accessibilityOnOff;
    RelativeLayout batteryIgnore;
    RelativeLayout appDetail;
    ImageView accessibilityOnOffImg;
    ImageView batteryIgnoreOnOffImg;

    Switch allOnOff;
    CheckBox model_1;
    CheckBox model_2;
    Switch onlyEffectInScreenOff;
    Switch autoHideInTask;
    SeekBar vibrationStrength;

    SettingData settingData;
    boolean inCurPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        settingData = MyAccessibilityService.mainFunction != null ? MyAccessibilityService.mainFunction.settingData : new SettingData(context);

        accessibilityOnOff = findViewById(R.id.accessibility_on_off);
        batteryIgnore = findViewById(R.id.batteryIgnore_on_off);
        appDetail = findViewById(R.id.app_detail);
        accessibilityOnOffImg = findViewById(R.id.accessibility_on_off_img);
        batteryIgnoreOnOffImg = findViewById(R.id.batteryIgnore_on_off_img);
        View.OnClickListener authorityOnOff = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.accessibility_on_off:
                        Intent intentAccessibility = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intentAccessibility);
                        inCurPage = false;
                        break;
                    case R.id.batteryIgnore_on_off:
                        if (((PowerManager) getSystemService(POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) {
                            Toast.makeText(context, "忽略电池优化权限已打开", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intentBatteryIgnore = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
                            startActivity(intentBatteryIgnore);
                        }
                        break;
                    case R.id.app_detail:
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        inCurPage = false;
                        break;
                }
            }
        };
        accessibilityOnOff.setOnClickListener(authorityOnOff);
        batteryIgnore.setOnClickListener(authorityOnOff);
        appDetail.setOnClickListener(authorityOnOff);

        model_1 = findViewById(R.id.model_1);
        model_2 = findViewById(R.id.model_2);
        model_1.setChecked(settingData.model == 1);
        model_2.setChecked(settingData.model == 2);
        View.OnClickListener modelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.model_1) {
                    settingData.setModel(1);
                    model_1.setChecked(true);
                    model_2.setChecked(false);
                }
                if (v.getId() == R.id.model_2) {
                    settingData.setModel(2);
                    model_2.setChecked(true);
                    model_1.setChecked(false);
                }
            }
        };
        model_1.setOnClickListener(modelClick);
        model_2.setOnClickListener(modelClick);

        allOnOff = findViewById(R.id.on_off_switch);
        onlyEffectInScreenOff = findViewById(R.id.onlyEffectInScreenOff);
        autoHideInTask = findViewById(R.id.autoHideInTask);
        allOnOff.setChecked(settingData.onOff);
        onlyEffectInScreenOff.setChecked(settingData.onlyEffectInScreenOff);
        autoHideInTask.setChecked(settingData.autoHideInTask);
        CompoundButton.OnCheckedChangeListener settingCheck = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.on_off_switch:
                        settingData.setOnOff(isChecked);
                        break;
                    case R.id.onlyEffectInScreenOff:
                        settingData.setOnlyEffectInScreenOff(isChecked);
                        break;
                    case R.id.autoHideInTask:
                        settingData.setAutoHideInTask(isChecked);
                        break;
                }
            }
        };
        allOnOff.setOnCheckedChangeListener(settingCheck);
        onlyEffectInScreenOff.setOnCheckedChangeListener(settingCheck);
        autoHideInTask.setOnCheckedChangeListener(settingCheck);

        vibrationStrength = findViewById(R.id.vibrationStrength);
        vibrationStrength.setProgress(settingData.vibrationStrength);
        vibrationStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                settingData.setVibrationStrength(seekBar.getProgress());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyAccessibilityService.mainFunction == null) {
            accessibilityOnOffImg.setImageResource(R.drawable.error);
        } else {
            accessibilityOnOffImg.setImageResource(R.drawable.ok);
        }
        if (((PowerManager) getSystemService(POWER_SERVICE)).isIgnoringBatteryOptimizations(getPackageName())) {
            batteryIgnoreOnOffImg.setImageResource(R.drawable.ok);
        } else {
            batteryIgnoreOnOffImg.setImageResource(R.drawable.error);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        inCurPage = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (settingData.autoHideInTask && inCurPage) {
            finishAndRemoveTask();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && settingData.autoHideInTask) {
            finishAndRemoveTask();
        }
        return super.onKeyUp(keyCode, event);
    }
}
