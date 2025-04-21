package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.network.ModNetworking;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OpenPouchAction implements KeyAction {
    @Override
    public void onKeyPressed(Minecraft client) {
        ModNetworking.sendOpenPouchPacketToServer();
    }

    @Override
    public boolean shouldTrigger(Player player) {
        AccessoriesCapability cap = AccessoriesCapability.get(player);
        if (cap == null) {
            return false;
        }
        @Nullable SlotEntryReference equipped = cap.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch);
        return equipped != null && !equipped.stack().isEmpty();
    }
}