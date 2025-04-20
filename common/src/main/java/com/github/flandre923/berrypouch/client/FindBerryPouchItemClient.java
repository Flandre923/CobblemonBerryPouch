package com.github.flandre923.berrypouch.client;

import com.github.flandre923.berrypouch.item.BerryPouch;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.player.Player;

public class FindBerryPouchItemClient {

    public static boolean hasPouch(Player player){
        AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
        if (accessoriesCap == null) return false;
        return accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                .stream()
                .anyMatch(p-> !p.stack().isEmpty() && p.stack().getItem() instanceof  BerryPouch);
    }
}
