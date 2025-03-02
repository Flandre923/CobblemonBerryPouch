package com.github.flandre923.berrypouch.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fStack;


public class RenderHelper {
    public static void renderGuiItemAlpha(ItemStack stack, int x, int y, int alpha, ItemRenderer renderer) {
        renderGuiItemAlpha(stack, x, y, alpha, renderer.getModel(stack, null, null, 0), renderer);
    }


    /**
     * Like {@link ItemRenderer::renderGuiItem} but with alpha
     */
    // [VanillaCopy] with a small change
    public static void renderGuiItemAlpha(ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer) {
        Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.translate(x, y, 100.0F);
        modelViewStack.translate(8.0f, 8.0f, 0.0f);
        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();

        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        renderer.render(
                stack,
                ItemDisplayContext.GUI,
                false,
                new PoseStack(),
                // This part differs from vanilla. We wrap the buffer to allow drawing translucently
                wrapBuffer(buffer, alpha, alpha < 255),
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        buffer.endBatch();

        RenderSystem.enableDepthTest();

        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }


    private static MultiBufferSource wrapBuffer(MultiBufferSource buffer, int alpha, boolean forceTranslucent) {
        return renderType -> new GhostVertexConsumer(buffer.getBuffer(forceTranslucent ? RenderType.translucent() : renderType), alpha);
    }
    public record GhostVertexConsumer(VertexConsumer wrapped, int alpha) implements VertexConsumer {
        @Override
        public VertexConsumer addVertex(float f, float g, float h) {
            return wrapped.addVertex(f,g,h);
        }

        @Override
        public VertexConsumer setColor(int i, int j, int k, int alpha) {
            return wrapped.setColor(i,j,k,(alpha * this.alpha) / 0xFF);
        }

        @Override
        public VertexConsumer setUv(float f, float g) {
            return wrapped.setUv(f,g);
        }

        @Override
        public VertexConsumer setUv1(int i, int j) {
            return wrapped.setUv1(i,j);
        }

        @Override
        public VertexConsumer setUv2(int i, int j) {
            return wrapped.setUv2(i,j);
        }

        @Override
        public VertexConsumer setNormal(float f, float g, float h) {
            return wrapped.setNormal(f,g,h);
        }
    }
}
