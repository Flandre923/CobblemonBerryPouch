package com.github.flandre923.berrypouch.item;

public class BerrySlotLayout {
    private final int slotIndex; // The index in the container inventory
    private final int x;         // X position of the slot on the GUI
    private final int y;         // Y position of the slot on the GUI
    private final int storageSlotIndex; // Index in the IBerryPouchStorage list

    public BerrySlotLayout(int slotIndex, int x, int y, int storageSlotIndex) {
        this.slotIndex = slotIndex;
        this.x = x;
        this.y = y;
        this.storageSlotIndex = storageSlotIndex;
    }

    // Getters
    public int getSlotIndex() { return slotIndex; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getStorageSlotIndex() { return storageSlotIndex; }
}