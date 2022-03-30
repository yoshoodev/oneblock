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
import org.jetbrains.annotations.NotNull;
import ro.yoshoo.oneblock.Config;
import ro.yoshoo.oneblock.Game;
import ro.yoshoo.oneblock.Oneblock;
import ro.yoshoo.oneblock.data.PlayerData;

public class OBCommand implements CommandExecutor {
    public static final String XLEAVE = "xleave";
    public static final String YLEAVE = "yleave";
    public static final String ZLEAVE = "zleave";
    public static final String PROGRESS_BAR = "ProgressBar";
    Game data;
    Oneblock plugin;
    World world;
    World leaveworld;

    int x;
    int y;
    int z;
    int id;
    int obXpos;
    int obZpos;

    public OBCommand(Oneblock instance, Game gameInstance) {
        this.plugin = instance;
        this.data = gameInstance;
    }

    private boolean notPlayer(String name) {
        for (PlayerData player : data.getPlayers()) {
            if (player.getUsername() == null) {
                continue;
            }
            if (player.getUsername().equals(name)) {
                return false;
            }
            if (player.getAllies().contains(name)) {
                return false;
            }
        }
        return true;
    }

    private int getID(String name) {
        for (int i = 0; i < data.getPlayers().size(); i++) {
            PlayerData player = data.getPlayers().get(i);
            if (player.getUsername().isEmpty()) {
                continue;
            }
            if (player.getUsername().equals(name)) {
                return i;
            }
            if (player.getAllies().contains(name)) {
                return i;
            }
        }
        return 0;
    }

    private void newPlayer(FileConfiguration config, Player player) {
        PlayerData currentPlayer;
        if (config.getBoolean(PROGRESS_BAR, true)) {
            String barName = data.getLevels().get(0).getName();
            BossBar tempBar = Bukkit.createBossBar(barName, data.getLevels().get(0).getColor(), BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY);
            currentPlayer = new PlayerData(player.getName(), tempBar);
        } else {
            currentPlayer = new PlayerData(player.getName());
        }
        data.getPlayers().add(currentPlayer);
        generationLogic(player, config);
        Config.savePlayers(plugin, data);
    }

    private void generationLogic(Player sender, FileConfiguration config) {
        switch (plugin.getConfig().getInt("Generation", 2)) {
            case(1):
                oldGen(sender, config);
                break;
            case(2):
                newGen(sender, config);
                break;
            default:
                oldGen(sender, config);
        }
    }

    private void oldGen(Player sender, FileConfiguration config) {
        id = getID(sender.getName());
        String text = "Found id : " + id;
        Bukkit.getLogger().info(text);
        int space = config.getInt("space", 100);
        obXpos = x + (id * space);
        obZpos = z;
        data.getPlayers().get(getID(sender.getName())).setX(obXpos);
        data.getPlayers().get(getID(sender.getName())).setZ(obZpos);
        generateIslands(config, sender);
    }

    private void newGen(Player sender, FileConfiguration config) {
        id = getID(sender.getName());
        int space = config.getInt("space", 100);
        int dx = 0;
        int dy = 1;
        // length of current segment
        int segmentLength = 1;
        // current position (x, y) and how much of current segment we passed
        int xtemp = 0;
        int ztemp = 0;
        int segmentPassed = 0;
        for (int n = 0; n < id; ++n) {
            // make a step, add 'direction' vector (dx, dy) to current position (x, y)
            xtemp += dx;
            ztemp += dy;
            ++segmentPassed;
            if (segmentPassed == segmentLength) {
                // done with current segment
                segmentPassed = 0;
                // 'rotate' directions
                int buffer = dy;
                dy = -dx;
                dx = buffer;

                // increase segment length if necessary
                if (dx == 0) {
                    ++segmentLength;
                }
            }
        }
        if(xtemp == 0){
            obXpos = x;
        } else {
            obXpos = (xtemp * space) + x;
        }
        if(ztemp == 0){
            obZpos = z;
        } else  {
        obZpos = (ztemp * space)  + z;}
        data.getPlayers().get(getID(sender.getName())).setX(obXpos);
        data.getPlayers().get(getID(sender.getName())).setZ(obZpos);
        generateIslands(config, sender);
    }

    private void generateIslands(FileConfiguration config, Player sender) {
        int posX = data.getPlayers().get(getID(sender.getName())).getX();
        int posZ = data.getPlayers().get(getID(sender.getName())).getZ();
            if(config.getBoolean("islands", true)){
            for (int i = -2; i <= 2; i++)
                for (int q = -2; q <= 2; q++)
                    if (Math.abs(i) + Math.abs(q) < 3) {
                        Block block = world.getBlockAt(posX + i, y, posZ + q);
                        block.setType(Material.GRASS_BLOCK);
                    }
            } else {
                Block block = world.getBlockAt(posX, y, posZ);
                block.setType(Material.GRASS_BLOCK);
            }
    }

