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
        ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/berry_pouch_69.png");

    public LargeBerryPouchScreen(LargeBerryPouchContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, BerryPouchType.LARGE, TEXTURE);
    }

    @Override
    protected void renderBackgroundTexture(GuiGraphics guiGraphics) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // 渲染上半部分
        guiGraphics.blit(TEXTURE, x, y, 0, 0, 255, 142, 256, 256);
        // 渲染下半部分
        guiGraphics.blit(TEXTURE, x + 40, y + 143, 40, 157, 175, 98, 256, 256);
    }

    @Override
    protected ItemStack getPlaceholderForSlot(int slotIndex) {
        return new ItemStack(BerryPouchType.LARGE.getStorageSlot().getSlotItem(slotIndex + 1));
    }
}