package com.github.flandre923.berrypouch.item.pouch;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class PokeBallBeltInventory extends SimpleContainer {
    private final ItemStack beltStack;

    public PokeBallBeltInventory(ItemStack beltStack, int size) {
        super(size);
        this.beltStack = beltStack;
        loadFromStack();
    }

    /**
     * 从物品组件加载数据
     */
    private void loadFromStack() {
        if (beltStack.isEmpty()) return;
        ItemContainerContents contents = beltStack.get(DataComponents.CONTAINER);
        if (contents != null && contents != ItemContainerContents.EMPTY) {
            contents.copyInto(this.getItems());
        }
    }

    /**
     * 保存数据到物品组件
     */
    private void saveToStack() {
        if (beltStack.isEmpty()) return;
        beltStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        saveToStack();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        saveToStack();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        saveToStack();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack result = super.removeItemNoUpdate(slot);
        saveToStack();
        return result;
    }
}