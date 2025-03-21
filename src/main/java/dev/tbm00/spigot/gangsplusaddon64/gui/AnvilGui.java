package dev.tbm00.spigot.gangsplusaddon64.gui;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.wesjd.anvilgui.AnvilGUI;

import dev.tbm00.spigot.gangsplusaddon64.GangsPlusAddon64;
import dev.tbm00.spigot.gangsplusaddon64.utils.Utils;

public class AnvilGui {
    
    /**
     * Creates an anvil gui for player to enter text and search shops with.
     */
    public AnvilGui(GangsPlusAddon64 javaPlugin, Player player, String title, String command, String defaultText, Material outputMat) {
        ItemStack leftItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta leftMeta = leftItem.getItemMeta();
        leftMeta.setDisplayName(" ");
        leftMeta.setItemName(" ");
        leftItem.setItemMeta(leftMeta);

        ItemStack rightItem = new ItemStack(Material.NAME_TAG);
        ItemMeta rightMeta = rightItem.getItemMeta();
        rightMeta.setDisplayName(defaultText);
        rightMeta.setItemName(defaultText);
        rightItem.setItemMeta(rightMeta);

        ItemStack outputItem = new ItemStack(outputMat);
        ItemMeta outputMeta = outputItem.getItemMeta();
        outputMeta.setDisplayName("click to enter");
        outputMeta.setItemName("click to enter");
        outputItem.setItemMeta(outputMeta);

        new AnvilGUI.Builder()
            .onClick((slot, stateSnapshot) -> {
                if(slot != AnvilGUI.Slot.OUTPUT || stateSnapshot.getText().isBlank()) {
                    return Collections.emptyList();
                }

                String arr[] = {stateSnapshot.getText()}; 
                while (arr[0].startsWith(" ")) {
                    arr[0] = arr[0].substring(1);
                }

                return Arrays.asList(
                    AnvilGUI.ResponseAction.close(),
                    AnvilGUI.ResponseAction.run(() -> process(player, command, arr[0]))
                );
            })
            .text(" ")
            .itemLeft(leftItem)
            .itemRight(rightItem)
            .itemOutput(outputItem)
            .title(title)
            .plugin(javaPlugin)
            .open(player);
    }

    private void process(Player player, String command, String arg) {
        Utils.sudoCommand(player, command + arg);
    }
}