package me.jogarx.bountyTasks.bounty;

import org.bukkit.Material;

import java.util.List;

public class Bounty {

    private final String id;
    private final String name;
    private final Material displayMaterial;
    private final List<BountyRequirement> requirements;
    private final List<BountyReward> rewards;

    public Bounty(
            String id,
            String name,
            Material displayMaterial,
            List<BountyRequirement> requirements,
            List<BountyReward> rewards
    ) {
        this.id = id;
        this.name = name;
        this.displayMaterial = displayMaterial;
        this.requirements = requirements;
        this.rewards = rewards;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getDisplayMaterial() {
        return displayMaterial;
    }

    public List<BountyRequirement> getRequirements() {
        return requirements;
    }

    public List<BountyReward> getRewards() {
        return rewards;
    }
}
