package com.lgh.volume.control;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.appcompat.content.res.AppCompatResources;

public class MySeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    private final Drawable thumbLeft = AppCompatResources.getDrawable(getContext(), R.drawable.seekbar_thumb_left);
    private final Drawable thumbRight = AppCompatResources.getDrawable(getContext(), R.drawable.seekbar_thumb_right);
    private final Drawable thumbNormal = AppCompatResources.getDrawable(getContext(), R.drawable.seekbar_thumb_normal);

    public MySeekBar(Context context) {
        super(context);
        init();
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                l.onProgressChanged(seekBar, progress, fromUser);
                init();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                l.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                l.onStopTrackingTouch(seekBar);
            }
        };
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    private void init() {
        if (getProgress() == 0) {
            setThumb(thumbLeft);
        } else if (getProgress() == getMax()) {
            setThumb(thumbRight);
        } else if (getThumb() != thumbNormal) {
            setThumb(thumbNormal);
        }
    }
}
