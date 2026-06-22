// com/rickroll/util/MathHelperExtension.java
package com.rickroll.util;

/**
 * Extension providing angle interpolation for rotations.
 */
public class MathHelperExtension {

    public static float interpolateAngleDegrees(float current, float target, float t) {
        float diff = target - current;
        while (diff < -180f) diff += 360f;
        while (diff > 180f) diff -= 360f;
        return current + diff * t;
    }

    public static float wrapAngleTo180(float angle) {
        angle = angle % 360f;
        if (angle >= 180f) angle -= 360f;
        if (angle < -180f) angle += 360f;
        return angle;
    }

    public static double wrapAngleTo180(double angle) {
        angle = angle % 360.0;
        if (angle >= 180.0) angle -= 360.0;
        if (angle < -180.0) angle += 360.0;
        return angle;
    }
}
