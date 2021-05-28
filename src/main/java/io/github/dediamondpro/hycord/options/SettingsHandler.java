package io.github.dediamondpro.hycord.options;

import io.github.dediamondpro.hycord.core.Location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsHandler {

    public static final ConcurrentHashMap<String, Location> locations = new ConcurrentHashMap<>();

    public static void init() {
        locations.put("mic", new Location(1892, 1052, 20, 20, 1920, 1080));
        locations.put("voice users", new Location(6,6,75,50,1920,1080));

        File configFile = new File("./config/HyCordConfig.txt");
        try {
            if (configFile.createNewFile()) {
                System.out.println("File created: " + configFile.getName());
                FileWriter writer = new FileWriter(String.valueOf(configFile.toPath()));
                for (String str : locations.keySet()) {
                    writer.write(str + ":" + locations.get(str).toString() + System.lineSeparator());
                }
                writer.close();
            } else {
                System.out.println("Loading location config");
                Scanner myReader = new Scanner(configFile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    System.out.println(data);
                    String[] split = data.split(":");
                    if (split.length == 2) {
                        locations.put(split[0], new Location(split[1]));
                    } else {
                        System.out.println("Error loading a nick");
                    }
                }
                myReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter("./config/HyCordConfig.txt");
            for (String str : locations.keySet()) {
                writer.write(str + ":" + locations.get(str).toString() + System.lineSeparator());
            }
            writer.close();
            System.out.println("config saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
