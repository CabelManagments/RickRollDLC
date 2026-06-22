// com/rickroll/module/Sprint.java
package com.rickroll.module;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Automatically sprints", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (mc.player.input.movementForward > 0 && !mc.player.isSneaking() && !mc.player.isUsingItem()) {
            if (!mc.player.horizontalCollision && !mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
        }
    }
}
