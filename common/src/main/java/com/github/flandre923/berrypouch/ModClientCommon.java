package com.github.flandre923.berrypouch;

import com.github.flandre923.berrypouch.item.BerryPouch;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class ModClientCommon {


    public static void init(){
        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_30.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> BerryPouch.shouldUseFullModel(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_69.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> BerryPouch.shouldUseFullModel(stack) ? 1.0F : 0.0F);
    }
}
