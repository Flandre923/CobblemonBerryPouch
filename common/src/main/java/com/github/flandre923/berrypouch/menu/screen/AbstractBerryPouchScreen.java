package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.helper.RenderHelper;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract  class AbstractBerryPouchScreen <T extends AbstractBerryPouchContainer>
        extends AbstractContainerScreen<T> {

    protected final BerryPouchType pouchType;
    protected final ResourceLocation texture;
    protected final Minecraft minecraft;

    public AbstractBerryPouchScreen(
            T menu, Inventory playerInv, Component title,
            BerryPouchType pouchType, ResourceLocation texture
    ) {
        super(menu, playerInv, title);
        this.pouchType = pouchType;
        this.texture = texture;
        this.minecraft = Minecraft.getInstance();
        initUiSettings();
    }


    protected void initUiSettings() {
        this.imageWidth = pouchType.getGuiWidth();
        this.imageHeight = pouchType.getGuiHeight();
        this.titleLabelX = pouchType.getTitleX();
        this.titleLabelY = pouchType.getTitleY();
        this.inventoryLabelX = pouchType.getInventoryX();
        this.inventoryLabelY = pouchType.getInventoryY();
    }
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui, mouseX, mouseY, partialTicks);
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        renderBackgroundTexture(guiGraphics);
        renderSlotPlaceholders(guiGraphics);
    }
    protected void renderBackgroundTexture(GuiGraphics guiGraphics) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
    }

    protected void renderSlotPlaceholders(GuiGraphics guiGraphics) {
        PoseStack poseStack = guiGraphics.pose();

        for (Slot slot : menu.slots) {
            if (slot.container == menu.getPouchInventory()) {
                int itemX = this.leftPos + slot.x;
                int itemY = this.topPos + slot.y;

                // 渲染空槽位占位符
                if (!slot.hasItem()) {
                    ItemStack placeholder = getPlaceholderForSlot(slot.getContainerSlot());
                    if (!placeholder.isEmpty()) {
                        RenderHelper.renderGuiItemAlpha(placeholder, itemX, itemY, 0x5F, minecraft.getItemRenderer());
                    }
                }
                // 特殊渲染数量1的物品
                else if (slot.getItem().getCount() == 1) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 300);
                    guiGraphics.drawString(minecraft.font, "1", itemX + 11, itemY + 9, 0xFFFFFF);
                    poseStack.popPose();
                }
            }
        }
    }

    protected abstract ItemStack getPlaceholderForSlot(int slotIndex);

    @Override
    public void onClose() {
        super.onClose();
        if (minecraft.player != null) {
            minecraft.player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS,
                    0.5F,
                    minecraft.player.level().random.nextFloat() * 0.1F + 0.9F);
        }
    }

}
