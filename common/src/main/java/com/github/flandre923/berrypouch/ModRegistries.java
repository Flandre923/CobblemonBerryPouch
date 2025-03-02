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
package com.github.flandre923.berrypouch;


import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchContainer24;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchContainer30;
import com.github.flandre923.berrypouch.menu.gui.BerryPouchContainer69;
import com.github.flandre923.berrypouch.recipe.BerryPouchUpgradeRecipe;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public final class ModRegistries {
    public class ModMenuTypes {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ModCommon.MOD_ID, Registries.MENU);

        public static final RegistrySupplier<MenuType<BerryPouchContainer24>> BERRY_POUCH_CONTAINER_24 =
                MENU_TYPES.register("berry_pouch_container_24", () ->
                        MenuRegistry.ofExtended(BerryPouchContainer24::fromNetwork));
        public static final RegistrySupplier<MenuType<BerryPouchContainer30>> BERRY_POUCH_CONTAINER_30 =
                MENU_TYPES.register("berry_pouch_container_30", () ->
                        MenuRegistry.ofExtended(BerryPouchContainer30::fromNetwork));
        public static final RegistrySupplier<MenuType<BerryPouchContainer69>> BERRY_POUCH_CONTAINER_69 =
                MENU_TYPES.register("berry_pouch_container_69", () ->
                        MenuRegistry.ofExtended(BerryPouchContainer69::fromNetwork));
    }

    public class Items {
        public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ModCommon.MOD_ID, Registries.ITEM);
        //public static final RegistrySupplier<BerryPouch> BERRY_POUCH_24;
        public static final RegistrySupplier<BerryPouch> BERRY_POUCH_30;
        public static final RegistrySupplier<BerryPouch> BERRY_POUCH_69;

        static {
            //BERRY_POUCH_24 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch_24"), ()->new BerryPouch(24));
            BERRY_POUCH_30 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch_30"), ()->new BerryPouch(30));
            BERRY_POUCH_69 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch_69"), ()->new BerryPouch(69));
        }
    }

    public class Recipes{
        private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create( ModCommon.MOD_ID,Registries.RECIPE_SERIALIZER);
        public static final Supplier<RecipeSerializer<?>> BACKPACK_UPGRADE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("berry_pouch_upgrade", BerryPouchUpgradeRecipe.Serializer::new);

    }

    public class Tabs{
        public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(ModCommon.MOD_ID, Registries.CREATIVE_MODE_TAB);
        public static final RegistrySupplier<CreativeModeTab> TAB_BERRY_POUCH = TAB.register("berry_pouch_tab",()->{
            return  CreativeModeTab.builder(CreativeModeTab.Row.TOP,10)
                    .title(Component.translatable(ModCommon.MOD_ID + ".berry_pouch.tab"))
                    .icon(()->new ItemStack(Items.BERRY_POUCH_30.get()))
                    .displayItems((pParameters, pOutput) -> {
//                        pOutput.accept(Items.BERRY_POUCH_24.get());
                        pOutput.accept(Items.BERRY_POUCH_30.get());
                        pOutput.accept(Items.BERRY_POUCH_69.get());
                    }).build();
        });


    }

    public static void init() {
        if(!ModRegistries.initialized) {
            ModMenuTypes.MENU_TYPES.register();
            Items.REGISTRY.register();
            Tabs.TAB.register();
            Recipes.RECIPE_SERIALIZERS.register();
            ModRegistries.initialized = true;
        }
    }
    private static boolean initialized=false;

}
