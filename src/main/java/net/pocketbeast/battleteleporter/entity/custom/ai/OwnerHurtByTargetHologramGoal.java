package net.pocketbeast.battleteleporter.entity.custom.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

import java.util.UUID;

public class OwnerHurtByTargetHologramGoal extends TargetGoal {

    Player owner;
    HologramEntity hologram;

    public OwnerHurtByTargetHologramGoal(HologramEntity pMob, boolean pMustSee) {
        super(pMob, pMustSee);
        this.hologram = pMob;
    }

    @Override
    public boolean canUse() {
        if (this.owner == null) {
            UUID ownerUUID = this.hologram.getOwnerId();
            if (ownerUUID == null) return false;

            this.owner = this.hologram.level().getPlayerByUUID(ownerUUID);
            if (owner == null) return false;
        }

        LivingEntity livingEntity = this.owner.getLastHurtByMob();
        return livingEntity != null;
    }

    @Override
    public void start() {
        super.start();
        this.mob.setTarget( this.owner.getLastHurtByMob() );
    }
}
