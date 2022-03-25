package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import ro.yoshoo.oneblock.command.OBCommand;
import ro.yoshoo.oneblock.data.LevelData;
import ro.yoshoo.oneblock.data.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameData {
    Oneblock plugin;
    public GameData(Oneblock instance){
        plugin = instance;
    }

    private static boolean on = false;

    private List<PlayerData> players = new ArrayList<>();
    private List<LevelData> levels = new ArrayList<>();

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        GameData.on = on;
    }

    public List<PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerData> players) {
        this.players = players;
    }

    public List<LevelData> getLevels() {
        return levels;
    }

    public void setLevels(List<LevelData> levels) {
        this.levels = levels;
    }

    private void initCommands(){
        Objects.requireNonNull(plugin.getCommand("oneblock")).setExecutor(new OBCommand(plugin, this));
    }

    public void start(){
        long frequency = plugin.getConfig().getLong("frequency", 7L);
        if(!on) {
            initCommands();
            BukkitTask logic = new GameLogic(plugin, this).runTaskTimer(plugin, frequency, frequency*2);
            setOn(!logic.isCancelled());
            Bukkit.getLogger().info(ChatColor.GREEN + "Game logic started successfully !");
        } else {
            Bukkit.getLogger().info(ChatColor.YELLOW + "Game logic is already running !");
        }
    }
}