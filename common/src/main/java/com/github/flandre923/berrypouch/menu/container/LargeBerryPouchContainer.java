package com.github.flandre923.berrypouch.menu.container;

import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.helper.PouchItemHelper;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LargeBerryPouchContainer extends  AbstractBerryPouchContainer {
    private final ItemStack pouchStack;

    public LargeBerryPouchContainer(int windowId, Inventory playerInv, ItemStack pouchStack) {
        super(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get(),
                windowId, playerInv, pouchStack, BerryPouchType.LARGE);
        this.pouchStack = pouchStack;
    }
        
    // 辅助方法：判断物品是否是树果
    public static boolean isBerry(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        // 使用cobblemon的berries标签来判断是否是树果
        return stack.is(net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("cobblemon", "berries")
        ));
    }
    
    // 辅助方法：判断物品是否是other_baits
    public static boolean isOtherBaits(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("cobblemon", "other_baits")
        ));
    }
    public static LargeBerryPouchContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
//        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        int isHand = buf.readInt();
        ItemStack item;
        if(isHand  == 0){
            item = inv.player.getItemInHand(InteractionHand.MAIN_HAND);
        }else if(isHand == 1){
            item = inv.player.getItemInHand(InteractionHand.OFF_HAND);
        }else{
            item = PouchItemHelper.findBerryPouch(inv.player);
        }
        return new LargeBerryPouchContainer(windowId, inv,item);
    }

    @Override
    protected void addPouchSlots() {
        // Natural树果：3行10列 (30格)，从(11,16)开始，格子间隔1个像素
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 10; ++col) {
                int slotIndex = col + row * 10;
                // 每个槽位大小17x17(16+1像素间隔)
                addBerrySlot(slotIndex, 12 + col * 18,17 + row * 18);
            }
        }

        // 混合树果：4行10列 (40格)，从(11,88)开始，格子间隔1个像素  
        for (int row = 0; row < 4; ++row) {
            for (int col = 0; col < 10; ++col) {
                int slotIndex = 30 + col + row * 10;
                // 每个槽位大小17x17(16+1像素间隔)
                addBerrySlot(slotIndex, 12 + col * 18,89 + row * 18);
            }
        }
        
        // Other Baits槽位：8行2列 (16格)，从(210,70)开始，每个格子18x18
        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 2; ++col) {
                int slotIndex = 70 + col + row * 2;
                addOtherBaitsSlot(slotIndex, 210 + col * 18, 17 + row * 18);
            }
        }
    }

    private void addBerrySlot(int slotIndex, int x, int y) {
        addSlot(new Slot(pouchInventory, slotIndex, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // 使用辅助方法判断是否是树果，并且检查是否匹配特定槽位
                return isBerry(stack) && BerryPouchType.LARGE.getStorageSlot()
                        .matchesSlotItem(slotIndex + 1, stack.getItem());
            }
        });
    }
    
    private void addOtherBaitsSlot(int slotIndex, int x, int y) {
        addSlot(new Slot(pouchInventory, slotIndex, x, y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // 使用辅助方法判断是否是other_baits
                return isOtherBaits(stack);
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(slotIndex);

        if (clickedSlot != null && clickedSlot.hasItem()) {
            ItemStack originalStack = clickedSlot.getItem().copy();
            returnStack = originalStack.copy();

            if (slotIndex < BerryPouchType.LARGE.getSize()) { // 从袋子移到背包
                if (!this.moveItemStackTo(originalStack, BerryPouchType.LARGE.getSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else { // 从背包移到袋子
                boolean moved = false;
                
                // 先尝试移动到树果槽位 (0-69)
                if (BerryPouchType.LARGE.getStorageSlot().has(originalStack.getItem())) {
                    moved = this.moveItemStackTo(originalStack, 0, 70, false);
                }
                
                // 如果树果槽位移动失败，尝试移动到other_baits槽位 (70-85)
                if (!moved && originalStack.is(net.minecraft.tags.TagKey.create(
                    net.minecraft.core.registries.Registries.ITEM,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("berrypouch", "other_baits")
                ))) {
                    moved = this.moveItemStackTo(originalStack, 70, 86, false);
                }
                
                if (!moved) {
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
