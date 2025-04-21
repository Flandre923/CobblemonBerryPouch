//package com.github.flandre923.berrypouch.menu.gui;
//
//import com.github.flandre923.berrypouch.ModCommon;
//import com.github.flandre923.berrypouch.helper.RenderHelper;
//import com.github.flandre923.berrypouch.item.BerryPouch;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//
//public class BerryPouchGui69 extends AbstractContainerScreen<BerryPouchContainer69> {
//    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"textures/gui/berry_pouch_69.png");
//
//    public BerryPouchGui69(BerryPouchContainer69 abstractContainerMenu, Inventory inventory, Component component) {
//        super(abstractContainerMenu, inventory, component);
////        imageHeight += 36;
////        recompute, same as super
////        inventoryLabelY = imageHeight - 94;
//        this.imageWidth = 256;
//        this.imageHeight = 142 + (255-157);
//        this.titleLabelY = 2;
//        this.titleLabelX = 25;
//        inventoryLabelX = 44;
//        inventoryLabelY = 147;
//    }
//
//    @Override
//    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground(gui,mouseX,mouseY,partialTicks);
//        super.render(gui, mouseX, mouseY, partialTicks);
//        this.renderTooltip(gui, mouseX, mouseY);
//    }
//
//    @Override
//    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
//        PoseStack ms = guiGraphics.pose();
//        Minecraft mc = Minecraft.getInstance();
//        int x = (width - imageWidth) / 2;
//        int y = (height - imageHeight) / 2;
//
//        // 渲染第一部分 (上半部分)
//        guiGraphics.blit(TEXTURE, x, y, 0, 0, 255, 142, 256, 256);
//        // 渲染第二部分 (下半部分)
//        guiGraphics.blit(TEXTURE, x+40, y + 143, 40, 157, 175, 255-157, 256, 256);
//        for (Slot slot : menu.slots) {
//            if (slot.container == menu.flowerBagInv) {  // 只处理花袋内的槽位
//                int itemX = this.leftPos + slot.x;
//                int itemY = this.topPos + slot.y;
//                if (!slot.hasItem()) {  // 如果槽位为空
//                    // 获取该槽位对应的花的ItemStack
//                    ItemStack missBerry = new ItemStack(BerryPouch.POUCH_GUI_69_STORAGE_SLOT.getSlotItem(slot.index+1));
//                    // 使用半透明效果(alpha=0x5F)渲染物品虚影
//                    RenderHelper.renderGuiItemAlpha(missBerry, itemX, itemY, 0x5F, mc.getItemRenderer());
//                }
//                // 特殊处理数量为1的情况
//                else if (slot.getItem().getCount() == 1) {
//                    // 始终显示数字"1"
//                    ms.pushPose();
//                    ms.translate(0, 0, 300);  // 确保数字显示在最上层
//                    guiGraphics.drawString(mc.font, "1", itemX + 11, itemY + 9, 0xFFFFFF);
//                    ms.popPose();
//                }
//            }
//        }
//    }
//    @Override
//    public void onClose() {
//        super.onClose();
//        // Play close sound on client side when GUI is closed
//        if (Minecraft.getInstance().player != null) {
//            Minecraft.getInstance().player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS,  0.5F, Minecraft.getInstance().player.level().random.nextFloat() * 0.1F + 0.9F);
//        }
//    }
//}
