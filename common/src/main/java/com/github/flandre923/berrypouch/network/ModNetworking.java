package com.github.flandre923.berrypouch.network;

import com.cobblemon.mod.common.item.interactive.PokerodItem;
import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.event.FishingRodEventHandler;
import com.github.flandre923.berrypouch.item.BerryPouch;
import com.github.flandre923.berrypouch.item.pouch.BerryPouchManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled; // For creating an empty buffer
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
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
    private static void handleCycleBaitRequest(ServerPlayer player, boolean isMainHand, boolean isLeftCycle) {
        Level level = player.level();
        ItemStack heldStack = isMainHand ? player.getMainHandItem() : player.getOffhandItem();

        // 1. 验证是否持有Cobblemon钓竿
        if (!FishingRodEventHandler.isCobblemonFishingRod(heldStack)) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.not_holding_rod"), true);
            return;
        }

        // 2. 获取饰品栏中的树果袋
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.capability_missing"), true);
            return;
        }

        Optional<SlotEntryReference> pouchRefOpt = Optional.ofNullable(capability.getFirstEquipped(stack -> stack.getItem() instanceof BerryPouch));
        if (pouchRefOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.need_pouch"), true);
            return;
        }

        // 3. 获取树果袋库存
        ItemStack pouchStack = pouchRefOpt.get().stack();
        SimpleContainer pouchInventory = BerryPouchManager.getInventory(pouchStack, level);

        // 4. 收集所有不同类型的Cobblemon树果
        List<Item> availableBerryTypes = new ArrayList<>();
        for (int i = 0; i < pouchInventory.getContainerSize(); i++) {
            ItemStack stackInSlot = pouchInventory.getItem(i);
            if (!stackInSlot.isEmpty() && FishingRodEventHandler.isCobblemonBerry(stackInSlot)) {
                if (!availableBerryTypes.contains(stackInSlot.getItem())) {
                    availableBerryTypes.add(stackInSlot.getItem());
                }
            }
        }

        if (availableBerryTypes.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.no_bait_in_pouch"), true);
            return;
        }

        // 5. 获取当前钓竿上的诱饵
        Item currentBaitItem = null;
        ItemStack currentBaitStack = PokerodItem.Companion.getBaitStackOnRod(heldStack);

        // 6. 如果当前有诱饵，先返还到树果袋
        if (!currentBaitStack.isEmpty()) {
            currentBaitItem = currentBaitStack.getItem();
            boolean returned = false;

            // 尝试堆叠到已有物品
            for (int i = 0; i < pouchInventory.getContainerSize(); i++) {
                ItemStack stackInSlot = pouchInventory.getItem(i);
                if (!stackInSlot.isEmpty() && ItemStack.isSameItem(stackInSlot, currentBaitStack)) {
                    if (stackInSlot.getCount() < stackInSlot.getMaxStackSize()) {
                        stackInSlot.grow(1);
                        returned = true;
                        break;
                    }
                }
            }

            // 如果仍然无法放入，掉落物品到世界
            if (!returned) {
                ItemEntity itemEntity = new ItemEntity(
                        level,
                        player.getX(),
                        player.getY() + player.getEyeHeight(),
                        player.getZ(),
                        new ItemStack(currentBaitItem, 1)
                );
                itemEntity.setPickUpDelay(10); // 设置10ticks的拾取延迟
                level.addFreshEntity(itemEntity);
                player.sendSystemMessage(
                        Component.translatable("message.berrypouch.bait_dropped",
                                Component.translatable(currentBaitItem.getDescriptionId())),
                        true
                );
            }
        }
        int currentIndex = currentBaitItem != null ? availableBerryTypes.indexOf(currentBaitItem) : -1;
        int nextIndex = isLeftCycle ? (currentIndex - 1 + availableBerryTypes.size()) % availableBerryTypes.size() : (currentIndex + 1) % availableBerryTypes.size();
        Item nextBaitItem = availableBerryTypes.get(nextIndex);
        boolean deducted = false;

        for (int i = 0; i < pouchInventory.getContainerSize(); i++) {
            ItemStack stackInSlot = pouchInventory.getItem(i);
            if (!stackInSlot.isEmpty() && stackInSlot.getItem() == nextBaitItem) {
                stackInSlot.shrink(1);
                if (stackInSlot.isEmpty()) {
                    pouchInventory.setItem(i, ItemStack.EMPTY);
                }
                deducted = true;
                break;
            }
        }

        if (!deducted) {
            player.sendSystemMessage(Component.translatable("message.berrypouch.berry_missing"), true);
            return;
        }

        // 9. 设置新诱饵
        PokerodItem.Companion.setBait(heldStack, new ItemStack(nextBaitItem, 1));
        pouchInventory.setChanged();

        // 10. 反馈和音效
        player.sendSystemMessage(
                Component.translatable("message.berrypouch.switched_bait",
                        Component.translatable(nextBaitItem.getDescriptionId())),
                true
        );
        player.level().playSound(
                null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.5f, 1.5f
        );

        // 11. 同步物品更新
        if (isMainHand) {
            player.containerMenu.broadcastChanges();
        } else {
            player.inventoryMenu.broadcastChanges();
        }
    }


    // 客户端调用此方法发送数据包
    public static void sendOpenPouchPacketToServer() {
        // It's crucial to ensure this is only called on the client side
        // A simple check:
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().getConnection() != null) {
            // Create an instance of the payload and send it
            NetworkManager.sendToServer(new OpenPouchPayload());
        } else {
            // Log a warning if called inappropriately, helps debugging
            ModCommon.LOG.warn("Attempted to send OpenPouchPayload when not in a valid client context!");
        }
    }
}