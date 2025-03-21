package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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

public class AdminGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    String label;
    Player player;
    List<Gang> gangMap;
    int currentSortIndex = 0;
    
    public AdminGui(GangsPlusAddon64 javaPlugin, List<Gang> gangs, Player player, int sortIndex) {
        this.player = player;
        this.javaPlugin = javaPlugin;
        this.gangMap = new ArrayList<>(gangs);
        label = "Admin Gui - ";
        gui = new PaginatedGui(6, 45, "Admin Gui");
        currentSortIndex = sortIndex;
        
        sortGangs();
        fillGangs();
        setupFooter();

        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        GuiUtils.disableAll(gui);

        gui.open(player);
    }

    /**
     * Sorts the internal map by the current index
     */
    private void sortGangs() {
        gangMap.sort(Comparator.comparingLong(Gang::getCreatedAt));
        gangMap.sort(Comparator.comparingInt(Gang::getKills).reversed());
        if (currentSortIndex==0) gangMap.sort(Comparator.comparingInt(Gang::getAllMembersCount).reversed());
        else if (currentSortIndex==1) gangMap.sort(Comparator.comparingLong(Gang::getCreatedAt));
        else if (currentSortIndex==2) gangMap.sort(Comparator.comparingDouble(Gang::getKdRatio).reversed());
        //else if (currentSortIndex==3) gangMap.sort(Comparator.comparingInt(Gang::getKills).reversed());
        else if (currentSortIndex==4) gangMap.sort(Comparator.comparingInt(Gang::getDeaths).reversed());
        else if (currentSortIndex==5) gangMap.sort(Comparator.comparingDouble(Gang::getWlRatio).reversed());
        else if (currentSortIndex==6) gangMap.sort(Comparator.comparingInt(Gang::getFightsWon).reversed());
        else if (currentSortIndex==7) gangMap.sort(Comparator.comparingInt(Gang::getFightsLost).reversed());
    }

    /**
     * Fills the GUI with items from the gang map.
     * Each gang that has a valid gang item and pricing information is converted into a clickable GUI item.
     */
    private void fillGangs() {
        for (Gang gang : gangMap) {
            /*check if valid & active gang*/ 
                if (gang.getAllMembersCount()<=0) continue;

            /*define item button's lore, name, flags, etc*/
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                Utils.applyHeadTexture(head, gang.getOwner());
                SkullMeta headMeta = (SkullMeta) head.getItemMeta();
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
                
                GuiUtils.addGuiItemGangAdmin(gui, player, gang, head, headMeta, lore, name, level, memberCount, ownerName, createdAt, wins, loses, wlr, kills, deaths, kdr);
        }
    }

    /**
     * Sets up the footer of the GUI with categories & all other buttons.
     */
    private void setupFooter() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // 1 - (my) Gang Management
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player)) {
            GuiUtils.setGuiItemManage(gui, item, meta, lore, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player));
        } else {
            gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }
        
        // 2 - (my) Gang Homes
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player)) {
            GuiUtils.setGuiItemHomes(gui, item, meta, lore, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player));
        } else {
            gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }

        // 3 - My Gang
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player))
            GuiUtils.setGuiItemMyGang(gui, meta, player);
        else gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 4 - empty
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 5 - All Gangs
        lore.add("&8-----------------------");
        lore.add("&eCurrently viewing all gangs");
        lore.add("&e(sorted by " + GuiUtils.GANG_SORT_TYPES[currentSortIndex]+")");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dAll Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.BOOK);
        gui.setItem(6, 5, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();

        // 6 - empty
        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 7 - previous
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageBack(gui, item, meta, lore, label);
        else gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 8 - next
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageNext(gui, item, meta, lore, label);
        else gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 9 - sort
        GuiUtils.setGuiItemSortGangsAdmin(gui, item, meta, lore, currentSortIndex);
    }
}