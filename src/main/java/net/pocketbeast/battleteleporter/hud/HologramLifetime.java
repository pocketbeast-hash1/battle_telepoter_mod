package net.pocketbeast.battleteleporter.hud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pocketbeast.battleteleporter.BattleTeleporterMod;

@Mod.EventBusSubscriber(modid = BattleTeleporterMod.MOD_ID, value = Dist.CLIENT)
public class HologramLifetime {

    private static int maxLifeTimeTicks = 0;
    public static int lifeTimeTicks = 0;
    private static boolean canBeDisabled = false;

    private static int IMG_WIDTH = 100;

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiEvent.Post event) {
        if (lifeTimeTicks <= 0) return;

        GuiGraphics gui = event.getGuiGraphics();

        int widthPercent = (int) (lifeTimeTicks / (double) maxLifeTimeTicks * 100);
        int width = (int) (100 * (widthPercent / 100D));

        int xLine = (event.getWindow().getGuiScaledWidth() / 2) - (IMG_WIDTH / 2);
        int yLine = 15;

        int progressBar = canBeDisabled ? 15 : 10;

        gui.blit(
                new ResourceLocation("battleteleporter:textures/gui/bars-hologram.png"),
                xLine,
                yLine,
                0,
                progressBar,
                width,
                5
        );

        gui.blit(
                new ResourceLocation("battleteleporter:textures/gui/bars-hologram.png"),
                xLine,
                yLine,
                0,
                0,
                100,
                10
        );
    }

    public static void showHUD(int ticks) {
        if (maxLifeTimeTicks == 0) {
            maxLifeTimeTicks = ticks;
        }
        lifeTimeTicks = ticks;
    }

    public static void updateCanBeDisabled(boolean canBeDisabled) {
        HologramLifetime.canBeDisabled = canBeDisabled;
    }

}
