package ru.nntu.vst.dips.symbolicdiversity.utils;

import java.util.HashMap;

public class CumulativeMap extends HashMap<String, Double> {
    public Double put(String key) {
        return this.containsKey(key) ? super.put(key, super.remove(key) + 1.0) : super.put(key, 1.0);
    }
}
