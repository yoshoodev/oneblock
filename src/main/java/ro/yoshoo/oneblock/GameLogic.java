package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Collections;
import java.util.List;

public class GameLogic extends BukkitRunnable {
    Oneblock plugin;
    GameData data;
    List<Player> onlinePlayers;

    public GameLogic(Oneblock instance, GameData dataInstance) {
        plugin = instance;
        data = dataInstance;
    }

    private boolean existPlayer(String name){
        for(PlayerData player : data.getPlayers()){
            if(player.getUsername() == null){
                continue;
            }
            if(player.getUsername().equals(name)){
                return true;
            }
            if (player.getAllies().contains(name)){
                return true;
            }
        }
        return false;
    }

    private int getID(String name){
        for(int i = 0; i < data.getPlayers().size(); i++){
            PlayerData player = data.getPlayers().get(i);
            if(player.getUsername() == null){
                continue;
            }
            if(player.getUsername().equals(name)){
                return i;
            }
            if (player.getAllies().contains(name)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void run() {
        if(!data.on){
            try {
                this.cancel();
            } catch (IllegalStateException e){
                Bukkit.getLogger().severe(ChatColor.RED +"Error in cancelling the game logic task !");
            }
        }
        onlinePlayers = Config.getDefaultWorld().getPlayers();
        Collections.shuffle(onlinePlayers);
        for (Player player : onlinePlayers){
            String name = player.getName();
            if(!existPlayer(name)){
                continue;
            }
            int id = getID(name);
            int obXpos = id * Integer.parseInt(plugin.getConfig().getString("space", "100"));

            int x = plugin.getConfig().getInt("x",0);
            int y = plugin.getConfig().getInt("y",0);
            int z = plugin.getConfig().getInt("z",0);
            Block block = Config.getDefaultWorld().getBlockAt(x + obXpos, y, z);
            if(block.isEmpty()){
                PlayerData currentPlayer = data.getPlayers().get(id);
            }
        }
    }
}