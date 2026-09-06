package me.jogarx.bountyTasks.bounty;

import me.jogarx.bountyTasks.BountyTasks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BountyBook {
    private final BountyTasks plugin;
    private final NamespacedKey bountyIdKey;

    public BountyBook(BountyTasks plugin) {
        this.plugin = plugin;
        this.bountyIdKey = new NamespacedKey(plugin, "bounty_id");
    }

    public ItemStack create(Bounty bounty) {

        ItemStack item = new ItemStack(Material.BOOK);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§e" + bounty.getName());

        meta.getPersistentDataContainer().set(
                bountyIdKey,
                PersistentDataType.STRING,
                bounty.getId()
        );

        List<String> lore = new ArrayList<>();

        lore.add("§7Requisitos:");

        for (BountyRequirement requirement : bounty.getRequirements()) {

            lore.add(
                    "§f- " +
                            requirement.getAmount() +
                            "x " +
                            formatMaterial(requirement.getMaterial())
            );
        }

        lore.add("");

        lore.add("§7Recompensas:");

        for (BountyReward reward : bounty.getRewards()) {

            lore.add(
                    "§a+ " +
                            reward.getAmount() +
                            "x " +
                            formatMaterial(reward.getMaterial())
            );
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    public void give(Player player, Bounty bounty) {

        ItemStack book = create(bounty);

        player.getInventory().addItem(book);
    }

    public String getBountyId(ItemStack item) {

        if (item == null || item.getType() != Material.BOOK) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(
                bountyIdKey,
                PersistentDataType.STRING
        );
    }

    public boolean remove(Player player, Bounty bounty) {

        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {

            ItemStack item = player.getInventory().getItem(slot);

            if (item == null || item.getType() != Material.BOOK) {
                continue;
            }

            String bountyId = getBountyId(item);

            if (!bounty.getId().equals(bountyId)) {
                continue;
            }

            int amount = item.getAmount();

            if (amount > 1) {
                item.setAmount(amount - 1);
            } else {
                player.getInventory().setItem(slot, null);
            }

            return true;
        }

        return false;
    }

    private String formatMaterial(Material material) {

        String name = material.name()
                .toLowerCase()
                .replace("_", " ");

        String[] words = name.split(" ");

        StringBuilder result = new StringBuilder();

        for (String word : words) {

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(
                    Character.toUpperCase(word.charAt(0))
            );

            result.append(
                    word.substring(1)
            );
        }

        return result.toString();
    }
}
