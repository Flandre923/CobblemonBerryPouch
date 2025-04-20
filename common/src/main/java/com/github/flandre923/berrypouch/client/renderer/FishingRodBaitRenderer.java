//package com.github.flandre923.berrypouch.client.renderer;// --- Client Side ---
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.block.model.ItemTransforms; // Use the correct import based on MC version
//import net.minecraft.client.renderer.entity.ItemRenderer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.client.resources.model.BakedModel;
//
//public class FishingRodBaitRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
//
//    private final ItemRenderer itemRenderer;
//    private final BakedModel originalRodModel; // Store the original model if needed
//
//    // 你需要获取 Cobblemon 钓竿的 Item 实例
//    private static final Item COBBLEMON_FISHING_ROD_ITEM = null; // = BuiltInRegistries.ITEM.get(new ResourceLocation("cobblemon", "fishing_rod_identifier"));
//
//    public FishingRodBaitRenderer() {
//        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
//        // 获取原版模型可能需要更复杂的逻辑，取决于 Cobblemon 如何注册其模型
//        // 或者直接渲染钓竿模型然后叠加
//        this.originalRodModel = null; // Placeholder
//    }
//
//
//    public static void register() {
//        // 确保在客户端初始化时调用
//        // 获取 Cobblemon 钓竿的 Item 对象!
//        Item cobblemonRod = BuiltInRegistries.ITEM.get(new ResourceLocation("cobblemon", "the_actual_rod_id")); // Replace with actual ID
//        if (cobblemonRod != Items.AIR) {
//             BuiltinItemRendererRegistry.INSTANCE.register(cobblemonRod, new FishingRodBaitRenderer());
//        } else {
//            ModCommon.LOG.error("Could not find Cobblemon fishing rod item to register renderer!");
//        }
//    }
//
//    @Override
//    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
//        // 1. 渲染基础钓竿模型 (如果需要，或者让原版渲染器处理)
//        // 如果 BuiltinItemRendererRegistry 完全覆盖了原版渲染，你需要在这里获取并渲染钓竿模型
//        BakedModel rodModel = itemRenderer.getModel(stack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
//        itemRenderer.render(stack, mode, false, matrices, vertexConsumers, light, overlay, rodModel);
//
//
//        // 2. 读取 NBT 获取诱饵
//        CompoundTag nbt = stack.getTag();
//        ItemStack baitStack = ItemStack.EMPTY;
//        if (nbt != null && nbt.contains(ModCommon.COBBLEMON_BAIT_NBT_KEY)) {
//            CompoundTag baitTag = nbt.getCompound(ModCommon.COBBLEMON_BAIT_NBT_KEY);
//             // Important: Need RegistryAccess on client. Get it from Minecraft instance or player if available.
//            baitStack = ItemStack.parseOptional(Minecraft.getInstance().level.registryAccess() ,baitTag).orElse(ItemStack.EMPTY);
//        }
//
//        // 3. 如果有诱饵，渲染诱饵模型
//        if (!baitStack.isEmpty()) {
//            matrices.pushPose(); // 保存当前变换状态
//
//            // 4. 应用变换 (平移, 旋转, 缩放) - !!! 需要大量调整 !!!
//            // 这些值需要根据钓竿模型和期望的位置仔细调整
//            // 示例：将诱饵移动到模型的一个点，缩小并旋转
//            // 这些变换也可能需要根据 ItemDisplayContext (mode) 进行调整 (e.g., GUI vs FIRST_PERSON)
//            if (mode == ItemDisplayContext.GUI) {
//                 matrices.translate(0.7, 0.2, 0.0); // 示例 GUI 位置
//                 matrices.scale(0.5f, 0.5f, 0.5f);
//            } else if (mode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || mode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
//                 matrices.translate(0.8, 0.6, -0.2); // 示例第一人称位置
//                 matrices.mulPose(Axis.XP.rotationDegrees(90f)); // 示例旋转
//                 matrices.scale(0.4f, 0.4f, 0.4f);
//            } else if (mode == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || mode == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
//                 matrices.translate(0.5, 0.6, 0.1); // 示例第三人称位置
//                 matrices.scale(0.4f, 0.4f, 0.4f);
//            } else if (mode == ItemDisplayContext.GROUND) {
//                matrices.translate(0.5, 0.3, 0.5); // 示例掉落物位置
//                matrices.scale(0.3f, 0.3f, 0.3f);
//            } else {
//                 // 其他模式 (FIXED, HEAD) 的默认变换
//                 matrices.translate(0.5, 0.5, 0.5); // 居中
//                 matrices.scale(0.3f, 0.3f, 0.3f);
//            }
//
//
//            // 5. 渲染诱饵物品
//            BakedModel baitModel = itemRenderer.getModel(baitStack, Minecraft.getInstance().level, Minecraft.getInstance().player, 0);
//            // 使用 itemRenderer.render 直接渲染物品模型
//            itemRenderer.render(baitStack, mode, false, matrices, vertexConsumers, light, overlay, baitModel);
//
//
//            matrices.popPose(); // 恢复之前的变换状态
//        }
//    }
//}