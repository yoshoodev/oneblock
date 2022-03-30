package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ro.yoshoo.oneblock.data.LevelData;
import ro.yoshoo.oneblock.data.PlayerData;
import ro.yoshoo.oneblock.data.SimpleJson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Config {
    public static final String CONFIG_YML = "config.yml";
    public static final String DEFAULT_WORLD = "world";

    Oneblock plugin;

    public Config(Oneblock instance){
        plugin = instance;
    }

    //WORLD
    private static World world;

    public static World getDefaultWorld() {
        return world;
    }

    public static void setDefaultWorld(World defaultWorld) {
        Config.world = defaultWorld;
    }

    private static World leaveworld;

    public static World getLeaveworld() {
        return leaveworld;
    }

    public static void setLeaveworld(World leaveworld) {
        Config.leaveworld = leaveworld;
    }


    private static FileConfiguration levels;

    public static FileConfiguration getLevels() {
        return levels;
    }

    public static void setLevels(FileConfiguration levels) {
        Config.levels = levels;
    }

    int getMajorVersion() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String majorVer = split[0];
        return Integer.parseInt(majorVer);
    }

    int getMinorVersion() {
        String[] split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        String minorVer = split[1];
        String minorVer2 = split.length > 2 ? split[2]:"0";
        if(!Objects.equals(minorVer2, "0")){
            return Integer.parseInt(minorVer2);
        }
        return Integer.parseInt(minorVer);
    }

    boolean isNextGen(){
        if(getMajorVersion() != 1)
            return true;
        return getMinorVersion() > 18;
    }

    private void loadConfig(){
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        String worldName = config.getString(DEFAULT_WORLD, DEFAULT_WORLD);
        setDefaultWorld(plugin.getServer().getWorld(worldName));
        String leaveWorldName = config.getString("leaveworld",DEFAULT_WORLD);
        setLeaveworld(plugin.getServer().getWorld(leaveWorldName));
    }

    public void loadPlayers(Game game){
        File file = new File(plugin.getDataFolder(),"playerData.json");
        if(file.exists()){
            List<PlayerData> load;
            load = SimpleJson.read(file);
            game.getPlayers().addAll(load);
        }
    }

    public void loadLevels(Game game){
        FileConfiguration levelConfig = getLevels();
        for (int i = 0; levelConfig.isList(String.format("%d", i)); i++) {
            List <String> temp = levelConfig.getStringList(String.format("%d", i));
            if (temp.isEmpty() || temp.get(0) == null) {
                String level = Integer.toString(i);
                String text = ChatColor.YELLOW + "Level " + level + "is empty !";
                Bukkit.getLogger().warning(text);
                continue;
            }
            LevelData tmpLevel = new LevelData(temp.get(0));
            try{
                tmpLevel.setColor(BarColor.valueOf(temp.get(1)));
            } catch (Exception e) {
                tmpLevel.setColor(BarColor.valueOf(plugin.getConfig().getString("ProgressBarColor", "GREEN")));
            }
            for(String mat : temp){
                Material tempMat = Material.matchMaterial(mat);
                if(tempMat != null){
                    tmpLevel.getBlocks().add(tempMat);
                }
            }
            game.getLevels().add(tmpLevel);
        }
    }

    private void loadLevelsFile(){
        File levelFile = new File(plugin.getDataFolder(),"levels.yml");
        if(!levelFile.exists()){
            plugin.saveResource("levels.yml", false);
        }
        setLevels(YamlConfiguration.loadConfiguration(levelFile));
    }

    public static void savePlayers(Oneblock plugin, Game data){
        File file = new File(plugin.getDataFolder(),"playerData.json");
        try{
            boolean exists = file.createNewFile();
            if(exists){
                Bukkit.getLogger().info(ChatColor.GREEN + "Player data file created successfully !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleJson.write(data.getPlayers(),file);
    }

    void start(){
        loadConfig();
        loadLevelsFile();
    }

}