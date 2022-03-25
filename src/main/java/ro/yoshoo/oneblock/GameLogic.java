package ro.yoshoo.oneblock;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collections;
import java.util.List;

public class GameLogic extends BukkitRunnable {
    Oneblock plugin;
    List<Player> onlinePlayers;

    public GameLogic(Oneblock instance) {
        plugin = instance;
    }



    @Override
    public void run() {
        onlinePlayers = Config.getDefaultWorld().getPlayers();
        Collections.shuffle(onlinePlayers);
        for (Player player : onlinePlayers){
            String name = player.getName();
        }
    }
}