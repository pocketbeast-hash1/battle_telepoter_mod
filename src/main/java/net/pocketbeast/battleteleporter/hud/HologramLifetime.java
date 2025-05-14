package net.pocketbeast.battleteleporter.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiEvent.Post event) {
        if (lifeTimeTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        Font font = mc.font;

        gui.drawString(
                font,
                String.valueOf(lifeTimeTicks),
                10,
                10,
                0x00FF00
        );

        int width = (int) (lifeTimeTicks / (double) maxLifeTimeTicks * 100);
        gui.blit(
                new ResourceLocation("textures/gui/bars.png"),
                10,
                50,
                0,
                4,
                width,
                5
        );
    }

    public static void showHUD(int ticks) {
        if (maxLifeTimeTicks == 0) {
            maxLifeTimeTicks = ticks;
        }
        lifeTimeTicks = ticks;
    }

}
