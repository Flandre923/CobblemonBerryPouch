package com.github.flandre923.berrypouch.item.pouch;

import blue.endless.jankson.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

public class PokeBallBeltHelper {
    private static final String SELECTED_INDEX_KEY = "SelectedIndex";
    private static final String SELECTED_ITEM_ID_KEY = "SelectedItemId"; // 新增

    /**
     * 获取当前选中的索引
     */
    public static int getSelectedIndex(ItemStack beltStack) {
        if (beltStack.isEmpty()) return 0;
        
        CustomData customData = beltStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag().getInt(SELECTED_INDEX_KEY);
        }
        return 0;
    }

    
    /**
     * 设置当前选中的索引
     */
    public static void setSelectedIndex(ItemStack beltStack, int index) {
        if (beltStack.isEmpty()) return;
        
        CustomData customData = beltStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putInt(SELECTED_INDEX_KEY, Math.max(0, Math.min(index, 8))); // 限制 0-8
        beltStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        updateSelectedItemId(beltStack);
    }
    
    /**
     * 获取指定槽位的物品
     */
    public static ItemStack getItemAt(ItemStack beltStack, int index) {
        ItemContainerContents contents = beltStack.get(DataComponents.CONTAINER);
        if (contents == null || contents == ItemContainerContents.EMPTY) {
            return ItemStack.EMPTY;
        }
        
        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        contents.copyInto(items);
        
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return ItemStack.EMPTY;
    }

    /**
     * 获取当前选中槽位的物品
     */
    public static ItemStack getSelectedItem(ItemStack beltStack) {
        return getItemAt(beltStack, getSelectedIndex(beltStack));
    }

    /**
     * 获取当前选中物品的 ID（ResourceLocation 字符串）
     */
    public static String getSelectedItemId(ItemStack beltStack) {
        if (beltStack.isEmpty()) return "";

        CustomData customData = beltStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag().getString(SELECTED_ITEM_ID_KEY);
        }
        return "";
    }

    /**
     * 设置当前选中物品的 ID
     */
    public static void setSelectedItemId(ItemStack beltStack, String itemId) {
        if (beltStack.isEmpty()) return;

        CustomData customData = beltStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = customData.copyTag();
        tag.putString(SELECTED_ITEM_ID_KEY, itemId != null ? itemId : "");
        beltStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }


    /**
     * 根据 ItemStack 设置选中物品的 ID
     */
    public static void setSelectedItemId(ItemStack beltStack, ItemStack selectedItem) {
        if (selectedItem.isEmpty()) {
            setSelectedItemId(beltStack, "");
        } else {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(selectedItem.getItem());
            setSelectedItemId(beltStack, itemId.toString());
        }
    }

    /**
     * 获取选中物品的 Item（如果存在）
     */
    @Nullable
    public static Item getSelectedItemType(ItemStack beltStack) {
        String itemId = getSelectedItemId(beltStack);
        if (itemId.isEmpty()) return null;

        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) return null;

        return BuiltInRegistries.ITEM.get(location);
    }


    /**
     * 更新选中物品ID为当前槽位的物品
     */
    public static void updateSelectedItemId(ItemStack beltStack) {
        ItemStack selectedItem = getSelectedItem(beltStack);
        setSelectedItemId(beltStack, selectedItem);
    }


    /**
     * 减少指定槽位的物品数量
     */
    public static ItemStack removeItemAt(ItemStack beltStack, int index, int amount) {
        ItemContainerContents contents = beltStack.get(DataComponents.CONTAINER);
        if (contents == null || contents == ItemContainerContents.EMPTY) {
            return ItemStack.EMPTY;
        }
        
        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        contents.copyInto(items);
        
        if (index >= 0 && index < items.size()) {
            ItemStack stackInSlot = items.get(index);
            if (!stackInSlot.isEmpty()) {
                ItemStack removed = stackInSlot.split(amount);
                beltStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                return removed;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 切换到下一个槽位（同时更新选中物品ID）
     */
    public static void cycleNext(ItemStack beltStack) {
        int current = getSelectedIndex(beltStack);
        int next = (current + 1) % 9;
        setSelectedIndex(beltStack, next);
        updateSelectedItemId(beltStack);
    }

    /**
     * 切换到上一个槽位（同时更新选中物品ID）
     */
    public static void cyclePrev(ItemStack beltStack) {
        int current = getSelectedIndex(beltStack);
        int prev = (current - 1 + 9) % 9;
        setSelectedIndex(beltStack, prev);
        updateSelectedItemId(beltStack);
    }
}