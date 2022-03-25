package ro.yoshoo.oneblock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Oneblock extends JavaPlugin {
    FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Config configLogic = new Config(this);
        configLogic.start();

        GameData game = new GameData(this);
        game.start();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }
}