package com.github.flandre923.berrypouch.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import java.util.List;
import java.util.ArrayList;

public class MarkedSlotsComponent {

    public static final List<Integer> EMPTY = List.of();

    public static final Codec<List<Integer>> CODEC = Codec.INT.listOf();

    public static final StreamCodec<ByteBuf, List<Integer>> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list(69));

    public static List<Integer> getOrDefault(List<Integer> list) {
        return list == null ? EMPTY : list;
    }
}