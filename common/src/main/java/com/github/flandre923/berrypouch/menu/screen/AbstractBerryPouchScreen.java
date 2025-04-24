package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.helper.MarkedSlotsHelper;
import com.github.flandre923.berrypouch.helper.RenderHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
import com.github.flandre923.berrypouch.network.ModNetworking;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public abstract  class AbstractBerryPouchScreen <T extends AbstractBerryPouchContainer>
        extends AbstractContainerScreen<T> {

    protected final BerryPouchType pouchType;
    protected final ResourceLocation texture;
    protected final Minecraft minecraft;

    private static final ResourceLocation STAR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/star.png");
    private static final int STAR_SIZE = 32;


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
        renderMarkedSlotIndicators(guiGraphics); // <-- 新增调用: 渲染标记指示器
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


    protected void renderMarkedSlotIndicators(GuiGraphics guiGraphics) {
        ItemStack currentPouchStack = findCurrentPouchStack();
        if (currentPouchStack.isEmpty()) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        for (Slot slot : menu.slots) {
            if (slot.container == menu.getPouchInventory()) {
                int pouchSlotIndex = slot.getContainerSlot();

                if (MarkedSlotsHelper.isSlotMarked(currentPouchStack, pouchSlotIndex)) {
                    poseStack.pushPose();
                    poseStack.translate(0, 0, 100);
//                    poseStack.scale(0.3f,0.3f,1f);
                    // Adjust star positioning relative to slot (top-right corner)
                    int starX = this.leftPos + slot.x ; // Slot width is 16
                    int starY = this.topPos + slot.y;

                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    // Ensure last two args match the actual texture size (e.g., 8, 8 or 32, 32)
//                    guiGraphics.blit(STAR_TEXTURE, starX, starY, 0, 0, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE);
                    guiGraphics.blit(STAR_TEXTURE, starX, starY, 16,16,0,0, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE);
                    RenderSystem.disableBlend();
                    poseStack.popPose();
                }
            }
        }
    }

    // --- Add Helper Method to find the current pouch stack ---
    private ItemStack findCurrentPouchStack() {
        Player player = this.minecraft.player;
        if (player == null) {
            return ItemStack.EMPTY;
        }

        // Get the Item type we expect based on the menu's initial stack
        Item targetItem = this.menu.getPouchStack().getItem();
        if (!(targetItem instanceof BerryPouch)) {
            // Should not happen if the menu opened correctly
            return ItemStack.EMPTY;
        }


        // 1. Check Main Hand
        ItemStack mainHandStack = player.getMainHandItem();
        if (mainHandStack.is(targetItem)) {
            return mainHandStack;
        }

        // 2. Check Off Hand
        ItemStack offHandStack = player.getOffhandItem();
        if (offHandStack.is(targetItem)) {
            return offHandStack;
        }


        // 3. Check Accessory Slots
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            // Use the correct method name: getFirstEquipped
            // It returns SlotEntryReference or null directly
            SlotEntryReference equippedRef = capability.getFirstEquipped(targetItem);
            if (equippedRef != null) {
                return equippedRef.stack(); // Return the stack from the accessory slot
            }
        }

        // 4. Not found in expected locations
        // As a last resort, return the potentially stale stack from the menu?
        // This might cause the old behavior. Returning EMPTY is safer for ensuring correctness.
        // ModCommon.LOG.warn("Could not find current pouch stack for rendering marks. Item: {}", targetItem); // Optional Warning
        return ItemStack.EMPTY;
        // return menu.getPouchStack(); // Returning this might show stale stars if the item was removed
    }


    // --- mouseClicked remains the same (logic is correct) ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && Screen.hasShiftDown()) {
            Slot clickedSlot = this.hoveredSlot;
            if (clickedSlot != null && clickedSlot.container == menu.getPouchInventory()) {
                int pouchSlotIndex = clickedSlot.getContainerSlot();
                ModNetworking.sendToggleMarkSlotPacketToServer(pouchSlotIndex); // Send the network packet
                // Optional: Client-side sound feedback
                // this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true; // Indicate event was handled
            }
        }
        return super.mouseClicked(mouseX, mouseY, button); // Default handling
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
