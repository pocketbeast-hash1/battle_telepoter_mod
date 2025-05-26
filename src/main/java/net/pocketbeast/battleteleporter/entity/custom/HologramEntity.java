package net.pocketbeast.battleteleporter.entity.custom;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.custom.ai.HurtByTargetHologramGoal;
import net.pocketbeast.battleteleporter.entity.custom.ai.OwnerHurtByTargetHologramGoal;
import net.pocketbeast.battleteleporter.entity.custom.ai.OwnerHurtTargetHologramGoal;
import net.pocketbeast.battleteleporter.network.NetworkHandler;
import net.pocketbeast.battleteleporter.network.packages.HologramLifetimePackage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class HologramEntity extends Mob implements RangedAttackMob {
    private static final HashMap<UUID, HologramEntity> playersHolograms = new HashMap<>();

    private static final EntityDataAccessor<String> OWNER_ID =
            SynchedEntityData.defineId(HologramEntity.class, EntityDataSerializers.STRING);

    public int remainingLifeTicks = 600;
    private double maxDistance = 100.0;

    public HologramEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0001)
                .add(Attributes.FOLLOW_RANGE, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5);
    }

    public static void addNewHologramToPlayer(@NotNull Player player, HologramEntity hologram) {
        playersHolograms.put(player.getUUID(), hologram);
        hologram.setOwnerId(player.getUUID());
        hologram.suitUpLikeOwner();
    }

    public static HologramEntity getHologramOfPlayer(@NotNull Player player) {
        return playersHolograms.get(player.getUUID());
    }

    public static void deletePlayersHologram(@NotNull Player player) {
        HologramEntity hologram = playersHolograms.get(player.getUUID());
        if (hologram != null) {
            if (hologram.isAlive()) {
                hologram.kill();
            }

            playersHolograms.remove(player.getUUID());
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new HologramLifetimePackage(0)
            );
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_ID, "");
    }

    private void setOwnerId(UUID uuid) {
        String strUUID = uuid.toString();
        this.entityData.set(OWNER_ID, strUUID);
    }

    public UUID getOwnerId() {
        String strUUID = this.entityData.get(OWNER_ID);
        if (strUUID.isEmpty())
            return null;
        else
            return UUID.fromString(strUUID);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 1.0d, 15, 60.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtTargetHologramGoal(this, true));
        this.targetSelector.addGoal(2, new OwnerHurtByTargetHologramGoal(this, true));
        this.targetSelector.addGoal(3, new HurtByTargetHologramGoal(this, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                this,
                Mob.class,
                1,
                true,
                true,
                (enemy) -> enemy instanceof Enemy
        ));
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if (!this.level().isClientSide()) {
            UUID ownerUUID = this.getOwnerId();
            Player owner = null;
            if (ownerUUID != null) owner = this.level().getPlayerByUUID(ownerUUID);

            if (owner != null) deletePlayersHologram( owner );
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isAlive() && !this.level().isClientSide()) {
            Player owner = this.level().getPlayerByUUID( this.getOwnerId() );

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

    @Override
    public void tick() {
        super.tick();

        if (this.isAggressive() && this.getTarget() != null) {
            this.getLookControl().setLookAt(this.getTarget());
        }
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, pVelocity);

        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - abstractarrow.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, 1.0f);
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(abstractarrow);
    }

    private void suitUpLikeOwner() {
        Player owner = this.level().getPlayerByUUID( this.getOwnerId() );
        if (owner != null) {
            ItemStack itemInHand = new ItemStack(Items.BOW);
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

    public int getRemainingLifeTicks() {
        return remainingLifeTicks;
    }

    public boolean ownerToFar() {
        Player owner = this.level().getPlayerByUUID( this.getOwnerId() );
        if (owner == null) return true;

        if( Math.abs(this.getX() - owner.getX()) > maxDistance) return true;
        if( Math.abs(this.getY() - owner.getY()) > maxDistance) return true;
        if( Math.abs(this.getZ() - owner.getZ()) > maxDistance) return true;

        return false;
    }

    public Player getOwner() {
        UUID ownerUUID = this.getOwnerId();
        if (ownerUUID == null) return null;

        return this.level().getPlayerByUUID(ownerUUID);
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getOwnerSkin() {
        if (!this.level().isClientSide()) return null;

        UUID ownerId = this.getOwnerId();
        if (ownerId == null) return null;

        AbstractClientPlayer clientPlayer = (AbstractClientPlayer) this.level().getPlayerByUUID(ownerId);
        if (clientPlayer != null) return clientPlayer.getSkinTextureLocation();
        else return null;
    }

}
