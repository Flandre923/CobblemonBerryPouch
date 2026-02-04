package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.LargeBerryPouchContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class LargeBerryPouchScreen extends AbstractBerryPouchScreen<LargeBerryPouchContainer> {
    private static final ResourceLocation TEXTURE =
<<<<<<< HEAD
        ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/berry_bag.png");

    public LargeBerryPouchScreen(LargeBerryPouchContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, BerryPouchType.LARGE, TEXTURE);
    }

    @Override
    protected void renderBackgroundTexture(GuiGraphics guiGraphics) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // 渲染整个背景纹理 (新图片大小: 256 * 300, 有效区域: 0,0 到 255,257)
        guiGraphics.blit(TEXTURE, x, y, 0, 0, 255, 257, 256, 300);
    }

    @Override
    protected ItemStack getPlaceholderForSlot(int slotIndex) {
        if (slotIndex < 70) {
            // 树果槽位 (0-69)
            return new ItemStack(BerryPouchType.LARGE.getStorageSlot().getSlotItem(slotIndex + 1));
        } else {
            // Other baits槽位 (70-85) - 不显示占位符，因为这些槽位通过标签检查
            return ItemStack.EMPTY;
        }
    }
}