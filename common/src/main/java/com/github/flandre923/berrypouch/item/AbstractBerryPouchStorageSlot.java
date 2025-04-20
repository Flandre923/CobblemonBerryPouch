package com.github.flandre923.berrypouch.item;
import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractBerryPouchStorageSlot implements IBerryPouchStorage{
    public final int SIZE;
    protected final ArrayList<StorageSlot> storageSlots;
    public AbstractBerryPouchStorageSlot(int size) {
        this.SIZE = size;
        this.storageSlots = new ArrayList<>();
        initializeSlots();
    }

    // Abstract method to be implemented by subclasses to get the Item for a specific slot
    protected abstract Item getSlotItemInternal(int slotIndex);

    @Override
    public Item getSlotItem(int slotIndex) {
        return getSlotItemInternal(slotIndex);
    }

    // Initialize the storageSlots list based on SIZE
    private void initializeSlots() {
        for (int i = 1; i <= SIZE; i++) {
            // Create a Supplier<Item> that calls getSlotItemInternal(i)
            int finalI = i;
            Supplier<Item> itemSupplier = () -> getSlotItemInternal(finalI);
            storageSlots.add(new StorageSlot(itemSupplier));
        }
    }

    @Override
    public List<StorageSlot> getStorageSlots() {
        return storageSlots;
    }

    @Override
    public boolean matchesSlotItem(int slotIndex, Item item) {
        if (slotIndex < 1 || slotIndex > SIZE) {
            return false;
        }
        StorageSlot slot = storageSlots.get(slotIndex - 1);
        return slot.isItem(item);
    }

    @Override
    public boolean has(Item item) {
        for (StorageSlot slot : storageSlots) {
            if (slot.isItem(item)) {
                return true;
            }
        }
        return false;
    }
}
