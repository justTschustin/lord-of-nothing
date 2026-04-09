package io.github.lord_of_nothing.settings;

import java.util.HashMap;
import java.util.Map;

public class ResolutionSettings {
    public static final Map.Entry<Integer, Integer> defaultResolution =
        new java.util.AbstractMap.SimpleImmutableEntry<>(1080, 720);

    public static HashMap<Integer, Integer> resolutions = new HashMap<Integer, Integer>() {{
        put(1080, 720);
        put(1920, 1080);
    }};

    public static Map.Entry<Integer, Integer> getResolutionFromWidth(int width) {
        for (Map.Entry<Integer, Integer> entry : resolutions.entrySet()) {
            Integer key = entry.getKey();

            if (key.equals(width)) {
                return entry;
            }
        }
        return defaultResolution;
    }

    public static HashMap<String, String> getResolutionsStrings() {
        HashMap<String, String> result = new java.util.LinkedHashMap<>();

        resolutions.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry ->
                result.put(
                    entry.getKey() + "x" + entry.getValue(),
                    String.valueOf(entry.getKey())
                )
            );

        return result;
    }

}
