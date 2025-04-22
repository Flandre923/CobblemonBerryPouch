package com.github.flandre923.berrypouch.network;

import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.helper.MarkedSlotsHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled; // For creating an empty buffer
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level; // Import Level
import net.minecraft.sounds.SoundEvents; // Import SoundEvents
import net.minecraft.sounds.SoundSource; // Import SoundSource

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ModNetworking {

    public static void register() {
        // 注册服务器端接收器 (C2S = Client to Server)
        // 当服务器收到 ID 为 OPEN_POUCH_PACKET_ID 的包时，调用 handleOpenPouch 方法
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,             // Side (Client -> Server)
                OpenPouchPayload.TYPE,               // The Payload Type constant
                OpenPouchPayload.CODEC,              // The StreamCodec for this payload
                ModNetworking::handleOpenPouch       // The handler method reference
        );
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                CycleBaitPacket.TYPE,
                CycleBaitPacket.CODEC,
                (packet, context) -> {
                    ServerPlayer player = (ServerPlayer) context.getPlayer();
                    context.queue(() -> handleCycleBaitRequest(player, packet.isMainHand(), packet.isLeftCycle()));
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                ToggleMarkSlotPayload.TYPE,
                ToggleMarkSlotPayload.CODEC,
                ModNetworking::handleToggleMarkSlot // 新的处理方法
        );
    }

    // 服务器端处理逻辑
    private static void handleOpenPouch(OpenPouchPayload payload, NetworkManager.PacketContext context) {
        // Cast to ServerPlayer is safe for C2S packets
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        // It's generally better practice to get the level from the player on the server
        Level level = player.level();

        // Queue the logic to run on the main server thread
        context.queue(() -> {
            AccessoriesCapability accessoriesCap = AccessoriesCapability.get(player);
            if (accessoriesCap == null) return;

            // Find the first equipped BerryPouch
            accessoriesCap.getEquipped(stack -> stack.getItem() instanceof BerryPouch)
                    .stream()
                    .findFirst()
                    .ifPresent(entry -> {
                        ItemStack pouchStack = entry.stack();
                        if (!pouchStack.isEmpty() && pouchStack.getItem() instanceof BerryPouch pouchItem && !player.level().isClientSide) {
                            // Play sound on the server
                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
                            // Use the helper method to open the GUI
                            BerryPouchManager.openPouchGUI(player, pouchStack, 2);
                        }
                    });
        });
    }

    // --- Update handleCycleBaitRequest ---
    private static void handleCycleBaitRequest(ServerPlayer player, boolean isMainHand, boolean isLeftCycle) {
        Level level = player.level();
        InteractionHand hand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack heldStack = player.getItemInHand(hand); // Rod ItemStack

        if (!FishingRodEventHandler.isCobblemonFishingRod(heldStack)) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.not_holding_rod"), true);
            return;
        }

        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) return;

        Optional<SlotEntryReference> pouchRefOpt = Optional.ofNullable(capability.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch));
        if (pouchRefOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.need_pouch"), true);
            return;
        }

        ItemStack pouchStack = pouchRefOpt.get().stack(); // The Berry Pouch ItemStack
        // --- Get Marked Slots using new Helper ---
        List<Integer> markedSlots = MarkedSlotsHelper.getMarkedSlots(pouchStack);

        // --- Get Pouch Inventory from Data Component ---
        ItemContainerContents currentContents = pouchStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        // Create a mutable list representation matching the pouch size for easier manipulation
        int pouchSize = ((BerryPouch) pouchStack.getItem()).getPouchType().getSize(); // Get size dynamically
        NonNullList<ItemStack> pouchItems = NonNullList.withSize(pouchSize, ItemStack.EMPTY);
        currentContents.copyInto(pouchItems); // Populate the list


        // --- Collect available MARKED berry types ---
        List<Item> availableMarkedBerryTypes = new ArrayList<>();
        for (int markedIndex : markedSlots) {
            if (markedIndex >= 0 && markedIndex < pouchItems.size()) { // Bounds check
                ItemStack stackInSlot = pouchItems.get(markedIndex);
                if (!stackInSlot.isEmpty() && FishingRodEventHandler.isCobblemonBerry(stackInSlot)) {
                    if (!availableMarkedBerryTypes.contains(stackInSlot.getItem())) {
                        availableMarkedBerryTypes.add(stackInSlot.getItem());
                    }
                }
            }
        }

        // --- Fallback: Collect ALL berry types ---
        List<Item> allBerryTypes = new ArrayList<>();
        for (ItemStack stackInSlot : pouchItems) {
            if (!stackInSlot.isEmpty() && FishingRodEventHandler.isCobblemonBerry(stackInSlot)) {
                if (!allBerryTypes.contains(stackInSlot.getItem())) {
                    allBerryTypes.add(stackInSlot.getItem());
                }
            }
        }

        // Determine which list to use and the appropriate message
        List<Item> finalAvailableBerryTypes;
        Component feedbackMsg;
        if (!availableMarkedBerryTypes.isEmpty()) {
            finalAvailableBerryTypes = availableMarkedBerryTypes;
            feedbackMsg = Component.translatable("message.berrypouch.no_marked_bait_in_pouch"); // Will only be shown if list becomes empty later
        } else if (!allBerryTypes.isEmpty()){
            finalAvailableBerryTypes = allBerryTypes;
            feedbackMsg = Component.translatable("message.berrypouch.no_bait_in_pouch");
        } else {
            player.sendSystemMessage(Component.translatable("message.berrypouch.no_bait_in_pouch"), true);
            return; // No bait at all
        }

        if (finalAvailableBerryTypes.isEmpty()) {
            player.sendSystemMessage(feedbackMsg, true); // Should ideally not happen if checks above are correct
            return;
        }

        // --- Handle Current Bait ---
        Item currentBaitItem = null;
        ItemStack currentBaitStackOnRod = PokerodItem.Companion.getBaitStackOnRod(heldStack);
        boolean returnedToPouch = false; // Flag to track if bait was put back

        if (!currentBaitStackOnRod.isEmpty()) {
            currentBaitItem = currentBaitStackOnRod.getItem();
            ItemStack baitToReturn = currentBaitStackOnRod.copy(); // Work with a copy

            // --- Attempt to return bait to pouch (modifying pouchItems list) ---
            // 1. Try stacking into marked slots first
            for (int markedIndex : markedSlots) {
                if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                    ItemStack stackInSlot = pouchItems.get(markedIndex);
                    if (ItemStack.isSameItemSameComponents(stackInSlot, baitToReturn) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                        stackInSlot.grow(1);
                        returnedToPouch = true;
                        break;
                    }
                }
            }
            // 2. Try stacking into any slot
            if (!returnedToPouch) {
                for (int i = 0; i < pouchItems.size(); i++) {
                    ItemStack stackInSlot = pouchItems.get(i);
                    if (ItemStack.isSameItemSameComponents(stackInSlot, baitToReturn) && stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                        stackInSlot.grow(1);
                        returnedToPouch = true;
                        break;
                    }
                }
            }
            // 3. Try placing into any empty marked slot
            if (!returnedToPouch) {
                for (int markedIndex : markedSlots) {
                    if (markedIndex >= 0 && markedIndex < pouchItems.size() && pouchItems.get(markedIndex).isEmpty()) {
                        pouchItems.set(markedIndex, baitToReturn); // Place the stack
                        returnedToPouch = true;
                        break;
                    }
                }
            }
            // 4. Try placing into any empty slot
            if (!returnedToPouch) {
                for (int i = 0; i < pouchItems.size(); i++) {
                    if (pouchItems.get(i).isEmpty()) {
                        pouchItems.set(i, baitToReturn);
                        returnedToPouch = true;
                        break;
                    }
                }
            }

            // --- If returned, clear bait from rod and save pouch changes ---
            if (returnedToPouch) {
                PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY); // Clear rod
                // Save the modified pouchItems list back to the component
                pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(pouchItems));
            } else {
                // Drop the item if it couldn't be placed back
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), baitToReturn);
                itemEntity.setPickUpDelay(10);
                level.addFreshEntity(itemEntity);
                player.sendSystemMessage(Component.translatable("message.berrypouch.bait_dropped", Component.translatable(currentBaitItem.getDescriptionId())), true);
                // Do NOT clear bait from rod here, as it wasn't put back
                currentBaitItem = null; // Act as if there was no bait for cycle calculation if dropped
            }
        }


        // --- Cycle to next bait ---
        int currentIndex = currentBaitItem != null ? finalAvailableBerryTypes.indexOf(currentBaitItem) : -1;
        if (currentIndex == -1 && !finalAvailableBerryTypes.isEmpty()){
            currentIndex = isLeftCycle ? 0 : finalAvailableBerryTypes.size() - 1 ; // Adjust start for left cycle
        }

        int nextIndex = isLeftCycle
                ? (currentIndex - 1 + finalAvailableBerryTypes.size()) % finalAvailableBerryTypes.size()
                : (currentIndex + 1) % finalAvailableBerryTypes.size();

        if (nextIndex < 0 || nextIndex >= finalAvailableBerryTypes.size()) {
            ModCommon.LOG.error("Error calculating next bait index. Current: {}, Next: {}, Size: {}", currentIndex, nextIndex, finalAvailableBerryTypes.size());
            return; // Avoid index out of bounds
        }

        Item nextBaitItem = finalAvailableBerryTypes.get(nextIndex);
        boolean deducted = false;

        // --- Deduct next bait from pouch (modifying pouchItems list) ---
        // 1. Try deducting from marked slots first
        for (int markedIndex : markedSlots) {
            if (markedIndex >= 0 && markedIndex < pouchItems.size()) {
                ItemStack stackInSlot = pouchItems.get(markedIndex);
                if (!stackInSlot.isEmpty() && stackInSlot.is(nextBaitItem)) {
                    stackInSlot.shrink(1);
                    // No need to set back to EMPTY if shrink makes it empty, NonNullList handles it.
                    deducted = true;
                    break;
                }
            }
        }
        // 2. Try deducting from any slot
        if (!deducted) {
            for (int i = 0; i < pouchItems.size(); i++) {
                ItemStack stackInSlot = pouchItems.get(i);
                if (!stackInSlot.isEmpty() && stackInSlot.is(nextBaitItem)) {
                    stackInSlot.shrink(1);
                    deducted = true;
                    break;
                }
            }
        }

        if (!deducted) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.berry_missing_after_selection"), true);
            // Maybe clear bait from rod if deduction failed unexpectedly?
            if (returnedToPouch) { // Only clear if we successfully put the old one back
                PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY);
            }
            // Save potential changes from returning the previous bait even if deduction fails
            pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(pouchItems));
            return;
        }

        // --- Set new bait and save pouch changes ---
        PokerodItem.Companion.setBait(heldStack, new ItemStack(nextBaitItem, 1));
        // Save the modified pouchItems list back to the component
        pouchStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(pouchItems));


        // --- Feedback and Sound ---
        player.sendSystemMessage(
                Component.translatable("message.berrypouch.switched_bait",
                        Component.translatable(nextBaitItem.getDescriptionId())),
                true
        );
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, 1.5f);

        // --- Sync Inventory ---
        // It seems broadcasting changes is necessary when modifying item components server-side
        player.inventoryMenu.broadcastChanges(); // Sync player inventory (which includes held items)
        if (player.containerMenu != player.inventoryMenu) { // Check if a different container is open
            player.containerMenu.broadcastChanges(); // Sync the currently open container too
        }
    }

    // --- Update handleToggleMarkSlot ---
    private static void handleToggleMarkSlot(ToggleMarkSlotPayload packet, NetworkManager.PacketContext context) {
        ServerPlayer player = (ServerPlayer) context.getPlayer();
        int slotIndex = packet.slotIndex();

        context.queue(() -> {
            if (player.containerMenu instanceof AbstractBerryPouchContainer pouchContainer) {
                ItemStack pouchStack = pouchContainer.getPouchStack(); // Get the actual ItemStack from the container
                if (!pouchStack.isEmpty() && pouchStack.getItem() instanceof BerryPouch) {
                    BerryPouchType pouchType = ((BerryPouch) pouchStack.getItem()).getPouchType();

                    if (slotIndex >= 0 && slotIndex < pouchType.getSize()) {
                        // Use the new helper to toggle the mark on the ItemStack's component
                        MarkedSlotsHelper.toggleMarkedSlot(pouchStack, slotIndex);

                        player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1.2f);

                        // Important: Broadcast changes AFTER modifying components server-side
                        // This ensures the client's view of the ItemStack (including its components) is updated.
                        pouchContainer.broadcastChanges();

                    } else {
                        ModCommon.LOG.warn("Player {} tried to toggle invalid slot index {} for pouch {}", player.getName().getString(), slotIndex, pouchStack.getHoverName().getString());
                    }
                } else {
                    ModCommon.LOG.warn("Player {} sent ToggleMarkSlotPacket but the container's pouchStack is invalid", player.getName().getString());
                }
            } else {
                ModCommon.LOG.warn("Player {} sent ToggleMarkSlotPacket but is not in a BerryPouchContainer", player.getName().getString());
            }
        });
    }

    // --- 修改: 发送打开背包的请求 (如果需要，虽然你的代码是从 KeyBindingManager 发起的) ---
    public static void sendOpenPouchPacketToServer() {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().getConnection() != null) {
            NetworkManager.sendToServer(new OpenPouchPayload());
        } else {
            ModCommon.LOG.warn("Attempted to send OpenPouchPayload when not in a valid client context!");
        }
    }

    // --- 新增: 客户端发送切换标记状态的请求 ---
    public static void sendToggleMarkSlotPacketToServer(int slotIndex) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().getConnection() != null) {
            NetworkManager.sendToServer(new ToggleMarkSlotPayload(slotIndex));
        } else {
            ModCommon.LOG.warn("Attempted to send ToggleMarkSlotPayload when not in a valid client context!");
        }
    }
}