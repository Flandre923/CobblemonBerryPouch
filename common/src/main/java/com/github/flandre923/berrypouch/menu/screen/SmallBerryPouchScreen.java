//package com.github.flandre923.berrypouch.menu.screen;
//
//import com.github.flandre923.berrypouch.ModCommon;
//import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
//import com.github.flandre923.berrypouch.menu.container.SmallBerryPouchContainer;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.item.ItemStack;
//
//public class SmallBerryPouchScreen extends AbstractBerryPouchScreen<SmallBerryPouchContainer>{
//    private static final ResourceLocation TEXTURE =
//            ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/berry_pouch_24.png");
//
//    public SmallBerryPouchScreen(SmallBerryPouchContainer menu, Inventory playerInv, Component title) {
//        super(menu, playerInv, title, BerryPouchType.SMALL, TEXTURE);
//    }
//
//    @Override
//    protected void renderBackgroundTexture(GuiGraphics guiGraphics) {
//        int x = (width - imageWidth) / 2;
//        int y = (height - imageHeight) / 2;
//
//        // 渲染上半部分
//        guiGraphics.blit(TEXTURE, x, y, 0, 0, 175, 71, 256, 256);
//        // 渲染下半部分
//        guiGraphics.blit(TEXTURE, x, y + 71, 0, 85, 175, 90, 256, 256);
//    }
//
//    @Override
//    protected ItemStack getPlaceholderForSlot(int slotIndex) {
//        return new ItemStack(BerryPouchType.SMALL.getStorageSlot().getSlotItem(slotIndex + 1));
//    }
//}
