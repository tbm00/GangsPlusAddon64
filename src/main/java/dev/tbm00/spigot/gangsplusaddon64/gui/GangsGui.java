package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.ChatColor;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class GangsGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    String label;
    Player player;
    
    public GangsGui(GangsPlusAddon64 javaPlugin, List<Gang> gangMap, Player player) {
        this.player = player;
        this.javaPlugin = javaPlugin;
        label = "All Gangs - ";
        gui = new PaginatedGui(6, 45, "All Gangs");
        
        fillGangs(gangMap, player);
        setupFooter();

        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        gui.open(player);

        
    }

    /**
     * Fills the GUI with items from the gang map.
     * Each gang that has a valid gang item and pricing information is converted into a clickable GUI item.
     *
     * @param gangMap a concurrent hash map of gang identifiers to Gang objects
     * @param player the player for whom the GUI is being built
     */
    private void fillGangs(List<Gang> gangMap, Player player) {
        for (Gang gang : gangMap) {
            /*check if valid & active gang*/ 
                if (gang.getAllMembersCount()<=0) continue;

            /*define item button's lore, name, flags, etc*/
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                List<String> lore = new ArrayList<>();
                headMeta.setOwningPlayer(gang.getOwner());
                
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
                
                GuiUtils.addGuiItemGang(gui, player, gang, head, headMeta, lore, name, level, memberCount, ownerName, createdAt, wins, loses, wlr, kills, deaths, kdr);
        }
    }

    /**
     * Sets up the footer of the GUI with categories & all other buttons.
     */
    private void setupFooter() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Button: All Gangs
        lore.add("&8-----------------------");
        lore.add("&eCurrently viewing all gangs");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dAll Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.CHEST);
        gui.setItem(6, 4, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        // Search
        GuiUtils.setGuiItemSearch(gui, item, meta, lore);

        // Button: My Gang / My Invites
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player)) {
            GuiUtils.setGuiItemMyGang(gui, item, meta, lore, player);
        } else {
            GuiUtils.setGuiItemMyInvites(gui, item, meta, lore, player);
        }

        gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Previous Page
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageBack(gui, item, meta, lore, label);
        else gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // Next Page
        if (gui.getPagesNum()>=2)  GuiUtils.setGuiItemPageNext(gui, item, meta, lore, label);
        else gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }
}