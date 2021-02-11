package com.stevekung.indicatia.mixin;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.indicatia.config.Equipments;
import com.stevekung.indicatia.config.ExtendedConfig;
import com.stevekung.indicatia.gui.exconfig.screen.RenderPreviewScreen;
import com.stevekung.indicatia.renderer.HUDInfo;
import com.stevekung.indicatia.utils.InfoUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.world.dimension.DimensionType;

@Mixin(InGameHud.class)
public class MixinInGameHud
{
    @Inject(at = @At("RETURN"), method = "render(F)V")
    public void render(CallbackInfo info)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (/*IndicatiaConfig.GENERAL.enableRenderInfo.get() && */!mc.options.hudHidden && !mc.options.debugEnabled && mc.player != null && mc.world != null && !(mc.currentScreen instanceof RenderPreviewScreen))
        {
            List<String> leftInfo = new LinkedList<>();
            List<String> rightInfo = new LinkedList<>();

            // left info
            if (ExtendedConfig.instance.ping && !mc.isInSingleplayer())
            {
                leftInfo.add(HUDInfo.getPing());

                if (ExtendedConfig.instance.pingToSecond)
                {
                    leftInfo.add(HUDInfo.getPingToSecond());
                }
            }
            if (ExtendedConfig.instance.serverIP && !mc.isInSingleplayer())
            {
                if (mc.getServer() != null)
                {
                    leftInfo.add(HUDInfo.getServerIP(mc));
                }
            }
            if (ExtendedConfig.instance.fps)
            {
                leftInfo.add(HUDInfo.getFPS());
            }
            if (ExtendedConfig.instance.xyz)
            {
                leftInfo.add(HUDInfo.getXYZ(mc));

                if (mc.player.dimension == DimensionType.THE_NETHER)
                {
                    leftInfo.add(HUDInfo.getOverworldXYZFromNether(mc));
                }
            }
            if (ExtendedConfig.instance.direction)
            {
                leftInfo.add(HUDInfo.renderDirection(mc));
            }
            if (ExtendedConfig.instance.biome)
            {
                leftInfo.add(HUDInfo.getBiome(mc));
            }
            if (ExtendedConfig.instance.slimeChunkFinder && mc.player.dimension == DimensionType.OVERWORLD)
            {
                String isSlimeChunk = InfoUtils.INSTANCE.isSlimeChunk(mc.player.getBlockPos()) ? "Yes" : "No";
                leftInfo.add(ColorUtils.stringToRGB(ExtendedConfig.instance.slimeChunkColor).toColoredFont() + "Slime Chunk: " + ColorUtils.stringToRGB(ExtendedConfig.instance.slimeChunkValueColor).toColoredFont() + isSlimeChunk);
            }

            // right info
            if (ExtendedConfig.instance.realTime)
            {
                rightInfo.add(HUDInfo.getCurrentTime());
            }
            if (ExtendedConfig.instance.gameTime)
            {
                rightInfo.add(HUDInfo.getCurrentGameTime(mc));
            }
            if (ExtendedConfig.instance.gameWeather && mc.world.isRaining())
            {
                rightInfo.add(HUDInfo.getGameWeather(mc));
            }
            if (ExtendedConfig.instance.moonPhase)
            {
                rightInfo.add(InfoUtils.INSTANCE.getMoonPhase(mc));
            }

            // left info
            for (int i = 0; i < leftInfo.size(); ++i)
            {
                String string = leftInfo.get(i);
                float fontHeight = mc.textRenderer.fontHeight + 1;
                float yOffset = 3 + fontHeight * i;
                float xOffset = mc.getWindow().getScaledWidth() - 2 - mc.textRenderer.getStringWidth(string);

                if (!StringUtils.isEmpty(string))
                {
                    mc.textRenderer.drawWithShadow(string, ExtendedConfig.instance.swapRenderInfo ? xOffset : 3.0625F, yOffset, 16777215);
                }
            }

            // right info
            for (int i = 0; i < rightInfo.size(); ++i)
            {
                String string = rightInfo.get(i);
                float fontHeight = mc.textRenderer.fontHeight + 1;
                float yOffset = 3 + fontHeight * i;
                float xOffset = mc.getWindow().getScaledWidth() - 2 - mc.textRenderer.getStringWidth(string);

                if (!StringUtils.isEmpty(string))
                {
                    mc.textRenderer.drawWithShadow(string, ExtendedConfig.instance.swapRenderInfo ? 3.0625F : xOffset, yOffset, 16777215);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "renderStatusEffectOverlay()V")
    public void renderStatusEffectOverlay(CallbackInfo info)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (/*IndicatiaConfig.GENERAL.enableRenderInfo.get() && */!mc.options.hudHidden && !mc.options.debugEnabled && mc.player != null && mc.world != null && !(mc.currentScreen instanceof RenderPreviewScreen))
        {
            // equipments
            if (!mc.player.isSpectator() && ExtendedConfig.instance.equipmentHUD)
            {
                if (ExtendedConfig.instance.equipmentPosition == Equipments.Position.HOTBAR)
                {
                    HUDInfo.renderHotbarEquippedItems(mc);
                }
                else
                {
                    if (ExtendedConfig.instance.equipmentDirection == Equipments.Direction.VERTICAL)
                    {
                        HUDInfo.renderVerticalEquippedItems(mc);
                    }
                    else
                    {
                        HUDInfo.renderHorizontalEquippedItems(mc);
                    }
                }
            }

            if (ExtendedConfig.instance.potionHUD)
            {
                HUDInfo.renderPotionHUD(mc);
            }
        }
    }
}