package net.pocketbeast.battleteleporter.network.packages;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.pocketbeast.battleteleporter.hud.HologramLifetime;

import java.util.function.Supplier;

public record HologramCanBeDisabledPackage(boolean canBeDisabled) {

    public static void encode(HologramCanBeDisabledPackage pkg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(pkg.canBeDisabled);
    }

    public static HologramCanBeDisabledPackage decode(FriendlyByteBuf buffer) {
        return new HologramCanBeDisabledPackage( buffer.readBoolean() );
    }

    public static void handle(HologramCanBeDisabledPackage pkg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HologramLifetime.updateCanBeDisabled(pkg.canBeDisabled);
        });
        ctx.get().setPacketHandled(true);
    }

}
