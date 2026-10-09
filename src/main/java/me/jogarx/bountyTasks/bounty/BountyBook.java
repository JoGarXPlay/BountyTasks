
package me.jogarx.bountyTasks.bounty;

import me.jogarx.bountyTasks.BountyTasks;
import me.jogarx.bountyTasks.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BountyBook {

    private final BountyTasks plugin;
    private final NamespacedKey bountyIdKey;

    public BountyBook(BountyTasks plugin) {
        this.plugin = plugin;
        this.bountyIdKey = new NamespacedKey(plugin, "bounty_id");
    }

    public ItemStack create(Bounty bounty) {

        LanguageManager language = plugin.getLanguageManager();

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

        meta.setLore(createLore(bounty, language));

        item.setItemMeta(meta);

        return item;
    }

    private List<String> createLore(
            Bounty bounty,
            LanguageManager language
    ) {

        List<String> lore = new ArrayList<>();

        lore.add(language.get("task.requirements"));

        for (BountyRequirement requirement : bounty.getRequirements()) {

            lore.add(
                    language.get(
                            "task.requirement_line",
                            Map.of(
                                    "amount",
                                    String.valueOf(requirement.getAmount()),
                                    "material",
                                    getMaterialName(
                                            requirement.getMaterial(),
                                            language
                                    )
                            )
                    )
            );
        }

        lore.add("");

        lore.add(language.get("task.rewards"));

        for (BountyReward reward : bounty.getRewards()) {

            lore.add(
                    language.get(
                            "task.reward_line",
                            Map.of(
                                    "amount",
                                    String.valueOf(reward.getAmount()),
                                    "material",
                                    getMaterialName(
                                            reward.getMaterial(),
                                            language
                                    )
                            )
                    )
            );
        }

        return lore;
    }

    private String getMaterialName(
            Material material,
            LanguageManager language
    ) {

        return language.get(
                "materials." + material.name().toLowerCase()
        );
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
}