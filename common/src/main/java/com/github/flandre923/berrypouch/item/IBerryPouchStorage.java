package com.github.flandre923.berrypouch.item;

import net.minecraft.world.item.Item;

public interface IBerryPouchStorage {
    boolean isSlotItem(int slotIndex, Item item);
    Item getSlotItem(int slotIndex);
    boolean has(Item item); // 添加新的 has 方法
}
