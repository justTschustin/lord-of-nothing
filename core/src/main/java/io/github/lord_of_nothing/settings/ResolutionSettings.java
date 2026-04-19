package io.github.lord_of_nothing.settings;

import com.badlogic.gdx.Graphics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ResolutionSettings {
    public static final ResolutionDto DEFAULT_RESOLUTION = new ResolutionDto(1920, 1080);
    private static final int MIN_WIDTH = 1920;
    private static final int MIN_HEIGHT = 1080;
    private static final ResolutionDto[] BASELINE_RESOLUTIONS = new ResolutionDto[] {
        new ResolutionDto(1920, 1080)
    };
    public static ResolutionDto[] resolutions = new ResolutionDto[] {DEFAULT_RESOLUTION};

    private ResolutionSettings() {}

    public static void initialize(Graphics.DisplayMode[] displayModes) {
        if (displayModes == null || displayModes.length == 0) {
            resolutions = new ResolutionDto[] {DEFAULT_RESOLUTION};
            return;
        }

        LinkedHashMap<String, ResolutionDto> uniqueResolutions = new LinkedHashMap<>();
        for (ResolutionDto baseline : BASELINE_RESOLUTIONS) {
            if (isBelowMinimumResolution(baseline.width, baseline.height)) {
                continue;
            }
            uniqueResolutions.putIfAbsent(baseline.toString(), baseline);
        }

        for (Graphics.DisplayMode mode : displayModes) {
            if (mode == null) {
                continue;
            }
            if (isBelowMinimumResolution(mode.width, mode.height)) {
                continue;
            }
            ResolutionDto dto = new ResolutionDto(mode.width, mode.height);
            uniqueResolutions.putIfAbsent(dto.toString(), dto);
        }

        if (uniqueResolutions.isEmpty()) {
            resolutions = new ResolutionDto[] {DEFAULT_RESOLUTION};
            return;
        }

        resolutions = uniqueResolutions.values().toArray(new ResolutionDto[0]);
        Arrays.sort(resolutions, Comparator
            .comparingInt((ResolutionDto r) -> r.width)
            .thenComparingInt(r -> r.height));
    }

    public static ResolutionDto getResolutionFromLabel(String label) {
        if (label == null) {
            return DEFAULT_RESOLUTION;
        }

        for (ResolutionDto resolution : resolutions) {
            if (label.equals(resolution.toString())) {
                return resolution;
            }
        }
        return DEFAULT_RESOLUTION;
    }

    public static HashMap<String, String> getResolutionsStrings() {
        HashMap<String, String> result = new LinkedHashMap<>();
        for (ResolutionDto resolution : resolutions) {
            result.put(resolution.toString(), String.valueOf(resolution.width));
        }

        return result;
    }

    private static boolean isBelowMinimumResolution(int width, int height) {
        return width < MIN_WIDTH || height < MIN_HEIGHT;
    }

}
