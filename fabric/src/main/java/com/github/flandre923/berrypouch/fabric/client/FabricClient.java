package com.github.flandre923.berrypouch.fabric.client;

import com.github.flandre923.berrypouch.ModClientCommon;
import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.client.input.KeyBindingManager;
import com.github.flandre923.berrypouch.menu.screen.ApricornBasketScreen;
import com.github.flandre923.berrypouch.menu.screen.LargeBerryPouchScreen;
import com.github.flandre923.berrypouch.menu.screen.PokeBallGunScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModRegistries.ModMenuTypes.POKEBALL_GUN_MENU.get(), PokeBallGunScreen::new);
        MenuScreens.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get(), LargeBerryPouchScreen::new);
        MenuScreens.register(ModRegistries.ModMenuTypes.APRICORN_BASKET_MENU.get(), ApricornBasketScreen::new);
        KeyBindingManager.register();
        ModClientCommon.init();
    }
}
