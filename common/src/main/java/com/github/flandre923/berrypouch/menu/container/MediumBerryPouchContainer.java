package com.github.flandre923.berrypouch.menu.container;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.helper.PouchItemHelper;
import com.github.flandre923.berrypouch.menu.slot.SlotLocked;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.Slot;

public class MediumBerryPouchContainer extends AbstractBerryPouchContainer {
    private final ItemStack pouchStack;

    public MediumBerryPouchContainer(int windowId, Inventory playerInv, ItemStack pouchStack) {
        super(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_30.get(),
                windowId, playerInv, pouchStack, BerryPouchType.MEDIUM);
        this.pouchStack = pouchStack;
    }

    public static MediumBerryPouchContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        int isHand = buf.readInt();
        ItemStack item;
        if(isHand  == 0){
            item = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        }else if(isHand == 1){
            item = inv.player.getItemInHand(InteractionHand.OFF_HAND);
        }else{
            item = PouchItemHelper.findBerryPouch(inv.player);
        }
        return new MediumBerryPouchContainer(windowId, inv, item);
    }

    @Override
    protected void addPouchSlots() {
        // 第一部分：3行8列 (24格)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 8; ++col) {
                int slotIndex = col + row * 8;
                addBerrySlot(slotIndex, 11 + col * 18, 16 + row * 18);
            }
        }

        // 第二部分：3行2列 (6格)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 2; ++col) {
                int slotIndex = 24 + row * 2 + col;
                addBerrySlot(slotIndex, 163 + col * 18, 16 + row * 18);
            }
        }
    }

    private void addBerrySlot(int slotIndex, int x, int y) {
        addSlot(new Slot(pouchInventory, slotIndex, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BerryPouchType.MEDIUM.getStorageSlot()
                        .matchesSlotItem(slotIndex + 1, stack.getItem());
            }
        });
    }

    @Override
    protected void addPlayerSlots(Inventory playerInv, ItemStack bag) {
        // 玩家主物品栏 (3行9列)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int index = col + row * 9 + 9;
                addSlot(new Slot(playerInv, index, 25 + col * 18, 96 + row * 18));
            }
        }

        // 玩家快捷栏 (9格)
        for (int col = 0; col < 9; ++col) {
            if (playerInv.getItem(col) == bag) {
                addSlot(new SlotLocked(playerInv, col, 25 + col * 18, 154));
            } else {
                addSlot(new Slot(playerInv, col, 25 + col * 18, 154));
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

            if (slotIndex < BerryPouchType.MEDIUM.getSize()) { // 从袋子移到背包
                if (!this.moveItemStackTo(originalStack, BerryPouchType.MEDIUM.getSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else { // 从背包移到袋子
                if (BerryPouchType.MEDIUM.getStorageSlot().has(originalStack.getItem())) {
                    if (!this.moveItemStackTo(originalStack, 0, BerryPouchType.MEDIUM.getSize(), false)) {
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
