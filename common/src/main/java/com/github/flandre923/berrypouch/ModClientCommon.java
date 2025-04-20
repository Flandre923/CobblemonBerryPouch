package com.github.flandre923.berrypouch;

import com.github.flandre923.berrypouch.client.FindBerryPouchItemClient;
import com.github.flandre923.berrypouch.client.input.KeyBindingManager;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;

public class ModClientCommon {

    public static void init() {
        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_30.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> FindBerryPouchItemClient.shouldUseFullModel(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_69.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> FindBerryPouchItemClient.shouldUseFullModel(stack) ? 1.0F : 0.0F);

        ClientLifecycleEvent.CLIENT_SETUP.register(__ -> KeyBindingManager.register());
        ClientTickEvent.CLIENT_POST.register(__ -> KeyBindingManager.checkKeyInputs());
    }
}
