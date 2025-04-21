package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.MediumBerryPouchContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MediumBerryPouchScreen extends AbstractBerryPouchScreen<MediumBerryPouchContainer> {
    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/berry_pouch_30.png");

    public MediumBerryPouchScreen(MediumBerryPouchContainer menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, BerryPouchType.MEDIUM, TEXTURE);
    }

    @Override
    protected void renderBackgroundTexture(GuiGraphics guiGraphics) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        // 渲染上半部分
        guiGraphics.blit(TEXTURE, x, y, 1, 1, 208, 79, 256, 256);
        // 渲染下半部分
        guiGraphics.blit(TEXTURE, x + 17, y + 79, 17, 104, 175, 98, 256, 256);
    }

    @Override
    protected ItemStack getPlaceholderForSlot(int slotIndex) {
        return new ItemStack(BerryPouchType.MEDIUM.getStorageSlot().getSlotItem(slotIndex + 1));
    }
}