package com.gitlab.srcmc.rctmod.item;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class BerryPouchGui30StorageSlot  implements  IBerryPouchStorage{
    public static final int SIZE = BerryPouch.MEDIUM_SIZE;
    public static final int ROWS = 3;
    public static final int COLUMNS = 8;
    public final BerryPouchGui24StorageSlot berryPouchGui24StorageSlot;
    public final StorageSlot SLOT_25;
    public final StorageSlot SLOT_26;
    public final StorageSlot SLOT_27;
    public final StorageSlot SLOT_28;
    public final StorageSlot SLOT_29;
    public final StorageSlot SLOT_30;

    private final ArrayList<StorageSlot> storageSlots;

    public BerryPouchGui30StorageSlot() {
        berryPouchGui24StorageSlot = new BerryPouchGui24StorageSlot();
        SLOT_25 = new StorageSlot(() -> CobblemonItems.KASIB_BERRY);
        SLOT_26 = new StorageSlot(() -> CobblemonItems.HABAN_BERRY);
        SLOT_27 = new StorageSlot(() -> CobblemonItems.COLBUR_BERRY);
        SLOT_28 = new StorageSlot(() -> CobblemonItems.BABIRI_BERRY);
        SLOT_29 = new StorageSlot(() -> CobblemonItems.CHILAN_BERRY);
        SLOT_30 = new StorageSlot(() -> CobblemonItems.ROSELI_BERRY);


        storageSlots = new ArrayList<>();
        storageSlots.add(SLOT_25);
        storageSlots.add(SLOT_26);
        storageSlots.add(SLOT_27);
        storageSlots.add(SLOT_28);
        storageSlots.add(SLOT_29);
        storageSlots.add(SLOT_30);
    }

    public List<StorageSlot> getStorageSlots() {
        return storageSlots;
    }

    public boolean isSlotItem(int slotIndex, Item item){
        if (slotIndex < 1 || slotIndex > SIZE) {
            return false; // 槽位索引超出范围
        }
        //槽位 1-24 的逻辑由 berryPouch24Slots 处理，槽位 25-30 的逻辑由当前类处理
        if (slotIndex <= BerryPouchGui24StorageSlot.SIZE) {
            return berryPouchGui24StorageSlot.isSlotItem(slotIndex, item); // 使用组合的 BerryPouchGui24StorageSlot 实例来检查
        } else {
            StorageSlot slot = storageSlots.get(slotIndex - BerryPouchGui24StorageSlot.SIZE - 1);
            return slot.isItem(item); // 检查当前类定义的槽位
        }
    }
    public Item getSlotItem(int slotIndex) {

        if (slotIndex < 1 || slotIndex > SIZE) { // 使用 SIZE 常量进行范围检查
            return null; // Slot index out of range
        }
        if (slotIndex <= BerryPouchGui24StorageSlot.SIZE) {
            return berryPouchGui24StorageSlot.getSlotItem(slotIndex); // Delegate to berryPouch24Slots for slots 1-24
        } else {
            // 修正索引：槽位 25 在 storageSlots 中的索引为 0，槽位 26 为 1，以此类推
            return storageSlots.get(slotIndex - BerryPouchGui24StorageSlot.SIZE - 1).getItemOrNull(); // 修正后的索引
        }
    }

    @Override
    public boolean has(Item item) {
        // 先检查当前类的槽位 (25-30)
        for (StorageSlot slot : storageSlots) {
            if (slot.isItem(item)) {
                return true;
            }
        }
        // 再委托给 BerryPouchGui24StorageSlot 检查槽位 (1-24)
        return berryPouchGui24StorageSlot.has(item);
    }
}
