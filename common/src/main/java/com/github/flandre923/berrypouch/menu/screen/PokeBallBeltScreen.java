package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltHelper;
import com.github.flandre923.berrypouch.menu.container.PokeBallBeltContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class PokeBallBeltScreen extends AbstractContainerScreen<PokeBallBeltContainer> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/pokeball_belt.png");
    // 纹理实际尺寸
    private static final int TEXTURE_WIDTH = 223;
    private static final int TEXTURE_HEIGHT = 129;
    // 选中框在纹理中的位置
    private static final int SELECTION_U = 191;
    private static final int SELECTION_V = 15;
    private static final int SELECTION_SIZE = 18;


    public PokeBallBeltScreen(PokeBallBeltContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 175;
        this.imageHeight = 128;
        this.inventoryLabelY = this.imageHeight - 94; // 调整标签位置
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui, mouseX, mouseY, partialTicks);
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
    }


    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        gui.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        renderSelectionHighlight(gui);
    }


    private void renderSelectionHighlight(GuiGraphics gui) {
        int selectedIndex = menu.getSelectedIndex();
        PoseStack poseStack = gui.pose();

        for (Slot slot : menu.slots) {
            // 只处理腰带的槽位
            if (slot.container == menu.getBeltInventory()) {
                int slotIndex = slot.getContainerSlot();

                if (slotIndex == selectedIndex) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 200); // 确保渲染在物品上层

                    int x = this.leftPos + slot.x-1;
                    int y = this.topPos + slot.y-1;

                    gui.blit(TEXTURE, x, y,
                            SELECTION_U, SELECTION_V,
                            SELECTION_SIZE, SELECTION_SIZE,
                            TEXTURE_WIDTH, TEXTURE_HEIGHT);

                    poseStack.popPose();
                    break; // 找到了就退出
                }
            }
        }
    }


}
