/*
 * This file is part of Radical Cobblemon Trainers.
 * Copyright (c) 2025, HDainester, All rights reserved.
 *
 * Radical Cobblemon Trainers is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Radical Cobblemon Trainers is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Radical Cobblemon Trainers. If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package com.github.flandre923.berrypouch.neoforge.client;

import com.github.flandre923.berrypouch.ModClientCommon;
import com.github.flandre923.berrypouch.ModCommon;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.client.input.KeyBindingManager;
import com.github.flandre923.berrypouch.menu.screen.LargeBerryPouchScreen;
import com.github.flandre923.berrypouch.menu.screen.MediumBerryPouchScreen;
import com.github.flandre923.berrypouch.menu.screen.SmallBerryPouchScreen;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.security.Key;

@EventBusSubscriber(modid = ModCommon.MOD_ID,bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClient {
    @SubscribeEvent
    public static  void registerScreen(RegisterMenuScreensEvent event)
    {
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_24.get(), SmallBerryPouchScreen::new);
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_30.get(), MediumBerryPouchScreen::new);
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get(), LargeBerryPouchScreen::new);
    }


    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModClientCommon.init();
    }

    @SubscribeEvent
    public static void onKeyMappingRegister(RegisterKeyMappingsEvent event){
        KeyBindingManager.register();
    }

}
