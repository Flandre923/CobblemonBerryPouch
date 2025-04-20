package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.network.ModNetworking;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class OpenPouchAction implements KeyAction {
    @Override
    public void onKeyPressed(Minecraft client) {
        ModNetworking.sendOpenPouchPacketToServer();
    }

    @Override
    public boolean shouldTrigger(Player player) {
        AccessoriesCapability cap = AccessoriesCapability.get(player);
        return cap != null &&
                !cap.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch)
                        .stack().isEmpty();
    }
}