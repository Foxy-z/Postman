package fr.onecraft.postman;

import org.bukkit.Bukkit;
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

public class Postman extends JavaPlugin {
    private final Map<String, String> directories = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info(this.getName() + " has been enabled.");
        getConfig().getKeys(false).forEach(folders -> directories.put(folders, getConfig().getString(folders)));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkFiles, 20, 20 * 5);
    }

    @Override
    public void onDisable() {
        getLogger().info(this.getName() + " has been disabled.");
    }

    private void checkFiles() {
        for (String folder : directories.keySet()) {
            File fromDir = new File(folder);
            File toDir = new File(directories.get(folder));
            moveDirectoryContent(fromDir, toDir);
        }
    }

    private void moveDirectoryContent(File fromDir, File toDir) {
        File[] files = fromDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            File dest = new File(toDir.getPath(), file.getName());
            if (toDir.exists() && !dest.exists() && file.renameTo(dest)) {
                logToFile("File " + file.getName() + " moved from " + file.getPath() + " to " + dest.getPath());
            }
        }
    }

    private void logToFile(String message) {
        try {
            Date systemDate = Calendar.getInstance().getTime();
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(systemDate);
            File file = new File(this.getDataFolder() + "/logs/", dateStr + ".log");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(file, true));
            String timeStr = new SimpleDateFormat("HH:mm:ss").format(systemDate);
            writer.println("[" + timeStr + "] " + message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}