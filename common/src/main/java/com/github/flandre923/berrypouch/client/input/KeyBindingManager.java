package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.ModCommon;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyBindingManager {

    public static final String KEY_CATEGORY = "key.categories"+ ModCommon.MOD_ID;

    public static final String KEY_OPEN_POUCH = "key."+ModCommon.MOD_ID+".open_pouch";
    public static final String KEY_CYCLE_BAIT = "key."+ModCommon.MOD_ID+".cycle_bait";
    public static final String KEY_CYCLE_BAIT_LEFT = "key." + ModCommon.MOD_ID + ".cycle_bait_left";
    public static final String KEY_CYCLE_BAIT_RIGHT = "key." + ModCommon.MOD_ID + ".cycle_bait_right";

    private static final Map<KeyMapping, KeyAction> KEY_ACTIONS = new HashMap<>();
    public static void register() {
        registerKeyBinding(
                new KeyMapping(KEY_OPEN_POUCH, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, KEY_CATEGORY),
                new OpenPouchAction()
        );

        registerKeyBinding(
                new KeyMapping(KEY_CYCLE_BAIT_LEFT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, KEY_CATEGORY),
                new CycleBaitAction(true)
        );
        registerKeyBinding(
                new KeyMapping(KEY_CYCLE_BAIT_RIGHT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, KEY_CATEGORY),
                new CycleBaitAction(false)
        );
    }
    private static void registerKeyBinding(KeyMapping mapping, KeyAction action) {
        KEY_ACTIONS.put(mapping, action);
        KeyMappingRegistry.register(mapping);
    }

    public static void checkKeyInputs() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        KEY_ACTIONS.forEach((mapping, action) -> {
            while (mapping.consumeClick()) {
                if (action.shouldTrigger(mc.player)) {
                    action.onKeyPressed(mc);
                }
            }
        });
    }
}
