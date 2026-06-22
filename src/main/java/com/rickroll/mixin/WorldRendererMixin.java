// com/rickroll/mixin/WorldRendererMixin.java
package com.rickroll.mixin;

import com.rickroll.RickRollDLC;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "render", at = @At("RETURN"))
    private void onRenderReturn(CallbackInfo ci) {
    }
}
