package ro.yoshoo.oneblock.command;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ro.yoshoo.oneblock.Config;
import ro.yoshoo.oneblock.GameData;
import ro.yoshoo.oneblock.Oneblock;
import ro.yoshoo.oneblock.data.PlayerData;

public class OBCommand implements CommandExecutor {
    GameData data;
    Oneblock plugin;
    World world;
    World leaveworld;

    int x;
    int y;
    int z;
    int id;
    int obXpos;

    public OBCommand(Oneblock instance, GameData gameInstance) {
        this.plugin = instance;
        this.data = gameInstance;
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

    private void newPlayer(FileConfiguration config, Player player){
        id = data.getPlayers().size() + 1;
        obXpos = id * config.getInt("space",100);
        if(config.getBoolean("islands", true)){
            for(int i=-2;i<=2;i++)
                for(int q=-2;q<=2;q++)
                    if(Math.abs(i)+Math.abs(q)< 3){
                        Block block = world.getBlockAt(x+obXpos+i,y,z+q);
                        block.setType(Material.GRASS_BLOCK);
                    }
        }
        PlayerData currentPlayer;
        if(config.getBoolean("ProgressBar", true)){
            String barName = data.getLevels().get(0).name;
            BossBar tempBar = Bukkit.createBossBar(barName,data.getLevels().get(0).color, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
            currentPlayer = new PlayerData(player.getName(), tempBar);
        } else {
            currentPlayer = new PlayerData(player.getName());
        }
        data.getPlayers().add(currentPlayer);
    }

    private boolean join(Player sender){
        FileConfiguration config = plugin.getConfig();
        x = plugin.getConfig().getInt("x",0);
        y = plugin.getConfig().getInt("y",0);
        z = plugin.getConfig().getInt("z",0);
        world = ro.yoshoo.oneblock.Config.getDefaultWorld();
        if(!existPlayer(sender.getName())){
            newPlayer(config, sender);
        }
        if(!GameData.isOn()){
            data.start();
        }
        id = getID(sender.getName());
        obXpos = id * config.getInt("space",100);
        Location location = new Location(world,x+obXpos+0.5,y+1.2,z+0.5);
        sender.teleport(location);
        return true;
    }

    private boolean set(Player sender){
        if(!sender.hasPermission("oneblock.set")){
            sender.sendMessage(ChatColor.RED + "You have no permission !");
            return false;
        }
        Location location = sender.getLocation();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        world = location.getWorld();
        FileConfiguration config = plugin.getConfig();
        config.set("world",world.getName());
        Config.setDefaultWorld(world);
        config.set("x",(double)x);
        config.set("y",(double)y);
        config.set("z",(double)z);
        Bukkit.getLogger().info(ChatColor.GREEN + "Saving config file !");
        plugin.saveConfig();
        world.getBlockAt(x,y,z).setType(Material.GRASS_BLOCK);
        return true;
    }

    private boolean leave(Player sender){
        if(plugin.getConfig().getBoolean("ProgressBar", false)) {
            data.getPlayers().get(getID(sender.getName())).getBossbar().removePlayer(sender);
        }
        FileConfiguration config = plugin.getConfig();
        leaveworld = Config.getLeaveworld();
        if(config.getDouble("yleave")==0||leaveworld==null)
            return false;
        sender.teleport(new Location(leaveworld,config.getDouble("xleave"),config.getDouble("yleave"),
                config.getDouble("zleave")));
        return true;
    }

    private boolean setleave(Player sender){
        if(!sender.hasPermission("oneblock.set")){
            sender.sendMessage(ChatColor.RED + "You have no permission !");
            return false;
        }
        Location location=sender.getLocation();
        leaveworld=location.getWorld();
        FileConfiguration config = plugin.getConfig();
        config.set("leaveworld",leaveworld.getName());
        Config.setLeaveworld(leaveworld);
        config.set("xleave",location.getX());
        config.set("yleave",location.getY());
        config.set("zleave",location.getZ());
        Bukkit.getLogger().info(ChatColor.GREEN + "Saving config file !");
        plugin.saveConfig();
        return true;
    }

    private boolean changeFrequency(String[] args,CommandSender sender){
        if(args.length <= 1) {
            return false;
        }
        int check = Integer.parseInt(args[1]);
        if(check < 4 || check > 16){
            sender.sendMessage(ChatColor.RED +"Value must be between 4 and 16 !");
            return true;
        }
        FileConfiguration config = plugin.getConfig();
        config.set("frequency",(long) check);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            switch(args[0].toLowerCase()){
                case("j"):
                case("join"):
                    return join((Player) sender);
                case("leave"):
                    return leave((Player) sender);
                case("set"):
                    return set((Player) sender);
                case("setleave"):
                    return setleave((Player) sender);
                default:
                    sender.sendMessage(String.format("%s%s%n%s%n%s%n%s%s",
                            ChatColor.DARK_RED,
                            "  ▄▄    ▄▄",
                            "█    █  █▄▀",
                            "▀▄▄▀ █▄▀",
                            "Created by Yoshoo\nPlugin version: v",
                            "1.0"));
                    return true;
            }
        }
        switch(args[0].toLowerCase()){
            case("frequency"):
                return changeFrequency(args, sender);
            default:
                sender.sendMessage(String.format("%s%s%n%s%n%s%n%s%s",
                        ChatColor.DARK_RED,
                        "  ▄▄    ▄▄",
                        "█    █  █▄▀",
                        "▀▄▄▀ █▄▀",
                        "Created by Yoshoo\nPlugin version: v",
                        "1.0"));
                return true;
        }
}}