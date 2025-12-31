package com.github.flandre923.berrypouch.component;

import com.github.flandre923.berrypouch.storage.SlotLimitData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * 槽位限制数据组件
 * 用于在物品的数据组件中存储槽位限制信息
 */
public record SlotLimitComponent(CompoundTag limitData) {
    
    public static final SlotLimitComponent EMPTY = new SlotLimitComponent(new CompoundTag());
    
    // Codec for persistent storage (save/load)
    public static final Codec<SlotLimitComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            CompoundTag.CODEC.fieldOf("limitData").forGetter(SlotLimitComponent::limitData)
        ).apply(instance, SlotLimitComponent::new)
    );
    
    // StreamCodec for network synchronization
    public static final StreamCodec<RegistryFriendlyByteBuf, SlotLimitComponent> STREAM_CODEC = 
        StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.COMPOUND_TAG,
            SlotLimitComponent::limitData,
            SlotLimitComponent::new
        );
    
    /**
     * 从SlotLimitData创建组件
     */
    public static SlotLimitComponent fromSlotLimitData(SlotLimitData slotLimitData) {
        return new SlotLimitComponent(slotLimitData.toNBT());
    }
    
    /**
     * 转换为SlotLimitData
     */
    public SlotLimitData toSlotLimitData() {
        SlotLimitData data = new SlotLimitData();
        data.fromNBT(this.limitData);
        return data;
    }
    
    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return this.limitData.isEmpty();
    }
    
    /**
     * 创建空组件
     */
    public static SlotLimitComponent empty() {
        return EMPTY;
    }
}