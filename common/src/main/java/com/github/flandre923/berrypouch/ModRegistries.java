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


import com.github.flandre923.berrypouch.component.MarkedSlotsComponent;
import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.PokeBallGun;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.LargeBerryPouchContainer;
import com.github.flandre923.berrypouch.menu.container.PokeBallGunContainer;
import com.github.flandre923.berrypouch.recipe.BerryPouchUpgradeRecipe;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class ModRegistries {
    public class ModMenuTypes {
        public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ModCommon.MOD_ID, Registries.MENU);

//        public static final RegistrySupplier<MenuType<SmallBerryPouchContainer>> BERRY_POUCH_CONTAINER_24 =
//                MENU_TYPES.register("berry_pouch_container_24", () ->
//                        MenuRegistry.ofExtended(SmallBerryPouchContainer::fromNetwork));
//        public static final RegistrySupplier<MenuType<MediumBerryPouchContainer>> BERRY_POUCH_CONTAINER_30 =
//                MENU_TYPES.register("berry_pouch_container_30", () ->
//                        MenuRegistry.ofExtended(MediumBerryPouchContainer::fromNetwork));
        public static final RegistrySupplier<MenuType<LargeBerryPouchContainer>> BERRY_POUCH_CONTAINER_69 =
                MENU_TYPES.register("berry_pouch_container", () ->
                        MenuRegistry.ofExtended(LargeBerryPouchContainer::fromNetwork));
        public static final Supplier<MenuType<PokeBallGunContainer>> POKEBALL_GUN_MENU = MENU_TYPES.register("pokeball_gun_menu",
                () -> MenuRegistry.ofExtended(PokeBallGunContainer::fromNetwork)
        );



    }

    public class Items {
        public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ModCommon.MOD_ID, Registries.ITEM);
        //public static final RegistrySupplier<BerryPouch.java> BERRY_POUCH_24;
//        public static final RegistrySupplier<BerryPouch> BERRY_POUCH_30;
        public static final RegistrySupplier<BerryPouch> BERRY_POUCH_69;
        public static final RegistrySupplier<PokeBallGun> POKEBALL_GUN ;

        static {
            //BERRY_POUCH_24 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch_24"), ()->new BerryPouch.java(24));
//            BERRY_POUCH_30 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch_30"), ()->new BerryPouch(BerryPouchType.MEDIUM));
            BERRY_POUCH_69 = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"berry_pouch"), ()->new BerryPouch(BerryPouchType.LARGE));
            POKEBALL_GUN = REGISTRY.register(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,"pokeball_gun"),()->new PokeBallGun((new Item.Properties().stacksTo(1))));
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
                    .icon(()->new ItemStack(Items.BERRY_POUCH_69.get()))
                    .displayItems((pParameters, pOutput) -> {
//                        pOutput.accept(Items.BERRY_POUCH_24.get());
//                        pOutput.accept(Items.BERRY_POUCH_30.get());
                        pOutput.accept(Items.BERRY_POUCH_69.get());
                        pOutput.accept(Items.POKEBALL_GUN.get());
                    }).build();
        });
    }

    public class ModDataComponentes{
        public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
                DeferredRegister.create( ModCommon.MOD_ID,Registries.DATA_COMPONENT_TYPE);


        // Register the MARKED_SLOTS component
        public static final RegistrySupplier<DataComponentType<List<Integer>>> MARKED_SLOTS =
                DATA_COMPONENTS.register("marked_slots", () ->
                        DataComponentType.<List<Integer>>builder()
                                .persistent(MarkedSlotsComponent.CODEC) // Use the codec for saving
                                .networkSynchronized(MarkedSlotsComponent.STREAM_CODEC) // Use the stream codec for networking
                                // .cacheEncoding() // Optional: Cache encoding if frequently sent/saved unchanged
                                .build()
                );
        public static final RegistrySupplier<DataComponentType<Optional<ResourceLocation>>> LAST_USED_BAIT = DATA_COMPONENTS.register(
                "last_used_bait",
                () -> DataComponentType.<Optional<ResourceLocation>>builder()
                        // ---> 在这里添加 .codec() <---
                        .persistent(ResourceLocation.CODEC.optionalFieldOf("item_rl").codec())
                        .networkSynchronized(ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional))
                        .build()
        );
        
        // Register the SLOT_LIMITS component for dynamic slot limits
        public static final RegistrySupplier<DataComponentType<com.github.flandre923.berrypouch.component.SlotLimitComponent>> SLOT_LIMITS =
                DATA_COMPONENTS.register("slot_limits", () ->
                        DataComponentType.<com.github.flandre923.berrypouch.component.SlotLimitComponent>builder()
                                .persistent(com.github.flandre923.berrypouch.component.SlotLimitComponent.CODEC)
                                .networkSynchronized(com.github.flandre923.berrypouch.component.SlotLimitComponent.STREAM_CODEC)
                                .build()
                );
        public static void register() {
            DATA_COMPONENTS.register();
            ModCommon.LOG.info("Registered Data Components for {}", ModCommon.MOD_ID); // Add logging
        }
    }


    public static void init() {
        if(!ModRegistries.initialized) {
            ModMenuTypes.MENU_TYPES.register();
            Items.REGISTRY.register();
            Tabs.TAB.register();
            Recipes.RECIPE_SERIALIZERS.register();
            ModDataComponentes.register();
            ModRegistries.initialized = true;
            // event
            FishingRodEventHandler.register();
        }
    }
    private static boolean initialized=false;

}
