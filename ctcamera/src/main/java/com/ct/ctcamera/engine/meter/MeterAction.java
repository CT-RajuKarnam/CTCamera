package com.ct.ctcamera.engine.meter;

import android.hardware.camera2.params.MeteringRectangle;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ct.ctcamera.CameraLogger;
import com.ct.ctcamera.engine.CameraEngine;
import com.ct.ctcamera.engine.action.ActionHolder;
import com.ct.ctcamera.engine.action.ActionWrapper;
import com.ct.ctcamera.engine.action.Actions;
import com.ct.ctcamera.engine.action.BaseAction;
import com.ct.ctcamera.engine.metering.Camera2MeteringTransform;
import com.ct.ctcamera.engine.offset.Reference;
import com.ct.ctcamera.metering.MeteringRegions;
import com.ct.ctcamera.metering.MeteringTransform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
public class MeterAction extends ActionWrapper {

    private final static String TAG = MeterAction.class.getSimpleName();
    private final static CameraLogger LOG = CameraLogger.create(TAG);

    private List<BaseMeter> meters;
    private BaseAction action;
    private final MeteringRegions regions;
    private final CameraEngine engine;
    private final boolean skipIfPossible;

    public MeterAction(@NonNull CameraEngine engine,
                       @Nullable MeteringRegions regions,
                       boolean skipIfPossible) {
        this.regions = regions;
        this.engine = engine;
        this.skipIfPossible = skipIfPossible;
    }

    @NonNull
    @Override
    public BaseAction getAction() {
        return action;
    }

    public boolean isSuccessful() {
        for (BaseMeter meter : meters) {
            if (!meter.isSuccessful()) {
                LOG.i("isSuccessful:", "returning false.");
                return false;
            }
        }
        LOG.i("isSuccessful:", "returning true.");
        return true;
    }

    @Override
    protected void onStart(@NonNull ActionHolder holder) {
        LOG.w("onStart:", "initializing.");
        initialize(holder);
        LOG.w("onStart:", "initialized.");
        super.onStart(holder);
    }

    private void initialize(@NonNull ActionHolder holder) {
        List<MeteringRectangle> areas = new ArrayList<>();
        if (regions != null) {
            MeteringTransform<MeteringRectangle> transform = new Camera2MeteringTransform(
                    engine.getAngles(),
                    engine.getPreview().getSurfaceSize(),
                    engine.getPreviewStreamSize(Reference.VIEW),
                    engine.getPreview().isCropping(),
                    holder.getCharacteristics(this),
                    holder.getBuilder(this)
            );
            MeteringRegions transformed = regions.transform(transform);
            areas = transformed.get(Integer.MAX_VALUE, transform);
        }

        BaseMeter ae = new ExposureMeter(areas, skipIfPossible);
        BaseMeter af = new FocusMeter(areas, skipIfPossible);
        BaseMeter awb = new WhiteBalanceMeter(areas, skipIfPossible);
        meters = Arrays.asList(ae, af, awb);
        action = Actions.together(ae, af, awb);
    }
}
