package net.pocketbeast.battleteleporter.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.network.packages.HologramCanBeDisabledPackage;
import net.pocketbeast.battleteleporter.network.packages.HologramLifetimePackage;

public class NetworkHandler {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(BattleTeleporterMod.MOD_ID, "main"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(HologramLifetimePackage.class, 0)
                .encoder(HologramLifetimePackage::encode)
                .decoder(HologramLifetimePackage::decode)
                .consumerMainThread(HologramLifetimePackage::handle)
                .add();

        CHANNEL.messageBuilder(HologramCanBeDisabledPackage.class, 1)
                .encoder(HologramCanBeDisabledPackage::encode)
                .decoder(HologramCanBeDisabledPackage::decode)
                .consumerMainThread(HologramCanBeDisabledPackage::handle)
                .add();
    }

}
