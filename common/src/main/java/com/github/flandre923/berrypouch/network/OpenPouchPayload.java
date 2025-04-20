package com.github.flandre923.berrypouch.network;

import com.github.flandre923.berrypouch.ModCommon;
import net.minecraft.network.RegistryFriendlyByteBuf; // Use RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Payload sent from client to server when the 'Open Pouch' key is pressed.
 * Contains no data, acts as a signal.
 */
public record OpenPouchPayload() implements CustomPacketPayload {

    // 1. Define a unique ID for this payload type
    public static final CustomPacketPayload.Type<OpenPouchPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, "open_pouch"));

    // 2. Define the StreamCodec for serialization/deserialization.
    //    StreamCodec.unit() is used for payloads with no fields.
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenPouchPayload> CODEC =
            StreamCodec.unit(new OpenPouchPayload());

    // 3. Implement the type() method from CustomPacketPayload
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}