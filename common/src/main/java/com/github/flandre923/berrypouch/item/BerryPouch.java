package com.github.flandre923.berrypouch.item;

import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BerryPouch extends AccessoryItem {
    private final BerryPouchType pouchType;

    public BerryPouch(BerryPouchType pouchType) {
        super(new Properties().stacksTo(1));
        this.pouchType = pouchType;
    }

    public int getSize() {
        return pouchType.getSize();
    }

    public BerryPouchType getPouchType(){
        return this.pouchType;
    }

    private static boolean isBerry(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return BerryPouchType.LARGE.getStorageSlot().has(item);
    }


    public static boolean onPickupItem(ItemEntity itemEntity,Player player)
    {
        ItemStack itemStack = itemEntity.getItem();
        // a. 首先检查掉落物是否是 berry
        if (!isBerry(itemStack)) {
            return false; // 不是莓果，不进行特殊处理，返回 false 让原版逻辑处理
        }

        // 优先检查装备的 pouch 槽
        AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
        if (accessoriesCap != null) {
            List<SlotEntryReference> equippedPouches = accessoriesCap.getEquipped(
                    stack -> stack.getItem() instanceof BerryPouch
            );
            for (SlotEntryReference entryRef : equippedPouches) {
                if (tryInsertItemStack(entryRef.stack(), itemStack.copy(), player.level())) {
                    itemEntity.discard();
                    return true;
                }
            }
        }

        // b. 遍历玩家背包寻找 BerryPouch
        Inventory playerInventory = player.getInventory();
        for (int i = 0; i < playerInventory.getContainerSize(); ++i) {
            ItemStack pouchStack = playerInventory.getItem(i);
            if (pouchStack.getItem() instanceof BerryPouch berryPouch) {
                // d. 找到 BerryPouch 后，检查对应槽位是否有空间
                if (tryInsertItemStack(pouchStack, itemStack.copy(), player.level())) { // 尝试将掉落的莓果 ItemStack 复制一份插入 BerryPouch
                    // 如果成功插入，移除世界上的掉落物
                    itemEntity.discard();
                    return true; // 阻止原版拾取逻辑，返回 true
                }
            }
        }
        return false; // 没有找到 BerryPouch 或 BerryPouch 没有空间，返回 false 让原版逻辑处理
    }


    public static boolean tryInsertItemStack(ItemStack container, ItemStack itemToInsert, Level level) {
        if (!(container.getItem() instanceof BerryPouch)) {
            return false;
        }
        SimpleContainer inventory = BerryPouchManager.getInventory(container, level);
        // First try to stack with existing items
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack existingStack = inventory.getItem(i);
            if (!existingStack.isEmpty() && ItemStack.isSameItem(existingStack, itemToInsert)) {
                int space = existingStack.getMaxStackSize() - existingStack.getCount();
                if (space > 0) {
                    int toTransfer = Math.min(space, itemToInsert.getCount());
                    existingStack.grow(toTransfer);
                    itemToInsert.shrink(toTransfer);
                    if (itemToInsert.isEmpty()) {
                        inventory.setChanged(); // Add this line to save changes after stacking
                        return true;
                    }
                    inventory.setChanged();
                }
            }
        }

        // Then try to put in empty slot
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                if (inventory.canPlaceItem(i, itemToInsert)) { // Check if item is allowed in this slot
                    inventory.setItem(i, itemToInsert.copy());
                    itemToInsert.setCount(0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && stack.getItem() instanceof BerryPouch pouchItem) {
            level.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS,0.5f,level.random.nextFloat() * 0.1F + 0.9F);
            BerryPouchManager.openPouchGUI(serverPlayer, stack, interactionHand==InteractionHand.MAIN_HAND?0:1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }



    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        return reference.slotName().equals("pouch");
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference ref) {
    }
}
