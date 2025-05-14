package net.pocketbeast.battleteleporter.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.ModEntities;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

@Mod.EventBusSubscriber(modid = BattleTeleporterMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.HOLOGRAM.get(), HologramEntity.createAttributes().build());
    }

}
