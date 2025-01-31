package net.azisaba.simpleelevator;

import net.azisaba.simpleelevator.listener.CommonListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleElevator extends JavaPlugin {
    public static SimpleElevator INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new CommonListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
