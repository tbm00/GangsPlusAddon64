package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.reflect.Field;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.Location;
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
    public static final List<String> pendingTeleports = new CopyOnWriteArrayList<>();
    

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
     * Teleports a player to the given world and coordinates after a 5-second delay.
     * If the player moves during the delay, the teleport is cancelled.
     *
     * @param player the player to teleport
     * @param worldName the target world's name
     * @param x target x-coordinate
     * @param y target y-coordinate
     * @param z target z-coordinate
     * @return true if the teleport countdown was started, false if the player was already waiting
     */
    public static boolean teleportPlayer(Player player, String worldName, double x, double y, double z) {
        String playerName = player.getName();
        if (pendingTeleports.contains(playerName)) {
            Utils.sendMessage(player, "&cYou are already waiting for a teleport!");
            return false;
        }
        pendingTeleports.add(playerName);
        Utils.sendMessage(player, "&aTeleporting in 3 seconds -- don't move!");

        // Schedule the teleport to run later
        Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
            if (pendingTeleports.contains(playerName)) {
                // Remove player from pending list and teleport
                pendingTeleports.remove(playerName);
                World targetWorld = Bukkit.getWorld(worldName);
                if (targetWorld != null) {
                    Location targetLocation = new Location(targetWorld, x, y, z);
                    player.teleport(targetLocation);
                } else {
                    Utils.sendMessage(player, "&cWorld not found!");
                }
            }
        }, 60L);

        return true;
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

    private static final Map<UUID, String> headTextureCache = new HashMap<>();

    /**
     * Adds skin texture to head meta.
     * 
     * If player+skin is in cached map, retrieve it
     * else use setOwningPlayer and save the SkullMeta to cache
     *
     * @param headMeta the head meta to modify
     * @param player the player whose head we want
     */
    public static void applyHeadTexture(SkullMeta headMeta, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (headTextureCache.containsKey(uuid)) {
            setHeadTexture(headMeta, headTextureCache.get(uuid));
        } else {
            // Not cached – use Mojang's API to load skin (this will trigger a network request)
            if (player.isOnline() && player instanceof Player)
                headMeta.setOwningPlayer((Player) player);
            else headMeta.setOwningPlayer(player);

            // Extract the texture from the modified headMeta and cache it
            String texture = extractHeadTexture(headMeta);
            if (texture != null) {
                headTextureCache.put(uuid, texture);
            }
        }
    }

    /**
     * Applies a texture string to the head meta using reflection.
     *
     * @param headMeta the SkullMeta to modify
     * @param texture  the base64 encoded texture string
     */
    private static void setHeadTexture(SkullMeta headMeta, String texture) {
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", texture));

            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * Extracts the base64 texture string from the SkullMeta.
     *
     * @param headMeta the SkullMeta from which to extract the texture
     * @return the texture string if found, otherwise null
     */
    private static String extractHeadTexture(SkullMeta headMeta) {
        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            
            GameProfile profile = (GameProfile) profileField.get(headMeta);
            if (profile != null && profile.getProperties().containsKey("textures")) {
                return profile.getProperties().get("textures").iterator().next().getValue();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }
}