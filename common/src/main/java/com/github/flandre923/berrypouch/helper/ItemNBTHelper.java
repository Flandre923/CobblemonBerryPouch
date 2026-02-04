package com.github.flandre923.berrypouch.helper;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

public final class ItemNBTHelper {
    private static final String SLOT_COUNTS_KEY = "BerryPouchSlotCounts";
    /**
     * 加载扩展背包数据（物品类型 + 真实数量）
     */
    public static void loadExtendedInventory(ItemStack stack, Level level,
                                             NonNullList<ItemStack> items, int[] slotCounts) {
        // 加载物品类型
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(items);

        // 加载真实数量
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();

        if (tag.contains(SLOT_COUNTS_KEY, Tag.TAG_INT_ARRAY)) {
            int[] saved = tag.getIntArray(SLOT_COUNTS_KEY);
            System.arraycopy(saved, 0, slotCounts, 0, Math.min(saved.length, slotCounts.length));
        } else {
            // 兼容旧数据：使用ItemStack自身的count
            for (int i = 0; i < items.size() && i < slotCounts.length; i++) {
                slotCounts[i] = items.get(i).getCount();
            }
        }
    }

    /**
     * 保存扩展背包数据
     */
    public static void saveExtendedInventory(ItemStack stack, Level level,
                                             NonNullList<ItemStack> items, int[] slotCounts) {
        if (stack.isEmpty()) return;

        // 保存物品类型（数量设为1，仅作标记）
        NonNullList<ItemStack> typeItems = NonNullList.withSize(items.size(), ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (!item.isEmpty() && slotCounts[i] > 0) {
                typeItems.set(i, item.copyWithCount(1));
            }
        }
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(typeItems));

        // 保存真实数量到CustomData
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putIntArray(SLOT_COUNTS_KEY, slotCounts.clone());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

}
