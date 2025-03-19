package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;
import net.brcdev.gangs.player.PlayerData;
import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class InvitePlayersGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    Gang gang;
    String label;
    
    public InvitePlayersGui(GangsPlusAddon64 javaPlugin, Gang gang, Player sender) {
        this.javaPlugin = javaPlugin;
        this.gang = gang;
        label = "Player Without Gangs - ";
        gui = new PaginatedGui(6, 45, gang.getFormattedName());
        
        fillPlayers(sender);
        setupFooter(sender);
        
        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        gui.open(sender);
    }

    /**
     * Fills the GUI with players from the gang.
     *
     * @param sender the player for whom the GUI is being built
     */
    private void fillPlayers(Player sender) {
        for (Player target : javaPlugin.getServer().getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            List<String> lore = new ArrayList<>();
            headMeta.setOwningPlayer(target);

            String name = target.getName();
            PlayerData data = GangsPlusAddon64.gangHook.getPlayerManager().getPlayerData(target);
            
            int kills = data.getKills();
            int deaths = data.getDeaths();
            double kdr = data.getKdRatio();
            int assists = data.getAssists();

            GuiUtils.addGuiItemInvite(gui, sender, gang, head, headMeta, lore, name, assists, kills, deaths, kdr, target);
        }
    }

    /**
     * Sets up the footer of the GUI with all, page next, page back, and search buttons.
     */
    private void setupFooter(Player sender) {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Button: All Gangs
        lore.add("&8-----------------------");
        lore.add("&eClick to view all gangs");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dAll Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.BOOK);
        gui.setItem(6, 4, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleAllClick(event, sender)));
        lore.clear();

        // Search
        GuiUtils.setGuiItemSearch(gui, item, meta, lore);

        // Button: My Gang
        GuiUtils.setGuiItemMyGang(gui, item, meta, lore, sender);
        
        gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Previous Page
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageBack(gui, item, meta, lore, label);
        else gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Next Page
        if (gui.getPagesNum()>=2)  GuiUtils.setGuiItemPageNext(gui, item, meta, lore, label);
        else gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }
}