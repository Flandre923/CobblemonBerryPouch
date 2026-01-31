package com.github.flandre923.berrypouch.menu.container;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.item.pouch.PokeBallGunHelper;
import com.github.flandre923.berrypouch.item.pouch.PokeBallGunInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PokeBallGunContainer extends AbstractContainerMenu {
    private final ItemStack gunStack;
    private final PokeBallGunInventory  gunInventory;

    // 用于同步 selectedIndex 的 DataSlot
    private int selectedIndex;

    // 布局常量
    private static final int GUN_SLOTS = 9;
    private static final int GUN_START_X = 7;
    private static final int GUN_START_Y = 15;
    private static final int PLAYER_INV_START_X = 7;
    private static final int PLAYER_INV_START_Y = 47;

    public PokeBallGunContainer(int containerId, Inventory playerInv, ItemStack gunStack) {
        super(ModRegistries.ModMenuTypes.POKEBALL_GUN_MENU.get(), containerId);
        this.gunStack = gunStack;
        this.gunInventory = new PokeBallGunInventory(gunStack,GUN_SLOTS);


        // 添加 DataSlot 用于同步
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return PokeBallGunHelper.getSelectedIndex(gunStack);
            }

            @Override
            public void set(int value) {
                selectedIndex = value;
                PokeBallGunHelper.updateSelectedItemId(gunStack);
            }
        });

        // 添加发射器槽位 (1行8列)
        addGunInventory();
        // 添加玩家背包槽位
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);


    }

    public static PokeBallGunContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        return new PokeBallGunContainer(windowId,inv,ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < GUN_SLOTS) {
                // 从发射器移到背包
                if (!this.moveItemStackTo(stackInSlot, GUN_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从背包移到发射器（只移动捕捉球）
                if (PokeBallSlot.isPokeBall(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, GUN_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            
            PokeBallGunHelper.updateSelectedItemId(gunStack);
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return !gunStack.isEmpty();
    }


    private void addGunInventory(){
        // 添加发射器槽位 (1行8列)
        for (int col = 0; col < GUN_SLOTS; col++) {
            addSlot(new PokeBallSlot(gunInventory, col, GUN_START_X + col * 18 + 1, GUN_START_Y + 1));
        }
    }

    private void addPlayerInventory(Inventory playerInv) {
        // 添加玩家背包 (3行9列)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9,
                        PLAYER_INV_START_X + col * 18 + 1,
                        PLAYER_INV_START_Y + row * 18 + 1));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInv) {
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col,
                    PLAYER_INV_START_X + col * 18 + 1,
                    PLAYER_INV_START_Y + 58 + 1));
        }
    }


    public class PokeBallSlot extends Slot {

        public PokeBallSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isPokeBall(stack);
        }

        public static boolean isPokeBall(ItemStack stack) {
            if (stack.isEmpty()) return false;

            // 方法1: 检查物品是否属于 Cobblemon 的 PokeBall 类
            if (stack.getItem() instanceof PokeBallItem) return true;

            // 方法2: 使用 Tag 检查（推荐，更灵活）
//            return stack.is(ModTags.Items.POKEBALLS);

            // 方法3: 检查物品 ID 前缀
            // ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            // return id.getNamespace().equals("cobblemon") && id.getPath().contains("poke_ball");
            return false;
        }

    }
    public SimpleContainer getGunInventory() {
        return this.gunInventory;
    }

    public ItemStack getGunStack() {
        return this.gunStack;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
