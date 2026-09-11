package com.github.flandre923.berrypouch.shared;

import com.github.flandre923.berrypouch.item.PokeBallGun;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ItemInHandRendererMixinShared {
    @FunctionalInterface
    public interface Renderer {
        void render(AbstractClientPlayer player, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHanded, PoseStack poseStack, MultiBufferSource multiBufferSource, int light);
    }

    private ItemInHandRendererMixinShared() {
    }

    public static boolean renderWithoutSwingIfNeeded(ItemStack mainHandItem, ItemStack offHandItem, AbstractClientPlayer player, InteractionHand interactionHand, ItemStack itemStack, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, Renderer renderer) {
        if ((mainHandItem.getItem() instanceof PokeBallGun || offHandItem.getItem() instanceof PokeBallGun) && !player.isShiftKeyDown()) {
            boolean isRightHand = interactionHand == InteractionHand.MAIN_HAND;
            boolean bl = isRightHand;
            poseStack.pushPose();
            int armDirection = isRightHand ? 1 : -1;
            float itemPosX = 0.56F;
            float itemPosY = -0.52F;
            float itemPosZ = -0.72F;
            poseStack.translate(armDirection * itemPosX, itemPosY, itemPosZ);
            ItemDisplayContext displayContext = isRightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            renderer.render(player, itemStack, displayContext, !bl, poseStack, multiBufferSource, combinedLight);
            poseStack.popPose();
            return true;
        }
        return false;
    }
}
