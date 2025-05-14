package net.pocketbeast.battleteleporter.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.item.custom.BattleTeleporterItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BattleTeleporterMod.MOD_ID);

    public static final RegistryObject<Item> BATTLE_TELEPORTER = ITEMS.register(
            "battle_teleporter",
            () -> new BattleTeleporterItem(new Item.Properties().durability(500))
    );

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
