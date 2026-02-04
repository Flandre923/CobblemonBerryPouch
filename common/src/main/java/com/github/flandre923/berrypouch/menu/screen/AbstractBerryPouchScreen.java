package com.github.flandre923.berrypouch.menu.screen;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.helper.MarkedSlotsHelper;
import com.github.flandre923.berrypouch.helper.RenderHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
import com.github.flandre923.berrypouch.network.ModNetworking;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private static final int STAR_SIZE = 20;


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
//                // 特殊渲染数量1的物品
//                else if (slot.getItem().getCount() == 1) {
//                    poseStack.pushPose();
//                    poseStack.translate(0, 0, 300);
//                    guiGraphics.drawString(minecraft.font, "1", itemX + 11, itemY + 9, 0xFFFFFF);
//                    poseStack.popPose();
//                }
            }
        }
    }
    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        // 只对袋子槽位特殊处理
        if (slot.container == menu.getPouchInventory() && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            int x = slot.x;
            int y = slot.y;

            // 手动渲染物品（不带装饰）
            guiGraphics.renderItem(stack, x, y);

            // 渲染耐久条等（但不渲染数量）
            guiGraphics.renderItemDecorations(minecraft.font, stack, x, y, ""); // 空字符串隐藏数量

            // 渲染自定义数量
            int count = stack.getCount();
            if (count > 0) {
                renderCustomItemCount(guiGraphics, x, y, count);
            }
        } else {
            // 其他槽位使用默认渲染
            super.renderSlot(guiGraphics, slot);
        }
    }

    /**
     * 渲染自定义物品数量（带缩写和动态缩放）
     */
    protected void renderCustomItemCount(GuiGraphics guiGraphics, int slotX, int slotY, int count) {
        String countStr = formatCount(count);
        float scale = getScaleForCount(countStr);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 350);

        int textWidth = minecraft.font.width(countStr);

        // 右下角对齐，基于槽位内坐标（槽位大小16x16）
        float textX = slotX + 18 - textWidth * scale - 1;
        float textY = slotY + 18 - 8 * scale - 1;

        poseStack.translate(textX, textY, 0);
        poseStack.scale(scale, scale, 1.0f);

        // 绘制带阴影的文字
        guiGraphics.drawString(minecraft.font, countStr, 0, 0, 0xFFFFFF, true);

        poseStack.popPose();
    }

    /**
     * 格式化数量显示
     * 64 -> "64"
     * 1500 -> "1.5K"
     * 2300000 -> "2.3M"
     */
    protected String formatCount(int count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 1000000) {
            double k = count / 1000.0;
            if (k >= 100) {
                return String.format("%.0fK", k);
            } else if (k >= 10) {
                return String.format("%.1fK", k).replace(".0K", "K");
            } else {
                return String.format("%.1fK", k).replace(".0K", "K");
            }
        } else {
            double m = count / 1000000.0;
            if (m >= 100) {
                return String.format("%.0fM", m);
            } else if (m >= 10) {
                return String.format("%.1fM", m).replace(".0M", "M");
            } else {
                return String.format("%.1fM", m).replace(".0M", "M");
            }
        }
    }

    /**
     * 根据文字长度动态缩放
     */
    protected float getScaleForCount(String countStr) {
        int length = countStr.length();
        if (length <= 2) {
            return 1.0f;
        } else if (length == 3) {
            return 0.85f;
        } else if (length == 4) {
            return 0.7f;
        } else {
            return 0.6f;
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
                    poseStack.translate(0, 0, 299);
                    int starX = this.leftPos + slot.x ; // Slot width is 16
                    int starY = this.topPos + slot.y;
                    guiGraphics.blit(STAR_TEXTURE, starX, starY, 16,16,0,0, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE);
                    poseStack.popPose();
                }
            }
        }
    }
    private ItemStack findCurrentPouchStack() {
        Player player = this.minecraft.player;
        if (player == null) {
            return ItemStack.EMPTY;
        }

        // Get the Item type we expect based on the menu's initial stack
        Item targetItem = this.menu.getPouchStack().getItem();
        if (!(targetItem instanceof BerryPouch)) {
            return ItemStack.EMPTY;
        }

        // 【关键修复】根据打开方式智能查找当前实际持有的栈
        int openFlag = this.menu.getOpenFlag();

        switch (openFlag) {
            case 0: // 主手打开 - 优先检查主手
                ItemStack mainHandStack = player.getMainHandItem();
                if (mainHandStack.is(targetItem) && !mainHandStack.isEmpty()) {
                    return mainHandStack;
                }
                // 主手没有，再检查装备栏
                AccessoriesCapability capability = AccessoriesCapability.get(player);
                if (capability != null) {
                    SlotEntryReference equippedRef = capability.getFirstEquipped(targetItem);
                    if (equippedRef != null && !equippedRef.stack().isEmpty()) {
                        return equippedRef.stack();
                    }
                }
                // 最后检查副手
                ItemStack offHandStack = player.getOffhandItem();
                if (offHandStack.is(targetItem) && !offHandStack.isEmpty()) {
                    return offHandStack;
                }
                break;

            case 1: // 副手打开 - 优先检查副手
                 offHandStack = player.getOffhandItem();
                if (offHandStack.is(targetItem) && !offHandStack.isEmpty()) {
                    return offHandStack;
                }
                // 副手没有，再检查主手
                 mainHandStack = player.getMainHandItem();
                if (mainHandStack.is(targetItem) && !mainHandStack.isEmpty()) {
                    return mainHandStack;
                }
                // 最后检查装备栏
                 capability = AccessoriesCapability.get(player);
                if (capability != null) {
                    SlotEntryReference equippedRef = capability.getFirstEquipped(targetItem);
                    if (equippedRef != null && !equippedRef.stack().isEmpty()) {
                        return equippedRef.stack();
                    }
                }
                break;

            case 2: // 快捷键打开（装备栏）- 优先检查装备栏
                 capability = AccessoriesCapability.get(player);
                if (capability != null) {
                    SlotEntryReference equippedRef = capability.getFirstEquipped(targetItem);
                    if (equippedRef != null && !equippedRef.stack().isEmpty()) {
                        return equippedRef.stack();
                    }
                }
                // 装备栏没有，再检查主手
                 mainHandStack = player.getMainHandItem();
                if (mainHandStack.is(targetItem) && !mainHandStack.isEmpty()) {
                    return mainHandStack;
                }
                // 最后检查副手
                 offHandStack = player.getOffhandItem();
                if (offHandStack.is(targetItem) && !offHandStack.isEmpty()) {
                    return offHandStack;
                }
                break;

            default:
                // 未知情况，使用通用逻辑
                break;
        }

        // 如果都找不到，返回空栈
        return ItemStack.EMPTY;
    }


    // --- mouseClicked remains the same (logic is correct) ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_S)) {
            Slot clickedSlot = this.hoveredSlot;
            if (clickedSlot != null && clickedSlot.container == menu.getPouchInventory()) {
                int pouchSlotIndex = clickedSlot.getContainerSlot();
                ModNetworking.sendToggleMarkSlotPacketToServer(pouchSlotIndex);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button); // Default handling
    }

    protected abstract ItemStack getPlaceholderForSlot(int slotIndex);

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 修复BUG：当GUI打开时，阻止数字键1-9的快捷键行为
        // 防止玩家在其他槽位按数字键时尝试移动树果袋导致崩溃
        if (keyCode >= GLFW.GLFW_KEY_1 && keyCode <= GLFW.GLFW_KEY_9) {
            return true; // 阻止数字键事件，不让它触发挥击栏切换
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

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
