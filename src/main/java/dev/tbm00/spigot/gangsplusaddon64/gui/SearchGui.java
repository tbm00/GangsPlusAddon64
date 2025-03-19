package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.wesjd.anvilgui.AnvilGUI;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.GangUtils;

public class SearchGui {
    
    /**
     * Creates an anvil gui for player to enter text and search shops with.
     */
    public SearchGui(GangsPlusAddon64 javaPlugin, Player player) {
        ItemStack leftItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta leftMeta = leftItem.getItemMeta();
        leftMeta.setDisplayName(" ");
        leftMeta.setItemName(" ");
        leftItem.setItemMeta(leftMeta);

        ItemStack rightItem = new ItemStack(Material.NAME_TAG);
        ItemMeta rightMeta = rightItem.getItemMeta();
        rightMeta.setDisplayName("gang or player");
        rightMeta.setItemName("gang or player");
        rightItem.setItemMeta(rightMeta);

        ItemStack outputItem = new ItemStack(Material.HOPPER);
        ItemMeta outputMeta = outputItem.getItemMeta();
        outputMeta.setDisplayName("click to search");
        outputMeta.setItemName("click to search");
        outputItem.setItemMeta(outputMeta);

        new AnvilGUI.Builder()
            .onClick((slot, stateSnapshot) -> {
                if(slot != AnvilGUI.Slot.OUTPUT || stateSnapshot.getText().isBlank()) {
                    return Collections.emptyList();
                }

                String arr[] = {stateSnapshot.getText()}; 

                return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> GangUtils.handleSearch(player, arr))
                );
            })
            .text(" ")
            .itemLeft(leftItem)
            .itemRight(rightItem)
            .itemOutput(outputItem)
            .title("Search All Gangs")
            .plugin(javaPlugin)
            .open(player);
    }
}