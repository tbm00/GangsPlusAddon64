package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.ChatColor;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;

import net.brcdev.gangs.gang.Gang;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class ManageGui {
    GangsPlusAddon64 javaPlugin;
    Gui gui;
    String label;
    Player player;
    Gang givenGang;
    
    public ManageGui(GangsPlusAddon64 javaPlugin, Player player) {
        this.player = player;
        this.javaPlugin = javaPlugin;
        label = "Gang Management";
        gui = new Gui(6, label);

        try {
            givenGang = GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(player);
        } catch (Exception e) {
            Utils.log(ChatColor.RED, "Caught exception getting gang for player " + player.getName());
            e.printStackTrace();
            return;
        }

        setGuiItemAllies();
        setGuiItemFriendlyFire();
        setGuiItemPerms();
        setGuiItemCmds();
        setGuiItemDeposit();
        setGuiItemBalance();
        setGuiItemLevelUp();
        setGuiItemGangDisplay();
        
        setupFooter();

        gui.disableAllInteractions();
        GuiUtils.disableAll(gui);

        gui.open(player);
    }

    /**
     * Sets up the GUI tools and footer buttons.
     * 
     * Initializes allies, friendly fire, permissions, commands, deposit, balance,
     * level up and gang display items along with additional static items.
     */
    private void setupFooter() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // 1 - Gang Management
        lore.add("&8-----------------------");
        lore.add("&eCurrently viewing gang management GUI");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dGang Management"));
        item.setType(Material.CHEST);
        item.setItemMeta(meta);
        gui.setItem(6, 1, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();
        
        // 2 - Gang Homes
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player) && givenGang.isMember(player)) {
            GuiUtils.setGuiItemHomes(gui, item, meta, lore, givenGang);
        } else {
            gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }
        
        // 3 - My Gang
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(player)) {
            GuiUtils.setGuiItemMyGang(gui, meta, player);
        } else gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        
        // 4 - empty
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 5 - All Gangs
        GuiUtils.setGuiItemAllGangs(gui, item, meta, lore);

        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }

    /**
     * Configures the GUI item for viewing allied gangs.
     * 
     * Sets the item appearance and click action to open the allies menu.
     */
    public void setGuiItemAllies() {
        ItemStack item = new ItemStack(Material.POPPY);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&6Click to view your gang's allies");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eAllied Gangs"));
        item.setItemMeta(meta);
        gui.setItem(2, 2, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleAllyMenuClick(event, givenGang)));
        lore.clear();
    }

    /**
     * Configures the GUI item for toggling friendly fire.
     * 
     * Sets the item appearance and click action to toggle the friendly fire setting.
     */
    public void setGuiItemFriendlyFire() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&6Click to toggle your gang's friendly-fire");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eFriendly Fire: &f" + givenGang.isFriendlyFire()));
        item.setItemMeta(meta);
        gui.setItem(2, 3, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleFriendlyFireClick(gui, event, givenGang)));
        lore.clear();
    }

    /**
     * Configures the GUI item for displaying the gang information.
     * 
     * Sets up a player head item with gang details including name, level, member count,
     * owner, creation date, kills, deaths, win/loss ratio and KDR.
     */
    public void setGuiItemGangDisplay() {
        /*define item button's lore, name, flags, etc*/
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        List<String> lore = new ArrayList<>();
        Utils.applyHeadTexture(headMeta, givenGang.getOwner());
        
        String name = (givenGang.getFormattedName()!=null) ? givenGang.getFormattedName() : givenGang.getRawName();
        int level = givenGang.getLevel();
        int memberCount = givenGang.getAllMembersCount();
        String ownerName = givenGang.getOwnerName();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy, HH:mm");
        String createdAt = dateFormat.format(givenGang.getCreatedAt());
        
        int wins = givenGang.getFightsWon();
        int loses = givenGang.getFightsLost();
        double wlr = givenGang.getWlRatio();

        int kills = givenGang.getKills();
        int deaths = givenGang.getDeaths();
        double kdr = givenGang.getKdRatio();
        
        GuiUtils.setGuiItemGang(gui, player, givenGang, head, headMeta, lore, name, level, memberCount, ownerName, createdAt, wins, loses, wlr, kills, deaths, kdr);
    }

    /**
     * Configures the GUI item for viewing gang rank permissions.
     * 
     * Sets the item appearance and click action to open the permissions menu.
     */
    public void setGuiItemPerms() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&6Click to view gang rank permissions");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eRank Permissions"));
        item.setItemMeta(meta);
        gui.setItem(2, 7, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handlePermsClick(event, givenGang)));
        lore.clear();
    }

    /**
     * Configures the GUI item for displaying available gang commands.
     * 
     * Sets the item appearance and provides a list of gang commands as lore.
     */
    public void setGuiItemCmds() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&7/g create <name>" );
        lore.add("&7/g invite/uninvite <player>" );
        lore.add("&7/g promote/demote <player>");
        lore.add("&7/g deposit/withdraw <#>");
        lore.add("&7/g home/sethome <home>");
        lore.add("&7/g kick <player>");
        lore.add("&7/g leave");
        lore.add("&7/g join <gang>");
        lore.add("&f/g help");
        lore.add("&f/help fight" );
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eGang Commands"));
        item.setItemMeta(meta);
        gui.setItem(2, 8, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();
    }

    /**
     * Configures the GUI item for depositing money into the gang bank.
     * 
     * Sets the item appearance and click action to open the deposit interface.
     */
    public void setGuiItemDeposit() {
        ItemStack item = new ItemStack(Material.STONE_BUTTON);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&6Click to deposit money into your gang's bank");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eDeposit $ into Gang Bank"));
        item.setItemMeta(meta);
        gui.setItem(4, 4, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleDepositClick(event, givenGang)));
        lore.clear();
    }

    /**
     * Configures the GUI item for displaying the gang bank balance.
     * 
     * Sets the item appearance and prevents interaction with the balance item.
     */
    public void setGuiItemBalance() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&a$" + Utils.formatInt(givenGang.getBankMoney()));
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eGang Bank Balance"));
        item.setItemMeta(meta);
        gui.setItem(4, 5, ItemBuilder.from(item).asGuiItem(event -> {event.setCancelled(true);}));
        lore.clear();
    }

    /**
     * Configures the GUI item for leveling up the gang.
     * 
     * Sets the item appearance, displays level up requirements and action,
     * and adds item flags to hide additional details.
     */
    public void setGuiItemLevelUp() {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add("&8-----------------------");
        lore.add("&fLevel up your gang to increase the");
        lore.add("&fplayer limit and set more homes");
        lore.add("&8-----------------------");
        lore.add("&7Level 1: 4 players, 2 homes");
        lore.add("&7Level 2: 8 players, 4 homes");
        lore.add("&7Level 3: 16 players, 6 homes");
        lore.add("&7Level 4: 32 players, 8 homes");
        lore.add("&7Level 5: 64 players, 10 homes");
        lore.add("&8-----------------------");
        

        switch (givenGang.getLevel()) {
            case 1:
                lore.add("&6Click to level up your gang");
                lore.add("&6for &a$2,000,000");
                break;
            case 2:
                lore.add("&6Click to level up your gang");
                lore.add("&6for &a$20,00,000");
                break;
            case 3:
                lore.add("&6Click to level up your gang");
                lore.add("&6for &a$200,000,000");
                break;
            case 4:
                lore.add("&6Click to level up your gang");
                lore.add("&6for &a$2,000,000,000");
                break;
            case 5:
                lore.add("&cGang is at max level!");
                break;
            default:
                break;
        }
        
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eLevel Up Gang"));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        gui.setItem(4, 6, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleLevelUpClick(gui, event, givenGang)));
        lore.clear();
    }
}