package net.pocketbeast.battleteleporter.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.entity.custom.HologramEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, BattleTeleporterMod.MOD_ID
    );

    public static final RegistryObject<EntityType<HologramEntity>> HOLOGRAM = ENTITY_TYPES.register(
            "hologram",
            () -> EntityType.Builder.of(HologramEntity::new, MobCategory.CREATURE)
                    .sized(1.0f, 2.0f)
                    .build("hologram")
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
