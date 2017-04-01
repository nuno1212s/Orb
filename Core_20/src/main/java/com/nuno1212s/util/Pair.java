package com.nuno1212s.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Pair of things
 */
public class Pair<E, V> {

    private E key;
    private V value;

    public Pair(E key, V value) {
        this.key = key;
        this.value = value;
    }

    public E getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public Pair<E, V> setKey(E key) {
        this.key = key;
        return this;
    }

    public Pair<E, V> setValue(V value) {
        this.value = value;
        return this;
    }

    public Map<E, V> toMap() {
        HashMap<E, V> map = new HashMap<>();
        map.put(this.key, this.value);
        return map;
    }

    public String toString() {
        return String.valueOf(this.getKey()) + "," + String.valueOf(this.getValue());
    }

}
