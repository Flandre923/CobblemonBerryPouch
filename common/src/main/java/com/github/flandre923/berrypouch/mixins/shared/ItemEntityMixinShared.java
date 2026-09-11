package com.github.flandre923.berrypouch.mixins.shared;

import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.ApricornBasketItem;
import com.github.flandre923.berrypouch.item.PokeBallGun;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class ItemEntityMixinShared {
    private ItemEntityMixinShared() {
    }

    public static boolean shouldCancelPickup(ItemEntity self, Player player, int pickupDelay, UUID thrower) {
        if (!player.level().isClientSide && pickupDelay == 0 && (thrower == null || thrower.equals(player.getUUID()))) {
            if (BerryPouch.onPickupItem(self, player)) {
                return true;
            }
            if (ApricornBasketItem.onPickupItem(self, player)) {
                return true;
            }
            if (PokeBallGun.onPickupItem(self, player)) {
                return true;
            }
        }
        return false;
    }
}
