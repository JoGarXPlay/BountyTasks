package me.jogarx.bountyTasks.bounty;

import org.bukkit.Material;

public class BountyReward {
    private final Material material;
    private final int amount;

    public BountyReward(
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
