package ro.yoshoo.oneblock;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Oneblock extends JavaPlugin {
    FileConfiguration config;
    Config configLogic;
    Game game;

    @Override
    public void onEnable() {
        // Plugin startup logic
        configLogic = new Config(this);
        configLogic.start();
        game = new Game(this, configLogic);
        game.start();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
        Config.savePlayers(this, game);
    }
}