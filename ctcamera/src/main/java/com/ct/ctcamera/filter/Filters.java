package com.ct.ctcamera.filter;

import androidx.annotation.NonNull;

import com.ct.ctcamera.filters.AutoFixFilter;
import com.ct.ctcamera.filters.BlackAndWhiteFilter;
import com.ct.ctcamera.filters.BrightnessFilter;
import com.ct.ctcamera.filters.ContrastFilter;
import com.ct.ctcamera.filters.CrossProcessFilter;
import com.ct.ctcamera.filters.DocumentaryFilter;
import com.ct.ctcamera.filters.DuotoneFilter;
import com.ct.ctcamera.filters.FillLightFilter;
import com.ct.ctcamera.filters.GammaFilter;
import com.ct.ctcamera.filters.GrainFilter;
import com.ct.ctcamera.filters.GrayscaleFilter;
import com.ct.ctcamera.filters.HueFilter;
import com.ct.ctcamera.filters.InvertColorsFilter;
import com.ct.ctcamera.filters.LomoishFilter;
import com.ct.ctcamera.filters.PosterizeFilter;
import com.ct.ctcamera.filters.SaturationFilter;
import com.ct.ctcamera.filters.SepiaFilter;
import com.ct.ctcamera.filters.SharpnessFilter;
import com.ct.ctcamera.filters.TemperatureFilter;
import com.ct.ctcamera.filters.TintFilter;
import com.ct.ctcamera.filters.VignetteFilter;
import com.ct.ctcamera.CameraView;

/**
 * Contains commonly used {@link Filter}s.
 *
 * You can use {@link #newInstance()} to create a new instance and
 * pass it to {@link CameraView#setFilter(Filter)}.
 */
public enum Filters {

    /** @see NoFilter */
    NONE(NoFilter.class),

    /** @see AutoFixFilter */
    AUTO_FIX(AutoFixFilter.class),

    /** @see BlackAndWhiteFilter */
    BLACK_AND_WHITE(BlackAndWhiteFilter.class),

    /** @see BrightnessFilter */
    BRIGHTNESS(BrightnessFilter.class),

    /** @see ContrastFilter */
    CONTRAST(ContrastFilter.class),

    /** @see CrossProcessFilter */
    CROSS_PROCESS(CrossProcessFilter.class),

    /** @see DocumentaryFilter */
    DOCUMENTARY(DocumentaryFilter.class),

    /** @see DuotoneFilter */
    DUOTONE(DuotoneFilter.class),

    /** @see FillLightFilter */
    FILL_LIGHT(FillLightFilter.class),

    /** @see GammaFilter */
    GAMMA(GammaFilter.class),

    /** @see GrainFilter */
    GRAIN(GrainFilter.class),

    /** @see GrayscaleFilter */
    GRAYSCALE(GrayscaleFilter.class),

    /** @see HueFilter */
    HUE(HueFilter.class),

    /** @see InvertColorsFilter */
    INVERT_COLORS(InvertColorsFilter.class),

    /** @see LomoishFilter */
    LOMOISH(LomoishFilter.class),

    /** @see PosterizeFilter */
    POSTERIZE(PosterizeFilter.class),

    /** @see SaturationFilter */
    SATURATION(SaturationFilter.class),

    /** @see SepiaFilter */
    SEPIA(SepiaFilter.class),

    /** @see SharpnessFilter */
    SHARPNESS(SharpnessFilter.class),

    /** @see TemperatureFilter */
    TEMPERATURE(TemperatureFilter.class),

    /** @see TintFilter */
    TINT(TintFilter.class),

    /** @see VignetteFilter */
    VIGNETTE(VignetteFilter.class);

    private Class<? extends Filter> filterClass;

    Filters(@NonNull Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    /**
     * Returns a new instance of the given filter.
     * @return a new instance
     */
    @NonNull
    public Filter newInstance() {
        try {
            return filterClass.newInstance();
        } catch (IllegalAccessException e) {
            return new NoFilter();
        } catch (InstantiationException e) {
            return new NoFilter();
        }
    }
}
