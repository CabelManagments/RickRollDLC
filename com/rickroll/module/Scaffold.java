// com/rickroll/module/Scaffold.java
package com.rickroll.module;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Places blocks under your feet", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        int blockSlot = findBlockSlot();
        if (blockSlot == -1) return;

        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos belowPos = playerPos.down();

        if (!mc.world.getBlockState(belowPos).isAir()) return;

        int prevSlot = mc.player.getInventory().getSelectedSlot();
        switchToSlot(blockSlot);

        Vec3d hitVec = Vec3d.ofCenter(belowPos).add(0, -0.5, 0);

        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
            new BlockHitResult(hitVec, Direction.UP, belowPos, false));
        mc.player.swingHand(Hand.MAIN_HAND);

        switchToSlot(prevSlot);
    }

    private int findBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem) {
                BlockItem blockItem = (BlockItem) stack.getItem();
                if (blockItem.getBlock().getDefaultState().isSolidBlock(mc.world, BlockPos.ORIGIN)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void switchToSlot(int slot) {
        if (slot >= 0 && slot < 9) {
            mc.player.getInventory().setSelectedSlot(slot);
        }
    }
}
