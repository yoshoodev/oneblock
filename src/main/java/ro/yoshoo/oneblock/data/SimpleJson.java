package ro.yoshoo.oneblock.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SimpleJson
{
    public static void write(List<PlayerData> pls, File f) {
        JSONObject main = new JSONObject();

        for (int i = 0;pls.size() > i;i++) {
            JSONObject user = new JSONObject();
            PlayerData pl = pls.get(i);
            if (pl.getUsername() == null) {
                continue;
            }
            user.put("user", pl.getUsername());
            user.put("level", pl.getLevel());
            user.put("breaks", pl.getBreaks());
            user.put("x", pl.getX());
            user.put("z", pl.getZ());

            JSONArray arr = new JSONArray();
            for(String us: pl.getAllies())
                arr.put(us);
            user.put("allies", arr);
            main.put(String.valueOf(i), user);
        }
        try (FileWriter file = new FileWriter(f)) {
            file.write(main.toString());
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<PlayerData> read(File f)  {
        JSONObject main = null;
        FileReader fr = null;
        try {
            fr = new FileReader(f);
            JSONTokener tokener = new JSONTokener(Objects.requireNonNull(fr));
            main = new JSONObject(tokener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List <PlayerData> infs = new ArrayList <>();
        if(main == null)
            return infs;
        for(int i = 0; i<main.length() ;i++) {
            PlayerData pl;
            JSONObject user = (JSONObject) main.get(""+i);
            String nick = (String) user.get("user");
            pl = new PlayerData(nick);
            pl.setLevel(((Number) user.get("level")).intValue());
            pl.setBreaks(((Number) user.get("breaks")).intValue());
            pl.setX(((Number) user.get("x")).intValue());
            pl.setZ(((Number) user.get("z")).intValue());
            JSONArray arr = (JSONArray) user.get("allies");
            for(int q = 0;q<arr.length();q++)
                pl.getAllies().add((String) arr.get(q));
            infs.add(pl);
        }
        return infs;
}}