package com.github.flandre923.berrypouch.item;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.world.item.Item;

public class BerryPouchGui86StorageSlot extends AbstractBerryPouchStorageSlot {
    public BerryPouchGui86StorageSlot(int size) {
        super(size); // Pass the size to the parent constructor
    }

    @Override
    protected Item getSlotItemInternal(int slotIndex) {
        // Slot indices 1-86 correspond to the BerryPouchGui86_STORAGE_SLOTS
        if (slotIndex >= 1 && slotIndex <= 70) {
            // Berry slots (1-70) - same as BerryPouchGui70StorageSlot
            switch (slotIndex) {
                // Natural berries (slots 1-30)
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
                
                // Mixed/Hybrid berries (slots 31-70)
                case 31: return CobblemonItems.LEPPA_BERRY;
                case 32: return CobblemonItems.LUM_BERRY;
                case 33: return CobblemonItems.FIGY_BERRY;
                case 34: return CobblemonItems.WIKI_BERRY;
                case 35: return CobblemonItems.MAGO_BERRY;
                case 36: return CobblemonItems.AGUAV_BERRY;
                case 37: return CobblemonItems.IAPAPA_BERRY;
                case 38: return CobblemonItems.SITRUS_BERRY;
                case 39: return CobblemonItems.TOUGA_BERRY;
                case 40: return CobblemonItems.CORNN_BERRY;
                case 41: return CobblemonItems.MAGOST_BERRY;
                case 42: return CobblemonItems.RABUTA_BERRY;
                case 43: return CobblemonItems.NOMEL_BERRY;
                case 44: return CobblemonItems.ENIGMA_BERRY;
                case 45: return CobblemonItems.POMEG_BERRY;
                case 46: return CobblemonItems.KELPSY_BERRY;
                case 47: return CobblemonItems.QUALOT_BERRY;
                case 48: return CobblemonItems.HONDEW_BERRY;
                case 49: return CobblemonItems.GREPA_BERRY;
                case 50: return CobblemonItems.TAMATO_BERRY;
                case 51: return CobblemonItems.SPELON_BERRY;
                case 52: return CobblemonItems.PAMTRE_BERRY;
                case 53: return CobblemonItems.WATMEL_BERRY;
                case 54: return CobblemonItems.DURIN_BERRY;
                case 55: return CobblemonItems.BELUE_BERRY;
                case 56: return CobblemonItems.KEE_BERRY;
                case 57: return CobblemonItems.MARANGA_BERRY;
                case 58: return CobblemonItems.HOPO_BERRY;
                case 59: return CobblemonItems.LIECHI_BERRY;
                case 60: return CobblemonItems.GANLON_BERRY;
                case 61: return CobblemonItems.SALAC_BERRY;
                case 62: return CobblemonItems.PETAYA_BERRY;
                case 63: return CobblemonItems.APICOT_BERRY;
                case 64: return CobblemonItems.LANSAT_BERRY;
                case 65: return CobblemonItems.STARF_BERRY;
                case 66: return CobblemonItems.MICLE_BERRY;
                case 67: return CobblemonItems.CUSTAP_BERRY;
                case 68: return CobblemonItems.JABOCA_BERRY;
                case 69: return CobblemonItems.ROWAP_BERRY;
                case 70: return CobblemonItems.EGGANT_BERRY;
            }
        } else if (slotIndex >= 71 && slotIndex <= 86) {
            // Other baits slots (71-86) - return null as these are handled by tag checking
            return null;
        }
        
        return null; // Should not happen if slotIndex is valid (1-86)
    }
}