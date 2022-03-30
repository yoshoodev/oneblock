package ro.yoshoo.oneblock.data;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    private String name;
    private BarColor color;
    private List<Material> blocks = new ArrayList<>();

    public LevelData(String name) {
        this.name = name;
    }

    public LevelData(String name, BarColor color){
        this(name);
        this.color = color;
    }

    public LevelData(String name, BarColor color, List<Material> blocks){
        this(name,color);
        this.blocks = blocks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return blocks.size();
    }

    public BarColor getColor() {
        return color;
    }

    public void setColor(BarColor color) {
        this.color = color;
    }

    public List<Material> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Material> blocks) {
        this.blocks = blocks;
    }
}