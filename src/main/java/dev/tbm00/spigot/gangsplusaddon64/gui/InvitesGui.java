package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class InvitesGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    String label;
    
    public InvitesGui(GangsPlusAddon64 javaPlugin, Player sender) {
        this.javaPlugin = javaPlugin;
        label = "Your Gang Invites - ";
        gui = new PaginatedGui(6, 45, "Your Gang Invites");
        
        fillInvites(sender);
        setupFooter(sender);
        
        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        gui.open(sender);
    }

    /**
     * Fills the GUI with invites.
     *
     * @param sender the player for whom the GUI is being built
     */
    private void fillInvites(Player sender) {
        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();
        for (Gang gang : gangs) {
            if (gang.getInvitations().contains(sender.getUniqueId())) {
                ItemStack head = new ItemStack(Material.PAPER);
                List<String> lore = new ArrayList<>();

                String name = (gang.getFormattedName()!=null) ? gang.getFormattedName() : gang.getRawName();
                int level = gang.getLevel();
                int memberCount = gang.getAllMembersCount();
                String ownerName = gang.getOwnerName();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy, HH:mm");
                String createdAt = dateFormat.format(gang.getCreatedAt());

                int wins = gang.getFightsWon();
                int loses = gang.getFightsLost();
                double wlr = gang.getWlRatio();

                int kills = gang.getKills();
                int deaths = gang.getDeaths();
                double kdr = gang.getKdRatio();
 
                GuiUtils.addGuiItemInvintation(gui, sender, gang, head, head.getItemMeta(), lore, name, level, memberCount, ownerName, createdAt, wins, loses, wlr, kills, deaths, kdr);
            }
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

        // Button: My Invites
        lore.add("&8-----------------------");
        lore.add("&eCurrently viewing your gang invites");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dYour Gang Invitations"));
        item.setItemMeta(meta);
        item.setType(Material.PAPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Previous Page
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageBack(gui, item, meta, lore, label);
        else gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Next Page
        if (gui.getPagesNum()>=2)  GuiUtils.setGuiItemPageNext(gui, item, meta, lore, label);
        else gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }
}