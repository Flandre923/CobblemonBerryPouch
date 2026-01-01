package com.github.flandre923.berrypouch.item.pouch;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

public class PokeBallBeltHelper {
    private static final String SELECTED_INDEX_KEY = "SelectedIndex";
    
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
     * 切换到下一个槽位
     */
    public static void cycleNext(ItemStack beltStack) {
        int current = getSelectedIndex(beltStack);
        setSelectedIndex(beltStack, (current + 1) % 9);
    }
    
    /**
     * 切换到上一个槽位
     */
    public static void cyclePrev(ItemStack beltStack) {
        int current = getSelectedIndex(beltStack);
        setSelectedIndex(beltStack, (current - 1 + 9) % 9);
    }
}