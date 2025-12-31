package com.github.flandre923.berrypouch.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BerryPouchSlot extends Slot {
    
    public BerryPouchSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }
    
    @Override
    public int getMaxStackSize() {
        return 1;
    }
    
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Integer.MAX_VALUE; // 关键：绕过 arg.getMaxStackSize()
    }
}