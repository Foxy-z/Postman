package fr.onecraft.postman;

import fr.onecraft.postman.core.helpers.Configs;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Postman extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info(this.getName() + " has been enabled.");
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Configs.checkFiles(this);
        }, 0, 20 * 10);
    }

    @Override
    public void onDisable() {
        getLogger().info(this.getName() + " has been enabled.");
    }
}