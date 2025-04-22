package com.github.flandre923.berrypouch.client;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class BerryPouchModelHelper {

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
