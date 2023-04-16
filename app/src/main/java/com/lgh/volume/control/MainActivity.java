package com.lgh.volume.control;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lgh.volume.control.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SettingData settingData;
    private boolean inCurPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding.rlAccessibilityOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccessibility = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                if (intentAccessibility.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentAccessibility);
                    inCurPage = false;
                } else {
                    Toast.makeText(getApplicationContext(), "授权窗口打开失败，请手动打开", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.rlDeviceAdminOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevicePolicyManager devicePolicyManager = getSystemService(DevicePolicyManager.class);
                ComponentName compMyDeviceAdmin = new ComponentName(getApplicationContext(), MyDeviceAdminReceiver.class);
                if (devicePolicyManager.isAdminActive(compMyDeviceAdmin)) {
                    Toast.makeText(getApplicationContext(), "设备管理器权限已开启", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intentDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compMyDeviceAdmin);
                    if (intentDeviceAdmin.resolveActivity(getPackageManager()) != null) {
                        startActivity(intentDeviceAdmin);
                        inCurPage = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "授权窗口打开失败，请手动打开", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.rlBatteryIgnoreOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(getPackageName())) {
                    Toast.makeText(getApplicationContext(), "忽略电池优化权限已开启", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentBatteryIgnore = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getPackageName()));
                    if (intentBatteryIgnore.resolveActivity(getPackageManager()) != null) {
                        startActivity(intentBatteryIgnore);
                        inCurPage = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "授权窗口打开失败，请手动打开", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        binding.rlAppDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                inCurPage = false;
            }
        });

        binding.scOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingData.setOnOff(isChecked);
            }
        });

        binding.scOnlyEffectInScreenOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingData.setOnlyEffectInScreenOff(isChecked);
            }
        });

        binding.scAutoHideInTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingData.setAutoHideInTask(isChecked);
            }
        });

        binding.sbVibrationStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        binding.sbLongPressTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                settingData.setLongPressTime(seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settingData = MyAccessibilityService.myMainFunction != null ? MyAccessibilityService.myMainFunction.getSettingData() : new SettingData(getApplicationContext());
        binding.scOnOff.setChecked(settingData.onOff);
        binding.scOnlyEffectInScreenOff.setChecked(settingData.onlyEffectInScreenOff);
        binding.scAutoHideInTask.setChecked(settingData.autoHideInTask);
        binding.sbVibrationStrength.setProgress(settingData.vibrationStrength);
        binding.sbLongPressTime.setProgress(settingData.longPressTime);

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (MyAccessibilityService.myMainFunction == null) {
                    binding.ivAccessibilityOnOffImg.setImageResource(R.drawable.error);
                } else {
                    binding.ivAccessibilityOnOffImg.setImageResource(R.drawable.ok);
                }
                if (getSystemService(DevicePolicyManager.class).isAdminActive(new ComponentName(getApplicationContext(), MyDeviceAdminReceiver.class))) {
                    binding.ivDeviceAdminOnOffImg.setImageResource(R.drawable.ok);
                } else {
                    binding.ivDeviceAdminOnOffImg.setImageResource(R.drawable.error);
                }
                if (getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(getPackageName())) {
                    binding.ivBatteryIgnoreOnOffImg.setImageResource(R.drawable.ok);
                } else {
                    binding.ivBatteryIgnoreOnOffImg.setImageResource(R.drawable.error);
                }
                handler.postDelayed(this, 500);
            }
        });

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
