package dev.tbm00.spigot.gangsplusaddon64.listener;

import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.Utils;

public class PlayerConnection implements Listener {
    GangsPlusAddon64 javaPlugin;

    public PlayerConnection(GangsPlusAddon64 javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    /**
     * Handles the player connection event.
     * Adds head to cache if its not there, or current cached head is old.
     *
     * @param event the PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(event.getPlayer())) {
            UUID uuid = event.getPlayer().getUniqueId();
            long currentTime = System.currentTimeMillis();
            if (Utils.headMetaCache.containsKey(uuid) && ((currentTime - Utils.headMetaCache.get(uuid).getRight()) < 3600000)) {
                return;
            }

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(event.getPlayer());
            head.setItemMeta(headMeta);
            
            final long updateTime = currentTime;
            // Delay to allow the server to apply the skin texture
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
                // Retrieve the updated SkullMeta from the ItemStack
                SkullMeta updatedMeta = (SkullMeta) head.getItemMeta();
                Utils.headMetaCache.put(uuid, Pair.of(updatedMeta, updateTime));
            }, 20L);
            
        }
    }
}