package com.github.flandre923.berrypouch.shared;

import net.minecraft.nbt.CompoundTag;

public final class EnableAutoFillMixinShared {
    private EnableAutoFillMixinShared() {
    }

    public static boolean load(CompoundTag tag, boolean currentState) {
        if (tag.contains("berryPouch_autoFill")) {
            return tag.getBoolean("berryPouch_autoFill");
        }
        return currentState;
    }

    public static void save(CompoundTag tag, boolean currentState) {
        tag.putBoolean("berryPouch_autoFill", currentState);
    }
}
