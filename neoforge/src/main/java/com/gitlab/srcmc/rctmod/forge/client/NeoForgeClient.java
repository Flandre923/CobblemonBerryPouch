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
package com.gitlab.srcmc.rctmod.forge.client;

import com.gitlab.srcmc.rctmod.ModCommon;

import com.gitlab.srcmc.rctmod.ModRegistries;
import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchGui24;
import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchGui30;
import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchGui69;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ModCommon.MOD_ID,bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeoForgeClient {
    @SubscribeEvent
    public static  void registerScreen(RegisterMenuScreensEvent event)
    {
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_24.get(), BerryPouchGui24::new);
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_30.get(), BerryPouchGui30::new);
        event.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get(), BerryPouchGui69::new);
    }

}
