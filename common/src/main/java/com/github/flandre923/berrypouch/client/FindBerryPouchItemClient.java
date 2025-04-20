package com.github.flandre923.berrypouch.client;

import com.github.flandre923.berrypouch.item.BerryPouch;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FindBerryPouchItemClient {

    public static boolean hasPouch(Player player){
        AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
        if (accessoriesCap == null) return false;
        return accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                .stream()
                .anyMatch(p-> !p.stack().isEmpty() && p.stack().getItem() instanceof  BerryPouch);
    }

    public static boolean shouldUseFullModel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if(stack.getItem() instanceof BerryPouch){
            SimpleContainer inventory = BerryPouch.getInventory(stack, Minecraft.getInstance().level);
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
