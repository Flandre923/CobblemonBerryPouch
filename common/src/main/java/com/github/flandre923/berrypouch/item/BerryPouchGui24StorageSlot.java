package com.github.flandre923.berrypouch.item;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class BerryPouchGui24StorageSlot implements  IBerryPouchStorage{
    public static final int SIZE = BerryPouch.SMALL_SIZE;
    public static final int ROWS = 3;
    public static final int COLUMNS = 8;

    public final StorageSlot SLOT_01;
    public final StorageSlot SLOT_02;
    public final StorageSlot SLOT_03;
    public final StorageSlot SLOT_04;
    public final StorageSlot SLOT_05;
    public final StorageSlot SLOT_06;
    public final StorageSlot SLOT_07;
    public final StorageSlot SLOT_08;
    public final StorageSlot SLOT_09;
    public final StorageSlot SLOT_10;
    public final StorageSlot SLOT_11;
    public final StorageSlot SLOT_12;
    public final StorageSlot SLOT_13;
    public final StorageSlot SLOT_14;
    public final StorageSlot SLOT_15;
    public final StorageSlot SLOT_16;
    public final StorageSlot SLOT_17;
    public final StorageSlot SLOT_18;
    public final StorageSlot SLOT_19;
    public final StorageSlot SLOT_20;
    public final StorageSlot SLOT_21;
    public final StorageSlot SLOT_22;
    public final StorageSlot SLOT_23;
    public final StorageSlot SLOT_24;
    private final ArrayList<StorageSlot> storageSlots;

    public BerryPouchGui24StorageSlot() {
        SLOT_01 = new StorageSlot(() -> CobblemonItems.ORAN_BERRY);
        SLOT_02 = new StorageSlot(() -> CobblemonItems.CHERI_BERRY);
        SLOT_03 = new StorageSlot(() -> CobblemonItems.CHESTO_BERRY);
        SLOT_04 = new StorageSlot(() -> CobblemonItems.PECHA_BERRY);
        SLOT_05 = new StorageSlot(() -> CobblemonItems.ASPEAR_BERRY);
        SLOT_06 = new StorageSlot(() -> CobblemonItems.RAWST_BERRY);
        SLOT_07 = new StorageSlot(() -> CobblemonItems.PERSIM_BERRY);
        SLOT_08 = new StorageSlot(() -> CobblemonItems.RAZZ_BERRY);

        SLOT_09 = new StorageSlot(() -> CobblemonItems.BLUK_BERRY);
        SLOT_10 = new StorageSlot(() -> CobblemonItems.NANAB_BERRY);
        SLOT_11 = new StorageSlot(() -> CobblemonItems.WEPEAR_BERRY);
        SLOT_12 = new StorageSlot(() -> CobblemonItems.PINAP_BERRY);
        SLOT_13 = new StorageSlot(() -> CobblemonItems.OCCA_BERRY);
        SLOT_14 = new StorageSlot(() -> CobblemonItems.PASSHO_BERRY);
        SLOT_15 = new StorageSlot(() -> CobblemonItems.WACAN_BERRY);
        SLOT_16 = new StorageSlot(() -> CobblemonItems.RINDO_BERRY);

        SLOT_17 = new StorageSlot(() -> CobblemonItems.YACHE_BERRY);
        SLOT_18 = new StorageSlot(() -> CobblemonItems.CHOPLE_BERRY);
        SLOT_19 = new StorageSlot(() -> CobblemonItems.KEBIA_BERRY);
        SLOT_20 = new StorageSlot(() -> CobblemonItems.SHUCA_BERRY);
        SLOT_21 = new StorageSlot(() -> CobblemonItems.COBA_BERRY);
        SLOT_22 = new StorageSlot(() -> CobblemonItems.PAYAPA_BERRY);
        SLOT_23 = new StorageSlot(() -> CobblemonItems.TANGA_BERRY);
        SLOT_24 = new StorageSlot(() -> CobblemonItems.CHARTI_BERRY);

        storageSlots = new ArrayList<>();
        storageSlots.add(SLOT_01);
        storageSlots.add(SLOT_02);
        storageSlots.add(SLOT_03);
        storageSlots.add(SLOT_04);
        storageSlots.add(SLOT_05);
        storageSlots.add(SLOT_06);
        storageSlots.add(SLOT_07);
        storageSlots.add(SLOT_08);
        storageSlots.add(SLOT_09);
        storageSlots.add(SLOT_10);
        storageSlots.add(SLOT_11);
        storageSlots.add(SLOT_12);
        storageSlots.add(SLOT_13);
        storageSlots.add(SLOT_14);
        storageSlots.add(SLOT_15);
        storageSlots.add(SLOT_16);
        storageSlots.add(SLOT_17);
        storageSlots.add(SLOT_18);
        storageSlots.add(SLOT_19);
        storageSlots.add(SLOT_20);
        storageSlots.add(SLOT_21);
        storageSlots.add(SLOT_22);
        storageSlots.add(SLOT_23);
        storageSlots.add(SLOT_24);
    }

    public List<StorageSlot> getStorageSlots() {
        return storageSlots;
    }

    public boolean isSlotItem(int slotIndex, Item item){
        if (slotIndex < 1 || slotIndex > SIZE) {
            return false; // Slot index out of range
        }
        StorageSlot slot = storageSlots.get(slotIndex - 1);
        return slot.isItem(item);
    }

    public Item getSlotItem(int slotIndex) {
        if (slotIndex < 1 || slotIndex > storageSlots.size()) {
            return null; // Slot index out of range
        }
        return storageSlots.get(slotIndex - 1).getItemOrNull();
    }

    @Override
    public boolean has(Item item) {
        for (StorageSlot slot : storageSlots) {
            if (slot.isItem(item)) {
                return true; // 如果 item 在任何一个槽位的允许列表中，返回 true
            }
        }
        return false; // 遍历完所有槽位都没有找到匹配的 item，返回 false
    }

}
