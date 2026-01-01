package com.github.flandre923.berrypouch.client;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BerryPouchModelHelper {

    public static boolean shouldUseFullModel(ItemStack stack) {
        if (stack.isEmpty()) return false;

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null || contents == ItemContainerContents.EMPTY) {
            return false;
        }
        return contents.stream().anyMatch(item -> !item.isEmpty());
    }
}
