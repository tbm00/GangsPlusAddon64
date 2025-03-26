package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.chat.TextComponent;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.ConfigHandler;

public class Utils {
    private static GangsPlusAddon64 javaPlugin;
    private static ConfigHandler configHandler;

    public static void init(GangsPlusAddon64 javaPlugin, ConfigHandler configHandler) {
        Utils.javaPlugin = javaPlugin;
        Utils.configHandler = configHandler;
    }

    /**
     * Logs one or more messages to the server console with the prefix & specified chat color.
     *
     * @param chatColor the chat color to use for the log messages
     * @param strings one or more message strings to log
     */
    public static void log(ChatColor chatColor, String... strings) {
		for (String s : strings)
            javaPlugin.getServer().getConsoleSender().sendMessage("[DSA64] " + chatColor + s);
	}

    /**
     * Formats int to "200,000" style
     * 
     * @param amount the amount to format
     * @return the formatted string
     */
    public static String formatInt(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    /**
     * Formats double to "200,000" style
     * 
     * @param amount the amount to format
     * @return the formatted string
     */
    public static String formatInt(double amount) {
        return formatInt((int) amount);
    }

    /**
     * Formats material to title case
     * 
     * @param amount the material to format
     * @return the formatted string
     */
    public static String formatMaterial(Material material) {
        if (material == null) return "null";

        StringBuilder builder = new StringBuilder();
        for(String word : material.toString().split("_"))
            builder.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ");
     
        return builder.toString().trim();
    }

    /**
     * Retrieves a player by their name.
     * 
     * @param arg the name of the player to retrieve
     * @return the Player object, or null if not found
     */
    public static Player getPlayer(String arg) {
        return javaPlugin.getServer().getPlayer(arg);
    }

    /**
     * Checks if the sender has a specific permission.
     * 
     * @param sender the command sender
     * @param perm the permission string
     * @return true if the sender has the permission, false otherwise
     */
    public static boolean hasPermission(CommandSender sender, String perm) {
        if (sender instanceof Player && ((Player)sender).getGameMode()==GameMode.CREATIVE) return false;
        return sender.hasPermission(perm) || sender instanceof ConsoleCommandSender;
    }

    /**
     * Sends a message to a target CommandSender.
     * 
     * @param target the CommandSender to send the message to
     * @param string the message to send
     */
    public static void sendMessage(CommandSender target, String string) {
        if (!string.isBlank())
            target.spigot().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', configHandler.getChatPrefix() + string)));
    }

    /**
     * Gives a player an ItemStack.
     * If they have a full inv, it drops on the ground.
     * 
     * @param player the player to give to
     * @param item the item to give
     */
    public static void giveItem(Player player, ItemStack item) {
        if ((player.getInventory().firstEmpty() == -1)) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        } else player.getInventory().addItem(item);
    }

    /**
     * Executes a command as the console.
     * 
     * @param command the command to execute
     * @return true if the command was successfully executed, false otherwise
     */
    public static boolean runCommand(String command) {
        ConsoleCommandSender console = javaPlugin.getServer().getConsoleSender();
        try {
            return Bukkit.dispatchCommand(console, command);
        } catch (Exception e) {
            log(ChatColor.RED, "Caught exception running command " + command + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Executes a command as a specific player.
     * 
     * @param target the player to execute the command as
     * @param command the command to execute
     * @return true if the command was successfully executed, false otherwise
     */
    public static boolean sudoCommand(Player target, String command) {
        try {
            return Bukkit.dispatchCommand(target, command);
        } catch (Exception e) {
            log(ChatColor.RED, "Caught exception sudoing command: " + target.getName() + " : /" + command + ": " + e.getMessage());
            return false;
        }
    }

   /**
     * Executes a command as a specific human entity.
     * 
     * @param target the player to execute the command as
     * @param command the command to execute
     * @return true if the command was successfully executed, false otherwise
     */
    public static boolean sudoCommand(HumanEntity target, String command) {
        try {
            return Bukkit.dispatchCommand(target, command);
        } catch (Exception e) {
            log(ChatColor.RED, "Caught exception sudoing command: " + target.getName() + " : /" + command + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets PVPStats
     */
    public static int getPvpStat(String stat, OfflinePlayer player) {
        try {
            return GangsPlusAddon64.pvpHook.getSQLHandler().getStats(stat, player.getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets PVPStats
     */
    public static int getPvpStat(String stat, Object player) {
        try {
            return GangsPlusAddon64.pvpHook.getSQLHandler().getStats(stat, ((Player) player).getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //old: private static final Map<UUID, SkullMeta> headMetaCache = new HashMap<>();
    public static final Map<UUID, Pair<SkullMeta, Long>> headMetaCache = new HashMap<>();
    private static final Set<UUID> refreshInProgress = new HashSet<>();

    /**
     * Adds skin texture to head meta.
     * 
     * If player+skin is in cached map, retrieve it
     * else use setOwningPlayer and save the SkullMeta to cache
     *
     * @param headMeta the head meta to modify
     * @param player the player whose head we want
     */
    public static void applyHeadTexture(ItemStack head, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        long currentTime=0;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        Pair<SkullMeta, Long> entry = headMetaCache.get(uuid);
        boolean needRefresh = false;
        
        if (player.isOnline() && player instanceof Player) {
            currentTime = javaPlugin.getServer().getWorld("Tadow").getFullTime()/72000;

            if (entry!=null && (entry.getRight()<currentTime || 
            (entry.getLeft().getOwningPlayer()==null || !entry.getLeft().getOwningPlayer().hasPlayedBefore())))
                needRefresh = true;
            else if (entry==null) needRefresh = true;
        }

        if (entry!=null && !needRefresh) {
            head.setItemMeta(entry.getLeft().clone());
        } else {
            headMeta.setOwningPlayer(player);
            head.setItemMeta(headMeta);
            needRefresh = true;
        }

        if (!needRefresh || refreshInProgress.contains(uuid)) return;

        refreshInProgress.add(uuid);
        final long updateTime = currentTime;

        // Delay to allow the server to apply the skin texture
        Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
            try {
                SkullMeta updatedMeta = (SkullMeta) head.getItemMeta();
                headMetaCache.put(uuid, Pair.of(updatedMeta, updateTime));
            } finally {
                refreshInProgress.remove(uuid);
            }
        }, 20L);
    }
}