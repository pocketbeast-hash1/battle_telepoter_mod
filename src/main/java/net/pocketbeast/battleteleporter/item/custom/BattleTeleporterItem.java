package net.pocketbeast.battleteleporter.item.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pocketbeast.battleteleporter.entity.ModEntities;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

public class BattleTeleporterItem extends Item {
    public BattleTeleporterItem(Properties pProperties) {
        super(pProperties);
    }

    private void spawnHologram(Level level, Player player) {
       HologramEntity hologram = ModEntities.HOLOGRAM.get().spawn(
                (ServerLevel) level,
                (ItemStack) null,
                null,
                player.getOnPos(),
                MobSpawnType.EVENT,
                true,
                true
        );
        HologramEntity.addNewHologramToPlayer(player, hologram);
    }

    private void swapWithHologram(Level level, Player player) {
        HologramEntity hologram = HologramEntity.getHologramOfPlayer(player);

        double hologramX = hologram.getX();
        double hologramY = hologram.getY();
        double hologramZ = hologram.getZ();
        float hologramXRot = hologram.getXRot();
        float hologramYRot = hologram.getYRot();

        hologram.teleportTo(
                (ServerLevel) level,
                player.getX(),
                player.getY(),
                player.getZ(),
                RelativeMovement.ROTATION,
                player.getYRot(),
                player.getXRot()
        );
        player.teleportTo(
                (ServerLevel) level,
                hologramX,
                hologramY,
                hologramZ,
                RelativeMovement.ROTATION,
                hologramYRot,
                hologramXRot
        );
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack teleporter = pPlayer.getItemInHand(pHand);

        pPlayer.getCooldowns().addCooldown(this, 30);

        if (!pLevel.isClientSide()) {
            HologramEntity playersHologram = HologramEntity.getHologramOfPlayer(pPlayer);
            if (playersHologram != null && playersHologram.isAlive()) {
                swapWithHologram(pLevel, pPlayer);
            } else {
                spawnHologram(pLevel, pPlayer);
            }
        }

        teleporter.hurtAndBreak(
                1,
                pPlayer,
                player -> {
                    player.broadcastBreakEvent(pHand);
                }
        );
        return InteractionResultHolder.sidedSuccess(teleporter, pLevel.isClientSide());
    }
}
