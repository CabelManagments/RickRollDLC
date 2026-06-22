// com/rickroll/module/TriggerBot.java
package com.rickroll.module;

import com.rickroll.setting.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Random;

public class TriggerBot extends Module {
    private final Random random = new Random();
    private long lastAttack = 0;
    private long nextDelay = 50;

    private final DoubleSetting delay = new DoubleSetting("Delay", 50.0, 10.0, 500.0);
    private final DoubleSetting jitter = new DoubleSetting("Jitter", 15.0, 0.0, 100.0);

    public TriggerBot() {
        super("TriggerBot", "Attacks entity you're looking at", Category.COMBAT);
        addSetting(delay);
        addSetting(jitter);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        long now = System.currentTimeMillis();
        if (now - lastAttack < nextDelay) return;

        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
        Entity entity = entityHit.getEntity();
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) entity;
        if (!target.isAlive()) return;
        if (target instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity) target;
            if (p.isCreative() || p.isSpectator()) return;
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;

        double baseDelay = delay.getValue();
        double jitterAmount = jitter.getValue();
        double gaussianOffset = random.nextGaussian() * jitterAmount;
        nextDelay = (long) Math.max(5, baseDelay + gaussianOffset);
    }
}
