package ro.yoshoo.oneblock;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

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
    }

    void start(){
        loadConfig();
    }

}