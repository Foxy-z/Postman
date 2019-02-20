package fr.onecraft.postman.core.helpers;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Configs {
    private final static String FOLDERS_FILENAME = "folders.yml";

    public static void checkFiles(JavaPlugin plugin) {
        YamlConfiguration configuration = getConfig(plugin);
        if (configuration == null) return;

        for (String folders : configuration.getKeys(false)) {
            File from = new File(folders);
            moveFiles(plugin, from);
        }
    }

    private static void moveFiles(JavaPlugin plugin, File from) {
        File to = getDirectory(plugin, from);
        if (to == null) return;
        for (File files : from.listFiles()) {
            try {
                if (!to.exists()) to.mkdirs();
                FileUtils.copyFile(files, new File(to.getPath(), files.getName()));
                files.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getDirectory(JavaPlugin plugin, File from) {
        YamlConfiguration configuration = getConfig(plugin);
        if (configuration == null) return null;

        return configuration.getKeys(false).stream()
                .filter(keys -> from.getPath().equals(keys))
                .findFirst().map(keys -> new File(configuration.getString(keys)))
                .orElse(null);
    }

    private static YamlConfiguration getConfig(JavaPlugin plugin) {
        try {
            YamlConfiguration configuration = new YamlConfiguration();
            File file = new File(plugin.getDataFolder(), FOLDERS_FILENAME);
            if (!file.exists()) return null;
            FileInputStream fileinputstream = new FileInputStream(file);
            configuration.load(new InputStreamReader(fileinputstream, Charset.forName("UTF-8")));
            return configuration;
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
