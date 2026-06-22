// com/rickroll/module/Step.java
package com.rickroll.module;

import com.rickroll.setting.DoubleSetting;

public class Step extends Module {
    private final DoubleSetting height = new DoubleSetting("Height", 1.5, 0.5, 2.5);

    public Step() {
        super("Step", "Step up blocks without jumping", Category.MOVEMENT);
        addSetting(height);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.stepHeight = height.getValue().floatValue();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.stepHeight = 0.6f;
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        mc.player.stepHeight = height.getValue().floatValue();
    }
}
