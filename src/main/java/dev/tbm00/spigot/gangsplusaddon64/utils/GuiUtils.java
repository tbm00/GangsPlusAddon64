package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.gui.*;

public class GuiUtils {
    private static GangsPlusAddon64 javaPlugin;
    public static final List<String> pendingTeleports = new CopyOnWriteArrayList<>();
    public final static String[] GANG_SORT_TYPES = {"Member Count", "Age", "KDR", "Kills", "Deaths", "Fight WLR", "Fight Wins", "Fight Loses"};
    public final static String[] PLAYER_SORT_TYPES = {"Gang Rank", "Elo Score", "PVP Kills", "PVP Deaths"};
    public final static String[] GANG_RANKS = {"Thug", "Gangstar", "Capo", "Kingpin"};

    public static void init(GangsPlusAddon64 javaPlugin) {
        GuiUtils.javaPlugin = javaPlugin;
    }

    /**
     * Handles the event when sort gangs button is clicked.
     * 
     * @param event the inventory click event
     * @param type integer for sort alg
     */
    public static void handleSortGangsClick(InventoryClickEvent event, int type) {
        event.setCancelled(true);
        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();
        new GangsGui(javaPlugin, gangs, (Player) event.getWhoClicked(), type);
    }

    /**
     * Handles the event when sort gangs button is clicked.
     * 
     * @param event the inventory click event
     * @param type integer for sort alg
     */
    public static void handleSortGangsAdminClick(InventoryClickEvent event, int type) {
        event.setCancelled(true);
        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();
        new AdminGui(javaPlugin, gangs, (Player) event.getWhoClicked(), type);
    }

    /**
     * Handles the event when sort players button is clicked.
     * 
     * @param event the inventory click event
     * @param type integer for sort alg
     * @param gang the gang to show players for
     */
    public static void handleSortPlayersClick(InventoryClickEvent event, int type, Gang gang) {
        event.setCancelled(true);
        new PlayersGui(javaPlugin, gang, (Player) event.getWhoClicked(), type);
    }

    /**
     * Handles the event when the all gangs item in the GUI is clicked.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the gang item
     * @param gang the gang associated with the clicked item
     */
    public static void handleAllClick(InventoryClickEvent event, Player sender) {
        event.setCancelled(true);
        List<Gang> gangs = GangsPlusAddon64.gangHook.getGangManager().getAllGangs();
        new GangsGui(javaPlugin, gangs, sender, 0);
    }

    /**
     * Handles the event when a page button is clicked.
     * 
     * @param event the inventory click event
     * @param next true to go to the next page; false to go to the previous page
     */
    public static void handlePageClick(InventoryClickEvent event, PaginatedGui gui, boolean next, String label) {
        event.setCancelled(true);
        if (next) gui.next();
        else gui.previous();
        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
    }

    /**
     * Handles the event when a gang item in the GUI is clicked.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the gang item
     * @param gang the gang associated with the clicked item
     */
    public static void handleGangClick(InventoryClickEvent event, Player sender, Gang gang) {
        event.setCancelled(true);
        
        new PlayersGui(javaPlugin, gang, sender, 0);
    }

    /**
     * Handles the event when a gang item in the GUI is clicked.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the gang item
     * @param gang the gang associated with the clicked item
     */
    public static void handleGangAdminClick(InventoryClickEvent event, Player sender, Gang gang) {
        event.setCancelled(true);

        if (event.isShiftClick()) {
            Utils.sudoCommand(sender, "gadmin disband " + gang.getRawName());
        } else new PlayersGui(javaPlugin, gang, sender, 0);
    }

    /**
     * Handles the event when a gang home item in the GUI is clicked.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the gang item
     * @param gang the gang w the homes
     */
    public static void handleHomesClick(InventoryClickEvent event, Player sender, Gang gang) {
        event.setCancelled(true);
        
        new HomesGui(javaPlugin, gang, sender);
    }

    /**
     * Handles the event when a gang home item in the GUI is clicked.
     * 
     * @param event the inventory click event
     * @param gui the inventory gui
     * @param sender the player who clicked the gang item
     * @param home the name of the associated home
     */
    public static void handleHomeClick(InventoryClickEvent event, PaginatedGui gui, Player sender, String home) {
        event.setCancelled(true);
        
        Utils.sudoCommand(sender, "g home " + home);
        gui.close(sender);
    }

