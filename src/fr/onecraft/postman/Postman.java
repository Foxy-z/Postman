package fr.onecraft.postman;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
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
    private Map<String, String> DIRECTORIES = new HashMap();

    @Override
    public void onEnable() {
        getLogger().info(this.getName() + " has been enabled.");
        loadDirectories();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::checkFiles, 0, 20 * 10);
    }

    @Override
    public void onDisable() {
        getLogger().info(this.getName() + " has been enabled.");
    }

    private void loadDirectories() {
        FileConfiguration configuration = getConfig();
        for (String folders : configuration.getKeys(false)) {
            DIRECTORIES.put(folders, configuration.getString(folders));
        }
    }

    private void checkFiles() {
        FileConfiguration configuration = getConfig();
        for (String folders : configuration.getKeys(false)) {
            File from = new File(folders);
            moveFiles(from);
        }
    }

    private void moveFiles(File from) {
        File to = new File(DIRECTORIES.get(from.getPath()));
        for (File files : from.listFiles()) {
            try {
                File newFile = new File(to.getPath(), files.getName());
                if (!to.exists() || newFile.exists()) {
                    FileUtils.copyFile(files, newFile);
                    logToFile("File " + files.getName() + " moved from " + files.getPath() + " to " + newFile.getPath());
                } else {
                    logToFile("File " + files.getName() + " couldn't be moved to " + newFile.getPath() + " (no directory or file already exists)");
                }

                files.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void logToFile(String message) {
        try {
            Date systemDate = Calendar.getInstance().getTime();
            String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(systemDate);
            File file = new File(this.getDataFolder() + "/logs/", dateStr + ".txt");
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