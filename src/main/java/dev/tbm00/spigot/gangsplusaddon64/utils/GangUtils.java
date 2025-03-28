package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;
import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.gui.*;

public class GangUtils {
    private static GangsPlusAddon64 javaPlugin;

    public static void init(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        GangUtils.javaPlugin = javaPlugin;
    }

    /**
     * Handles the sub command for opening the gang gui with all gangs.
     * 
     * @param player the command sender
     * @return true after creating gui instance
     */
    public static boolean handleMainGuiCmd(Player player) {
        new GangsGui(javaPlugin, GangsPlusAddon64.gangHook.getGangManager().getAllGangs(), player, 0);
        return true;
    }

    /**
     * Handles the sub command for opening the gang gui with all gangs.
     * 
     * @param player the command sender
     * @return true after creating gui instance
     */
    public static boolean handleAdminGuiCmd(Player player) {
        new GangsAdminGui(javaPlugin, GangsPlusAddon64.gangHook.getGangManager().getAllGangs(), player, 1);
        return true;
    }

    /**
     * Handles the sub command for opening the gang gui player's gang.
     * 
     * @param player the command sender
     * @return true after creating gui instance
     */
    public static boolean handleMyGuiCmd(Player player) {
        new PlayersGui(javaPlugin, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player), player, 0);
        return true;
    }

    /**
     * Handles searching the shops by String/player
     * 
     * @param sender the command sender
     * @param args the arguments passed to the command
     * @return true if task was processed successfully
     */
    public static boolean handleSearch(Player sender, String arg) {
        while (arg.startsWith(" ")) {
            arg = arg.substring(1);
        }

        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();

        String targetUUID = GangsPlusAddon64.repHook.getRepManager().getPlayerUUID(arg);
        if (targetUUID!=null) {
            OfflinePlayer target;
            try {
                target = javaPlugin.getServer().getOfflinePlayer(UUID.fromString(targetUUID));
                for (Gang gang : gangs) {
                    if (gang.isMember(target)) {
                        new PlayersGui(javaPlugin, gang, sender, 0);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (GangsPlusAddon64.gangHook.getGangManager().isGang(arg)) {
            new PlayersGui(javaPlugin, GangsPlusAddon64.gangHook.getGangManager().getGang(arg), sender, 0);
            return true;
        }

        new GangsGui(javaPlugin, gangs, sender, 0);
        return true;
    }
}