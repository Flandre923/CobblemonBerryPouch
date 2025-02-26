package com.gitlab.srcmc.rctmod.item;

import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchContainer24;
import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchContainer30;
import com.gitlab.srcmc.rctmod.menu.gui.BerryPouchContainer69;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BerryPouch extends Item {

//    public static final int DYE_COUNT = 16;
//    public static final int SIZE = 2 * DYE_COUNT;
    public static final int SMALL_SIZE = 24;
    public static final int MEDIUM_SIZE = 30;
    public static final int LARGE_SIZE = 69;

    public static final BerryPouchGui24StorageSlot POUCH_GUI_24_STORAGE_SLOT = new BerryPouchGui24StorageSlot();
    public static final BerryPouchGui30StorageSlot POUCH_GUI_30_STORAGE_SLOT = new BerryPouchGui30StorageSlot();
    public static final BerryPouchGui69StorageSlot POUCH_GUI_69_STORAGE_SLOT = new BerryPouchGui69StorageSlot();

    public final int size;
    public BerryPouch(int size) {
        super(new Properties().stacksTo(1));
        this.size = size;
    }

    public final int getSize()
    {
        return size;
    }

    public static SimpleContainer getInventory(ItemStack stack, Level level) {
        if (!(stack.getItem() instanceof BerryPouch berryPouch)) return new SimpleContainer(SMALL_SIZE);
        return new ItemBackedInventory(stack, level, berryPouch.getSize());
    }
    // Helper method to check if an ItemStack is a berry
    private static boolean isBerry(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        // 使用 POUCH_GUI_24_STORAGE_SLOT 的 has 方法检查是否是任何类型的莓果
        return POUCH_GUI_24_STORAGE_SLOT.has(item) || POUCH_GUI_30_STORAGE_SLOT.has(item) || POUCH_GUI_69_STORAGE_SLOT.has(item);
    }


    public static boolean onPickupItem(ItemEntity itemEntity,Player player)
    {
        ItemStack itemStack = itemEntity.getItem();
        // a. 首先检查掉落物是否是 berry
        if (!isBerry(itemStack)) {
            return false; // 不是莓果，不进行特殊处理，返回 false 让原版逻辑处理
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
        SimpleContainer inventory = getInventory(container, level);
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

    public static void transferPouchInventory(ItemStack sourcePouchStack, ItemStack targetPouchStack, Level level) {
        if (!(sourcePouchStack.getItem() instanceof BerryPouch sourcePouch) || !(targetPouchStack.getItem() instanceof BerryPouch targetPouch)) {
            return; // 传入的 ItemStack 不是 BerryPouch，直接返回
        }

        if (targetPouch.getSize() <= sourcePouch.getSize()) {
            return; // 目标 BerryPouch 容量不大于等于源 BerryPouch，不进行转移
        }

        SimpleContainer sourceInventory = BerryPouch.getInventory(sourcePouchStack, level);
        SimpleContainer targetInventory = BerryPouch.getInventory(targetPouchStack, level);

        for (int i = 0; i < sourceInventory.getContainerSize(); i++) {
            ItemStack itemToTransfer = sourceInventory.getItem(i);
            if (!itemToTransfer.isEmpty()) {
                ItemStack copyToInsert = itemToTransfer.copy(); // 复制一份，避免直接修改源物品栏
                tryInsertItemStack(targetPouchStack, copyToInsert, level);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (!level.isClientSide) {
            level.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS,0.5f,level.random.nextFloat() * 0.1F + 0.9F);
            ItemStack stack = player.getItemInHand(interactionHand);
            MenuRegistry.openExtendedMenu((ServerPlayer) player,new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return stack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    return getItemMenu(syncId,inv,player,getSize());
                }
            }, buf -> buf.writeBoolean(interactionHand == InteractionHand.MAIN_HAND));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide());
    }


    public static AbstractContainerMenu getItemMenu(int syncId, Inventory inv, Player player, int size) {
        switch (size) {
            case 24:
                return new BerryPouchContainer24(syncId, inv, player.getItemInHand(InteractionHand.MAIN_HAND));
            case 30:
                return new BerryPouchContainer30(syncId, inv, player.getItemInHand(InteractionHand.MAIN_HAND));
            case 69:
                return new BerryPouchContainer69(syncId, inv, player.getItemInHand(InteractionHand.MAIN_HAND));
            default:
                throw new IllegalArgumentException("Invalid berry pouch size: " + size + ". Supported sizes are: 24, 30, 69");
        }
    }
}
