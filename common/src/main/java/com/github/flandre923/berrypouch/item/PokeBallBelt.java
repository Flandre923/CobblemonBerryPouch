package com.github.flandre923.berrypouch.item;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltHelper;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltInventory;
import com.github.flandre923.berrypouch.menu.container.PokeBallBeltContainer;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class PokeBallBelt extends Item {
    public PokeBallBelt(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (player.isShiftKeyDown()) {
                openGui(serverPlayer, stack);
            }else {
                return throwSelectedItem(level, serverPlayer, stack);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }


    private InteractionResultHolder<ItemStack> throwSelectedItem(Level level, ServerPlayer player, ItemStack beltStack) {
        int selectedIndex = PokeBallBeltHelper.getSelectedIndex(beltStack);
        ItemStack itemInSlot = PokeBallBeltHelper.getItemAt(beltStack, selectedIndex);

        if (itemInSlot.isEmpty()) {
            // 槽位为空，什么也不做
            player.sendSystemMessage(Component.translatable("message.berrypouch.belt_slot_empty"), true);
            return InteractionResultHolder.fail(beltStack);
        }
        if (!(itemInSlot.getItem() instanceof PokeBallItem pokeBallItem)) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.not_pokeball"), true);
            return InteractionResultHolder.fail(beltStack);
        }

        PokeBall pokeBall = pokeBallItem.getPokeBall();

        // 创建并投掷精灵球实体
        EmptyPokeBallEntity pokeBallEntity = new EmptyPokeBallEntity(pokeBall, level, player, CobblemonEntities.EMPTY_POKEBALL);
        float overhandFactor = player.getXRot() < 0
                ? 5f * (float) Math.cos(Math.toRadians(player.getXRot()))
                : 5f;


        pokeBallEntity.shootFromRotation(
                player,
                player.getXRot() - overhandFactor,
                player.getYRot(),
                0.0f,
                pokeBall.getThrowPower(),
                1.0f
        );

        // 调整初始位置
        pokeBallEntity.setPos(pokeBallEntity.position().add(
                pokeBallEntity.getDeltaMovement().normalize().scale(1.0)
        ));
        pokeBallEntity.setOwner(player);
        // 添加到世界
        level.addFreshEntity(pokeBallEntity);
        // 消耗腰带中的精灵球
        PokeBallBeltHelper.removeItemAt(beltStack, selectedIndex, 1);

        return InteractionResultHolder.fail(beltStack);
    }

    private void openGui(ServerPlayer player, ItemStack stack) {
        MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.berrypouch.pokeball_belt");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                return new PokeBallBeltContainer(containerId, playerInv, stack);
            }

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
            }
        });
    }

    public static boolean onPickupItem(ItemEntity itemEntity, Player player) {
        // 玩家正在查看精灵球腰带 GUI，不要自动装入
        if (player instanceof ServerPlayer sp && sp.containerMenu instanceof PokeBallBeltContainer) {
            return false;
        }

        ItemStack itemStack = itemEntity.getItem();
        if (!PokeBallBeltContainer.PokeBallSlot.isPokeBall(itemStack)) {
            return false; // 不是精灵球，不进行特殊处理
        }

        if (tryInsertIntoBelt(itemStack, player)) {
            if (itemStack.isEmpty() || itemStack.getCount() == 0) {
                itemEntity.discard();
            }
            return true;
        }
        return false;
    }

    /**
     * 尝试将精灵球放入玩家装备的腰带中
     */
    private static boolean tryInsertIntoBelt(ItemStack stack, Player player) {
        // 查找玩家背包中的精灵球腰带
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() instanceof PokeBallBelt) {
                if (PokeBallBeltInventory.tryInsertToStack(invStack, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

}
