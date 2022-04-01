package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import ro.yoshoo.oneblock.data.LevelData;
import ro.yoshoo.oneblock.data.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.bukkit.Material.CHEST;
import static org.bukkit.Material.GRASS_BLOCK;

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

    Random random = new Random(System.currentTimeMillis());

    double multiplier;
    int chance;

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
//        Initialize level multiplier from config
        multiplier = plugin.getConfig().getInt("level_multiplier", 5);
        chance = plugin.getConfig().getInt("chance", 4);
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
                blockLogic(block, currentPlayer);
                fallProtect(player, currentPlayer);
            }
        }
    }

    private void blockLogic(Block block, PlayerData currentPlayer){
        BossBar bar = currentPlayer.getBossbar();
        LevelData curLevel = data.getLevels().get(currentPlayer.getLevel());
//        ADD A BLOCK BREAK
        currentPlayer.addBreak();
//        CHECK FOR LEVEL UP
        if(currentPlayer.getBreaks() >= 16 + (currentPlayer.getLevel() * multiplier)){
            currentPlayer.levelUp();
            LevelData newLevel = data.getLevels().get(currentPlayer.getLevel());
            if(bar != null) {
                bar.setTitle(newLevel.getName());
                bar.setColor(newLevel.getColor());
            }
        }
//        SET PROGRESS BAR
        if(bar != null){
            double breaks = currentPlayer.getBreaks();
            double levelBreaks = 16 + (currentPlayer.getLevel() * multiplier);
            bar.setProgress(breaks / levelBreaks);
        }
//        BLOCK GENERATION LOGIC
        int blocksInLevel = curLevel.getSize();
        int randomBlock = 0;
        if (blocksInLevel != 0) {
            randomBlock = random.nextInt(blocksInLevel);
        }
        if(curLevel.getBlocks().get(randomBlock) == null){
            block.setType(GRASS_BLOCK);
        } else if (curLevel.getBlocks().get(randomBlock) == CHEST){
//            try {
//                block.setType(CHEST);
//                Chest chest = (Chest) block.getState();
//                Inventory inv = chest.getInventory();
//                ArrayList<Material> ch_now;
//                if (random < 26)
//                    ch_now = s_ch;
//                else if (random < 68)
//                    ch_now = m_ch;
//                else
//                    ch_now = h_ch;
//                int max = rnd.nextInt(3)+2;
//                for(int i = 0;i<max;i++) {
//                    Material m = ch_now.get(rnd.nextInt(ch_now.size()));
//                    if (m.getMaxStackSize() == 1)
//                        inv.addItem(new ItemStack(m, 1));
//                    else
//                        inv.addItem(new ItemStack(m, rnd.nextInt(4)+2));
//                }
//            } catch (Exception e) {
//                Bukkit.getConsoleSender().sendMessage("[OB] Error when generating items for the chest! Pls redo chests.yml!");
//            }
        } else {
//            CHANCE OF BLOCK FROM ANOTHER LEVEL LOGIC
            if(random.nextInt(chance - 1) == 0 && currentPlayer.getLevel() > 0){
                int randomLevel = random.nextInt(currentPlayer.getLevel());
                LevelData randomLevelData = data.getLevels().get(randomLevel);
                int randomLevelBlock = random.nextInt(randomLevelData.getSize());
                block.setType(randomLevelData.getBlocks().get(randomLevelBlock));
            } else {
//                DEFAULT BLOCK FORM THE CURRENT LEVEL
                block.setType(curLevel.getBlocks().get(randomBlock));
            }
        }
        if (random.nextInt(9) == 0){
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