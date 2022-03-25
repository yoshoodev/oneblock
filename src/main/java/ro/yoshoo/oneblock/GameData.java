package ro.yoshoo.oneblock;

import org.bukkit.scheduler.BukkitTask;
import ro.yoshoo.oneblock.data.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    Oneblock plugin;
    public GameData(Oneblock instance){
        plugin = instance;
    }

    boolean on = false;

    private List<PlayerData> players = new ArrayList<>();

    public List<PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerData> players) {
        this.players = players;
    }

    void start(){
        BukkitTask logic = new GameLogic(plugin, this).runTaskTimer(plugin, 64L,64L);
    }
}