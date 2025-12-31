package com.github.flandre923.berrypouch.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态槽位限制数据结构
 * 存储每个槽位中每种物品的堆叠限制
 */
public class SlotLimitData {
    // 槽位索引 -> (物品 -> 限制数量)
    private final Map<Integer, Map<Item, Integer>> slotLimits = new HashMap<>();
    // 槽位索引 -> 通用限制数量（适用于所有物品）
    private final Map<Integer, Integer> universalSlotLimits = new HashMap<>();
    
    /**
     * 设置特定槽位中特定物品的堆叠限制
     */
    public void setSlotLimit(int slotIndex, Item item, int limit) {
        slotLimits.computeIfAbsent(slotIndex, k -> new HashMap<>()).put(item, limit);
    }
    
    /**
     * 设置特定槽位的通用堆叠限制（适用于所有物品）
     */
    public void setUniversalSlotLimit(int slotIndex, int limit) {
        universalSlotLimits.put(slotIndex, limit);
    }
    
    /**
     * 获取特定槽位中特定物品的堆叠限制
     * 优先级：特定物品限制 > 通用槽位限制 > 物品默认限制
     */
    public int getSlotLimit(int slotIndex, Item item) {
        // 1. 检查是否有特定物品的限制
        Map<Item, Integer> slotMap = slotLimits.get(slotIndex);
        if (slotMap != null && slotMap.containsKey(item)) {
            int limit = slotMap.get(item);
            System.out.println("Found specific limit for slot " + slotIndex + ", item " + item + ": " + limit);
            return limit;
        }
        
        // 2. 检查是否有通用槽位限制
        if (universalSlotLimits.containsKey(slotIndex)) {
            int limit = universalSlotLimits.get(slotIndex);
            System.out.println("Found universal limit for slot " + slotIndex + ", item " + item + ": " + limit);
            return limit;
        }
        
        // 3. 返回物品默认堆叠限制
        int defaultLimit = item.getDefaultMaxStackSize();
        System.out.println("Using default limit for slot " + slotIndex + ", item " + item + ": " + defaultLimit);
        return defaultLimit;
    }
    
    /**
     * 获取特定槽位中特定物品的堆叠限制
     * 如果没有设置，返回物品的默认最大堆叠数
     */
    public int getSlotLimit(int slotIndex, ItemStack stack) {
        return getSlotLimit(slotIndex, stack.getItem());
    }
    
    /**
     * 移除特定槽位的限制
     */
    public void removeSlotLimit(int slotIndex) {
        slotLimits.remove(slotIndex);
        universalSlotLimits.remove(slotIndex);
    }
    
    /**
     * 移除特定槽位中特定物品的限制
     */
    public void removeSlotLimit(int slotIndex, Item item) {
        Map<Item, Integer> slotMap = slotLimits.get(slotIndex);
        if (slotMap != null) {
            slotMap.remove(item);
            if (slotMap.isEmpty()) {
                slotLimits.remove(slotIndex);
            }
        }
    }
    
    /**
     * 检查特定槽位是否有自定义限制
     */
    public boolean hasSlotLimit(int slotIndex) {
        return (slotLimits.containsKey(slotIndex) && !slotLimits.get(slotIndex).isEmpty()) ||
               universalSlotLimits.containsKey(slotIndex);
    }
    
    /**
     * 检查特定槽位中特定物品是否有自定义限制
     */
    public boolean hasSlotLimit(int slotIndex, Item item) {
        Map<Item, Integer> slotMap = slotLimits.get(slotIndex);
        return (slotMap != null && slotMap.containsKey(item)) ||
               universalSlotLimits.containsKey(slotIndex);
    }
    
    /**
     * 获取特定槽位的所有物品限制
     */
    public Map<Item, Integer> getSlotLimits(int slotIndex) {
        return slotLimits.getOrDefault(slotIndex, new HashMap<>());
    }
    
    /**
     * 清空所有限制
     */
    public void clear() {
        slotLimits.clear();
        universalSlotLimits.clear();
    }
    
    /**
     * 序列化到NBT
     */
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        
        // 保存特定物品限制
        ListTag slotList = new ListTag();
        for (Map.Entry<Integer, Map<Item, Integer>> slotEntry : slotLimits.entrySet()) {
            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt("slot", slotEntry.getKey());
            
            ListTag itemList = new ListTag();
            for (Map.Entry<Item, Integer> itemEntry : slotEntry.getValue().entrySet()) {
                CompoundTag itemTag = new CompoundTag();
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(itemEntry.getKey());
                itemTag.putString("item", itemId.toString());
                itemTag.putInt("limit", itemEntry.getValue());
                itemList.add(itemTag);
            }
            slotTag.put("items", itemList);
            slotList.add(slotTag);
        }
        tag.put("slotLimits", slotList);
        
        // 保存通用槽位限制
        ListTag universalList = new ListTag();
        for (Map.Entry<Integer, Integer> entry : universalSlotLimits.entrySet()) {
            CompoundTag universalTag = new CompoundTag();
            universalTag.putInt("slot", entry.getKey());
            universalTag.putInt("limit", entry.getValue());
            universalList.add(universalTag);
        }
        tag.put("universalLimits", universalList);
        
        return tag;
    }
    
    /**
     * 从NBT反序列化
     */
    public void fromNBT(CompoundTag tag) {
        clear();
        
        // 加载特定物品限制
        if (tag.contains("slotLimits", Tag.TAG_LIST)) {
            ListTag slotList = tag.getList("slotLimits", Tag.TAG_COMPOUND);
            for (int i = 0; i < slotList.size(); i++) {
                CompoundTag slotTag = slotList.getCompound(i);
                int slotIndex = slotTag.getInt("slot");
                
                if (slotTag.contains("items", Tag.TAG_LIST)) {
                    ListTag itemList = slotTag.getList("items", Tag.TAG_COMPOUND);
                    for (int j = 0; j < itemList.size(); j++) {
                        CompoundTag itemTag = itemList.getCompound(j);
                        String itemId = itemTag.getString("item");
                        int limit = itemTag.getInt("limit");
                        
                        ResourceLocation itemLocation = ResourceLocation.tryParse(itemId);
                        if (itemLocation != null) {
                            Item item = BuiltInRegistries.ITEM.get(itemLocation);
                            if (item != null) {
                                setSlotLimit(slotIndex, item, limit);
                            }
                        }
                    }
                }
            }
        }
        
        // 加载通用槽位限制
        if (tag.contains("universalLimits", Tag.TAG_LIST)) {
            ListTag universalList = tag.getList("universalLimits", Tag.TAG_COMPOUND);
            for (int i = 0; i < universalList.size(); i++) {
                CompoundTag universalTag = universalList.getCompound(i);
                int slotIndex = universalTag.getInt("slot");
                int limit = universalTag.getInt("limit");
                setUniversalSlotLimit(slotIndex, limit);
            }
        }
    }
}