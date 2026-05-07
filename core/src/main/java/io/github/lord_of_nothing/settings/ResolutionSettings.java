package io.github.lord_of_nothing.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ResolutionSettings {
    private static final int MIN_WIDTH = 1080;
    private static final int MIN_HEIGHT = 720;
    private static final ResolutionDto[] BASELINE_RESOLUTIONS = new ResolutionDto[] {
        new ResolutionDto(1920, 1080)
    };
    private static ResolutionDto defaultResolution = new ResolutionDto(MIN_WIDTH, MIN_HEIGHT);
    public static ResolutionDto[] resolutions = new ResolutionDto[] {};

    private ResolutionSettings() {}

    public static void initialize(Graphics.DisplayMode[] displayModes) {
        Graphics.DisplayMode maxWindowedMode = Gdx.graphics == null ? null : Gdx.graphics.getDisplayMode();
        defaultResolution = getSafeDefaultResolution(maxWindowedMode);

        if (displayModes == null || displayModes.length == 0) {
            resolutions = new ResolutionDto[] {defaultResolution};
            return;
        }

        LinkedHashMap<String, ResolutionDto> uniqueResolutions = new LinkedHashMap<>();
        uniqueResolutions.putIfAbsent(defaultResolution.toString(), defaultResolution);
        for (ResolutionDto baseline : BASELINE_RESOLUTIONS) {
            if (isWindowedResolutionTooSmallOrTooLarge(baseline.width, baseline.height, maxWindowedMode)) {
                continue;
            }
            uniqueResolutions.putIfAbsent(baseline.toString(), baseline);
        }

        for (Graphics.DisplayMode mode : displayModes) {
            if (mode == null) {
                continue;
            }
            if (isWindowedResolutionTooSmallOrTooLarge(mode.width, mode.height, maxWindowedMode)) {
                continue;
            }
            ResolutionDto dto = new ResolutionDto(mode.width, mode.height);
            uniqueResolutions.putIfAbsent(dto.toString(), dto);
        }

        if (uniqueResolutions.isEmpty()) {
            resolutions = new ResolutionDto[] {defaultResolution};
            return;
        }

        resolutions = uniqueResolutions.values().toArray(new ResolutionDto[0]);
        Arrays.sort(resolutions, Comparator
            .comparingInt((ResolutionDto r) -> r.width)
            .thenComparingInt(r -> r.height));
    }

    public static ResolutionDto getResolutionFromLabel(String label) {
        if (label == null) {
            return getDefaultResolution();
        }

        for (ResolutionDto resolution : resolutions) {
            if (label.equals(resolution.toString())) {
                return resolution;
            }
        }
        return getDefaultResolution();
    }

    public static ResolutionDto getDefaultResolution() {
        if (defaultResolution != null) {
            return defaultResolution;
        }

        Graphics.DisplayMode currentMode = Gdx.graphics == null ? null : Gdx.graphics.getDisplayMode();
        return getSafeDefaultResolution(currentMode);
    }

    public static HashMap<String, String> getResolutionsStrings() {
        HashMap<String, String> result = new LinkedHashMap<>();
        for (ResolutionDto resolution : resolutions) {
            result.put(resolution.toString(), String.valueOf(resolution.width));
        }

        return result;
    }

    public static ResolutionDto clampToWindowedBounds(int width, int height, Graphics.DisplayMode maxWindowedMode) {
        int clampedWidth = Math.max(MIN_WIDTH, width);
        int clampedHeight = Math.max(MIN_HEIGHT, height);

        if (maxWindowedMode != null) {
            clampedWidth = Math.min(clampedWidth, maxWindowedMode.width);
            clampedHeight = Math.min(clampedHeight, maxWindowedMode.height);
        }

        return new ResolutionDto(clampedWidth, clampedHeight);
    }

    private static boolean isWindowedResolutionTooSmallOrTooLarge(
        int width,
        int height,
        Graphics.DisplayMode maxWindowedMode
    ) {
        if (width < MIN_WIDTH || height < MIN_HEIGHT) {
            return true;
        }

        return maxWindowedMode != null && (width > maxWindowedMode.width || height > maxWindowedMode.height);
    }

    private static ResolutionDto getSafeDefaultResolution(Graphics.DisplayMode maxWindowedMode) {
        if (maxWindowedMode == null) {
            return new ResolutionDto(MIN_WIDTH, MIN_HEIGHT);
        }

        return clampToWindowedBounds(maxWindowedMode.width, maxWindowedMode.height, maxWindowedMode);
    }

}
