package com.github.flandre923.berrypouch.helper;

import com.github.flandre923.berrypouch.item.BerryPouch;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PouchItemHelper {
    public static ItemStack findBerryPouch(Player player){
        AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
        if (accessoriesCap == null) return ItemStack.EMPTY;
        return accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                .stream()
                .findFirst().get().stack();
    }

}
