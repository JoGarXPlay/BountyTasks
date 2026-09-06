package me.jogarx.bountyTasks.bounty;

import me.jogarx.bountyTasks.BountyTasks;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BountyManager {
    private final BountyTasks plugin;
    private final List<Bounty> bounties = new ArrayList<>();

    public BountyManager(BountyTasks plugin) {
        this.plugin = plugin;
    }

    public void loadBounties() {

        bounties.clear();

        File file = new File(plugin.getDataFolder(), "tasks.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection tasksSection = config.getConfigurationSection("tasks");

        if (tasksSection == null) {
            plugin.getLogger().warning("No se encontró la sección 'tasks' en tasks.yml");
            return;
        }

        for (String id : tasksSection.getKeys(false)) {

            ConfigurationSection taskSection =
                    tasksSection.getConfigurationSection(id);

            if (taskSection == null) {
                continue;
            }

            String name = taskSection.getString("name", id);

            String materialName = taskSection.getString(
                    "material",
                    "BOOK"
            );

            Material displayMaterial = Material.matchMaterial(materialName);

            if (displayMaterial == null) {

                plugin.getLogger().warning(
                        "Material inválido para la tarea: " + id
                );

                displayMaterial = Material.BOOK;
            }

            List<BountyRequirement> requirements =
                    loadRequirements(taskSection);

            List<BountyReward> rewards =
                    loadRewards(taskSection);

            Bounty bounty = new Bounty(
                    id,
                    name,
                    displayMaterial,
                    requirements,
                    rewards
            );

            bounties.add(bounty);

            plugin.getLogger().info(
                    "Tarea cargada: " + id
            );
        }

        plugin.getLogger().info(
                "Se cargaron " + bounties.size() + " tareas."
        );
    }

    private List<BountyRequirement> loadRequirements(
            ConfigurationSection taskSection
    ) {

        List<BountyRequirement> requirements = new ArrayList<>();

        List<?> requirementList =
                taskSection.getList("requirements");

        if (requirementList == null) {
            return requirements;
        }

        for (Object object : requirementList) {

            if (!(object instanceof java.util.Map<?, ?> map)) {
                continue;
            }

            Object materialValue = map.get("material");
            Object amountValue = map.get("amount");

            if (materialValue == null || amountValue == null) {
                continue;
            }

            Material material = Material.matchMaterial(
                    materialValue.toString()
            );

            if (material == null) {
                plugin.getLogger().warning(
                        "Material inválido en requisito: "
                                + materialValue
                );
                continue;
            }

            int amount = Integer.parseInt(
                    amountValue.toString()
            );

            requirements.add(
                    new BountyRequirement(material, amount)
            );
        }

        return requirements;
    }

    private List<BountyReward> loadRewards(
            ConfigurationSection taskSection
    ) {

        List<BountyReward> rewards = new ArrayList<>();

        List<?> rewardList =
                taskSection.getList("rewards");

        if (rewardList == null) {
            return rewards;
        }

        for (Object object : rewardList) {

            if (!(object instanceof java.util.Map<?, ?> map)) {
                continue;
            }

            Object materialValue = map.get("material");
            Object amountValue = map.get("amount");

            if (materialValue == null || amountValue == null) {
                continue;
            }

            Material material = Material.matchMaterial(
                    materialValue.toString()
            );

            if (material == null) {
                plugin.getLogger().warning(
                        "Material inválido en recompensa: "
                                + materialValue
                );
                continue;
            }

            int amount = Integer.parseInt(
                    amountValue.toString()
            );

            rewards.add(
                    new BountyReward(material, amount)
            );
        }

        return rewards;
    }

    public List<Bounty> getBounties() {
        return bounties;
    }

    public Bounty getBounty(String id) {

        for (Bounty bounty : bounties) {

            if (bounty.getId().equalsIgnoreCase(id)) {
                return bounty;
            }
        }

        return null;
    }
}
