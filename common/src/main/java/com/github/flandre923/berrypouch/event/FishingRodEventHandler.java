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

        // 1. Check if Cobblemon fishing rod
        if (!isCobblemonFishingRod(heldStack)) {
            return CompoundEventResult.pass();
        }

        // Server-side logic only
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Get equipped pouch
            AccessoriesCapability capability = AccessoriesCapability.get(serverPlayer);
            if (capability == null) return CompoundEventResult.pass();
            Optional<SlotEntryReference> pouchRefOpt = capability.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                    .stream()
                    .findFirst();
            if (pouchRefOpt.isEmpty()) return CompoundEventResult.pass();
            ItemStack pouchStack = pouchRefOpt.get().stack();
            if (!(pouchStack.getItem() instanceof BerryPouch berryPouch)) return CompoundEventResult.pass();

            // 2. Check current bait on rod
            ItemStack currentBaitStackOnRod = PokerodItem.Companion.getBaitStackOnRod(heldStack);

            // --- Logic based on whether the rod has bait ---
            if (!currentBaitStackOnRod.isEmpty()) {
                // --- Logic if rod ALREADY has bait ---
                Item rodBaitItem = currentBaitStackOnRod.getItem();
                ResourceLocation rodBaitRL = BuiltInRegistries.ITEM.getKey(rodBaitItem);

                // Sync LAST_USED_BAIT if necessary and possible
                if (!rodBaitRL.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
                    Optional<ResourceLocation> pouchLastUsedRLOpt = PouchDataHelper.getLastUsedBait(pouchStack);
                    // Needs sync if pouch has no record OR pouch record differs from rod
                    boolean needsSync = pouchLastUsedRLOpt.isEmpty() || !pouchLastUsedRLOpt.get().equals(rodBaitRL);

                    if (needsSync) {
                        // Check if the bait type on the rod actually exists in the pouch before syncing
                        ItemContainerContents pouchContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                        boolean rodBaitExistsInPouch = pouchContents.stream()
                                .anyMatch(stack -> !stack.isEmpty() && stack.is(rodBaitItem));
                        if (rodBaitExistsInPouch) {
                            PouchDataHelper.setLastUsedBait(pouchStack, rodBaitItem);
                            // Component change on pouchStack will be saved eventually or if pouch is modified later.
                        }
                        // If needsSync was true but rodBait doesn't exist in pouch, we don't sync.
                    }
                }
                // Rod already has bait, normal fishing behaviour proceeds. Pass event.
                return CompoundEventResult.pass();

            } else {
                // --- Logic if rod is EMPTY (Prepare for Auto-Bait) ---

                // <<< NEW LOGIC >>> Clear the Last Used Bait record because the rod is empty.
                // This ensures the auto-bait doesn't immediately try to use a potentially outdated preference.
                PouchDataHelper.clearLastUsedBait(pouchStack);

                // --- Now, proceed with the Auto-Bait logic ---

                // 4. Get Pouch Data (Container, Marked Slots, etc.)
                ItemContainerContents currentContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                int pouchSize = berryPouch.getSize();
                NonNullList<ItemStack> pouchItems = NonNullList.withSize(pouchSize, ItemStack.EMPTY);
                currentContents.copyInto(pouchItems); // Mutable list

                List<Integer> markedSlots = MarkedSlotsHelper.getMarkedSlots(pouchStack);
                // lastUsedBaitRLOpt is no longer needed here as we just cleared it.
                boolean hasMarkedSlots = !markedSlots.isEmpty(); // Check if any slots are marked

                // --- Start Priority Search (Now starts effectively at Marked -> Any) ---
                int foundSlotIndex = -1;
                ItemStack baitToUse = ItemStack.EMPTY;
                Item usedBaitItem = null; // Keep track of the item type used

                // Priority 1 (Effectively Skipped because Last Used was cleared)

                // Priority 2: Marked Bait (Only if there are marked slots)
                boolean checkedMarked = false; // Flag to know if we went through marked logic
                if (hasMarkedSlots) {
                    checkedMarked = true; // We are attempting marked logic
                    for (int markedIndex : markedSlots) {
                        if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                            ItemStack stackInSlot = pouchItems.get(markedIndex);
                            if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                                foundSlotIndex = markedIndex;
                                baitToUse = stackInSlot;
                                usedBaitItem = stackInSlot.getItem();
                                break; // Found first available marked bait
                            }
                        }
                    }
                    // Requirement 2: If we intended to use marked but found none
                    if (foundSlotIndex == -1) {
                        serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.marked_bait_exhausted"), true);
                        return CompoundEventResult.pass(); // Stop auto-baiting process
                    }
                }

                // Priority 3: Any Available Bait (Only if Marked weren't applicable or already checked and failed - though failure case returns above)
                // This runs if hasMarkedSlots was false, OR if we somehow passed the exhaustion check above (shouldn't happen).
                if (foundSlotIndex == -1) { // No marked slots or somehow passed marked check without finding anything
                    for (int i = 0; i < pouchItems.size(); i++) {
                        ItemStack stackInSlot = pouchItems.get(i);
                        if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                            foundSlotIndex = i;
                            baitToUse = stackInSlot;
                            usedBaitItem = stackInSlot.getItem();
                            break; // Found first available bait
                        }
                    }
                }

                // --- Apply Found Bait (if any) ---
                if (foundSlotIndex != -1 && !baitToUse.isEmpty() && usedBaitItem != null) {
                    ItemStack baitToSet = baitToUse.copyWithCount(1);

                    // 5. Set bait on rod
                    PokerodItem.Companion.setBait(heldStack, baitToSet);

                    // 6. Remove bait from pouch inventory (use the mutable list)
                    ItemStack originalStack = pouchItems.get(foundSlotIndex);
                    originalStack.shrink(1);

                    // 7. Update pouch Data Components (Inventory and Last Used)
                    pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(pouchItems));
                    // Set Last Used Bait to the one we just applied
                    PouchDataHelper.setLastUsedBait(pouchStack, usedBaitItem);

                    // 8. Send message to player
                    serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.autobait", baitToSet.getHoverName()), true);

                    // 9. Interrupt event as we handled the baiting
                    return CompoundEventResult.interruptTrue(heldStack);

                } else {
                    // No bait found according to rules (Marked -> Any), or marked bait was exhausted.
                    // No message needed here unless specifically desired for "pouch empty".
                    return CompoundEventResult.pass();
                }
            } // End of if/else block for rod bait status
        } // End server-side check

        // Client side or other unhandled cases, pass event
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