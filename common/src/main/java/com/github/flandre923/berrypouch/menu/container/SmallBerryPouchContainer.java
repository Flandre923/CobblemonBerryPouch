package com.github.flandre923.berrypouch.menu.container;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.helper.PouchItemHelper;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SmallBerryPouchContainer extends AbstractBerryPouchContainer{
    private final ItemStack pouchStack;

    public SmallBerryPouchContainer(int windowId, Inventory playerInv, ItemStack pouchStack) {
        super(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_24.get(),
                windowId, playerInv, pouchStack, BerryPouchType.SMALL);
        this.pouchStack = pouchStack;
    }

    public static SmallBerryPouchContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        int isHand = buf.readInt();
        ItemStack item;
        if(isHand  == 0){
            item = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        }else if(isHand == 1){
            item = inv.player.getItemInHand(InteractionHand.OFF_HAND);
        }else{
            item = PouchItemHelper.findBerryPouch(inv.player);
        }
        return new SmallBerryPouchContainer(windowId, inv, item);
    }


    @Override
    protected void addPouchSlots() {
        // 24格布局 (3行8列)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 8; ++col) {
                int slotIndex = col + row * 8;
                int xPos = 17 + col * 18; // 水平间距18像素
                int yPos = 10 + row * 18; // 垂直间距18像素

                addSlot(new Slot(pouchInventory, slotIndex, xPos, yPos) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return BerryPouchType.SMALL.getStorageSlot()
                                .matchesSlotItem(slotIndex + 1, stack.getItem());
                    }
                });
            }
        }
    }


    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slotIndex);

        if (clickedSlot != null && clickedSlot.hasItem()) {
            ItemStack originalStack = clickedSlot.getItem().copy();
            returnStack = originalStack.copy();

            if (slotIndex < BerryPouchType.SMALL.getSize()) { // 从袋子移到背包
                if (!this.moveItemStackTo(originalStack, BerryPouchType.SMALL.getSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else { // 从背包移到袋子
                if (BerryPouchType.SMALL.getStorageSlot().has(originalStack.getItem())) {
                    if (!this.moveItemStackTo(originalStack, 0, BerryPouchType.SMALL.getSize(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                clickedSlot.set(ItemStack.EMPTY);
            } else {
                clickedSlot.set(originalStack);
                clickedSlot.setChanged();
            }

            if (originalStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            clickedSlot.onTake(player, originalStack);
        }
        return returnStack;
    }
}
