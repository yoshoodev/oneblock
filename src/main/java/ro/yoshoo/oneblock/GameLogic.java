package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ro.yoshoo.oneblock.data.PlayerData;

import java.util.Collections;
import java.util.List;

public class GameLogic extends BukkitRunnable {
    Oneblock plugin;
    Game data;
    List<Player> onlinePlayers;

    public GameLogic(Oneblock instance, Game dataInstance) {
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

    int x;
    int y;
    int z;

    @Override
    public void run() {
//        Check if plugin should be running
        if(!Game.isOn()){
            try {
                this.cancel();
            } catch (IllegalStateException e){
                Bukkit.getLogger().severe(ChatColor.RED +"Error in cancelling the game logic task !");
            }
        }
//        Get online players and add them to a collection.
        onlinePlayers = Config.getDefaultWorld().getPlayers();
        Collections.shuffle(onlinePlayers);
//        Initialize start coordinates from config.
        x = plugin.getConfig().getInt("x",0);
        y = plugin.getConfig().getInt("y",0);
        z = plugin.getConfig().getInt("z",0);
//        Logic for each player online.
        for (Player player : onlinePlayers){
            String name = player.getName();
//            If player didn't join OB then skip.
            if(!existPlayer(name)){
                continue;
            }
//            Get player ID and his personal Block x distance. (e.g 100blocks from O)
            int id = getID(name);
            int obXpos = id * plugin.getConfig().getInt("space", 100);
            Block block = Config.getDefaultWorld().getBlockAt(x + obXpos, y, z);
            if(block.isEmpty()){
                PlayerData currentPlayer = data.getPlayers().get(id);
                fallProtect(player, currentPlayer);
            }
        }
    }

    private void fallProtect(Player player, PlayerData currentPlayer){
        Location location = player.getLocation();
        if (location.getBlockX() == currentPlayer.getX() && location.getY() - 1 < y && location.getBlockZ() == currentPlayer.getZ()) {
            location.setY((double) y + 1);
            player.teleport(location);
        }
    }

}