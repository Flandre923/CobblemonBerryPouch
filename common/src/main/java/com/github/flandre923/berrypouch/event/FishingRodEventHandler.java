package com.github.flandre923.berrypouch.event;// 创建一个新的事件监听类，或者在你的主类/客户端类中注册
import com.cobblemon.mod.common.api.fishing.FishingBaits;
import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.helper.MarkedSlotsHelper;
import com.github.flandre923.berrypouch.helper.PouchDataHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.List;
import java.util.Optional;

public class FishingRodEventHandler {

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(FishingRodEventHandler::onPlayerUseItem);
    }

    private static CompoundEventResult<ItemStack> onPlayerUseItem(Player player, InteractionHand hand) {
        ItemStack heldStack = player.getItemInHand(hand); // 可能是鱼竿
        Level level = player.level();

        // 1. 检查是否为 Cobblemon 鱼竿
        if (!isCobblemonFishingRod(heldStack)) {
            return CompoundEventResult.pass();
        }

        // 仅在服务器端处理逻辑
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // *** 首先获取装备的袋子，因为无论鱼竿是否有饵，我们都可能需要更新它 ***
            AccessoriesCapability capability = AccessoriesCapability.get(serverPlayer);
            if (capability == null) {
                return CompoundEventResult.pass(); // 没有饰品能力
            }
            Optional<SlotEntryReference> pouchRefOpt = capability.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                    .stream()
                    .findFirst();
            if (pouchRefOpt.isEmpty()) {
                return CompoundEventResult.pass(); // 没有装备袋子
            }
            ItemStack pouchStack = pouchRefOpt.get().stack();
            if (!(pouchStack.getItem() instanceof BerryPouch berryPouch)) {
                return CompoundEventResult.pass(); // 理论上不会发生
            }

            // 2. 检查鱼竿上当前的饵料
            ItemStack currentBaitStackOnRod = PokerodItem.Companion.getBaitStackOnRod(heldStack);

            // ---> 新增逻辑：如果鱼竿已有饵料，尝试同步 LAST_USED_BAIT <---
            if (!currentBaitStackOnRod.isEmpty()) {
                Item rodBaitItem = currentBaitStackOnRod.getItem();
                ResourceLocation rodBaitRL = BuiltInRegistries.ITEM.getKey(rodBaitItem);

                // 如果 rodBaitRL 无效 (比如物品未注册)，则不处理
                if (!rodBaitRL.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
                    Optional<ResourceLocation> pouchLastUsedRLOpt = PouchDataHelper.getLastUsedBait(pouchStack);

                    // 检查是否需要更新: (袋子无记录 || 袋子记录与鱼竿不同)
                    boolean needsSync = pouchLastUsedRLOpt.isEmpty() || !pouchLastUsedRLOpt.get().equals(rodBaitRL);

                    if (needsSync) {
                        // 在更新前，必须确认袋子里确实有这种类型的饵料
                        ItemContainerContents pouchContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                        boolean rodBaitExistsInPouch = pouchContents.stream()
                                .anyMatch(stack -> !stack.isEmpty() && stack.is(rodBaitItem));

                        if (rodBaitExistsInPouch) {
                            // 同步：将鱼竿当前的饵料类型设置为袋子的 LAST_USED_BAIT
                            PouchDataHelper.setLastUsedBait(pouchStack, rodBaitItem);
                            // 注意：这里我们只更新了 LAST_USED_BAIT，没有修改袋子内容，
                            // 所以不需要立即保存 CONTAINER 组件。
                            // 如果后续没有其他修改（比如自动装饵），这个更改会在物品下次保存时生效。
                        }
                    }
                }
                // 鱼竿已有饵料，事件正常传递，不做自动装饵
                return CompoundEventResult.pass();
            }

            // ---> 原有逻辑：鱼竿为空，执行自动装饵 <---
            // (此部分代码基本不变，但现在 PouchDataHelper.getLastUsedBait 会获取到上面可能已同步的值)

            // 4. 获取袋子数据 (现在 pouchStack 已经获取到了)
            ItemContainerContents currentContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            int pouchSize = berryPouch.getSize();
            NonNullList<ItemStack> pouchItems = NonNullList.withSize(pouchSize, ItemStack.EMPTY);
            currentContents.copyInto(pouchItems);

            List<Integer> markedSlots = MarkedSlotsHelper.getMarkedSlots(pouchStack);
            // 重新获取一次，因为它可能刚刚被上面的同步逻辑更新了
            Optional<ResourceLocation> lastUsedBaitRL = PouchDataHelper.getLastUsedBait(pouchStack);

            // --- 开始按优先级查找诱饵 ---
            int foundSlotIndex = -1;
            ItemStack baitToUse = ItemStack.EMPTY;

            // 优先级 1: 查找上次使用的诱饵 (现在可能是同步后的值)
            if (lastUsedBaitRL.isPresent()) {
                Optional<Item> lastUsedItem = BuiltInRegistries.ITEM.getOptional(lastUsedBaitRL.get());
                if (lastUsedItem.isPresent() && !lastUsedItem.get().equals(BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getDefaultKey()))) { // 确保 Item 有效
                    for (int i = 0; i < pouchItems.size(); i++) {
                        ItemStack stackInSlot = pouchItems.get(i);
                        if (!stackInSlot.isEmpty() && stackInSlot.is(lastUsedItem.get()) && isCobblemonBerry(stackInSlot)) {
                            foundSlotIndex = i;
                            baitToUse = stackInSlot;
                            break;
                        }
                    }
                } else if (lastUsedItem.isEmpty()){
                    // 如果 ResourceLocation 存在但找不到对应 Item (可能 Mod 移除)，清除无效记录
                    PouchDataHelper.clearLastUsedBait(pouchStack);
                }
            }

            // 优先级 2: 如果没找到上次的，查找标记的诱饵
            if (foundSlotIndex == -1) {
                for (int markedIndex : markedSlots) {
                    if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                        ItemStack stackInSlot = pouchItems.get(markedIndex);
                        if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                            foundSlotIndex = markedIndex;
                            baitToUse = stackInSlot;
                            break;
                        }
                    }
                }
            }

            // 优先级 3: 如果还没找到，查找第一个可用的诱饵
            if (foundSlotIndex == -1) {
                for (int i = 0; i < pouchItems.size(); i++) {
                    ItemStack stackInSlot = pouchItems.get(i);
                    if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                        foundSlotIndex = i;
                        baitToUse = stackInSlot;
                        break;
                    }
                }
            }

            // --- 应用找到的诱饵 ---
            if (foundSlotIndex != -1 && !baitToUse.isEmpty()) {
                ItemStack baitToSet = baitToUse.copyWithCount(1);
                Item usedBaitItem = baitToUse.getItem();

                // 5. 设置诱饵到鱼竿
                PokerodItem.Companion.setBait(heldStack, baitToSet);

                // 6. 从袋子库存中移除一个诱饵
                ItemStack originalStack = pouchItems.get(foundSlotIndex);
                originalStack.shrink(1);
                if (originalStack.isEmpty()) {
                    pouchItems.set(foundSlotIndex, ItemStack.EMPTY);
                }

                // 7. 更新袋子的 Data Components (库存和上次使用)
                pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(pouchItems));
                PouchDataHelper.setLastUsedBait(pouchStack, usedBaitItem); // 更新为这次自动装载的

                // 8. 发送消息给玩家
                serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.autobait", baitToSet.getHoverName()), true);

                // 9. 中断事件
                return CompoundEventResult.interruptTrue(heldStack);
            } else {
                // 优先级 4: 袋子为空或没有可用诱饵
                return CompoundEventResult.pass();
            }
        }

        // 客户端或其他情况，传递事件
        return CompoundEventResult.pass();
    }

    // 辅助方法 (保持不变)
    public static boolean isCobblemonFishingRod(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof PokerodItem;
    }

    public static boolean isCobblemonBerry(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return FishingBaits.INSTANCE.getFromBaitItemStack(stack) != null;
    }
}