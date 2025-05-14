package net.pocketbeast.battleteleporter.network.packages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.pocketbeast.battleteleporter.hud.HologramLifetime;

import java.util.function.Supplier;

public record HologramLifetimePackage(int lifeTimeTicks) {

    public static void encode(HologramLifetimePackage pkg, FriendlyByteBuf buffer) {
        buffer.writeInt(pkg.lifeTimeTicks);
    }

    public static HologramLifetimePackage decode(FriendlyByteBuf buffer) {
        return new HologramLifetimePackage( buffer.readInt() );
    }

    public static void handle(HologramLifetimePackage pkg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HologramLifetime.showHUD(pkg.lifeTimeTicks);
        });
        ctx.get().setPacketHandled(true);
    }

}
