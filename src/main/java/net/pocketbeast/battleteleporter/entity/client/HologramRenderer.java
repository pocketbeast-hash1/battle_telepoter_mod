package net.pocketbeast.battleteleporter.entity.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

public class HologramRenderer extends HumanoidMobRenderer<HologramEntity, HologramModel<HologramEntity>> {
    public HologramRenderer(EntityRendererProvider.Context pContext) {
        this(pContext, new HologramModel<>(pContext.bakeLayer(ModModelLayers.HOLOGRAM_LAYER)), 1.0f);
    }

    public HologramRenderer(EntityRendererProvider.Context pContext, HologramModel<HologramEntity> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new HologramModel<>(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HologramModel<>(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                pContext.getModelManager()
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(HologramEntity hologramEntity) {
        ResourceLocation ownerSkin = hologramEntity.getOwnerSkin();
        if (ownerSkin != null && hologramEntity.isAlive()) {
            return ownerSkin;
        } else {
            return new ResourceLocation(BattleTeleporterMod.MOD_ID, "textures/entity/hologram.png");
        }
    }
}
