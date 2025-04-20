package com.github.flandre923.berrypouch.helper;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

public final class ItemNBTHelper {
    public static NonNullList<ItemStack> getList(ItemStack stack, boolean force, Level level) {
        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (contents == ItemContainerContents.EMPTY && force) {
            contents = ItemContainerContents.fromItems(NonNullList.withSize(1, ItemStack.EMPTY));
            stack.set(DataComponents.CONTAINER, contents);
        }
        int slotCount = (int) contents.stream().count();
        NonNullList<ItemStack> items = NonNullList.withSize(slotCount, ItemStack.EMPTY);
        contents.copyInto(items);
        return items;
    }

    public static void setList(ItemStack stack,NonNullList<ItemStack> items,Level level) {
        if (!stack.isEmpty()) {
            ItemContainerContents contents = ItemContainerContents.fromItems(items);
            stack.set(DataComponents.CONTAINER, contents);
        }
    }
}
