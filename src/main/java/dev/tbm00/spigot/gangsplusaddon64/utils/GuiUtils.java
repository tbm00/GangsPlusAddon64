package dev.tbm00.spigot.gangsplusaddon64.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.gui.*;

public class GuiUtils {
    private static GangsPlusAddon64 javaPlugin;
    public static final List<String> pendingTeleports = new CopyOnWriteArrayList<>();

    public static void init(GangsPlusAddon64 javaPlugin) {
        GuiUtils.javaPlugin = javaPlugin;
    }

    /**
     * Handles the event when search button is clicked.
     * 
     * @param event the inventory click event
     */
    public static void handleSearchClick(InventoryClickEvent event) {
        event.setCancelled(true);
        new SearchGui(javaPlugin, (Player) event.getWhoClicked());
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
        new GangsGui(javaPlugin, gangs, sender);
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
        
        new PlayersGui(javaPlugin, gang, sender);
    }

    /**
     * Handles the event when invite gui is clicked open.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the gang item
     */
    public static void handleMyInvitesClick(InventoryClickEvent event, Player sender) {
        event.setCancelled(true);
        
        new InvitesGui(javaPlugin, sender);
    }

    /**
     * Handles the event when invite gui is clicked open.
     * 
     * @param event the inventory click event
     * @param sender the player who clicked the invite item
     * @param gang the gang the player is in 
     */
    public static void handleOpenInvitePlayersClick(InventoryClickEvent event, Player sender, Gang gang) {
        event.setCancelled(true);
        
        new InvitePlayersGui(javaPlugin, gang, sender);
    }

    /**
     * Handles the event when an invite was accpted clicked.
     * 
     * @param event the inventory click event
     * @param gangName the name of the gang to join
     */
    public static void handleInviteAcceptClick(InventoryClickEvent event, String gangName) {
        event.setCancelled(true);
        Utils.sudoCommand(event.getWhoClicked(), "g join " + gangName);
    }

    /**
     * Handles the event when an invite was given clicked.
     * 
     * @param event the inventory click event
     * @param playerName the name of the player to invite
     */
    public static void handleInviteSendClick(InventoryClickEvent event, String playerName) {
        event.setCancelled(true);
        Utils.sudoCommand(event.getWhoClicked(), "g invite " + playerName);
    }

    /**
     * Sets the gang GUI's footer's search page button format.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     */
    public static void setGuiItemSearch(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore) {
        lore.add("&8-----------------------");
        lore.add("&6Click to search for a specific item");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dSearch Gangs"));
        item.setItemMeta(meta);
        item.setType(Material.NAME_TAG);
        gui.setItem(6, 5, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleSearchClick(event)));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer button: my gang.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param player player to open for
     */
    public static void setGuiItemMyGang(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, Player player) {
        lore.add("&8-----------------------");
        lore.add("&6Click to inspect your gang");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dYour Gang"));
        item.setItemMeta(meta);
        item.setType(Material.PAPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleGangClick(event, player, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player))));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer button: my gang.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param player player to open for
     */
    public static void setGuiItemInvitePlayers(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, Player player) {
        lore.add("&8-----------------------");
        lore.add("&6Click to invite players to your gang");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dInvite Players"));
        item.setItemMeta(meta);
        item.setType(Material.PAPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleOpenInvitePlayersClick(event, player, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player))));
        lore.clear();
    }

    /**
     * Sets the gang GUI's footer button: my invites.
     *
     * @param gui the gui that will be sent to the player
     * @param item holder for current item
     * @param meta holder for current item's meta
     * @param lore holder for current item's lore
     * @param player player to open for
     */
    public static void setGuiItemMyInvites(PaginatedGui gui, ItemStack item, ItemMeta meta, List<String> lore, Player player) {
        lore.add("&8-----------------------");
        lore.add("&6Click to see your gang invites");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dYour Gang Invitations"));
        item.setItemMeta(meta);
        item.setType(Material.PAPER);
        gui.setItem(6, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleMyInvitesClick(event, player)));
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
        lore.add("&7Wins: &f" + wins + "&7, Loses: &f" + loses + "&7, Ratio: &f" + String.format("%.2f", wlr));
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.2f", kdr));
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
     * @param assists the player's current
     * @param kill the player's current
     * @param deaths the player's current
     * @param kdr the player's current 
     */
    public static void addGuiItemPlayer(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int rank, int assists, int kills, int deaths, double kdr) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&7Rank: &f" + rank);
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.2f", kdr));
        lore.add("&7Assists: "+assists);
        lore.add("&8-----------------------");
        lore.add("&6Click to view gang members");

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
     * @param assists the player's current
     * @param kill the player's current
     * @param deaths the player's current
     * @param kdr the player's current 
     * @param target the player we might invite
     */
    public static void addGuiItemInvite(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int assists, int kills, int deaths, double kdr, Player target) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.2f", kdr));
        lore.add("&7Assists: "+assists);
        lore.add("&8-----------------------");
        lore.add("&6Click to invite to your gang");

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

        if (!gang.isMember(target)) return;

        gui.addItem(ItemBuilder.from(item).asGuiItem(event -> handleInviteSendClick(event, target.getName())));
    }

    /**
     * Formats and adds an invite to the gang GUI.
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
    public static void addGuiItemInvintation(PaginatedGui gui, Player sender, Gang gang, ItemStack item, ItemMeta meta, List<String> lore, String name, int level, int memberCount, String ownerName, String createdAt, int wins, int loses, double wlr, int kills, int deaths, double kdr) {
        meta.setLore(null);
        lore.add("&8-----------------------");
        lore.add("&f" + memberCount + " &7members, " + ownerName);
        lore.add("&7Created " + createdAt);
        lore.add("");
        lore.add("&7Wins: &f" + wins + "&7, Loses: &f" + loses + "&7, Ratio: &f" + String.format("%.2f", wlr));
        lore.add("&7Kills: &f" + kills + "&7, Deaths: &f" + deaths + "&7, Ratio: &f" + String.format("%.2f", kdr));
        lore.add("&8-----------------------");
        lore.add("&aClick to accept invite and join gang");

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

        gui.addItem(ItemBuilder.from(item).asGuiItem(event -> handleInviteAcceptClick(event, gang.getRawName())));
    }
}