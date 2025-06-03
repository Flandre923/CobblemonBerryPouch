package com.github.flandre923.berrypouch.neoforge;

import com.github.flandre923.berrypouch.ModCommon;
import com.github.flandre923.berrypouch.ModRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

@EventBusSubscriber(modid = ModCommon.MOD_ID,bus = EventBusSubscriber.Bus.MOD)
public class InventorySorterIntegration {

	@SubscribeEvent
	private static void sendImc(InterModEnqueueEvent evt) {
		InterModComms.sendTo("inventorysorter", "containerblacklist",
				() -> BuiltInRegistries.MENU.getKey(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_30.get()));
		InterModComms.sendTo("inventorysorter", "containerblacklist",
				() -> BuiltInRegistries.MENU.getKey(ModRegistries.ModMenuTypes.BERRY_POUCH_CONTAINER_69.get()));
	}
}
