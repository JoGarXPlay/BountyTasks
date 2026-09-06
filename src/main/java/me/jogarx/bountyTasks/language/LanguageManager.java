package me.jogarx.bountyTasks.language;

import me.jogarx.bountyTasks.BountyTasks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

public class LanguageManager {

    private final BountyTasks plugin;

    private FileConfiguration languageConfig;

    public LanguageManager(BountyTasks plugin) {
        this.plugin = plugin;
    }

    public void loadLanguage() {

        // Crear config.yml si no existe
        plugin.saveDefaultConfig();

        String language = plugin.getConfig().getString(
                "language",
                "es"
        );

        File languagesFolder = new File(
                plugin.getDataFolder(),
                "languages"
        );

        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        plugin.saveResource("languages/es.yml", false);
        plugin.saveResource("languages/en.yml", false);

        File languageFile = new File(
                languagesFolder,
                language + ".yml"
        );

        // Si el idioma configurado no existe, usamos inglés
        if (!languageFile.exists()) {

            plugin.getLogger().warning(
                    "No se encontró el idioma '" +
                            language +
                            "'. Se utilizará en.yml."
            );

            languageFile = new File(
                    languagesFolder,
                    "en.yml"
            );
        }

        if (!languageFile.exists()) {

            plugin.getLogger().severe(
                    "No se encontró el archivo de idioma en.yml."
            );

            return;
        }

        languageConfig =
                YamlConfiguration.loadConfiguration(languageFile);

        plugin.getLogger().info(
                "Idioma cargado: " +
                        languageFile.getName()
        );
    }

    public void reload() {
        loadLanguage();
    }

    public String get(String path) {

        if (languageConfig == null) {
            return path;
        }

        return languageConfig.getString(
                path,
                path
        );
    }

    public String get(
            String path,
            Map<String, String> placeholders
    ) {

        String text = get(path);

        for (Map.Entry<String, String> entry :
                placeholders.entrySet()) {

            text = text.replace(
                    "{" + entry.getKey() + "}",
                    entry.getValue()
            );
        }

        return text;
    }

}
