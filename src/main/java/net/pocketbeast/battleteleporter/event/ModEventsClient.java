package net.pocketbeast.battleteleporter.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;
import net.pocketbeast.battleteleporter.hud.HologramLifetime;
import net.pocketbeast.battleteleporter.item.custom.BattleTeleporterItem;

@Mod.EventBusSubscriber(modid = BattleTeleporterMod.MOD_ID, value = Dist.CLIENT)
public class ModEventsClient {

    @SubscribeEvent
    public static void onChangeItem(LivingEquipmentChangeEvent event) {

        // чтобы при смене баттл телепортера сбрасывалось значение canBeDisabled
        ItemStack from = event.getFrom();
        if (from.getItem() instanceof BattleTeleporterItem) {
            ItemStack to = event.getTo();
            if (!(to.getItem() instanceof BattleTeleporterItem)) {
                HologramLifetime.updateCanBeDisabled(false);
            }
        }

    }

}
