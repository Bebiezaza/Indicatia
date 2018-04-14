package stevekung.mods.indicatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.stevekunglib.util.CommonUtils;
import stevekung.mods.stevekunglib.util.LangUtils;

@SideOnly(Side.CLIENT)
public class GuiExtendedConfig extends GuiScreen
{
    private static final List<ExtendedConfig.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(ExtendedConfig.Options.SWAP_INFO_POS);
        OPTIONS.add(ExtendedConfig.Options.HEALTH_STATUS);
        OPTIONS.add(ExtendedConfig.Options.KEYSTROKE_POSITION);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_ORDERING);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_DIRECTION);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_STATUS);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_POSITION);
        OPTIONS.add(ExtendedConfig.Options.POTION_HUD_STYLE);
        OPTIONS.add(ExtendedConfig.Options.POTION_HUD_POSITION);
        OPTIONS.add(ExtendedConfig.Options.CPS_POSITION);
        OPTIONS.add(ExtendedConfig.Options.CPS_OPACITY);
    }

    public void display()
    {
        CommonUtils.registerEventHandler(this);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        CommonUtils.unregisterEventHandler(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public void initGui()
    {
        int i = 0;

        for (ExtendedConfig.Options options : OPTIONS)
        {
            if (options.isFloat())
            {
                this.buttonList.add(new GuiConfigSlider(options.getOrdinal(), this.width / 2 - 160 + i % 2 * 160, this.height / 6 - 17 + 24 * (i >> 1), 160, options));
            }
            else
            {
                GuiConfigButton button = new GuiConfigButton(options.getOrdinal(), this.width / 2 - 160 + i % 2 * 165, this.height / 6 - 17 + 24 * (i >> 1), 160, options, ExtendedConfig.instance.getKeyBinding(options));
                this.buttonList.add(button);
            }
            ++i;
        }
        this.buttonList.add(new GuiButton(100, this.width / 2 - 155, this.height / 6 + 127, 150, 20, LangUtils.translate("extended_config.render_info.title")));
        this.buttonList.add(new GuiButton(101, this.width / 2 + 10, this.height / 6 + 127, 150, 20, LangUtils.translate("extended_config.custom_color.title")));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 151, 150, 20, LangUtils.translate("extended_config.offset.title")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 175, LangUtils.translate("gui.done")));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            ExtendedConfig.save();
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            ExtendedConfig.save();

            if (button.id < 100 && button instanceof GuiConfigButton)
            {
                ExtendedConfig.Options options = ((GuiConfigButton)button).getOption();
                ExtendedConfig.instance.setOptionValue(options, 1);
                button.displayString = ExtendedConfig.instance.getKeyBinding(ExtendedConfig.Options.byOrdinal(button.id));
            }
            if (button.id == 100)
            {
                this.mc.displayGuiScreen(new GuiRenderInfoSettings(this));
            }
            if (button.id == 101)
            {
                this.mc.displayGuiScreen(new GuiCustomColorSettings(this));
            }
            if (button.id == 102)
            {
                this.mc.displayGuiScreen(new GuiOffsetSettings(this));
            }
            if (button.id == 200)
            {
                this.mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, LangUtils.translate("extended_config.main.title") + " : " + LangUtils.translate("extended_config.current_profile.info", TextFormatting.YELLOW + ExtendedConfig.currentProfile + TextFormatting.RESET), this.width / 2, 10, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}