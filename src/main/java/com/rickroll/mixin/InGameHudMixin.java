// com/rickroll/mixin/InGameHudMixin.java
package com.rickroll.mixin;

import com.rickroll.RickRollDLC;
import com.rickroll.module.Module;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter, CallbackInfo ci) {
        if (RickRollDLC.mc.player == null || RickRollDLC.mc.options.hudHidden) return;

        int y = 2;
        int x = 2;

        for (Module module : RickRollDLC.modules) {
            if (!module.isEnabled()) continue;

            String name = module.getName();
            int textWidth = RickRollDLC.mc.textRenderer.getWidth(name);

            int bgColor = 0x80000000;
            int textColor = module.getCategory().getColor();

            context.fill(x, y, x + textWidth + 8, y + 12, bgColor);
            context.drawTextWithShadow(RickRollDLC.mc.textRenderer, name, x + 4, y + 2, textColor);

            y += 13;
        }
    }
}
