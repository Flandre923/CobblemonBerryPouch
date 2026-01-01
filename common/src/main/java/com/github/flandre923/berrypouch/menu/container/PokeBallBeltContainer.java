package com.github.flandre923.berrypouch.menu.container;

import com.cobblemon.mod.common.item.PokeBallItem;
import com.github.flandre923.berrypouch.ModRegistries;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltHelper;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PokeBallBeltContainer extends AbstractContainerMenu {
    private final ItemStack beltStack;
    private final PokeBallBeltInventory  beltInventory;

    // 用于同步 selectedIndex 的 DataSlot
    private int selectedIndex;

    // 布局常量
    private static final int BELT_SLOTS = 9;
    private static final int BELT_START_X = 7;
    private static final int BELT_START_Y = 15;
    private static final int PLAYER_INV_START_X = 7;
    private static final int PLAYER_INV_START_Y = 47;

    public PokeBallBeltContainer(int containerId, Inventory playerInv, ItemStack beltStack) {
        super(ModRegistries.ModMenuTypes.POKEBALL_BELT_MENU.get(), containerId);
        this.beltStack = beltStack;
        this.beltInventory = new PokeBallBeltInventory(beltStack,BELT_SLOTS);


        // 添加 DataSlot 用于同步
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return PokeBallBeltHelper.getSelectedIndex(beltStack);
            }

            @Override
            public void set(int value) {
                selectedIndex = value;
            }
        });

        // 添加腰带槽位 (1行8列)
        addBeltInventory();
        // 添加玩家背包槽位
        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);


    }

    public static PokeBallBeltContainer fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {
        return new PokeBallBeltContainer(windowId,inv,ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            if (index < BELT_SLOTS) {
                // 从腰带移到背包
                if (!this.moveItemStackTo(stackInSlot, BELT_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从背包移到腰带（只移动捕捉球）
                if (PokeBallSlot.isPokeBall(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 0, BELT_SLOTS, false)) {
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
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return !beltStack.isEmpty();
    }


    private void addBeltInventory(){
        // 添加腰带槽位 (1行8列)
        for (int col = 0; col < BELT_SLOTS; col++) {
            addSlot(new PokeBallSlot(beltInventory, col, BELT_START_X + col * 18 + 1, BELT_START_Y + 1));
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
    public SimpleContainer getBeltInventory() {
        return this.beltInventory;
    }

    public ItemStack getBeltStack() {
        return this.beltStack;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }
}
