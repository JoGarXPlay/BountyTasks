package me.jogarx.bountyTasks.bounty;

import org.bukkit.Material;

public class BountyRequirement {
    private final Material material;
    private final int amount;

    public BountyRequirement(
            Material material,
            int amount
    ) {
        this.material = material;
        this.amount = amount;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
