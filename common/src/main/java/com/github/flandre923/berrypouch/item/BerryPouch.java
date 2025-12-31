package com.github.flandre923.berrypouch.item;

import com.github.flandre923.berrypouch.item.pouch.BerryPouchInventory;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
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

    public static boolean isBerry(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        
        // 检查物品是否有 natural、mutation 或 other 标签
        return stack.is(net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("berrypouch", "natural_berries")
        )) || stack.is(net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("berrypouch", "mutation_berries")
        )) || stack.is(net.minecraft.tags.TagKey.create(
            net.minecraft.core.registries.Registries.ITEM,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("berrypouch", "other_baits")
        ));
    }

    public static boolean onPickupItem(ItemEntity itemEntity,Player player) {
        if (player instanceof ServerPlayer sp && sp.containerMenu instanceof AbstractBerryPouchContainer) {
            return false; // 玩家正在查看树果袋 GUI，不要自动装入
        }

        ItemStack itemStack = itemEntity.getItem();
        if (!isBerry(itemStack)) {
            return false; // 不是莓果，不进行特殊处理，返回 false 让原版逻辑处理
        }

        if(onPickupItem(itemEntity.getItem(), player)){
            if(itemEntity.getItem().isEmpty() || itemEntity.getItem().getCount() == 0)
                itemEntity.discard();
            return true;
        }else{
            return false;
        }
    }

    public static boolean onPickupItem(ItemStack itemStack,Player player){
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
                if (tryInsertItemStack(entryRef.stack(), itemStack, player.level(),player)) {
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
                if (tryInsertItemStack(pouchStack, itemStack, player.level(),player)) { // 尝试将掉落的莓果 ItemStack 复制一份插入 BerryPouch
                    // 如果成功插入，移除世界上的掉落物
                    return true; // 阻止原版拾取逻辑，返回 true
                }
            }
        }
        return false; // 没有找到 BerryPouch 或 BerryPouch 没有空间，返回 false 让原版逻辑处理
    }

    public static boolean tryInsertItemStack(ItemStack container, ItemStack itemToInsert, Level level,Player player) {
        if (!(container.getItem() instanceof BerryPouch)) {
            return false;
        }
        SimpleContainer inventory = BerryPouchManager.getInventory(container, level);
        if (!(inventory instanceof BerryPouchInventory pouchInventory)) {
            return false;
        }
        boolean success = false;
        int remaining = itemToInsert.getCount();

        // First try to stack with existing items
        for (int i = 0; i < pouchInventory.getContainerSize() && remaining > 0; i++) {
            ItemStack existingStack = pouchInventory.getItems().get(i); // 获取类型标记
            if (!existingStack.isEmpty() && ItemStack.isSameItem(existingStack, itemToInsert)) {
                pouchInventory.addToSlot(i, remaining);
                remaining = 0;
                success = true;
                break;
            }
        }

        // Then try to put in empty slot
        if (!success && remaining > 0) {
            for (int i = 0; i < pouchInventory.getContainerSize(); i++) {
                if (pouchInventory.getItems().get(i).isEmpty() && pouchInventory.canPlaceItem(i, itemToInsert)) {
                    pouchInventory.getItems().set(i, itemToInsert.copyWithCount(1)); // 设置类型标记
                    pouchInventory.setSlotCount(i, remaining);
                    remaining = 0;
                    success = true;
                    break;
                }
            }
        }

        if (success) {
            itemToInsert.setCount(0);

            if (!level.isClientSide) {
                level.playSound(null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_PICKUP,
                        SoundSource.PLAYERS,
                        0.2F,
                        ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
                );
            }
        }

        return success;
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
