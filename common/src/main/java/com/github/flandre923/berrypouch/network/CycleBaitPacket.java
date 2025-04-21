package com.github.flandre923.berrypouch.network;

import com.github.flandre923.berrypouch.ModCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleBaitPacket(boolean isMainHand, boolean isLeftCycle) implements CustomPacketPayload {
    public static final Type<CycleBaitPacket> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "cycle_bait"));

    public static final StreamCodec<FriendlyByteBuf, CycleBaitPacket> CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeBoolean(packet.isMainHand());
                        buf.writeBoolean(packet.isLeftCycle());
                    },
                    buf -> new CycleBaitPacket(buf.readBoolean(), buf.readBoolean())
            );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}