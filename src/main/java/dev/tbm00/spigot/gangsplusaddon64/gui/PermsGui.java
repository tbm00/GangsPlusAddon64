package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class PermsGui {
    GangsPlusAddon64 javaPlugin;
    Gui gui;
    String label;
    Player player;
    Gang givenGang;
    
    public PermsGui(GangsPlusAddon64 javaPlugin, Player player) {
        this.player = player;
        this.javaPlugin = javaPlugin;
        label = "Gang Rank Permissions";
        gui = new Gui(6, label);
        
        setupItems();

        gui.open(player);
    }

    /**
     * Sets up the footer of the GUI with categories & all other buttons.
     */
    private void setupItems() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&7- Go to gang homes");
        lore.add("&7- Join & start gang /fights");
        lore.add("&7- Use /gangchat and /allychat");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fGang Rank: Thug"));
        clearMetaJunk(meta);
        item.setItemMeta(meta);
        item.setType(Material.STONE_SWORD);
        gui.setItem(3, 2, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        lore.add("&8-----------------------");
        lore.add("&7- Invite/uninvite gang members");
        lore.add("&7- Deposit/withdraw money to/from gang's bank");
        lore.add("&7+ all thug perms");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fGang Rank: Gangstar"));
        clearMetaJunk(meta);
        item.setItemMeta(meta);
        item.setType(Material.IRON_SWORD);
        gui.setItem(3, 4, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        lore.add("&8-----------------------");
        lore.add("&7- Kick gang members");
        lore.add("&7- Promote/demote gang members");
        lore.add("&7- Set/delete gang homes");
        lore.add("&7- Toggle friendly-fire");
        lore.add("&7- Send ally requests to other gangs");
        lore.add("&7+ all thug & gangstar perms");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fGang Rank: Capo"));
        clearMetaJunk(meta);
        item.setItemMeta(meta);
        item.setType(Material.DIAMOND_SWORD);
        gui.setItem(3, 6, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        lore.add("&8-----------------------");
        lore.add("&7- Rename the gang");
        lore.add("&7- Disband the gang");
        lore.add("&7+ all thug, gangstar, & capo perms");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fGang Rank: Kingpin"));
        clearMetaJunk(meta);
        item.setItemMeta(meta);
        item.setType(Material.NETHERITE_SWORD);
        gui.setItem(3, 8, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        
        // 5 - Back button
        lore.add("&8-----------------------");
        lore.add("&6Click to go back");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fBack"));
        item.setItemMeta(meta);
        item.setType(Material.STONE_BUTTON);
        gui.setItem(6, 5, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleManageMenuClick(event, givenGang)));
        lore.clear();

        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }

    private void clearMetaJunk(ItemMeta meta) {
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }
}