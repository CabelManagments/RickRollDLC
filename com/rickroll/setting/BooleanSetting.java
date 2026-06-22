// com/rickroll/setting/BooleanSetting.java
package com.rickroll.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public String getType() { return "boolean"; }
}
