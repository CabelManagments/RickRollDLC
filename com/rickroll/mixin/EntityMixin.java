// com/rickroll/mixin/EntityMixin.java
package com.rickroll.mixin;

import com.rickroll.RickRollDLC;
import com.rickroll.module.Step;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public float stepHeight;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if ((Object) this != RickRollDLC.mc.player) return;
        if (RickRollDLC.mc.player == null) return;

        Step stepModule = RickRollDLC.step;
        if (stepModule != null && stepModule.isEnabled()) {
            RickRollDLC.mc.player.stepHeight = stepModule.getSettingValue("Height");
        }
    }
}
