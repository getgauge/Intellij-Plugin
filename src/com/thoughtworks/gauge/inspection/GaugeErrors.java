package com.thoughtworks.gauge.inspection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GaugeErrors {
    private static Map<String, List<GaugeError>> e = new HashMap<>();

    static void add(String key, List<GaugeError> errors) {
        e.put(key, errors);
    }

    static void init() {
        e = new HashMap<>();
    }

    static List<GaugeError> get(String key) {
        return e.get(key) == null ? new ArrayList<>() : e.get(key);
    }
}
