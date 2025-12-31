package com.github.flandre923.berrypouch.storage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * 扩展容器，支持超大堆叠
 * 内部使用ExtendedItemStack管理真实数量，对外提供标准ItemStack接口
 */
public class ExtendedContainer {
    private final NonNullList<ExtendedItemStack> extendedItems;
    private final int size;
    public final int limitSize;
    
    public ExtendedContainer(int size) {
        this.size = size;
        this.extendedItems = NonNullList.withSize(size, new ExtendedItemStack(ItemStack.EMPTY));
        this.limitSize = 256;
    }
    

    
    /**
     * 获取用于显示的ItemStack（数量可能被限制）
     */
    public ItemStack getDisplayStack(int slot) {
        if (slot >= 0 && slot < size) {
            return extendedItems.get(slot).getDisplayStack();
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * 获取真实的数量
     */
    public int getRealCount(int slot) {
        if (slot >= 0 && slot < size) {
            return extendedItems.get(slot).getExtendedCount();
        }
        return 0;
    }
    
    /**
     * 设置槽位的物品和数量
     */
    public void setItem(int slot, ItemStack stack, int count) {
        if (slot >= 0 && slot < size) {
            if (stack.isEmpty() || count <= 0) {
                extendedItems.set(slot, new ExtendedItemStack(ItemStack.EMPTY));
            } else {
                extendedItems.set(slot, new ExtendedItemStack(stack, count));
            }
        }
    }
    
    /**
     * 设置槽位的物品（使用ItemStack的数量）
     */
    public void setItem(int slot, ItemStack stack) {
        setItem(slot, stack, stack.getCount());
    }
    
    /**
     * 尝试插入物品到指定槽位
     */
    public ItemStack insertItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= size || stack.isEmpty()) {
            return stack;
        }
        
        ExtendedItemStack currentExtended = extendedItems.get(slot);
        int maxLimit = this.limitSize;
        
        if (currentExtended.isEmpty()) {
            // 空槽位，直接放置
            int toPlace = Math.min(stack.getCount(), maxLimit);
            setItem(slot, stack, toPlace);
            
            ItemStack remaining = stack.copy();
            remaining.shrink(toPlace);
            return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
        } else if (currentExtended.isSameItem(stack)) {
            // 相同物品，尝试堆叠
            int remainingCount = currentExtended.tryAdd(stack, maxLimit);
            
            if (remainingCount < stack.getCount()) {
                // 成功添加了一些物品
                ItemStack remaining = stack.copy();
                remaining.setCount(remainingCount);
                return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
            }
        }
        
        return stack; // 无法插入
    }
    
    /**
     * 从指定槽位移除物品
     */
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= size || amount <= 0) {
            return ItemStack.EMPTY;
        }
        
        ExtendedItemStack currentExtended = extendedItems.get(slot);
        if (currentExtended.isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        // 限制移除数量为物品的maxStackSize
        ItemStack baseStack = currentExtended.getBaseStack();
        int maxRemove = Math.min(amount, baseStack.getMaxStackSize());
        
        return currentExtended.tryRemove(maxRemove);
    }
    
    /**
     * 检查槽位是否为空
     */
    public boolean isEmpty(int slot) {
        if (slot >= 0 && slot < size) {
            return extendedItems.get(slot).isEmpty();
        }
        return true;
    }
    
    /**
     * 获取容器大小
     */
    public int getContainerSize() {
        return size;
    }
    
    /**
     * 转换为标准的NonNullList<ItemStack>用于保存
     */
    public NonNullList<ItemStack> toItemList() {
        NonNullList<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            items.set(i, getDisplayStack(i));
        }
        return items;
    }
    
    /**
     * 从标准的NonNullList<ItemStack>加载
     */
    public void fromItemList(NonNullList<ItemStack> items) {
        for (int i = 0; i < Math.min(items.size(), size); i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                setItem(i, stack, stack.getCount());
            } else {
                setItem(i, ItemStack.EMPTY, 0);
            }
        }
    }
    
    /**
     * 获取真实数量的数组（用于NBT保存）
     */
    public int[] getRealCounts() {
        int[] counts = new int[size];
        for (int i = 0; i < size; i++) {
            counts[i] = extendedItems.get(i).getExtendedCount();
        }
        return counts;
    }
    
    /**
     * 设置真实数量的数组（用于NBT加载）
     */
    public void setRealCounts(int[] counts) {
        for (int i = 0; i < Math.min(counts.length, size); i++) {
            ExtendedItemStack extended = extendedItems.get(i);
            if (!extended.isEmpty()) {
                extended.setExtendedCount(counts[i]);
            }
        }
    }
}