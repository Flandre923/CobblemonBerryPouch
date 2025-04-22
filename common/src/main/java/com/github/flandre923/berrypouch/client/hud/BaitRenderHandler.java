package com.github.flandre923.berrypouch.client.hud;

import com.cobblemon.mod.common.item.interactive.PokerodItem; // 引入 PokerodItem
import com.github.flandre923.berrypouch.ModCommon;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientGuiEvent;
import io.wispforest.owo.ui.hud.Hud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


@Environment(EnvType.CLIENT)
public class BaitRenderHandler  implements ClientGuiEvent.RenderHud {
    private static final ResourceLocation GUI_ICONS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "textures/gui/gui_icon.png");
    // 图标在纹理文件中的尺寸
    private static final int ICON_TEXTURE_SIZE = 32;
    // 纹理文件的完整尺寸
    private static final int TEXTURE_SHEET_WIDTH = 64;
    private static final int TEXTURE_SHEET_HEIGHT = 64;

    // 在屏幕上渲染的图标尺寸（可以与纹理尺寸不同，但这里我们保持一致）
    private static final int ICON_RENDER_SIZE = 24; // 稍微缩小一点，避免太大
    // 图标之间的间距
    private static final int ICON_SPACING = 2;
    // 整个UI距离屏幕边缘的内边距
    private static final int PADDING_X = 10;
    private static final int PADDING_Y = 10;

    // 计算总宽度和高度
    private static final int TOTAL_WIDTH = ICON_RENDER_SIZE * 3 + ICON_SPACING * 2;
    private static final int TOTAL_HEIGHT = ICON_RENDER_SIZE;

    // 各元素在纹理文件中的 UV 坐标 (左上角)
    private static final int LEFT_ARROW_U = 0;
    private static final int LEFT_ARROW_V = 0;
    private static final int RIGHT_ARROW_U = ICON_TEXTURE_SIZE; // 32
    private static final int RIGHT_ARROW_V = 0;
    private static final int FRAME_U = 0;
    private static final int FRAME_V = ICON_TEXTURE_SIZE; // 32

    @Override
    public void renderHud(GuiGraphics guiGraphics, DeltaTracker deltaTracker) { // Renamed parameters for clarity and changed float to DeltaTracker
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        // 确保玩家存在
        if (player == null) {
            return;
        }
        // 获取玩家主手物品
        ItemStack heldItem = player.getMainHandItem(); // 或者检查副手: player.getOffhandItem()
        if (!(heldItem.getItem() instanceof PokerodItem pokerodItem)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof PokerodItem)) {
                return;
            }
        }
        ItemStack baitStack = PokerodItem.Companion.getBaitStackOnRod(heldItem);
        if (baitStack.isEmpty()) {
            return;
        }
        // --- 开始渲染 ---
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // 计算渲染的基准坐标 (右下角)
        int baseX = screenWidth - PADDING_X - TOTAL_WIDTH;
        int baseY = screenHeight - PADDING_Y - TOTAL_HEIGHT;

        // 计算各元素的精确渲染位置
        int leftArrowX = baseX;
        int frameX = leftArrowX + ICON_RENDER_SIZE + ICON_SPACING;
        int rightArrowX = frameX + ICON_RENDER_SIZE + ICON_SPACING;

        // --- 绘制背景图标 (箭头和框) ---
        RenderSystem.setShader(GameRenderer::getPositionTexShader); // 设置着色器
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // 设置颜色为白色不透明
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);     // 绑定我们的纹理
        RenderSystem.enableBlend();                               // 启用混合以支持纹理透明度
        RenderSystem.defaultBlendFunc();                          // 设置默认混合模式
        // 绘制左箭头
        guiGraphics.blit(GUI_ICONS_TEXTURE, leftArrowX, baseY, // 屏幕坐标
                LEFT_ARROW_U, LEFT_ARROW_V,           // UV 坐标
                ICON_RENDER_SIZE, ICON_RENDER_SIZE,   // 渲染尺寸
                ICON_TEXTURE_SIZE,ICON_TEXTURE_SIZE,
                TEXTURE_SHEET_WIDTH, TEXTURE_SHEET_HEIGHT); // 纹理图纸尺寸
        // 绘制框
        guiGraphics.blit(GUI_ICONS_TEXTURE, frameX, baseY,
                FRAME_U, FRAME_V,
                ICON_RENDER_SIZE, ICON_RENDER_SIZE,
                TEXTURE_SHEET_WIDTH, TEXTURE_SHEET_HEIGHT);

        // 绘制右箭头
        guiGraphics.blit(GUI_ICONS_TEXTURE, rightArrowX, baseY,
                RIGHT_ARROW_U, RIGHT_ARROW_V,
                ICON_RENDER_SIZE, ICON_RENDER_SIZE,
                TEXTURE_SHEET_WIDTH, TEXTURE_SHEET_HEIGHT);
        RenderSystem.disableBlend(); // 关闭混合


        // --- 绘制诱饵物品图标 ---
        // 物品图标通常是 16x16，我们需要将其绘制在框的中心
        int itemRenderX = frameX + (ICON_RENDER_SIZE - 16) / 2; // (24 - 16) / 2 = 4px 偏移
        int itemRenderY = baseY + (ICON_RENDER_SIZE - 16) / 2;
        // 使用 GuiGraphics 渲染物品（它会自动处理 ItemRenderer 的设置）
        guiGraphics.renderItem(baitStack, itemRenderX, itemRenderY);
        // 如果需要显示物品数量（比如诱饵堆叠），可以取消下面的注释
        // guiGraphics.renderItemDecorations(client.font, baitStack, itemRenderX, itemRenderY, null); // null 表示不显示特定文本
    }
}
