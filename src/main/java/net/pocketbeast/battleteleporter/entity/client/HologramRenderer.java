package net.pocketbeast.battleteleporter.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

public class HologramRenderer extends MobRenderer<HologramEntity, HologramModel<HologramEntity>> {
    public HologramRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new HologramModel<>(pContext.bakeLayer(ModModelLayers.HOLOGRAM_LAYER)), 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(HologramEntity hologramEntity) {
        return new ResourceLocation(BattleTeleporterMod.MOD_ID, "textures/entity/hologram.png");
    }
}
