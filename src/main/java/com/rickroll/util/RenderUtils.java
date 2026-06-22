// com/rickroll/util/RenderUtils.java
package com.rickroll.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class RenderUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawBox(WorldRenderContext context, Entity entity, float r, float g, float b, float a, float lineWidth) {
        Vec3d camPos = context.camera().getPos();
        Box box = entity.getBoundingBox();

        double minX = box.minX - camPos.x;
        double minY = box.minY - camPos.y;
        double minZ = box.minZ - camPos.z;
        double maxX = box.maxX - camPos.x;
        double maxY = box.maxY - camPos.y;
        double maxZ = box.maxZ - camPos.z;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float cr = Math.max(0, Math.min(1, r));
        float cg = Math.max(0, Math.min(1, g));
        float cb = Math.max(0, Math.min(1, b));
        float ca = Math.max(0, Math.min(1, a));

        addLineVertices(buffer, minX, minY, minZ, maxX, minY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, minZ, maxX, minY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, maxZ, minX, minY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, minY, maxZ, minX, minY, minZ, cr, cg, cb, ca);

        addLineVertices(buffer, minX, maxY, minZ, maxX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, maxY, minZ, maxX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, maxY, maxZ, minX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, maxY, maxZ, minX, maxY, minZ, cr, cg, cb, ca);

        addLineVertices(buffer, minX, minY, minZ, minX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, minZ, maxX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, maxZ, maxX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, minY, maxZ, minX, maxY, maxZ, cr, cg, cb, ca);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawGlowOutline(WorldRenderContext context, Entity entity, float r, float g, float b, float a, float lineWidth) {
        Vec3d camPos = context.camera().getPos();
        Box box = entity.getBoundingBox().expand(0.05);

        double minX = box.minX - camPos.x;
        double minY = box.minY - camPos.y;
        double minZ = box.minZ - camPos.z;
        double maxX = box.maxX - camPos.x;
        double maxY = box.maxY - camPos.y;
        double maxZ = box.maxZ - camPos.z;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth * 2.5f);
        RenderSystem.disableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float cr = Math.max(0, Math.min(1, r));
        float cg = Math.max(0, Math.min(1, g));
        float cb = Math.max(0, Math.min(1, b));
        float ca = Math.max(0, Math.min(1, a * 0.6f));

        addLineVertices(buffer, minX, minY, minZ, maxX, minY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, minZ, maxX, minY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, maxZ, minX, minY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, minY, maxZ, minX, minY, minZ, cr, cg, cb, ca);

        addLineVertices(buffer, minX, maxY, minZ, maxX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, maxY, minZ, maxX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, maxY, maxZ, minX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, maxY, maxZ, minX, maxY, minZ, cr, cg, cb, ca);

        addLineVertices(buffer, minX, minY, minZ, minX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, minZ, maxX, maxY, minZ, cr, cg, cb, ca);
        addLineVertices(buffer, maxX, minY, maxZ, maxX, maxY, maxZ, cr, cg, cb, ca);
        addLineVertices(buffer, minX, minY, maxZ, minX, maxY, maxZ, cr, cg, cb, ca);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawTracerLine(WorldRenderContext context, Entity entity, float r, float g, float b, float a, float lineWidth) {
        Vec3d camPos = context.camera().getPos();
        Vec3d entityPos = entity.getEyePos().subtract(camPos);
        Vec3d playerPos = mc.player.getEyePos().subtract(camPos);

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(lineWidth);
        RenderSystem.disableDepthTest();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float cr = Math.max(0, Math.min(1, r));
        float cg = Math.max(0, Math.min(1, g));
        float cb = Math.max(0, Math.min(1, b));
        float ca = Math.max(0, Math.min(1, a));

        buffer.vertex((float) playerPos.x, (float) playerPos.y, (float) playerPos.z).color(cr, cg, cb, ca);
        buffer.vertex((float) entityPos.x, (float) entityPos.y, (float) entityPos.z).color(cr, cg, cb, ca);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void addLineVertices(BufferBuilder buffer, double x1, double y1, double z1,
                                         double x2, double y2, double z2,
                                         float r, float g, float b, float a) {
        buffer.vertex((float) x1, (float) y1, (float) z1).color(r, g, b, a);
        buffer.vertex((float) x2, (float) y2, (float) z2).color(r, g, b, a);
    }
}
