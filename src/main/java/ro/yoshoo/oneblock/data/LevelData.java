package ro.yoshoo.oneblock.data;

import org.bukkit.boss.BarColor;

public class LevelData {
    public String name;
    public int size = 0;
    public BarColor color;

    public LevelData(String name) {
        this.name = name;
    }
}