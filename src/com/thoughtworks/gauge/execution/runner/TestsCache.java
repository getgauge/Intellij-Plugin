package com.thoughtworks.gauge.execution.runner;

import java.util.HashMap;
import java.util.Map;

public class TestsCache {
    private Map<String, Integer> idCache = new HashMap<>();
    private Integer id = 0;

    public Integer getId(String key) {
        return idCache.get(key);
    }

    public Integer getCurrentId() {
        return id;
    }

    public void setId(String key, Integer id) {
        idCache.put(key, id);
    }

    public void setId(String key) {
        idCache.put(key, ++this.id);
    }
}
