package com.github.flandre923.berrypouch.network;

import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.helper.MarkedSlotsHelper;
import com.github.flandre923.berrypouch.helper.PouchDataHelper;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.PokeBallBelt;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchType;
import com.github.flandre923.berrypouch.item.pouch.PokeBallBeltHelper;
import com.github.flandre923.berrypouch.menu.container.AbstractBerryPouchContainer;
import dev.architectury.networking.NetworkManager;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
                    context.queue(() -> handleCycleRequest(player, packet.isMainHand(), packet.isLeftCycle()));
                }
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                ToggleMarkSlotPayload.TYPE,
                ToggleMarkSlotPayload.CODEC,
                ModNetworking::handleToggleMarkSlot // 新的处理方法
        );

        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            ToggleAutoBerryPayload.TYPE,
            ToggleAutoBerryPayload.CODEC,
            (packet, context) -> {
                ServerPlayer player = (ServerPlayer) context.getPlayer();
                context.queue(() -> handleToggleAutoBerry(player));
            }
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


    private static void handleCycleRequest(ServerPlayer player, boolean isMainHand, boolean isLeftCycle) {
        Level level = player.level();
        InteractionHand hand = isMainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack heldStack = player.getItemInHand(hand); // Rod ItemStack
        // 精灵球腰带 - 切换选中的index
        if (heldStack.getItem() instanceof PokeBallBelt) {
            handleCyclePokeBallBelt(player, heldStack, isLeftCycle);
            return;
        }
        // 钓竿 - 切换鱼饵
        if (FishingRodEventHandler.isCobblemonFishingRod(heldStack)) {
            handleCycleBaitRequest(player, heldStack, isLeftCycle);
            return;
        }

        // 都不是，提示玩家
        player.sendSystemMessage(Component.translatable("message.berrypouch.not_holding_valid_item"), true);


    }

    private static void handleCyclePokeBallBelt(ServerPlayer player, ItemStack beltStack, boolean isLeftCycle) {
        if (isLeftCycle) {
            PokeBallBeltHelper.cyclePrev(beltStack);
        } else {
            PokeBallBeltHelper.cycleNext(beltStack);
        }

        // 获取新选中的物品名称用于提示
        ItemStack selectedItem = PokeBallBeltHelper.getSelectedItem(beltStack);

        if (!selectedItem.isEmpty()) {
            player.sendSystemMessage(
                    Component.translatable("message.berrypouch.switched_pokeball", selectedItem.getHoverName()),
                    true
            );
        } else {
            player.sendSystemMessage(
                    Component.translatable("message.berrypouch.slot_empty", PokeBallBeltHelper.getSelectedIndex(beltStack) + 1),
                    true
            );
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, 1.5f);
    }

    private static void handleCycleBaitRequest(ServerPlayer player, ItemStack heldStack, boolean isLeftCycle) {
        Level level = player.level();

        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) return;

        Optional<SlotEntryReference> pouchRefOpt = Optional.ofNullable(capability.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch));
        if (pouchRefOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.need_pouch"), true);
            return;
        }

        ItemStack pouchStack = pouchRefOpt.get().stack(); // The Berry Pouch ItemStack
        if (!(pouchStack.getItem() instanceof BerryPouch pouchItem)) {
            return; // Should not happen
        }

        // --- Get Pouch Inventory and Marked Slots ---
        List<Integer> markedSlots = MarkedSlotsHelper.getMarkedSlots(pouchStack);
        boolean preferMarked = !markedSlots.isEmpty(); // Player's intent: cycle marked if possible

        SimpleContainer pouchItems = BerryPouchManager.getInventory(pouchStack,level);
        // --- Determine Available Bait *Types* based on intent and *current* availability ---
        List<Item> availableMarkedItemTypes = new ArrayList<>();
        if (preferMarked) {
            for (int markedIndex : markedSlots) {
                if (markedIndex >= 0 && markedIndex < pouchItems.getContainerSize()) {
                    ItemStack stackInSlot = pouchItems.getItem(markedIndex);
                    // Check if it's a non-empty berry and the type isn't already added
                    if (!stackInSlot.isEmpty() && FishingRodEventHandler.isCobblemonBerry(stackInSlot) && !availableMarkedItemTypes.contains(stackInSlot.getItem())) {
                        availableMarkedItemTypes.add(stackInSlot.getItem());
                    }
                }
            }
        }

        // --- Determine *all* available bait types (still needed for fallback if not preferring marked) ---
        List<Item> allAvailableItemTypes = new ArrayList<>();
        for (ItemStack stackInSlot : pouchItems.getItems()) {
            if (!stackInSlot.isEmpty() && FishingRodEventHandler.isCobblemonBerry(stackInSlot) && !allAvailableItemTypes.contains(stackInSlot.getItem())) {
                allAvailableItemTypes.add(stackInSlot.getItem());
            }
        }

        // --- Handle Returning Current Bait (Needs to happen before the new check) ---
        Item currentBaitItem = null;
        ItemStack currentBaitStackOnRod = PokerodItem.Companion.getBaitStackOnRod(heldStack);
        boolean returnedToPouch = false;
        if (!currentBaitStackOnRod.isEmpty()) {
            currentBaitItem = currentBaitStackOnRod.getItem();
            ItemStack baitToReturn = currentBaitStackOnRod.copy();

            for (int i =0;i< pouchItems.getContainerSize();i++){
                int space = pouchItems.getItem(i).getMaxStackSize() - pouchItems.getItem(i).getCount();
                if(pouchItems.canPlaceItem(i,baitToReturn) && space >0){
                    pouchItems.getItem(i).grow(1);
                    returnedToPouch =true;
                }
            }

            if (returnedToPouch) {
                PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY); // Clear rod bait IF returned
            } else {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(), baitToReturn);
                itemEntity.setPickUpDelay(10);
                level.addFreshEntity(itemEntity);
                player.sendSystemMessage(Component.translatable("message.berrypouch.bait_dropped", Component.translatable(currentBaitItem.getDescriptionId())), true);
                PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY);
            }
        }

        if (preferMarked && availableMarkedItemTypes.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.marked_bait_exhausted_switching_none"), true); // Use a new message
            PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY);
            if (returnedToPouch) {
                pouchItems.setChanged();
            }
            player.inventoryMenu.broadcastChanges();
            if (player.containerMenu != player.inventoryMenu) {
                player.containerMenu.broadcastChanges();
            }
            return;
        }

        List<Item> finalAvailableBerryTypes;
        boolean cyclingMarked = false;

        if (preferMarked) {
            finalAvailableBerryTypes = availableMarkedItemTypes;
            cyclingMarked = true;
        } else if (!allAvailableItemTypes.isEmpty()){
            finalAvailableBerryTypes = allAvailableItemTypes;
        } else {
            player.sendSystemMessage(Component.translatable("message.berrypouch.no_bait_in_pouch"), true);
            if (returnedToPouch) {
                pouchItems.setChanged();
            }
            return;
        }


        int currentIndex = currentBaitItem != null ? finalAvailableBerryTypes.indexOf(currentBaitItem) : -1;
        if (currentIndex == -1 && !finalAvailableBerryTypes.isEmpty()){
            currentIndex = isLeftCycle ? 0 : finalAvailableBerryTypes.size() - 1 ;
        } else if (finalAvailableBerryTypes.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.no_bait_in_pouch"), true);
            if (returnedToPouch) {
                pouchItems.setChanged();
            }
            return;
        }


        int nextIndex = isLeftCycle
                ? (currentIndex - 1 + finalAvailableBerryTypes.size()) % finalAvailableBerryTypes.size()
                : (currentIndex + 1) % finalAvailableBerryTypes.size();

        if (nextIndex < 0 || nextIndex >= finalAvailableBerryTypes.size()) {
            ModCommon.LOG.error("Error calculating next bait index. Current: {}, Next: {}, Size: {}", currentIndex, nextIndex, finalAvailableBerryTypes.size());
            if (returnedToPouch) { // Save potential changes
                pouchItems.setChanged();
            }
            return;
        }

        Item nextBaitItem = finalAvailableBerryTypes.get(nextIndex);
        boolean deducted = false;

        if (cyclingMarked) {
            // Requirement 1: Only deduct from MARKED slots if that's the mode
            for (int markedIndex : markedSlots) {
                if (markedIndex >= 0 && markedIndex < pouchItems.getContainerSize()) {
                    ItemStack stackInSlot = pouchItems.getItem(markedIndex);
                    if (!stackInSlot.isEmpty() && stackInSlot.is(nextBaitItem)) {
                        stackInSlot.shrink(1);
                        deducted = true;
                        break; // Deducted one, stop searching marked slots
                    }
                }
            }
        } else {
            // Standard behavior: Deduct from any slot
            for (int i = 0; i < pouchItems.getContainerSize(); i++) {
                ItemStack stackInSlot = pouchItems.getItem(i);
                if (!stackInSlot.isEmpty() && stackInSlot.is(nextBaitItem)) {
                    stackInSlot.shrink(1);
                    deducted = true;
                    break; // Deducted one, stop searching all slots
                }
            }
        }


        // --- Handle Deduction Result ---
        if (!deducted) {
            // If deduction failed, it should have been caught by the availability checks
            // or the earlier `preferMarked && availableMarkedItemTypes.isEmpty()` check.
            // This path suggests an inconsistency, possibly if the bait was removed between checks.
            ModCommon.LOG.warn("Failed to deduct bait {} even though it was expected to be available.", nextBaitItem);
            player.sendSystemMessage(Component.translatable("message.berrypouch.berry_missing_after_selection"), true);

            // Save changes if bait was returned earlier
            if (returnedToPouch) {
                pouchItems.setChanged();
            }
            // Ensure rod is clear since deduction failed
            PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY);
            player.inventoryMenu.broadcastChanges();
            if (player.containerMenu != player.inventoryMenu) {
                player.containerMenu.broadcastChanges();
            }
            return;
        }

        // --- Success: Set new bait and save pouch changes ---
        PokerodItem.Companion.setBait(heldStack, new ItemStack(nextBaitItem, 1));
        PouchDataHelper.setLastUsedBait(pouchStack, nextBaitItem);
        pouchItems.setChanged();

        // --- Feedback and Sound ---
        player.sendSystemMessage(
                Component.translatable("message.berrypouch.switched_bait",
                        Component.translatable(nextBaitItem.getDescriptionId())),
                true
        );
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, 1.5f);

        // --- Sync Inventory ---
        player.inventoryMenu.broadcastChanges();
        if (player.containerMenu != player.inventoryMenu) {
            player.containerMenu.broadcastChanges();
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

    private static void handleToggleAutoBerry(ServerPlayer player) {
        // 切换自动填充状态逻辑
        boolean oldState = PouchDataHelper.isAutoBerryEnabled(player);
        boolean newState = !oldState;
        PouchDataHelper.setAutoBerryEnabled(player, newState);

        if(oldState){
            Level level = player.level();
            InteractionHand[] hands ={InteractionHand.MAIN_HAND, InteractionHand.OFF_HAND};
            for (InteractionHand hand : hands) {
                ItemStack heldStack = player.getItemInHand(hand);
                if(FishingRodEventHandler.isCobblemonFishingRod(heldStack)){
                    ItemStack currentBait =PokerodItem.Companion.getBaitStackOnRod(heldStack);
                    BerryPouch.onPickupItem(currentBait,player);
                    if (!currentBait.isEmpty()) {
                        PokerodItem.Companion.setBait(heldStack, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }

        Component message = Component.translatable("message.berrypouch.auto_berry_toggled." + newState);
        player.sendSystemMessage(message, true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), 
            SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, 1.2f);
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