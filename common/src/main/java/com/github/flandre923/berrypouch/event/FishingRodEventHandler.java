package com.github.flandre923.berrypouch.event;// 创建一个新的事件监听类，或者在你的主类/客户端类中注册
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
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
        ItemStack heldStack = player.getItemInHand(hand);
        Level level = player.level();

        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return CompoundEventResult.pass(); // Only process on server side for ServerPlayer
        }


        if (!isCobblemonFishingRod(heldStack) || !PouchDataHelper.isAutoBerryEnabled((ServerPlayer)player)) {
            return CompoundEventResult.pass(); // Not a Cobblemon rod
        }

        // --- Main Logic: Distinguish Cast vs Retrieve ---
        // If player.fishing is null, they are attempting to cast the rod.
        // If player.fishing is not null, they are attempting to retrieve the rod.
        boolean isAttemptingCast = player.fishing == null;

        // Check current bait on rod
        ItemStack currentBaitStackOnRod = PokerodItem.Companion.getBaitStackOnRod(heldStack);

        // --- Logic if rod ALREADY has bait ---
        if (!currentBaitStackOnRod.isEmpty()) {
            // Rod already has bait.
            // If player is attempting to cast with bait, let the default logic run.
            // If player is attempting to retrieve with bait, let the default logic run.
            // In either case, no auto-baiting from pouch is needed.
            // Optional: Sync LAST_USED_BAIT if the bait on the rod isn't the one recorded in the pouch.
            // This would handle cases where bait was manually put on the rod or the pouch NBT was reset.
            // Let's add this check here.
            Item rodBaitItem = currentBaitStackOnRod.getItem();
            // Check if rod bait is a valid Cobblemon berry/bait type before syncing
            if (isCobblemonBerry(currentBaitStackOnRod)) {
                ResourceLocation rodBaitRL = BuiltInRegistries.ITEM.getKey(rodBaitItem);
                Optional<ResourceLocation> pouchLastUsedRLOpt = PouchDataHelper.getLastUsedBait(pouchStackCheck(serverPlayer)); // Use helper method to get pouch stack

                // Needs sync if pouch has no record OR pouch record differs from rod
                boolean needsSync = pouchLastUsedRLOpt.isEmpty() || !pouchLastUsedRLOpt.get().equals(rodBaitRL);

                if (needsSync) {
                    // Find the pouch stack to update
                    Optional<ItemStack> pouchStackOpt = getEquippedBerryPouch(serverPlayer);
                    if (pouchStackOpt.isPresent()) {
                        ItemStack pouchStack = pouchStackOpt.get();
                        // Check if the bait type on the rod actually exists in the pouch before syncing
                        // This prevents syncing a bait type the pouch doesn't even contain.
                        ItemContainerContents pouchContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                        boolean rodBaitExistsInPouch = pouchContents.stream()
                                .anyMatch(stack -> !stack.isEmpty() && stack.is(rodBaitItem));
                        if (rodBaitExistsInPouch) {
                            PouchDataHelper.setLastUsedBait(pouchStack, rodBaitItem);
                            // No message needed for sync, it's internal state update.
                        }
                    }
                }
            }
            // Always pass when the rod already has bait.
            return CompoundEventResult.pass();

        } else {
            // --- Rod is EMPTY when used ---

            // We ONLY attempt to auto-bait if the player is ATTEMPTING TO CAST.
            if (!isAttemptingCast) {
                // Player is attempting to retrieve an empty rod (unlikely unless something went wrong)
                // or perhaps just right-clicking an empty rod without casting intent (less likely with PokerodItem).
                // In any case, no auto-baiting needed on retrieve.
                return CompoundEventResult.pass();
            }

            // --- If we reach here, it's a ServerPlayer attempting to CAST an EMPTY Cobblemon rod ---

            // Get equipped pouch and contents
            Optional<ItemStack> pouchStackOpt = getEquippedBerryPouch(serverPlayer);
            if (pouchStackOpt.isEmpty()) return CompoundEventResult.pass(); // No Berry Pouch equipped
            ItemStack pouchStack = pouchStackOpt.get();
            BerryPouch berryPouch = (BerryPouch) pouchStack.getItem(); // Safe cast due to getEquipped filter

            ItemContainerContents currentContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            int pouchSize = berryPouch.getSize();
            NonNullList<ItemStack> pouchItems = NonNullList.withSize(pouchSize, ItemStack.EMPTY);
            currentContents.copyInto(pouchItems); // Mutable list

            List<Integer> markedSlots = MarkedSlotsHelper.getMarkedSlots(pouchStack);
            boolean hasMarkedSlots = !markedSlots.isEmpty();

            int foundSlotIndex = -1;
            ItemStack baitToUse = ItemStack.EMPTY;
            Item usedBaitItem = null;

            // --- Bait Finding Priority ---

            // Priority 1: Last Used Bait Type
            Optional<ResourceLocation> lastUsedBaitRLOpt = PouchDataHelper.getLastUsedBait(pouchStack);
            if (lastUsedBaitRLOpt.isPresent()) {
                ResourceLocation lastUsedRL = lastUsedBaitRLOpt.get();
                Optional<Item> lastUsedItemOpt = BuiltInRegistries.ITEM.getOptional(lastUsedRL); // Look up the Item

                if (lastUsedItemOpt.isPresent()) {
                    Item lastUsedItem = lastUsedItemOpt.get();
                    // Try to find this specific item type in the pouch, prioritizing marked slots
                    if (hasMarkedSlots) {
                        for (int markedIndex : markedSlots) {
                            if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                                ItemStack stackInSlot = pouchItems.get(markedIndex);
                                // Check if item type matches AND it's a valid bait, and stack is not empty
                                if (!stackInSlot.isEmpty() && stackInSlot.is(lastUsedItem) && isCobblemonBerry(stackInSlot)) {
                                    foundSlotIndex = markedIndex;
                                    baitToUse = stackInSlot;
                                    usedBaitItem = lastUsedItem;
                                    // No message needed yet, we'll send one after applying.
                                    break; // Found the last used type in a marked slot
                                }
                            }
                        }
                    }

                    // If not found in marked slots, check any slot for the last used type
                    if (foundSlotIndex == -1) {
                        for (int i = 0; i < pouchItems.size(); i++) {
                            ItemStack stackInSlot = pouchItems.get(i);
                            // Check if item type matches AND it's a valid bait, and stack is not empty
                            if (!stackInSlot.isEmpty() && stackInSlot.is(lastUsedItem) && isCobblemonBerry(stackInSlot)) {
                                foundSlotIndex = i;
                                baitToUse = stackInSlot;
                                usedBaitItem = lastUsedItem;
                                // No message needed yet.
                                break; // Found the last used type in any slot
                            }
                        }
                    }
                    // If found here (in either marked or any slot), foundSlotIndex and usedBaitItem are set.
                    // If not found, foundSlotIndex remains -1, and we proceed to Priority 2.
                }
                // If lastUsedItemOpt was empty (item no longer exists?), foundSlotIndex remains -1, proceed to Priority 2.
            }

            // Priority 2: Marked Bait (Only if Priority 1 failed)
            // Check marked slots from the beginning if last used wasn't found/applicable
            if (foundSlotIndex == -1 && hasMarkedSlots) {
                for (int markedIndex : markedSlots) {
                    if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                        ItemStack stackInSlot = pouchItems.get(markedIndex);
                        // Check if it's a valid bait and stack is not empty
                        if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                            foundSlotIndex = markedIndex;
                            baitToUse = stackInSlot;
                            usedBaitItem = stackInSlot.getItem();
                            break; // Found first available marked bait
                        }
                    }
                }
                // Special case: If we HAVE marked slots but found none in the marked search, send message and stop.
                if (foundSlotIndex == -1) {
                    serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.marked_bait_exhausted"), true);
                    // Stop auto-baiting process. Let the default rod use logic run (which will fail to cast).
                    return CompoundEventResult.pass();
                }
            }

            // Priority 3: Any Available Bait (Only if Priority 1 and Priority 2 failed)
            if (foundSlotIndex == -1) { // Only check any slot if both Priority 1 and Priority 2 failed
                for (int i = 0; i < pouchItems.size(); i++) {
                    ItemStack stackInSlot = pouchItems.get(i);
                    // Check if it's a valid bait and stack is not empty
                    if (!stackInSlot.isEmpty() && isCobblemonBerry(stackInSlot)) {
                        foundSlotIndex = i;
                        baitToUse = stackInSlot;
                        usedBaitItem = stackInSlot.getItem();
                        break; // Found first available bait in any slot
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
                // Set Last Used Bait to the one we just applied - THIS IS WHERE LAST USED IS UPDATED
                PouchDataHelper.setLastUsedBait(pouchStack, usedBaitItem);

                // 8. Send message to player
                serverPlayer.sendSystemMessage(Component.translatable("message.berrypouch.autobait", baitToSet.getHoverName()), true);

                // 9. Interrupt event. We successfully added bait, the rod is now ready for casting.
                // Interrupting prevents the default PokerodItem use logic from running without bait.
                return CompoundEventResult.interruptTrue(heldStack);

            } else {
                // No bait found according to rules (Last Used -> Marked -> Any),
                // AND we didn't return early from marked exhaustion message.
                // Let the game's normal interaction proceed. The rod has no bait, so the cast will fail.
                return CompoundEventResult.pass();
            }
        } // End of if/else block for rod bait status
    }

    /**
     * Helper method to get the equipped Berry Pouch stack from a player.
     * Returns an empty Optional if not found or not a valid pouch.
     */
    private static Optional<ItemStack> getEquippedBerryPouch(ServerPlayer player) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) return Optional.empty();
        Optional<SlotEntryReference> pouchRefOpt = capability.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                .stream()
                .findFirst();
        if (pouchRefOpt.isEmpty()) return Optional.empty();
        ItemStack pouchStack = pouchRefOpt.get().stack();
        if (!(pouchStack.getItem() instanceof BerryPouch)) return Optional.empty(); // Should be redundant but good practice
        return Optional.of(pouchStack);
    }

    // Quick helper for the sync logic to avoid repeating pouch lookup
    private static ItemStack pouchStackCheck(ServerPlayer player) {
        return getEquippedBerryPouch(player).orElse(ItemStack.EMPTY);
    }


    // 辅助方法 (保持不变)
    public static boolean isCobblemonFishingRod(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof PokerodItem;
    }

    public static boolean isCobblemonBerry(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // Cobblemon API check for fishing baits (which includes berries used as bait)
        return BerryPouch.isBerry(stack);
    }
}