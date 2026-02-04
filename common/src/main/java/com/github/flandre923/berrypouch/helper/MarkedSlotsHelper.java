package com.github.flandre923.berrypouch.helper;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.component.MarkedSlotsComponent;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarkedSlotsHelper {

    // Get the list of marked slots, returning an empty list if the component is absent.
    public static List<Integer> getMarkedSlots(ItemStack stack) {
        // Use getOrDefault on the component holder itself
        return stack.getOrDefault(ModRegistries.ModDataComponentes.MARKED_SLOTS.get(), MarkedSlotsComponent.EMPTY);
    }

    // Set the list of marked slots.
    public static void setMarkedSlots(ItemStack stack, List<Integer> markedSlots) {
        // Ensure we don't set null, use the registered component type
        stack.set(ModRegistries.ModDataComponentes.MARKED_SLOTS.get(), Objects.requireNonNullElse(markedSlots, MarkedSlotsComponent.EMPTY));
    }

    // Toggle the marked state of a slot index.
    public static void toggleMarkedSlot(ItemStack stack, int slotIndex) {
        List<Integer> currentMarked = getMarkedSlots(stack);
        List<Integer> newMarked = new ArrayList<>(currentMarked); // Work with a mutable copy
        if (newMarked.contains(slotIndex)) {
            newMarked.remove(Integer.valueOf(slotIndex));
        } else {
            newMarked.add(slotIndex);
        }
        setMarkedSlots(stack, newMarked); // Set the modified list back
    }

    // Check if a specific slot index is marked.
    public static boolean isSlotMarked(ItemStack stack, int slotIndex) {
        return getMarkedSlots(stack).contains(slotIndex);
    }
}