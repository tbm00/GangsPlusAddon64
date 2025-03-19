

package dev.tbm00.spigot.gangsplusaddon64.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class GangGuiCmd implements TabExecutor {
    //private final GangsPlusAddon64 javaPlugin;
    //private final ConfigHandler configHandler;
    private final String PLAYER_PERM = "gangsplusaddon64.player";

    public GangGuiCmd(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        //this.javaPlugin = javaPlugin;
        //this.configHandler = configHandler;
    }

    /**
     * Handles the /ggg command.
     * 
     * @param player the command sender
     * @param consoleCommand the command being executed
     * @param alias the alias used for the command
     * @param args the arguments passed to the command
     * @return true if the command was handled successfully, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            Utils.sendMessage(sender, "&cThis command cannot be run through the console!");
            return true;
        } else if (!Utils.hasPermission(sender, PLAYER_PERM)) {
            Utils.sendMessage(sender, "&cNo permission!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
            return GangUtils.handleMainGuiCmd(player);

        String subCmd = args[0].toLowerCase();
        switch (subCmd) {
            case "help":
                return handleHelpCmd(player);
            case "all":
                return GangUtils.handleMainGuiCmd(player);
            case "mine":
                return GangUtils.handleMyGuiCmd(player);
            default:
                return GangUtils.handleSearch(player, args);
        }
    }
    
    /**
     * Handles the sub command for the help menu.
     * 
     * @param player the command sender
     * @return true after displaying help menu
     */
    private boolean handleHelpCmd(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "--- " + ChatColor.LIGHT_PURPLE + "Shop Owner Commands" + ChatColor.DARK_PURPLE + " ---\n"
            + ChatColor.WHITE + "/ggg <gang/player>" + ChatColor.GRAY + " Inspect gang/player stats"
        );
        return true;
    }

    /**
     * Handles tab completion for the /ggg command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getName().startsWith(args[0]))
                    list.add(player.getName());
            });
        }
        return list;
    }
}