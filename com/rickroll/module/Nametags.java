// com/rickroll/module/Nametags.java
package com.rickroll.module;

import com.rickroll.setting.DoubleSetting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Nametags extends Module {
    private final DoubleSetting scale = new DoubleSetting("Scale", 1.0, 0.5, 3.0);

    public Nametags() {
        super("Nametags", "Custom nametags for players", Category.RENDER);
        addSetting(scale);
    }

    @Override
    public void onHudRender(DrawContext context) {
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) return;
    }

    @Override
    public void onWorldRender(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        if (mc.world == null || mc.player == null) return;
    }
}
