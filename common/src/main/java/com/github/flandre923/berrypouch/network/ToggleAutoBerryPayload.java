// 新增网络包定义
package com.github.flandre923.berrypouch.network;

import com.github.flandre923.berrypouch.ModCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToggleAutoBerryPayload() implements CustomPacketPayload {
    public static final Type<ToggleAutoBerryPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "toggle_auto_berry"));

    public static final StreamCodec<FriendlyByteBuf, ToggleAutoBerryPayload> CODEC =
            StreamCodec.unit(new ToggleAutoBerryPayload());
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}