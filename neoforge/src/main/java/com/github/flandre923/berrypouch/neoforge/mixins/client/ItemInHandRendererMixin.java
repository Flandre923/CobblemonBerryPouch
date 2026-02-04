package com.github.flandre923.berrypouch.neoforge.mixins.client;

import com.github.flandre923.berrypouch.item.PokeBallGun;
import com.mojang.blaze3d.vertex.PoseStack;
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
    ) {}
    /**
     * 阻止 PokeBallGun 的挥手动画
     * 当玩家持有 PokeBallGun 且没有按 Shift 时，直接返回不执行挥手动画
     */

    @Inject(
            at = @At("HEAD"),
            method = "renderArmWithItem",
            cancellable = true
    )
    private void onRenderArmWithItem(AbstractClientPlayer abstractClientPlayer, float partialTicks, float pitch, InteractionHand interactionHand, float swingProgress, ItemStack itemStack, float equippedProgress, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLight, CallbackInfo ci) {
        // 如果物品是 PokeBallGun 且没有按 Shift，返回空物品来阻止动画
        if ((this.mainHandItem.getItem() instanceof PokeBallGun  || this.offHandItem.getItem() instanceof PokeBallGun) && !abstractClientPlayer.isShiftKeyDown()) {

            // 手动渲染物品，但不使用挥动动画
            // 1. 获取手臂方向
            boolean isRightHand = interactionHand == InteractionHand.MAIN_HAND;
            boolean bl = isRightHand;

            // 2. 应用基础物品位置变换（不使用挥动动画）
            poseStack.pushPose();

            // 3. 计算手臂位置（基于 Minecraft 的标准位置）
            int armDirection = isRightHand ? 1 : -1;

            // 4. 应用标准物品变换（参考 renderArmWithItem 中的逻辑）
            // 物品基础位置
            float itemPosX = 0.56F;
            float itemPosY = -0.52F;
            float itemPosZ = -0.72F;

            poseStack.translate(armDirection * itemPosX, itemPosY, itemPosZ);

            // 5. 应用物品旋转（保持静止位置，不添加挥动旋转）
            // 不应用 swingProgress 相关的旋转，保持物品静止

            // 6. 渲染物品
            ItemDisplayContext displayContext = isRightHand
                    ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                    : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

            renderItem(
                    abstractClientPlayer,
                    itemStack,
                    displayContext,
                    !bl,
                    poseStack,
                    multiBufferSource,
                    combinedLight
            );

            poseStack.popPose();
            ci.cancel();


        }
    }
}
