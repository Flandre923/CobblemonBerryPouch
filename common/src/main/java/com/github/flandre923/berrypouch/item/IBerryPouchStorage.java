package com.github.flandre923.berrypouch.item;

import net.minecraft.world.item.Item;

import java.util.List;

public interface IBerryPouchStorage {
    boolean matchesSlotItem(int slotIndex, Item item);
    Item getSlotItem(int slotIndex);
    boolean has(Item item); // 添加新的 has 方法
    List<StorageSlot> getStorageSlots();
}
