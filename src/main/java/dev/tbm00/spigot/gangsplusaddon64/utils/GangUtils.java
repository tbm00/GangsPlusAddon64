package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;
import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.gui.*;

public class GangUtils {
    private static GangsPlusAddon64 javaPlugin;
    //private static ConfigHandler configHandler;
    public static final List<String> pendingTeleports = new CopyOnWriteArrayList<>();
    public static String clipboardWorld = null;
    public static int x1=0, y1=0, z1=0, x2=0, y2=0, z2=0, xc=0, yc=0, zc=0;

    public static void init(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        GangUtils.javaPlugin = javaPlugin;
        //GangUtils.configHandler = configHandler;
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
        new AdminGui(javaPlugin, GangsPlusAddon64.gangHook.getGangManager().getAllGangs(), player, 1);
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
    public static boolean handleSearch(Player sender, String[] args) {
        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();

        while (args[0].startsWith(" ")) {
            args[0] = args[0].substring(1);
        }

        for (Gang gang : gangs) {
            if (gang.getRawName().equalsIgnoreCase(args[0])
                || gang.getName().equalsIgnoreCase(args[0])
                || gang.getFormattedName().equalsIgnoreCase(args[0])) {
                    new PlayersGui(javaPlugin, gang, sender, 0);
                    return true;
                }
        }
        for (Gang gang : gangs) {
            if (StringUtils.containsIgnoreCase(gang.getRawName(), args[0])
                || StringUtils.containsIgnoreCase(gang.getName(), args[0])
                || StringUtils.containsIgnoreCase(gang.getFormattedName(), args[0])) {
                    new PlayersGui(javaPlugin, gang, sender, 0);
                    return true;
                }
        }
                
        String targetUUID = GangsPlusAddon64.repHook.getRepManager().getPlayerUUID(args[0]);
        if (targetUUID==null) {
            return false;
        }

        OfflinePlayer target;
        try {
            target = javaPlugin.getServer().getOfflinePlayer(UUID.fromString(targetUUID));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } if (target==null) return false;
        
        
        for (Gang gang : gangs) {
            if (gang.isMember(target)) {
                new PlayersGui(javaPlugin, gang, sender, 0);
                return true;
            }
        }

        return false;
    }

    /**
     * Teleports the player to the given shop's location.
     * 
     * @param player the player to teleport
     * @param shop the shop whose location the player will be teleported to
     
    public static void teleportPlayerToShop(Player player, Shop shop) {
        double x=shop.getBaseLocation().getX(), y=shop.getBaseLocation().getY(), z=shop.getBaseLocation().getZ();
        String world=shop.getBaseLocation().getWorldName();

        Utils.teleportPlayer(player, world, x, y+1, z);
    }*/
}