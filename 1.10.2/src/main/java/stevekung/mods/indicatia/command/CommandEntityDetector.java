package stevekung.mods.indicatia.command;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.core.IndicatiaMod;
import stevekung.mods.indicatia.util.GameProfileUtil;
import stevekung.mods.indicatia.util.JsonUtil;

public class CommandEntityDetector extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "entitydetect";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        JsonUtil json = new JsonUtil();

        if (args.length == 1)
        {
            String input = args[0];
            EntityPlayer player = sender.getEntityWorld().getPlayerEntityByName(input);

            for (Entry<Class<? extends Entity>, String> entity : EntityList.CLASS_TO_NAME.entrySet())
            {
                String entityName = entity.getValue();

                if (input.equals(entityName))
                {
                    ExtendedConfig.ENTITY_DETECT_TYPE = entityName;
                }
                else if (player != null && input.equals(player.getName()))
                {
                    if (GameProfileUtil.getUsername().equalsIgnoreCase(input))
                    {
                        sender.addChatMessage(json.text("Cannot set entity detector type to yourself!").setStyle(json.red()));
                        return;
                    }
                    else
                    {
                        ExtendedConfig.ENTITY_DETECT_TYPE = player.getName();
                    }
                }
                else
                {
                    ExtendedConfig.ENTITY_DETECT_TYPE = input.equalsIgnoreCase("reset") ? "" : input;
                }
            }
            sender.addChatMessage(json.text("Set entity detector type to " + input));
            ExtendedConfig.save();
            return;
        }
        throw new WrongUsageException("commands.entitydetect.usage");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        NetHandlerPlayClient connection = IndicatiaMod.MC.thePlayer.connection;
        List<NetworkPlayerInfo> playerInfo = Lists.newArrayList(connection.getPlayerInfoMap());
        List<String> entityList = EntityList.getEntityNameList();
        entityList.add("all");
        entityList.add("only_mob");
        entityList.add("only_creature");
        entityList.add("only_non_mob");
        entityList.add("only_player");
        entityList.add("reset");

        for (int i = 0; i < playerInfo.size(); ++i)
        {
            if (i < playerInfo.size())
            {
                entityList.add(playerInfo.get(i).getGameProfile().getName().replace(GameProfileUtil.getUsername(), ""));
            }
        }
        if (args.length == 1)
        {
            return CommandBase.getListOfStringsMatchingLastWord(args, entityList);
        }
        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}