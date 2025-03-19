package dev.tbm00.spigot.gangsplusaddon64;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.brcdev.gangs.GangsPlugin;
import net.milkbowl.vault.economy.Economy;
import net.slipcor.pvpstats.PVPStats;

import dev.tbm00.spigot.rep64.Rep64;

import dev.tbm00.spigot.gangsplusaddon64.utils.*;
import dev.tbm00.spigot.gangsplusaddon64.command.*;
import dev.tbm00.spigot.gangsplusaddon64.listener.PlayerMovement;

public class GangsPlusAddon64 extends JavaPlugin {
    private ConfigHandler configHandler;
    public static GangsPlugin gangHook;
    public static Economy ecoHook;
    public static Rep64 repHook;
    public static PVPStats pvpHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final PluginDescriptionFile pdf = this.getDescription();

        if (getConfig().contains("enabled") && getConfig().getBoolean("enabled")) {
            configHandler = new ConfigHandler(this);

            Utils.init(this, configHandler);
            GuiUtils.init(this);
            
            Utils.log(ChatColor.LIGHT_PURPLE,
                    ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-",
                    pdf.getName() + " v" + pdf.getVersion() + " created by tbm00",
                    ChatColor.DARK_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-"
            );

            setupHooks();

            if (configHandler.isFeatureEnabled()) {
                // Register Listener
                getServer().getPluginManager().registerEvents(new PlayerMovement(), this);
                
                // Register Commands
                getCommand("ggg").setExecutor(new GangGuiCmd(this, configHandler));
                getCommand("ggga").setExecutor(new AdminGuiCmd(this, configHandler));
            }
        }
    }

    /**
     * Sets up the required hooks for plugin integration.
     * Disables the plugin if any required hook fails.
     */
    private void setupHooks() {

        if (!setupGangsPlus()) {
            getLogger().severe("GangsPlus hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupPVPStats()) {
            getLogger().severe("PVPStats hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupVault()) {
            getLogger().severe("Vault hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }

        if (!setupRep64()) {
            getLogger().severe("Rep64 hook failed -- disabling plugin!");
            disablePlugin();
            return;
        }
    }

    /**
     * Attempts to hook into the GangsPlus plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupGangsPlus() {
        if (!isPluginAvailable("GangsPlus")) return false;

        Plugin gangp = Bukkit.getPluginManager().getPlugin("GangsPlus");
        if (gangp.isEnabled() && gangp instanceof GangsPlugin)
            gangHook = (GangsPlugin) gangp;
        else return false;

        Utils.log(ChatColor.GREEN, "GangsPlus hooked.");
        return true;
    }

    /**
     * Attempts to hook into the GangsPlus plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupPVPStats() {
        if (!isPluginAvailable("GangsPlus")) return false;

        Plugin pvpp = Bukkit.getPluginManager().getPlugin("PVPStats");
        if (pvpp.isEnabled() && pvpp instanceof PVPStats)
            pvpHook = (PVPStats) pvpp;
        else return false;

        Utils.log(ChatColor.GREEN, "PVPStats hooked.");
        return true;
    }

    /**
     * Attempts to hook into the Vault plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupVault() {
        if (!isPluginAvailable("Vault")) return false;

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        ecoHook = rsp.getProvider();
        if (ecoHook == null) return false;

        Utils.log(ChatColor.GREEN, "Vault hooked.");
        return true;
    }

    /**
     * Attempts to hook into the Rep64 plugin.
     *
     * @return true if the hook was successful, false otherwise.
     */
    private boolean setupRep64() {
        if (!isPluginAvailable("Rep64")) return false;

        Plugin rep64 = Bukkit.getPluginManager().getPlugin("Rep64");
        if (rep64.isEnabled() && rep64 instanceof Rep64)
            repHook = (Rep64) rep64;
        else return false;

        Utils.log(ChatColor.GREEN, "Rep64 hooked.");
        return true;
    }

    /**
     * Checks if the specified plugin is available and enabled on the server.
     *
     * @param pluginName the name of the plugin to check
     * @return true if the plugin is available and enabled, false otherwise.
     */
    private boolean isPluginAvailable(String pluginName) {
		final Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
		return plugin != null && plugin.isEnabled();
	}

    /**
     * Disables the plugin.
     */
    private void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        getLogger().info("GangsPlusAddon64 disabled..! ");
    }
}