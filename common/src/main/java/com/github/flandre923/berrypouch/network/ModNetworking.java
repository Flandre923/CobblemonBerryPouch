package com.github.flandre923.berrypouch.network;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.item.BerryPouch;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled; // For creating an empty buffer
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level; // Import Level
import net.minecraft.sounds.SoundEvents; // Import SoundEvents
import net.minecraft.sounds.SoundSource; // Import SoundSource


public class ModNetworking {


    public static void register() {
        // 注册服务器端接收器 (C2S = Client to Server)
        // 当服务器收到 ID 为 OPEN_POUCH_PACKET_ID 的包时，调用 handleOpenPouch 方法
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,             // Side (Client -> Server)
                OpenPouchPayload.TYPE,               // The Payload Type constant
                OpenPouchPayload.CODEC,              // The StreamCodec for this payload
                ModNetworking::handleOpenPouch       // The handler method reference
        );    }

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
                        if (!pouchStack.isEmpty() && pouchStack.getItem() instanceof BerryPouch pouchItem) {
                            // Play sound on the server
                            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1F + 0.9F);
                            // Use the helper method to open the GUI
                            BerryPouch.openPouchGUI(player, pouchStack, pouchItem,InteractionHand.MAIN_HAND);
                        }
                    });
        });
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