package com.github.flandre923.berrypouch;

import com.github.flandre923.berrypouch.client.BerryPouchModelHelper;
import com.github.flandre923.berrypouch.client.hud.BaitRenderHandler;
import com.github.flandre923.berrypouch.client.input.KeyBindingManager;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import dev.architectury.event.events.client.ClientTickEvent;

public class ModClientCommon {

    public static void init() {
        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_30.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> BerryPouchModelHelper.shouldUseFullModel(stack) ? 1.0F : 0.0F);

        ItemProperties.register(ModRegistries.Items.BERRY_POUCH_69.get(),
                ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "full"),
                (stack, level, entity, seed) -> BerryPouchModelHelper.shouldUseFullModel(stack) ? 1.0F : 0.0F);

        ClientTickEvent.CLIENT_POST.register(__ -> KeyBindingManager.checkKeyInputs());
        ClientGuiEvent.RENDER_HUD.register(new BaitRenderHandler());
    }
}
