package me.jogarx.bountyTasks;

import me.jogarx.bountyTasks.bounty.BountyManager;
import me.jogarx.bountyTasks.command.BountyTasksCommand;
import me.jogarx.bountyTasks.listener.BountyTasksListener;
import me.jogarx.bountyTasks.language.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BountyTasks extends JavaPlugin {

    private BountyManager bountyManager;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("[BountyTasks] se ha iniciado correctamente.");

        getCommand("bountytasks").setExecutor(
                new BountyTasksCommand(this)
        );

        getServer().getPluginManager().registerEvents(
                new BountyTasksListener(this),
                this
        );

        saveResource("tasks.yml", false);

        languageManager = new LanguageManager(this);
        languageManager.loadLanguage();

        bountyManager = new BountyManager(this);
        bountyManager.loadBounties();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("[BountyTasks] se ha detenido.");
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public void reloadPlugin(){
        reloadConfig();

        languageManager.reload();

        bountyManager.loadBounties();

        getLogger().info(
                "BountyTasks ha sido recargado correctamente."
        );
    }

}
