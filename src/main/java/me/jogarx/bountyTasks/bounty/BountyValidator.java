package me.jogarx.bountyTasks.bounty;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BountyValidator {
    public boolean hasRequirements(Player player, Bounty bounty) {

        for (BountyRequirement requirement : bounty.getRequirements()) {

            int amount = countItems(
                    player,
                    requirement.getMaterial()
            );

            if (amount < requirement.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public int countItems(
            Player player,
            org.bukkit.Material material
    ) {

        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {

            if (item == null) {
                continue;
            }

            if (item.getType() != material) {
                continue;
            }

            total += item.getAmount();
        }

        return total;
    }

    public void removeRequirements(Player player, Bounty bounty) {

        for (BountyRequirement requirement : bounty.getRequirements()) {

            int remaining = requirement.getAmount();

            for (ItemStack item : player.getInventory().getContents()) {

                if (remaining <= 0) {
                    break;
                }

                if (item == null) {
                    continue;
                }

                if (item.getType() != requirement.getMaterial()) {
                    continue;
                }

                int amount = item.getAmount();

                if (amount <= remaining) {

                    remaining -= amount;

                    item.setAmount(0);

                } else {

                    item.setAmount(amount - remaining);

                    remaining = 0;
                }
            }
        }
    }
}
