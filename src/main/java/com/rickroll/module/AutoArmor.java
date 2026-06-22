// com/rickroll/module/AutoArmor.java
package com.rickroll.module;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", "Equips best armor automatically", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.player.playerScreenHandler == null || mc.interactionManager == null) return;

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack currentlyEquipped = mc.player.getEquippedStack(slot);

            int bestSlot = -1;
            int bestArmor = getArmorValue(currentlyEquipped, slot);

            for (int i = 0; i < mc.player.getInventory().size(); i++) {
                if (i >= 36 && i <= 39) continue;
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.isEmpty()) continue;

                if (!isArmorForSlot(stack, slot)) continue;
                int armorVal = getArmorValue(stack, slot);
                if (armorVal > bestArmor) {
                    bestArmor = armorVal;
                    bestSlot = i;
                }
            }

            if (bestSlot != -1) {
                int inventorySlot = bestSlot < 9 ? bestSlot + 36 : bestSlot;
                int equipmentSlotIndex = getEquipmentSlotIndex(slot);

                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, inventorySlot, 0, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, equipmentSlotIndex, 0, SlotActionType.PICKUP, mc.player);

                if (!currentlyEquipped.isEmpty()) {
                    mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, inventorySlot, 0, SlotActionType.PICKUP, mc.player);
                }
                return;
            }
        }
    }

    private boolean isArmorForSlot(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return false;
        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) return false;
        return equippable.slot() == slot;
    }

    private int getArmorValue(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) return 0;
        if (!(stack.getItem() instanceof ArmorItem)) return 0;
        ArmorItem armor = (ArmorItem) stack.getItem();
        return armor.getMaterial().getDefenseForType(armor.getType()) * 100 + armor.getMaterial().getDurability(armor.getType());
    }

    private int getEquipmentSlotIndex(EquipmentSlot slot) {
        switch (slot) {
            case HEAD: return 5;
            case CHEST: return 6;
            case LEGS: return 7;
            case FEET: return 8;
            default: return -1;
        }
    }
}
