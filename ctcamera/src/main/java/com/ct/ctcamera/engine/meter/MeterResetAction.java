package com.ct.ctcamera.engine.meter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.ct.ctcamera.engine.action.ActionWrapper;
import com.ct.ctcamera.engine.action.Actions;
import com.ct.ctcamera.engine.action.BaseAction;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class MeterResetAction extends ActionWrapper {

    private final BaseAction action;

    public MeterResetAction() {
        this.action = Actions.together(
                new ExposureReset(),
                new FocusReset(),
                new WhiteBalanceReset()
        );
    }

    @NonNull
    @Override
    public BaseAction getAction() {
        return action;
    }
}
