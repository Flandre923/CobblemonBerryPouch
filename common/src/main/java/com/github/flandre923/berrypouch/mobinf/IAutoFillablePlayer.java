package com.github.flandre923.berrypouch.mobinf;

import net.minecraft.nbt.CompoundTag;

public interface IAutoFillablePlayer {
    boolean berryPouch$getAutoFillBerryuPouch();
    void berryPouch$setAutoFillBerryPouch(boolean autoFill);
    void berryPouch$switchAutoFill();
    void berryPouch$save(CompoundTag tag);
    void berryPouch$load(CompoundTag tag);
}