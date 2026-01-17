package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.PokeBallGun;
import com.github.flandre923.berrypouch.network.CycleBaitPacket;
import dev.architectury.networking.NetworkManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CycleBaitAction implements KeyAction{
    private static final Component NEED_POUCH_MESSAGE =
            Component.translatable("message.berrypouch.need_pouch");
    private static final Component NOT_HOLDING_VALID_ITEM =
            Component.translatable("message.berrypouch.not_holding_valid_item");

    private final boolean isLeftCycle;

    public CycleBaitAction(boolean isLeftCycle) {
        this.isLeftCycle = isLeftCycle;
    }

    @Override
    public void onKeyPressed(Minecraft client) {
        NetworkManager.sendToServer(
                new CycleBaitPacket(isMainHandValid(client.player), isLeftCycle)
        );
    }

    @Override
    public boolean shouldTrigger(Player player) {

        // 检查是否持有精灵球发射器
        if (isHoldingPokeBallGun(player)) {
            return true;
        }
        // 检查是否持有钓竿
        if (isHoldingFishingRod(player)) {
            // 钓竿需要检查是否装备了Pouch
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            if (capability == null) {
                sendFeedback(player, NEED_POUCH_MESSAGE);
                return false;
            }
            var entry = capability.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch);
            if (entry == null || entry.stack().isEmpty()) {
                sendFeedback(player, NEED_POUCH_MESSAGE);
                return false;
            }
            return true;
        }
        // 都不是有效物品
        return false;
    }

    private boolean isHoldingFishingRod(Player player) {
        return FishingRodEventHandler.isCobblemonFishingRod(player.getMainHandItem()) ||
                FishingRodEventHandler.isCobblemonFishingRod(player.getOffhandItem());
    }
    private boolean isHoldingPokeBallGun(Player player) {
        return player.getMainHandItem().getItem() instanceof PokeBallGun ||
                player.getOffhandItem().getItem() instanceof PokeBallGun;
    }

    private boolean isMainHandValid(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        // 主手是精灵球发射器或钓竿
        return mainHand.getItem() instanceof PokeBallGun ||
                FishingRodEventHandler.isCobblemonFishingRod(mainHand);
    }
}
