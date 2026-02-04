package com.github.flandre923.berrypouch.item.pouch;

import com.github.flandre923.berrypouch.helper.ItemNBTHelper;
import com.github.flandre923.berrypouch.item.IBerryPouchStorage;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BerryPouchInventory extends SimpleContainer {

    private final ItemStack pouchStack;
    private final Level level;  // 添加 level 字段
    private IBerryPouchStorage pouchStorageSlot;
    private final int[] slotCounts;

    public BerryPouchInventory(ItemStack pouchStack, Level level, BerryPouchType pouchType) {
        super(pouchType.getSize()); // 使用BerryPouchType中定义的大小
        this.pouchStack = pouchStack;
        this.level = level;
        this.slotCounts = new int[pouchType.getSize()];
        this.pouchStorageSlot = pouchType.getStorageSlot();
        loadFromNBT();
    }

    private void loadFromNBT() {
//        NonNullList<ItemStack> items = ItemNBTHelper.getList(pouchStack, false, level);
//        for (int i = 0; i < Math.min(items.size(), getContainerSize()); i++) {
//            setItem(i, items.get(i));
//        }
        ItemNBTHelper.loadExtendedInventory(pouchStack, level, getItems(), slotCounts);
    }

    @Override
    public @NotNull ItemStack addItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = stack.copy();

        // 先尝试堆叠到已有相同物品的槽位
        for (int i = 0; i < getContainerSize() && !remaining.isEmpty(); i++) {
            ItemStack existing = getItems().get(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, remaining)) {
                slotCounts[i] += remaining.getCount();
                remaining.setCount(0);
                setChanged();
            }
        }

        // 再放入空槽位
        for (int i = 0; i < getContainerSize() && !remaining.isEmpty(); i++) {
            if (getItems().get(i).isEmpty() && canPlaceItem(i, remaining)) {
                getItems().set(i, remaining.copyWithCount(1));
                slotCounts[i] = remaining.getCount();
                remaining.setCount(0);
                setChanged();
            }
        }

        return remaining; // 返回未能放入的部分
    }
    /**
     * 获取槽位的真实数量（可超过堆叠上限）
     */
    public int getSlotCount(int slot) {
        if (slot >= 0 && slot < slotCounts.length) {
            return slotCounts[slot];
        }
        return 0;
    }

    /**
     * 设置槽位的真实数量
     */
    public void setSlotCount(int slot, int count) {
        if (slot >= 0 && slot < slotCounts.length) {
            slotCounts[slot] = Math.max(0, count);
            setChanged();
        }
    }
    /**
     * 增加槽位数量
     */
    public void addToSlot(int slot, int amount) {
        setSlotCount(slot, getSlotCount(slot) + amount);
    }

    /**
     * 从槽位减少数量，返回实际减少的数量
     */
    public int removeFromSlot(int slot, int amount) {
        int current = getSlotCount(slot);
        int toRemove = Math.min(current, amount);
        setSlotCount(slot, current - toRemove);

        // 如果数量归零，清空物品类型
        if (getSlotCount(slot) <= 0) {
            getItems().set(slot, ItemStack.EMPTY);
        }
        return toRemove;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        getItems().set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1)); // 只存类型
        slotCounts[slot] = stack.isEmpty() ? 0 : stack.getCount();
        setChanged();
    }


    @Override
    public @NotNull ItemStack getItem(int slot) {
        ItemStack typeStack = super.getItem(slot);
        if (typeStack.isEmpty() || slotCounts[slot] <= 0) {
            return ItemStack.EMPTY;
        }
        // 返回带真实数量的副本（用于显示/逻辑）
        return typeStack.copyWithCount(slotCounts[slot]);
    }


    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack typeStack = super.getItem(slot);
        if (typeStack.isEmpty()) return ItemStack.EMPTY;
        // 限制最多取出物品的默认堆叠上限
        int maxTake = typeStack.getMaxStackSize();
        int actualAmount = Math.min(amount, maxTake);
        int removed = removeFromSlot(slot, actualAmount);
        return typeStack.copyWithCount(removed);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (pouchStorageSlot != null) {
            return pouchStorageSlot.matchesSlotItem(slot + 1, stack.getItem());
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return !pouchStack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
//        ItemNBTHelper.setList(pouchStack, getItems(), level);
        ItemNBTHelper.saveExtendedInventory(pouchStack, level, getItems(), slotCounts);
    }
}
