package com.github.flandre923.berrypouch.storage;

import net.minecraft.world.item.ItemStack;

/**
 * 扩展的ItemStack包装器，支持超过默认maxStackSize的数量
 * 不修改原始ItemStack，而是在容器层面管理额外的数量
 */
public class ExtendedItemStack {
    private final ItemStack baseStack;
    private int extendedCount;
    
    public ExtendedItemStack(ItemStack stack) {
        this.baseStack = stack.copy();
        this.extendedCount = stack.getCount();
    }
    
    public ExtendedItemStack(ItemStack stack, int count) {
        this.baseStack = stack.copy();
        this.baseStack.setCount(1); // 基础ItemStack始终保持为1
        this.extendedCount = count;
    }
    
    /**
     * 获取真实的数量（可能超过maxStackSize）
     */
    public int getExtendedCount() {
        return extendedCount;
    }
    
    /**
     * 设置真实的数量
     */
    public void setExtendedCount(int count) {
        this.extendedCount = Math.max(0, count);
    }
    
    /**
     * 增加数量
     */
    public void grow(int amount) {
        this.extendedCount += amount;
    }
    
    /**
     * 减少数量
     */
    public void shrink(int amount) {
        this.extendedCount = Math.max(0, this.extendedCount - amount);
    }
    
    /**
     * 获取用于显示的ItemStack（数量被限制在maxStackSize内）
     */
    public ItemStack getDisplayStack() {
        if (extendedCount <= 0) {
            return ItemStack.EMPTY;
        }
        
        ItemStack displayStack = baseStack.copy();
        int displayCount = Math.min(extendedCount, baseStack.getMaxStackSize());
        displayStack.setCount(displayCount);
        return displayStack;
    }
    
    /**
     * 获取基础ItemStack（用于类型比较）
     */
    public ItemStack getBaseStack() {
        return baseStack.copy();
    }
    
    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return baseStack.isEmpty() || extendedCount <= 0;
    }
    
    /**
     * 检查是否与另一个ItemStack相同（忽略数量）
     */
    public boolean isSameItem(ItemStack other) {
        return ItemStack.isSameItemSameComponents(baseStack, other);
    }
    
    /**
     * 检查是否与另一个ExtendedItemStack相同（忽略数量）
     */
    public boolean isSameItem(ExtendedItemStack other) {
        return ItemStack.isSameItemSameComponents(baseStack, other.baseStack);
    }
    
    /**
     * 尝试添加物品，返回无法添加的数量
     */
    public int tryAdd(ItemStack stack, int maxLimit) {
        if (!isSameItem(stack)) {
            return stack.getCount(); // 不同物品，无法添加
        }
        
        int availableSpace = maxLimit - extendedCount;
        if (availableSpace <= 0) {
            return stack.getCount(); // 没有空间
        }
        
        int toAdd = Math.min(stack.getCount(), availableSpace);
        grow(toAdd);
        return stack.getCount() - toAdd; // 返回剩余数量
    }
    
    /**
     * 尝试移除物品，返回实际移除的ItemStack
     */
    public ItemStack tryRemove(int amount) {
        if (isEmpty()) {
            return ItemStack.EMPTY;
        }
        
        int toRemove = Math.min(amount, extendedCount);
        shrink(toRemove);
        
        ItemStack result = baseStack.copy();
        result.setCount(toRemove);
        return result;
    }
    
    /**
     * 复制这个ExtendedItemStack
     */
    public ExtendedItemStack copy() {
        return new ExtendedItemStack(baseStack, extendedCount);
    }
    
    @Override
    public String toString() {
        return "ExtendedItemStack{" +
                "item=" + baseStack.getItem() +
                ", extendedCount=" + extendedCount +
                '}';
    }
}