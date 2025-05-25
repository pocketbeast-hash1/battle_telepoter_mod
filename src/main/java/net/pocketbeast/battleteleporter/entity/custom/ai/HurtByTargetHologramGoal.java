package net.pocketbeast.battleteleporter.entity.custom.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

import java.util.Objects;
import java.util.UUID;

public class HurtByTargetHologramGoal extends TargetGoal {

    Player owner;
    HologramEntity hologram;

    public HurtByTargetHologramGoal(HologramEntity pMob, boolean pMustSee) {
        super(pMob, pMustSee);
        this.hologram = pMob;
    }

    @Override
    public boolean canUse() {
        if (this.owner == null) {
            UUID ownerUUID = this.hologram.getOwnerId();
            Player owner = null;
            if (ownerUUID != null) owner = this.hologram.level().getPlayerByUUID( ownerUUID );
            if (owner != null) this.owner = owner;
        }

        LivingEntity livingEntity = this.mob.getLastHurtByMob();
        return livingEntity != null && !Objects.equals(livingEntity, this.owner);
    }

    @Override
    public void start() {
        super.start();
        this.mob.setTarget( this.mob.getLastHurtByMob() );
    }
}
