package stevekung.mods.indicatia.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.Feedback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandSource;
import stevekung.mods.indicatia.event.IndicatiaEventHandler;
import stevekung.mods.stevekungslib.utils.CommonUtils;
import stevekung.mods.stevekungslib.utils.LangUtils;

public class AFKCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(ArgumentBuilders.literal("afk").requires(requirement -> requirement.hasPermissionLevel(0))
                .then(ArgumentBuilders.literal("start").executes(requirement -> AFKCommand.startAFK(null))
                        .then(ArgumentBuilders.argument("reason", StringArgumentType.greedyString()).executes(requirement -> AFKCommand.startAFK(StringArgumentType.getString(requirement, "reason")))))
                .then(ArgumentBuilders.literal("stop").executes(requirement -> AFKCommand.stopAFK()))
                .then(ArgumentBuilders.literal("change_reason").then(ArgumentBuilders.argument("reason", StringArgumentType.greedyString()).executes(requirement -> AFKCommand.setReason(StringArgumentType.getString(requirement, "reason")))))
                .then(ArgumentBuilders.literal("mode")
                        .then(ArgumentBuilders.literal("idle").executes(requirement -> AFKCommand.changeAFKMode("idle")))
                        .then(ArgumentBuilders.literal("move").executes(requirement -> AFKCommand.changeAFKMode("move")))
                        .then(ArgumentBuilders.literal("360").executes(requirement -> AFKCommand.changeAFKMode("360")))
                        .then(ArgumentBuilders.literal("360_move").executes(requirement -> AFKCommand.changeAFKMode("360_move")))));
    }

    private static int startAFK(String reason)
    {
        if (!IndicatiaEventHandler.isAFK)
        {
            IndicatiaEventHandler.isAFK = true;
            IndicatiaEventHandler.afkReason = reason;

            if (reason.isEmpty())
            {
                reason = "";
            }
            else
            {
                reason = ", " + LangUtils.translate("commands.afk.reason", reason);
            }

            String message = LangUtils.translate("commands.afk.afk_now");

            //if (IndicatiaConfig.GENERAL.enableAFKMessage.get())TODO
            {
                MinecraftClient.getInstance().player.sendChatMessage(message + reason);
            }
            return 1;
        }
        else
        {
            Feedback.sendError(LangUtils.translateComponent("commands.afk.afk_started"));
            return 0;
        }
    }

    private static int stopAFK()
    {
        if (IndicatiaEventHandler.isAFK)
        {
            IndicatiaEventHandler.isAFK = false;
            IndicatiaEventHandler.afkMoveTicks = 0;

            //if (IndicatiaConfig.GENERAL.enableAFKMessage.get())TODO
            {
                if (IndicatiaEventHandler.afkReason.isEmpty())
                {
                    MinecraftClient.getInstance().player.sendChatMessage(LangUtils.translateComponent("commands.afk.afk_stopped", CommonUtils.ticksToElapsedTime(IndicatiaEventHandler.afkTicks)).getText());
                }
                else
                {
                    MinecraftClient.getInstance().player.sendChatMessage(LangUtils.translateComponent("commands.afk.afk_stopped_with_reason", IndicatiaEventHandler.afkReason, CommonUtils.ticksToElapsedTime(IndicatiaEventHandler.afkTicks)).getText());
                }
            }
            return 1;
        }
        else
        {
            Feedback.sendError(LangUtils.translateComponent("commands.afk.afk_not_started"));
            return 0;
        }
    }

    private static int setReason(String newReason)
    {
        if (IndicatiaEventHandler.isAFK)
        {
            String oldReason = IndicatiaEventHandler.afkReason;
            IndicatiaEventHandler.afkReason = newReason;
            Feedback.sendFeedback(LangUtils.translateComponent("commands.afk.change_afk_reason", oldReason, newReason));
            return 1;
        }
        else
        {
            Feedback.sendError(LangUtils.translateComponent("commands.afk.afk_not_started"));
            return 0;
        }
    }

    private static int changeAFKMode(String mode)
    {
        switch (mode)
        {
        case "idle":
            IndicatiaEventHandler.afkMode = "idle";
            IndicatiaEventHandler.afkMoveTicks = 0;
            Feedback.sendFeedback(LangUtils.translateComponent("commands.afk.set_afk_mode", IndicatiaEventHandler.afkMode));
            return 1;
        case "move":
            IndicatiaEventHandler.afkMode = "move";
            Feedback.sendFeedback(LangUtils.translateComponent("commands.afk.set_afk_mode", IndicatiaEventHandler.afkMode));
            return 1;
        case "360":
            IndicatiaEventHandler.afkMode = "360";
            Feedback.sendFeedback(LangUtils.translateComponent("commands.afk.set_afk_mode", IndicatiaEventHandler.afkMode));
            return 1;
        case "360_move":
            IndicatiaEventHandler.afkMode = "360_move";
            Feedback.sendFeedback(LangUtils.translateComponent("commands.afk.set_afk_mode", IndicatiaEventHandler.afkMode));
            return 1;
        }
        return 0;
    }
}