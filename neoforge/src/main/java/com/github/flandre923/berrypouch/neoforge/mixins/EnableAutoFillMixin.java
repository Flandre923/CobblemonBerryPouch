package com.github.flandre923.berrypouch.neoforge.mixins;

import com.github.flandre923.berrypouch.mobinf.IAutoFillablePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class EnableAutoFillMixin implements IAutoFillablePlayer {

    @Unique
    public boolean berryPouch$autoFillBerryuPouch = true;
    @Unique
    public boolean berryPouch$getAutoFillBerryuPouch() {
        return berryPouch$autoFillBerryuPouch;
    }

    @Unique
    public void berryPouch$setAutoFillBerryPouch(boolean autoFillBerryuPouch) {
        this.berryPouch$autoFillBerryuPouch = autoFillBerryuPouch;
    }

    @Unique
    public void berryPouch$switchAutoFill() {
        this.berryPouch$autoFillBerryuPouch = !this.berryPouch$getAutoFillBerryuPouch();
    }

    @Unique
    public void berryPouch$save(CompoundTag tag){
        tag.putBoolean("berryPouch_autoFill", this.berryPouch$autoFillBerryuPouch);
    }

    @Unique
    public void berryPouch$load(CompoundTag tag) {
        if (tag.contains("berryPouch_autoFill")) {
            this.berryPouch$autoFillBerryuPouch = tag.getBoolean("berryPouch_autoFill");
        }
    }

    // ====  mixin ====
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSave(CompoundTag tag, CallbackInfo ci) {
        ((EnableAutoFillMixin)(Object)this).berryPouch$save(tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        ((EnableAutoFillMixin)(Object)this).berryPouch$load(tag);
    }

}
