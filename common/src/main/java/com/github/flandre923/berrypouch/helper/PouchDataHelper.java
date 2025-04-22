package com.github.flandre923.berrypouch.helper; // 或者其他合适的包

import com.github.flandre923.berrypouch.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class PouchDataHelper {

    // 获取上次使用的树果 ResourceLocation
    public static Optional<ResourceLocation> getLastUsedBait(ItemStack pouchStack) {
        if (pouchStack == null || pouchStack.isEmpty()) {
            return Optional.empty();
        }
        // 如果 Component 不存在，getOrDefault 会返回 Optional.empty()
        return pouchStack.getOrDefault(ModRegistries.ModDataComponentes.LAST_USED_BAIT.get(), Optional.empty());
    }

    // 设置上次使用的树果
    public static void setLastUsedBait(ItemStack pouchStack, Item baitItem) {
        if (pouchStack == null || pouchStack.isEmpty() || baitItem == null) {
            return;
        }
        ResourceLocation itemRL = BuiltInRegistries.ITEM.getKey(baitItem);
        // 只在有效的 ResourceLocation 时设置
        if (!itemRL.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
             pouchStack.set(ModRegistries.ModDataComponentes.LAST_USED_BAIT.get(), Optional.of(itemRL));
        } else {
             // 如果物品无效（例如 air），则清除记录
             clearLastUsedBait(pouchStack);
        }
    }

    // 清除上次使用的树果记录
    public static void clearLastUsedBait(ItemStack pouchStack) {
        if (pouchStack == null || pouchStack.isEmpty()) {
            return;
        }
        // 移除 Component 或将其设置为空 Optional 都可以
        pouchStack.set(ModRegistries.ModDataComponentes.LAST_USED_BAIT.get(), Optional.empty());
    }
}