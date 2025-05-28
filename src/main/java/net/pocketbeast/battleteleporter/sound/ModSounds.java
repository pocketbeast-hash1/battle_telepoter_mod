package net.pocketbeast.battleteleporter.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BattleTeleporterMod.MOD_ID);

    public static final RegistryObject<SoundEvent> HOLOGRAM_CREATE = registerSoundEvent("hologram_create");
    public static final RegistryObject<SoundEvent> HOLOGRAM_SWAP = registerSoundEvent("hologram_swap");
    public static final RegistryObject<SoundEvent> HOLOGRAM_DISABLE = registerSoundEvent("hologram_disable");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BattleTeleporterMod.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
