package net.pocketbeast.battleteleporter.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.PacketDistributor;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.network.NetworkHandler;
import net.pocketbeast.battleteleporter.network.packages.HologramLifetimePackage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HologramEntity extends AbstractGolem {
    private static final HashMap<Player, HologramEntity> playersHolograms = new HashMap<>();

    private Player owner;
    private int remainingLifeTicks = 600;
    private double maxDistance = 100.0;

    public HologramEntity(EntityType<? extends AbstractGolem> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.FOLLOW_RANGE, 10.0);
    }

    public static void addNewHologramToPlayer(Player player, HologramEntity hologram) {
        playersHolograms.put(player, hologram);
        hologram.suitUpLikeOwner();
    }

    public static HologramEntity getHologramOfPlayer(Player player) {
        return playersHolograms.get(player);
    }

    public static void deletePlayersHologram(Player player) {
        HologramEntity hologram = playersHolograms.get(player);
        if (hologram != null) {
            if (hologram.isAlive()) {
                hologram.kill();
            }

            playersHolograms.remove(player);
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new HologramLifetimePackage(0)
            );
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        deletePlayersHologram( getOwner() );
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isAlive()) {
            Player owner = getOwner();

            if (owner == null) {
                this.kill();
                return;
            }

            if (remainingLifeTicks <= 0) {
                deletePlayersHologram(owner);
                owner.sendSystemMessage(Component.translatable(
                        "message." + BattleTeleporterMod.MOD_ID + "." + "hologram_died_because_of_time_expired"
                ));
                return;
            }

            if ( ownerToFar() ) {
                deletePlayersHologram(owner);
                owner.sendSystemMessage(Component.translatable(
                        "message." + BattleTeleporterMod.MOD_ID + "." + "hologram_died_because_of_distance"
                ));
                return;
            }

            remainingLifeTicks--;

            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) owner),
                    new HologramLifetimePackage(getRemainingLifeTicks())
            );
        }
    }

    private void suitUpLikeOwner() {
        Player owner = this.getOwner();
        if (owner != null) {
            ItemStack itemInHand = owner.getItemInHand(InteractionHand.MAIN_HAND);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemInHand);

            ItemStack helmet = owner.getItemBySlot(EquipmentSlot.HEAD);
            ItemStack chestplate = owner.getItemBySlot(EquipmentSlot.CHEST);
            ItemStack pants = owner.getItemBySlot(EquipmentSlot.LEGS);
            ItemStack boots = owner.getItemBySlot(EquipmentSlot.FEET);

            this.setItemSlot(EquipmentSlot.HEAD, helmet);
            this.setItemSlot(EquipmentSlot.CHEST, chestplate);
            this.setItemSlot(EquipmentSlot.LEGS, pants);
            this.setItemSlot(EquipmentSlot.FEET, boots);

            this.setDropChance(EquipmentSlot.MAINHAND, 0);
            this.setDropChance(EquipmentSlot.HEAD, 0);
            this.setDropChance(EquipmentSlot.CHEST, 0);
            this.setDropChance(EquipmentSlot.LEGS, 0);
            this.setDropChance(EquipmentSlot.FEET, 0);
        }
    }

    public Player getOwner() {
        Player owner = null;
        if (this.owner == null) {
            for (Map.Entry<Player, HologramEntity> entry : playersHolograms.entrySet()) {
                if (Objects.equals(this, entry.getValue())) {
                    owner = entry.getKey();
                    this.owner = entry.getKey();
                }
            }
        } else {
            owner = this.owner;
        }

        return owner;
    }

    public int getRemainingLifeTicks() {
        return remainingLifeTicks;
    }

    public boolean ownerToFar() {
        Player owner = getOwner();
        if (owner == null) return true;

        if( Math.abs(this.getX() - owner.getX()) > maxDistance) return true;
        if( Math.abs(this.getY() - owner.getY()) > maxDistance) return true;
        if( Math.abs(this.getZ() - owner.getZ()) > maxDistance) return true;

        return false;
    }

}
