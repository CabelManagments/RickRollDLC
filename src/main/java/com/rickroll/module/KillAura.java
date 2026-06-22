// com/rickroll/module/KillAura.java
package com.rickroll.module;

import com.rickroll.setting.BooleanSetting;
import com.rickroll.setting.DoubleSetting;
import com.rickroll.setting.EnumSetting;
import com.rickroll.util.MathUtils;
import com.rickroll.util.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class KillAura extends Module {
    private final Random random = new Random();
    private long lastAttackTime = 0;
    private long nextAttackDelay = 100;
    private float[] currentRotations = new float[]{0f, 0f};
    private float[] targetRotations = new float[]{0f, 0f};
    private boolean isRotating = false;
    private int rotationStepsRemaining = 0;
    private float yawStep = 0f;
    private float pitchStep = 0f;

    private final DoubleSetting range = new DoubleSetting("Range", 4.5, 3.0, 6.0);
    private final DoubleSetting aps = new DoubleSetting("APS", 10.0, 1.0, 20.0);
    private final DoubleSetting smoothness = new DoubleSetting("Smoothness", 50.0, 0.0, 100.0);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final BooleanSetting spoofRotations = new BooleanSetting("SpoofRotations", true);
    private final EnumSetting targetMode = new EnumSetting("TargetMode", "Closest", "Closest", "Health", "Angle");
    private final BooleanSetting playersOnly = new BooleanSetting("PlayersOnly", true);
    private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", false);

    public KillAura() {
        super("KillAura", "Attacks entities in range with neuro-mode Gaussian timing", Category.COMBAT);
        addSetting(range);
        addSetting(aps);
        addSetting(smoothness);
        addSetting(rotate);
        addSetting(spoofRotations);
        addSetting(targetMode);
        addSetting(playersOnly);
        addSetting(throughWalls);
    }

    @Override
    public void onEnable() {
        lastAttackTime = 0;
        isRotating = false;
        currentRotations = new float[]{mc.player.getYaw(), mc.player.getPitch()};
    }

    @Override
    public void onDisable() {
        isRotating = false;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        LivingEntity target = findTarget();
        if (target == null) {
            isRotating = false;
            return;
        }

        double distSq = mc.player.squaredDistanceTo(target);
        double r = range.getValue();
        if (distSq > r * r) return;

        if (rotate.getValue()) {
            Vec3d targetPos = target.getEyePos();
            float[] angles = RotationUtils.getRotations(mc.player.getEyePos(), targetPos);

            double smooth = smoothness.getValue();
            int steps = (int) Math.max(1, MathUtils.map(smooth, 0, 100, 1, 20));

            currentRotations[0] = MathHelper.interpolateAngleDegrees(currentRotations[0], angles[0], 1.0 / steps);
            currentRotations[1] = MathHelper.interpolateAngleDegrees(currentRotations[1], angles[1], 1.0 / steps);

            if (spoofRotations.getValue()) {
                RotationUtils.spoofRotation(currentRotations[0], currentRotations[1]);
            }

            mc.player.setYaw(currentRotations[0]);
            mc.player.setPitch(currentRotations[1]);
        }

        boolean hasLineOfSight = throughWalls.getValue() || mc.world.raycast(
            new net.minecraft.world.RaycastContext(
                mc.player.getEyePos(),
                target.getEyePos(),
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                mc.player
            )
        ).getType() == net.minecraft.util.hit.HitResult.Type.MISS;

        if (!hasLineOfSight) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime >= nextAttackDelay) {
            attack(target);
            lastAttackTime = currentTime;

            double apsValue = aps.getValue();
            double baseDelay = 1000.0 / apsValue;
            double gaussianOffset = random.nextGaussian() * (baseDelay * 0.15);
            nextAttackDelay = (long) Math.max(30, baseDelay + gaussianOffset);
        }
    }

    private LivingEntity findTarget() {
        List<LivingEntity> candidates = new ArrayList<>();
        double r = range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (playersOnly.getValue() && !(entity instanceof PlayerEntity)) continue;
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entity;
                if (player.isCreative() || player.isSpectator()) continue;
            }
            LivingEntity living = (LivingEntity) entity;
            if (!living.isAlive()) continue;
            if (mc.player.squaredDistanceTo(living) > r * r) continue;

            candidates.add(living);
        }

        if (candidates.isEmpty()) return null;

        String mode = targetMode.getValue();
        switch (mode) {
            case "Health":
                candidates.sort(Comparator.comparingDouble(e -> e.getHealth()));
                return candidates.get(0);
            case "Angle":
                candidates.sort(Comparator.comparingDouble(this::getAngleDifference));
                return candidates.get(0);
            default:
                candidates.sort(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)));
                return candidates.get(0);
        }
    }

    private double getAngleDifference(LivingEntity entity) {
        float[] angles = RotationUtils.getRotations(mc.player.getEyePos(), entity.getEyePos());
        double yawDiff = MathHelper.wrapDegrees(angles[0] - mc.player.getYaw());
        double pitchDiff = MathHelper.wrapDegrees(angles[1] - mc.player.getPitch());
        return Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }

    private void attack(LivingEntity target) {
        if (mc.player.isUsingItem()) {
            mc.player.stopUsingItem();
        }

        boolean wasSprinting = mc.player.isSprinting();
        if (wasSprinting) {
            mc.player.setSprinting(false);
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (wasSprinting) {
            mc.player.setSprinting(true);
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }
}
