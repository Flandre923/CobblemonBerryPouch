package com.github.flandre923.berrypouch.network;

import com.github.flandre923.berrypouch.ModCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record ToggleMarkSlotPayload(int slotIndex) implements CustomPacketPayload {
    public static final Type<ToggleMarkSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "toggle_mark_slot"));

    public static final StreamCodec<FriendlyByteBuf, ToggleMarkSlotPayload> CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.slotIndex()),
                    buf -> new ToggleMarkSlotPayload(buf.readInt())
            );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}