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
    private Map<String, String> directories = new HashMap();

    @Override
    public void onEnable() {
        getLogger().info(this.getName() + " has been enabled.");
        getConfig().getKeys(false).forEach(folders -> directories.put(folders, getConfig().getString(folders)));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkFiles, 0, 20 * 10);
    }

    @Override
    public void onDisable() {
        getLogger().info(this.getName() + " has been disabled.");
    }

    private void checkFiles() {
        for (String folders : directories.keySet()) {
            File from = new File(folders);
            moveDirectoryContent(from, new File(directories.get(from.getPath())));
        }
    }

    private void moveDirectoryContent(File fromDir, File toDir) {
        File[] files = fromDir.listFiles();
        if (files == null) return;
        for (File file : files) {
            File newFile = new File(toDir.getPath(), file.getName());
            if (!toDir.exists() || newFile.exists()) {
                if (file.renameTo(newFile)) {
                    logToFile("File " + file.getName() + " moved from " + file.getPath() + " toDir " + newFile.getPath());
                } else {
                    logToFile("Failed toDir move " + file.getName() + " from " + file.getPath() + " toDir " + newFile.getPath());
                }
            } else {
                logToFile("File " + file.getName() + " couldn't be moved toDir " + newFile.getPath() + " (no directory or file already exists)");
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
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file, true);
            PrintWriter printer = new PrintWriter(writer);
            String timeStr = new SimpleDateFormat("HH:mm:ss").format(systemDate);
            printer.println("[" + timeStr + "] " + message);
            printer.flush();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}