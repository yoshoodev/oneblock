package ro.yoshoo.oneblock.data;

import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerData {
    private String username;
    private List<String> allies = new ArrayList<>();
    private int level = 0;
    private int breaks = 0;
    private BossBar bossbar = null;
    private int x;
    private int z;

    public PlayerData(String name){
        this.username = name;
    }

    public PlayerData(String name, BossBar bar){
        this(name);
        this.bossbar = bar;
    }

    public PlayerData(String name, BossBar bar, List<String> invited){
        this(name,bar);
        this.allies.addAll(invited);
    }

    public void levelUp(){
        ++level;
        breaks = 0;
    }

    public void addBreak(){
        breaks++;
    }

    public int getBreaks() {
        return breaks;
    }

    public void setBreaks(int breaks) {
        this.breaks = breaks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getAllies() {
        return allies;
    }

    public void setAllies(List<String> allies) {
        this.allies = allies;
    }

    public BossBar getBossbar() {
        return bossbar;
    }

    public void setBossbar(BossBar bossbar) {
        this.bossbar = bossbar;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public static final Comparator<PlayerData> COMPARE_BY_LVL = (lhs, rhs) -> {
        if (rhs.username == null)
            return -1;
        return rhs.level - lhs.level;
    };

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}