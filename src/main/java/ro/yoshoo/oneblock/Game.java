package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitTask;
import ro.yoshoo.oneblock.command.OBCommand;
import ro.yoshoo.oneblock.data.LevelData;
import ro.yoshoo.oneblock.data.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game {
    Oneblock plugin;
    Config config;
    public Game(Oneblock instance, Config configInstance){
        plugin = instance;
        config = configInstance;
    }

    private static boolean on = false;

    private List<PlayerData> players = new ArrayList<>();
    private List<LevelData> levels = new ArrayList<>();

    public static boolean isOn() {
        return on;
    }

    public static void setOn(boolean on) {
        Game.on = on;
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

    private void initBossBar(){
        if(!getPlayers().isEmpty()) {
            for (PlayerData player : getPlayers()) {
                if (player.getBossbar() == null) {
                    LevelData level = getLevels().get(player.getLevel());
                    String name = level.getName();
                    BarColor color = level.getColor();
                    BossBar tempBar = Bukkit.createBossBar(name, color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
                    tempBar.setProgress(player.getBreaks());
                    player.setBossbar(tempBar);
                }
            }
        }
    }




    public void start(){
        getLevels().clear();
        getPlayers().clear();
        long frequency = plugin.getConfig().getLong("frequency", 7L);
        config.loadLevels(this);
        config.loadPlayers(this);
        initBossBar();
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