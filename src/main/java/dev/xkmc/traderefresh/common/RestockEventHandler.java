package dev.xkmc.traderefresh.common;

import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.init.TradeRefresh;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


@EventBusSubscriber(modid = TradeRefresh.MODID, bus = EventBusSubscriber.Bus.GAME)
public class RestockEventHandler {

	@SubscribeEvent
	public static void onMobInteract(PlayerInteractEvent.EntityInteract event) {
		if (!TRConfig.SERVER.allowEmeraldBlockForceRestock.get()) return;
		if (event.getItemStack().is(Items.EMERALD_BLOCK) && event.getTarget() instanceof Villager villager) {
			if (event.getLevel().isClientSide()) {
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
				return;
			}
			if (villager.getVillagerXp() > 0 && villager.needsToRestock()) {
				villager.resetNumberOfRestocks();
				villager.restock();
				event.getItemStack().shrink(1);
				event.setCancellationResult(InteractionResult.CONSUME);
				event.setCanceled(true);
				event.getLevel().broadcastEntityEvent(villager, EntityEvent.VILLAGER_HAPPY);
				villager.playSound(SoundEvents.VILLAGER_YES, 1, villager.getVoicePitch());
			} else {
				event.setCancellationResult(InteractionResult.FAIL);
				event.setCanceled(true);
				villager.setUnhappyCounter(40);
				villager.playSound(SoundEvents.VILLAGER_NO, 1, villager.getVoicePitch());
			}
		}
	}

}
