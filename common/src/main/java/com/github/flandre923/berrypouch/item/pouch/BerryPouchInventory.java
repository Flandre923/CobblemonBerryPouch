package com.github.flandre923.berrypouch.item.pouch;

import com.github.flandre923.berrypouch.helper.ItemNBTHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.IBerryPouchStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BerryPouchInventory extends SimpleContainer {

    private final ItemStack pouchStack;
    private final Level level;  // 添加 level 字段
    private IBerryPouchStorage pouchStorageSlot;
    private final BerryPouchType pouchType;

    public BerryPouchInventory(ItemStack pouchStack, Level level, BerryPouchType pouchType) {
        super(pouchType.getSize()); // 使用BerryPouchType中定义的大小
        this.pouchStack = pouchStack;
        this.level = level;
        this.pouchType = pouchType;
        this.pouchStorageSlot = pouchType.getStorageSlot();
        loadFromNBT();
    }

    private void loadFromNBT() {
        NonNullList<ItemStack> items = ItemNBTHelper.getList(pouchStack, false, level);
        for (int i = 0; i < Math.min(items.size(), getContainerSize()); i++) {
            setItem(i, items.get(i));
        }
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
        ItemNBTHelper.setList(pouchStack, getItems(), level);
    }
}
