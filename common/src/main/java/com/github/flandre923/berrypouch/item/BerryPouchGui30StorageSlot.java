package com.github.flandre923.berrypouch.item;

import com.cobblemon.mod.common.CobblemonItems;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import net.minecraft.world.item.Item;

public class BerryPouchGui30StorageSlot  extends  AbstractBerryPouchStorageSlot{
    public BerryPouchGui30StorageSlot(int size) {
        super(size); // Pass the size to the parent constructor
    }

    @Override
    protected Item getSlotItemInternal(int slotIndex) {
        // Slot indices 1-30 correspond to the original BerryPouchGui30_STORAGE_SLOTS
        switch (slotIndex) {
            case 1: return CobblemonItems.ORAN_BERRY;
            case 2: return CobblemonItems.CHERI_BERRY;
            case 3: return CobblemonItems.CHESTO_BERRY;
            case 4: return CobblemonItems.PECHA_BERRY;
            case 5: return CobblemonItems.ASPEAR_BERRY;
            case 6: return CobblemonItems.RAWST_BERRY;
            case 7: return CobblemonItems.PERSIM_BERRY;
            case 8: return CobblemonItems.RAZZ_BERRY;
            case 9: return CobblemonItems.BLUK_BERRY;
            case 10: return CobblemonItems.NANAB_BERRY;
            case 11: return CobblemonItems.WEPEAR_BERRY;
            case 12: return CobblemonItems.PINAP_BERRY;
            case 13: return CobblemonItems.OCCA_BERRY;
            case 14: return CobblemonItems.PASSHO_BERRY;
            case 15: return CobblemonItems.WACAN_BERRY;
            case 16: return CobblemonItems.RINDO_BERRY;
            case 17: return CobblemonItems.YACHE_BERRY;
            case 18: return CobblemonItems.CHOPLE_BERRY;
            case 19: return CobblemonItems.KEBIA_BERRY;
            case 20: return CobblemonItems.SHUCA_BERRY;
            case 21: return CobblemonItems.COBA_BERRY;
            case 22: return CobblemonItems.PAYAPA_BERRY;
            case 23: return CobblemonItems.TANGA_BERRY;
            case 24: return CobblemonItems.CHARTI_BERRY;
            case 25: return CobblemonItems.KASIB_BERRY;
            case 26: return CobblemonItems.HABAN_BERRY;
            case 27: return CobblemonItems.COLBUR_BERRY;
            case 28: return CobblemonItems.BABIRI_BERRY;
            case 29: return CobblemonItems.CHILAN_BERRY;
            case 30: return CobblemonItems.ROSELI_BERRY;
            default: return null; // Should not happen if slotIndex is valid (1-30)
        }
    }
}
