package com.github.flandre923.berrypouch.item.pouch;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class PokeBallGunInventory extends SimpleContainer {
    private final ItemStack gunStack;

    public PokeBallGunInventory(ItemStack gunStack, int size) {
        super(size);
        this.gunStack = gunStack;
        loadFromStack();
    }

    /**
     * 从物品组件加载数据
     */
    private void loadFromStack() {
        if (gunStack.isEmpty()) return;
        ItemContainerContents contents = gunStack.get(DataComponents.CONTAINER);
        if (contents != null && contents != ItemContainerContents.EMPTY) {
            contents.copyInto(this.getItems());
        }
    }

    /**
     * 保存数据到物品组件
     */
    private void saveToStack() {
        if (gunStack.isEmpty()) return;
        gunStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
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



    /**
     * 尝试将物品插入发射器
     * @param toInsert 要插入的物品（会被修改count）
     * @return 是否成功插入（全部或部分）
     */
    public boolean tryInsert(ItemStack toInsert) {
        if (toInsert.isEmpty()) return false;

        int originalCount = toInsert.getCount();

        // 先尝试堆叠到现有的同类型物品
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack slotStack = getItem(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, toInsert)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, toInsert.getCount());
                    slotStack.grow(toAdd);
                    toInsert.shrink(toAdd);
                    setItem(i, slotStack); // 触发保存
                    if (toInsert.isEmpty()) {
                        return true;
                    }
                }
            }
        }

        // 再尝试放入空槽位
        for (int i = 0; i < getContainerSize(); i++) {
            if (getItem(i).isEmpty()) {
                setItem(i, toInsert.copy());
                toInsert.setCount(0);
                return true;
            }
        }

        return toInsert.getCount() < originalCount; // 部分插入也算成功
    }



    /**
     * 静态方法：直接操作 ItemStack 而不创建 Inventory 实例
     */
    public static boolean tryInsertToStack(ItemStack gunStack, ItemStack toInsert) {
        PokeBallGunInventory inv = new PokeBallGunInventory(gunStack, 9);
        return inv.tryInsert(toInsert);
    }



}
