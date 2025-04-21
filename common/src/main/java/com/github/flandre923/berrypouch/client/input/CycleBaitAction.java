package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.network.CycleBaitPacket;
import dev.architectury.networking.NetworkManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class CycleBaitAction implements KeyAction{
    private static final Component NEED_POUCH_MESSAGE =
            Component.translatable("message.berrypouch.need_pouch");

    private final boolean isLeftCycle;

    public CycleBaitAction(boolean isLeftCycle) {
        this.isLeftCycle = isLeftCycle;
    }

    @Override
    public void onKeyPressed(Minecraft client) {
        NetworkManager.sendToServer(
                new CycleBaitPacket(isMainHandRod(client.player), isLeftCycle)
        );
    }

    @Override
    public boolean shouldTrigger(Player player) {

        // 检查是否持有钓竿
        if (!isHoldingFishingRod(player)) {
            return false;
        }

        // 检查是否装备了Pouch
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        boolean hasPouch = capability != null &&
                !capability.getFirstEquipped(stack ->
                        stack.getItem() instanceof BerryPouch
                ).stack().isEmpty();

        if (!hasPouch) {
            sendFeedback(player, NEED_POUCH_MESSAGE);
        }

        return hasPouch;
    }

    private boolean isHoldingFishingRod(Player player) {
        return FishingRodEventHandler.isCobblemonFishingRod(player.getMainHandItem()) ||
                FishingRodEventHandler.isCobblemonFishingRod(player.getOffhandItem());
    }

    private boolean isMainHandRod(Player player) {
        return FishingRodEventHandler.isCobblemonFishingRod(player.getMainHandItem());
    }
}
