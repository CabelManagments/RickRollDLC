// com/rickroll/module/Tracers.java
package com.rickroll.module;

import com.rickroll.setting.BooleanSetting;
import com.rickroll.setting.DoubleSetting;
import com.rickroll.util.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class Tracers extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth", 1.5, 0.5, 5.0);

    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.RENDER);
        addSetting(players);
        addSetting(lineWidth);
    }

    @Override
    public void onWorldRender(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        if (mc.world == null || mc.player == null) return;

        float lw = lineWidth.getValue().floatValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (players.getValue() && !(entity instanceof PlayerEntity)) continue;
            if (entity instanceof PlayerEntity) {
                PlayerEntity p = (PlayerEntity) entity;
                if (p.isCreative() || p.isSpectator()) continue;
            }

            float[] color = getEntityColor(entity);
            RenderUtils.drawTracerLine(context, entity, color[0], color[1], color[2], color[3], lw);
        }
    }

    private float[] getEntityColor(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return new float[]{1.0f, 0.2f, 0.6f, 1.0f};
        }
        return new float[]{0.5f, 1.0f, 0.2f, 0.8f};
    }
}
