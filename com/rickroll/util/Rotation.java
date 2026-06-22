// com/rickroll/util/RotationUtils.java
package com.rickroll.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static float[] getRotations(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffY = to.y - from.y;
        double diffZ = to.z - from.z;

        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

        return new float[]{yaw, pitch};
    }

    public static void spoofRotation(float yaw, float pitch) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
            yaw, pitch, mc.player.isOnGround()
        ));
    }

    public static float normalizeAngle(float angle) {
        angle = angle % 360;
        if (angle >= 180) angle -= 360;
        if (angle < -180) angle += 360;
        return angle;
    }

    public static float getAngleDifference(float a, float b) {
        float diff = a - b;
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        return diff;
    }

    public static double getDistance(Vec3d a, Vec3d b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
