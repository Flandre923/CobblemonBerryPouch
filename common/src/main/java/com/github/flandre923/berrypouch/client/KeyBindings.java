package com.github.flandre923.berrypouch.client;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.network.ModNetworking;
import com.mojang.blaze3d.platform.InputConstants;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

import java.util.List;

public class KeyBindings {
    public static final String KEY_CATEGORY = "key.categories"+ModCommon.MOD_ID;
    public static final String KEY_YOUR_ACTION = "key."+ModCommon.MOD_ID+".open_pouch";

    public static final KeyMapping OPEN_POUCH = new KeyMapping(
            KEY_YOUR_ACTION,
            InputConstants.Type.KEYSYM, // 输入类型，通常是 KEYSYM (键盘)
            GLFW.GLFW_KEY_K,
            KEY_CATEGORY
    );

    public static void register() {
        KeyMappingRegistry.register(OPEN_POUCH);
    }

    public static void checkKeyInput() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        while (OPEN_POUCH.consumeClick()) {
            Player player = mc.player;
            AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
            if (accessoriesCap == null) return;
            accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                    .stream()
                    .findFirst()
                    .ifPresent(entry->{
                        ItemStack pouchStack = entry.stack();
                        if (!pouchStack.isEmpty()) {
                            ModNetworking.sendOpenPouchPacketToServer();
                        }
                    });
        }
    }
}