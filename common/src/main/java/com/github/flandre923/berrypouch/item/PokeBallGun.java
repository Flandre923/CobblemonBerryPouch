package com.github.flandre923.berrypouch.item;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.github.flandre923.berrypouch.item.pouch.PokeBallGunHelper;
import com.github.flandre923.berrypouch.item.pouch.PokeBallGunInventory;
import com.github.flandre923.berrypouch.menu.container.PokeBallGunContainer;
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


public class PokeBallGun extends Item {
    public PokeBallGun(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                openGui(serverPlayer, stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        // 服务端执行投掷逻辑
        if (player instanceof ServerPlayer serverPlayer) {
            return throwSelectedItem(level, serverPlayer, stack);
        }

//        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        return InteractionResultHolder.success(stack);
    }


    private InteractionResultHolder<ItemStack> throwSelectedItem(Level level, ServerPlayer player, ItemStack gunStack) {
        int selectedIndex = PokeBallGunHelper.getSelectedIndex(gunStack);
        ItemStack itemInSlot = PokeBallGunHelper.getItemAt(gunStack, selectedIndex);

        if (itemInSlot.isEmpty()) {
            // 槽位为空，什么也不做
            player.sendSystemMessage(Component.translatable("message.berrypouch.gun_slot_empty"), true);
            return InteractionResultHolder.fail(gunStack);
        }
        if (!(itemInSlot.getItem() instanceof PokeBallItem pokeBallItem)) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.not_pokeball"), true);
            return InteractionResultHolder.fail(gunStack);
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

        // 计算枪口位置：从玩家眼睛位置向前延伸
        double eyeHeight = player.getEyeHeight();
        double gunPosX = player.getX() ;
        double gunPosY = player.getY() + eyeHeight - 0.2;
        double gunPosZ = player.getZ() ;

        pokeBallEntity.setPos(gunPosX, gunPosY, gunPosZ);
        pokeBallEntity.setOwner(player);
        // 添加到世界
        level.addFreshEntity(pokeBallEntity);
        // 消耗发射器中的精灵球
        PokeBallGunHelper.removeItemAt(gunStack, selectedIndex, 1);

        return InteractionResultHolder.success(gunStack);
    }

    private void openGui(ServerPlayer player, ItemStack stack) {
        MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.berrypouch.pokeball_gun");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                return new PokeBallGunContainer(containerId, playerInv, stack);
            }

            @Override
            public void saveExtraData(FriendlyByteBuf buf) {
            }
        });
    }

    public static boolean onPickupItem(ItemEntity itemEntity, Player player) {
        // 玩家正在查看精灵球发射器 GUI，不要自动装入
        if (player instanceof ServerPlayer sp && sp.containerMenu instanceof PokeBallGunContainer) {
            return false;
        }

        ItemStack itemStack = itemEntity.getItem();
        if (!PokeBallGunContainer.PokeBallSlot.isPokeBall(itemStack)) {
            return false; // 不是精灵球，不进行特殊处理
        }

        if (tryInsertIntoGun(itemStack, player)) {
            if (itemStack.isEmpty() || itemStack.getCount() == 0) {
                itemEntity.discard();
            }
            return true;
        }
        return false;
    }

    /**
     * 尝试将精灵球放入玩家装备的发射器中
     */
    private static boolean tryInsertIntoGun(ItemStack stack, Player player) {
        // 查找玩家背包中的精灵球发射器
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() instanceof PokeBallGun) {
                if (PokeBallGunInventory.tryInsertToStack(invStack, stack)) {
                    return true;
                }
            }
        }
        return false;
    }

}
