package com.pckg.self_service.backend.utils;

import java.util.Map;
import java.util.TreeMap;

public class PackageSelfServiceBackendUtils {

    private static final Map<Double, String> SIZE_MAP = new TreeMap<>();

    static {
        SIZE_MAP.put(0d, "S");
        SIZE_MAP.put(200d, "M");
        SIZE_MAP.put(1000d, "L");
        SIZE_MAP.put(10000d, "XL");
    }

    /**
     * Converts the weight of the package from grams to t-shirt sizes: 'S','M','L','XL'.
     * @param wtInGms weights of the package in grams.
     * @return returns the t-shirt size equivalent of grams.
     */
    public static String determinePackageSize(Double wtInGms){
        if (wtInGms < 0) {
            return "Invalid weight: cannot be negative";
        }
        return SIZE_MAP.entrySet().stream()
                .filter(entry -> wtInGms < entry.getKey())
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse("XL"); // Default case for >= 10kg
    }
}
