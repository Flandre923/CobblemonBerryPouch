package com.github.flandre923.berrypouch.client;

import com.cobblemon.mod.common.api.fishing.PokeRod;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.network.CycleBaitPacket;
import com.github.flandre923.berrypouch.network.ModNetworking;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

import java.util.List;

public class KeyBindings {
    public static final String KEY_CATEGORY = "key.categories"+ModCommon.MOD_ID;

    public static final String KEY_YOUR_ACTION = "key."+ModCommon.MOD_ID+".open_pouch";
    public static KeyMapping OPEN_POUCH;

    public static final String KEY_CYCLE_BAIT = "key."+ModCommon.MOD_ID+".cycle_bait";
    public static KeyMapping cycleBaitKey;

    public static void register() {
        OPEN_POUCH = new KeyMapping(
                KEY_YOUR_ACTION,
                InputConstants.Type.KEYSYM, // 输入类型，通常是 KEYSYM (键盘)
                GLFW.GLFW_KEY_K,
                KEY_CATEGORY
        );

        cycleBaitKey = new KeyMapping(
                KEY_CYCLE_BAIT,
                GLFW.GLFW_KEY_O, // 选择一个默认键位
                KEY_CATEGORY
        );

        KeyMappingRegistry.register(OPEN_POUCH);
        KeyMappingRegistry.register(cycleBaitKey);
        registerClientTickHandler();
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

    private static void registerClientTickHandler() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            KeyBindings.checkKeyInput();
        });


        ClientTickEvent.CLIENT_POST.register(client -> {
            while (cycleBaitKey.consumeClick()) {
                if (client.player != null && client.level != null) {
                    ItemStack heldStack = client.player.getMainHandItem(); // 或者检查副手
                    if (FishingRodEventHandler.isCobblemonFishingRod(heldStack)) { // 使用之前的检查方法
                        // 检查是否装备了 Pouch (可以在客户端做初步检查，但服务器必须验证)
                        AccessoriesCapability capability = AccessoriesCapability.get(client.player);
                        if (capability != null && !capability.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch).stack().isEmpty()) {
                            // 发送切换请求到服务器
                            // 可以发送手的信息，如果支持双持钓竿
                            NetworkManager.sendToServer(new CycleBaitPacket(client.player.getMainHandItem().getItem() instanceof PokerodItem)); // true表示主手
                        } else {
                            // 可选：提示玩家需要装备 Pouch
                            client.player.displayClientMessage(
                                    Component.translatable("message.berrypouch.need_pouch"),
                                    true
                            );
                        }
                    }
                }
            }
        });
    }
}