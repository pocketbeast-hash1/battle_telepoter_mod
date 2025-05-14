package net.pocketbeast.battleteleporter.entity.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;

public class ModModelLayers {
    public static final ModelLayerLocation HOLOGRAM_LAYER = new ModelLayerLocation(
            new ResourceLocation(BattleTeleporterMod.MOD_ID, "hologram_layer"),
            "main"
    );
}
