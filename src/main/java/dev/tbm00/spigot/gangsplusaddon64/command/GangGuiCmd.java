

package dev.tbm00.spigot.gangsplusaddon64.command;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.gui.HomesGui;
import dev.tbm00.spigot.gangsplusaddon64.gui.ManageGui;
import dev.tbm00.spigot.gangsplusaddon64.gui.PlayersGui;
import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;
import net.brcdev.gangs.gang.Gang;

public class GangGuiCmd implements TabExecutor {
    private final GangsPlusAddon64 javaPlugin;
    //private final ConfigHandler configHandler;
    private final String PLAYER_PERM = "gangsplusaddon64.player";

    public GangGuiCmd(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        this.javaPlugin = javaPlugin;
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
        } else if (!Utils.hasPermission(sender, PLAYER_PERM)) {
            Utils.sendMessage(sender, "&cNo permission!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0)
            return GangUtils.handleMainGuiCmd(player);

        Gang playerGang = GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player);

        switch (args[0]) {
            case "homes":
            case "home":
                if (playerGang==null) return GangUtils.handleMainGuiCmd(player);
                new HomesGui(javaPlugin, playerGang, player);
                return true;
            case "list":
            case "players":
                if (playerGang==null) return GangUtils.handleMainGuiCmd(player);
                new PlayersGui(javaPlugin, playerGang, player, 0);
                return true;
            case "manage":
            case "gui":
                new ManageGui(javaPlugin, player);
                return true;
            default:
                return GangUtils.handleSearch(player, args[0]);
        }
    }

    /**
     * Handles tab completion for the /gangs command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.clear();
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player)) {
                    if (player.getName().startsWith(args[0])&&args[0].length()>0)
                        list.add(player.getName());
                }
            });
            String[] subCmds = new String[]{"<gang>","<player>","manage","players","homes"};
            for (String n : subCmds) {
                if (n!=null && n.startsWith(args[0])) 
                    list.add(n);
            }
        }
        return list;
    }
}