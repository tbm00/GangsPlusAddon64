

package dev.tbm00.spigot.gangsplusaddon64.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class AdminGuiCmd implements TabExecutor {
    //private final GangsPlusAddon64 javaPlugin;
    //private final ConfigHandler configHandler;
    private final String ADMIN_PERM = "gangsplusaddon64.admin";

    public AdminGuiCmd(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        //this.javaPlugin = javaPlugin;
        //this.configHandler = configHandler;
    }

    /**
     * Handles the /gangs command.
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
        } else if (!Utils.hasPermission(sender, ADMIN_PERM)) {
            Utils.sendMessage(sender, "&cNo permission!");
            return true;
        }

        Player player = (Player) sender;

        
        return GangUtils.handleAdminGuiCmd(player);
    }

    /**
     * Handles tab completion for the /gangs command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}