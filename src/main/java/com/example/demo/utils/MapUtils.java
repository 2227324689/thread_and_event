package com.example.demo.utils;


import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class MapUtils {
    

    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }
    

    public static boolean isEmpty(Dictionary coll) {
        return (coll == null || coll.isEmpty());
    }

    public static Object computeIfAbsent(Map target, Object key, BiFunction mappingFunction, Object param1,
                                         Object param2) {

        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(key, "mappingFunction");
        Objects.requireNonNull(key, "param1");
        Objects.requireNonNull(key, "param2");

        Object val = target.get(key);
        if (val == null) {
            Object ret = mappingFunction.apply(param1, param2);
            target.put(key, ret);
            return ret;
        }
        return val;
    }
}
