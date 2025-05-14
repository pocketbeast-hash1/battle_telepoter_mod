package net.pocketbeast.battleteleporter;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pocketbeast.battleteleporter.item.ModItems;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            BattleTeleporterMod.MOD_ID
    );

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(
            "main_tab",
            () ->CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BATTLE_TELEPORTER.get()))
                    .title(Component.translatable("tab." + BattleTeleporterMod.MOD_ID + "." + "main_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.BATTLE_TELEPORTER.get());
                    })
                    .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