    private boolean join(Player sender) {
        FileConfiguration config = plugin.getConfig();
        x = plugin.getConfig().getInt("x", 0);
        y = plugin.getConfig().getInt("y", 0);
        z = plugin.getConfig().getInt("z", 0);
        world = ro.yoshoo.oneblock.Config.getDefaultWorld();
        if (notPlayer(sender.getName())) {
            newPlayer(config, sender);
        }
        if (!Game.isOn()) {
            data.start();
        }
        PlayerData player = data.getPlayers().get(getID(sender.getName()));
        if (config.getBoolean(PROGRESS_BAR, true)) {
            player.getBossbar().addPlayer(sender);
        }
        Location location = new Location(world, player.getX() + 0.5, y + 1.2, player.getZ() + 0.5);
        sender.teleport(location);
        return true;
    }

    private boolean set(Player sender) {
        if (!sender.hasPermission("oneblock.set")) {
            sender.sendMessage(ChatColor.RED + "You have no permission !");
            return false;
        }
        Location location = sender.getLocation();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        world = location.getWorld();
        FileConfiguration config = plugin.getConfig();
        config.set("world", world.getName());
        Config.setDefaultWorld(world);
        config.set("x", (double) x);
        config.set("y", (double) y);
        config.set("z", (double) z);
        Bukkit.getLogger().info(ChatColor.GREEN + "Saving config file !");
        plugin.saveConfig();
        world.getBlockAt(x, y, z).setType(Material.GRASS_BLOCK);
        return true;
    }

    private boolean leave(Player sender) {
        if (plugin.getConfig().getBoolean(PROGRESS_BAR, false)) {
            data.getPlayers().get(getID(sender.getName())).getBossbar().removePlayer(sender);
        }
        FileConfiguration config = plugin.getConfig();
        leaveworld = Config.getLeaveworld();
        if (config.getDouble(YLEAVE) == 0 || leaveworld == null)
            return false;
        if (config.getBoolean(PROGRESS_BAR, true)) {
            PlayerData player = data.getPlayers().get(getID(sender.getName()));
            player.getBossbar().removePlayer(sender);
        }
        sender.teleport(new Location(leaveworld, config.getDouble(XLEAVE), config.getDouble(YLEAVE),
                config.getDouble(ZLEAVE)));
        return true;
    }

    private boolean setleave(Player sender) {
        if (!sender.hasPermission("oneblock.set")) {
            sender.sendMessage(ChatColor.RED + "You have no permission !");
            return false;
        }
        Location location = sender.getLocation();
        leaveworld = location.getWorld();
        FileConfiguration config = plugin.getConfig();
        config.set("leaveworld", leaveworld.getName());
        Config.setLeaveworld(leaveworld);
        config.set(XLEAVE, location.getX());
        config.set(YLEAVE, location.getY());
        config.set(ZLEAVE, location.getZ());
        Bukkit.getLogger().info(ChatColor.GREEN + "Saving config file !");
        plugin.saveConfig();
        return true;
    }

    private boolean changeFrequency(String[] args, CommandSender sender) {
        if (args.length <= 1) {
            return false;
        }
        int check = Integer.parseInt(args[1]);
        if (check < 4 || check > 16) {
            sender.sendMessage(ChatColor.RED + "Value must be between 4 and 16 !");
            return true;
        }
        FileConfiguration config = plugin.getConfig();
        config.set("frequency", (long) check);
        return true;
    }

    private boolean info(CommandSender sender){
        sender.sendMessage(String.format("%s%s%n%s%n%s%n%s%s",
                ChatColor.RED,
                "  ▄▄    ▄▄",
                "█    █  █▄▀",
                "▀▄▄▀ █▄▀",
                "Created by Yoshoo\nPlugin version: v",
                "1.0"));
        return true;
    }

    private boolean reset(Player sender){
        PlayerData temp = data.getPlayers().get(getID(sender.getName()));
        if (temp == null){
            return false;
        }
        BossBar tempBar = temp.getBossbar();
        if(tempBar != null)
            tempBar.removePlayer(sender);
        temp.setUsername("ReMoVeD_PLAYER");
        return true;
    }

    private boolean reload(){
        if (Game.isOn()){
            Bukkit.getScheduler().cancelTasks(plugin);
            Game.setOn(false);
            data.start();
            return true;
        }
        Bukkit.getLogger().info("Game logic is off already !");
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0)
            return info(sender);
        if (sender instanceof Player) {
            switch (args[0].toLowerCase()) {
                case ("j"):
                case ("join"):
                    return join((Player) sender);
                case ("leave"):
                    return leave((Player) sender);
                case ("set"):
                    return set((Player) sender);
                case ("setleave"):
                    return setleave((Player) sender);
                case ("reset"):
                    return reset((Player) sender);
                default:
                    return info(sender);
            }
        }
        switch (args[0].toLowerCase()) {
            case ("frequency"):
                return changeFrequency(args, sender);
            case ("reload"):
                    return reload();
            default:
                return info(sender);
        }
    }
}