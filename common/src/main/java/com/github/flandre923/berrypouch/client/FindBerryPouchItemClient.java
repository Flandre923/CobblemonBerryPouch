package com.github.flandre923.berrypouch.client;

import com.cobblemon.mod.common.api.item.Berry;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FindBerryPouchItemClient {

    public static ItemStack findBerryPouch(Player player){
        AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
        if (accessoriesCap == null) return ItemStack.EMPTY;
        return accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                .stream()
                .findFirst().get().stack();
    }

    public static boolean shouldUseFullModel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if(stack.getItem() instanceof BerryPouch){
            SimpleContainer inventory = BerryPouchManager.getInventory(stack, Minecraft.getInstance().level);
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
