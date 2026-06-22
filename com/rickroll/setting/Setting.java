// com/rickroll/setting/Setting.java
package com.rickroll.setting;

public abstract class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T value) { this.value = value; }
    public T getDefaultValue() { return defaultValue; }
    public abstract String getType();
}
