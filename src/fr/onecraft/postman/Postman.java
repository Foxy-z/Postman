package fr.onecraft.postman;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Postman extends JavaPlugin {
    private final Map<String, String> directories = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info(this.getName() + " has been enabled.");
        ConfigurationSection config = getConfig().getConfigurationSection("folders");
        config.getKeys(false).forEach(dir -> directories.put(dir, config.getString(dir)));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkFiles, 20, 20 * 5);
    }

    @Override
    public void onDisable() {
        getLogger().info(this.getName() + " has been disabled.");
    }

    private void checkFiles() {
        for (Entry<String, String> entry : directories.entrySet()) {
            File fromDir = new File(entry.getKey());
            File toDir = new File(entry.getValue());
            moveDirectoryContent(fromDir, toDir);
        }
    }

    private void moveDirectoryContent(File fromDir, File toDir) {
        File[] files = fromDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            File dest = new File(toDir.getPath(), file.getName());
            if (toDir.exists() && !dest.exists() && file.isFile() && file.renameTo(dest)) {
                logToFile("File " + file.getName() + " moved from " + file.getPath() + " to " + dest.getPath());
            }
        }
    }

    private void logToFile(String message) {
        try {
            Date now = Calendar.getInstance().getTime();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(now);
            String time = new SimpleDateFormat("HH:mm:ss").format(now);

            File file = new File(this.getDataFolder() + "/logs/", today + ".log");
            if (!file.exists()) file.getParentFile().mkdirs();

            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            writer.println("[" + time + "] " + message);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}