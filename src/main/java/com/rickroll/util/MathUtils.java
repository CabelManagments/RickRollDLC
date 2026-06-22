// com/rickroll/util/MathUtils.java
package com.rickroll.util;

public class MathUtils {
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public static double interpolate(double a, double b, double t) {
        return a + (b - a) * t;
    }

    public static float interpolateAngle(float a, float b, float t) {
        float diff = b - a;
        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;
        return a + diff * t;
    }

    public static double gaussian(Random rng, double mean, double stddev) {
        return mean + rng.nextGaussian() * stddev;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class Random {
        private java.util.Random rng = new java.util.Random();
        public double nextGaussian() { return rng.nextGaussian(); }
    }
}
