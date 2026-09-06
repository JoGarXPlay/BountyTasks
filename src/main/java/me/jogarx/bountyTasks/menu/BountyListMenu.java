package me.jogarx.bountyTasks.menu;

import me.jogarx.bountyTasks.BountyTasks;
import me.jogarx.bountyTasks.bounty.Bounty;
import me.jogarx.bountyTasks.bounty.BountyRequirement;
import me.jogarx.bountyTasks.bounty.BountyReward;
import me.jogarx.bountyTasks.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BountyListMenu {

    private final BountyTasks plugin;

    public BountyListMenu(BountyTasks plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {

        LanguageManager language =
                plugin.getLanguageManager();

        Inventory inventory = plugin.getServer().createInventory(
                null,
                27,
                language.get("menu.open_tasks")
        );

        List<Bounty> bounties = plugin
                .getBountyManager()
                .getBounties();

        List<Bounty> randomBounties =
                new ArrayList<>(bounties);

        java.util.Collections.shuffle(randomBounties);

        int slot = 10;

        for (Bounty bounty : randomBounties) {

            ItemStack item =
                    createBountyItem(
                            bounty,
                            language
                    );

            inventory.setItem(slot, item);

            slot++;

            if (slot == 17) {
                break;
            }
        }

        // Botón para volver al menú principal
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();

        if (backMeta != null) {
            backMeta.setDisplayName(language.get("menu.back")   );
            backMeta.setLore(List.of(
                    language.get("menu.back_description")
            ));

            back.setItemMeta(backMeta);
        }

        inventory.setItem(22, back);

        player.openInventory(inventory);
    }

    private ItemStack createBountyItem(Bounty bounty,  LanguageManager language) {

        ItemStack item = new ItemStack(
                bounty.getDisplayMaterial()
        );

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                bounty.getName()
        );

        NamespacedKey bountyIdKey =
                new NamespacedKey(plugin, "bounty_id");

        meta.getPersistentDataContainer().set(
                bountyIdKey,
                PersistentDataType.STRING,
                bounty.getId()
        );


        List<String> lore = new ArrayList<>();

        lore.add(language.get("task.requirements"));

        for (BountyRequirement requirement :
                bounty.getRequirements()) {

            lore.add(
                    language.get(
                            "task.requirement_line",
                            java.util.Map.of(
                                    "amount",
                                    String.valueOf(
                                            requirement.getAmount()
                                    ),
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

        for (   BountyReward reward :
                bounty.getRewards()) {

            lore.add(
                    language.get(
                            "task.reward_line",
                            java.util.Map.of(
                                    "amount",
                                    String.valueOf(
                                            reward.getAmount()
                                    ),
                                    "material",
                                    getMaterialName(
                                            reward.getMaterial(),
                                            language
                                    )
                            )
                    )
            );
        }

        lore.add("");
        lore.add(language.get("task.accept"));

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
    }

    private String getMaterialName(
            Material material,
            LanguageManager language
    ) {

        return language.get(
                "materials." +
                        material.name().toLowerCase()
        );
    }
}
