package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class PlayersGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    Gang gang;
    String label;
    List<OfflinePlayer> playerMap;
    int currentSortIndex = 0;
    
    public PlayersGui(GangsPlusAddon64 javaPlugin, Gang gang, Player sender, int sortIndex) {
        this.javaPlugin = javaPlugin;
        this.gang = gang;
        label = gang.getFormattedName()+"§8- ";
        gui = new PaginatedGui(6, 45, gang.getFormattedName());
        currentSortIndex = sortIndex;
        playerMap = new ArrayList<>();
        for (OfflinePlayer player : gang.getAllMembers()) {
            playerMap.add(player);
        }
        
        sortPlayers();
        fillPlayers(sender);
        setupFooter(sender);
        
        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        GuiUtils.disableAll(gui);

        gui.open(sender);
    }
    
    /**
     * Sorts the internal map by the current index
     */
    private void sortPlayers() {
        switch (currentSortIndex) {
            case 0:
                playerMap.sort(Comparator.comparingInt(player -> gang.getMemberData((OfflinePlayer) player).getRank()).reversed());
                break;
            case 1:
                playerMap.sort(Comparator.comparingInt(player -> Utils.getPvpStat("elo", (OfflinePlayer) player)).reversed());
                break;
            case 2:
                playerMap.sort(Comparator.comparingInt(player -> Utils.getPvpStat("kills", (OfflinePlayer) player)).reversed());
                break;
            case 3:
                playerMap.sort(Comparator.comparingInt(player -> Utils.getPvpStat("deaths", (OfflinePlayer) player)).reversed());
                break;
            default:
                break;
        }
    }

    /**
     * Fills the GUI with players from the gang.
     *
     * @param sender the player for whom the GUI is being built
     */
    private void fillPlayers(Player sender) {
        for (OfflinePlayer player : playerMap) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            Utils.applyHeadTexture(head, player);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            List<String> lore = new ArrayList<>();

            String name = player.getName();
            int rank = gang.getMemberData(player).getRank();
            PlayerData data = GangsPlusAddon64.gangHook.getPlayerManager().getPlayerData(player);
            
            int g_kills=0, g_deaths=0;
            double g_kdr=0;
            try {
                g_kills = data.getKills();
                g_deaths = data.getDeaths();
                g_kdr = data.getKdRatio();
            } catch (Exception e) {}

            int p_kills=0, p_deaths=0;
            double p_kdr=0, p_elo=0;
            try {
                p_elo = Utils.getPvpStat("elo", player);
                p_kills = Utils.getPvpStat("kills", player);
                p_deaths = Utils.getPvpStat("deaths", player);
                p_kdr = (p_deaths > 0) ? ((double) p_kills / p_deaths) : 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

            GuiUtils.addGuiItemPlayer(gui, sender, gang, head, headMeta, lore, name, rank, g_kills, g_deaths, g_kdr, p_elo, p_kills, p_deaths, p_kdr, currentSortIndex);
        }
    }

    /**
     * Sets up the footer of the GUI with all, page next, page back, and search buttons.
     */
    private void setupFooter(Player sender) {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // 1 - (my) Gang Management
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(sender)) {
            GuiUtils.setGuiItemManage(gui, item, meta, lore, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(sender));
        } else {
            gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }
        
        // 2 - (my) Gang Homes
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(sender)) {
            GuiUtils.setGuiItemHomes(gui, item, meta, lore, GangsPlusAddon64.gangHook.getGangManager().getPlayersGang(sender));
        } else {
            gui.setItem(6, 2, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }
        
        // 3 - (my) Gang Members
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(sender)) {
            if (gang.isMember(sender)) {
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                Utils.applyHeadTexture(head, sender);
                SkullMeta headMeta = (SkullMeta) head.getItemMeta();
                List<String> headLore = new ArrayList<>();
                headLore.add("&8-----------------------");
                headLore.add("&eCurrently viewing your gang's members");
                headLore.add("&e(sorted by " + GuiUtils.PLAYER_SORT_TYPES[currentSortIndex]+")");
                headMeta.setLore(headLore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
                headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dGang Members"));
                head.setItemMeta(headMeta);
                gui.setItem(6, 3, ItemBuilder.from(head).asGuiItem(event -> {event.setCancelled(true);}));
                headLore.clear();
            } else {
                GuiUtils.setGuiItemMyGang(gui, meta, sender);
            }
        } else gui.setItem(6, 3, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        
        // 4 - empty
        gui.setItem(6, 4, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 5 - All Gangs
        GuiUtils.setGuiItemAllGangs(gui, item, meta, lore);

        // 6 - empty
        gui.setItem(6, 6, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 7 - previous
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageBack(gui, item, meta, lore, label);
        else gui.setItem(6, 7, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 8 - next
        if (gui.getPagesNum()>=2) GuiUtils.setGuiItemPageNext(gui, item, meta, lore, label);
        else gui.setItem(6, 8, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));

        // 9 - sort
        GuiUtils.setGuiItemSortPlayers(gui, item, meta, lore, currentSortIndex, gang);
    }
}