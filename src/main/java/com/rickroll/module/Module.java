// com/rickroll/module/Module.java
package com.rickroll.module;

import com.rickroll.gui.ClickGuiScreen;
import com.rickroll.setting.Setting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled = false;
    private int keyCode = -1;

    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public void onWorldRender(WorldRenderContext context) {}
    public void onHudRender(DrawContext context) {}

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void setEnabled(boolean state) {
        if (enabled != state) {
            enabled = state;
            if (enabled) onEnable();
            else onDisable();
        }
    }

    public boolean isEnabled() { return enabled; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public int getKeyCode() { return keyCode; }
    public void setKeyCode(int keyCode) { this.keyCode = keyCode; }
    public List<Setting<?>> getSettings() { return settings; }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getSettingValue(String name) {
        for (Setting<?> setting : settings) {
            if (setting.getName().equals(name)) {
                return (T) setting.getValue();
            }
        }
        return null;
    }

    public enum Category {
        COMBAT("Combat", 0xFFff69b4),
        MOVEMENT("Movement", 0xFF7cfc00),
        RENDER("Render", 0xFF00ffff),
        PLAYER("Player", 0xffffaa00),
        WORLD("World", 0xFFaa00ff);

        private final String displayName;
        private final int color;

        Category(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public int getColor() { return color; }
    }
}
