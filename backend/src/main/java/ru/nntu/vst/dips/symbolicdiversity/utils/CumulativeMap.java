package ru.nntu.vst.dips.symbolicdiversity.utils;


import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CumulativeMap extends HashMap<String, Double> {
    public Double put(String key) {
        return this.containsKey(key) ? super.put(key, super.remove(key) + 1.0) : super.put(key, 1.0);
    }
}
