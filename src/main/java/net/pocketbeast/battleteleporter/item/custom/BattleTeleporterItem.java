package net.pocketbeast.battleteleporter.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.pocketbeast.battleteleporter.entity.ModEntities;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;
import net.pocketbeast.battleteleporter.network.NetworkHandler;
import net.pocketbeast.battleteleporter.network.packages.HologramCanBeDisabledPackage;
import net.pocketbeast.battleteleporter.sound.ModSounds;

public class BattleTeleporterItem extends Item {
    private static final int USE_TIME_TO_DISABLE = 40;

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

    private void makePoofAround(Level pLevel, LivingEntity entity, int poofCount) {
        if (!pLevel.isClientSide() && entity != null) {
            RandomSource random = RandomSource.create();
            ServerLevel serverLevel = (ServerLevel) pLevel;
            serverLevel.sendParticles(
                    ParticleTypes.POOF,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    poofCount,
                    Mth.nextDouble(random, -1.5, 1.5),
                    Mth.nextDouble(random, 0, 1.5),
                    Mth.nextDouble(random, -1.5, 1.5),
                    0.05d
            );
        }
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack teleporter = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);
        return InteractionResultHolder.sidedSuccess(teleporter, pLevel.isClientSide());
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        Player pPlayer = (Player) pLivingEntity;

        if (!pLevel.isClientSide()) {
            HologramEntity playersHologram = HologramEntity.getHologramOfPlayer(pPlayer);

            boolean hologramEnabled = playersHologram != null && playersHologram.isAlive();
            int timeUsed = this.getUseDuration(pStack) - pTimeCharged;
            if (hologramEnabled && timeUsed < USE_TIME_TO_DISABLE) {
                swapWithHologram(pLevel, pPlayer);
                pPlayer.getCooldowns().addCooldown(this, 10);
                pLevel.playSeededSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HOLOGRAM_SWAP.get(), SoundSource.BLOCKS, 0.5f, 1, 0);
                makePoofAround(pLevel, playersHologram, 50);

            } else if (hologramEnabled) {
                playersHologram.disable();
                pPlayer.getCooldowns().addCooldown(this, 60);
                pLevel.playSeededSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HOLOGRAM_DISABLE.get(), SoundSource.BLOCKS, 0.5f, 1, 0);

            } else {
                spawnHologram(pLevel, pPlayer);
                pPlayer.getCooldowns().addCooldown(this, 30);
                pLevel.playSeededSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), ModSounds.HOLOGRAM_CREATE.get(), SoundSource.BLOCKS, 0.5f, 1, 0);
                makePoofAround(pLevel, pPlayer, 50);

            }
        }

        pStack.hurtAndBreak(
                1,
                pPlayer,
                player -> {
                    player.broadcastBreakEvent(pPlayer.getUsedItemHand());
                }
        );
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (!pLevel.isClientSide()) {
            Player player = (Player) pLivingEntity;
            HologramEntity playersHologram = HologramEntity.getHologramOfPlayer(player);

            boolean hologramEnabled = playersHologram != null && playersHologram.isAlive();
            if (!hologramEnabled) return;

            int timeUsed = this.getUseDuration(pStack) - pRemainingUseDuration;

            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new HologramCanBeDisabledPackage(timeUsed >= USE_TIME_TO_DISABLE)
            );
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 7200;
    }
}
