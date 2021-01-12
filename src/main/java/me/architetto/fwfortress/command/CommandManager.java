package me.architetto.fwfortress.command;

import me.architetto.fwfortress.command.admin.CreateCommand;
import me.architetto.fwfortress.command.admin.DeleteCommand;
import me.architetto.fwfortress.command.admin.ReloadCommand;
import me.architetto.fwfortress.command.admin.ToggleCommand;
import me.architetto.fwfortress.command.extra.RepairCommand;
import me.architetto.fwfortress.command.user.InfoCommand;
import me.architetto.fwfortress.command.user.InvadeCommand;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor{

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new CreateCommand());
        subcommands.add(new DeleteCommand());
        subcommands.add(new InvadeCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new ReloadCommand());
        subcommands.add(new ToggleCommand());
        subcommands.add(new RepairCommand());

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Error: can't run commands from console");
            return true;
        }

        Player p = (Player) sender;

        if (args.length > 0) {
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {

                    SubCommand subCommand = getSubcommands().get(i);

                    if (!sender.hasPermission(subCommand.getPermission())) {
                        Message.ERR_PERMISSION.send(sender);
                        return true;
                    }

                    if (args.length < subCommand.getArgsRequired()) {
                        Message.ERR_SYNTAX.send(sender);
                        return true;
                    }

                    subCommand.perform(p, args);

                }
            }
        }else{
            p.sendMessage(ChatFormatter.commandsInfo());

            for (int i = 0; i < getSubcommands().size(); i++) {

                SubCommand subCommand = getSubcommands().get(i);

                if (!sender.hasPermission(subCommand.getPermission()))
                    continue;

                p.sendMessage(ChatFormatter.formatListMessage(subCommand.getSyntax()
                        + " - " + subCommand.getDescription()));
            }

            p.sendMessage(ChatFormatter.chatFooter());
        }

        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            for (int i = 0; i < getSubcommands().size(); i++) {
                SubCommand subCommand = getSubcommands().get(i);

                if (!sender.hasPermission(subCommand.getPermission()))
                    continue;

                subcommandsArguments.add(subCommand.getName());
            }

            return subcommandsArguments;

        }else if (args.length >= 2) {
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    return getSubcommands().get(i).getSubcommandArguments((Player) sender, args);
                }
            }
        }

        return null;
    }

}
