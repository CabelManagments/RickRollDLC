// com/rickroll/setting/EnumSetting.java
package com.rickroll.setting;

import java.util.Arrays;
import java.util.List;

public class EnumSetting extends Setting<String> {
    private final List<String> options;

    public EnumSetting(String name, String defaultValue, String... options) {
        super(name, defaultValue);
        this.options = Arrays.asList(options);
    }

    public List<String> getOptions() { return options; }

    @Override
    public String getType() { return "enum"; }
}
