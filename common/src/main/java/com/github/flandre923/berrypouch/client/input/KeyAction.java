package com.github.flandre923.berrypouch.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface  KeyAction {
    void onKeyPressed(Minecraft client);
    boolean shouldTrigger(Player player);
    default void sendFeedback(Player player, Component message) {
        player.displayClientMessage(message, true);
    }
}
