package com.github.flandre923.berrypouch.client.input;

import com.github.flandre923.berrypouch.network.ToggleAutoBerryPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ToggleAutoBerryAction implements KeyAction {
    @Override
    public boolean shouldTrigger(Player player) {
        return true;
    }

    @Override
    public void onKeyPressed(Minecraft mc) {
        NetworkManager.sendToServer(new ToggleAutoBerryPayload());
    }
}