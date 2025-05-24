package net.pocketbeast.battleteleporter.event;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.ModEntities;
import net.pocketbeast.battleteleporter.entity.client.HologramModel;
import net.pocketbeast.battleteleporter.entity.client.HologramRenderer;
import net.pocketbeast.battleteleporter.entity.client.ModModelLayers;
import net.pocketbeast.battleteleporter.network.NetworkHandler;

@Mod.EventBusSubscriber(modid = BattleTeleporterMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.HOLOGRAM.get(), HologramRenderer::new);
        NetworkHandler.register();
    }

    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.HOLOGRAM_LAYER, HologramModel::createBodyLayer);
    }

}
