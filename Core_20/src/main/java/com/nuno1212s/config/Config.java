package com.nuno1212s.config;

/**
 * Config class
 */
public abstract class Config {

    public abstract String getString(String key);

    public abstract int getInt(String key);

    public abstract Object get(String key);

    public abstract double getDouble(String key);

    public abstract void set(String key, Object value);

}
