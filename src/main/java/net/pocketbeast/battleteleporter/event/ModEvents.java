package net.pocketbeast.battleteleporter.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;
import net.pocketbeast.battleteleporter.network.NetworkHandler;
import net.pocketbeast.battleteleporter.network.packages.HologramLifetimePackage;

@Mod.EventBusSubscriber(modid = BattleTeleporterMod.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player != null) {
            HologramEntity.deletePlayersHologram(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player != null) {

            // чтобы при заходе в мир, очищался HUD голограммы
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new HologramLifetimePackage(0)
            );

        }
    }

}
