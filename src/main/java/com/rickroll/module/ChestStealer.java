// com/rickroll/module/ChestStealer.java
package com.rickroll.module;

import com.rickroll.setting.DoubleSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Random;

public class ChestStealer extends Module {
    private final DoubleSetting delay = new DoubleSetting("Delay", 50.0, 0.0, 500.0);
    private final DoubleSetting jitter = new DoubleSetting("Jitter", 20.0, 0.0, 100.0);
    private long lastSteal = 0;
    private long nextDelay = 50;
    private final Random random = new Random();

    public ChestStealer() {
        super("ChestStealer", "Steals items from chests automatically", Category.WORLD);
        addSetting(delay);
        addSetting(jitter);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.player.playerScreenHandler == null || mc.interactionManager == null) return;

        if (!(mc.player.playerScreenHandler instanceof GenericContainerScreenHandler)) return;

        GenericContainerScreenHandler handler = (GenericContainerScreenHandler) mc.player.playerScreenHandler;

        long now = System.currentTimeMillis();
        if (now - lastSteal < nextDelay) return;

        for (int i = 0; i < handler.getInventory().size(); i++) {
            if (i >= handler.getInventory().size() - 36) break;

            ItemStack stack = handler.getSlot(i).getStack();
            if (stack.isEmpty()) continue;

            mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
            lastSteal = now;

            double baseDelay = delay.getValue();
            double jitterAmount = jitter.getValue();
            double gaussianOffset = random.nextGaussian() * jitterAmount;
            nextDelay = (long) Math.max(0, baseDelay + gaussianOffset);
            return;
        }
    }
}
