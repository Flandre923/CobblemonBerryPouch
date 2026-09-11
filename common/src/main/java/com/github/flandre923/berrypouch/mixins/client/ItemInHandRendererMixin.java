package com.github.flandre923.berrypouch.mixins.client;

import com.github.flandre923.berrypouch.shared.ItemInHandRendererMixinShared;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemInHandRendererMixin {
    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private ItemStack offHandItem;

    @Shadow
    public void renderItem(
            net.minecraft.world.entity.LivingEntity livingEntity,
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean leftHanded,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light
    ) {
    }

    @Inject(at = @At("HEAD"), method = "renderArmWithItem", cancellable = true)
    private void onRenderArmWithItem(AbstractClientPlayer abstractClientPlayer, float partialTicks, float pitch, InteractionHand interactionHand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, CallbackInfo ci) {
        if (ItemInHandRendererMixinShared.renderWithoutSwingIfNeeded(mainHandItem, offHandItem, abstractClientPlayer, interactionHand, itemStack, poseStack, multiBufferSource, combinedLight, this::renderItem)) {
            ci.cancel();
        }
    }
}
