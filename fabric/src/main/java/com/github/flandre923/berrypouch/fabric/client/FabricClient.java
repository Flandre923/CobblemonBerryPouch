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
package com.github.flandre923.berrypouch.fabric.client;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchGui24;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchGui30;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchGui69;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.MenuScreens;

@Environment(EnvType.CLIENT)
public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_24.get(), BerryPouchGui24::new);
        MenuScreens.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_30.get(), BerryPouchGui30::new);
        MenuScreens.register(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get(), BerryPouchGui69::new);

    }
}
