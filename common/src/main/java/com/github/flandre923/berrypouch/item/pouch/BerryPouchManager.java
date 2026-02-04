package com.github.flandre923.berrypouch.item.pouch;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.menu.container.LargeBerryPouchContainer;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class BerryPouchManager {


    private static AbstractContainerMenu createMenu(BerryPouchType type, int syncId, Inventory inv, ItemStack pouchStack,int openFlag) {
        switch (type) {
            case LARGE:
                return new LargeBerryPouchContainer(syncId, inv, pouchStack,openFlag);
            default:
                throw new IllegalArgumentException("Unknown pouch type: " + type);
        }
    }

    public static BerryPouchInventory getInventory(ItemStack stack, Level level) {
        BerryPouchType type = getPouchType(stack);
        return new BerryPouchInventory(stack, level, type);
    }

    public static boolean isHoldingPouch(Player player, ItemStack pouchStack) {
        if (player == null || pouchStack.isEmpty()) {
            return false;
        }

        // 检查主手和副手
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        return (ItemStack.matches(pouchStack, mainHand) ||
                ItemStack.matches(pouchStack, offHand)) ;
    }

    private static BerryPouchType getPouchType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("Stack cannot be null or empty");
        }

        if (!(stack.getItem() instanceof BerryPouch berryPouch)) {
            throw new IllegalArgumentException("Item must be a BerryPouch");
        }
        return berryPouch.getPouchType();
    }

    public static void openPouchGUI(ServerPlayer player, ItemStack stack, int flag) {
        if (player.level().isClientSide || stack.isEmpty() || !(stack.getItem() instanceof BerryPouch)) {
            return; // 安全检查
        }
        BerryPouchType type = getPouchType(stack);
        MenuRegistry.openExtendedMenu(player, createMenuProvider(stack, type ,flag), buf -> {
            buf.writeInt(flag); // 将手部信息写入数据包
        });
    }


    public static MenuProvider createMenuProvider(ItemStack stack, BerryPouchType type,int openFlag) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return BerryPouchManager.createMenu(type, syncId, inv, stack, openFlag); // 使用上面实现的createMenu方法
            }
        };
    }

}