    /**
     * Sets the gang GUI's footer's sort button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     */
    public static void setGuiItemAllGangs(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore) {
        lore.add("&8-----------------------");
        lore.add("&6Click to view all gangs");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dAll Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.BOOK);
        gui.setItem(6, 5, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleAllClick(event, (Player) event.getWhoClicked())));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's sort button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param type integer of the sort alg
     */
    public static void setGuiItemSortGangs(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, int type) {
        int next = (type==7) ? 0 : type+1;

        lore.add("&8-----------------------");
        lore.add("&6Click to change sort order");
        lore.add("&6("+ GANG_SORT_TYPES[type] + " -> " + GANG_SORT_TYPES[next] + ")");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dSort Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.HOPPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleSortGangsClick(event, next)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's sort button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param type integer of the sort alg
     */
    public static void setGuiItemSortGangsAdmin(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, int type) {
        int next = (type==7) ? 0 : type+1;

        lore.add("&8-----------------------");
        lore.add("&6Click to change sort order");
        lore.add("&6("+ GANG_SORT_TYPES[type] + " -> " + GANG_SORT_TYPES[next] + ")");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dSort Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.HOPPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleSortGangsAdminClick(event, next)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's sort button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param type integer of the sort alg
     * @param gang the gang to show players for
     */
    public static void setGuiItemSortPlayers(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, int type, Gang gang) {
        int next = (type==3) ? 0 : type+1;

        lore.add("&8-----------------------");
        lore.add("&6Click to change sort order");
        lore.add("&6("+ PLAYER_SORT_TYPES[type] + " -> " + PLAYER_SORT_TYPES[next] + ")");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dSort Players"));
        item.setItemMeta(meta);
        item.setType(Material.HOPPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleSortPlayersClick(event, next, gang)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer button: my gang.
     *
     * @param gui the gui that will be sent to the player
     * @param meta holder for current item's meta
     * @param player player to open for
     */
    public static void setGuiItemMyGang(PaginatedGui gui, ItemMeta meta, Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        List<String> lore = new ArrayList<>();
        headMeta.setOwningPlayer(player);
        lore.add("&8-----------------------");
        lore.add("&6Click to view your gang");
        headMeta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dYour Gang"));
        head.setItemMeta(headMeta);
        gui.setItem(6, 3, ItemBuilder.from(head).asGuiItem(event -> GuiUtils.handleGangClick(event, player, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player))));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's previous page button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param label holder for gui's title
     */
    public static void setGuiItemPageBack(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, String label) {
        lore.add("&8-----------------------");
        lore.add("&6Click to go to the previous page");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fPrevious Page"));
        item.setItemMeta(meta);
        item.setType(Material.STONE_BUTTON);
        gui.setItem(6, 8, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handlePageClick(event, gui, false, label)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's next page button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param label holder for gui's title
     */
    public static void setGuiItemPageNext(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, String label) {
        lore.add("&8-----------------------");
        lore.add("&6Click to go to the next page");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&fNext Page"));
        item.setItemMeta(meta);
        item.setType(Material.STONE_BUTTON);
        gui.setItem(6, 9, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handlePageClick(event, gui, true, label)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer's previous page button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param gang the gang owning the home
     */
    public static void setGuiItemHomes(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, Gang gang) {
        lore.add("&8-----------------------");
        lore.add("&6Click to TP to your gang's home(s)");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dGang Homes"));
        item.setItemMeta(meta);
        item.setType(Material.COMPASS);
        gui.setItem(6, 2, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleHomesClick(event, (Player) event.getWhoClicked(), gang)));
        lore.clear();
    }

    /**
     * Formats and adds an gang to the gang GUI.
     *
     * @param gui the paginated GUI to which the item will be added
     * @param sender the player viewing the gang
     * @param gang the gang associated with the item
     * @param item the head to be displayed in the GUI
     * @param meta the metadata of the item
     * @param lore the list of lore descriptions to be displayed
     * @param name the gang's name
     * @param level the gang's current
     * @param memberCount the gang's current
     * @param ownerName the gang's current
     * @param createdAt the gang's creation
     * @param wins the gang's current
     * @param loses the gang's current
     * @param wlr the gang's current
     * @param kill the gang's current
     * @param deaths the gang's current
     * @param kdr the gang's current
     */
    public static void addGuiItemGang(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int level, int memberCount, String ownerName, String createdAt, int wins, int loses, double wlr, int kills, int deaths, double kdr) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&f" + memberCount + " &7members, " + ownerName);
        lore.add("&7Created " + createdAt);
        lore.add("");
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.1f", kdr));
        lore.add("&7Wins: &f" + wins + "&7, Loses: &f" + loses + "&7, Ratio: &f" + String.format("%.1f", wlr));
        lore.add("&8-----------------------");
        lore.add("&6Click to view gang members");

        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + "&7- Lvl. " + level));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        item.setAmount(1);

        gui.addItem(ItemBuilder.from(item).asGuiItem(event -> handleGangClick(event, sender, gang)));
    }

    /**
     * Formats and adds an gang to the gang GUI.
     *
     * @param gui the paginated GUI to which the item will be added
     * @param sender the player viewing the gang
     * @param gang the gang associated with the item
     * @param item the head to be displayed in the GUI
     * @param meta the metadata of the item
     * @param lore the list of lore descriptions to be displayed
     * @param name the gang's name
     * @param level the gang's current
     * @param memberCount the gang's current
     * @param ownerName the gang's current
     * @param createdAt the gang's creation
     * @param wins the gang's current
     * @param loses the gang's current
     * @param wlr the gang's current
     * @param kill the gang's current
     * @param deaths the gang's current
     * @param kdr the gang's current
     */
    public static void addAdminGuiItemGang(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int level, int memberCount, String ownerName, String createdAt, int wins, int loses, double wlr, int kills, int deaths, double kdr) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&f" + memberCount + " &7members, " + ownerName);
        lore.add("&7Created " + createdAt);
        lore.add("");
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.1f", kdr));
        lore.add("&7Wins: &f" + wins + "&7, Loses: &f" + loses + "&7, Ratio: &f" + String.format("%.1f", wlr));
        lore.add("&8-----------------------");
        lore.add("&6Click to view gang members");
        lore.add("&eShift-Click to disband the gang");

        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + "&7- Lvl. " + level));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        item.setAmount(1);

        gui.addItem(ItemBuilder.from(item).asGuiItem(event -> handleGangAdminClick(event, sender, gang)));
    }

    /**
     * Formats and adds an player to the gang GUI.
     *
     * @param gui the paginated GUI to which the item will be added
     * @param sender the player viewing the gang
     * @param gang the gang to be viewed
     * @param item the head to be displayed in the GUI
     * @param meta the metadata of the item
     * @param lore the list of lore descriptions to be displayed
     * @param name the gang's name
     * @param rank the gang's current
     * @param g_kill the player's current
     * @param g_deaths the player's current
     * @param g_kdr the player's current 
     * @param p_elo the player's current
     * @param p_kill the player's current
     * @param p_deaths the player's current
     * @param p_kdr the player's current 
     */
    public static void addGuiItemPlayer(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int rank, int g_kills, int g_deaths, double g_kdr, double p_elo, int p_kills, int p_deaths, double p_kdr) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&7Gang Rank: &f" + GANG_RANKS[rank-1]);
        lore.add("&7Elo: &f"+p_elo);

        lore.add("");
        lore.add("          &7&oPVP & PVE");
        lore.add("&7Kills: &f" + g_kills + "&7, Deaths: &f" + g_deaths + "&7, Ratio: &f" + String.format("%.1f", g_kdr));
        
        lore.add("");
        lore.add("          &7&oPVP Only");
        lore.add("&7Kills: &f" + p_kills + "&7, Deaths: &f" + p_deaths + "&7, Ratio: &f" + String.format("%.1f", p_kdr));
        
        //lore.add("&8-----------------------");
        //lore.add("&6Click to ");

        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        item.setItemMeta(meta);
        item.setAmount(1);

        gui.addItem(ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
    }
}