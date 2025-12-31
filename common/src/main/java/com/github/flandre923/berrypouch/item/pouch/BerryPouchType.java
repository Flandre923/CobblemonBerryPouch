package com.github.flandre923.berrypouch.item.pouch;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.BerryPouchGui24StorageSlot;
import com.github.flandre923.berrypouch.item.BerryPouchGui30StorageSlot;
import com.github.flandre923.berrypouch.item.BerryPouchGui86StorageSlot;
import com.github.flandre923.berrypouch.item.IBerryPouchStorage;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public enum BerryPouchType {
    SMALL(24, 175, 161, "small", "berry_pouch_small",
            new BerryPouchGui24StorageSlot(24), // <-- 将尺寸 24 传递进去
            8, 2, 8, 84),
    MEDIUM(30, 208, 202, "medium", "berry_pouch_medium",
            new BerryPouchGui30StorageSlot(30), // <-- 将尺寸 30 传递进去
            21, 2, 21, 84),
    LARGE(86, 256, 257, "large", "berry_pouch_large",
            new BerryPouchGui86StorageSlot(86), // 更新为86个槽位存储类 (70个树果 + 16个other_baits)
            11, 4, 47, 161);

    private final int size;
    private final int guiWidth;
    private final int guiHeight;
    private final String name;
    private final String registryName;
    private final IBerryPouchStorage storageSlot;

    private final int titleX;
    private final int titleY;
    private final int inventoryX;
    private final int inventoryY;

    BerryPouchType(int size, int guiWidth, int guiHeight, String name,
                   String registryName, IBerryPouchStorage storageSlot,
                   int titleX, int titleY, int inventoryX, int inventoryY) {
        this.size = size;
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
        this.name = name;
        this.registryName = registryName;
        this.storageSlot = storageSlot;
        this.titleX = titleX;
        this.titleY = titleY;
        this.inventoryX = inventoryX;
        this.inventoryY = inventoryY;
    }

    public ResourceLocation getTexture() {
        return ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID,
                "textures/gui/berry_pouch_" + name + ".png");
    }

    public String getRegistryName() {
        return registryName;
    }

    public ResourceLocation getRegistryLocation() {
        return ResourceLocation.fromNamespaceAndPath(ModCommon.MOD_ID, registryName);
    }

    public static BerryPouchType fromSize(int size) {
        return Arrays.stream(values())
                .filter(type -> type.size == size)
                .findFirst()
                .orElse(SMALL);
    }
    // 新增获取存储槽的方法
    public IBerryPouchStorage getStorageSlot() {
        return storageSlot;
    }
    // Getters
    public int getSize() { return size; }
    public int getGuiWidth() { return guiWidth; }
    public int getGuiHeight() { return guiHeight; }

    // 添加新的getter方法
    public int getTitleX() {
        return titleX;
    }

    public int getTitleY() {
        return titleY;
    }

    public int getInventoryX() {
        return inventoryX;
    }

    public int getInventoryY() {
        return inventoryY;
    }
}