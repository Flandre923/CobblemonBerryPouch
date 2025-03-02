package com.github.flandre923.berrypouch.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class StorageSlot {
    private final Supplier<Item> itemSupplier;

    public StorageSlot(Supplier<Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
    }

    public Item getItemOrNull()
    {
        return itemSupplier.get();
    }

    public boolean isItem(ItemStack itemStack) {
        return itemStack.getItem() == getItemOrNull();
    }

    public boolean isItem(Item item) {
        return item == getItemOrNull();
    }
}
