// com/rickroll/module/AutoTotem.java
package com.rickroll.module;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", "Moves totems to offhand automatically", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.player.playerScreenHandler == null || mc.interactionManager == null) return;

        ItemStack offhand = mc.player.getOffHandStack();

        if (offhand.isOf(Items.TOTEM_OF_UNDYING)) return;

        int totemSlot = -1;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                totemSlot = i;
                break;
            }
        }

        if (totemSlot == -1) return;

        int invSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;

        if (!offhand.isEmpty()) {
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, invSlot, 0, SlotActionType.PICKUP, mc.player);
        }

        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, invSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);
    }
}
