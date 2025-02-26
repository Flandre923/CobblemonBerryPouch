package com.gitlab.srcmc.rctmod.item;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class BerryPouchGui69StorageSlot  implements  IBerryPouchStorage{
    public static final int SIZE = BerryPouch.LARGE_SIZE;
    public static final int ROWS = 3;
    public static final int COLUMNS = 8;

    public final BerryPouchGui30StorageSlot berryPouch30Slots; // 组合 BerryPouchGui30StorageSlot
    public final StorageSlot SLOT_31;
    public final StorageSlot SLOT_32;
    public final StorageSlot SLOT_33;
    public final StorageSlot SLOT_34;
    public final StorageSlot SLOT_35;
    public final StorageSlot SLOT_36;
    public final StorageSlot SLOT_37;
    public final StorageSlot SLOT_38;
    public final StorageSlot SLOT_39;
    public final StorageSlot SLOT_40;
    public final StorageSlot SLOT_41;
    public final StorageSlot SLOT_42;
    public final StorageSlot SLOT_43;
    public final StorageSlot SLOT_44;
    public final StorageSlot SLOT_45;
    public final StorageSlot SLOT_46;
    public final StorageSlot SLOT_47;
    public final StorageSlot SLOT_48;
    public final StorageSlot SLOT_49;
    public final StorageSlot SLOT_50;
    public final StorageSlot SLOT_51;
    public final StorageSlot SLOT_52;
    public final StorageSlot SLOT_53;
    public final StorageSlot SLOT_54;
    public final StorageSlot SLOT_55;
    public final StorageSlot SLOT_56;
    public final StorageSlot SLOT_57;
    public final StorageSlot SLOT_58;
    public final StorageSlot SLOT_59;
    public final StorageSlot SLOT_60;
    public final StorageSlot SLOT_61;
    public final StorageSlot SLOT_62;
    public final StorageSlot SLOT_63;
    public final StorageSlot SLOT_64;
    public final StorageSlot SLOT_65;
    public final StorageSlot SLOT_66;
    public final StorageSlot SLOT_67;
    public final StorageSlot SLOT_68;
    public final StorageSlot SLOT_69;

    private final ArrayList<StorageSlot> storageSlots;

    public BerryPouchGui69StorageSlot() {
        berryPouch30Slots = new BerryPouchGui30StorageSlot(); // 初始化 BerryPouchGui30StorageSlot 实例

        SLOT_31 = new StorageSlot(() -> CobblemonItems.LEPPA_BERRY);
        SLOT_32 = new StorageSlot(() -> CobblemonItems.LUM_BERRY);
        SLOT_33 = new StorageSlot(() -> CobblemonItems.FIGY_BERRY);
        SLOT_34 = new StorageSlot(() -> CobblemonItems.WIKI_BERRY);
        SLOT_35 = new StorageSlot(() -> CobblemonItems.MAGO_BERRY);
        SLOT_36 = new StorageSlot(() -> CobblemonItems.AGUAV_BERRY);
        SLOT_37 = new StorageSlot(() -> CobblemonItems.IAPAPA_BERRY);
        SLOT_38 = new StorageSlot(() -> CobblemonItems.SITRUS_BERRY);
        SLOT_39 = new StorageSlot(() -> CobblemonItems.TOUGA_BERRY);
        SLOT_40 = new StorageSlot(() -> CobblemonItems.CORNN_BERRY);
        SLOT_41 = new StorageSlot(() -> CobblemonItems.MAGOST_BERRY);
        SLOT_42 = new StorageSlot(() -> CobblemonItems.RABUTA_BERRY);
        SLOT_43 = new StorageSlot(() -> CobblemonItems.NOMEL_BERRY);
        SLOT_44 = new StorageSlot(() -> CobblemonItems.ENIGMA_BERRY);
        SLOT_45 = new StorageSlot(() -> CobblemonItems.POMEG_BERRY);
        SLOT_46 = new StorageSlot(() -> CobblemonItems.KELPSY_BERRY);
        SLOT_47 = new StorageSlot(() -> CobblemonItems.QUALOT_BERRY);
        SLOT_48 = new StorageSlot(() -> CobblemonItems.HONDEW_BERRY);
        SLOT_49 = new StorageSlot(() -> CobblemonItems.GREPA_BERRY);
        SLOT_50 = new StorageSlot(() -> CobblemonItems.TAMATO_BERRY);
        SLOT_51 = new StorageSlot(() -> CobblemonItems.SPELON_BERRY);
        SLOT_52 = new StorageSlot(() -> CobblemonItems.PAMTRE_BERRY);
        SLOT_53 = new StorageSlot(() -> CobblemonItems.WATMEL_BERRY);
        SLOT_54 = new StorageSlot(() -> CobblemonItems.DURIN_BERRY);
        SLOT_55 = new StorageSlot(() -> CobblemonItems.BELUE_BERRY);
        SLOT_56 = new StorageSlot(() -> CobblemonItems.KEE_BERRY);
        SLOT_57 = new StorageSlot(() -> CobblemonItems.MARANGA_BERRY);
        SLOT_58 = new StorageSlot(() -> CobblemonItems.HOPO_BERRY);
        SLOT_59 = new StorageSlot(() -> CobblemonItems.LIECHI_BERRY);
        SLOT_60 = new StorageSlot(() -> CobblemonItems.GANLON_BERRY);
        SLOT_61 = new StorageSlot(() -> CobblemonItems.SALAC_BERRY);
        SLOT_62 = new StorageSlot(() -> CobblemonItems.PETAYA_BERRY);
        SLOT_63 = new StorageSlot(() -> CobblemonItems.APICOT_BERRY);
        SLOT_64 = new StorageSlot(() -> CobblemonItems.LANSAT_BERRY);
        SLOT_65 = new StorageSlot(() -> CobblemonItems.STARF_BERRY);
        SLOT_66 = new StorageSlot(() -> CobblemonItems.MICLE_BERRY);
        SLOT_67 = new StorageSlot(() -> CobblemonItems.CUSTAP_BERRY);
        SLOT_68 = new StorageSlot(() -> CobblemonItems.JABOCA_BERRY);
        SLOT_69 = new StorageSlot(() -> CobblemonItems.ROWAP_BERRY);

        storageSlots = new ArrayList<>();
        // 先添加 BerryPouchGui30StorageSlot 中的所有槽位 (1-30)
        // 然后添加额外的 39 个槽位 (31-69)
        storageSlots.add(SLOT_31);
        storageSlots.add(SLOT_32);
        storageSlots.add(SLOT_33);
        storageSlots.add(SLOT_34);
        storageSlots.add(SLOT_35);
        storageSlots.add(SLOT_36);
        storageSlots.add(SLOT_37);
        storageSlots.add(SLOT_38);
        storageSlots.add(SLOT_39);
        storageSlots.add(SLOT_40);
        storageSlots.add(SLOT_41);
        storageSlots.add(SLOT_42);
        storageSlots.add(SLOT_43);
        storageSlots.add(SLOT_44);
        storageSlots.add(SLOT_45);
        storageSlots.add(SLOT_46);
        storageSlots.add(SLOT_47);
        storageSlots.add(SLOT_48);
        storageSlots.add(SLOT_49);
        storageSlots.add(SLOT_50);
        storageSlots.add(SLOT_51);
        storageSlots.add(SLOT_52);
        storageSlots.add(SLOT_53);
        storageSlots.add(SLOT_54);
        storageSlots.add(SLOT_55);
        storageSlots.add(SLOT_56);
        storageSlots.add(SLOT_57);
        storageSlots.add(SLOT_58);
        storageSlots.add(SLOT_59);
        storageSlots.add(SLOT_60);
        storageSlots.add(SLOT_61);
        storageSlots.add(SLOT_62);
        storageSlots.add(SLOT_63);
        storageSlots.add(SLOT_64);
        storageSlots.add(SLOT_65);
        storageSlots.add(SLOT_66);
        storageSlots.add(SLOT_67);
        storageSlots.add(SLOT_68);
        storageSlots.add(SLOT_69);
    }

    public List<StorageSlot> getStorageSlots() {
        return storageSlots;
    }
    public boolean isSlotItem(int slotIndex, Item item){
        if (slotIndex < 1 || slotIndex > SIZE) {
            return false; // 槽位索引超出范围
        }
        // 槽位 1-30 的逻辑由 berryPouch30Slots 处理，槽位 31-69 的逻辑由当前类处理
        if (slotIndex <= BerryPouchGui30StorageSlot.SIZE) {
            return berryPouch30Slots.isSlotItem(slotIndex, item); // 使用组合的 BerryPouchGui30StorageSlot 实例来检查
        } else {
            StorageSlot slot = storageSlots.get(slotIndex - BerryPouchGui30StorageSlot.SIZE - 1);
            return slot.isItem(item); // 检查当前类定义的槽位
        }
    }
    public Item getSlotItem(int slotIndex) {
        if (slotIndex < 1 || slotIndex > SIZE) { // 使用 SIZE 常量进行范围检查
            return null; // Slot index out of range
        }
        if (slotIndex <= BerryPouchGui30StorageSlot.SIZE) {
            return berryPouch30Slots.getSlotItem(slotIndex); // Delegate to berryPouch30Slots for slots 1-30
        } else {
            // 修正索引：槽位 31 在 storageSlots 中的索引为 0，槽位 32 为 1，以此类推
            return storageSlots.get(slotIndex - BerryPouchGui30StorageSlot.SIZE - 1).getItemOrNull(); // 修正后的索引
        }
    }
    @Override
    public boolean has(Item item) {
        // 先检查当前类的槽位 (31-69)
        for (StorageSlot slot : storageSlots) {
            if (slot.isItem(item)) {
                return true;
            }
        }
        // 再委托给 berryPouch30Slots 检查槽位 (1-30)
        return berryPouch30Slots.has(item);
    }

}
