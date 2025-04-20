package com.github.flandre923.berrypouch.event;// 创建一个新的事件监听类，或者在你的主类/客户端类中注册
import com.cobblemon.mod.common.api.fishing.FishingBaits;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.item.BerryPouch;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.SimpleContainer;
import java.util.List;

public class FishingRodEventHandler {

    // 假设 COBBLEMON_BAIT_NBT_KEY 和 COBBLEMON_FISHING_RODS 已定义
    // 假设 isCobblemonFishingRod(ItemStack stack) 和 isCobblemonBerry(ItemStack stack) 方法已实现
    // isCobblemonBerry 可以使用你 BerryPouch 中的 isBerry，但最好更精确地检查 Cobblemon 的树果

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(FishingRodEventHandler::onPlayerUseItem);
    }

    private static CompoundEventResult<ItemStack> onPlayerUseItem(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand);
        Level level = player.level();
        if (!isCobblemonFishingRod(heldStack)) {
            return CompoundEventResult.pass();
        }

        // 2. 检查钓竿是否已经有诱饵 (在服务器端检查 NBT)
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            RegistryAccess registryAccess = serverPlayer.registryAccess();

            ItemStack currentBaitStack = PokerodItem.Companion.getBaitStackOnRod(heldStack);
            if (!currentBaitStack.isEmpty()) {
                // Already has bait, do nothing.
                return CompoundEventResult.pass();
            }

            // 3. Find equipped BerryPouch
            AccessoriesCapability capability = AccessoriesCapability.get(serverPlayer);
            if (capability == null) {
                return CompoundEventResult.pass();
            }

            List<SlotEntryReference> equippedPouches = capability.getEquipped(stack -> stack.getItem() instanceof BerryPouch);
            if (equippedPouches.isEmpty()) {
                return CompoundEventResult.pass();
            }

            // 4. 遍历 Pouch 寻找第一个可用的 Cobblemon 树果
            for (SlotEntryReference pouchRef : equippedPouches) {
                ItemStack pouchStack = pouchRef.stack();
                SimpleContainer pouchInventory = BerryPouch.getInventory(pouchStack, level);
                for (int i = 0; i < pouchInventory.getContainerSize(); i++) {
                    ItemStack potentialBait = pouchInventory.getItem(i);
                    if (!potentialBait.isEmpty() && isCobblemonBerry(potentialBait)) { // 确保是 Cobblemon 的树果
                        // 5. 找到树果，将其设置到钓竿 NBT
                        ItemStack baitToSet = potentialBait.copy();
                        baitToSet.setCount(1); // 诱饵通常是1个
                        PokerodItem.Companion.setBait(heldStack, baitToSet);
                        potentialBait.shrink(1);
                        if (potentialBait.isEmpty()) {
                            pouchInventory.setItem(i, ItemStack.EMPTY);
                        }
                        pouchInventory.setChanged(); // Save pouch changes
                        serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.autobait", baitToSet.getHoverName()),true);
                        return CompoundEventResult.interruptTrue(heldStack);
                    }
                }
            }
        }
        return CompoundEventResult.pass();
    }

    // 辅助方法 (需要实现)
    public static boolean isCobblemonFishingRod(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof PokerodItem;
    }

    public static boolean isCobblemonBerry(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return FishingBaits.INSTANCE.getFromBaitItemStack(stack) != null;
    }
}

// 在你的 Mod 主类或 Client 类初始化时调用:
// FishingRodEventHandler.register();