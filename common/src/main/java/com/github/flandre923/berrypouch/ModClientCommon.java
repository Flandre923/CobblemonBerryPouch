package com.github.flandre923.berrypouch;

import com.cobblemon.mod.common.item.CobblemonItem;
import com.github.flandre923.berrypouch.client.BerryPouchModelHelper;
import com.github.flandre923.berrypouch.client.hud.BaitRenderHandler;
import com.github.flandre923.berrypouch.client.input.KeyBindingManager;
import com.github.flandre923.berrypouch.item.pouch.PokeBallGunHelper;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.world.item.component.CustomData;

public class ModClientCommon {

    public static void init() {
        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_69.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> BerryPouchModelHelper.shouldUseFullModel(stack) ? 1.0F : 0.0F);


        ItemProperties.register(ModRegistries.Items.POKEBALL_GUN.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "poke_ball"),
                (stack, level, entity, seed) -> {
                    String id = PokeBallGunHelper.getSelectedItemId(stack);

                    if (id == null || id.isEmpty()) {
                        ModCommon.LOG.info("Pokeball Gun: id is null or empty");
                        return 0.0F;
                    }

                    String[] parts = id.split(":");
                    if (parts.length > 1) {
                        float index = getPokeBallModelIndex(parts[1]);
                        ModCommon.LOG.info("Pokeball Gun: id={}, path={}, index={}", id, parts[1], index);
                        return index;
                    }
                    ModCommon.LOG.info("Pokeball Gun: id={}, parts.length={}", id, parts.length);
                    return 0f;
                });

        ClientTickEvent.CLIENT_POST.register(__ -> KeyBindingManager.checkKeyInputs());
        ClientGuiEvent.RENDER_HUD.register(new BaitRenderHandler());
    }

    public static float getPokeBallModelIndex(String path) {
        return switch (path) {
            case "poke_ball" -> 1.0F;
            case "citrine_ball" -> 2.0F;
            case "verdant_ball" -> 3.0F;
            case "azure_ball" -> 4.0F;
            case "roseate_ball" -> 5.0F;
            case "slate_ball" -> 6.0F;
            case "premier_ball" -> 7.0F;
            case "great_ball" -> 8.0F;
            case "ultra_ball" -> 9.0F;
            case "safari_ball" -> 10.0F;
            case "fast_ball" -> 11.0F;
            case "level_ball" -> 12.0F;
            case "lure_ball" -> 13.0F;
            case "heavy_ball" -> 14.0F;
            case "love_ball" -> 15.0F;
            case "friend_ball" -> 16.0F;
            case "moon_ball" -> 17.0F;
            case "sport_ball" -> 18.0F;
            case "park_ball" -> 19.0F;
            case "net_ball" -> 20.0F;
            case "dive_ball" -> 21.0F;
            case "nest_ball" -> 22.0F;
            case "repeat_ball" -> 23.0F;
            case "timer_ball" -> 24.0F;
            case "luxury_ball" -> 25.0F;
            case "dusk_ball" -> 26.0F;
            case "heal_ball" -> 27.0F;
            case "quick_ball" -> 28.0F;
            case "dream_ball" -> 29.0F;
            case "beast_ball" -> 30.0F;
            case "master_ball" -> 31.0F;
            case "cherish_ball" -> 32.0F;
            // 古代球种 (Ancient Balls)
            case "ancient_poke_ball" -> 33.0F;
            case "ancient_citrine_ball" -> 34.0F;
            case "ancient_verdant_ball" -> 35.0F;
            case "ancient_azure_ball" -> 36.0F;
            case "ancient_roseate_ball" -> 37.0F;
            case "ancient_slate_ball" -> 38.0F;
            case "ancient_ivory_ball" -> 39.0F;
            case "ancient_great_ball" -> 40.0F;
            case "ancient_ultra_ball" -> 41.0F;
            case "ancient_feather_ball" -> 42.0F;
            case "ancient_wing_ball" -> 43.0F;
            case "ancient_jet_ball" -> 44.0F;
            case "ancient_heavy_ball" -> 45.0F;
            case "ancient_leaden_ball" -> 46.0F;
            case "ancient_gigaton_ball" -> 47.0F;
            case "ancient_origin_ball" -> 48.0F;
            // 默认情况
            default -> 0.0F;
        };
    }

}
