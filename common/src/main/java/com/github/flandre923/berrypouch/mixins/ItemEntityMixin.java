package com.github.flandre923.berrypouch.mixins;

import com.github.flandre923.berrypouch.item.BerryPouch;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Shadow
    private int pickupDelay;

    @Shadow
    private UUID thrower;

    @Inject(at = @At("HEAD"), method = "playerTouch", cancellable = true)
    private void onPickup(Player player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!player.level().isClientSide && pickupDelay == 0 && (thrower == null || thrower.equals(player.getUUID()))
                && BerryPouch.onPickupItem(self, player)) {
            ci.cancel();
        }
    }
}
