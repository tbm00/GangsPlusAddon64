package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Location;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;

import net.brcdev.gangs.gang.Gang;
import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.*;

public class HomesGui {
    GangsPlusAddon64 javaPlugin;
    PaginatedGui gui;
    Gang gang;
    String label;
    
    public HomesGui(GangsPlusAddon64 javaPlugin, Gang gang, Player sender) {
        this.javaPlugin = javaPlugin;
        this.gang = gang;
        label = "Gang Homes - ";
        gui = new PaginatedGui(6, 45, gang.getFormattedName());
        
        fillHomes(sender);
        setupFooter(sender);
        
        gui.updateTitle(label + gui.getCurrentPageNum() + "/" + gui.getPagesNum());
        gui.disableAllInteractions();
        GuiUtils.disableAll(gui);
        
        gui.open(sender);
    }

    /**
     * Fills the GUI with players from the gang.
     *
     * @param sender the player for whom the GUI is being built
     */
    private void fillHomes(Player sender) {
        Map<String, Location> homes = gang.getHomes();
        int i = 1;
        for (Map.Entry<String, Location> entry : homes.entrySet()) {
            ItemStack item = new ItemStack(Material.GLASS);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();

            String name = entry.getKey();
            Location location = entry.getValue();

            lore.add("&8-----------------------");
            lore.add("&7"+location.getWorld().getName()+": &f"+(int)location.getX()+"&7, &f"
                    +(int)location.getY()+"&7, &f"+(int)location.getZ());
            lore.add("&8-----------------------");
            lore.add("&6Click to TP to gang home: " + name);
            meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d"+name));
            item.setItemMeta(meta);
            item.setAmount(i);

            gui.addItem(ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleHomeClick(event, gui, (Player) event.getWhoClicked(), name)));

            lore.clear();
            ++i;
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
        if (GangsPlusAddon64.gangHook.getGangManager().isInGang(sender) && gang.isMember(sender)) {
            GuiUtils.setGuiItemManage(gui, item, meta, lore, gang);
        } else {
            gui.setItem(6, 1, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
        }
        
        // 2 - homes
        lore.add("&8-----------------------");
        lore.add("&eCurrently viewing your gang's home(s)");
        meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).toList());
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&dGang Homes"));
        item.setItemMeta(meta);
        item.setType(Material.COMPASS);
        gui.setItem(6, 2, ItemBuilder.from(item).asGuiItem(event -> GuiUtils.handleHomesClick(event, (Player) event.getWhoClicked(), gang)));
        lore.clear();
        
        // 3 - My Gang
        GuiUtils.setGuiItemMyGang(gui, meta, sender);
        
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

        // 9 - empty
        gui.setItem(6, 9, ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).setName(" ").asGuiItem(event -> event.setCancelled(true)));
    }
}