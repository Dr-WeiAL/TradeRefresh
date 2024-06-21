package dev.xkmc.traderefresh.network;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.traderefresh.init.TRConfig;
import dev.xkmc.traderefresh.mixin.VillagerAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraftforge.network.NetworkEvent;

@SerialClass
public class RefreshToServer extends SerialPacketBase {

	@Override
	public void handle(NetworkEvent.Context context) {
		ServerPlayer player = context.getSender();
		if (player == null) return;
		if (!(player.containerMenu instanceof MerchantMenu gui)) return;
		if (!(gui.trader instanceof Villager villager)) return;
		if (TRConfig.COMMON.alwaysAllowRefresh.get()) {
			player.doCloseContainer();
			var offer = villager.getOffers();
			offer.remove(offer.size() - 1);
			if (offer.size() % 2 != 0) offer.remove(offer.size() - 1);
			villager.setOffers(offer);
			((VillagerAccessor) villager).callUpdateTrades();
			villager.startTrading(player);
		} else {
			if (villager.getVillagerXp() > 0) return;
			player.doCloseContainer();
			villager.setOffers(null);
			villager.startTrading(player);
		}
	}

}
