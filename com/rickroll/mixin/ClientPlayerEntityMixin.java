// com/rickroll/mixin/ClientPlayerEntityMixin.java
package com.rickroll.mixin;

import com.rickroll.RickRollDLC;
import com.rickroll.module.Fly;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPackets(CallbackInfo ci) {
        if (RickRollDLC.fly != null && RickRollDLC.fly.isEnabled()) {
            return;
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickReturn(CallbackInfo ci) {
        if (RickRollDLC.mc.player == null) return;
    }
}
