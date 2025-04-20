package com.github.flandre923.berrypouch.menu.gui;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.client.FindBerryPouchItemClient;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.menu.SlotLocked;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BerryPouchContainer24 extends AbstractContainerMenu {
    private final ItemStack bag;
    public final Container flowerBagInv;
    public static BerryPouchContainer24 fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new BerryPouchContainer24(windowId, inv, inv.player.getItemInHand(hand));
    }

    public BerryPouchContainer24(int windowId, Inventory playerInv, ItemStack bag) {
        super(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_24.get(), windowId);
        this.bag = bag;

        if (!playerInv.player.level().isClientSide) {
            flowerBagInv = BerryPouch.getInventory(bag, playerInv.player.level());
        } else {
            flowerBagInv = new SimpleContainer(BerryPouch.SMALL_SIZE);
        }
        addBerrySlots(flowerBagInv);
        addInventorySlots(playerInv);
        addHotbarSlots(playerInv, bag);
    }

    private void addBerrySlots(Container flowerBagInv) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 8; ++col) {
                int slot = col + row * 8;
                addSlot(new Slot(flowerBagInv, slot, 17 + col * 18, 10 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(BerryPouch.POUCH_GUI_24_STORAGE_SLOT.getStorageSlots().get(slot+1).getItemOrNull());
                    }
                });
            }
        }
    }

    private void addInventorySlots(Inventory playerInv) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 80 + row * 18));
            }
        }
    }

    private void addHotbarSlots(Inventory playerInv, ItemStack bag) {
        for (int i = 0; i < 9; ++i) {
            if (playerInv.getItem(i) == bag) {
                addSlot(new SlotLocked(playerInv, i, 8 + i * 18, 138));
            } else {
                addSlot(new Slot(playerInv, i, 8 + i * 18, 138));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY; // 初始返回空物品堆
        Slot clickedSlot = this.slots.get(slotIndex); // 获取被点击的槽位

        if (clickedSlot != null && clickedSlot.hasItem()) { // 检查槽位和物品
            ItemStack originalStack = clickedSlot.getItem().copy(); // 复制一份物品堆，用于后续操作
            returnStack = originalStack.copy(); // 记录要返回的物品堆 (如果移动成功，会修改为空物品堆)

            if (slotIndex < BerryPouch.SMALL_SIZE) { // 从花袋移动到玩家背包
                if (!this.moveItemStackTo(originalStack, BerryPouch.SMALL_SIZE, this.slots.size(), true)) { // 从花袋槽位后开始，到所有槽位结束，反向检查
                    return ItemStack.EMPTY; // 移动失败，返回空物品堆
                }
            } else { // 从玩家背包移动到花袋
                if (BerryPouch.POUCH_GUI_24_STORAGE_SLOT.has(originalStack.getItem())) { // 检查是否是允许放入花袋的物品
                    if (!this.moveItemStackTo(originalStack, 0, BerryPouch.SMALL_SIZE, false)) { // 从花袋的第一个槽位开始，到花袋槽位结束，正向检查
                        return ItemStack.EMPTY; // 移动失败，返回空物品堆
                    }
                } else {
                    return ItemStack.EMPTY; // 物品不允许放入花袋，移动失败
                }
            }

            if (originalStack.isEmpty()) { // 如果原始槽位的物品被完全移动了
                clickedSlot.set(ItemStack.EMPTY); // 清空原始槽位
            } else {
                clickedSlot.setChanged(); // 标记原始槽位发生改变 (数量变化)
            }

            if (originalStack.getCount() == returnStack.getCount()) { // 如果移动前后物品数量没有变化，表示移动失败
                return ItemStack.EMPTY; // 返回空物品堆
            }
            clickedSlot.onTake(player, originalStack); // 通知槽位物品被取走
        }
        return returnStack; // 返回结果物品堆 (成功移动返回原始物品堆的副本，失败返回空物品堆)
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return !main.isEmpty() && main == bag || !off.isEmpty() && off == bag || FindBerryPouchItemClient.hasPouch(player);
    }



}
