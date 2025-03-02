package com.github.flandre923.berrypouch.item;

import com.github.flandre923.berrypouch.helper.ItemNBTHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemBackedInventory extends SimpleContainer {

    private final ItemStack stack;
    private final Level level;  // 添加 level 字段
    private IBerryPouchStorage pouchStorageSlot;

    public ItemBackedInventory(ItemStack stack, Level level, int expectedSize) {
        super(expectedSize);
        this.stack = stack;
        this.level = level;
        BerryPouch berryPouch = (BerryPouch)stack.getItem();
        int pouchSize = berryPouch.getSize();

        if (pouchSize == BerryPouch.SMALL_SIZE) {
            this.pouchStorageSlot = BerryPouch.POUCH_GUI_24_STORAGE_SLOT;
        } else if (pouchSize == BerryPouch.MEDIUM_SIZE) {
            this.pouchStorageSlot = BerryPouch.POUCH_GUI_30_STORAGE_SLOT;
        } else if (pouchSize == BerryPouch.LARGE_SIZE) {
            this.pouchStorageSlot = BerryPouch.POUCH_GUI_69_STORAGE_SLOT;
        } else {
            this.pouchStorageSlot = null; // Or handle default case if needed, maybe throw error or default to small size? Based on context, setting to null might be reasonable if size is unexpected.
        }
        NonNullList<ItemStack> lst = ItemNBTHelper.getList(stack,false,level);
        for (int i = 0; i < lst.size(); i++) {
            setItem(i, lst.get(i));
        }
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (pouchStorageSlot != null) {
            return pouchStorageSlot.isSlotItem(slot + 1, stack.getItem());
        }
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return !stack.isEmpty();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        ItemNBTHelper.setList(stack, getItems(), level);
    }
}
