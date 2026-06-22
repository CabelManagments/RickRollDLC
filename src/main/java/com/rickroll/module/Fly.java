// com/rickroll/module/Fly.java
package com.rickroll.module;

import com.rickroll.setting.DoubleSetting;
import com.rickroll.setting.EnumSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module {
    private final DoubleSetting speed = new DoubleSetting("Speed", 2.0, 0.5, 10.0);
    private final EnumSetting mode = new EnumSetting("Mode", "Glide", "Glide", "Vanilla", "Packet");
    private int packetCounter = 0;

    public Fly() {
        super("Fly", "Allows flying in survival (Glide mode)", Category.MOVEMENT);
        addSetting(speed);
        addSetting(mode);
    }

    @Override
    public void onEnable() {
        packetCounter = 0;
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().allowFlying = false;
            mc.player.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        String currentMode = mode.getValue();

        switch (currentMode) {
            case "Glide":
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.sendAbilitiesUpdate();
                mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
                if (mc.player.input.jumping) {
                    mc.player.setVelocity(mc.player.getVelocity().x, speed.getValue() * 0.5, mc.player.getVelocity().z);
                } else if (mc.player.input.sneaking) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -speed.getValue() * 0.5, mc.player.getVelocity().z);
                }
                break;

            case "Vanilla":
                mc.player.getAbilities().allowFlying = true;
                mc.player.getAbilities().flying = true;
                mc.player.sendAbilitiesUpdate();
                break;

            case "Packet":
                mc.player.setVelocity(0, 0, 0);
                double spd = speed.getValue();
                Vec3d motion = Vec3d.ZERO;

                double forward = mc.player.input.movementForward;
                double strafe = mc.player.input.movementSideways;
                double yaw = Math.toRadians(mc.player.getYaw());

                if (forward != 0 || strafe != 0) {
                    motion = new Vec3d(
                        -Math.sin(yaw) * forward + Math.cos(yaw) * strafe,
                        0,
                        Math.cos(yaw) * forward + Math.sin(yaw) * strafe
                    ).normalize().multiply(spd);
                }

                if (mc.player.input.jumping) motion = motion.add(0, spd, 0);
                if (mc.player.input.sneaking) motion = motion.add(0, -spd, 0);

                mc.player.setVelocity(motion);

                packetCounter++;
                if (packetCounter % 20 == 0) {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() - 0.04, mc.player.getZ(), false
                    ));
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 0.04, mc.player.getZ(), false
                    ));
                }
                break;
        }
    }
}
