package stevekung.mods.indicatia.renderer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.config.IndicatiaConfig;
import stevekung.mods.indicatia.utils.CapeUtils;
import stevekung.mods.stevekunglib.utils.GameProfileUtils;

@OnlyIn(Dist.CLIENT)
public class LayerCustomCape implements LayerRenderer<AbstractClientPlayer>
{
    private final RenderPlayer playerRenderer;

    public LayerCustomCape(RenderPlayer playerRenderer)
    {
        this.playerRenderer = playerRenderer;
    }

    @Override
    public void render(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (IndicatiaConfig.GENERAL.enableCustomCape.get() && entity.getName().getUnformattedComponentText().equals(GameProfileUtils.getUsername()) && !entity.isInvisible() && ExtendedConfig.showCustomCape && CapeUtils.CAPE_TEXTURE != null)
        {
            ItemStack itemStack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (itemStack.isEmpty() || itemStack.getItem() != Items.ELYTRA)
            {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                CapeUtils.bindCapeTexture();
                GlStateManager.enableRescaleNormal();
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.0F, 0.0F, 0.125F);
                double d0 = entity.prevChasingPosX + (entity.chasingPosX - entity.prevChasingPosX) * partialTicks - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks);
                double d1 = entity.prevChasingPosY + (entity.chasingPosY - entity.prevChasingPosY) * partialTicks - (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks);
                double d2 = entity.prevChasingPosZ + (entity.chasingPosZ - entity.prevChasingPosZ) * partialTicks - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
                float f = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialTicks;
                double d3 = MathHelper.sin(f * 0.017453292F);
                double d4 = -MathHelper.cos(f * 0.017453292F);
                float f1 = (float)d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
                f3 = MathHelper.clamp(f3, -20.0F, 20.0F);

                if (f2 < 0.0F)
                {
                    f2 = 0.0F;
                }

                float f4 = entity.prevCameraYaw + (entity.cameraYaw - entity.prevCameraYaw) * partialTicks;
                f1 = f1 + MathHelper.sin((entity.prevDistanceWalkedModified + (entity.distanceWalkedModified - entity.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                if (entity.isSneaking())
                {
                    f1 += 25.0F;
                    GlStateManager.translatef(0.0F, 0.145F, -0.015F);
                }
                GlStateManager.rotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                this.playerRenderer.getMainModel().renderCape(0.0625F);
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }
}